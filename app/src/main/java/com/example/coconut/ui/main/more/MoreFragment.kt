package com.example.coconut.ui.main.more


import android.content.Intent
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.example.coconut.R
import com.example.coconut.base.BaseKotlinFragment
import com.example.coconut.databinding.FragmentMoreBinding
import com.example.coconut.ui.setting.SettingActivity
import kotlinx.android.synthetic.main.fragment_more.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MoreFragment : BaseKotlinFragment<FragmentMoreBinding,MoreViewModel>() {

    override val layoutResourceId: Int
        get() = R.layout.fragment_more

    override val baseToolBar: Toolbar?
        get() = activity?.findViewById(R.id.baseToolBar)

    override val viewModel: MoreViewModel by viewModel()

    override fun initStartView() {
        setToolbarTitle(getString(R.string.title_more))
    }

    override fun initDataBinding() {
        viewModel.text.observe(this, Observer {
            textView.text = it
        })
    }

    override fun initAfterBinding() {

    }

    override fun setBaseToolbarItemClickListener(itemId: Int) {
        when(itemId){
            R.id.action_settings->{
                startActivity(Intent(activity, SettingActivity::class.java))
            }
        }
    }

    override fun setMenuVisibilityOf(menu: Menu) {
        menu.findItem(R.id.action_chat_add).isVisible = false
    }
}