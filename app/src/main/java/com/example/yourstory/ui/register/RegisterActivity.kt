package com.example.yourstory.ui.register

import UserPreferences
import android.content.Context
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
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.yourstory.R
import com.example.yourstory.databinding.ActivityRegisterBinding
import com.example.yourstory.network.local.SingletonDatastore
import com.example.yourstory.network.local.UserLogin
import com.example.yourstory.ui.Home.HomeActivity
import com.example.yourstory.ui.ViewModelFactory
import com.example.yourstory.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var dataStore: DataStore<Preferences>
    private var mIsShowPass = false
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    private var name = ""
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataStore = SingletonDatastore.getInstance(this)


        binding.edRegisterEmail.hint = getString(R.string.input_email)
        binding.edRegisterPassword.hint = getString(R.string.input_password)
        binding.edRegisterName.hint = getString(R.string.input_name)

        val text = getString(R.string.register_to_login)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                Log.d("intentHepmew", "jalan")
            }
        }
        val spannableString = SpannableString(text)
        val indexStart = 43
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
        binding.tvRegisterToLogin.text = spannableString
        binding.tvRegisterToLogin.movementMethod = LinkMovementMethod.getInstance()

        binding.showPassword.setOnClickListener {
            mIsShowPass = !mIsShowPass
            showPassword(mIsShowPass)
        }

        showPassword(mIsShowPass)

        setViewModel()
        setupAction()

        registerViewModel.registerResponse.observe(this) {
            if (!it.error) {
                registerViewModel.login(email, password)
            } else {
                Toast.makeText(this@RegisterActivity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        registerViewModel.loginResponse.observe(this) {
            registerViewModel.saveUser(
                UserLogin(
                    it.loginResult.name,
                    it.loginResult.userId,
                    it.loginResult.token
                )
            )
            val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
            startActivity(intent)
        }

        registerViewModel.isLoading.observe(this) {
            loadingButton(it)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Close the app when the back button is pressed from the home activity
        finishAffinity()
    }

    private fun showPassword(isShow: Boolean) {
        if (isShow) {
            binding.edRegisterPassword.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
            binding.showPassword.setImageResource(R.drawable.ic_hide_password)
        } else {
            binding.edRegisterPassword.transformationMethod =
                PasswordTransformationMethod.getInstance()
            binding.showPassword.setImageResource(R.drawable.ic_reveal_password)
        }
        binding.edRegisterPassword.setSelection(binding.edRegisterPassword.text.toString().length)
    }

    private fun setViewModel() {
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore), dataStore, this)
        )[RegisterViewModel::class.java]
    }

    private fun setupAction() {
        binding.btnRegister.setOnClickListener {
            name = binding.edRegisterName.text.toString()
            email = binding.edRegisterEmail.text.toString()
            password = binding.edRegisterPassword.text.toString()

            when {
                name.isEmpty() -> {
                    binding.edRegisterName.error = "Input name"
                }
                email.isEmpty() -> {
                    binding.edRegisterEmail.error = "Input email"
                }
                password.isEmpty() -> {
                    binding.edRegisterPassword.error = "Input password"
                }
                else -> {
                    if (!binding.edRegisterPassword.isError && !binding.edRegisterEmail.isError) {
                        registerViewModel.register(name, email, password)
                    }
                }
            }
        }
    }

    private fun loadingButton(isLoading: Boolean) {
        binding.btnRegister.isEnabled = !isLoading
        if (isLoading) {
            binding.btnRegister.text = getString(R.string.loading)
        } else {
            binding.btnRegister.text = getString(R.string.btn_register)
        }
    }
}