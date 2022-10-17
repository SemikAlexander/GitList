package com.example.testapp.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.testapp.R
import com.example.testapp.databinding.ItemUserListBinding
import com.example.testapp.services.model.response.users.UserListItem

class UserListAdapter(
    val onItemClick: (UserListItem) -> Unit = {}
) : PagingDataAdapter<UserListItem, UserListAdapter.Holder>(UsersDiffUtil) {

    override fun onBindViewHolder(holder: Holder, position: Int) {
        getItem(position)?.let(holder::bind)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return Holder(ItemUserListBinding.inflate(layoutInflater, parent, false))
    }

    inner class Holder(private val binding: ItemUserListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: UserListItem) {
            binding.run {
                item.run {
                    itemView.setOnClickListener {
                        onItemClick(item)
                    }

                    avatarIV.load(avatar_url) {
                        crossfade(true)
                        placeholder(R.drawable.ic_avatar_placeholder)
                        transformations(CircleCropTransformation())
                    }

                    loginTV.text = item.login

                    idUserTV.text = "id: ${item.id}"
                }
            }
        }
    }
}

object UsersDiffUtil : DiffUtil.ItemCallback<UserListItem>() {
    override fun areItemsTheSame(oldItem: UserListItem, newItem: UserListItem): Boolean {
        if (oldItem.id == newItem.id && oldItem.login == newItem.login)
            return true
        return false
    }

    override fun areContentsTheSame(oldItem: UserListItem, newItem: UserListItem): Boolean {
        if (oldItem == newItem) {
            return true
        }
        return false
    }
}