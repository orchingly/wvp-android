package com.ly.wvp.data.model

/**
 * {
 *     "code": 0,
 *     "data": {
 *         "app": "rtp",
 *         "flv": "http://192.168.133.176:8091/rtp/43000000801320000009_43000000801320000009.live.flv",
 *         "fmp4": "http://192.168.133.176:8091/rtp/43000000801320000009_43000000801320000009.live.mp4",
 *         "hls": "http://192.168.133.176:8091/rtp/43000000801320000009_43000000801320000009/hls.m3u8",
 *         "https_flv": "https://192.168.133.176:5443/rtp/43000000801320000009_43000000801320000009.live.flv",
 *         "https_fmp4": "https://192.168.133.176:5443/rtp/43000000801320000009_43000000801320000009.live.mp4",
 *         "https_hls": "https://192.168.133.176:5443/rtp/43000000801320000009_43000000801320000009/hls.m3u8",
 *         "https_ts": "https://192.168.133.176:5443/rtp/43000000801320000009_43000000801320000009.live.ts",
 *         "mediaServerId": "FQ3TF8yT83wh5Wvz",
 *         "progress": 0.0,
 *         "rtc": "http://192.168.133.176:8091/index/api/webrtc?app=rtp&stream=43000000801320000009_43000000801320000009&type=play",
 *         "rtcs": "https://192.168.133.176:5443/index/api/webrtc?app=rtp&stream=43000000801320000009_43000000801320000009&type=play",
 *         "rtmp": "rtmp://192.168.133.176:1935/rtp/43000000801320000009_43000000801320000009",
 *         "rtsp": "rtsp://192.168.133.176:554/rtp/43000000801320000009_43000000801320000009",
 *         "stream": "43000000801320000009_43000000801320000009",
 *         "tracks": [
 *             {
 *                 "channels": 1,
 *                 "codecId": 0,
 *                 "codecType": 0,
 *                 "fps": 0,
 *                 "height": 0,
 *                 "ready": true,
 *                 "sampleBit": 0,
 *                 "sampleRate": 0,
 *                 "width": 0
 *             },
 *             {
 *                 "channels": 0,
 *                 "codecId": 0,
 *                 "codecType": 0,
 *                 "fps": 18,
 *                 "height": 1080,
 *                 "ready": true,
 *                 "sampleBit": 0,
 *                 "sampleRate": 0,
 *                 "width": 1920
 *             }
 *         ],
 *         "ts": "http://192.168.133.176:8091/rtp/43000000801320000009_43000000801320000009.live.ts",
 *         "ws_flv": "ws://192.168.133.176:8091/rtp/43000000801320000009_43000000801320000009.live.flv",
 *         "ws_fmp4": "ws://192.168.133.176:8091/rtp/43000000801320000009_43000000801320000009.live.mp4",
 *         "ws_hls": "ws://192.168.133.176:8091/rtp/43000000801320000009_43000000801320000009/hls.m3u8",
 *         "ws_ts": "ws://192.168.133.176:8091/rtp/43000000801320000009_43000000801320000009.live.ts",
 *         "wss_flv": "wss://192.168.133.176:5443/rtp/43000000801320000009_43000000801320000009.live.flv",
 *         "wss_fmp4": "wss://192.168.133.176:5443/rtp/43000000801320000009_43000000801320000009.live.mp4",
 *         "wss_hls": "wss://192.168.133.176:5443/rtp/43000000801320000009_43000000801320000009/hls.m3u8"
 *     },
 *     "msg": "鎴愬姛"
 * }
 */
class StreamContent {

    /**
     * 应用名
     */
    private var app: String? = null

    /**
     * 流ID
     */
    private var stream: String? = null

    /**
     * IP
     */
    private var ip: String? = null

    /**
     * HTTP-FLV流地址
     */
    private var flv: String? = null

    /**
     * HTTPS-FLV流地址
     */
    private var https_flv: String? = null

    /**
     * Websocket-FLV流地址
     */
    private var ws_flv: String? = null

    /**
     * Websockets-FLV流地址
     */
    private var wss_flv: String? = null

    /**
     * HTTP-FMP4流地址
     */
    private var fmp4: String? = null

    /**
     * HTTPS-FMP4流地址
     */
    private var https_fmp4: String? = null

    /**
     * Websocket-FMP4流地址
     */
    private var ws_fmp4: String? = null

    /**
     * Websockets-FMP4流地址
     */
    private var wss_fmp4: String? = null

    /**
     * HLS流地址
     */
    private var hls: String? = null

    /**
     * HTTPS-HLS流地址
     */
    private var https_hls: String? = null

    /**
     * Websocket-HLS流地址
     */
    private var ws_hls: String? = null

    /**
     * Websockets-HLS流地址
     */
    private var wss_hls: String? = null

    /**
     * HTTP-TS流地址
     */
    private var ts: String? = null

    /**
     * HTTPS-TS流地址
     */
    private var https_ts: String? = null

    /**
     * Websocket-TS流地址
     */
    private var ws_ts: String? = null

    /**
     * Websockets-TS流地址
     */
    private var wss_ts: String? = null

    /**
     * RTMP流地址
     */
    private var rtmp: String? = null

    /**
     * RTMPS流地址
     */
    private var rtmps: String? = null

    /**
     * RTSP流地址
     */
    private var rtsp: String? = null

    /**
     * RTSPS流地址
     */
    private var rtsps: String? = null

    /**
     * RTC流地址
     */
    private var rtc: String? = null

    /**
     * RTCS流地址
     */
    private var rtcs: String? = null

    /**
     * 流媒体ID
     */
    private var mediaServerId: String? = null

    /**
     * 流编码信息
     */
    private var tracks: Any? = null

    /**
     * 开始时间
     */
    private var startTime: String? = null

    /**
     * 结束时间
     */
    private var endTime: String? = null

    private var progress = 0.0

    fun getApp(): String? {
        return app
    }

    fun setApp(app: String?) {
        this.app = app
    }

    fun getStream(): String? {
        return stream
    }

    fun setStream(stream: String?) {
        this.stream = stream
    }

    fun getIp(): String? {
        return ip
    }

    fun setIp(ip: String?) {
        this.ip = ip
    }

    fun getFlv(): String? {
        return flv
    }

    fun setFlv(flv: String?) {
        this.flv = flv
    }

    fun getHttps_flv(): String? {
        return https_flv
    }

    fun setHttps_flv(https_flv: String?) {
        this.https_flv = https_flv
    }

    fun getWs_flv(): String? {
        return ws_flv
    }

    fun setWs_flv(ws_flv: String?) {
        this.ws_flv = ws_flv
    }

    fun getWss_flv(): String? {
        return wss_flv
    }

    fun setWss_flv(wss_flv: String?) {
        this.wss_flv = wss_flv
    }

    fun getFmp4(): String? {
        return fmp4
    }

    fun setFmp4(fmp4: String?) {
        this.fmp4 = fmp4
    }

    fun getHttps_fmp4(): String? {
        return https_fmp4
    }

    fun setHttps_fmp4(https_fmp4: String?) {
        this.https_fmp4 = https_fmp4
    }

     fun getWs_fmp4(): String? {
        return ws_fmp4
    }

     fun setWs_fmp4(ws_fmp4: String?) {
        this.ws_fmp4 = ws_fmp4
    }

     fun getWss_fmp4(): String? {
        return wss_fmp4
    }

     fun setWss_fmp4(wss_fmp4: String?) {
        this.wss_fmp4 = wss_fmp4
    }

     fun getHls(): String? {
        return hls
    }

     fun setHls(hls: String?) {
        this.hls = hls
    }

     fun getHttps_hls(): String? {
        return https_hls
    }

     fun setHttps_hls(https_hls: String?) {
        this.https_hls = https_hls
    }

     fun getWs_hls(): String? {
        return ws_hls
    }

     fun setWs_hls(ws_hls: String?) {
        this.ws_hls = ws_hls
    }

     fun getWss_hls(): String? {
        return wss_hls
    }

     fun setWss_hls(wss_hls: String?) {
        this.wss_hls = wss_hls
    }

     fun getTs(): String? {
        return ts
    }

     fun setTs(ts: String?) {
        this.ts = ts
    }

     fun getHttps_ts(): String? {
        return https_ts
    }

     fun setHttps_ts(https_ts: String?) {
        this.https_ts = https_ts
    }

     fun getWs_ts(): String? {
        return ws_ts
    }

     fun setWs_ts(ws_ts: String?) {
        this.ws_ts = ws_ts
    }

     fun getWss_ts(): String? {
        return wss_ts
    }

     fun setWss_ts(wss_ts: String?) {
        this.wss_ts = wss_ts
    }

     fun getRtmp(): String? {
        return rtmp
    }

     fun setRtmp(rtmp: String?) {
        this.rtmp = rtmp
    }

     fun getRtmps(): String? {
        return rtmps
    }

     fun setRtmps(rtmps: String?) {
        this.rtmps = rtmps
    }

     fun getRtsp(): String? {
        return rtsp
    }

     fun setRtsp(rtsp: String?) {
        this.rtsp = rtsp
    }

     fun getRtsps(): String? {
        return rtsps
    }

     fun setRtsps(rtsps: String?) {
        this.rtsps = rtsps
    }

     fun getRtc(): String? {
        return rtc
    }

     fun setRtc(rtc: String?) {
        this.rtc = rtc
    }

     fun getRtcs(): String? {
        return rtcs
    }

     fun setRtcs(rtcs: String?) {
        this.rtcs = rtcs
    }

     fun getMediaServerId(): String? {
        return mediaServerId
    }

     fun setMediaServerId(mediaServerId: String?) {
        this.mediaServerId = mediaServerId
    }

     fun getTracks(): Any? {
        return tracks
    }

     fun setTracks(tracks: Any?) {
        this.tracks = tracks
    }

     fun getStartTime(): String? {
        return startTime
    }

     fun setStartTime(startTime: String?) {
        this.startTime = startTime
    }

     fun getEndTime(): String? {
        return endTime
    }

     fun setEndTime(endTime: String?) {
        this.endTime = endTime
    }

     fun getProgress(): Double {
        return progress
    }

     fun setProgress(progress: Double) {
        this.progress = progress
    }
}