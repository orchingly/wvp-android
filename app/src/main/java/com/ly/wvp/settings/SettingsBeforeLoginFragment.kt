package com.ly.wvp.settings

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import androidx.constraintlayout.widget.Group
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ly.wvp.R

class SettingsBeforeLoginFragment: SettingsFragment() {

    private lateinit var btnGoLogin: Button
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Group>( R.id.nav_bottom_group).visibility = GONE
        btnGoLogin = view.findViewById(R.id.btn_go_login)
    }


    override fun saveConfig(): Boolean{
        val success = super.saveConfig()
        //弹出登录按钮
        if (success){
            btnGoLogin.visibility = VISIBLE
            btnGoLogin.setOnClickListener {
                findNavController().navigate(R.id.loginFragment,
                    null,
                    NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build())
            }
        }
        return success
    }
}