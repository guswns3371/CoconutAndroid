package com.example.coconut.ui.main.chat.add

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coconut.From
import com.example.coconut.IntentID
import com.example.coconut.R
import com.example.coconut.adapter.AddChatRecyclerAdapter
import com.example.coconut.adapter.AddChatRecyclerAdapter.AddChatHorizonAdapter
import com.example.coconut.base.BaseKotlinActivity
import com.example.coconut.databinding.ActivityAddChatBinding
import com.example.coconut.model.response.account.UserDataResponse
import com.example.coconut.ui.main.account.AccountViewModel
import com.example.coconut.ui.main.chat.inner.InnerChatActivity
import com.example.coconut.util.MyPreference
import com.example.coconut.util.showToast
import kotlinx.android.synthetic.main.activity_add_chat.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddChatActivity : BaseKotlinActivity<ActivityAddChatBinding, AccountViewModel>() {

    private val TAG = "AddChatActivity"
    override val layoutResourceId: Int = R.layout.activity_add_chat
    override var toolbar: Toolbar? = null

    private val pref: MyPreference by inject()
    override val viewModel: AccountViewModel by viewModel()
    private val recyclerAdapter: AddChatRecyclerAdapter by inject()
    private lateinit var horizonAdapter: AddChatHorizonAdapter
    private lateinit var list: ArrayList<UserDataResponse>
    lateinit var myIdPref: String
    private var removeList: ArrayList<String> = arrayListOf()


    override fun initStartView() {
        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = this

        toolbar = findViewById(R.id.chat_add_tool_bar)
        setToolbarTitle(getString(R.string.invite))

        chat_add_recycler_view.apply {
            // 세로
            layoutManager = LinearLayoutManager(this@AddChatActivity)
            adapter = recyclerAdapter
            setHasFixedSize(true)
        }

        chat_add_horiz_recycler_view.apply {
            // 가로
            horizonAdapter = recyclerAdapter.getAddChatHorizonAdapter()
            layoutManager =
                LinearLayoutManager(this@AddChatActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = horizonAdapter
            setHasFixedSize(true)
        }

        myIdPref = pref.userIdx!!

        viewModel.getAllAccounts()
    }

    override fun initDataBinding() {
        viewModel.userDataResponseLiveData.observe(this, {
            list = arrayListOf()
            it.forEach { data ->
                if (data.id != myIdPref && !removeList.contains(data.id))
                    list.add(data)
            }
            recyclerAdapter.addChatAddItem(list)
        })
    }

    override fun initAfterBinding() {
        intent.getStringArrayListExtra(IntentID.CHAT_ROOM_PEOPLE_LIST)?.let {
            removeList = it
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                afterAdded()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun afterAdded() {
        recyclerAdapter.getInvitingList().let {
            if (it.size == 0) {
                showToast("대화상대를 선택해주세요")
                return@let
            }

            intent.getStringExtra(From.WHERE)?.let { where ->
                when (where) {
                    From.CHAT_FRAGMENT -> {
                        Intent(this@AddChatActivity, InnerChatActivity::class.java).apply {
                            putExtra(IntentID.CHAT_MODE, IntentID.CHAT_WITH_PEOPLE_FROM_INVITING)
                            putExtra(IntentID.PEOPLE_IDS, it)
                            startActivity(this)
                            this@AddChatActivity.finish()
                        }
                    }
                    From.INNER_CHAT_ACTIVITY -> {
                        Intent(this@AddChatActivity, InnerChatActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            putExtra(IntentID.CHAT_MODE, IntentID.CHAT_WITH_PEOPLE_FROM_INVITING)
                            putExtra(IntentID.PEOPLE_IDS, it)
                            putExtra(IntentID.CHAT_ROOM_ID, intent.getStringExtra(IntentID.CHAT_ROOM_ID))
                            startActivity(this)
                            this@AddChatActivity.finish()
                        }
                    }
                    else -> { }
                }
            }

        }

    }
}


