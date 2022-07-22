package com.dicoding.storyapp.view.login

import androidx.lifecycle.*
import com.dicoding.storyapp.api.ApiConfig
import com.dicoding.storyapp.api.response.LoginResponse
import com.dicoding.storyapp.model.UserModel
import com.dicoding.storyapp.model.UserPreference
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference) : ViewModel(){

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _response = MutableLiveData<LoginResponse>()
    val response: LiveData<LoginResponse> = _response

    private val _failure = MutableLiveData<Throwable>()
    val failure: LiveData<Throwable> = _failure

    fun login(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _response.value = response.body()
                    val loginResult = _response.value?.loginResult
                    val user = loginResult?.name?.let { name ->
                        loginResult.token?.let { token ->
                        UserModel(name, token, true)
                    } }
                    viewModelScope.launch {
                        if (user != null) {
                            pref.login(user)
                        }
                    }
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        _response.value = LoginResponse(null,
                            jObjError.getBoolean("error"),
                            jObjError.getString("message"))
                    } catch (e: Exception) {

                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _failure.value = t;
            }
        })
    }
}