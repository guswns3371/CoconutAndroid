package com.example.coconut.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coconut.IntentID
import com.example.coconut.R
import com.example.coconut.RoomType
import com.example.coconut.model.request.chat.ChatRoomNameChangeRequest
import com.example.coconut.model.response.account.UserDataResponse
import com.example.coconut.model.response.chat.ChatRoomInfoResponse
import com.example.coconut.model.response.chat.ChatRoomListResponse
import com.example.coconut.ui.main.chat.ChatViewModel
import com.example.coconut.ui.main.chat.inner.InnerChatActivity
import com.example.coconut.util.*
import kotlinx.android.synthetic.main.custom_dialog_default.*
import kotlinx.android.synthetic.main.item_chat_fragment.view.*

class ChatListRecyclerAdapter(
    private var pref: MyPreference,
    private var chatViewModel: ChatViewModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemLongClickListener(v: View, item: ChatRoomListResponse)
    }

    private var listener: OnItemClickListener? = null

    private var itemRoomList: ArrayList<ChatRoomListResponse> = arrayListOf()

    inner class ChatListHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_chat_fragment, parent, false)
    ) {
        private var roomName = ""
        private lateinit var roomInfo: ChatRoomInfoResponse
        private lateinit var userInfoList: ArrayList<UserDataResponse>
        private val userImageViews = arrayListOf<ArrayList<ImageView?>>(
            arrayListOf(
                itemView.chat_list_image11
            ),
            arrayListOf(
                itemView.chat_list_image21,
                itemView.chat_list_image22
            ),
            arrayListOf(
                itemView.chat_list_image31,
                itemView.chat_list_image32,
                itemView.chat_list_image33
            ),
            arrayListOf(
                itemView.chat_list_image41,
                itemView.chat_list_image42,
                itemView.chat_list_image43,
                itemView.chat_list_image44
            )
        )

        private val imageLayouts = arrayListOf<View?>(
            itemView.image_layout_for_one,
            itemView.image_layout_for_two,
            itemView.image_layout_for_three,
            itemView.image_layout_for_four
        )

        @SuppressLint("SetTextI18n")
        fun onBind(item: ChatRoomListResponse) {
            itemView.run {
                roomInfo = item.chatRoomInfo!!
                userInfoList = item.userInfo!!

                chat_list_name.text = item.chatRoomName
                chat_list_people_size.text = "${userInfoList.size + 1}"

                imageLayouts.forEach { it?.gone() }
                chat_list_people_size.hide()

                val userCount = if (userInfoList.size > 4) 4 else userInfoList.size

                if (roomInfo.roomType == RoomType.ME) {
                    chat_list_people_size.text = "ME"
                    chat_list_people_size.show()
                }

                if (userCount > 1) {
                    chat_list_people_size.show()
                }

                userImageViews[userCount - 1].forEachIndexed { index, image ->
                    Glide.with(context)
                        .load(userInfoList[index].profilePicture?.toHTTPString())
                        .placeholder(R.drawable.account)
                        .into(image!!)
                }

                imageLayouts[userCount - 1]?.show()

                chat_list_last_content.text = roomInfo.lastMessage
                chat_list_last_time.text = roomInfo.lastTime

                roomInfo.lastMessage?.run {
                    if (!isNullOrBlank()) {
                        chat_list_last_content.show()
                    } else {
                        chat_list_last_content.hide()
                    }
                }

                item.unReads?.run {
                    if (toInt() > 0) {
                        chat_list_unread_num.text = item.unReads
                        chat_list_unread_num.show()
                    } else {
                        chat_list_unread_num.hide()
                    }
                }


                setOnClickListener {
                    Intent(context, InnerChatActivity::class.java).apply {
                        putExtra(IntentID.CHAT_MODE, IntentID.CHAT_WITH_PEOPLE_FROM_CHAT_FRAG)
                        putExtra(IntentID.CHAT_ROOM_TITLE, item.chatRoomName)
                        putExtra(IntentID.CHAT_ROOM_PEOPLE_LIST, roomInfo.members)
                        putExtra(IntentID.CHAT_ROOM_ID, roomInfo.id)
                        putExtra(IntentID.CHAT_ROOM_PEOPLE_INFOS, userInfoList)
                        ContextCompat.startActivity(context, this, null)
                    }
                }

                setOnLongClickListener {
                    listener?.onItemLongClickListener(itemView, item)
                    false
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ChatListHolder(parent)

    override fun getItemCount(): Int = itemRoomList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ChatListHolder).onBind(itemRoomList[position])
    }

    fun setOnItemLongClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun addChatList(itemRoomList: ArrayList<ChatRoomListResponse>) {
        this.itemRoomList = itemRoomList
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        itemRoomList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemRoomList.size)
    }
}