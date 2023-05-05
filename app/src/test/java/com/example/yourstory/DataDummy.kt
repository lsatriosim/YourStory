package com.example.yourstory

import com.example.yourstory.network.remote.responses.Story
import kotlin.text.Typography.quote

object DataDummy {
    fun generateDummyStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                i.toString(),
                "createdAt $i",
                "name $i",
                "description $i",
                "id $i",
                i.toDouble(),
                i.toDouble()
            )
            items.add(story)
        }
        return items
    }
}