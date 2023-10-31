package com.ly.wvp.auth

/**
 *
 */
data class NetError(private val code:Int, private val cause: String){
    companion object{

        /**
         * 正常
         */
        const val OK = 200

        /**
         * 用户验证失败或未登录
         */
        const val AUTH_FAILED = 401

        /**
         * 应用错误
         */
        const val APP_ERROR = 501

        /**
         * JSON解析错误
         */
        const val JSON_ERROR = 502

        /**
         * 捕获的其他异常
         */
        const val OTHER_EXCEPTION = 503

        /**
         * 服务器网络不可用
         */
        const val INTERNET_INVALID = 504
    }
    fun getCode() = code

    fun getReason() = cause

    override fun toString(): String {
        return "$code: $cause"
    }
}
