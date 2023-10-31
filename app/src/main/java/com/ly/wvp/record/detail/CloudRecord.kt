package com.ly.wvp.record.detail

import com.ly.wvp.calendar.Calendar
import com.ly.wvp.data.model.StreamDetectionItem
import java.time.Duration

/**
 * @param eventList 事件, 有人,移动
 * @see StreamDetectionItem.dType
 */
const val TIME_FORMAT_VERSION_1 = "HH:mm:ss"
const val TIME_FORMAT_VERSION_2 = "HHmmss"
data class CloudRecord(val app: String,
                       val stream: String,
                       val calendar: Calendar,
                       val recordFile: String,
                       var eventList: ArrayList<DetectionEvent> = ArrayList(),
                        var duration: Duration = Duration.ZERO,
                        var format: String = TIME_FORMAT_VERSION_2)

