package com.ly.wvp.data.model

/**
 * 分页查询通道
 * url: http://localhost:18080/api/device/query/devices/43000000801320000008/channels?page=1&count=15
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
 *                 "PTZType": 0,
 *                 "PTZTypeText": "鏈煡",
 *                 "address": "wuhan",
 *                 "block": "435200",
 *                 "certifiable": 0,
 *                 "channelId": "43000000801310000008",
 *                 "channelType": 0,
 *                 "civilCode": "435200",
 *                 "createTime": "2023-08-23 10:09:24",
 *                 "deviceId": "43000000801320000008",
 *                 "errCode": 0,
 *                 "gpsTime": "2023-09-12 09:40:12",
 *                 "hasAudio": false,
 *                 "id": 1,
 *                 "latitude": 30.35,
 *                 "latitudeGcj02": 30.347570936984795,
 *                 "latitudeWgs84": 30.35,
 *                 "longitude": 114.33,
 *                 "longitudeGcj02": 114.33546195325007,
 *                 "longitudeWgs84": 114.33,
 *                 "manufacture": "shiyuetech",
 *                 "model": "RealGBD_V2.1.1",
 *                 "name": "sydevice",
 *                 "owner": "RealGBD_V2",
 *                 "parentId": "43000000801320000008",
 *                 "parental": 0,
 *                 "port": 0,
 *                 "registerWay": 1,
 *                 "safetyWay": 0,
 *                 "status": true,
 *                 "subCount": 0,
 *                 "updateTime": "2023-09-12 09:40:12"
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
class DeviceChanel(){
    /**
     * 数据库自增ID
     */
    private var id = 0

    /**
     * 通道国标编号
     */
    private var channelId: String? = null

    /**
     * 设备国标编号
     */
    private var deviceId: String? = null

    /**
     * 通道名
     */
    private var name: String? = null

    /**
     * 生产厂商
     */
    private var manufacture: String? = null

    /**
     * 型号
     */
    private var model: String? = null

    /**
     * 设备归属
     */
    private var owner: String? = null

    /**
     * 行政区域
     */
    private var civilCode: String? = null

    /**
     * 警区
     */
    private var block: String? = null

    /**
     * 安装地址
     */
    private var address: String? = null

    /**
     * 是否有子设备 1有, 0没有
     */
    private var parental = 0

    /**
     * 父级id
     */
    private var parentId: String? = null

    /**
     * 信令安全模式  缺省为0; 0:不采用; 2: S/MIME签名方式; 3: S/ MIME加密签名同时采用方式; 4:数字摘要方式
     */
    private var safetyWay = 0

    /**
     * 注册方式 缺省为1;1:符合IETFRFC3261标准的认证注册模 式; 2:基于口令的双向认证注册模式; 3:基于数字证书的双向认证注册模式
     */
    private var registerWay = 0

    /**
     * 证书序列号
     */
    private var certNum: String? = null

    /**
     * 证书有效标识 缺省为0;证书有效标识:0:无效1: 有效
     */
    private var certifiable = 0

    /**
     * 证书无效原因码
     */
    private var errCode = 0

    /**
     * 证书终止有效期
     */
    private var endTime: String? = null

    /**
     * 保密属性 缺省为0; 0:不涉密, 1:涉密
     */
    private var secrecy: String? = null

    /**
     * IP地址
     */
    private var ipAddress: String? = null

    /**
     * 端口号
     */
    private var port = 0

    /**
     * 密码
     */
    private var password: String? = null

    /**
     * 云台类型
     */
    private var PTZType = 0

    /**
     * 云台类型描述字符串
     */
    private var PTZTypeText: String? = null

    /**
     * 创建时间
     */
    private var createTime: String? = null

    /**
     * 更新时间
     */
    private var updateTime: String? = null

    /**
     * 在线/离线
     * 1在线,0离线
     * 默认在线
     * 信令:
     * <Status>ON</Status>
     * <Status>OFF</Status>
     * 遇到过NVR下的IPC下发信令可以推流， 但是 Status 响应 OFF
     */
    private var status = false

    /**
     * 经度
     */
    private var longitude = 0.0

    /**
     * 纬度
     */
    private var latitude = 0.0

    /**
     * 经度 GCJ02
     */
    private var longitudeGcj02 = 0.0

    /**
     * 纬度 GCJ02
     */
    private var latitudeGcj02 = 0.0

    /**
     * 经度 WGS84
     */
    private var longitudeWgs84 = 0.0

    /**
     * 纬度 WGS84
     */
    private var latitudeWgs84 = 0.0

    /**
     * 子设备数
     */
    private var subCount = 0

    /**
     * 流唯一编号，存在表示正在直播
     */
    private var streamId: String? = null

    /**
     * 是否含有音频
     */
    private var hasAudio = false

    /**
     * 标记通道的类型，0->国标通道 1->直播流通道 2->业务分组/虚拟组织/行政区划
     */
    private var channelType = 0

    /**
     * 业务分组
     */
    private var businessGroupId: String? = null

    /**
     * GPS的更新时间
     */
    private var gpsTime: String? = null

    open fun getId(): Int {
        return id
    }

    open fun setId(id: Int) {
        this.id = id
    }

    open fun getDeviceId(): String? {
        return deviceId
    }

    open fun setDeviceId(deviceId: String?) {
        this.deviceId = deviceId
    }

    open fun setPTZType(PTZType: Int) {
        this.PTZType = PTZType
        when (PTZType) {
            0 -> PTZTypeText = "未知"
            1 -> PTZTypeText = "球机"
            2 -> PTZTypeText = "半球"
            3 -> PTZTypeText = "固定枪机"
            4 -> PTZTypeText = "遥控枪机"
        }
    }

    open fun getChannelId(): String? {
        return channelId
    }

    open fun setChannelId(channelId: String?) {
        this.channelId = channelId
    }

    open fun getName(): String? {
        return name
    }

    open fun setName(name: String?) {
        this.name = name
    }

    open fun getManufacture(): String? {
        return manufacture
    }

    open fun setManufacture(manufacture: String?) {
        this.manufacture = manufacture
    }

    open fun getModel(): String? {
        return model
    }

    open fun setModel(model: String?) {
        this.model = model
    }

    open fun getOwner(): String? {
        return owner
    }

    open fun setOwner(owner: String?) {
        this.owner = owner
    }

    open fun getCivilCode(): String? {
        return civilCode
    }

    open fun setCivilCode(civilCode: String?) {
        this.civilCode = civilCode
    }

    open fun getBlock(): String? {
        return block
    }

    open fun setBlock(block: String?) {
        this.block = block
    }

    open fun getAddress(): String? {
        return address
    }

    open fun setAddress(address: String?) {
        this.address = address
    }

    open fun getParental(): Int {
        return parental
    }

    open fun setParental(parental: Int) {
        this.parental = parental
    }

    open fun getParentId(): String? {
        return parentId
    }

    open fun setParentId(parentId: String?) {
        this.parentId = parentId
    }

    open fun getSafetyWay(): Int {
        return safetyWay
    }

    open fun setSafetyWay(safetyWay: Int) {
        this.safetyWay = safetyWay
    }

    open fun getRegisterWay(): Int {
        return registerWay
    }

    open fun setRegisterWay(registerWay: Int) {
        this.registerWay = registerWay
    }

    open fun getCertNum(): String? {
        return certNum
    }

    open fun setCertNum(certNum: String?) {
        this.certNum = certNum
    }

    open fun getCertifiable(): Int {
        return certifiable
    }

    open fun setCertifiable(certifiable: Int) {
        this.certifiable = certifiable
    }

    open fun getErrCode(): Int {
        return errCode
    }

    open fun setErrCode(errCode: Int) {
        this.errCode = errCode
    }

    open fun getEndTime(): String? {
        return endTime
    }

    open fun setEndTime(endTime: String?) {
        this.endTime = endTime
    }

    open fun getSecrecy(): String? {
        return secrecy
    }

    open fun setSecrecy(secrecy: String?) {
        this.secrecy = secrecy
    }

    open fun getIpAddress(): String? {
        return ipAddress
    }

    open fun setIpAddress(ipAddress: String?) {
        this.ipAddress = ipAddress
    }

    open fun getPort(): Int {
        return port
    }

    open fun setPort(port: Int) {
        this.port = port
    }

    open fun getPassword(): String? {
        return password
    }

    open fun setPassword(password: String?) {
        this.password = password
    }

    open fun getPTZType(): Int {
        return PTZType
    }

    open fun getPTZTypeText(): String? {
        return PTZTypeText
    }

    open fun setPTZTypeText(PTZTypeText: String?) {
        this.PTZTypeText = PTZTypeText
    }

    open fun isStatus(): Boolean {
        return status
    }

    open fun setStatus(status: Boolean) {
        this.status = status
    }

    open fun getLongitude(): Double {
        return longitude
    }

    open fun setLongitude(longitude: Double) {
        this.longitude = longitude
    }

    open fun getLatitude(): Double {
        return latitude
    }

    open fun setLatitude(latitude: Double) {
        this.latitude = latitude
    }

    open fun getLongitudeGcj02(): Double {
        return longitudeGcj02
    }

    open fun setLongitudeGcj02(longitudeGcj02: Double) {
        this.longitudeGcj02 = longitudeGcj02
    }

    open fun getLatitudeGcj02(): Double {
        return latitudeGcj02
    }

    open fun setLatitudeGcj02(latitudeGcj02: Double) {
        this.latitudeGcj02 = latitudeGcj02
    }

    open fun getLongitudeWgs84(): Double {
        return longitudeWgs84
    }

    open fun setLongitudeWgs84(longitudeWgs84: Double) {
        this.longitudeWgs84 = longitudeWgs84
    }

    open fun getLatitudeWgs84(): Double {
        return latitudeWgs84
    }

    open fun setLatitudeWgs84(latitudeWgs84: Double) {
        this.latitudeWgs84 = latitudeWgs84
    }

    open fun getSubCount(): Int {
        return subCount
    }

    open fun setSubCount(subCount: Int) {
        this.subCount = subCount
    }

    open fun isHasAudio(): Boolean {
        return hasAudio
    }

    open fun setHasAudio(hasAudio: Boolean) {
        this.hasAudio = hasAudio
    }

    open fun getStreamId(): String? {
        return streamId
    }

    open fun setStreamId(streamId: String?) {
        this.streamId = streamId
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

    open fun getChannelType(): Int {
        return channelType
    }

    open fun setChannelType(channelType: Int) {
        this.channelType = channelType
    }

    open fun getBusinessGroupId(): String? {
        return businessGroupId
    }

    open fun setBusinessGroupId(businessGroupId: String?) {
        this.businessGroupId = businessGroupId
    }

    open fun getGpsTime(): String? {
        return gpsTime
    }

    open fun setGpsTime(gpsTime: String?) {
        this.gpsTime = gpsTime
    }
}
