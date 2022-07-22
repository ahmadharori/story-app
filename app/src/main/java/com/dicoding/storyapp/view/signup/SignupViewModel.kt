package com.dicoding.storyapp.view.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.api.ApiConfig
import com.dicoding.storyapp.api.response.RegisterResponse
import com.dicoding.storyapp.model.UserPreference
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupViewModel(private val pref: UserPreference) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _response = MutableLiveData<RegisterResponse>()
    val response: LiveData<RegisterResponse> = _response

    private val _failure = MutableLiveData<Throwable>()
    val failure: LiveData<Throwable> = _failure

    fun signUp(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _response.value = response.body()
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Log.e(TAG, "error: ${jObjError.getBoolean("error")}")
                        Log.e(TAG, "message: ${jObjError.getString("message")}")
                        _response.value = RegisterResponse(jObjError.getBoolean("error"),
                            jObjError.getString("message"))
                    } catch (e: Exception) {

                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                _failure.value = t
            }
        })
    }

    companion object {
        private const val TAG = "SignupViewModel"
    }
}