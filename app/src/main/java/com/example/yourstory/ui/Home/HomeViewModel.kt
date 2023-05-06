package com.example.yourstory.ui.Home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.yourstory.network.remote.paging.StoryRepository
import com.example.yourstory.network.remote.responses.Story

class HomeViewModel(storyRepository: StoryRepository) : ViewModel() {
    private val _story = MutableLiveData<List<Story>>()
    val story: LiveData<List<Story>> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val storyPaging: LiveData<PagingData<Story>> = storyRepository.getStory().cachedIn(viewModelScope)

    companion object{
        private const val TAG = "HomeViewModel"
    }
}