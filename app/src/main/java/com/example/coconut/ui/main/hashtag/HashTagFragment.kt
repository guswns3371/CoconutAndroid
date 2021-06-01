package com.example.coconut.ui.main.hashtag

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coconut.BroadCastIntentID
import com.example.coconut.R
import com.example.coconut.adapter.HashTagRecyclerAdapter
import com.example.coconut.base.BaseKotlinFragment
import com.example.coconut.base.BroadcastReceiverManager
import com.example.coconut.databinding.FragmentHashtagBinding
import com.example.coconut.model.response.hashtag.CovidDataResponse
import com.example.coconut.ui.OnFragmentInteractionListener
import com.example.coconut.ui.setting.SettingActivity
import com.example.coconut.util.MyPreference
import kotlinx.android.synthetic.main.activity_zoomable_image.*
import kotlinx.android.synthetic.main.fragment_hashtag.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HashTagFragment
    : BaseKotlinFragment<FragmentHashtagBinding, HashTagViewModel>(), BroadcastReceiverManager {
    override val layoutResourceId = R.layout.fragment_hashtag
    override val viewModel: HashTagViewModel by viewModel()
    private val recyclerAdapter: HashTagRecyclerAdapter by inject()
    private var loadingDialog: Dialog? = null
    private var listener: OnFragmentInteractionListener? = null
    private var number = 0

    override val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            when (p1?.action) {
                BroadCastIntentID.SEND_ON_CONNECT -> {
                    listener?.onFragmentReload()
                }
                BroadCastIntentID.SEND_ON_DISCONNECT -> {
                }
                BroadCastIntentID.SEND_ON_ERROR -> {
                }
                else -> {
                }
            }
        }
    }

    override val baseToolBar: Toolbar?
        get() = activity?.findViewById(R.id.baseToolBar)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener)
            listener = context
    }

    override fun initStartView() {
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = this

        setToolbarTitle(getString(R.string.title_hashtag))

        val recyclerView =
            viewDataBinding.root.findViewById<RecyclerView>(R.id.hash_recycler_view).apply {
//            layoutManager = LinearLayoutManager(activity!!)
                layoutManager =
                    object :
                        LinearLayoutManager(activity!!, HORIZONTAL, false) {
                        override fun canScrollHorizontally(): Boolean {
                            return false
                        }

                    }
                adapter = recyclerAdapter
                setHasFixedSize(true)
            }

        viewDataBinding.root.findViewById<TextView>(R.id.hash_left_btn_txt).setOnClickListener {
            number = if (number != 0) number - 1 else 0
            recyclerView.scrollToPosition(number)
        }
        viewDataBinding.root.findViewById<TextView>(R.id.hash_right_btn_txt).setOnClickListener {
            number = if (number != recyclerView.childCount) number + 1 else recyclerView.childCount
            recyclerView.scrollToPosition(number)
        }
    }

    override fun initDataBinding() {
        viewModel.newsDataResponseLiveData.observe(this, {
            loadingDialog!!.dismiss()
            recyclerAdapter.setNewsList(it)
        })
        viewModel.covidDataResponseLiveData.observe(this, {
            it.add(0, CovidDataResponse("지역", "증가", "확진자수", "사망자", "일일검사", "발생률"))
            recyclerAdapter.setCovidList(it)
        })
        viewModel.musicDataResponseLiveData.observe(this, {
            recyclerAdapter.setMusicList(it)
        })
    }

    override fun initAfterBinding() {
        loadingDialog = Dialog(activity!!).apply {
            setContentView(R.layout.custom_simple_loading_dialog)
            setCancelable(true)
            show()
        }
        viewModel.getNewsData()
        viewModel.getCovidData()
        viewModel.getMusicTopList()
    }

    override fun setBaseToolbarItemClickListener(itemId: Int) {
        when (itemId) {
            R.id.action_settings -> {
                showAccountSettingPopupMenu()
            }
        }
    }

    private fun showAccountSettingPopupMenu() {
        activity!!.findViewById<View>(R.id.action_settings)?.let { view ->
            PopupMenu(activity!!, view).apply {
                menu.add(getString(R.string.toolbar_account_reload))
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.title) {
                        getString(R.string.toolbar_account_reload) -> {
                            listener?.onFragmentReload()
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
                show()
            }
        }
    }

    override fun setMenuVisibilityOf(menu: Menu) {
        menu.findItem(R.id.action_chat_add).isVisible = false
    }


}