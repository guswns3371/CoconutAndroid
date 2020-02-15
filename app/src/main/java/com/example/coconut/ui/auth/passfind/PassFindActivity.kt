package com.example.coconut.ui.auth.passfind

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.coconut.R
import com.example.coconut.base.BaseKotlinActivity
import com.example.coconut.databinding.ActivityPassFindBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PassFindActivity : BaseKotlinActivity<ActivityPassFindBinding, PassFindViewModel>() {
    private val TAG = "PassFindActivity"
    override val layoutResourceId: Int
        get() = R.layout.activity_pass_find
    override val viewModel: PassFindViewModel by viewModel()

    override fun initStartView() {
        //viewModel 에서 button click을 하기 위함
        viewDataBinding.viewModel = viewModel // view와 viewmodel을 연결한다
        viewDataBinding.lifecycleOwner = this
        viewModel.onCreate()
    }

    override fun initDataBinding() {
    }

    override fun initAfterBinding() {
    }
}
