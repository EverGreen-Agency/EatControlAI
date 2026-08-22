package com.eatcontrolai.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "eat_control")

/**
 * Persistência local.
 *
 * Guarda **apenas texto estruturado**: perfil, privacidade e o histórico de decisões. Imagem nunca
 * entra aqui — é o que torna o `NFR-008` verdadeiro por construção e não por promessa. O frame
 * capturado vive em memória durante a análise e é descartado.
 *
 * DataStore em vez de Room porque são três documentos pequenos, sem consulta relacional. Room entra
 * quando o histórico precisar de filtro e agregação de verdade.
 */
class LocalStore(context: Context) {

    private val store = context.applicationContext.dataStore

    suspend fun read(key: Key): String? = store.data.first()[key.preference]

    suspend fun write(key: Key, value: String) {
        store.edit { it[key.preference] = value }
    }

    suspend fun clear(key: Key) {
        store.edit { it.remove(key.preference) }
    }

    enum class Key(val preference: Preferences.Key<String>) {
        PROFILE(stringPreferencesKey("profile")),
        PRIVACY(stringPreferencesKey("privacy")),
        HISTORY(stringPreferencesKey("history")),

        /** Regras que a pessoa criou a partir da própria experiência. */
        PERSONAL_RULES(stringPreferencesKey("personal_rules")),

        /** Sintomas que a pessoa relatou. É dado de saúde: local, sem backup, apagável. */
        SYMPTOM_REPORTS(stringPreferencesKey("symptom_reports"))
    }
}
