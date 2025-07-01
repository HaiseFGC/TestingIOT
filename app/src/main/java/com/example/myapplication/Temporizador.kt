package com.example.myapplication

import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun Temporizador(navController: NavHostController) {
    val context = LocalContext.current
    val dataStore = remember{ TemporizadorDataStore(context)}
    val scope = rememberCoroutineScope()

    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    var mostrarPicker by remember { mutableStateOf(false) }
    var seleccionandoHoraInicio by remember { mutableStateOf(true) }
    var horaTempInicio by remember { mutableStateOf("") }
    var horaTempFin by remember { mutableStateOf("") }


    //Para obtener la lista de temporizadores desde el dataStore
    val temporizadores by dataStore.listaTemporizadores.collectAsState(initial = emptyList())
    val listaTemporizadores = remember { mutableStateListOf<TemporizadorData>()}

    //Sincronizar listaTemporizadores con el dataStore al cargar
    LaunchedEffect(temporizadores) {
        Log.d("Temporizador", "Sincronizando temporizadores: $temporizadores")
        if(listaTemporizadores.isEmpty()){
            listaTemporizadores.clear()
            listaTemporizadores.addAll(temporizadores)
        }
    }
    // Recalcular cuando cambia el contenido (convierte la lista en una lista inmutable nueva)
    LaunchedEffect(listaTemporizadores.toList()) {
        dataStore.guardarTemporizadores(listaTemporizadores)
    }

    //Actualizar hora actual cada segundo
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTime()
            delay(1000L)
        }
    }

    //Mostrar TimePicker cuando sea necesario
    LaunchedEffect(mostrarPicker, seleccionandoHoraInicio) {
        if (mostrarPicker) {
            showTimePicker(context) { selected ->
                if (selected == null) {
                    // Usuario canceló
                    mostrarPicker = false
                    seleccionandoHoraInicio = true
                } else {
                    if (seleccionandoHoraInicio) {
                        horaTempInicio = selected
                        seleccionandoHoraInicio = false
                        mostrarPicker = true // Para continuar seleccionando la hora de fin
                    } else {
                        horaTempFin = selected
                        listaTemporizadores.add(TemporizadorData(horaTempInicio, horaTempFin))
                        mostrarPicker = false
                        seleccionandoHoraInicio = true
                    }
                }
            }
        }
    }

    // Verificar si hay un temporizador activo
    var temporizadorActivo = listaTemporizadores.any {
        isTimeInRange(currentTime, it.horaInicio, it.horaFin)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hora actual: $currentTime", style = MaterialTheme.typography.headlineMedium)

        Button(onClick = { mostrarPicker = true }) {
            Text("Agregar Temporizador")
        }

        if (mostrarPicker) {
            Text("Seleccionando ${if (seleccionandoHoraInicio) "hora Inicio" else "hora Fin"}")
        }

        if (temporizadorActivo) {
            Text("hay un temporizador activo")
        } else {
            Text("No hay temporizadores activos")
        }

        HorizontalDivider()

        Text("Temporizadores:", style = MaterialTheme.typography.titleMedium)
        LazyColumn{
            itemsIndexed(listaTemporizadores) { index, t ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text("Temporizador ${index + 1}: ${t.horaInicio} - ${t.horaFin}", modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            scope.launch{
                                dataStore.guardarTemporizadores(listaTemporizadores.apply { removeAt(index)})
                            }},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ){
                        Text("Eliminar", color = Color.White)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("principal") }) {
            Text("Volver")
        }

    }
}

fun getCurrentTime(): String{
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    return String.format("%02d:%02d", hour, minute)
}

fun showTimePicker(
    context: android.content.Context,
    onTimeSelected: (String?) -> Unit // ahora puede ser null
) {
    val calendar = Calendar.getInstance()
    val dialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            val formatted = String.format("%02d:%02d", hour, minute)
            onTimeSelected(formatted)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    // Manejo de cancelación
    dialog.setOnCancelListener {
        onTimeSelected(null)
    }

    dialog.show()
}

fun isTimeInRange(current: String, start: String, end: String): Boolean {
    val formatter = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
    return try {
        val currentDate = formatter.parse(current)
        val startDate = formatter.parse(start)
        val endDate = formatter.parse(end)

        if (startDate.before(endDate)) {
            !currentDate.before(startDate) && currentDate.before(endDate)
        } else {
            !currentDate.before(startDate) || currentDate.before(endDate)
        }
    } catch (e: Exception) {
        false
    }
}