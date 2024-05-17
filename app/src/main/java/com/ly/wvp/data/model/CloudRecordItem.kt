package com.ly.wvp.data.model

/**
 * 云端录像数据
 */
class CloudRecordItem {
    /**
     * 主键
     */
    var id = 0

    /**
     * 应用名
     */
    var app: String? = null

    /**
     * 流
     */
    var stream: String? = null

    /**
     * 健全ID
     */
    var callId: String? = null

    /**
     * 开始时间
     */
    var startTime: Long = 0

    /**
     * 结束时间
     */
    var endTime: Long = 0

    /**
     * ZLM Id
     */
    var mediaServerId: String? = null

    /**
     * 文件名称
     */
    var fileName: String? = null

    /**
     * 文件路径
     */
    var filePath: String? = null

    /**
     * 文件夹
     */
    var folder: String? = null

    /**
     * 收藏，收藏的文件不移除
     */
    var collect: Boolean? = null

    /**
     * 保留，收藏的文件不移除
     */
    var reserve: Boolean? = null

    /**
     * 文件大小
     */
    var fileSize: Long = 0

    /**
     * 文件时长
     */
    var timeLen: Long = 0

}
