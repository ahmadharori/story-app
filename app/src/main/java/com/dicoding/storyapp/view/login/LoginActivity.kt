package com.dicoding.storyapp.view.login

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityLoginBinding
import com.dicoding.storyapp.model.UserPreference
import com.dicoding.storyapp.view.ViewModelFactory
import com.dicoding.storyapp.view.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        loginViewModel.response.observe(this) {
            if (it.error == false) {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.success))
                    setMessage(it.message)
                    setPositiveButton(getString(R.string.proceed)) { _, _ ->
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    create()
                    show()
                }
            } else {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.failed))
                    setMessage(it.message)
                    setNegativeButton(getString(R.string.close)) {_,_ ->
                    }
                    create()
                    show()
                }
            }
        }

        loginViewModel.failure.observe(this) { failure ->
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.failed))
                setMessage(failure.message)
                setNegativeButton(getString(R.string.close)) {_,_ ->
                }
                create()
                show()
            }
        }
    }

    private fun setupAction() {

        binding.emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.emailEditTextLayout.error = null
                binding.emailEditText.setPadding(0,0,32,0)
            } else {
                if (binding.emailEditText.text.toString() == "") {
                    binding.emailEditText.setPadding(0, 0, intToDpConverter(44), 0)
                    binding.emailEditTextLayout.error = getString(R.string.field_no_empty)
                }
            }
        }

        binding.passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.passwordEditTextLayout.error = null
            } else {
                if (binding.passwordEditText.text.toString() == "") {
                    binding.passwordEditTextLayout.error = getString(R.string.field_no_empty)
                }
            }
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            var isValid = true

            if (email.isEmpty()) {
                isValid = false
                binding.emailEditTextLayout.error = getString(R.string.field_no_empty)
            }

            when {
                password.isEmpty() -> {
                    isValid = false
                    binding.passwordEditTextLayout.error = getString(R.string.field_no_empty)
                }
                binding.passwordEditText.error != null -> {
                    isValid = false
                }
            }

            if (isValid) {
                loginViewModel.login(email, password)
            }
        }
    }

    private fun showLoading(isLoading : Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun intToDpConverter(padding: Int): Int {
        val scale = resources.displayMetrics.density
        return (padding * scale + 0.5f).toInt()
    }
}