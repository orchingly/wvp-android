package com.ly.wvp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns


import com.ly.wvp.R
import com.ly.wvp.TAG
import com.ly.wvp.auth.HttpConnectionClient
import com.ly.wvp.auth.NetError
import com.ly.wvp.auth.NetError.Companion.APP_ERROR
import com.ly.wvp.auth.NetError.Companion.INTERNET_INVALID
import com.ly.wvp.auth.NetError.Companion.JSON_ERROR
import com.ly.wvp.auth.ResponseBody
import com.ly.wvp.auth.ServerUrl
import com.ly.wvp.auth.TokenSession
import com.ly.wvp.data.storage.SettingsConfig
import com.ly.wvp.util.Utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import okhttp3.Request
import org.json.JSONObject
import kotlin.Exception

class LoginViewModel() : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private var _netError = MutableLiveData<NetError>()

    fun getNetError(): LiveData<NetError> = _netError

    private var config: SettingsConfig? = null

    fun setConfig(config: SettingsConfig){
        this.config = config
    }

    fun login(username: String, password: String, useRememberPasswd: Boolean) {

        CoroutineScope(Dispatchers.IO).launch {
            val passwd = if (useRememberPasswd) password else Utils.md5Encoding(password)
            val httpUrl = HttpConnectionClient.buildPublicHeader(config!!)
                .addPathSegment(ServerUrl.API)
                .addPathSegment(ServerUrl.USER)
                .addPathSegment(ServerUrl.LOGIN)
                .addQueryParameter(ServerUrl.PARAM_KEY_USER_NAME, username)
                .addQueryParameter(ServerUrl.PARAM_KEY_PASSWD, passwd)
                .build()


            val request = Request.Builder()
                .url(httpUrl)
                .get()
                .build()

            try {
                HttpConnectionClient.request(request).run {
                    Log.d(TAG, "login:msg: ${this.message}, body:${this.body}")
                    if (this.isSuccessful){
                        this.body?.let {
                            val body = parseResponseBody(it.string())
                            body?.let {
                                when (body.code()){
                                    0 -> {
                                        TokenSession.setToken(body.accessToken())
                                        launch(Dispatchers.Main){
                                            _loginResult.value =
                                                LoginResult(success = LoggedInUserView(body))
                                        }
                                    }
                                    else ->{
                                        _netError.postValue(NetError(body.code(),  body.msg()))
                                    }
                                }
                                return@launch
                            }?: kotlin.run {
                                Log.e(TAG, "login: login internal failed")
                            }
                            _netError.postValue(NetError(JSON_ERROR,  "response body null, Json parse error"))
                        }
                    }
                    else{
                        _netError.postValue(NetError(APP_ERROR,  "${this.code}: ${this.message}"))
                    }
                }
            }
            catch (e: Exception){
                _netError.postValue(NetError(INTERNET_INVALID, e.message?: "OkHttp Exception"))
            }

        }
    }

    private fun parseResponseBody(json: String): ResponseBody?{
        return try {
            val responseBody = JSONObject(json)
            val code = responseBody.getInt(ResponseBody.CODE)
            var dataObj: JSONObject? = null
            if (code == 0){
                dataObj = responseBody.getJSONObject(ResponseBody.DATA)
                val token = dataObj.getString(ResponseBody.ACCESS_TOKEN)
                ResponseBody(code,
                    token,
                    responseBody.getString(ResponseBody.MSG))
            }
            else{
                ResponseBody(code,
                    TokenSession.EMPTY_TOKEN,
                    responseBody.getString(ResponseBody.MSG))
            }
        } catch (e: Exception){
            Log.e(TAG, "json parse error", e)
            null
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }
}