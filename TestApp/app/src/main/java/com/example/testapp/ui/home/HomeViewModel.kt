package com.example.testapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.testapp.services.model.response.users.UserList
import com.example.testapp.services.model.response.users.UserListItem
import com.example.testapp.services.retrofit.API
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

class HomeViewModel : ViewModel() {
    val userList: Flow<PagingData<UserListItem>> = Pager(PagingConfig(pageSize = 20)) {
        GenericDataSource(::getUserList)
    }.flow.stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())
        .cachedIn(viewModelScope)

    private val _isUserListError = MutableStateFlow(false)
    val isUserListError: StateFlow<Boolean> by lazy { _isUserListError.asStateFlow() }

    private suspend fun getUserList(
        pageIndex: Int,
        pageSize: Int,
        searchFields: String? = null,
        filter: String? = null,
        type: String? = null
    ): List<UserListItem> {
        return try {
            val users = API.api.getAllUsers(pageIndex)
            users
        } catch (e: Exception) {
            listOf()
        }
    }
}