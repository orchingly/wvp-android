package com.ly.wvp.auth

class ServerUrl {
    companion object{
        const val HTTP = "http"
        const val HTTPS = "https"
        const val HOST = "192.168.133.176"
        const val PORT = 18080
        const val API = "api"
        const val USER = "user"
        const val LOGIN = "login"
        const val PARAM_KEY_USER_NAME = "username"
        const val PARAM_KEY_PASSWD = "password"


        const val API_DEVICE = "api/v1/device"

        const val API_DEVICE_QUERY = "api/device/query"
        const val QUERY_DEVICES = "devices"
        const val QUERY_CHANNELS = "channels"

        //分页
        const val PARAM_KEY_PAGE = "page"
        const val PARAM_KEY_COUNT = "count"

        const val API_DEVICE_CHANEL_LIST = "channellist"

        //点播
        const val API_PLAY = "api/play"
        const val START = "start"

        //流媒体
        const val API_SERVER = "api/server"
        const val MEDIA_ONLINE = "media_server/online/list"

        const val CLOUD_PROXY = "record_proxy"
        const val API_RECORD = "api/record"
        const val LIST = "list"
        const val DATE = "date"
        const val FILE = "file"

        const val START_TIME = "startTime"
        const val END_TIME = "endTime"

        //录像事件
        const val RECORD_ACTION = "api/record/action"

        //快照
        const val SNAP = "snap"
    }
}