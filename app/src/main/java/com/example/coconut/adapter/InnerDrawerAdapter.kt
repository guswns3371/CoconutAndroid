package com.example.coconut.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.coconut.Constant
import com.example.coconut.IntentID
import com.example.coconut.R
import com.example.coconut.model.model.BaseItem
import com.example.coconut.model.response.account.UserDataResponse
import com.example.coconut.ui.main.account.info.AccountInfoActivity
import com.example.coconut.util.showToast

class InnerDrawerAdapter: BaseAdapter(){

    private val TAG = "InnerDrawerAdapter"
    private var items : List<UserDataResponse> = arrayListOf()

    private class ViewHolder{
        var userImageView : ImageView? = null
        var nameTextView : TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View
        val holder : ViewHolder
        val item = items[position]

        when(convertView == null){
            true->{
                view = LayoutInflater.from(parent!!.context).inflate(R.layout.inner_chat_drawer_item,null)
                holder = ViewHolder()
                holder.nameTextView = view.findViewById(R.id.inner_drawer_name)
                holder.userImageView = view.findViewById(R.id.inner_drawer_img)

                view.tag = holder
            }
            false->{
                holder = convertView.tag as ViewHolder
                view = convertView
            }
        }

        holder.userImageView?.let {
            Glide.with(parent!!.context)
                .load(Constant.BASE_URL+item.profile_image)
                .placeholder(R.drawable.account)
                .into(it)
        }

        holder.nameTextView?.text = item.name

        view.setOnClickListener {
            Intent(parent!!.context, AccountInfoActivity::class.java).apply {
                putExtra(IntentID.USER_RESPONSE,item)
                ContextCompat.startActivity(parent.context, this,null)
            }
        }

        return view
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
       return 0
    }

    override fun getCount(): Int {
       return items.size
    }

    fun addItemList(items : List<UserDataResponse>){
        this.items = items
        notifyDataSetChanged()
    }
}