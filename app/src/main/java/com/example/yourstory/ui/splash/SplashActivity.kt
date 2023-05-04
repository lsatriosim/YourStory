package com.example.yourstory.ui.splash

import UserPreferences
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.yourstory.R
import com.example.yourstory.network.local.SingletonDatastore
import com.example.yourstory.ui.Home.HomeActivity
import com.example.yourstory.ui.login.LoginActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SplashActivity : AppCompatActivity() {
    private lateinit var dataStore: DataStore<Preferences>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        dataStore = SingletonDatastore.getInstance(this)

        val userSession = UserPreferences(dataStore)
        val token = runBlocking{
            dataStore.data.first()[stringPreferencesKey("token")]
        }
        Log.d("UserSession", userSession.toString())
        val intent = if(token != null){
            Intent(this@SplashActivity, HomeActivity::class.java)
        }else{
            Intent(this@SplashActivity, LoginActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}