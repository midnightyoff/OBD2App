package com.example.obd2app

import android.bluetooth.BluetoothSocket
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.obd2app.obd2.ObdConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class SimpleViewModel: ViewModel() {
    var socket: BluetoothSocket? = null

    private val _connectionStatus = MutableStateFlow<String>("Не подключено")
    val connectionStatus: StateFlow<String> = _connectionStatus

    private val _commandResponse = MutableStateFlow<String>("Пусто")
    val commandResponse: StateFlow<String> = _commandResponse

    private var obdConnection: ObdConnection? = null
    // Устанавливаем соединение с OBD
    fun setConnection(socket: BluetoothSocket) {
        this.socket = socket
        val inputStream = socket.inputStream
        val outputStream = socket.outputStream
        obdConnection = ObdConnection(inputStream, outputStream)
        _connectionStatus.value = "Подключено"
    }

//    fun setConnection(inputStream: InputStream, outputStream: OutputStream) {
//        obdConnection = ObdConnection(inputStream, outputStream)
//        _connectionStatus.value = "Подключено"
//    }

    // Закрытие соединения
    fun disconnect() {
        obdConnection = null
        _connectionStatus.value = "Отключено"
        try {
            socket?.close()
            socket = null
        } catch (_: Exception) {
//            _connectionStatus.value = "Ошибка при отключении: ${e.message}"
        }
    }

    // Отправка команды
    fun sendCommand(command: String) {
        viewModelScope.launch {
            try {
                val response =
                    obdConnection?.query(command) ?: "Ошибка: устройство не подключено"
                _commandResponse.value += "\n" + response.toString()
            } catch (e: Exception) {
//                _commandResponse.value = "Ошибка: ${e.message}"
            }
        }
    }
}