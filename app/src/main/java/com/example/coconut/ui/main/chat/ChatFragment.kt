package com.example.coconut.ui.main.chat

import android.content.*
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.FOCUS_DOWN
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.coconut.BroadCastIntentID
import com.example.coconut.R
import com.example.coconut.SocketReceive
import com.example.coconut.adapter.ChatListRecyclerAdapter
import com.example.coconut.base.BaseKotlinFragment
import com.example.coconut.base.BroadcastReceiverManager
import com.example.coconut.base.SocketServiceManager
import com.example.coconut.databinding.FragmentChatBinding
import com.example.coconut.model.response.chat.ChatRoomListResponse
import com.example.coconut.service.SocketService
import com.example.coconut.ui.main.chat.add.AddChatActivity
import com.example.coconut.ui.setting.SettingActivity
import com.example.coconut.util.MyPreference
import com.example.coconut.util.toArrayList
import com.example.coconut.util.toCleanString
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.gmail.bishoybasily.stomp.lib.StompClient

class ChatFragment : BaseKotlinFragment<FragmentChatBinding, ChatViewModel>(),
    SocketServiceManager, BroadcastReceiverManager {

    private val TAG = "ChatFragment"
    override val layoutResourceId: Int
        get() = R.layout.fragment_chat
    override val viewModel: ChatViewModel by viewModel()
    private val recyclerAdapter: ChatListRecyclerAdapter by inject()
    private lateinit var chatRoomRoomList: ArrayList<ChatRoomListResponse>

    private val pref: MyPreference by inject()
    override val baseToolBar: Toolbar?
        get() = activity?.findViewById(R.id.baseToolBar)

    lateinit var myIdPref: String
    override var isBind: Boolean = false
    override var socket: Socket? = null
    override var stompClient: StompClient? = null
    override val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e("serviceConn", "onServiceDisconnected")
            socket = null
            stompClient = null
            isBind = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e("serviceConn", "onServiceConnected")
            val binder = service as SocketService.MyBinder
            stompClient = binder.getService().getStompClient()
            socket = binder.getService().getSocket()
            isBind = true

            socketForChatListUpdate()
        }
    }
    override val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            when (p1?.action) {
                BroadCastIntentID.SEND_ON_CONNECT -> {
                    socketForChatListUpdate()
                }
                BroadCastIntentID.SEND_ON_DISCONNECT -> {
                    clearDisposable()
                }
                else -> {
                }
            }
        }
    }

    private fun socketForChatListUpdate() {
        if (stompClient == null) Log.e(TAG, "onStompClient is null")
        stompClient?.apply {
            // 안읽은 메시지가 있는 경우
            addDisposable(this.join("/sub/chat/frag/$myIdPref")
                .doOnError { error -> Log.e(TAG, "socketForChatListUpdate error: $error") }
                .subscribe {
                    activity?.runOnUiThread {
                        Log.e(TAG, "socketForChatListUpdate: $it")
                        Thread.sleep(10)
                        viewModel.getChatRoomLists(myIdPref)
                    }
                })
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getChatRoomLists(myIdPref)
    }

    override fun onStop() {
        Log.e(TAG, "onStop")
        super.onStop()
        clearDisposable()
    }

    override fun initStartView() {
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = this

        registerReceiver()

        bindService(activity)

        myIdPref = pref.userIdx!!

        setToolbarTitle(getString(R.string.title_chat))
        viewDataBinding.root.findViewById<RecyclerView>(R.id.chat_recycler_view).apply {
            layoutManager = LinearLayoutManager(activity!!)
            adapter = recyclerAdapter
            setHasFixedSize(true)
        }


    }

    override fun initDataBinding() {
        viewModel.chatRoomListResponseLiveData.observe(this, Observer {
            chatRoomRoomList = arrayListOf()
            it.forEach { chatList ->
                chatRoomRoomList.add(chatList)
            }
            recyclerAdapter.addChatList(chatRoomRoomList)
        })
    }

    override fun initAfterBinding() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterReceiver()
        unbindService(activity)
    }

    private fun registerReceiver() {
        IntentFilter(BroadCastIntentID.SEND_ON_CONNECT).let {
            it.addAction(BroadCastIntentID.SEND_ON_DISCONNECT)
            registerBroadcastReceiver(activity!!, it)
        }
    }

    private fun unregisterReceiver() {
        unregisterBroadcastReceiver(activity!!)
    }

    override fun setBaseToolbarItemClickListener(itemId: Int) {
        when (itemId) {
            R.id.action_settings -> {
                showAccountSettingPopupMenu()
            }
            R.id.action_chat_add -> {
                startActivity(Intent(activity, AddChatActivity::class.java))
            }
        }
    }

    private fun showAccountSettingPopupMenu() {
        activity!!.findViewById<View>(R.id.action_settings)?.let { view ->
            PopupMenu(activity!!, view).apply {
                menu.add(getString(R.string.toolbar_account_edit))
                menu.add(getString(R.string.toolbar_account_setting))
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.title) {
                        getString(R.string.toolbar_account_edit) -> {
                            /**편집*/
                            showToast("편집")
                        }
                        getString(R.string.toolbar_account_setting) -> {
                            /**전체설정*/
                            startActivity(Intent(activity, SettingActivity::class.java))
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
                show()
            }
        }
    }

    override fun setMenuVisibilityOf(menu: Menu) {
        menu.findItem(R.id.action_chat_add).isVisible = true
    }

    private val onChatListUpdate = Emitter.Listener {
        activity?.runOnUiThread {
            viewModel.getChatRoomLists(myIdPref)
        }
    }

}