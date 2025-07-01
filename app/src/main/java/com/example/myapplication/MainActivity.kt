package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.media.RingtoneManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import com.example.myapplication.componentes.GraficoBarras
import com.example.myapplication.ui.theme.SplashScreen
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiNetworkSpecifier


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

    var socketManager = remember { MySocketManager() }

    //Valores recibidos del servidor
    var temperatura by remember { mutableStateOf(0f)}
    var humedad by remember { mutableStateOf(0f)}
    var gas by remember { mutableStateOf(0f)}

    //Conexión a WebSocket (una sola vez)
    LaunchedEffect(Unit) {
        socketManager.conectar(
            onTemperatura = { temperatura = it },
            onHumedad = { humedad = it },
            onGas = { gas = it.toFloat()}

        )
        // (opcional) si quieres desconectar al salir:
        // awaitDispose { socketManager.desconectar() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ){
        Termometro(temp = temperatura)
        CalidadAireDinamico(humedad = humedad)
        FiltracionGas(nivelGas = gas)
        SeleccionarVelocidad { seleccion ->
            velocidadSeleccionada = seleccion

            val velocidadInt = when(seleccion){
                "Apagar" -> 0
                "Velocidad 1" -> 1
                "Velocidad 2" -> 2
                "Velocidad 3 "-> 3
                else -> 0
            }

            //Enviar por websocket
            socketManager.enviarVelocidad(velocidadInt)
        }
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
            Graficas(navController)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("principal") }) {
                Text("Volver")
            }
        }
    }
}

@Composable
fun Graficas(navController: NavHostController) {
    val datos = listOf(40f, 55f, 30f, 70f, 45f, 60f, 20f) // Tiempo de uso por día (ejemplo)
    val dias = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Tiempo de uso semanal", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        GraficoBarras(datos, dias)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("principal") }) {
            Text("Volver")
        }
    }
}


@Composable
fun PantallaConfiguracion(navController: NavHostController){
    val context = LocalContext.current
    var mostrarDialogoWifi by remember { mutableStateOf(false)}
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (mostrarDialogoWifi) {
                DialogoWifi(
                    onDismiss = { mostrarDialogoWifi = false },
                    onConectar = { ssid, password ->
                        conectarAWifi(context, ssid, password)
                    }
                )
            }

            Button(onClick = { mostrarDialogoWifi = true }) {
                Text("Conectarse a Wi-Fi")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("principal") }) {
                Text("Volver")
            }
        }
    }
}

@Composable
fun DialogoWifi(
    onDismiss: () -> Unit,
    onConectar: (String, String) -> Unit
) {
    var ssid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Conexión Wi-Fi") },
        text = {
            Column {
                OutlinedTextField(
                    value = ssid,
                    onValueChange = { ssid = it },
                    label = { Text("Nombre de red (SSID)") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConectar(ssid, password)
                onDismiss()
            }) {
                Text("Conectar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

fun conectarAWifi(context: Context, ssid: String, password: String) {
    val specifier = WifiNetworkSpecifier.Builder()
        .setSsid(ssid)
        .setWpa2Passphrase(password)
        .build()

    val request = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .setNetworkSpecifier(specifier)
        .build()

    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            connectivityManager.bindProcessToNetwork(network)
        }
    }

    connectivityManager.requestNetwork(request, callback)
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
fun CalidadAireDinamico(humedad: Float){
    val porcentaje = humedad.toInt().coerceIn(0,100)
    var humedadMostrada by remember { mutableStateOf(humedad)}

    //Actualizar el valor cada 5 segundos
    LaunchedEffect(Unit){
        while(true){
            delay(5000)
            humedadMostrada = humedad
        }
    }

    CalidadAire(porcentaje)
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
//Modelo temporizador

@kotlinx.serialization.Serializable
data class TemporizadorData(
    val horaInicio: String,
    val horaFin: String
)
