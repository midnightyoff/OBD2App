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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.obd2app.obd2.ObdConnection
import com.example.obd2app.ui.theme.OBD2AppTheme
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

// Ctrl + Alt + L - format code
// TODO логировать запросы ответы
class MainActivity : ComponentActivity() {
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
//        val blutoothPermission = Manifest.permission.BLUETOOTH_CONNECT
//        val blutoothPermissionMissing = checkSelfPermission(blutoothPermission) != PackageManager.PERMISSION_GRANTED
//        val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//        val permissionMissing1 = checkSelfPermission(Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED
//        val permissionMissing = checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
//        if (permissionMissing) {
//            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
//            startActivity(intent)
//        }

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
            val viewModel: SimpleViewModel by viewModels()
            val navController = rememberNavController()
            OBD2AppTheme {
                NavHost(navController = navController, startDestination = "screenStart", builder = {
                    composable("screenStart") {
                        BluetoothConnectionScreen(navController, viewModel, bluetoothAdapter)
                    }
                    composable("screenTerminal") {
                        ObdCommandScreen(navController, viewModel)
                    }
                })
//                ItemsList(devices)
//                Layout()
            }
        }
    }
}

@Composable
fun BluetoothConnectionScreen(navController: NavController, viewModel: SimpleViewModel, bluetoothAdapter: BluetoothAdapter) {
//    var selectedItemIndex by remember { mutableStateOf(-1) }
//    var deviceAddress by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var isBluetoothPermissionGranted by remember { mutableStateOf(
        context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
    ) }
    // Проверка и запрос разрешений
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            isBluetoothPermissionGranted = true
            // Permission Accepted: Do something
//            Log.d("ExampleScreen","PERMISSION GRANTED")
        }
    }
    if (!isBluetoothPermissionGranted)
        launcher.launch(Manifest.permission.BLUETOOTH_CONNECT)
    val pairedDevices = bluetoothAdapter.bondedDevices.toList()
//    val pairedDevices: List<BluetoothDevice?> = listOf<BluetoothDevice?>(null)
    var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }
    Column {
        Row {
            Spacer(modifier = Modifier.height(30.dp))
        }
        Row {
            Column {
                pairedDevices.forEach { device ->
                    device?.let {
                        ListItem(
                            headlineContent = { Text(device.name) },
                            colors = ListItemDefaults.colors(
                                containerColor = if (selectedDevice != null && selectedDevice == device) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.inversePrimary
                            ),
                            modifier = Modifier.clickable {
                                selectedDevice = device // Запоминаем строку
                            }
                        )
                    }
                }
            }
        }
        Row {
            Button(
                onClick = {
                    if (selectedDevice == null)
                        return@Button
                    if (!isBluetoothPermissionGranted)
                        launcher.launch(Manifest.permission.BLUETOOTH_CONNECT)
                    coroutineScope.launch {
                        try {
                            // UUID для ELM327
                            val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                            // Создаём сокет и подключаемся
                            val socket = selectedDevice!!.createRfcommSocketToServiceRecord(uuid)
                            socket.connect()
                            // Устанавливаем соединение в ViewModel
                            viewModel.setConnection(socket)
                            // Переходим на экран отправки команд
                            navController.navigate("commandScreen")
                        } catch (_: Exception) {
//                            viewModel.setConnectionStatus("Ошибка подключения: ${e.message}")
                        }
                    }
                    navController.navigate("screenTerminal")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Подключить")
            }
        }
    }
}

@Composable
fun ObdCommandScreen(navController: NavController, viewModel: SimpleViewModel) {
    // Состояние для ввода текста
    var inputText by remember { mutableStateOf("010C") }
    // Состояние для вывода текста
    val response by viewModel.commandResponse.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row {
            Button(
                onClick = {
                    viewModel.disconnect()
                    navController.navigate("screenStart")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Отключиться")
            }
        }
        // Поле для ввода текста
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Введите текст") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка для ввода команды
        Button(
            onClick = {
                viewModel.sendCommand(inputText)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Отправить")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Поле для вывода текста
        // Текст с возможностью прокрутки
        SelectionContainer {
            Text(
                text = response,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            )
        }
    }
}

@Preview(
    showSystemUi = true
)
@Composable
fun GreetingPreview() {
//    val items = listOf("Kotlin", "Java", "JavaScript", "Python", "C#", "C++", "Rust")
    OBD2AppTheme {
//        ItemsList(items)
    }
}