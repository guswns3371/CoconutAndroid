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
import com.example.coconut.model.response.account.UserDataResponse
import com.example.coconut.ui.main.account.info.AccountInfoActivity
import com.example.coconut.util.gone
import com.example.coconut.util.show
import kotlinx.android.synthetic.main.item_account_fragment.view.*
import kotlinx.android.synthetic.main.item_account_fragment_large.view.*
import kotlin.collections.ArrayList

class AccountRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object{
        private const val TYPE_MYSELF = 0
        private const val TYPE_FRIENDS = 1
    }

    private val TAG = "AccountRecyclerAdapter"
    private var itemList : ArrayList<UserDataResponse> = arrayListOf()
    private var statusList : Array<String> = arrayOf()

    class AccountHolder(parent: ViewGroup): RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_account_fragment,parent,false)
    ){
        private val TAG = "AccountHolder"
        fun onBind(item : UserDataResponse){
            itemView.run {
                val profilePicturePath =
                    if (item.profilePicture!!.startsWith("http")) item.profilePicture
                    else Constant.BASE_URL+item.profilePicture

                Glide.with(context)
                    .load(profilePicturePath)
                    .placeholder(R.drawable.account)
                    .into(account_image)

                account_name.text = item.name
                account_msg.text = item.stateMessage

                if (item.status){
                    account_user_status.background = context.getDrawable(R.drawable.online_sign)
                }else{
                    account_user_status.background = context.getDrawable(R.drawable.offline_sign)
                }


                item.stateMessage?.run {
                    when (this.isBlank()){
                        true->{ account_msg.gone() }
                        false->{ account_msg.show() }
                    }
                }

                setOnClickListener {
                    Log.e(TAG,item.toString())
                    Intent(context,AccountInfoActivity::class.java).apply {
                        putExtra(IntentID.USER_RESPONSE,item)
                        ContextCompat.startActivity(context, this,null)
                    }
                }
            }
        }
    }

    class AccountLargeHolder(parent: ViewGroup): RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_account_fragment_large,parent,false)
    ){
        private val TAG = "AccountLargeHolder"
        fun onBind(item : UserDataResponse){
            itemView.run {
                val profilePicturePath =
                    if (item.profilePicture!!.startsWith("http")) item.profilePicture
                    else Constant.BASE_URL+item.profilePicture

                Glide.with(context)
                    .load(profilePicturePath)
                    .placeholder(R.drawable.account)
                    .into(account_image_large)

                account_text_large.text = item.name
                account_msg_large.text = item.stateMessage

                item.stateMessage?.run {
                    when (this.isBlank() ||  this.isEmpty()){
                        true->{ account_msg_large.gone() }
                        false->{ account_msg_large.show() }
                    }
                }

                setOnClickListener {
                    Log.e(TAG,item.toString())
                    Intent(context,AccountInfoActivity::class.java).apply {
                        putExtra(IntentID.USER_RESPONSE,item)
                        ContextCompat.startActivity(context, this,null)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_MYSELF ->{
                AccountLargeHolder(parent)
            }
            TYPE_FRIENDS ->{
                AccountHolder(parent)
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is AccountHolder -> holder.onBind(itemList[position])
            is AccountLargeHolder -> holder.onBind(itemList[position])
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> TYPE_MYSELF
            else -> TYPE_FRIENDS
        }
    }

    fun addAccountItem(itemList: ArrayList<UserDataResponse>){
        Log.e(TAG,"addAccountItem")
        // 기존 list에는 유저 상태정보만 입력된 상태 ( socket으로 받은 정보가  rxjava로 받은 정보보다 빠르므로 꼬임)
        // 그래서 기존list의 유저 상태 정보를 rxjava에의해 받은 정보에 덮혀 씌운다
        itemList.forEach {
            it.status = this.statusList.contains(it.id)
        }
        this.itemList = itemList

        notifyDataSetChanged()
    }

    fun updateUserState(statusList : Array<String>){
        Log.e(TAG,"updateUserState")
        this.statusList = statusList
        // list에 유저 상태정보만 입력
        this.itemList.forEach {
            it.status = statusList.contains(it.id)
        }
        notifyDataSetChanged()
    }
}