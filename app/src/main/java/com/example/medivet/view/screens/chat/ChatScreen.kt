package com.example.medivet.view.screens.chat

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.medivet.utils.SessionManager
import com.example.medivet.viewModel.chat.ChatViewModel
import com.example.medivet.viewModel.chat.ChatViewModelFactory

@Composable
fun ChatScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    val factory = ChatViewModelFactory(sessionManager = sessionManager)
    val chatViewModel: ChatViewModel = viewModel(factory = factory)

    var message by remember { mutableStateOf("") }
    val messages by chatViewModel.messages.observeAsState(emptyList())
    val isTyping by chatViewModel.isTyping.observeAsState(false)

    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            reverseLayout = false,
            state = listState
        ) {

            items(messages) { msg ->
                ChatBubble(msg.text, msg.isUser)
            }

            if (isTyping) {
                item {
                    TypingIndicator()
                }
            }
        }

        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .padding(bottom = 30.dp)
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje…") },
                maxLines = 4
            )
            Button(
                onClick = {
                    if (message.isNotBlank()) {
                        chatViewModel.sendMessage(message)
                        message = ""
                    }
                },
                modifier = Modifier.padding(start = 6.dp)
            ) {
                Text("Enviar")
            }
        }
    }
}

@Composable
fun ChatBubble(text: String, isUser: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 260.dp)
                .background(
                    if (isUser) Color(0xFFDCF8C6) else Color(0xFFEFEFEF),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(text)
        }
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition()
    val dot1 = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Row(
        modifier = Modifier
            .padding(8.dp)
            .background(Color(0xFFEFEFEF), shape = RoundedCornerShape(12.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Dot(alpha = dot1.value)
        Dot(alpha = (dot1.value - 0.33f).coerceIn(0f, 1f))
        Dot(alpha = (dot1.value - 0.66f).coerceIn(0f, 1f))
    }
}

@Composable
fun Dot(alpha: Float) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .background(Color.Gray.copy(alpha = alpha), CircleShape)
    )
}

