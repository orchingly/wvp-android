package com.ly.wvp.auth

import com.ly.wvp.data.storage.SettingsConfig
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit

object HttpConnectionClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .build()

    fun request(request: Request): Response{

        return client.newCall(request).execute()
    }


    fun buildPublicHeader(config: SettingsConfig): HttpUrl.Builder {
        val host = config.ip.ifEmpty {
            ServerUrl.HOST
        }
        val port = if (config.port > 0) {
            config.port
        } else {
            ServerUrl.PORT
        }

        val http = if (config.enableTls) ServerUrl.HTTPS else ServerUrl.HTTP

        if (TokenSession.getToken().isNotEmpty()) {
            return HttpUrl.Builder()
                .scheme(http)
                .host(host)
                .port(port)
                .addQueryParameter(TokenSession.TOKEN_HEADER, TokenSession.getToken())
        }
        else{
            return HttpUrl.Builder()
                .scheme(http)
                .host(host)
                .port(port)
        }
    }


    /**
     * 查询国标设备 deviceId
     * url:http://localhost:18080/api/device/query/devices/43000000801320000008
     * {
     *     "code": 0,
     *     "data": {
     *         "asMessageChannel": false,
     *         "channelCount": 1,
     *         "charset": "GB2312",
     *         "createTime": "2023-08-23 10:09:24",
     *         "deviceId": "43000000801320000008",
     *         "expires": 0,
     *         "firmware": "435200",
     *         "geoCoordSys": "WGS84",
     *         "hostAddress": "10.42.0.45:5860",
     *         "ip": "10.42.0.45",
     *         "keepaliveIntervalTime": 0,
     *         "keepaliveTime": "2023-09-18 17:20:13",
     *         "localIp": "192.168.133.175",
     *         "manufacturer": "shiyuetech",
     *         "mobilePositionSubmissionInterval": 5,
     *         "model": "RealGBD_V2.1.1",
     *         "onLine": false,
     *         "port": 5860,
     *         "registerTime": "2023-09-18 17:20:13",
     *         "ssrcCheck": false,
     *         "streamMode": "TCP-PASSIVE",
     *         "streamModeForParam": 1,
     *         "subscribeCycleForAlarm": 0,
     *         "subscribeCycleForCatalog": 0,
     *         "subscribeCycleForMobilePosition": 0,
     *         "switchPrimarySubStream": false,
     *         "transport": "UDP",
     *         "updateTime": "2023-09-18 17:20:13"
     *     },
     *     "msg": "鎴愬姛"
     * }
     */
    fun queryDevice(){

    }



}