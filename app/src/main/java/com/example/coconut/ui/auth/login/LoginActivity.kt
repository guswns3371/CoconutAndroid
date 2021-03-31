package com.example.coconut.ui.auth.login

import android.app.Dialog
import android.content.Intent
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.example.coconut.Constant
import com.example.coconut.Constant.Companion.RC_AUTH
import com.example.coconut.Constant.Companion.RC_FAIL
import com.example.coconut.IntentID
import com.example.coconut.R
import com.example.coconut.base.BaseKotlinActivity
import com.example.coconut.databinding.ActivityLoginBinding
import com.example.coconut.ui.MainActivity
import com.example.coconut.ui.auth.login.verify.EmailVerifyActivity
import com.example.coconut.ui.auth.passfind.PassFindActivity
import com.example.coconut.ui.auth.register.RegisterActivity
import com.example.coconut.util.disable
import com.example.coconut.util.showToast
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : BaseKotlinActivity<ActivityLoginBinding, LoginViewModel>() {
    private val TAG = "LoginActivity"
    override var toolbar: Toolbar? = null

    override val layoutResourceId: Int
        get() = R.layout.activity_login

    override val viewModel: LoginViewModel by viewModel()

    override fun initStartView() {
        //viewModel 에서 button click을 하기 위함
        viewDataBinding.viewModel = viewModel // view와 viewmodel을 연결한다
        viewDataBinding.lifecycleOwner = this
        viewModel.onCreate()
    }

    override fun initDataBinding() {
        viewModel.loginResponseLiveData.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                when (it.isCorrect) {
                    true -> {
                        errorText.text = ""
                        when (it.isConfirmed) {
                            true -> { // 이메일 인증을 받았을 경우
                                callActivity(Constant.HOME_PAGE)
                            }
                            false -> { // 이메일 인증을 받지 않았을 경우
                                callActivity(Constant.EMAIL_VERIFY_PAGE)
                            }
                        }
                    }
                    false -> {
                        errorText.text = "아이디와 비밀번호를 확인해주세요"
                    }
                }
            }

        })

        viewModel.progressObservable.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                Log.i(TAG, "initDataBinding: ${it.msg}")
            }
        })

        viewModel.activityObservable.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                Log.i(TAG, "Activity request change observed")
                it.intent?.run {
                    startActivityForResult(this,it.rc)
                }
            }
        })

        viewModel.loginSuccessObservable.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                Log.i(TAG, "loginSuccessObservable : $it")
                when(it) {
                    true ->{
                        loginBtn.disable()
                        googleLoginBtn.disable()
                        registerBtn.disable()
                        callActivity(Constant.HOME_PAGE)
                    }
                    false ->{
                        showToast("연동 로그인 실패")
                    }
                }
            }
        })
    }

    override fun initAfterBinding() {
        registerBtn.setOnClickListener { callActivity(Constant.REGISTER_PAGE) }
        findPassword.setOnClickListener { callActivity(Constant.PASSWORD_FIND_PAGE) }
    }

    private fun callActivity(where: Int) {
        when (where) {
            Constant.REGISTER_PAGE -> {
                startActivity(Intent(applicationContext, RegisterActivity::class.java))
            }
            Constant.PASSWORD_FIND_PAGE -> {
                startActivity(Intent(applicationContext, PassFindActivity::class.java))
            }
            Constant.HOME_PAGE -> {
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity((intent))
                finish() // 현재 액티비티를 스택에서 pop
            }
            Constant.EMAIL_VERIFY_PAGE -> {
                val intent = Intent(applicationContext, EmailVerifyActivity::class.java)
                intent.putExtra(IntentID.EMAIL, emailTextInput.text.toString())
                startActivity((intent))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RESULT_CANCELED -> {
                viewModel.notifyActivityResponse(data, RC_FAIL)
            }
            else -> {
                viewModel.notifyActivityResponse(data, RC_AUTH)
            }
        }
    }


}
