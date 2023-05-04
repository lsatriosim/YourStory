package com.example.yourstory.ui.login

import UserPreferences
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.*
import com.example.yourstory.network.local.UserLogin
import com.example.yourstory.network.remote.responses.LoginResponse
import com.example.yourstory.network.remote.retrofit.ApiConfig
import com.example.yourstory.ui.register.RegisterViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreferences,private var dataStore: DataStore<Preferences>): ViewModel() {
    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading:LiveData<Boolean> = _isLoading

    companion object{
        const val TAG = "LoginViewModel"
    }

    fun saveUser(user: UserLogin){
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

    fun getUser():LiveData<UserLogin>{
        return pref.getUser().asLiveData()
    }

    init {
        _isLoading.value = false
    }

    fun login(email:String, password:String){
        _isLoading.value = true
        val client = ApiConfig.getApiService(dataStore).login(email,password)
        client.enqueue(object: Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if(response.isSuccessful){
                    _loginResponse.value = response.body()
                }else{
                    Log.e(LoginViewModel.TAG, "onFailure (s): ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(LoginViewModel.TAG, "onFailure (f): ${t.message.toString()}")
            }

        })
    }
}