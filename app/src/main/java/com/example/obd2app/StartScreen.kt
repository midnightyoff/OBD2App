package com.example.obd2app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun StartScreen(items: List<String>, navController: NavController) {
//    var selectedItemIndex by remember { mutableStateOf(-1) }
    var selectedItem = remember { mutableStateOf<String>("") }
    Column {
        Row {
            Spacer(modifier = Modifier.height(30.dp))
        }
        Row {
            Column {
                items.forEach { item ->
                    ListItem(
                        headlineContent = { Text(item) },
                        colors = ListItemDefaults.colors(
                            containerColor = if (selectedItem.value.equals(item)) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.inversePrimary
                        ),
                        modifier = Modifier.clickable {
                            selectedItem.value = item // Запоминаем строку
                        }
                    )
                }
            }
        }
        Row {
            Button(
                onClick = {
                    // TODO подключиться к адаптеру
                    navController.navigate("screenTerminal")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Подключить")
            }
        }
    }
}

/*
@Composable
fun SimpleItemsList(items: List<String>) {
    val selectedItem = remember { mutableStateOf(setOf<String>()) }
    Column {
        items.forEach { item ->
            ListItem(
                headlineContent = { Text(item) }
            )
        }
    }
}*/
