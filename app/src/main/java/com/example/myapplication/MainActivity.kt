package com.example.myapplication

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.media.Ringtone
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
fun AppNavegacion(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash"){
        composable("splash") { SplashScreen(navController)}
        composable("principal") { PantallaPrincipal(navController) }
        composable("PantallaTemporizador") { PantallaTemporizador(navController)}
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
        CalidadAireDinamico()
        FiltracionGas(nivelGas = 105.0f)
        SeleccionarVelocidad { seleccion -> velocidadSeleccionada = seleccion }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly

        ){
            Button(onClick = {navController.navigate("PantallaTemporizador")}){
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
@Composable
fun PantallaTemporizador(navController: NavHostController){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

    val calidadTexto = when{
        porcentaje >= 80 -> "Excelente"
        porcentaje in 50..79 -> "Bueno"
        else -> "Malo"
    }

    val colorIndicator = when{
        porcentaje >= 80 -> Color(0xFF4BBF33)
        porcentaje in 50..79 -> Color(0xFFC8A728)
        else -> Color.Red
    }

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
                color = colorIndicator,
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
        text = "Calidad del aire: $calidadTexto",
        color = colorIndicator,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun CalidadAireDinamico(){
    val calidadAire = remember { mutableStateOf(0)}

    //Actualizar el valor cada 5 segundos
    LaunchedEffect(Unit){
        while(true){
            delay(5000)
            calidadAire.value = (0..100).random()
        }
    }

    CalidadAire(porcentaje = calidadAire.value)
}

@Composable
fun FiltracionGas(nivelGas: Float){
    val context = LocalContext.current
    val hayFiltracion = nivelGas >= 70f
    var mostrarDialogo by remember { mutableStateOf(false)}

    //Sonido de alerta
    LaunchedEffect(hayFiltracion) {
        if(hayFiltracion){
            val notificacionUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, notificacionUri)
            ringtone.play()
            mostrarDialogo = true
        }
    }

    // Mostrar mensaje en pantalla
    if(hayFiltracion){
        Text("Filtración de gas detectada!", color = MaterialTheme.colorScheme.error)
    }else{
        Text("Nivel de gas normal")
    }

    //Mostrar popup en caso de filtración
    if(mostrarDialogo){
        AlertDialog(
            onDismissRequest = {mostrarDialogo = false},
            title = { Text("¡Alerta de gas!")},
            text = { Text("Se ha detectado una filtración de gas. Por favor, revise el entorno")},
            confirmButton = {
                Button(onClick = { mostrarDialogo = false}){
                    Text("Entendido")
                }
            }
        )
    }
}


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
//Modelo temporizador

@kotlinx.serialization.Serializable
data class TemporizadorData(
    val horaInicio: String,
    val horaFin: String
)
