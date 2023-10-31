package com.ly.wvp.data.model

/**
 * 视频流检测结果信息
 *      * {"code":0,"data":[
 *      * {"DType":1,"actTime":"2023-09-18 11:00:37","actType":1,"app":"rtp","stream":"43000000801320000008_43000000801310000008"},
 *      * {"DType":1,"actTime":"2023-09-18 11:00:40","actType":0,"app":"rtp","stream":"43000000801320000008_43000000801310000008"},
 *      * {"DType":1,"actTime":"2023-09-18 11:00:41","actType":1,"app":"rtp","stream":"43000000801320000008_43000000801310000008"},
 *      * {"DType":1,"actTime":"2023-09-18 11:00:41","actType":0,"app":"rtp","stream":"43000000801320000008_43000000801310000008"}
 *      * ],"msg":"鎴愬姛"}
 */
class StreamDetectionItem {
    /**
     * 应用名
     */
    var app: String? = null

    /**
     * 流ID
     */
    var stream: String? = null

    /**
     * 检测类型
     */
    var dType = 0

    /**
     * 事件时间
     */
    var actTime: String? = null

    /**
     * 事件类型
     */
    var actType = 0

    override fun toString(): String {
        return "[$app, $stream, $dType, $actTime,$actType]"
    }

    companion object {
        /**移动, 出现 */
        const val ACT_START = 1

        /**停止, 消失 */
        const val ACT_STOP = 0

        /**
         * 移动
         */
        const val EVENT_MOVE = 1

        /**
         * 人形
         */
        const val EVENT_PERSON = 2

        const val DType = "DType"

        const val actTime = "actTime"

        const val actType = "actType"

        const val app = "app"

        const val stream = "stream"


    }
}