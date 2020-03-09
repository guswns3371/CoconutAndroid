package com.example.coconut.ui.auth.register

import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.example.coconut.R
import com.example.coconut.base.BaseKotlinActivity
import com.example.coconut.databinding.ActivityRegisterBinding
import com.example.coconut.ui.auth.login.LoginActivity
import kotlinx.android.synthetic.main.activity_register.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : BaseKotlinActivity<ActivityRegisterBinding,RegisterViewModel>() {
    private val TAG = "RegisterActivity"
    override val layoutResourceId: Int
        get() = R.layout.activity_register
    override val viewModel: RegisterViewModel by viewModel()
    override var toolbar: Toolbar? = null


    override fun initStartView() {
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = this
        viewModel.onCreate()
    }

    override fun initDataBinding() {
        viewModel.registerResponseLiveData.observe(this, Observer {
            when(it.isRegistered){
                true->{
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity((intent))
                    finish() // 현재 액티비티를 스택에서 pop
                }
                false->{}
            }
            when(it.isEmailOk){
                true->{
                    emailErrorText.setTextColor(Color.GREEN)
                    emailErrorText.text = resources.getString(R.string.valid_email)
                }
                false->{
                    emailErrorText.setTextColor(Color.RED)
                    emailErrorText.text = resources.getString(R.string.invalid_email)
                }
            }
        })

        viewModel.registerValidLiveData.observe(this, Observer {
            when(it.idOk){
                true->{ idErrorText.text = "" }
                false->{ idErrorText.text = resources.getString(R.string.invalid_userid) }
            }
            when(it.nameOk){
                true->{ nameErrorText.text = "" }
                false->{ nameErrorText.text = resources.getString(R.string.invalid_username) }
            }
            when(it.passwordOk){
                true->{}
                false->{}
            }
            when(it.passwordConOk){
                true->{ passwordErrorText.text = "" }
                false->{ passwordErrorText.text = resources.getString(R.string.invalid_passCon) }
            }
        })
    }

    override fun initAfterBinding() {
    }


}
