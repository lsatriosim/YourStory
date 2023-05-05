package com.example.yourstory.ui

import UserPreferences
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yourstory.di.Injection
import com.example.yourstory.ui.Home.HomeViewModel
import com.example.yourstory.ui.login.LoginViewModel
import com.example.yourstory.ui.maps.MapsViewModel
import com.example.yourstory.ui.register.RegisterViewModel

class ViewModelFactory(private val pref: UserPreferences, private val dataStore: DataStore<Preferences>, private val context: Context?) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(Injection.provideRepository(context!!)) as T
        }else if(modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            return RegisterViewModel(pref,dataStore) as T
        }else if(modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(pref,dataStore) as T
        }else if(modelClass.isAssignableFrom(MapsViewModel::class.java)){
            return MapsViewModel(dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}