package com.example.yourstory.ui.register

import UserPreferences
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourstory.network.local.UserLogin
import com.example.yourstory.network.remote.responses.LoginResponse
import com.example.yourstory.network.remote.responses.RegisterResponse
import com.example.yourstory.network.remote.retrofit.ApiConfig
import com.example.yourstory.ui.Home.HomeViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val pref: UserPreferences,private var dataStore: DataStore<Preferences>): ViewModel() {
    companion object{
        const val TAG = "RegisterViewModel"
    }

    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse:LiveData<RegisterResponse> = _registerResponse

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading:LiveData<Boolean> = _isLoading

    fun saveUser(user: UserLogin){
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

    init {
        _isLoading.value = false
    }

    fun register(name:String, email:String, password:String){
        _isLoading.value = true
        val client = ApiConfig.getApiService(dataStore).register(name, email, password)
        client.enqueue(object: Callback<RegisterResponse>{
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if(response.isSuccessful){
                    _registerResponse.value = response.body()
                }else{
                    Log.e(RegisterViewModel.TAG, "onFailure (s): ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(RegisterViewModel.TAG, "onFailure (f): ${t.message.toString()}")
            }

        })
    }

    fun login(email:String, password:String){
        _isLoading.value = true
        val client = ApiConfig.getApiService(dataStore).login(email,password)
        client.enqueue(object: Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if(response.isSuccessful){
                    _loginResponse.value = response.body()
                }else{
                    Log.e(RegisterViewModel.TAG, "onFailure (s): ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(RegisterViewModel.TAG, "onFailure (f): ${t.message.toString()}")
            }

        })
    }
}