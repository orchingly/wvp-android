package com.ly.wvp.record.detail

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.ly.wvp.R
import com.ly.wvp.data.model.StreamDetectionItem
import com.ly.wvp.data.model.StreamDetectionItem.Companion.ACT_START
import com.ly.wvp.data.model.StreamDetectionItem.Companion.ACT_STOP
import com.ly.wvp.data.model.StreamDetectionItem.Companion.EVENT_MOVE
import com.ly.wvp.data.model.StreamDetectionItem.Companion.EVENT_PERSON
import kotlinx.coroutines.yield
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class RecordFileListAdapter: Adapter<RecordFileListAdapter.FileListHolder>() {

    companion object{
        private const val TAG = "RecordFileListAdapter"
    }

    private val recordList = ArrayList<CloudRecord>()
    private val actionList = ArrayList<StreamDetectionItem>()
    private var playListener: PlayListener? = null

    private var mLastPlayItem: FileListHolder? = null
    private var mLastItemColor: Int = -1

    private val mFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    fun setPlayListener(playListener: PlayListener){
        this.playListener = playListener
    }

    fun updateRecordList(record: List<CloudRecord>){
        recordList.clear()
        recordList.addAll(reformatCloudRecordIfNeeded(record))
    }

    private fun reformatCloudRecordIfNeeded(record: List<CloudRecord>): Collection<CloudRecord> {
        val formatRecord = ArrayList<CloudRecord>()
        record.forEach {
            val fileName = formatFileNameIfNeeded(it.recordFile)
            //时间戳格式
            //onRecordPlay播放url用时间戳来区分新版旧版的格式
            val format = if (it.recordFile.contains(":")) TIME_FORMAT_VERSION_1 else TIME_FORMAT_VERSION_2
            formatRecord.add(CloudRecord(it.app, it.stream, it.calendar, fileName, it.eventList, it.duration, format))
        }
       return formatRecord
    }


    /**
     * 解析录像事件
     */
    suspend fun analyzeRecordAction(list: List<StreamDetectionItem>) {
        Log.d(TAG, "updateActionList: record size: ${recordList.size}")
        for (k in 0 until recordList.size){
            yield()
            val it = recordList[k]
            val fileName = it.recordFile
            val fileSp = fileName.split("-")
            //起始时间
            val from = fileSp[0]
            val to = fileSp[1]

            val calendar = it.calendar
            val yy = calendar.year.toString()
            val mm = if (calendar.month < 10) "0${calendar.month}" else calendar.month.toString()
            val dd = if (calendar.day < 10) "0${calendar.day}" else calendar.day.toString()


            val date = "$yy-$mm-$dd"
            val fromDateTime = "$date $from"
            val toDateTime = "$date $to"

            Log.d(TAG, "updateActionList: record : $fromDateTime - $toDateTime")

            val localDateTimeFrom = LocalDateTime.parse(fromDateTime, mFormat)
            val localDateTimeTo = LocalDateTime.parse(toDateTime, mFormat)

            it.eventList = ArrayList()
            if (localDateTimeFrom.isBefore(localDateTimeTo)){

                //计算时间长度
                it.duration = Duration.between(localDateTimeFrom, localDateTimeTo)
                localDateTimeFrom.toLocalTime()

                //移动事件
                val moveEvents = extractEvent(EVENT_MOVE, list)
                if (moveEvents.isNotEmpty()){
                    val eventList = checkAction(localDateTimeFrom, localDateTimeTo, moveEvents)
                    it.eventList.addAll(eventList)
                }

                //人形事件
//                val personEvents = extractEvent(EVENT_PERSON, list)
//                if (personEvents.isNotEmpty()){
//                    val eventList = checkAction(localDateTimeFrom, localDateTimeTo, personEvents)
//                    it.eventList.addAll(eventList)
//                }

            }
            //日期跨天
            else{
                Log.d(TAG, "updateActionList: time cross day")
            }
        }
//        recordList.forEach {
//
//        }

    }

    /**
     * 分离不同类型事件
     */
    private fun extractEvent(
        eventType: Int,
        source: List<StreamDetectionItem>
    ): ArrayList<StreamDetectionItem> {
        val target = ArrayList<StreamDetectionItem>()
        source.forEach{
            if (it.dType == eventType){
                target.add(it)
            }
        }
        return target
    }

    /**
     * localDateTimeFrom ->localDateTimeTo属于一个时间片段
     * 从list(例如一天的事件列表)中找出属于这个时间片段的系列事件
     */
    private fun checkAction(
        localDateTimeFrom: LocalDateTime,
        localDateTimeTo: LocalDateTime,
        list: List<StreamDetectionItem>
    ): ArrayList<DetectionEvent> {
        //事件开始时间
        var actionDetectStartTime = ""
        var actionDetectStartTimeLocal: LocalDateTime? = null
        val eventList = ArrayList<DetectionEvent>()
        var lastAction = -1

        for (i in list.indices){

            //记录到事件的时间点
            val actPointTime = list[i].actTime ?: ""
            //事件时间异常
            if (actPointTime.isEmpty()){
                continue
            }

            val actionDateTime = LocalDateTime.parse(actPointTime, mFormat)
            val action = list[i].actType
            val event = list[i].dType

            //还未到起始时间,继续寻找下一个事件点
            if (actionDateTime.isBefore(localDateTimeFrom)){
                Log.d(TAG, "checkAction: actionDateTime.isBefore(localDateTimeFrom) : actionDateTime = $actPointTime,  localDateTimeFrom = ${localDateTimeFrom.format(mFormat)}")
                lastAction = action
                continue
            }

            Log.d(TAG, "checkAction: actPointTime = $actPointTime, action = $action, event = $event")

            //超过结束时间,后续事件无效
            if (actionDateTime.isAfter(localDateTimeTo)){
                //时间片段结束,但是事件没有结束,从actionDetectStartTime到事件片段结尾都应该标记为事件
                if (actionDetectStartTime.isNotEmpty()){
                    Log.d(TAG, "checkAction: actionDateTime.isAfter(localDateTimeTo) actionDetectStartTime = $actionDetectStartTime")
                    eventList.add( DetectionEvent(actionDetectStartTime, actPointTime, event,
                        Duration.between(localDateTimeFrom, actionDetectStartTimeLocal).seconds,-1))
                }
                //时间片段结束,但是没有在该时间内找到开始事件,那么可能是开始事件在片段之前
                else if (lastAction == ACT_START
                    //增加保险, 理论上肯定是空
                    && eventList.isEmpty()){
                    Log.d(TAG, "checkAction: lastAction == ACT_START, full event")
                    //开始事件发生在片段之前, 说明整个片段都属于该事件
                    eventList.add( DetectionEvent(localDateTimeFrom.format(mFormat), localDateTimeTo.format(mFormat), event,
                            0,-1))
                }
                //真的没有开始事件
                else{
                    Log.d(TAG, "checkAction: actionDateTime.isAfter(localDateTimeTo) actionDetectStartTime = empty")
                }
                return eventList
            }

            //找到事件结束点
            if (action == ACT_STOP){
                //第一次找到事件结束点,说明从from 到当前事件时间内都发生该事件,应当记录下来
                //只找到事件结束没有找到开始
                if (/*eventList.isEmpty() && */actionDetectStartTime.isEmpty()){
                    Log.d(TAG, "checkAction: ACT_STOP actionDetectStartTime.isEmpty ")
                    val detectionEvent = DetectionEvent(localDateTimeFrom.format(mFormat), actionDateTime.toString(), event,
                        0, Duration.between(localDateTimeFrom, actionDateTime).seconds)
                    eventList.add(detectionEvent)
                }
                //from->to时间内必有开始事件的点,且被缓存, 此时应当记录起始时间
                else{
                    Log.d(TAG, "checkAction: ACT_STOP eventList.add ")
                    val detectionEvent = DetectionEvent(actionDetectStartTime, actPointTime, event,
                        Duration.between(localDateTimeFrom, actionDetectStartTimeLocal).seconds,
                        Duration.between(localDateTimeFrom, actionDateTime).seconds)
                    eventList.add(detectionEvent)
                    //重置开始时间
                    actionDetectStartTime = ""
                    actionDetectStartTimeLocal = null
                }
            }
            //找到一个事件开始点
            else if (action == ACT_START){
                Log.d(TAG, "checkAction: ACT_START $actPointTime")
                actionDetectStartTime = actPointTime
                actionDetectStartTimeLocal = actionDateTime
            }
        }
        return eventList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileListHolder {
        val holder = FileListHolder(LayoutInflater.from(parent.context).inflate(R.layout.record_file_list_item, parent, false))
        holder.itemView.setOnClickListener{
           handleClickEvent(holder, parent.context)
        }
        return holder
    }

    override fun getItemCount(): Int {
        return recordList.size
    }

    override fun onBindViewHolder(holder: FileListHolder, position: Int) {
        val fileName = recordList[position].recordFile
        val fileSp = fileName.split("-")
        //14:42:35-14:42:48-13517.mp4
        val timeSuffix = fileSp[2]
        val index = timeSuffix.indexOf(".", 0, true)
        val timeStamp = if (index > -1){
            timeSuffix.substring(0, index)
        } else "0"

        val timeSecond = convertToTime(timeStamp.toInt())
        //起始时间
        val from = fileSp[0]
        val to = fileSp[1]

        holder.time.text = timeSecond
        holder.recordTimeStart.text = from
        holder.recordTimeEnd.text = to
        holder.record = recordList[position]

        val context = holder.itemView.context

        if (position == clickPos){
            setRecordTimeColor(holder, context.resources.getColorStateList(R.color.red, context.theme))
        }
        else{
            setRecordTimeColor(holder, context.resources.getColorStateList(R.color.text_primary, context.theme))
        }

        val eventList = recordList[position].eventList
        if (eventList.isEmpty()){
            holder.recordState.setTextColor(holder.itemView.context.resources.getColorStateList(R.color.ticket_check_bg, holder.itemView.context.theme))
            holder.recordState.text = "正常"
            return
        }
        else{
            if (eventList.first().event == EVENT_PERSON || eventList.last().event == EVENT_PERSON){
                holder.recordState.setTextColor(holder.itemView.context.resources.getColorStateList(R.color.red, holder.itemView.context.theme))
                holder.recordState.text = "人形"
            }
            else if (eventList.first().event == EVENT_MOVE || eventList.last().event == EVENT_MOVE){
                holder.recordState.setTextColor(holder.itemView.context.resources.getColorStateList(R.color.red, holder.itemView.context.theme))
                holder.recordState.text = "移动"
            }

        }

    }

    /**
     * 老版本日期格式: 14:42:35-14:42:48-13517.mp4
     * 新版本日期格式: 155343-155430.mp4
     * 将新版本格式转为老版本格式
     */
    private fun formatFileNameIfNeeded(recordFile: String): String {
        val fileSp = recordFile.split("-")
        if (fileSp.isEmpty()){
            return recordFile
        }
        if (fileSp.size < 2){
            Log.w(TAG, "formatFileNameIfNeeded: invalid record file: $recordFile")
            return recordFile
        }
        if (fileSp[0].contains(":")){
            return recordFile
        }
        else{

            val target = SimpleDateFormat("HH:mm:ss", Locale.CHINA)
            val origin = SimpleDateFormat("HHmmss", Locale.CHINA)
            val dayOfEndDate: Date = origin.parse("235959")?: kotlin.run{
                Log.w(TAG, "formatFileNameIfNeeded: parse failed 235959" )
                return recordFile
            }
            val dayOfStartDate: Date = origin.parse("000000")?:  kotlin.run{
                Log.w(TAG, "formatFileNameIfNeeded: parse failed 000000" )
                return recordFile
            }
            val from: Date = origin.parse(fileSp[0])?: kotlin.run {
                Log.w(TAG, "formatFileNameIfNeeded: parse from failed $recordFile")
                return recordFile
            }
            val to: Date = origin.parse(fileSp[1].substring(0, fileSp[1].indexOf(".")))?: kotlin.run {
                Log.w(TAG, "formatFileNameIfNeeded: parse to failed $recordFile")
                return recordFile
            }
            //开始时间大于结束时间,支持跨1天,跨两天不支持
            val duration = if (from.time > to.time){
                dayOfEndDate.time - from.time + (to.time - dayOfStartDate.time)
            }
            else{
                to.time - from.time
            }
            return "${target.format(from)}-${target.format(to)}-${duration}.mp4"
        }
    }

    private fun convertToTime(timeStamp: Int): String{
        val t = timeStamp / 1000
        //超过1分钟
        return if (t >= 60) {
            var min = t / 60
            var time = "$min 分"
            //超过1小时
            if (min >= 60) {
                val h = min / 60
                min %= 60

                time = "$h 小时 $min 分"
            }
            val sec = t % 60
            time = "$time $sec 秒"
            return time
        }
        else{
            "$t 秒"
        }
    }

    var clickPos: Int = -1
//    var clicked: Boolean = false

    private fun handleClickEvent(holder: FileListHolder, context: Context){
        holder.record?.let {
            playListener?.onRecordPlay(it)
        }
        val pos = holder.adapterPosition
        if (clickPos != pos){
            //刷新之前点击的item状态
            notifyItemChanged(clickPos)
            //更新点击位置
            clickPos = pos
            setRecordTimeColor(holder, context.resources.getColorStateList(R.color.red, context.theme))
        }
    }

    private fun setRecordTimeColor(holder: FileListHolder, colorState: ColorStateList){
        holder.recordTimeStart.setTextColor(colorState)
        holder.recordTimeEnd.setTextColor(colorState)
    }

    class FileListHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val recordTimeStart: TextView = itemView.findViewById(R.id.record_file_time_start)
        val recordTimeEnd: TextView = itemView.findViewById(R.id.record_file_time_end)
        val recordState: TextView = itemView.findViewById(R.id.record_file_event_state)
        var record: CloudRecord? = null
        val time: TextView = itemView.findViewById(R.id.file_time_second_total)

    }

    interface PlayListener{
        fun onRecordPlay(record: CloudRecord)
    }
}