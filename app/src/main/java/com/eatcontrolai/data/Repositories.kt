package com.eatcontrolai.data

import com.eatcontrolai.core.model.MealRecord
import com.eatcontrolai.core.model.PrivacySettings
import com.eatcontrolai.core.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

/**
 * Repositórios com estado observável e persistência local.
 *
 * O padrão é o mesmo nos três: hidrata do [LocalStore] na criação e grava a cada mudança. A escrita
 * é assíncrona de propósito — a UI reage ao `StateFlow` na hora, sem esperar disco.
 */
class ProfileRepository(
    private val store: LocalStore,
    private val scope: CoroutineScope,
    private val default: UserProfile
) {
    private val _profile = MutableStateFlow(default)
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    init {
        scope.launch {
            store.read(LocalStore.Key.PROFILE)?.let { json ->
                _profile.value = Serialization.decodeProfile(json, default)
            }
            _profile.drop(1)
                .onEach { store.write(LocalStore.Key.PROFILE, Serialization.encode(it)) }
                .launchIn(scope)
        }
    }

    fun update(transform: (UserProfile) -> UserProfile) {
        _profile.value = transform(_profile.value)
    }

    /** Volta ao perfil de demonstração. Útil antes de gravar o vídeo do pitch. */
    fun reset() {
        _profile.value = default
    }
}

class PrivacyRepository(
    private val store: LocalStore,
    private val scope: CoroutineScope
) {
    private val _settings = MutableStateFlow(PrivacySettings())
    val settings: StateFlow<PrivacySettings> = _settings.asStateFlow()

    init {
        scope.launch {
            store.read(LocalStore.Key.PRIVACY)?.let { json ->
                _settings.value = Serialization.decodePrivacy(json)
            }
            _settings.drop(1)
                .onEach { store.write(LocalStore.Key.PRIVACY, Serialization.encode(it)) }
                .launchIn(scope)
        }
    }

    fun update(transform: (PrivacySettings) -> PrivacySettings) {
        _settings.value = transform(_settings.value)
    }
}

/**
 * Histórico das análises. Cada item veio de uma execução real da pipeline.
 *
 * Guarda decisão, evidências usadas e latência — nunca a imagem (NFR-008).
 */
class MealHistoryRepository(
    private val store: LocalStore,
    private val scope: CoroutineScope,
    private val maxRecords: Int = 200
) {
    private val _records = MutableStateFlow<List<MealRecord>>(emptyList())
    val records: StateFlow<List<MealRecord>> = _records.asStateFlow()

    init {
        scope.launch {
            store.read(LocalStore.Key.HISTORY)?.let { json ->
                _records.value = Serialization.decodeHistory(json)
            }
            _records.drop(1)
                .onEach { store.write(LocalStore.Key.HISTORY, Serialization.encode(it)) }
                .launchIn(scope)
        }
    }

    fun add(record: MealRecord) {
        _records.value = (listOf(record) + _records.value).take(maxRecords)
    }

    fun replace(record: MealRecord) {
        _records.value = _records.value.map { if (it.id == record.id) record else it }
    }

    fun clear() {
        _records.value = emptyList()
    }
}
