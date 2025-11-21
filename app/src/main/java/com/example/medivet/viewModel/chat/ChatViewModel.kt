package com.example.medivet.viewModel.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.model.model.ChatRequest
import com.example.medivet.model.model.Message
import com.example.medivet.model.repository.ChatRepository
import com.example.medivet.utils.SessionManager
import kotlinx.coroutines.delay

import kotlinx.coroutines.launch


class ChatViewModel(
    private val sessionManager: SessionManager,
    private val chatRepository: ChatRepository = ChatRepository()
) : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>(emptyList())
    val messages: LiveData<List<Message>> = _messages
    private val _isTyping = MutableLiveData(false)
    val isTyping: LiveData<Boolean> = _isTyping

    fun sendMessage(message: String) {
        val current = _messages.value ?: emptyList()
        _messages.value = current + Message(message, isUser = true)

        viewModelScope.launch {
            try {
                val token = sessionManager.getToken() ?: return@launch
                _isTyping.value = true
                val response = chatRepository.sendMessage(ChatRequest(message), token)
                delay(700)
                _isTyping.value = false
                val updated = _messages.value ?: emptyList()
                _messages.value = updated + Message(response.response, isUser = false)

            } catch (e: Exception) {
                _isTyping.value = false
                val updated = _messages.value ?: emptyList()
                _messages.value = updated + Message("Error: ${e.message}", isUser = false)
            }
        }
    }

}