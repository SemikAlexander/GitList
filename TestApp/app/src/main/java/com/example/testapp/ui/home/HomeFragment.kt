package com.example.testapp.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.testapp.R
import com.example.testapp.R.id
import com.example.testapp.R.id.*
import com.example.testapp.databinding.FragmentHomeBinding
import com.example.testapp.extention.gone
import com.example.testapp.extention.viewBinding
import com.example.testapp.extention.visible
import com.example.testapp.ui.home.adapter.UserListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val binding: FragmentHomeBinding by viewBinding()
    private val viewModel by lazy { ViewModelProviders.of(this)[HomeViewModel::class.java] }

    private val userListAdapter by lazy(LazyThreadSafetyMode.PUBLICATION) {
        UserListAdapter(
            onItemClick = {
                val bundle = Bundle()
                bundle.putString("username", it.login)

                findNavController().navigate(action_homeFragment_to_userFragment, bundle)
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            settingIV.setOnClickListener {
                findNavController().navigate(action_homeFragment_to_settingFragment)
            }
        }

        userListData()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isUserListError.collect {
                    if (it) {
                        binding.progressBar.gone()
                        binding.usersRV.gone()
                    }
                }
            }
        }
    }

    private fun userListData() {
        binding.run {
            usersRV.visible()

            runCatching {
                viewModel.viewModelScope.launch(Dispatchers.Main) {
                    viewModel.userList.collectLatest {
                        userListAdapter.submitData(it)
                    }
                }
            }

            usersRV.run {
                adapter = userListAdapter
                setHasFixedSize(true)
            }

            userListAdapter.addLoadStateListener {
                when (it.refresh) {
                    LoadState.Loading -> binding.progressBar.visible()
                    else -> binding.progressBar.gone()
                }
            }
        }
    }
}