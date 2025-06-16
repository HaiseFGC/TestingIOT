package com.example.myapplication

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer


//Crea instancia unica de DataStore
private val Context.dataStore by preferencesDataStore(name = "temporizadores")

class TemporizadorDataStore(private val context: Context) {

    private val TEMPORIZADORES_KEY = stringPreferencesKey("temporizadores-json")

    //Flow que devuelve la lista de temporizadores
    val listaTemporizadores: Flow<List<TemporizadorData>> = context.dataStore.data
        .map { preferences ->
            val jsonString = preferences[TEMPORIZADORES_KEY] ?: return@map emptyList()
            try{
                Json.decodeFromString(jsonString)
            }catch (e: Exception){
                emptyList()
            }
        }

    //Guardar la lista como un Json string
    suspend fun guardarTemporizadores(lista: List<TemporizadorData>) {
        context.dataStore.edit { preferences ->
            val jsonString = Json.encodeToString(lista)
            preferences[TEMPORIZADORES_KEY] = jsonString
        }
    }


}