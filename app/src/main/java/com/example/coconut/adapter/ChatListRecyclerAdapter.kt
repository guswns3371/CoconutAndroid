package com.example.coconut.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coconut.Constant
import com.example.coconut.IntentID
import com.example.coconut.R
import com.example.coconut.model.response.account.UserDataResponse
import com.example.coconut.model.response.chat.ChatRoomInfoResponse
import com.example.coconut.model.response.chat.ChatRoomListResponse
import com.example.coconut.ui.main.chat.inner.InnerChatActivity
import com.example.coconut.util.*
import kotlinx.android.synthetic.main.item_chat_fragment.view.*

class ChatListRecyclerAdapter(private var pref: MyPreference) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "ChatListRecyclerAdapter"
    private var itemRoomList : ArrayList<ChatRoomListResponse> = arrayListOf()

     inner class ChatListHolder(parent: ViewGroup): RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_chat_fragment,parent,false)
    ){
        private val TAG = "ChatListHolder"
        private var roomName = ""
        private lateinit var roomInfoInfo : ChatRoomInfoResponse
        private lateinit var userInfos : ArrayList<UserDataResponse>

        @SuppressLint("SetTextI18n")
        fun onBind(item : ChatRoomListResponse){
            itemView.run {
                roomInfoInfo = item.chatRoomInfo!!
                userInfos = item.userInfo!!

                chat_list_name.text = item.chatRoomName
                chat_list_people_size.text = "${userInfos.size+1}"

                image_layout_for_one.gone()
                image_layout_for_two.gone()
                image_layout_for_three.gone()
                image_layout_for_four.gone()

                when(userInfos.size){
                    1->{
                        //1명

                        Glide.with(context)
                            .load(userInfos[0].profilePicture?.toHTTPString())
                            .placeholder(R.drawable.account)
                            .into(chat_list_image11)

                        chat_list_people_size.hide()
                        if (userInfos[0].id == pref.userIdx){
                            chat_list_people_size.text = "ME"
                            chat_list_people_size.show()
                        }
                        image_layout_for_one.show()

                    }
                    2->{
                        //2명
                        chat_list_people_size.show()

                        Glide.with(context)
                            .load(userInfos[0].profilePicture?.toHTTPString())
                            .placeholder(R.drawable.account)
                            .into(chat_list_image21)

                        Glide.with(context)
                            .load(userInfos[1].profilePicture?.toHTTPString())
                            .placeholder(R.drawable.account)
                            .into(chat_list_image22)

                        image_layout_for_two.show()

                    }
                    3->{
                        //3명
                        chat_list_people_size.show()

                        Glide.with(context)
                            .load(userInfos[0].profilePicture?.toHTTPString())
                            .placeholder(R.drawable.account)
                            .into(chat_list_image31)
                        Glide.with(context)
                            .load(userInfos[1].profilePicture?.toHTTPString())
                            .placeholder(R.drawable.account)
                            .into(chat_list_image32)
                        Glide.with(context)
                            .load(userInfos[2].profilePicture?.toHTTPString())
                            .placeholder(R.drawable.account)
                            .into(chat_list_image33)

                        image_layout_for_three.show()
                    }
                    else->{
                        //4명 이상 (자신 미포함)
                        chat_list_people_size.show()

                        Glide.with(context)
                            .load(userInfos[0].profilePicture?.toHTTPString())
                            .placeholder(R.drawable.account)
                            .into(chat_list_image41)

                        Glide.with(context)
                            .load(userInfos[1].profilePicture?.toHTTPString())
                            .placeholder(R.drawable.account)
                            .into(chat_list_image42)

                        Glide.with(context)
                            .load(userInfos[2].profilePicture?.toHTTPString())
                            .placeholder(R.drawable.account)
                            .into(chat_list_image43)

                        Glide.with(context)
                            .load(userInfos[3].profilePicture?.toHTTPString())
                            .placeholder(R.drawable.account)
                            .into(chat_list_image44)

                        image_layout_for_four.show()

                    }
                }

                chat_list_last_content.text = roomInfoInfo.lastMessage
                chat_list_last_time.text = roomInfoInfo.lastTime

                roomInfoInfo.lastMessage?.run {
                    if (!isNullOrBlank()){
                        chat_list_last_content.show()
                    }else{
                        chat_list_last_content.hide()
                    }
                }

                item.unReads?.run {
                    if (toInt() > 0) {
                        chat_list_unread_num.text = item.unReads
                        chat_list_unread_num.show()
                    }else{
                        chat_list_unread_num.hide()
                    }
                }

                setOnClickListener {
                    Intent(context,InnerChatActivity::class.java).apply {
                        putExtra(IntentID.CHAT_MODE,IntentID.CHAT_WITH_PEOPLE_FROM_CHAT_FRAG)
                        putExtra(IntentID.CHAT_ROOM_TITLE,item.chatRoomName)
                        putExtra(IntentID.CHAT_ROOM_PEOPLE_LIST,roomInfoInfo.members)
                        putExtra(IntentID.CHAT_ROOM_ID,roomInfoInfo.id)
                        putExtra(IntentID.CHAT_ROOM_PEOPLE_INFOS,userInfos)
                        ContextCompat.startActivity(context, this,null)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = ChatListHolder(parent)

    override fun getItemCount(): Int = itemRoomList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChatListHolder).onBind(itemRoomList[position])
    }


    fun addChatList(itemRoomList : ArrayList<ChatRoomListResponse>){
        this.itemRoomList = itemRoomList
        notifyDataSetChanged()
    }
}