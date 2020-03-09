package com.example.coconut.ui.main.hashtag

import android.view.Menu
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.example.coconut.R
import com.example.coconut.base.BaseKotlinFragment
import com.example.coconut.databinding.FragmentHashtagBinding
import kotlinx.android.synthetic.main.fragment_hashtag.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HashTagFragment : BaseKotlinFragment<FragmentHashtagBinding,HashTagViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.fragment_hashtag
    override val viewModel: HashTagViewModel by viewModel()

    override val baseToolBar: Toolbar?
        get() = activity?.findViewById(R.id.baseToolBar)

    override fun initStartView() {
        setToolbarTitle(getString(R.string.title_hashtag))
    }

    override fun initDataBinding() {
        viewModel.text.observe(this, Observer {
            text_notifications.text = it
        })
    }

    override fun initAfterBinding() {
    }

    override fun setBaseToolbarItemClickListener(itemId: Int) {
    }

    override fun setMenuVisibilityOf(menu: Menu) {
        menu.findItem(R.id.action_chat_add).isVisible = false
    }
}