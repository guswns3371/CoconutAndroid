package com.example.coconut.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coconut.R
import com.example.coconut.util.toHTTPString
import kotlinx.android.synthetic.main.item_zoomable_image.view.*

class ZoomableRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemList: ArrayList<String> = arrayListOf()

    inner class ImageHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_zoomable_image, parent, false)
    ) {

        fun onBind(item: String) {
            itemView.run {
                Glide.with(context)
                    .load(item.toHTTPString())
                    .placeholder(R.drawable.black)
                    .into(zoomable_image_item)

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ImageHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ImageHolder).onBind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size

    fun setItemList(itemList: ArrayList<String>) {
        this.itemList = itemList
    }
}
