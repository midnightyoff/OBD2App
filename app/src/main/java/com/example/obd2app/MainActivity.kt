package com.example.obd2app

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.obd2app.obd2.ObdConnection
import com.example.obd2app.ui.theme.OBD2AppTheme
import java.io.IOException
import java.util.UUID

// Ctrl + Alt + L - format code
// TODO логировать запросы ответы
class MainActivity : ComponentActivity() {
    private var bluetoothAdapter: BluetoothAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissionMissing1 =
            checkSelfPermission(Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED
        if (permissionMissing1) {
//            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            startActivity(intent)
        }
        val permissionMissing =
            checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
        if (permissionMissing) {
//            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
//            startActivity(intent)
        }

        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        val pairedDevices = bluetoothAdapter?.bondedDevices
        val mac: String = ""
//        mac.isNotEmpty()
        val device = bluetoothAdapter?.getRemoteDevice(mac)
        device.let {
            val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            val socket = device?.createInsecureRfcommSocketToServiceRecord(uuid)
            try {
                socket?.connect()
                if (socket != null) {
                    val obdConnection = ObdConnection(socket.inputStream, socket.outputStream)
//                    obdConnection.query()
                }
            } catch (_: IOException) {

            }

        }
//        var devices = pairedDevices?.map { "${it.name}\n${it.address}" }
//
//        // TODO подключиться по bluetooth
//        if (devices == null)
//            return

        val devices = listOf("1", "2", "3")

        enableEdgeToEdge()
//        setContent {
//            OBD2AppTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
        setContent {
            val navController = rememberNavController()
            OBD2AppTheme {
                NavHost(navController = navController, startDestination = "screenStart", builder = {
                    composable("screenStart") {
                        StartScreen(devices, navController)
                    }
                    composable("screenTerminal") {
                        TerminalScreen()
                    }
                })
//                ItemsList(devices)
//                Layout()
            }
        }
    }
}

@Composable
fun Layout() {
    Button(
        onClick = {
            /* Переход на SecondActivity */
        }
    ) {
        Text("Перейти на второе Activity")
    }
}

@Composable
fun ItemsList(items: List<String>) {
    val selectedItem = remember { mutableStateOf(setOf<String>()) }
    Column {
        items.forEach { item ->
            ListItem(
                headlineContent = { Text(item) }
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(
    showSystemUi = true
)
@Composable
fun GreetingPreview() {
    val items = listOf("Kotlin", "Java", "JavaScript", "Python", "C#", "C++", "Rust")
    OBD2AppTheme {
        ItemsList(items)
    }
}