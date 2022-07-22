package com.dicoding.storyapp.view.signup

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivitySignupBinding
import com.dicoding.storyapp.model.UserPreference
import com.dicoding.storyapp.view.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var signupViewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
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
        signupViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[SignupViewModel::class.java]

        signupViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        signupViewModel.response.observe(this) {
            if (it.error == false) {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.success))
                    setMessage(it.message)
                    setPositiveButton(getString(R.string.proceed)) { _, _ ->
                        finish()
                    }
                    create()
                    show()
                }
            } else {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.failed))
                    setMessage(it.message)
                    setNegativeButton(getString(R.string.close)) { _, _ ->
                    }
                    create()
                    show()
                }
            }
        }

        signupViewModel.failure.observe(this) { failure ->
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

        binding.nameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.nameEditTextLayout.error = null
            } else {
                if (binding.nameEditText.text.toString() == "") {
                    binding.nameEditTextLayout.error = getString(R.string.field_no_empty)
                } else {
                    binding.nameEditTextLayout.error = null
                }
            }
        }

        binding.emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.emailEditTextLayout.error = null
                binding.emailEditText.setPadding(0,0,32,0)
            } else {
                if (binding.emailEditText.text.toString() == "") {
                    binding.emailEditText.setPadding(0, 0, intToDpConverter(44), 0)
                    binding.emailEditTextLayout.error = getString(R.string.field_no_empty)
                } else {
                    binding.emailEditTextLayout.error = null
                    binding.emailEditText.setPadding(0,0,32,0)
                }
            }
        }

        binding.passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.passwordEditTextLayout.error = null
            } else {
                if (binding.passwordEditText.text.toString() == "") {
                    binding.passwordEditTextLayout.error = getString(R.string.field_no_empty)
                } else {
                    binding.passwordEditTextLayout.error = null
                }
            }
        }

        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            var isValid = true

            if (name.isEmpty()) {
                isValid = false
                binding.nameEditTextLayout.error = getString(R.string.field_no_empty)
            }

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
                signupViewModel.signUp(name, email, password)
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