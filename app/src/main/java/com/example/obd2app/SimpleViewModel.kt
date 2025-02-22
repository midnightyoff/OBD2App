package com.example.obd2app

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
    var socket: Socket? = null

    private val _connectionStatus = MutableStateFlow<String>("Не подключено")
    val connectionStatus: StateFlow<String> = _connectionStatus

    private val _commandResponse = MutableStateFlow<String>("")
    val commandResponse: StateFlow<String> = _commandResponse

    private var obdConnection: ObdConnection? = null

    // Устанавливаем соединение с OBD
    fun setConnection(inputStream: InputStream, outputStream: OutputStream) {
        obdConnection = ObdConnection(inputStream, outputStream)
        _connectionStatus.value = "Подключено"
    }

    // Закрытие соединения
    fun disconnect() {
        obdConnection = null
        _connectionStatus.value = "Отключено"
    }

    // Отправка команды
    fun sendCommand(command: String) {
        viewModelScope.launch {
            try {
                val response =
                    obdConnection?.query(command) ?: "Ошибка: устройство не подключено"
                _commandResponse.value = response.toString()
            } catch (e: Exception) {
                _commandResponse.value = "Ошибка: ${e.message}"
            }
        }
    }
}