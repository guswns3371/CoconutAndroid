package com.example.coconut.ui.auth.login

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import androidx.lifecycle.Observer
import com.example.coconut.Constant
import com.example.coconut.R
import com.example.coconut.base.BaseKotlinActivity
import com.example.coconut.databinding.ActivityLoginBinding
import com.example.coconut.ui.ActivityNavigater
import com.example.coconut.ui.MainActivity
import com.example.coconut.ui.auth.passfind.PassFindActivity
import com.example.coconut.ui.auth.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginActivity : BaseKotlinActivity<ActivityLoginBinding,LoginViewModel>() {
    private val TAG = "LoginActivity"
    override val layoutResourceId: Int
        get() = R.layout.activity_login

    override val viewModel : LoginViewModel by viewModel()

    override fun initStartView() {
        //viewModel 에서 button click을 하기 위함
        viewDataBinding.viewModel = viewModel // view와 viewmodel을 연결한다
        viewDataBinding.lifecycleOwner = this
        viewModel.onCreate()
    }

    override fun initDataBinding() {
        viewModel.loginResponseLiveData.observe(this, Observer {
            when(it.isCorrect){
                true->{
                    errorText.text =""
                    callActivity(Constant.HOME_PAGE)
                }
                false->{
                    errorText.text = "아이디와 비밀번호를 확인해주세요"
                }
            }
        })
    }

    override fun initAfterBinding() {
        registerBtn.setOnClickListener { callActivity(Constant.REGISTER_PAGE) }
        findPassword.setOnClickListener { callActivity(Constant.PASSWORD_FIND_PAGE) }
    }

    private fun callActivity(where: Int) {
        when(where){
            Constant.REGISTER_PAGE-> startActivity(Intent(applicationContext,RegisterActivity::class.java))
            Constant.PASSWORD_FIND_PAGE -> startActivity(Intent(applicationContext,PassFindActivity::class.java))
            Constant.HOME_PAGE-> {
                val intent = Intent(applicationContext,MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity((intent))
                finish() // 현재 액티비티를 스택에서 pop
            }
        }
    }
}
