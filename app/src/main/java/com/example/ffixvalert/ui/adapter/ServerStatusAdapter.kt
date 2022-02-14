package com.example.ffixvalert.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ffixvalert.databinding.ServerRowBinding
import com.example.ffixvalert.model.ServerStatus


class ServerStatusAdapter(private val onCheckChangeListener: ServerCheckChangeListener) :
    ListAdapter<ServerStatus, RecyclerView.ViewHolder>(ServerStatueDifferCallback()) {

    class ViewHolder(private val binding: ServerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ServerStatus, onCheck: ServerCheckChangeListener) {
            binding.serverStatus = item
            binding.onCheck = onCheck
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ServerRowBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(getItem(position),onCheckChangeListener)
    }


}

class ServerStatueDifferCallback : DiffUtil.ItemCallback<ServerStatus>() {
    override fun areItemsTheSame(oldItem: ServerStatus, newItem: ServerStatus): Boolean {
        return oldItem.canCreateCharacter == newItem.canCreateCharacter
    }

    override fun areContentsTheSame(oldItem: ServerStatus, newItem: ServerStatus): Boolean {
        return oldItem == newItem
    }
}

class ServerCheckChangeListener(private val onCheckListener:(CompoundButton, Boolean, ServerStatus) -> Unit){
    fun onCheck(b: CompoundButton, check: Boolean, c: ServerStatus) = onCheckListener(b,check,c)
}