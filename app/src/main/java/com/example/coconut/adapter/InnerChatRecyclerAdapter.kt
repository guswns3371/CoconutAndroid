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
import com.example.coconut.R
import com.example.coconut.model.response.chat.ChatHistoryResponse
import com.example.coconut.ui.main.account.info.AccountInfoActivity
import com.example.coconut.util.MyPreference
import com.example.coconut.util.gone
import com.example.coconut.util.show
import com.example.coconut.util.showToast
import kotlinx.android.synthetic.main.item_chat.view.*

class InnerChatRecyclerAdapter(private var pref: MyPreference) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "InnerChatRecyclerAdapter"
    private var itemList : ArrayList<ChatHistoryResponse> = arrayListOf()
    private var readPeopleList : ArrayList<String> = arrayListOf()

    inner class InnerChatHolder(parent: ViewGroup): RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_chat,parent,false)
    ){
        private val TAG = "InnerChatHolder"
        fun onBind(item : ChatHistoryResponse){
            itemView.run {
                val userInfo = item.user_info

                when(userInfo.id == pref.UserId){
                    true->{
                        // 유저편 view (내가 보낸 메시지)
                        not_mine_linear.gone()
                        mine_linear.show()

                        //읽음표시
                        chat_read_count_mine.text = item.read_people
                        chat_time_mine.text = item.time

                        // isFile로 chat_content_image_mine VISIBLE로
                        chat_content_text_mine.text = item.chat_content
                        chat_content_image_mine.gone()

                        chat_content_text_mine.setOnLongClickListener {
                            context.showToast("long clicked!")
                            false
                        }
                    }
                    false->{
                        // 상대편 view (상대가 보낸 메시지)
                        not_mine_linear.show()
                        mine_linear.gone()
                        Glide.with(context)
                            .load(Constant.BASE_URL+userInfo.profile_image)
                            .placeholder(R.drawable.account)
                            .into(chat_user_image_nm)

                        chat_user_name_nm.text = userInfo.name

                        //읽음표시
                        chat_read_count_nm.text = item.read_people
                        chat_time_nm.text = item.time

                        // isFile로 chat_content_image_nm를 VISIBLE 로
                        chat_content_text_nm.text = item.chat_content
                        chat_content_image_nm.gone()

                        chat_user_image_nm.setOnClickListener {
                            Log.e(TAG,item.toString())
                            Intent(context, AccountInfoActivity::class.java).apply {
                                putExtra(IntentID.USER_RESPONSE,userInfo)
                                ContextCompat.startActivity(context, this,null)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = InnerChatHolder(parent)

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as InnerChatHolder).onBind(itemList[position])
    }

    fun addChatItem(itemList: ArrayList<ChatHistoryResponse>){
        Log.e(TAG,"addChatItem")
        this.itemList = itemList
        notifyDataSetChanged()
    }

    fun updateChatReadCount(readPeopleList : ArrayList<String>){
        Log.e(TAG,"updateChatReadCount")
        this.readPeopleList = readPeopleList
        notifyDataSetChanged()
    }

}