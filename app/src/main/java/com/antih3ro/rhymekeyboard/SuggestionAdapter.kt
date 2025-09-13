package com.antih3ro.rhymekeyboard

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.antih3ro.rhymekeyboard.databinding.SuggestionItemBinding

class SuggestionAdapter(private val onWordClick: (String) -> Unit) :
    ListAdapter<String, SuggestionAdapter.SuggestionViewHolder>(SuggestionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val binding = SuggestionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SuggestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val word = getItem(position)
        holder.bind(word)
        holder.itemView.setOnClickListener { onWordClick(word) }
    }

    class SuggestionViewHolder(binding: SuggestionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val textView: TextView = binding.suggestionWord
        fun bind(word: String) {
            textView.text = word
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