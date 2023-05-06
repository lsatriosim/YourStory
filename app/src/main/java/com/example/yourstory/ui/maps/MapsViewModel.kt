package com.example.yourstory.ui.maps

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yourstory.network.remote.responses.StoriesResponse
import com.example.yourstory.network.remote.responses.Story
import com.example.yourstory.network.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private var dataStore: DataStore<Preferences>) : ViewModel() {
    private val _story = MutableLiveData<List<Story>>()
    val story: LiveData<List<Story>> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private const val TAG = "MapViewModel"
    }


    init {
        getStory()
    }

    private fun getStory() {
        _isLoading.value = true
        val client = ApiConfig.getApiService(dataStore).getStoriesWithLocation(1)
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _story.value = response.body()?.listStory
                } else {
                    Log.e(MapsViewModel.TAG, "onFailure (s): ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(MapsViewModel.TAG, "onFailure (f): ${t.message.toString()}")
            }

        })
    }
}