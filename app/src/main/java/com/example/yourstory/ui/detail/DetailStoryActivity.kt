package com.example.yourstory.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.yourstory.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    companion object {
        const val EXTRA_PHOTO = "extra_photo"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_DESCRIPTION = "extra_description"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var photo_url = intent.getStringExtra(EXTRA_PHOTO)
        var name = intent.getStringExtra(EXTRA_NAME)
        var description = intent.getStringExtra(EXTRA_DESCRIPTION)

        Glide.with(this@DetailStoryActivity)
            .load(photo_url)
            .into(binding.ivDetailPhoto)
        binding.tvDetailName.text = name
        binding.tvDetailDescription.text = description
    }
}