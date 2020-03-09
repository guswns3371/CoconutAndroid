package com.example.coconut.ui.auth.login.verify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.example.coconut.Constant
import com.example.coconut.IntentID
import com.example.coconut.R
import com.example.coconut.base.BaseKotlinActivity
import com.example.coconut.databinding.ActivityEmailVerifyBinding
import com.example.coconut.ui.MainActivity
import com.example.coconut.ui.auth.login.LoginViewModel
import kotlinx.android.synthetic.main.activity_email_verify.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class EmailVerifyActivity : BaseKotlinActivity<ActivityEmailVerifyBinding,LoginViewModel>() {
    private val TAG = "EmailVerifyActivity"
    override var toolbar: Toolbar? = null

    override val layoutResourceId: Int
        get() = R.layout.activity_email_verify

    override val viewModel: LoginViewModel by viewModel()

    override fun initStartView() {
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = this

        intent.getStringExtra(IntentID.EMAIL)?.let{
            /** MVVM 패턴에 위배되지 않을까?*/
            viewModel.email.set(it)
        }
    }

    override fun initDataBinding() {
        viewModel.loginResponseLiveData.observe(this, Observer {event->
            event.getContentIfNotHandled()?.let {
                when(it.isConfirmed){
                    true->{
                        verifyErrorText.text= ""
                        callActivity(Constant.HOME_PAGE)
                    }
                    false->{
                        verifyErrorText.text = "코드가 일치하지 않습니다"
                    }
                }
            }
        })
    }

    override fun initAfterBinding() {
    }

    private fun callActivity(where: Int) {
        when(where){
            Constant.HOME_PAGE-> {
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity((intent))
                finish() // 현재 액티비티를 스택에서 pop
            }
        }
    }

}
