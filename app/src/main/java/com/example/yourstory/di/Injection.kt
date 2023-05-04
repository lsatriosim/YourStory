package com.example.yourstory.di

import android.content.Context
import com.example.yourstory.network.local.SingletonDatastore
import com.example.yourstory.network.local.StoryDatabase
import com.example.yourstory.network.remote.paging.StoryRepository
import com.example.yourstory.network.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository{
        var datastore = SingletonDatastore.getInstance(context)
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService(datastore)
        return StoryRepository(database,apiService)
    }
}