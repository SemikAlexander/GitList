package com.example.testapp.ui.user

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.services.model.response.user.UserData
import com.example.testapp.services.retrofit.API
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserViewModel: ViewModel() {
    private val _isUserDataLoadError = MutableStateFlow(false)
    val isUserDataLoadError: StateFlow<Boolean> by lazy { _isUserDataLoadError.asStateFlow() }

    private val _userData = MutableSharedFlow<UserData>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    val userData: SharedFlow<UserData> = _userData.asSharedFlow()

    fun getUser(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { API.api.getUser(userName) }
                .onFailure { _isUserDataLoadError.value = true }
                .onSuccess {
                    _userData.tryEmit(it)
                    _isUserDataLoadError.value = false
                }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun dateParse(date: String): String {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")

        return formatter.format(parser.parse(date))
    }
}