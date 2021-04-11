package com.example.coconut.ui.main.chat

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coconut.R
import com.example.coconut.SocketReceive
import com.example.coconut.adapter.ChatListRecyclerAdpater
import com.example.coconut.base.BaseKotlinFragment
import com.example.coconut.base.SocketServiceManager
import com.example.coconut.databinding.FragmentChatBinding
import com.example.coconut.model.response.chat.ChatListResponse
import com.example.coconut.service.SocketService
import com.example.coconut.ui.main.chat.add.AddChatActivity
import com.example.coconut.ui.setting.SettingActivity
import com.example.coconut.util.MyPreference
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.gmail.bishoybasily.stomp.lib.StompClient
class ChatFragment : BaseKotlinFragment<FragmentChatBinding,ChatViewModel>() , SocketServiceManager{

    private val TAG = "ChatFragment"
    override val layoutResourceId: Int
        get() = R.layout.fragment_chat
    override val viewModel: ChatViewModel by viewModel()
    private val recyclerAdapter : ChatListRecyclerAdpater by inject()
    private lateinit var chatRoomList : ArrayList<ChatListResponse>

    private val pref : MyPreference by inject()
    override val baseToolBar: Toolbar?
            get() = activity?.findViewById(R.id.baseToolBar)

    lateinit var myIdPref: String
    override var isBind: Boolean =false
    override var socket: Socket?=null
    override var stompClient: StompClient? = null
    override val serviceConnection: ServiceConnection = object : ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e("serviceConn","onServiceDisconnected")
            socket = null
            isBind = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e("serviceConn","onServiceConnected")
            val binder = service as SocketService.MyBinder
            socket = binder.getService().getSocket()
            isBind = true

            socketForChatListUpdate()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getChatRoomLists(myIdPref)
    }

    override fun initStartView() {
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = this

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
        viewModel.chatListResponseLiveData.observe(this, Observer {
            chatRoomList = arrayListOf()
            it.forEach {chatList->
                chatRoomList.add(chatList)
            }
            recyclerAdapter.addChatList(chatRoomList)
        })
    }

    override fun initAfterBinding() {
    }

    override fun onDestroyView() {
        unbindService(activity)
        super.onDestroyView()
    }

    override fun setBaseToolbarItemClickListener(itemId: Int) {
        when(itemId){
            R.id.action_settings->{ showAccountSettingPopupMenu() }
            R.id.action_chat_add->{
                startActivity(Intent(activity, AddChatActivity::class.java))
            }
        }
    }

    private fun showAccountSettingPopupMenu(){
        activity!!.findViewById<View>(R.id.action_settings)?.let { view ->
            PopupMenu(activity!!,view).apply {
                menu.add(getString(R.string.toolbar_account_edit))
                menu.add(getString(R.string.toolbar_account_setting))
                setOnMenuItemClickListener {menuItem ->
                    when(menuItem.title){
                        getString(R.string.toolbar_account_edit)->{
                            /**편집*/
                            showToast("편집")
                        }
                        getString(R.string.toolbar_account_setting)->{
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

    private fun socketForChatListUpdate(){
        if (socket == null) Log.e(TAG,"socket is null")
        socket?.apply {
            on(SocketReceive.CHAT_LIST_UPDATE,onChatListUpdate)
        }
    }

    private val onChatListUpdate = Emitter.Listener {
        activity?.runOnUiThread{
            viewModel.getChatRoomLists(myIdPref)
        }
    }

}