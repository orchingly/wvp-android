package com.ly.wvp.data.storage

data class SettingsConfig(val ip: String, val port: Int, val enableTls: Boolean,
                          var rememberPasswd: Boolean = false,
                          var passwd: String = "",
                          var user: String = "",
                          var sessionToken: String = "")
