package com.eatcontrolai.data

import com.eatcontrolai.core.model.MealRecord
import com.eatcontrolai.core.model.PrivacySettings
import com.eatcontrolai.core.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Repositórios em memória.
 *
 * Dados reais, fluxo real, sem persistência entre execuções — a trilha de DataStore/Room está no
 * Bloco 2 de `docs/01_ESTRUTURA_E_ESTADO_ATUAL.md`. Nada aqui grava imagem em disco, o que mantém o
 * NFR-005 verdadeiro por construção enquanto a tela de privacidade não estiver ligada a storage.
 */
class ProfileRepository(initial: UserProfile) {
    private val _profile = MutableStateFlow(initial)
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    fun update(transform: (UserProfile) -> UserProfile) = _profile.update(transform)
}

class PrivacyRepository {
    private val _settings = MutableStateFlow(PrivacySettings())
    val settings: StateFlow<PrivacySettings> = _settings.asStateFlow()

    fun update(transform: (PrivacySettings) -> PrivacySettings) = _settings.update(transform)
}

/** Histórico das análises feitas nesta sessão. Cada item veio de uma execução real da pipeline. */
class MealHistoryRepository {
    private val _records = MutableStateFlow<List<MealRecord>>(emptyList())
    val records: StateFlow<List<MealRecord>> = _records.asStateFlow()

    fun add(record: MealRecord) = _records.update { listOf(record) + it }

    fun markConfirmed(id: String) = _records.update { list ->
        list.map { if (it.id == id) it.copy(userConfirmed = true) else it }
    }

    fun replace(record: MealRecord) = _records.update { list ->
        list.map { if (it.id == record.id) record else it }
    }
}
