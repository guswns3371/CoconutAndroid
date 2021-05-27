package com.example.coconut.ui.main.account

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
import com.example.coconut.BroadCastIntentID
import com.example.coconut.IntentID
import com.example.coconut.R
import com.example.coconut.adapter.AccountRecyclerAdapter
import com.example.coconut.base.BaseKotlinFragment
import com.example.coconut.base.BroadcastReceiverManager
import com.example.coconut.base.SocketServiceManager
import com.example.coconut.databinding.FragmentAccountBinding
import com.example.coconut.model.response.account.UserDataResponse
import com.example.coconut.service.SocketService
import com.example.coconut.ui.OnFragmentInteractionListener
import com.example.coconut.ui.auth.login.LoginActivity
import com.example.coconut.ui.auth.login.LoginViewModel
import com.example.coconut.ui.setting.SettingActivity
import com.example.coconut.util.MyPreference
import com.gmail.bishoybasily.stomp.lib.StompClient
import io.socket.client.Socket
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountFragment : BaseKotlinFragment<FragmentAccountBinding, AccountViewModel>(),
    SocketServiceManager, BroadcastReceiverManager {

    private val TAG = "AccountFragment"
    override val layoutResourceId: Int = R.layout.fragment_account
    override val viewModel: AccountViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()
    private lateinit var list: ArrayList<UserDataResponse>
    private val recyclerAdapter: AccountRecyclerAdapter by inject()

    private val pref: MyPreference by inject()

    override var isBind: Boolean = false
    override var socket: Socket? = null
    override var stompClient: StompClient? = null
    private var listener: OnFragmentInteractionListener? = null

    override val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            when (p1?.action) {
                BroadCastIntentID.SEND_USER_LIST -> {
                    val userList =
                        p1.getSerializableExtra(IntentID.RECEIVE_USER_LIST) as ArrayList<String>?
                    Log.e(TAG, "onReceive: SEND_BROADCAST : $userList")
                    recyclerAdapter.updateUserStateArrayList(userList)
                }
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
            socket = binder.getService().getSocket()
            stompClient = binder.getService().getStompClient()
            isBind = true
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

        setToolbarTitle(getString(R.string.title_account))
        viewDataBinding.root.findViewById<RecyclerView>(R.id.account_recycler_view).apply {
            layoutManager = LinearLayoutManager(activity!!)
            adapter = recyclerAdapter
            setHasFixedSize(true)
        }
    }

    override fun initDataBinding() {
        viewModel.userDataResponseLiveData.observe(this, Observer {
            Log.e(TAG, "observing")
            list = arrayListOf()
            it.forEach { data ->
                // 에러 메시지 출력
                data.err?.run {
                    //존재하지 않은 회원일경우
                    // => pref에서 id정보 삭제
                    // => LoginActivity로 전환
                    showToast(this)
                    Log.e(TAG, this)
                    pref.resetUserId()
                    pref.resetFcmToken()
                    val intent = Intent(activity!!, LoginActivity::class.java)
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or //이걸 해줘야 fragment도 같이 pop
                                Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                    startActivity(intent)
                }
                list.add(data)
            }
            recyclerAdapter.addAccountItem(list)
        })
    }

    override fun initAfterBinding() {
        bindService(activity)
        loginViewModel.sendFcmTokenToServer(pref.userIdx!!, pref.fcmToken!!)
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "onStart")
        viewModel.getAllAccounts()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(activity)
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
                menu.add(getString(R.string.toolbar_account_edit))
                menu.add(getString(R.string.toolbar_account_friends))
                menu.add(getString(R.string.toolbar_account_setting))
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.title) {
                        getString(R.string.toolbar_account_edit) -> {
                            /**편집*/
                            showToast("편집")
                        }
                        getString(R.string.toolbar_account_friends) -> {
                            /**친구관리*/
                            showToast("친구관리")
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
        menu.findItem(R.id.action_chat_add).isVisible = false
    }

    private fun registerReceiver() {
        IntentFilter(BroadCastIntentID.SEND_USER_LIST).let {
            it.addAction(BroadCastIntentID.SEND_ON_CONNECT)
            it.addAction(BroadCastIntentID.SEND_ON_DISCONNECT)
            it.addAction(BroadCastIntentID.SEND_ON_ERROR)
            registerBroadcastReceiver(activity!!, it)
        }
    }

    private fun unregisterReceiver() {
        unregisterBroadcastReceiver(activity)
    }


    /**
    fun socketForUserStatus() {
    if (socket == null) Log.e(TAG, "socket is null")
    socket?.apply {
    on(SocketReceive.ONLINE_USER, onConnectedUser)
    on(SocketReceive.OFFLINE_USER, onDisconnectedUser)
    }
    }

    private val onConnectedUser = Emitter.Listener {
    activity?.runOnUiThread {
    (it[0] as String).apply {
    this.split(",").toTypedArray().run {
    Log.e("$TAG who is on now [conn]", this.showElements())
    recyclerAdapter.updateUserState(this)
    }
    }
    }
    }

    private val onDisconnectedUser = Emitter.Listener {
    activity?.runOnUiThread {
    (it[0] as JSONObject).apply {
    val whoIsOn = getString(SocketReceive.OFFLINE_USER_WHOISON)
    //Log.e("$TAG who is on until",whoIsOn)
    //Log.e("$TAG who is out now [disconn]",getString(SocketReceive.OFFLINE_USER_DISCONNECTED))
    whoIsOn.split(",").toTypedArray().run {
    Log.e("$TAG who is on until [disconn]", this.showElements())
    recyclerAdapter.updateUserState(this)
    }
    }
    }
    }
     **/

}