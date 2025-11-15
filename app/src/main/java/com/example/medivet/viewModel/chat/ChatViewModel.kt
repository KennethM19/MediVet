package com.example.medivet.viewModel.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medivet.model.model.Message
import com.example.medivet.model.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>(emptyList())
    val messages: LiveData<List<Message>> = _messages

    fun sendMessage(userMessage: String) {
        val currentMessages = _messages.value ?: emptyList()
        val newMessages = currentMessages + Message(userMessage, true)
        _messages.value = newMessages

        viewModelScope.launch(Dispatchers.IO) {
            val botReply = repository.sendMessage(userMessage)
            val updatedMessages = listOf(Message(botReply, false)) + newMessages
            _messages.postValue(updatedMessages)
        }
    }
}