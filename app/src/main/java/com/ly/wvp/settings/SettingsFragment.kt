package com.ly.wvp.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ly.wvp.R
import com.ly.wvp.data.storage.DataStorage
import com.ly.wvp.data.storage.SettingsConfig
import com.ly.wvp.util.shortToast
import com.ly.wvp.widget.check.FinishTicket

open class SettingsFragment : Fragment() {

    companion object {

        private const val TAG = "SettingsFragment"
    }

    private lateinit var viewModel: SettingsViewModel
    private lateinit var navController: NavController
    private lateinit var eIp: EditText
    private lateinit var ePort: EditText
    private lateinit var tls: CheckBox
    private lateinit var storage: DataStorage
    private lateinit var anim: FinishTicket

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storage = DataStorage.getInstance(requireContext())
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        navController = findNavController()
        eIp = view.findViewById(R.id.input_ip)
        ePort = view.findViewById(R.id.input_port)
        tls = view.findViewById(R.id.enable_tls)
        anim = view.findViewById(R.id.save_finish)
        activateBottomNav(view)
        restoreSavedConfig()

        view.findViewById<Button>(R.id.btn_save).setOnClickListener {
            saveConfig()
        }
    }

    private fun restoreSavedConfig(){
        val config = storage.getConfig()
        if (config.ip.isNotEmpty()) {
            eIp.setText(config.ip)
        }
        if (config.port > 0) {
            ePort.setText(config.port.toString())
        }
        tls.isSelected = config.enableTls
    }

    open fun saveConfig(): Boolean{
        if (ePort.text == null){
            Log.w(TAG, "saveConfig: text null" )
            R.string.tip_port_not_null.shortToast(requireContext())
        }
        if (ePort.text.toString().isEmpty()){
            Log.w(TAG, "saveConfig: text is empty")
            R.string.tip_port_not_null.shortToast(requireContext())
        }
        val port = ePort.text.toString().toInt()
        if (eIp.text == null){
            Log.w(TAG, "saveConfig: ip text null")
            R.string.tip_ip_not_null.shortToast(requireContext())
        }
        val config = SettingsConfig(eIp.text.toString(), port, tls.isSelected)
        if (storage.saveConfig(config)){
            anim.setUpEvent()
            return true
        }
        return false
    }

    private fun activateBottomNav(view: View){
        view.findViewById<Button>(R.id.tab_device).isSelected = false
        view.findViewById<Button>(R.id.tab_multi_device_play).isSelected = false
        view.findViewById<Button>(R.id.tab_record).isSelected = false
        view.findViewById<Button>(R.id.tab_settings).isSelected = true

        view.findViewById<Button>(R.id.tab_device).setOnClickListener {
            navController.navigate(R.id.deviceListFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.deviceListFragment, true).build()
            )
        }
        view.findViewById<Button>(R.id.tab_record).setOnClickListener {
            navController.navigate(R.id.cloudRecordListFragment,
                null,
                //cloudRecordListFragment及以上全部出栈
                NavOptions.Builder().setPopUpTo(R.id.cloudRecordListFragment, true).build()
            )
        }
        view.findViewById<Button>(R.id.tab_multi_device_play).setOnClickListener {
            navController.navigate(R.id.multiPlayFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.multiPlayFragment, true).build()
            )
        }
    }

}