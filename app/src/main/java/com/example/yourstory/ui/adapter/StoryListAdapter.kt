package com.example.yourstory.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yourstory.databinding.ItemStoryBinding
import com.example.yourstory.network.remote.responses.Story

class StoryListAdapter: PagingDataAdapter<Story, StoryListAdapter.MyViewHolder>(DIFF_CALLBACK) {
    companion object{
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>(){
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    class MyViewHolder(private val binding: ItemStoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Story){
            binding.tvItemName.text = data.name
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .into(binding.ivItemPhoto)
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       val data = getItem(position)
        Log.d("AdapterList", data?.name ?: "gaada datanya")
        if (data != null){
            holder.bind(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

}