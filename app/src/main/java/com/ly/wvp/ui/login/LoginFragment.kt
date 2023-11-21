package com.ly.wvp.ui.login

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController

import com.ly.wvp.R
import com.ly.wvp.auth.NetError
import com.ly.wvp.data.storage.DataStorage
import com.ly.wvp.util.Utils
import com.ly.wvp.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    companion object{
        const val TAG = "LoginFragment"
    }

    private lateinit var loginViewModel: LoginViewModel
    private var rootView: View? = null

    private var userName: EditText? = null
    private var password: EditText? = null
    private var btnLogin: Button? = null
    private var loading: ProgressBar? = null
    private lateinit var navController: NavController
    private lateinit var storage: DataStorage

    private lateinit var configService: Button

    private lateinit var mRememberPasswd: RadioButton
    private var isCheck = false
    private var passwdMd5 = ""
    private var useRememberPasswd = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)
        storage = DataStorage.getInstance(requireContext())
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]
        loginViewModel.setConfig(storage.getConfig())
        navController = findNavController()
        userName = root.findViewById(R.id.username)
        password = root.findViewById(R.id.password)
        btnLogin = root.findViewById(R.id.login)
        loading = root.findViewById(R.id.loading)
        configService = root.findViewById(R.id.go_config_service)
        mRememberPasswd = root.findViewById(R.id.remember_passwd)
        isCheck = storage.getConfig().rememberPasswd
        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                btnLogin?.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    userName?.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    password?.error = getString(it)
                }
            })

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loading?.visibility = View.GONE
                loginResult.success?.let {
                    updateUiWithUser(it)
                }
            })
        loginViewModel.getNetError().observe(viewLifecycleOwner){
            handleLoginError(it)
        }



        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                checkUseRememberPasswd()
                loginViewModel.loginDataChanged(
                    userName?.text.toString(),
                    password?.text.toString()
                )
            }

            private fun checkUseRememberPasswd() {
                if (password?.text.toString() == passwdMd5){
                    return
                }
                else{
                    useRememberPasswd = false
                    val passwdText: String =  password?.text.toString()
                    passwdMd5 = Utils.md5Encoding(passwdText)
                }
            }
        }
        userName?.addTextChangedListener(afterTextChangedListener)
        password?.addTextChangedListener(afterTextChangedListener)
        password?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(
                    userName?.text.toString(),
                    password?.text.toString(),
                    useRememberPasswd
                )
            }
            false
        }

        btnLogin?.setOnClickListener {
            loading?.visibility = View.VISIBLE
            loginViewModel.login(
                userName?.text.toString(),
                password?.text.toString(),
                useRememberPasswd
            )
        }

        configService.setOnClickListener {
            goSettings()
        }
        //记住密码
        initRememberPasswd()
    }

    private fun initRememberPasswd() {
        passwdMd5 = storage.getConfig().passwd
        if (passwdMd5.isNotEmpty()) {
            useRememberPasswd = true
            password?.setText(passwdMd5)
            userName?.setText(storage.getConfig().user)
        }
        //remember passwd
        mRememberPasswd.isChecked = isCheck
        //更新UI状态
        mRememberPasswd.setOnClickListener {
            Log.d(TAG, "setOnClickListener before : $isCheck")
            val newCheck = !isCheck
            val config = storage.getConfig()
            config.rememberPasswd = newCheck
            if (storage.saveConfig(config)){
                Log.d(TAG, "setOnClickListener saveConfig newCheck : $newCheck")
                mRememberPasswd.isChecked = newCheck
                isCheck = newCheck
            }
            //保存失败
            else{
                Log.d(TAG, "onViewCreated: roll back  $isCheck")
            }
        }
    }

    private fun handleLoginError(error: NetError) {
        loading?.visibility = View.GONE
        error.toString().shortToast(requireContext())
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        savePasswd()
        toastLoginSuccess()
        //登录成功进入设备页面
        navController.navigate(R.id.deviceListFragment,
            null,
            NavOptions.Builder().setPopUpTo(R.id.deviceListFragment, true).build())
    }

    private fun savePasswd() {
        val user = userName?.text.toString()
        val config = storage.getConfig()
        config.user = user
        config.passwd = if (isCheck){
            passwdMd5
        }
        else{
            ""
        }
        storage.saveConfig(config)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        rootView = null
    }

    private fun toastLoginSuccess(){
        val appContext = context?.applicationContext ?: return
        CoroutineScope(Dispatchers.Main).launch {
            R.string.user_login_success.shortToast(appContext)
        }
    }

    private fun goSettings(){
        navController.navigate(R.id.settingsBeforeLoginFragment,
            null,
            NavOptions.Builder().setPopUpTo(R.id.settingsBeforeLoginFragment, true).build()
        )
    }
}