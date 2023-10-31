package com.ly.wvp

import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.MemoryFile
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.MotionEvent.BUTTON_BACK
import android.view.ViewManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController


const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//
//        Looper.prepare()
//        val looper = Looper.myLooper()
//        val handler = Handler(looper!!)
//        Looper.loop()
//        val msg = Message()
//        msg.what = 1
////        msg.target = handler
//        handler.sendMessage(Message())
//        Message().isAsynchronous = true
//        Handler.createAsync(looper)
//
//        Thread {
//            val manager = windowManager
//
//            manager.addView()
//        }.start()
    }


    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            val navController = findNavController(R.id.fragmentContainerView)
            Log.d(TAG, "onKeyUp: queue size ${navController.backQueue.size}")
            //顶级导航退出
            if (navController.backQueue.size < 3){
                finish()
                return true
            }
            navController.currentBackStackEntry?.let {
                if (it.destination.id == R.id.loginFragment){
                    Log.d(TAG, "onKeyUp: login fragment exit")
                    finish()
                    return true
                }
            }
        }
        return super.onKeyUp(keyCode, event)
    }
}