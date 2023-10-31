package com.ly.wvp.data.model

/**
 * 分页查询国标设备
 * url:http://localhost:18080/api/device/query/devices?page=1&count=15
 * 结果:
 * {
 *     "code": 0,
 *     "data": {
 *         "endRow": 1,
 *         "hasNextPage": false,
 *         "hasPreviousPage": false,
 *         "isFirstPage": true,
 *         "isLastPage": true,
 *         "list": [
 *             {
 *                 "asMessageChannel": false,
 *                 "channelCount": 1,
 *                 "charset": "GB2312",
 *                 "createTime": "2023-08-23 10:09:24",
 *                 "deviceId": "43000000801320000008",
 *                 "expires": 0,
 *                 "firmware": "435200",
 *                 "geoCoordSys": "WGS84",
 *                 "hostAddress": "10.42.0.45:5860",
 *                 "ip": "10.42.0.45",
 *                 "keepaliveIntervalTime": 0,
 *                 "keepaliveTime": "2023-09-18 17:20:13",
 *                 "localIp": "192.168.133.175",
 *                 "manufacturer": "shiyuetech",
 *                 "mobilePositionSubmissionInterval": 5,
 *                 "model": "RealGBD_V2.1.1",
 *                 "onLine": false,
 *                 "port": 5860,
 *                 "registerTime": "2023-09-18 17:20:13",
 *                 "ssrcCheck": false,
 *                 "streamMode": "TCP-PASSIVE",
 *                 "streamModeForParam": 1,
 *                 "subscribeCycleForAlarm": 0,
 *                 "subscribeCycleForCatalog": 0,
 *                 "subscribeCycleForMobilePosition": 0,
 *                 "switchPrimarySubStream": false,
 *                 "transport": "UDP",
 *                 "updateTime": "2023-09-18 17:20:13"
 *             }
 *         ],
 *         "navigateFirstPage": 1,
 *         "navigateLastPage": 1,
 *         "navigatePages": 8,
 *         "navigatepageNums": [
 *             1
 *         ],
 *         "nextPage": 0,
 *         "pageNum": 1,
 *         "pageSize": 15,
 *         "pages": 1,
 *         "prePage": 0,
 *         "size": 1,
 *         "startRow": 1,
 *         "total": 1
 *     },
 *     "msg": "鎴愬姛"
 * }
 */
class Device{
    /**
     * 设备国标编号
     */
    private var deviceId: String? = null

    /**
     * 设备名
     */
    private var name: String? = null

    /**
     * 生产厂商
     */
    private var manufacturer: String? = null

    /**
     * 型号
     */
    private var model: String? = null

    /**
     * 固件版本
     */
    private var firmware: String? = null

    /**
     * 传输协议
     * UDP/TCP
     */
    private var transport: String? = null

    /**
     * 数据流传输模式
     * UDP:udp传输
     * TCP-ACTIVE：tcp主动模式
     * TCP-PASSIVE：tcp被动模式
     */
    private var streamMode: String? = null

    /**
     * wan地址_ip
     */
    private var ip: String? = null

    /**
     * wan地址_port
     */
    private var port = 0

    /**
     * wan地址
     */
    private var hostAddress: String? = null

    /**
     * 在线
     */
    private var onLine = false


    /**
     * 注册时间
     */
    private var registerTime: String? = null


    /**
     * 心跳时间
     */
    private var keepaliveTime: String? = null


    /**
     * 心跳间隔
     */
    private var keepaliveIntervalTime = 0

    /**
     * 通道个数
     */
    private var channelCount = 0

    /**
     * 注册有效期
     */
    private var expires = 0

    /**
     * 创建时间
     */
    private var createTime: String? = null

    /**
     * 更新时间
     */
    private var updateTime: String? = null

    /**
     * 设备使用的媒体id, 默认为null
     */
    private var mediaServerId: String? = null

    /**
     * 字符集, 支持 UTF-8 与 GB2312
     */
    private var charset: String? = null

    /**
     * 目录订阅周期，0为不订阅
     */
    private var subscribeCycleForCatalog = 0

    /**
     * 移动设备位置订阅周期，0为不订阅
     */
    private var subscribeCycleForMobilePosition = 0

    /**
     * 移动设备位置信息上报时间间隔,单位:秒,默认值5
     */
    private var mobilePositionSubmissionInterval = 5

    /**
     * 报警订阅周期，0为不订阅
     */
    private var subscribeCycleForAlarm = 0

    /**
     * 是否开启ssrc校验，默认关闭，开启可以防止串流
     */
    private var ssrcCheck = false

    /**
     * 地理坐标系， 目前支持 WGS84,GCJ02
     */
    private var geoCoordSys: String? = null

    private var password: String? = null

    /**
     * 收流IP
     */
    private var sdpIp: String? = null

    /**
     * SIP交互IP（设备访问平台的IP）
     */
    private var localIp: String? = null

    /**
     * 是否作为消息通道
     */
    private var asMessageChannel = false

    private var channelList: List<DeviceChanel>? = null

    fun setChannelList(channelList: List<DeviceChanel>){
        this.channelList = channelList
    }

    fun getChannelList(): List<DeviceChanel>?{
        return channelList
    }


    open fun getDeviceId(): String? {
        return deviceId
    }

    open fun setDeviceId(deviceId: String?) {
        this.deviceId = deviceId
    }

    open fun getName(): String? {
        return name
    }

    open fun setName(name: String?) {
        this.name = name
    }

    open fun getManufacturer(): String? {
        return manufacturer
    }

    open fun setManufacturer(manufacturer: String?) {
        this.manufacturer = manufacturer
    }

    open fun getModel(): String? {
        return model
    }

    open fun setModel(model: String?) {
        this.model = model
    }

    open fun getFirmware(): String? {
        return firmware
    }

    open fun setFirmware(firmware: String?) {
        this.firmware = firmware
    }

    open fun getTransport(): String? {
        return transport
    }

    open fun setTransport(transport: String?) {
        this.transport = transport
    }

    open fun getStreamMode(): String? {
        return streamMode
    }

    open fun getStreamModeForParam(): Int? {
        if (streamMode == null) {
            return 0
        }
        if (streamMode.equals("UDP", ignoreCase = true)) {
            return 0
        } else if (streamMode.equals("TCP-PASSIVE", ignoreCase = true)) {
            return 1
        } else if (streamMode.equals("TCP-ACTIVE", ignoreCase = true)) {
            return 2
        }
        return 0
    }

    open fun setStreamMode(streamMode: String?) {
        this.streamMode = streamMode
    }

    open fun getIp(): String? {
        return ip
    }

    open fun setIp(ip: String?) {
        this.ip = ip
    }

    open fun getPort(): Int {
        return port
    }

    open fun setPort(port: Int) {
        this.port = port
    }

    open fun getHostAddress(): String? {
        return hostAddress
    }

    open fun setHostAddress(hostAddress: String?) {
        this.hostAddress = hostAddress
    }

    open fun isOnLine(): Boolean {
        return onLine
    }

    open fun setOnLine(onLine: Boolean) {
        this.onLine = onLine
    }

    open fun getChannelCount(): Int {
        return channelCount
    }

    open fun setChannelCount(channelCount: Int) {
        this.channelCount = channelCount
    }

    open fun getRegisterTime(): String? {
        return registerTime
    }

    open fun setRegisterTime(registerTime: String?) {
        this.registerTime = registerTime
    }

    open fun getKeepaliveTime(): String? {
        return keepaliveTime
    }

    open fun setKeepaliveTime(keepaliveTime: String?) {
        this.keepaliveTime = keepaliveTime
    }

    open fun getExpires(): Int {
        return expires
    }

    open fun setExpires(expires: Int) {
        this.expires = expires
    }

    open fun getCreateTime(): String? {
        return createTime
    }

    open fun setCreateTime(createTime: String?) {
        this.createTime = createTime
    }

    open fun getUpdateTime(): String? {
        return updateTime
    }

    open fun setUpdateTime(updateTime: String?) {
        this.updateTime = updateTime
    }

    open fun getMediaServerId(): String? {
        return mediaServerId
    }

    open fun setMediaServerId(mediaServerId: String?) {
        this.mediaServerId = mediaServerId
    }

    open fun getCharset(): String? {
        return charset
    }

    open fun setCharset(charset: String?) {
        this.charset = charset
    }

    open fun getSubscribeCycleForCatalog(): Int {
        return subscribeCycleForCatalog
    }

    open fun setSubscribeCycleForCatalog(subscribeCycleForCatalog: Int) {
        this.subscribeCycleForCatalog = subscribeCycleForCatalog
    }

    open fun getSubscribeCycleForMobilePosition(): Int {
        return subscribeCycleForMobilePosition
    }

    open fun setSubscribeCycleForMobilePosition(subscribeCycleForMobilePosition: Int) {
        this.subscribeCycleForMobilePosition = subscribeCycleForMobilePosition
    }

    open fun getMobilePositionSubmissionInterval(): Int {
        return mobilePositionSubmissionInterval
    }

    open fun setMobilePositionSubmissionInterval(mobilePositionSubmissionInterval: Int) {
        this.mobilePositionSubmissionInterval = mobilePositionSubmissionInterval
    }

    open fun getSubscribeCycleForAlarm(): Int {
        return subscribeCycleForAlarm
    }

    open fun setSubscribeCycleForAlarm(subscribeCycleForAlarm: Int) {
        this.subscribeCycleForAlarm = subscribeCycleForAlarm
    }

    open fun isSsrcCheck(): Boolean {
        return ssrcCheck
    }

    open fun setSsrcCheck(ssrcCheck: Boolean) {
        this.ssrcCheck = ssrcCheck
    }

    open fun getGeoCoordSys(): String? {
        return geoCoordSys
    }

    open fun setGeoCoordSys(geoCoordSys: String?) {
        this.geoCoordSys = geoCoordSys
    }

    open fun getPassword(): String? {
        return password
    }

    open fun setPassword(password: String?) {
        this.password = password
    }

    open fun getSdpIp(): String? {
        return sdpIp
    }

    open fun setSdpIp(sdpIp: String?) {
        this.sdpIp = sdpIp
    }

    open fun getLocalIp(): String? {
        return localIp
    }

    open fun setLocalIp(localIp: String?) {
        this.localIp = localIp
    }

    open fun getKeepaliveIntervalTime(): Int {
        return keepaliveIntervalTime
    }

    open fun setKeepaliveIntervalTime(keepaliveIntervalTime: Int) {
        this.keepaliveIntervalTime = keepaliveIntervalTime
    }

    open fun isAsMessageChannel(): Boolean {
        return asMessageChannel
    }

    open fun setAsMessageChannel(asMessageChannel: Boolean) {
        this.asMessageChannel = asMessageChannel
    }

    /*======================设备主子码流逻辑START=========================*/
    /**
     * 开启主子码流切换的开关（false-不开启，true-开启）
     */
    private var switchPrimarySubStream = false

    open fun isSwitchPrimarySubStream(): Boolean {
        return switchPrimarySubStream
    }

    open fun setSwitchPrimarySubStream(switchPrimarySubStream: Boolean) {
        this.switchPrimarySubStream = switchPrimarySubStream
    }

    /*======================设备主子码流逻辑END=========================*/
}
