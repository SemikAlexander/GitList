package com.example.testapp.ui.user

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import coil.transform.CircleCropTransformation
import com.example.testapp.R
import com.example.testapp.R.drawable.ic_avatar_placeholder
import com.example.testapp.R.string.*
import com.example.testapp.databinding.FragmentUserBinding
import com.example.testapp.extention.toast
import com.example.testapp.extention.viewBinding
import kotlinx.coroutines.launch

class UserFragment : Fragment(R.layout.fragment_user) {
    private val binding: FragmentUserBinding by viewBinding()
    private val viewModel by lazy { ViewModelProviders.of(this)[UserViewModel::class.java] }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            arguments?.getString("username")?.let { viewModel.getUser(it) }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userData.collect { user ->
                    binding.run {
                        avatarIV.load(user.avatar_url) {
                            crossfade(true)
                            placeholder(ic_avatar_placeholder)
                            transformations(CircleCropTransformation())
                        }

                        nameUserTV.text = user.name

                        loginTV.text = "username: ${user.login}"

                        emailTV.text = user.email ?: resources.getString(no_email)
                        blogTV.text = user.blog ?: resources.getString(no_blog)
                        locationTV.text = user.location ?: resources.getString(no_location)
                        organizationTV.text = user.company ?: resources.getString(no_organization)

                        followersTV.text = (user.followers ?: resources.getString(no_followers)).toString()
                        followingTV.text = (user.following ?: resources.getString(no_following)).toString()

                        createdTV.text = user.created_at?.let { viewModel.dateParse(it) }
                        updatedTV.text = user.updated_at?.let { viewModel.dateParse(it) }

                        emailTV.setOnClickListener {
                            if (user.email != null) {
                                try {
                                    val emailIntent = Intent(
                                        Intent.ACTION_SENDTO, Uri.fromParts(
                                            "mailto", "${user.email}", null
                                        )
                                    )

                                    ContextCompat.startActivity(
                                        requireContext(),
                                        emailIntent,
                                        null
                                    )
                                } catch (ex: Exception) {
                                    toast(resources.getString(email_error))
                                }
                            }
                        }

                        blogTV.setOnClickListener {
                            if (user.blog != null) {
                                try {
                                    val webIntent = Intent(Intent.ACTION_VIEW)
                                    webIntent.data = Uri.parse(user.blog)

                                    startActivity(webIntent)
                                } catch (ex: Exception) {
                                    toast(resources.getString(web_error))
                                }
                            }
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isUserDataLoadError.collect {
                    if (it) {
                        toast("Error!")
                    }
                }
            }
        }
    }
}