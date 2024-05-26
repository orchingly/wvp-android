package com.ly.wvp.device.onescreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ly.wvp.data.storage.DataStorage

class DeviceFilterViewModel: ViewModel() {

    companion object{
        private const val TAG = "DeviceFilterViewModel"
    }
    /**
     * 选中列表
     */
    private val _filterOptions = MutableLiveData<ArrayList<SelectionItem>>()

    /**
     * 选中列表临时缓存，只有保存后才会同步到 [_filterOptions]
     */
    private val mFilterOptionsCache = ArrayList<SelectionItem>()

    private lateinit var mStorage: DataStorage

    /**
     * 每次打开选项列表都需要同步一下，否则选中数据错乱
     */
    fun syncFilterOptions(options: ArrayList<SelectionItem>){
        mFilterOptionsCache.clear()
        mFilterOptionsCache.addAll(options)
    }
    /**
     * 外部可观察的多选列表
     */
    fun getDeviceFilterOptions(): LiveData<ArrayList<SelectionItem>> = _filterOptions

    /**
     * 初始化多选数据
     */
    fun initFilterOptions(filterOptions: ArrayList<SelectionItem>, storage: DataStorage){
        mFilterOptionsCache.clear()
        mFilterOptionsCache.addAll(filterOptions)
        mStorage = storage
    }

    /**
     * 根据选中状态更新缓存数据
     */
    fun onChannelClicked(check: Boolean, deviceId: String?, channelId: String?){
        if (deviceId == null || channelId == null){
            Log.w(TAG, "onChannelClicked: null device or channel id return")
            return
        }
        val selectionItem = SelectionItem(deviceId, channelId)
        if (check){
            if (mFilterOptionsCache.contains(selectionItem)){
                return
            }
            else{
                Log.d(TAG, "onChannelClicked: checked $selectionItem")
                mFilterOptionsCache.add(selectionItem)
            }
        }
        else{
            Log.d(TAG, "onChannelClicked: unchecked $selectionItem")
            mFilterOptionsCache.remove(selectionItem)
        }
    }

    fun saveChanged(){
        _filterOptions.value = ArrayList(mFilterOptionsCache)
        //保存
        if (!mStorage.saveMultiPlayDeviceList(mFilterOptionsCache)){
            //保存失败
            Log.w(TAG, "bindSelectionConfirmListener: selection save failed")
        }
    }

}