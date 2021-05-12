package com.example.coconut.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coconut.Constant
import com.example.coconut.IntentID
import com.example.coconut.MessageType
import com.example.coconut.R
import com.example.coconut.model.response.chat.ChatHistoryResponse
import com.example.coconut.ui.ZoomableImageActivity
import com.example.coconut.ui.main.account.info.AccountInfoActivity
import com.example.coconut.util.*
import kotlinx.android.synthetic.main.item_chat.view.*

class InnerChatRecyclerAdapter(private var pref: MyPreference) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "InnerChatRecyclerAdapter"
    private var itemList : ArrayList<ChatHistoryResponse> = arrayListOf()
    private var fixedPeopleList : ArrayList<String> = arrayListOf()

    inner class InnerChatHolder(parent: ViewGroup): RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_chat,parent,false)
    ){
        private val TAG = "InnerChatHolder"
        fun onBind(item : ChatHistoryResponse){
            itemView.run {
                val userInfo = item.userInfo
                val messageType = item.messageType
                val count = fixedPeopleList.size - item.readMembers.toInt()
                when(userInfo.id == pref.userIdx){
                    true->{
                        // 유저편 view (내가 보낸 메시지)
                        not_mine_linear.gone()
                        mine_linear.show()

                        //읽음표시
                        chat_read_count_mine.text = if (count !=0) "$count" else ""

                        chat_time_mine.text = item.time

                        if (messageType == MessageType.IMAGE) {
                            chat_content_text_mine.gone()
                            chat_content_image_mine.show()
                            Glide.with(context)
                                .load(item.chatImages?.get(0)?.toHTTPString())
                                .placeholder(R.drawable.account)
                                .into(chat_content_image_mine)
                        } else if (messageType == MessageType.TEXT) {
                            chat_content_text_mine.show()
                            chat_content_image_mine.gone()
                            chat_content_text_mine.text = item.history
                        }

                        chat_content_image_mine.setOnClickListener {
                            Intent(context, ZoomableImageActivity::class.java).run {
                                putExtra(IntentID.USER_IMAGE, item.chatImages?.get(0))
                                ContextCompat.startActivity(context, this,null)
                            }
                        }

                        chat_content_text_mine.setOnLongClickListener {
                            context.showToast("long clicked!")
                            false
                        }

                        chat_content_image_mine.setOnLongClickListener {
                            context.showToast("long clicked!")
                            false
                        }
                    }
                    false->{
                        // 상대편 view (상대가 보낸 메시지)
                        not_mine_linear.show()
                        mine_linear.gone()
                        Glide.with(context)
                            .load(userInfo.profilePicture?.toHTTPString())
                            .placeholder(R.drawable.account)
                            .into(chat_user_image_nm)

                        chat_user_name_nm.text = userInfo.name

                        //읽음표시
                        chat_read_count_nm.text = if (count != 0) "$count" else ""

                        chat_time_nm.text = item.time

                        if (messageType == MessageType.IMAGE) {
                            chat_content_text_nm.gone()
                            chat_content_image_nm.show()
                            Glide.with(context)
                                .load(item.chatImages?.get(0)?.toHTTPString())
                                .placeholder(R.drawable.account)
                                .into(chat_content_image_nm)
                        } else if (messageType == MessageType.TEXT) {
                            chat_content_text_nm.show()
                            chat_content_image_nm.gone()
                            chat_content_text_nm.text = item.history
                        }

                        chat_user_image_nm.setOnClickListener {
                            Log.e(TAG,item.toString())
                            Intent(context, AccountInfoActivity::class.java).apply {
                                putExtra(IntentID.USER_RESPONSE,userInfo)
                                ContextCompat.startActivity(context, this,null)
                            }
                        }

                        chat_content_image_nm.setOnClickListener {
                            Intent(context, ZoomableImageActivity::class.java).run {
                                putExtra(IntentID.USER_IMAGE, item.chatImages?.get(0))
                                ContextCompat.startActivity(context, this,null)
                            }
                        }

                        chat_content_text_nm.setOnLongClickListener {
                            context.showToast("long clicked!")
                            false
                        }

                        chat_content_image_nm.setOnLongClickListener {
                            context.showToast("long clicked!")
                            false
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = InnerChatHolder(parent)

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as InnerChatHolder).onBind(itemList[position])
    }

    fun addChatItem(itemList: ArrayList<ChatHistoryResponse>){
        this.itemList = itemList
        notifyDataSetChanged()
    }

    fun setFixedPeopleList(fixedPeopleList: ArrayList<String>){
        this.fixedPeopleList = fixedPeopleList
    }

    private fun stringToArrayList(string : String) : ArrayList<String> {
        string.split(",").toTypedArray().run{
            val list = arrayListOf<String>()
            forEach { list.add(it) }
             return list
        }
    }

}