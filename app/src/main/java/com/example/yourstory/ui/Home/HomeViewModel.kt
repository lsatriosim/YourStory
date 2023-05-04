package com.example.yourstory.ui.Home

import UserPreferences
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.yourstory.network.remote.paging.StoryRepository
import com.example.yourstory.network.remote.responses.StoriesResponse
import com.example.yourstory.network.remote.responses.Story
import com.example.yourstory.network.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private var pref: UserPreferences,private var dataStore: DataStore<Preferences>, storyRepository: StoryRepository) : ViewModel() {
    private val _story = MutableLiveData<List<Story>>()
    val story: LiveData<List<Story>> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val storyPaging: LiveData<PagingData<Story>> = storyRepository.getStory().cachedIn(viewModelScope)

    companion object{
        private const val TAG = "HomeViewModel"
    }

    init{
        getStory()
    }

    private fun getStory(){
        _isLoading.value = true
        val client = ApiConfig.getApiService(dataStore).getStories()
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                _isLoading.value = false
                if(response.isSuccessful){
                    _story.value = response.body()?.listStory
                }else{
                    Log.e(TAG, "onFailure (s): ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure (f): ${t.message.toString()}")
            }

        })
    }
}