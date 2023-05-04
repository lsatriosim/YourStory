package com.example.yourstory.network.remote.responses

import com.google.gson.annotations.SerializedName

data class StoriesResponse(

    @field:SerializedName("listStory")
	val listStory: List<Story>,

    @field:SerializedName("error")
	val error: Boolean,

    @field:SerializedName("message")
	val message: String
)
