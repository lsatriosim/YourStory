package com.example.yourstory.network.remote.paging

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.yourstory.network.local.StoryDatabase
import com.example.yourstory.network.local.StoryRemoteMediator
import com.example.yourstory.network.remote.responses.Story
import com.example.yourstory.network.remote.retrofit.ApiService

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService) {
    fun getStory(): LiveData<PagingData<Story>>{
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase,apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}