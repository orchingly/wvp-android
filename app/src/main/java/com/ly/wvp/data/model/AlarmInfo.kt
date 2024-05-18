package com.ly.wvp.data.model

/**
 * 报警信息
 *
 */
class AlarmInfo {

    /**
     * 应用名
     */
    var app: String? = null

    /**
     * 流ID
     */
    var stream: String? = null

    /**
     * 报警类型：EVENT_MOVE 运动检测
     */
    var alarmType = 0

    /**
     * 事件时间
     */
    var actTime: String? = null

    /**
     * 事件标记：0 事件结束，1,事件开始
     */
    var actFlag = 0

    override fun toString(): String {
        return "[$app, $stream, $alarmType, $actTime,$actFlag]"
    }

    companion object {

        /**
         * 报警触发时间点 往后持续的秒数
         * 摄像机报警事件指有事件起点，无结束点，需要自定义一个事件结束的时间点
         * 默认假设每个事件持续30s
         */
        const val ALARM_DURATION = 30

        /**报警事件开始 */
        const val ACT_START = 1

        /**报警事件结束*/
        const val ACT_STOP = 0

        /**
         * 移动
         */
        const val EVENT_MOVE = 1

        /**
         * 人形
         *
         */
        @Deprecated("摄像机报警无此类型")
        const val EVENT_PERSON = 2

        /**
         * 其他类型的报警
         */
        const val EVENT_OTHER = 3

        const val DType = "DType"

        const val actTime = "actTime"

        const val actType = "actType"

        const val app = "app"

        const val stream = "stream"

        fun copy(alarmInfo: AlarmInfo): AlarmInfo{
            val copy = AlarmInfo()
            copy.app = alarmInfo.app
            copy.stream = alarmInfo.stream
            copy.actTime = alarmInfo.actTime
            copy.actFlag = alarmInfo.actFlag
            copy.alarmType = alarmInfo.alarmType
            return copy
        }

    }
}