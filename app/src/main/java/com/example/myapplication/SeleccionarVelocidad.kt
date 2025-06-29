package com.example.myapplication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SeleccionarVelocidad(onSeleccionado: (String) -> Unit){
    val opciones = listOf("Apagar","Velocidad 1", "Velocidad 2", "Velocidad 3")
    var seleccionActual by remember { mutableStateOf("Apagar")}

    Column(horizontalAlignment = Alignment.CenterHorizontally ){
        Text("Velocidad de ventilador", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement  = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ){
            opciones.forEachIndexed { index, opcion ->
                Column(horizontalAlignment = Alignment.CenterHorizontally){
                    OutlinedButton(
                        onClick = {
                            seleccionActual = opcion
                            onSeleccionado(opcion)
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (seleccionActual == opcion) Color.Black else Color.White
                        ),
                        border = BorderStroke(1.dp, Color.Black),
                        modifier = Modifier.size(50.dp)
                    ){
                        Text("${index}", color = if(seleccionActual == opcion) Color.White else Color.Black)
                    }
                }
            }
        }
    }
}