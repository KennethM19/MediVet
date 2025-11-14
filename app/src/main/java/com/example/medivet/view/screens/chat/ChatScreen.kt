package com.example.medivet.view.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medivet.model.model.Message

@Composable
fun ChatScreen(
    navController: NavController,
) {
    var message by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>() }
    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            reverseLayout = true
        ) {
            items(messages) {
                msg ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                ) {
                    Text(
                        text = msg.text,
                        modifier = Modifier
                            .background(
                                if (msg.isUser) Color(0xFFD1E7DD) else Color(0xFFF8D7DA),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    )
                }

            }
        }

        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(0);
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje...") }
            )
            Button(
                onClick = {
                    if (message.isNotBlank()) {
                        // Mensaje del usuario
                        messages.add(0, Message(message, true))

                        // Respuesta del bot (simulada)
                        val botReply = getBotResponse(message)
                        messages.add(0, Message(botReply, false))

                        message = ""
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Enviar")
            }
        }
    }
}

fun getBotResponse(userMessage: String): String {
    return when {
        userMessage.contains("hola", ignoreCase = true) -> "¡Hola! ¿Cómo estás?"
        userMessage.contains("perro", ignoreCase = true) -> "Me encantan los perros 🐶"
        else -> "Interesante... cuéntame más."
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatScreen() {
    val navController = rememberNavController()
    ChatScreen(navController)
}