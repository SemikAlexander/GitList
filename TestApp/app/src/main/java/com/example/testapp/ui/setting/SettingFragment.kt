package com.example.testapp.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.testapp.R
import com.example.testapp.databinding.FragmentSettingBinding
import com.example.testapp.extention.toast
import com.example.testapp.extention.viewBinding

class SettingFragment : Fragment(R.layout.fragment_setting) {
    private val binding: FragmentSettingBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            gitHubRL.setOnClickListener {
                openLink("https://docs.github.com/en/rest")
            }

            sourceCodeRL.setOnClickListener {
                openLink("https://docs.github.com/en/rest")
            }
        }
    }

    private fun openLink(link: String) {
        try {
            val webIntent = Intent(Intent.ACTION_VIEW)
            webIntent.data = Uri.parse(link)

            startActivity(webIntent)
        } catch (ex: Exception) {
            toast(resources.getString(R.string.web_error))
        }
    }
}