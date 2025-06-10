package com.example.myapplication

import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppNavegacion()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hola $name!",
        modifier = modifier
    )
}

fun accion(context: Context) {
    Toast.makeText(context, "Botón presionado!", Toast.LENGTH_SHORT).show()
}

@Composable
fun MiBoton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Button(
        onClick = { accion(context) },
        modifier = modifier
    ) {
        Text("Haz click aquí.")
    }
}

@Composable
fun AppNavegacion(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash"){
        composable("splash") { SplashScreen(navController)}
        composable("principal") { PantallaPrincipal(navController) }
        composable("pantalla1") { Pantalla1(navController)}
        composable("Metricas") { Metricas(navController)}
        composable("Configuracion") { PantallaConfiguracion(navController) }
    }
}
@Composable
fun PantallaPrincipal(navController: NavHostController){
    var velocidadSeleccionada by remember { mutableStateOf("Apagar")}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ){
        Termometro(temp = 10.2f)
        CalidadAire(porcentaje = 50)
        seleccionarVelocidad { seleccion -> velocidadSeleccionada = seleccion }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly

        ){
            Button(onClick = {navController.navigate("pantalla1")}){
                Image(
                    painter = painterResource(id = R.drawable.temporizador),
                    contentDescription = "Temporizador",
                    modifier = Modifier.size(60.dp)
                )
            }
            Button(onClick = {navController.navigate("Metricas")}) {
                Image(
                    painter = painterResource(id = R.drawable.ahorro_energia),
                    contentDescription = "Metricas",
                    modifier = Modifier.size(60.dp)
                )
            }
            Button(onClick = {navController.navigate("Configuracion")}) {
                Image(
                    painter = painterResource(id = R.drawable.configuracion),
                    contentDescription = "configuracion",
                    modifier = Modifier.size(60.dp)
                )
            }
        }

    }
}

@Composable
fun VelocidadVentilador(selected: Int, onSeleccion: (Int) -> Unit){
    val labels = listOf("Apagar","Velocidad 1", "Velocidad 2","Velocidad 3")
    Column(horizontalAlignment = Alignment.CenterHorizontally){
        Text("Velocidad del ventilador", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()){
            (1..4).forEach{ index ->
                Column(horizontalAlignment = Alignment.CenterHorizontally){
                    OutlinedButton(
                        onClick = { onSeleccion(index)},
                        shape = CircleShape,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if(selected == index) Color.Black else Color.White
                        ),
                        border = BorderStroke(1.dp, Color.Black),
                        modifier = Modifier.size(50.dp)
                    ) {
                        Text("$index", color = if (selected == index) Color.White else Color.Black)
                    }
                    Text(labels[index-1])
                }

            }
        }
    }
}
@Composable
fun seleccionarVelocidad(onSeleccionado: (String) -> Unit){
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
                        Text("${index + 1}", color = if(seleccionActual == opcion) Color.White else Color.Black)
                    }
                }
            }
        }
    }
}
@Composable
fun Pantalla1(navController: NavHostController){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Esta es la pantalla de temporizador")
            Spacer(modifier = Modifier.height(16.dp))

            Temporizador(navController)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("principal") }) {
                Text("Volver")
            }
        }
    }
}

@Composable
fun Metricas(navController: NavHostController){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Esta es la pestaña de Métricas")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("principal") }) {
                Text("Volver")
            }
        }
    }
}


@Composable
fun PantallaConfiguracion(navController: NavHostController){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Esta es la pestaña de configuración")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("principal") }) {
                Text("Volver")
            }
        }
    }
}

@Composable
fun SplashScreen(navController: NavHostController){
    LaunchedEffect(true){
        delay(2000)
        navController.navigate("principal"){
            popUpTo("splash"){inclusive = true}
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Image(
            painter = painterResource(id = R.drawable.splashscreen),
            contentDescription = "SplashScreen",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun Termometro(temp: Float){
    val color = when{
        temp < 18 -> Color.Blue
        temp < 25 -> Color.Green
        else -> Color.Red
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("Temperatura del Aire", style = MaterialTheme.typography.labelLarge)
        Text("$temp°C", style = MaterialTheme.typography.headlineMedium)

        LinearProgressIndicator(
            progress = temp /40f,
            color = color,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }
}

@Composable
fun CalidadAire(porcentaje: Int){
    var sweepAngle = (porcentaje/ 100f) * 360f

    Box(
        modifier = Modifier.size(150.dp),
        contentAlignment = Alignment.Center
    ){
        Canvas(modifier = Modifier.size(150.dp)){
            drawArc(
                color = Color.LightGray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(15f)
            )
            drawArc(
                color = Color(0xFF4CAF50),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(15f)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally){
            Text("$porcentaje", style = MaterialTheme.typography.headlineLarge)
            Text("de 100")
        }
    }

    Text(
        text = "Calidad del aire: ",
        color = Color(0xFF4CAF50),
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun Temporizador(navController: NavHostController) {
    val context = LocalContext.current
    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    var mostrarPicker by remember { mutableStateOf(false) }
    var seleccionandoHoraInicio by remember { mutableStateOf(true) }
    var horaTempInicio by remember { mutableStateOf("") }
    var horaTempFin by remember { mutableStateOf("") }

    var listaTemporizadores = remember { mutableStateListOf<TemporizadorData>() }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTime()
            delay(1000L)
        }
    }

    LaunchedEffect(mostrarPicker, seleccionandoHoraInicio) {
        if (mostrarPicker) {
            showTimePicker(context) { selected ->
                if (seleccionandoHoraInicio) {
                    horaTempInicio = selected
                    seleccionandoHoraInicio = false
                } else {
                    horaTempFin = selected
                    listaTemporizadores.add(TemporizadorData(horaTempInicio, horaTempFin))
                    mostrarPicker = false
                    seleccionandoHoraInicio = true
                }
            }
        }
    }

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
        listaTemporizadores.forEachIndexed { index, t ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(" temporizador ${index + 1}: ${t.horaInicio} - ${t.horaFin}", modifier = Modifier.weight(1f))
                Button(
                    onClick = { listaTemporizadores.removeAt(index)},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ){
                    Text("X", color = Color.White)
                }

            }

        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("principal") }) {
            Text("Volver")
        }

    }
}

@Composable
fun ContadorTemporizador(minutos: Int, segundos: Int) {
    var tiempoRestante by remember { mutableStateOf(minutos * 60 + segundos) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(tiempoRestante) {
        if (tiempoRestante > 0) {
            kotlinx.coroutines.delay(1000)
            tiempoRestante--
        } else {
            // Mostrar mensaje cuando se acaba el tiempo
            Toast.makeText(context, "¡Tiempo terminado!", Toast.LENGTH_SHORT).show()
            // O también puedes usar Snackbar:
            coroutineScope.launch {
                scaffoldState.snackbarHostState.showSnackbar("¡Tiempo finalizado!")
            }
        }
    }

    Scaffold(scaffoldState = scaffoldState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val minutosRestantes = tiempoRestante / 60
            val segundosRestantes = tiempoRestante % 60
            Text(
                text = String.format("%02d:%02d", minutosRestantes, segundosRestantes),
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}

@Composable
fun TimePickerComposable(context: Context, onTimeSelected: (String) -> Unit){
    val calendar = Calendar.getInstance()
    TimePickerDialog(
        context,
        {_, hour, minute ->
            val formatted = String.format("%02d:%02d",hour,minute)
            onTimeSelected(formatted)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    ).show()
}

fun getCurrentTime(): String{
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    return String.format("%02d:%02d", hour, minute)
}

fun showTimePicker(context: android.content.Context, onTimeSelected: (String) -> Unit){
    val calendar = Calendar.getInstance()
    TimePickerDialog(
        context,
        { _, hour, minute ->
            val formatted = String.format("%02d:%02d", hour, minute)
            onTimeSelected(formatted)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    ).show()
}

fun isTimeInRange(current: String, start: String, end: String): Boolean{
    val formatter = java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
    val currentDate = formatter.parse(current)
    val startDate = formatter.parse(start)
    val endDate = formatter.parse(end)

    return if(startDate.before(endDate)){
        currentDate.after(startDate) && currentDate.before(endDate)
    }else{
        currentDate.after(startDate) || currentDate.before(endDate)
    }
}

//Modelo temporizador

data class TemporizadorData(
    val horaInicio: String,
    val horaFin: String
)


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Greeting("Android", modifier = Modifier.align(Alignment.TopStart))
            MiBoton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
}