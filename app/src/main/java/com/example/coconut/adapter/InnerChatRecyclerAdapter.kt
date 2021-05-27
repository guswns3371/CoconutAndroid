package com.example.coconut.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import kotlinx.android.synthetic.main.item_chat_fragment.view.*

class InnerChatRecyclerAdapter(private var pref: MyPreference) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemList: ArrayList<ChatHistoryResponse> = arrayListOf()
    private var fixedPeopleList: ArrayList<String> = arrayListOf()

    inner class InnerChatHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
    ) {
        private val imageLayoutsMine = arrayListOf<View?>(
            itemView.chat_image_layout_for_one_mine,
            itemView.chat_image_layout_for_two_mine,
            itemView.chat_image_layout_for_three_mine,
            itemView.chat_image_layout_for_four_mine
        )

        private val imageLayoutsNotMine = arrayListOf<View?>(
            itemView.chat_image_layout_for_one_nm,
            itemView.chat_image_layout_for_two_nm,
            itemView.chat_image_layout_for_three_nm,
            itemView.chat_image_layout_for_four_nm
        )

        private val userImageViewsMine = arrayListOf<ArrayList<ImageView?>>(
            arrayListOf(
                itemView.chat_content_image_mine11
            ),
            arrayListOf(
                itemView.chat_content_image_mine21,
                itemView.chat_content_image_mine22
            ),
            arrayListOf(
                itemView.chat_content_image_mine31,
                itemView.chat_content_image_mine32,
                itemView.chat_content_image_mine33
            ),
            arrayListOf(
                itemView.chat_content_image_mine41,
                itemView.chat_content_image_mine42,
                itemView.chat_content_image_mine43,
                itemView.chat_content_image_mine44
            )
        )

        private val userImageViewsNotMine = arrayListOf<ArrayList<ImageView?>>(
            arrayListOf(
                itemView.chat_content_image_nm11
            ),
            arrayListOf(
                itemView.chat_content_image_nm21,
                itemView.chat_content_image_nm22
            ),
            arrayListOf(
                itemView.chat_content_image_nm31,
                itemView.chat_content_image_nm32,
                itemView.chat_content_image_nm33
            ),
            arrayListOf(
                itemView.chat_content_image_nm41,
                itemView.chat_content_image_nm42,
                itemView.chat_content_image_nm43,
                itemView.chat_content_image_nm44
            )
        )

        fun onBind(item: ChatHistoryResponse) {
            itemView.run {
                val messageType = item.messageType
                if (messageType == MessageType.INFO) {
                    not_mine_linear.gone()
                    mine_linear.gone()
                    info_linear.show()

                    info_text.text = item.history
                    return
                }

                val userInfo = item.userInfo
                var count = fixedPeopleList.size - item.readMembers!!.toInt()

                count = if (count < 0) 0 else count
                when (userInfo.id == pref.userIdx) {
                    true -> {

                        imageLayoutsMine.forEach { it?.gone() }

                        // 유저편 view (내가 보낸 메시지)
                        not_mine_linear.gone()
                        info_linear.gone()
                        mine_linear.show()

                        //읽음표시
                        chat_read_count_mine.text = if (count != 0) "$count" else ""
                        chat_time_mine.text = item.time

                        when (messageType) {
                            MessageType.TEXT -> {
                                chat_content_text_mine.show()
                                chat_image_layout_for_mine.gone()
                                chat_content_text_mine.text = item.history
                            }
                            MessageType.IMAGE -> {
                                chat_content_text_mine.gone()
                                chat_image_layout_for_mine.show()

                                val imageCount =
                                    if (item.chatImages?.size!! > 4) 4 else item.chatImages?.size!!

                                userImageViewsMine[imageCount - 1].forEachIndexed { index, imageView ->
                                    Glide.with(context)
                                        .load(item.chatImages?.get(index)?.toHTTPString())
                                        .placeholder(R.drawable.black)
                                        .into(imageView!!)

                                    imageView.setOnClickListener {
                                        Intent(context, ZoomableImageActivity::class.java).run {
                                            putStringArrayListExtra(
                                                IntentID.CHAT_IMAGES,
                                                item.chatImages
                                            )
                                            putExtra(
                                                IntentID.CHAT_IMAGE_INDEX,
                                                index
                                            )
                                            ContextCompat.startActivity(context, this, null)
                                        }
                                    }
                                }

                                imageLayoutsMine[imageCount - 1]?.show()
                            }

                        }

                        chat_content_text_mine.setOnLongClickListener {
                            context.showToast("long clicked!")
                            false
                        }

                    }
                    false -> {
                        imageLayoutsNotMine.forEach { it?.gone() }

                        // 상대편 view (상대가 보낸 메시지)
                        not_mine_linear.show()
                        mine_linear.gone()
                        info_linear.gone()

                        Glide.with(context)
                            .load(userInfo.profilePicture?.toHTTPString())
                            .placeholder(R.drawable.account)
                            .into(chat_user_image_nm)

                        chat_user_name_nm.text = userInfo.name

                        //읽음표시
                        chat_read_count_nm.text = if (count != 0) "$count" else ""
                        chat_time_nm.text = item.time

                        when (messageType) {
                            MessageType.TEXT -> {
                                chat_content_text_nm.show()
                                chat_image_layout_for_nm.gone()
                                chat_content_text_nm.text = item.history
                            }
                            MessageType.IMAGE -> {
                                chat_content_text_nm.gone()
                                chat_image_layout_for_nm.show()

                                val imageCount =
                                    if (item.chatImages?.size!! > 4) 4 else item.chatImages?.size!!

                                userImageViewsNotMine[imageCount - 1].forEachIndexed { index, imageView ->
                                    Glide.with(context)
                                        .load(item.chatImages?.get(index)?.toHTTPString())
                                        .placeholder(R.drawable.black)
                                        .into(imageView!!)

                                    imageView.setOnClickListener {
                                        Intent(context, ZoomableImageActivity::class.java).run {
                                            putStringArrayListExtra(
                                                IntentID.CHAT_IMAGES,
                                                item.chatImages
                                            )
                                            putExtra(
                                                IntentID.CHAT_IMAGE_INDEX,
                                                index
                                            )
                                            ContextCompat.startActivity(context, this, null)
                                        }
                                    }
                                }

                                imageLayoutsNotMine[imageCount - 1]?.show()
                            }
                        }


                        chat_user_image_nm.setOnClickListener {
                            Intent(context, AccountInfoActivity::class.java).apply {
                                putExtra(IntentID.USER_RESPONSE, userInfo)
                                ContextCompat.startActivity(context, this, null)
                            }
                        }

                        chat_content_text_nm.setOnLongClickListener {
                            context.showToast("long clicked!")
                            false
                        }

                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        InnerChatHolder(parent)

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as InnerChatHolder).onBind(itemList[position])
    }

    fun addChatItem(itemList: ArrayList<ChatHistoryResponse>) {
        this.itemList = itemList
        notifyDataSetChanged()
    }

    fun setFixedPeopleList(fixedPeopleList: ArrayList<String>) {
        this.fixedPeopleList = fixedPeopleList
    }

    private fun stringToArrayList(string: String): ArrayList<String> {
        string.split(",").toTypedArray().run {
            val list = arrayListOf<String>()
            forEach { list.add(it) }
            return list
        }
    }

}