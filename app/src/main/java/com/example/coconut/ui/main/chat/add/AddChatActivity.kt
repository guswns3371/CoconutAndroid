package com.example.coconut.ui.main.chat.add

import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coconut.IntentID
import com.example.coconut.R
import com.example.coconut.adapter.AddChatRecyclerAdpater
import com.example.coconut.adapter.AddChatRecyclerAdpater.AddChatHorizonAdapter
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

class AddChatActivity : BaseKotlinActivity<ActivityAddChatBinding,AccountViewModel>() {

    private val TAG = "AddChatActivity"
    override val layoutResourceId: Int = R.layout.activity_add_chat
    override var toolbar: Toolbar? = null

    private val pref : MyPreference by inject()
    override val viewModel: AccountViewModel by viewModel()
    private val recyclerAdapter : AddChatRecyclerAdpater by inject()
    private lateinit var recycleradapterHoriz : AddChatHorizonAdapter
    private lateinit var list : ArrayList<UserDataResponse>
    lateinit var myIdPref: String


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
            recycleradapterHoriz = recyclerAdapter.getAddChatHorizonAdapter()
            layoutManager = LinearLayoutManager(this@AddChatActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = recycleradapterHoriz
            setHasFixedSize(true)
        }

        myIdPref = pref.userID!!

        viewModel.getAllAccounts()
    }

    override fun initDataBinding() {
        viewModel.userDataResponseLiveData.observe(this, Observer {
            Log.e(TAG,"observing")
            list = arrayListOf()
            it.forEach { data ->
                if (data.id != myIdPref)
                    list.add(data)
            }
            recyclerAdapter.addChatAddItem(list)
        })
    }

    override fun initAfterBinding() {
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_add_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add->{ toInnerChatActivity() }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toInnerChatActivity(){
        recyclerAdapter.getInvitingList().let {
            if (it.size == 0){
                showToast("대화상대를 선택해주세요")
                return@let
            }
            Intent(this@AddChatActivity,InnerChatActivity::class.java).apply {
                putExtra(IntentID.CHAT_MODE,IntentID.CHAT_WITH_PEOPLE_FROM_INVITING)
                putExtra(IntentID.PEOPLE_IDS,it)
                startActivity(this)
                this@AddChatActivity.finish()
            }
        }

    }
}


