package com.example.coconut.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coconut.R
import com.example.coconut.model.response.account.UserDataResponse
import com.example.coconut.util.gone
import com.example.coconut.util.show
import com.example.coconut.util.toHTTPString
import kotlinx.android.synthetic.main.item_account_fragment.view.*
import kotlinx.android.synthetic.main.item_add_chat.view.*

class AddChatRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = "AddChatRecyclerAdapter"
    private var itemList: ArrayList<UserDataResponse> = arrayListOf()
    private val addChatHorizonAdapter: AddChatHorizonAdapter = AddChatHorizonAdapter()

    fun getAddChatHorizonAdapter() = this.addChatHorizonAdapter

    fun getInvitingList(): ArrayList<String> {
        val list = arrayListOf<String>()
        this.invitingList.forEach {
            list.add(it.id)
        }
        return list
    }

    // 서버에 보낼 리스트
    private var invitingList: ArrayList<UserDataResponse> = arrayListOf()

    inner class AddChatHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_account_fragment, parent, false)
    ) {
        private val TAG = "AddChatHolder"
        fun onBind(
            item: UserDataResponse,
            invitingList: ArrayList<UserDataResponse>
        ) {
            itemView.run {
                Glide.with(context)
                    .load(item.profilePicture?.toHTTPString())
                    .placeholder(R.drawable.account)
                    .into(account_image)

                account_name.text = item.name
                account_user_status.gone()
                account_msg.gone()
                chat_add_button.show()

                chat_add_button.isClickable = false
                chat_add_button.isChecked = invitingList.contains(item)

                setOnClickListener {
                    when (chat_add_button.isChecked) {
                        true -> {
                            // 체크를 취소하고 invitingList에서 remove
                            chat_add_button.isChecked = false
                            invitingList.remove(item)
                        }
                        false -> {
                            // 체크를 설정하고 invitingList에 add
                            chat_add_button.isChecked = true
                            invitingList.add(item)
                        }
                    }
                    addChatHorizonAdapter.notifyDataSetChanged()
                    Log.e(TAG, "${invitingList.size}")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AddChatHolder(parent)

    override fun getItemCount(): Int = itemList.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AddChatHolder).onBind(itemList[position], invitingList)
    }

    fun addChatAddItem(itemList: ArrayList<UserDataResponse>) {
        Log.e(TAG, "addChatAddItem")
        this.itemList = itemList
        notifyDataSetChanged()
    }

    /** 가로 리사이클러뷰 **/
    inner class AddChatHorizonAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        inner class AddChatHorizonHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_add_chat, parent, false)
        ) {
            private val TAG = "AddChatHorizonHolder"
            fun onBind(
                item: UserDataResponse,
                invitingList: ArrayList<UserDataResponse>
            ) {
                itemView.run {
                    Log.e(TAG, item.name)
                    Glide.with(context)
                        .load(item.profilePicture?.toHTTPString())
                        .placeholder(R.drawable.account)
                        .into(chat_add_horiz_image)

                    chat_add_horiz_name.text = item.name

                    chat_add_horiz_delete.setOnClickListener {
                        invitingList.remove(item)
                        notifyDataSetChanged()
                        this@AddChatRecyclerAdapter.notifyDataSetChanged()
                        Log.e(TAG, "${invitingList.size}")
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            AddChatHorizonHolder(parent)

        override fun getItemCount(): Int = invitingList.size
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as AddChatHorizonHolder).onBind(invitingList[position], invitingList)
        }
    }
}


