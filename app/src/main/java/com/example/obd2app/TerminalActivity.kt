package com.example.obd2app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.obd2app.ui.theme.OBD2AppTheme

class TerminalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        
        enableEdgeToEdge()
        setContent {
            OBD2AppTheme {
                Layout()
            }
        }
    }

    @Composable
    fun Layout() {
        // Состояние для ввода текста
        var inputText by remember { mutableStateOf("00") }
        // Состояние для вывода текста
        var displayedText by remember { mutableStateOf("Нет пока") }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Поле для ввода текста
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Введите текст") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка для вывода текста
            Button(
                onClick = {
                    // При нажатии на кнопку выводим текст в TextView
                    // TODO отправить запрос из inputText
                    displayedText = inputText
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Отправить")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Поле для вывода текста
            Text(
                text = displayedText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    OBD2AppTheme {
        Layout()
    }
}