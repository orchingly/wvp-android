package com.ly.wvp.record.detail

/**
 * @param startTime: 起始时间
 * @param endTime: 结束时间, -1:当前片段结尾
 * @param event: 事件: 无,人形,移动
 */
data class DetectionEvent(val startTime: String,
                          val endTime: String,
                          val event: Int,
                          //该事件在当前录像总时长中的起始时间:秒
                          val startSec: Long,
                        //该事件在当前录像总时长中的结束时间:秒
                          val endSec: Long)
