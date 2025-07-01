package com.example.myapplication
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject


class MySocketManager {

    private lateinit var socket: Socket

    private var onTemperaturaRecibida: ((Float) -> Unit)? = null
    private var onHumedadRecibida: ((Float) -> Unit)? = null
    private var onGasAnalogicoRecibido: ((Int) -> Unit)? = null

    private var onVelocidadSeleccionada: ((Int, Boolean) -> Unit)? = null

    fun conectar(
        onTemperatura: (Float) -> Unit,
        onHumedad: (Float) -> Unit,
        onGas: (Int) -> Unit,
        onVelocidad: ((Int,Boolean) -> Unit)? = null
    ) {
        val options = IO.Options()
        socket = IO.socket("http://192.168.1.12:3000", options)

        onTemperaturaRecibida = onTemperatura
        onHumedadRecibida = onHumedad
        onGasAnalogicoRecibido = onGas
        onVelocidadSeleccionada = onVelocidad


        socket.on(Socket.EVENT_CONNECT) {
            println("🟢 Conectado al servidor WebSocket")
        }

        socket.on("sensor/temperatura") { args ->
            val data = args[0] as JSONObject
            val temp = data.getDouble("temperatura").toFloat()
            onTemperaturaRecibida?.invoke(temp)
        }

        socket.on("sensor/humedad") { args ->
            val data = args[0] as JSONObject
            val hum = data.getDouble("humedad").toFloat()
            onHumedadRecibida?.invoke(hum)
        }

        socket.on("sensor/gas/analogico") { args ->
            val data = args[0] as JSONObject
            val gas = data.getInt("gasAnalogico")
            onGasAnalogicoRecibido?.invoke(gas)
        }

        socket.on("estado_ventilador"){ args ->
            val data = args[0] as JSONObject
            val velocidad = data.optInt("velocidad", -1)
            val estado = data.optBoolean("estado", false)
            if(velocidad >= 0){
                onVelocidadSeleccionada?.invoke(velocidad,estado)
            }
        }

        socket.connect()
    }

    fun enviarVelocidad(nuevaVelocidad: Int){
        if(!this::socket.isInitialized || !socket.connected()){
            println("Socket no conectado; no se envia velocidad")
            return
        }
    }

    fun desconectar() {
        if(this::socket.isInitialized) socket.disconnect()
    }
}