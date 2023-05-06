package com.example.yourstory.ui.login

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModelProvider
import com.example.yourstory.R
import com.example.yourstory.databinding.ActivityLoginBinding
import com.example.yourstory.network.local.SingletonDatastore
import com.example.yourstory.network.local.UserLogin
import com.example.yourstory.ui.Home.HomeActivity
import com.example.yourstory.ui.ViewModelFactory
import com.example.yourstory.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var dataStore: DataStore<Preferences>
    private var mIsShowPass = false
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private var email = ""
    private var password = ""
    private lateinit var user: UserLogin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataStore = SingletonDatastore.getInstance(this)

        binding.edLoginEmail.hint = getString(R.string.input_email)
        binding.edLoginPassword.hint = getString(R.string.input_password)
        val text = getString(R.string.login_to_register)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
                Log.d("intentHepmew", "jalan")
            }
        }
        val spannableString = SpannableString(text)
        val indexStart = 41
        val indexEnd = 48
        spannableString.setSpan(
            clickableSpan,
            indexStart,
            indexEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLUE),
            indexStart,
            indexEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvLoginToRegister.text = spannableString
        binding.tvLoginToRegister.movementMethod = LinkMovementMethod.getInstance()

        binding.showPassword.setOnClickListener {
            mIsShowPass = !mIsShowPass
            showPassword(mIsShowPass)
        }

        showPassword(mIsShowPass)

        setViewModel()
        setupAction()

        loginViewModel.loginResponse.observe(this) {
            if (!it.error) {
                var result = it.loginResult
                loginViewModel.saveUser(UserLogin(result.name, result.userId, result.token))
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this@LoginActivity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        loginViewModel.isLoading.observe(this) {
            loadingButton(it)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Close the app when the back button is pressed from the home activity
        finishAffinity()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            email = binding.edLoginEmail.text.toString()
            password = binding.edLoginPassword.text.toString()

            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = "Input email"
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.error = "Input password"
                }
                else -> {
                    if (!binding.edLoginPassword.isError && !binding.edLoginEmail.isError) {
                        loginViewModel.login(email, password)
                    }
                }
            }
        }
    }

    private fun setViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore), dataStore, this)
        )[LoginViewModel::class.java]

        loginViewModel.getUser().observe(this) { user ->
            this.user = user
        }
    }

    private fun showPassword(isShow: Boolean) {
        if (isShow) {
            binding.edLoginPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.showPassword.setImageResource(R.drawable.ic_hide_password)
        } else {
            binding.edLoginPassword.transformationMethod =
                PasswordTransformationMethod.getInstance()
            binding.showPassword.setImageResource(R.drawable.ic_reveal_password)
        }
        binding.edLoginPassword.setSelection(binding.edLoginPassword.text.toString().length)
    }

    private fun loadingButton(isLoading: Boolean) {
        binding.btnLogin.isEnabled = !isLoading
        if (isLoading) {
            binding.btnLogin.text = getString(R.string.loading)
        } else {
            binding.btnLogin.text = getString(R.string.login)
        }
    }
}