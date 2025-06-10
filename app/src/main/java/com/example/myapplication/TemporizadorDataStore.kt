package com.example.myapplication

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "temporizadores")

class TemporizadorDataStore(private val context: Context) {

    private val TEMPORIZADORES_KEY = stringPreferencesKey("lista_temporizadores")
    private val gson = Gson()

    val listaTemporizadores: Flow<List<TemporizadorData>> = context.dataStore.data.map { preferences ->
        val json = preferences[TEMPORIZADORES_KEY]
        if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<TemporizadorData>>() {}.type
            gson.fromJson<List<TemporizadorData>>(json, type)
        }
    }

    suspend fun guardarTemporizadores(lista: List<TemporizadorData>) {
        val json = gson.toJson(lista)
        context.dataStore.edit { preferences ->
            preferences[TEMPORIZADORES_KEY] = json
        }
    }
}
