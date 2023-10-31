package com.ly.wvp.ui.login

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController

import com.ly.wvp.R
import com.ly.wvp.auth.NetError
import com.ly.wvp.data.storage.DataStorage
import com.ly.wvp.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private var rootView: View? = null

    private var userName: EditText? = null
    private var password: EditText? = null
    private var btnLogin: Button? = null
    private var loading: ProgressBar? = null
    private lateinit var navController: NavController
    private lateinit var storage: DataStorage

    private lateinit var configService: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)
        storage = DataStorage(requireContext())
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]
        loginViewModel.setConfig(storage.getConfig())
        navController = findNavController()
        userName = root.findViewById(R.id.username)
        password = root.findViewById(R.id.password)
        btnLogin = root.findViewById(R.id.login)
        loading = root.findViewById(R.id.loading)
        configService = root.findViewById(R.id.go_config_service)

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
                loginViewModel.loginDataChanged(
                    userName?.text.toString(),
                    password?.text.toString()
                )
            }
        }
        userName?.addTextChangedListener(afterTextChangedListener)
        password?.addTextChangedListener(afterTextChangedListener)
        password?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(
                    userName?.text.toString(),
                    password?.text.toString()
                )
            }
            false
        }

        btnLogin?.setOnClickListener {
            loading?.visibility = View.VISIBLE
            loginViewModel.login(
                userName?.text.toString(),
                password?.text.toString()
            )
        }

        configService.setOnClickListener {
            goSettings()
        }
    }

    private fun handleLoginError(error: NetError) {
        loading?.visibility = View.GONE
        error.toString().shortToast(requireContext())
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        toastLoginSuccess()
        //登录成功进入设备页面
        navController.navigate(R.id.deviceListFragment,
            null,
            NavOptions.Builder().setPopUpTo(R.id.deviceListFragment, true).build())
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