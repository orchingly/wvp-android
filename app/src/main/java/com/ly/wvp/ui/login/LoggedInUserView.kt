package com.ly.wvp.ui.login

import com.ly.wvp.auth.ResponseBody

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val userInfo: ResponseBody
    //... other data fields that may be accessible to the UI
)