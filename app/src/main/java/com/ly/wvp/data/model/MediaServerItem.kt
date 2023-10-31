package com.ly.wvp.data.model


/**
 * 流媒体服务信息
 * {
 *     "code": 0,
 *     "data": [
 *         {
 *             "autoConfig": true,
 *             "createTime": "2023-10-09 09:24:57",
 *             "currentPort": 0,
 *             "defaultServer": true,
 *             "hookAliveInterval": 10.0,
 *             "hookIp": "192.168.133.176",
 *             "httpPort": 8091,
 *             "httpSSlPort": 5443,
 *             "id": "FQ3TF8yT83wh5Wvz",
 *             "ip": "192.168.133.176",
 *             "recordAssistPort": 18081,
 *             "rtmpPort": 1935,
 *             "rtmpSSlPort": 0,
 *             "rtpEnable": true,
 *             "rtpPortRange": "30000,30500",
 *             "rtpProxyPort": 10000,
 *             "rtspPort": 554,
 *             "rtspSSLPort": 0,
 *             "sdpIp": "192.168.133.176",
 *             "secret": "035c73f7-bb6b-3241-a715-d9eb2d1996cc",
 *             "sendRtpPortRange": "30000,30500",
 *             "status": true,
 *             "streamIp": "192.168.133.176",
 *             "updateTime": "2023-10-09 09:24:57"
 *         }
 *     ],
 *     "msg": "鎴愬姛"
 * }
 */
class MediaServerItem {
    /**
     * ID
     */
    var id: String? = null

    /**
     * IP
     */
    var ip: String? = null

    /**
     * hook使用的IP（zlm访问WVP使用的IP）
     */
    var hookIp: String? = null

    /**
     * SDP IP
     */
    var sdpIp: String? = null

    /**
     * 流IP
     */
    var streamIp: String? = null

    /**
     * HTTP端口
     */
    var httpPort = 0

    /**
     * HTTPS端口
     */
    var httpSSlPort = 0

    /**
     * RTMP端口
     */
    var rtmpPort = 0

    /**
     * RTMPS端口
     */
    var rtmpSSlPort = 0

    /**
     * RTP收流端口（单端口模式有用）
     */
    var rtpProxyPort = 0

    /**
     * RTSP端口
     */
    var rtspPort = 0

    /**
     * RTSPS端口
     */
    var rtspSSLPort = 0

    /**
     * 是否开启自动配置ZLM
     */
    var autoConfig = false

    /**
     * ZLM鉴权参数
     */
    var secret: String? = null

    /**
     * keepalive hook触发间隔,单位秒
     */
    var hookAliveInterval: Float? = null

    /**
     * 是否使用多端口模式
     */
    var rtpEnable = false

    /**
     * 状态
     */
    var status = false

    /**
     * 多端口RTP收流端口范围
     */
    var rtpPortRange: String? = null

    /**
     * RTP发流端口范围
     */
    var sendRtpPortRange: String? = null

    /**
     * assist服务端口
     */
    var recordAssistPort = 0

    /**
     * 创建时间
     */
    var createTime: String? = null

    /**
     * 更新时间
     */
    var updateTime: String? = null

    /**
     * 上次心跳时间
     */
    var lastKeepaliveTime: String? = null

    /**
     * 是否是默认ZLM
     *
     */
    var defaultServer = false

    /**
     * 当前使用到的端口
     */
    var currentPort = 0

//    fun getId(): String? {
//        return id
//    }
//
//    fun setId(id: String?) {
//        this.id = id
//    }
//
//    fun getIp(): String? {
//        return ip
//    }
//
//    fun setIp(ip: String?) {
//        this.ip = ip
//    }
//
//    fun getHookIp(): String? {
//        return hookIp
//    }
//
//    fun setHookIp(hookIp: String?) {
//        this.hookIp = hookIp
//    }
//
//    fun getSdpIp(): String? {
//        return sdpIp
//    }
//
//    fun setSdpIp(sdpIp: String?) {
//        this.sdpIp = sdpIp
//    }
//
//    fun getStreamIp(): String? {
//        return streamIp
//    }
//
//    fun setStreamIp(streamIp: String?) {
//        this.streamIp = streamIp
//    }
//
//    fun getHttpPort(): Int {
//        return httpPort
//    }
//
//    fun setHttpPort(httpPort: Int) {
//        this.httpPort = httpPort
//    }
//
//    fun getHttpSSlPort(): Int {
//        return httpSSlPort
//    }
//
//    fun setHttpSSlPort(httpSSlPort: Int) {
//        this.httpSSlPort = httpSSlPort
//    }
//
//    fun getRtmpPort(): Int {
//        return rtmpPort
//    }
//
//    fun setRtmpPort(rtmpPort: Int) {
//        this.rtmpPort = rtmpPort
//    }
//
//    fun getRtmpSSlPort(): Int {
//        return rtmpSSlPort
//    }
//
//    fun setRtmpSSlPort(rtmpSSlPort: Int) {
//        this.rtmpSSlPort = rtmpSSlPort
//    }
//
//    fun getRtpProxyPort(): Int {
//        return rtpProxyPort
//    }
//
//    fun setRtpProxyPort(rtpProxyPort: Int) {
//        this.rtpProxyPort = rtpProxyPort
//    }
//
//    fun getRtspPort(): Int {
//        return rtspPort
//    }
//
//    fun setRtspPort(rtspPort: Int) {
//        this.rtspPort = rtspPort
//    }
//
//    fun getRtspSSLPort(): Int {
//        return rtspSSLPort
//    }
//
//    fun setRtspSSLPort(rtspSSLPort: Int) {
//        this.rtspSSLPort = rtspSSLPort
//    }
//
//    fun isAutoConfig(): Boolean {
//        return autoConfig
//    }
//
//    fun setAutoConfig(autoConfig: Boolean) {
//        this.autoConfig = autoConfig
//    }
//
//    fun getSecret(): String? {
//        return secret
//    }
//
//    fun setSecret(secret: String?) {
//        this.secret = secret
//    }
//
//    fun isRtpEnable(): Boolean {
//        return rtpEnable
//    }
//
//    fun setRtpEnable(rtpEnable: Boolean) {
//        this.rtpEnable = rtpEnable
//    }
//
//    fun getRtpPortRange(): String? {
//        return rtpPortRange
//    }
//
//    fun setRtpPortRange(rtpPortRange: String?) {
//        this.rtpPortRange = rtpPortRange
//    }
//
//    fun getRecordAssistPort(): Int {
//        return recordAssistPort
//    }
//
//    fun setRecordAssistPort(recordAssistPort: Int) {
//        this.recordAssistPort = recordAssistPort
//    }
//
//    fun isDefaultServer(): Boolean {
//        return defaultServer
//    }
//
//    fun setDefaultServer(defaultServer: Boolean) {
//        this.defaultServer = defaultServer
//    }
//
//    fun getCreateTime(): String? {
//        return createTime
//    }
//
//    fun setCreateTime(createTime: String?) {
//        this.createTime = createTime
//    }
//
//    fun getUpdateTime(): String? {
//        return updateTime
//    }
//
//    fun setUpdateTime(updateTime: String?) {
//        this.updateTime = updateTime
//    }
//
//    fun getCurrentPort(): Int {
//        return currentPort
//    }
//
//    fun setCurrentPort(currentPort: Int) {
//        this.currentPort = currentPort
//    }
//
//    fun isStatus(): Boolean {
//        return status
//    }
//
//    fun setStatus(status: Boolean) {
//        this.status = status
//    }
//
//    fun getLastKeepaliveTime(): String? {
//        return lastKeepaliveTime
//    }
//
//    fun setLastKeepaliveTime(lastKeepaliveTime: String?) {
//        this.lastKeepaliveTime = lastKeepaliveTime
//    }
//
//    fun getHookAliveInterval(): Float? {
//        return hookAliveInterval
//    }
//
//    fun setHookAliveInterval(hookAliveInterval: Float?) {
//        this.hookAliveInterval = hookAliveInterval
//    }
//
//    fun getSendRtpPortRange(): String? {
//        return sendRtpPortRange
//    }
//
//    fun setSendRtpPortRange(sendRtpPortRange: String?) {
//        this.sendRtpPortRange = sendRtpPortRange
//    }
}
