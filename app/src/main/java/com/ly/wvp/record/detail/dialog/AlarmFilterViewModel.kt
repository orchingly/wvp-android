package com.ly.wvp.record.detail.dialog

import android.content.DialogInterface
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ly.wvp.data.storage.DataStorage

/**
 * 云端录像筛选管理
 */

class AlarmFilterViewModel(private val mStorage: DataStorage): ViewModel() {

    companion object{
        const val TAG = "AlarmFilterViewModel"
        const val NORMAL = "正常"
        const val MOVE = "移动"
        const val OTHER = "异常"
    }

    /**
     * 报警选项列表
     */
    private val mAlarmOptionsArray = arrayOf(NORMAL, MOVE, OTHER)

    private val _checkedAlarmOptions  = MutableLiveData<BooleanArray>()

    /**
     * 报警选项选中标记
     */
    private lateinit var mCheckedAlarmOptions: BooleanArray

    /**
     * 多选缓存,保存后刷新到 [_checkedAlarmOptions]
     */
    private lateinit var mCheckedCache: BooleanArray


    init {
        mCheckedAlarmOptions = mStorage.getAlarmFilterArray(mAlarmOptionsArray)
        _checkedAlarmOptions.value = mCheckedAlarmOptions
        mCheckedCache = BooleanArray(mCheckedAlarmOptions.size)
    }

    private val mAlarmChoiceListener by lazy {
        DialogInterface.OnMultiChoiceClickListener { _, which, isChecked ->
            val optionString = mAlarmOptionsArray[which]
            Log.d(TAG, "OnMultiChoiceClickListener: check $optionString $isChecked")
            mCheckedCache[which] = isChecked
        }
    }

    private val mAlarmDialogClickListener by lazy {
        DialogInterface.OnClickListener { _, which ->
            when (which){
                //保存选项
                DialogInterface.BUTTON_POSITIVE ->{
                    Log.d(TAG, "OnClickListener: ok click")
                    for (i in mCheckedAlarmOptions.indices){
                        mCheckedAlarmOptions[i] = mCheckedCache[i]
                    }
                    mStorage.saveAlarmFilterArray(mAlarmOptionsArray, mCheckedAlarmOptions)
                    _checkedAlarmOptions.value = mCheckedAlarmOptions
                }
                DialogInterface.BUTTON_NEGATIVE ->{
                    Log.d(TAG, "OnClickListener: cancel click")
                }
            }
        }
    }


    fun getAlarmDialogClickListener() = mAlarmDialogClickListener

    fun getAlarmChoiceListener() = mAlarmChoiceListener

    fun getAlarmOptionsArray(): Array<String> = mAlarmOptionsArray

    fun getCheckedAlarmOptions(): LiveData<BooleanArray> = _checkedAlarmOptions

    fun getCheckedCache() = mCheckedCache

}