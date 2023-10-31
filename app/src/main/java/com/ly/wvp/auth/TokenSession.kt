package com.ly.wvp.auth



object TokenSession {

    const val EMPTY_TOKEN = ""
    const val TOKEN_HEADER = "access-token"

    private var mTokenCache: String = EMPTY_TOKEN

    fun setToken(token: String){
        mTokenCache = token
    }

    fun getToken(): String{
        return mTokenCache
    }
}