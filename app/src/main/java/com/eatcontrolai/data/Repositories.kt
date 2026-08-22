package com.eatcontrolai.data

import com.eatcontrolai.core.model.MealRecord
import com.eatcontrolai.core.model.PrivacySettings
import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.domain.glp1.PersonalRule
import com.eatcontrolai.domain.glp1.SymptomReport
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
sealed interface ProfileState {
    data object Loading : ProfileState
    data object NeedsOnboarding : ProfileState
    data class Ready(val profile: UserProfile) : ProfileState
}

internal object ProfilePolicy {
    private const val LEGACY_DEMO_PROFILE_ID = "demo-joao"

    fun normalizeStored(profile: UserProfile): UserProfile? =
        profile.takeUnless { it.id == LEGACY_DEMO_PROFILE_ID }

    fun stateFor(profile: UserProfile): ProfileState =
        if (profile.displayName.isBlank()) ProfileState.NeedsOnboarding else ProfileState.Ready(profile)
}

class ProfileRepository(
    private val store: LocalStore,
    private val scope: CoroutineScope
) {
    private val emptyProfile = UserProfile(id = "")
    private val _profile = MutableStateFlow(emptyProfile)
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        scope.launch {
            val decoded = store.read(LocalStore.Key.PROFILE)?.let { json ->
                Serialization.decodeProfile(json, emptyProfile)
            } ?: emptyProfile
            // Builds anteriores sem onboarding persistiam uma persona sintética. Ela não pode
            // sobreviver a uma atualização como se fosse dado real do usuário.
            val loaded = ProfilePolicy.normalizeStored(decoded) ?: emptyProfile
            if (loaded !== decoded) store.clear(LocalStore.Key.PROFILE)
            _profile.value = loaded
            _state.value = ProfilePolicy.stateFor(loaded)
        }
    }

    fun update(transform: (UserProfile) -> UserProfile) {
        val updated = transform(_profile.value)
        _profile.value = updated
        _state.value = ProfilePolicy.stateFor(updated)
        scope.launch { store.write(LocalStore.Key.PROFILE, Serialization.encode(updated)) }
    }

    /** Remove o perfil local e volta ao onboarding, sem injetar dados demonstrativos. */
    fun reset() {
        _profile.value = emptyProfile
        _state.value = ProfileState.NeedsOnboarding
        scope.launch { store.clear(LocalStore.Key.PROFILE) }
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
 * Regras que a própria pessoa criou, inclusive a partir de desconforto relatado.
 *
 * Requisito trazido pela validação clínica (`VAL-GLP1-R1`): a análise cruza o alimento identificado
 * com plano, metas e **regras pessoais**. Elas evitam ou observam um item; nunca criam meta clínica.
 */
class PersonalRuleRepository(
    private val store: LocalStore,
    private val scope: CoroutineScope
) {
    private val _rules = MutableStateFlow<List<PersonalRule>>(emptyList())
    val rules: StateFlow<List<PersonalRule>> = _rules.asStateFlow()

    init {
        scope.launch {
            store.read(LocalStore.Key.PERSONAL_RULES)?.let { json ->
                _rules.value = Serialization.decodePersonalRules(json)
            }
            _rules.drop(1)
                .onEach { store.write(LocalStore.Key.PERSONAL_RULES, Serialization.encodePersonalRules(it)) }
                .launchIn(scope)
        }
    }

    /** Regra com o mesmo id substitui a anterior, para a edição não duplicar o cadastro. */
    fun save(rule: PersonalRule) {
        val others = _rules.value.filterNot { it.id == rule.id }
        _rules.value = others + rule
    }

    fun remove(id: String) {
        _rules.value = _rules.value.filterNot { it.id == id }
    }

    fun clear() {
        _rules.value = emptyList()
    }
}

/**
 * Sintomas relatados pela pessoa.
 *
 * É dado de saúde criado por ela: fica neste aparelho, está fora do backup em nuvem junto do resto do
 * DataStore, e pode ser apagado. Nada aqui é inferido — sintoma é relato.
 */
class SymptomRepository(
    private val store: LocalStore,
    private val scope: CoroutineScope,
    private val maxReports: Int = 200
) {
    private val _reports = MutableStateFlow<List<SymptomReport>>(emptyList())
    val reports: StateFlow<List<SymptomReport>> = _reports.asStateFlow()

    init {
        scope.launch {
            store.read(LocalStore.Key.SYMPTOM_REPORTS)?.let { json ->
                _reports.value = Serialization.decodeSymptomReports(json)
            }
            _reports.drop(1)
                .onEach { store.write(LocalStore.Key.SYMPTOM_REPORTS, Serialization.encodeSymptomReports(it)) }
                .launchIn(scope)
        }
    }

    fun add(report: SymptomReport) {
        _reports.value = (listOf(report) + _reports.value).take(maxReports)
    }

    fun clear() {
        _reports.value = emptyList()
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
