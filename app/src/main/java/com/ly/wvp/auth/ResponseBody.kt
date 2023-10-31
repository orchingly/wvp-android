package com.ly.wvp.auth

/**
 * ResponseBody from server eg:
 * {
 *     "code": 0,
 *     "data": {
 *         "accessToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6IjNlNzk2NDZjNGRiYzQwODM4M2E5ZWVkMDlmMmI4NWFlIn0.eyJqdGkiOiI3cVh0LXlySjlRdWtwaWZ4ZUxZVHpBIiwiaWF0IjoxNjk1MTA1NDg2LCJleHAiOjE2OTc2OTc0ODYsIm5iZiI6MTY5NTEwNTQ4Niwic3ViIjoibG9naW4iLCJhdWQiOiJBdWRpZW5jZSIsInVzZXJuYW1lIjoiYWRtaW4iLCJwYXNzd29yZCI6IjIxMjMyZjI5N2E1N2E1YTc0Mzg5NGEwZTRhODAxZmMzIiwicm9sZUlkIjoxfQ.ATAG2dF9RK62Cwutvd-nHO9rtKaAARRQjjEwWKhW3tajlX62o__o8yHz1mc-U1Okc7GuWFV2QC4Whgy7HqvXoZaSKTJvsAJ59R2VrVYwqfgN0jkr6U1-NWZ96FxI9w_DgHcyOPkCmPYRvL_aQIFP-BJioDwcZIIya4Nm6sKR5tAAJsKJKJJ6wZt8aT1sNEwEUC4wUAu8yfrpb9KoS3-pp7Apxwlgf1brJnDSFlzSKnsRkUzdHHLXCKCU3mrJnzpRf9jvv4lXXSkfqsuTzPya5ga8QHndF-GRrMpZ-b5idIBTcybS22x60AAx_zJzoCYQnzGCtUMyn3bliHd8lTGJkQ",
 *         "accountNonExpired": true,
 *         "accountNonLocked": true,
 *         "credentialsNonExpired": true,
 *         "enabled": true,
 *         "id": 1,
 *         "role": {
 *             "authority": "0",
 *             "createTime": "2021-04-13 14:14:57",
 *             "id": 1,
 *             "name": "admin",
 *             "updateTime": "2021-04-13 14:14:57"
 *         },
 *         "username": "admin"
 *     },
 *     "msg": "成功"
 * }
 */
class ResponseBody {
    companion object{
        const val CODE = "code"
        const val DATA = "data"
        const val LIST = "list"
        const val ACCESS_TOKEN = "accessToken"
        const val ACCOUNT_NON_EXPIRED = "accountNonExpired"
        const val ACCOUNT_NON_LOCKED = "accountNonLocked"
        const val CRE_NON_EXPIRED = "credentialsNonExpired"
        const val ENABLED = "enabled"
        const val ID = "id"
        const val MSG = "msg"
        //点播结果
        const val HLS = "hls"
        const val FLV = "flv"
        const val RTMP = "rtmp"
        const val APP = "app"
        const val HTTPS_FLV = "https_flv"
        const val HTTPS_HLS = "https_hls"
        const val RTMPS = "rtmps"

        //record
        const val STREAM = "stream"
        const val TIME = "time"

        const val STREAM_IP = "streamIp"
        const val PORT = "httpPort"
        const val HTTPS_PORT = "httpSSlPort"
        const val IP = "ip"
    }

    private var mCode: Int
    private lateinit var mAccessToken: String
    private lateinit var msg: String

    constructor(code: Int, token: String, msg: String){
        mCode = code
        mAccessToken = token
        this.msg = msg
    }

    fun code() = mCode

    fun accessToken() = mAccessToken

    fun msg() = msg

}