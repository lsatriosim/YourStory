package com.example.yourstory.network.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


object SingletonDatastore{
    private var dataStoreInstance: DataStore<Preferences>? = null

    fun getInstance(context: Context): DataStore<Preferences>{
        synchronized(this){
            if(dataStoreInstance == null){
                dataStoreInstance = context.dataStore
            }
            return dataStoreInstance!!
        }
    }
}