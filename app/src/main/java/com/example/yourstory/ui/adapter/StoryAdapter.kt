package com.example.yourstory.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yourstory.R
import com.example.yourstory.network.remote.responses.Story

class StoryAdapter(private val listStory: List<Story>): RecyclerView.Adapter<StoryAdapter.ViewHolder>() {
    private var onItemClickCallback: OnItemClickCallback? = null

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imgStory = view.findViewById<ImageView>(R.id.iv_item_photo)
        val tvName = view.findViewById<TextView>(R.id.tv_item_name)
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: List<String>)
    }


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false))
    }

    override fun getItemCount(): Int = listStory.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story: List<String> = listOf(listStory[position].photoUrl, listStory[position].name, listStory[position].description)
        Glide.with(holder.itemView.context)
            .load(story[0])
            .into(holder.imgStory)
        holder.tvName.text = story[1]
        holder.itemView.setOnClickListener{onItemClickCallback?.onItemClicked(story)}
    }

}