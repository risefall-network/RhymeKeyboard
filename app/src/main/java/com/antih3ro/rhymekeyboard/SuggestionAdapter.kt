package com.antih3ro.rhymekeyboard

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class SuggestionAdapter(
    private val onSuggestionClick: (String) -> Unit
) : ListAdapter<String, SuggestionAdapter.SuggestionViewHolder>(SuggestionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val button = LayoutInflater.from(parent.context)
            .inflate(R.layout.suggestion_item, parent, false) as Button
        return SuggestionViewHolder(button, onSuggestionClick)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SuggestionViewHolder(
        private val button: Button,
        private val onSuggestionClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(button) {
        fun bind(word: String) {
            button.text = word
            button.setOnClickListener { onSuggestionClick(word) }
        }
    }
}

class SuggestionDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}