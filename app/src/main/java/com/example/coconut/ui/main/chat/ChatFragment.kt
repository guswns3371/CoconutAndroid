package com.example.coconut.ui.main.chat

import android.app.Dialog
import android.content.*
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coconut.BroadCastIntentID
import com.example.coconut.From
import com.example.coconut.IntentID
import com.example.coconut.R
import com.example.coconut.adapter.ChatListRecyclerAdapter
import com.example.coconut.base.BaseKotlinFragment
import com.example.coconut.base.BroadcastReceiverManager
import com.example.coconut.base.SocketServiceManager
import com.example.coconut.databinding.FragmentChatBinding
import com.example.coconut.model.request.chat.ChatRoomExitRequest
import com.example.coconut.model.request.chat.ChatRoomNameChangeRequest
import com.example.coconut.model.response.chat.ChatRoomListResponse
import com.example.coconut.service.SocketService
import com.example.coconut.ui.OnFragmentInteractionListener
import com.example.coconut.ui.main.chat.add.AddChatActivity
import com.example.coconut.ui.setting.SettingActivity
import com.example.coconut.util.*
import io.socket.client.Socket
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.gmail.bishoybasily.stomp.lib.StompClient
import kotlinx.android.synthetic.main.custom_dialog_default.*

class ChatFragment : BaseKotlinFragment<FragmentChatBinding, ChatViewModel>(),
    SocketServiceManager, BroadcastReceiverManager {

    private val TAG = "ChatFragment"
    override val layoutResourceId: Int
        get() = R.layout.fragment_chat
    override val viewModel: ChatViewModel by viewModel()
    private val recyclerAdapter: ChatListRecyclerAdapter by inject()

    private val pref: MyPreference by inject()
    override val baseToolBar: Toolbar?
        get() = activity?.findViewById(R.id.baseToolBar)

    lateinit var myIdPref: String
    override var isBind: Boolean = false
    override var socket: Socket? = null
    override var stompClient: StompClient? = null
    private var listener: OnFragmentInteractionListener? = null

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
                    listener?.onFragmentReload()
                    if (isBind)
                        socketForChatListUpdate()
                }
                BroadCastIntentID.SEND_ON_DISCONNECT -> {
                }
                BroadCastIntentID.SEND_ON_ERROR -> {
                }
                BroadCastIntentID.SEND_ON_FCM_PUSH -> {
                }
                else -> {
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener)
            listener = context
    }

    override fun initStartView() {
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = this

        myIdPref = pref.userIdx!!

        setToolbarTitle(getString(R.string.title_chat))
        viewDataBinding.root.findViewById<RecyclerView>(R.id.chat_recycler_view).apply {
            layoutManager = LinearLayoutManager(activity!!)
            adapter = recyclerAdapter.apply {

                setOnItemLongClickListener(object : ChatListRecyclerAdapter.OnItemClickListener {
                    override fun onItemLongClickListener(v: View, item: ChatRoomListResponse) {
                        Dialog(activity!!).apply {
                            setContentView(R.layout.custom_dialog_list)
                            val titleList = arrayOf("채팅방 나가기", "채팅방 이름 바꾸기")
                            val arrayAdapter = ArrayAdapter<String>(
                                context,
                                R.layout.item_dialog_simple_list,
                                R.id.dialog_item_text,
                                titleList
                            )
                            findViewById<ListView>(R.id.dialog_list_view).apply {
                                adapter = arrayAdapter
                                setOnItemClickListener { parent, view, position, id ->
                                    cancel()
                                    when (position) {
                                        0 -> {
                                            Dialog(activity!!).apply {

                                                setContentView(R.layout.custom_dialog_default)
                                                setCancelable(false)
                                                show()

                                                dialog_title.text = "정말 나가시겠습니까?"
                                                dialog_edit_textinput.gone()
                                                dialog_content.gone()

                                                dialog_negative.setOnClickListener { dismiss() }

                                                dialog_positive.setOnClickListener {
                                                    viewModel.exitChatRoom(
                                                        ChatRoomExitRequest(
                                                            item.chatRoomInfo?.id,
                                                            pref.userIdx
                                                        )
                                                    )
                                                    dismiss()
                                                }
                                            }
                                        }
                                        1 -> {
                                            Dialog(activity!!).apply {

                                                setContentView(R.layout.custom_dialog_default)
                                                setCancelable(false)
                                                show()

                                                dialog_title.text = titleList[1]
                                                dialog_edit_text.text =
                                                    item.chatRoomName?.toEditable()
                                                dialog_content.gone()
                                                dialog_reset.show()

                                                dialog_negative.setOnClickListener { dismiss() }

                                                dialog_reset.setOnClickListener {
                                                    dialog_edit_text.text.toString().let { _ ->
                                                        changeChatRoomName(null, item.chatRoomInfo?.id)
                                                        dismiss()
                                                    }
                                                }

                                                dialog_positive.setOnClickListener {
                                                    dialog_edit_text.text.toString().let { text ->
                                                        changeChatRoomName(text, item.chatRoomInfo?.id)
                                                        dismiss()
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                            setCancelable(true)
                            show()
                        }
                    }
                })
            }
            setHasFixedSize(true)
        }


    }

    private fun changeChatRoomName(text: String?, chatRoomId: String?) {
        viewModel.changeChatRoomName(
            ChatRoomNameChangeRequest(
                text,
                chatRoomId,
                pref.userIdx
            )
        )
    }

    override fun initDataBinding() {
        viewModel.chatRoomListResponseLiveData.observe(this, {
            recyclerAdapter.addChatList(it)
        })

        viewModel.chatChangeResponseLiveData.observe(this, {
            if (it) {
                viewModel.getChatRoomLists(myIdPref)
            } else {
                showToast("채팅방 업데이트 오류")
            }
        })

    }

    override fun initAfterBinding() {
        bindService(activity)
    }

    private fun socketForChatListUpdate() {
        if (stompClient == null) Log.e(TAG, "onStompClient is null")
        stompClient?.apply {

            clearDisposable()

            // 안읽은 메시지가 있는 경우
            addDisposable(this.join("/sub/chat/frag/$myIdPref")
                .doOnError { error -> Log.e(TAG, "socketForChatListUpdate error: $error") }
                .subscribe {
                    Log.e(TAG, "socketForChatListUpdate: $it")
                    viewModel.getChatRoomLists(myIdPref)
                })
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getChatRoomLists(myIdPref)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver()
    }

    override fun onStop() {
        Log.e(TAG, "onStop")
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbindService(activity)
        isBind = false
    }

    private fun registerReceiver() {
        IntentFilter(BroadCastIntentID.SEND_ON_CONNECT).let {
            it.addAction(BroadCastIntentID.SEND_ON_DISCONNECT)
            it.addAction(BroadCastIntentID.SEND_ON_ERROR)
            it.addAction(BroadCastIntentID.SEND_ON_FCM_PUSH)
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
                Intent(activity, AddChatActivity::class.java).apply {
                    putExtra(From.WHERE, From.CHAT_FRAGMENT)
                    startActivity(this)
                }
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

    /**
    private val onChatListUpdate = Emitter.Listener {
    activity?.runOnUiThread {
    viewModel.getChatRoomLists(myIdPref)
    }
    }
     **/

}