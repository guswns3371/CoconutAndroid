package com.example.coconut.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coconut.Constant.Companion.COVID
import com.example.coconut.Constant.Companion.JOB
import com.example.coconut.Constant.Companion.KOREA_COVID_DATA_URL
import com.example.coconut.Constant.Companion.MUSIC
import com.example.coconut.Constant.Companion.MUSIC_DATA_URL
import com.example.coconut.Constant.Companion.NEWS
import com.example.coconut.Constant.Companion.NEWS_DATA_URL
import com.example.coconut.Constant.Companion.NOTICE
import com.example.coconut.Constant.Companion.PROGRAMMERS_JOB_URL
import com.example.coconut.Constant.Companion.SEOULTECH_NOTICE_URL
import com.example.coconut.R
import com.example.coconut.model.response.account.UserDataResponse
import com.example.coconut.model.response.hashtag.*
import kotlinx.android.synthetic.main.item_hash_tag.view.*
import org.koin.android.ext.android.inject

class HashTagRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_NEWS = 0
        const val TYPE_COVID = 1
        const val TYPE_MUSIC = 2
        const val TYPE_NOTICE = 3
        const val TYPE_JOB = 4
    }

    private var titleList = arrayListOf(NEWS, COVID, MUSIC, NOTICE, JOB)
    private var linkList = arrayListOf(
        NEWS_DATA_URL,
        KOREA_COVID_DATA_URL,
        MUSIC_DATA_URL,
        SEOULTECH_NOTICE_URL,
        PROGRAMMERS_JOB_URL
    )
    private var itemList: ArrayList<Any> =
        arrayListOf(
            arrayListOf<Any>(),
            arrayListOf<Any>(),
            arrayListOf<Any>(),
            arrayListOf<Any>(),
            arrayListOf<Any>()
        )

    inner class HashTagHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_hash_tag, parent, false)
    ) {
        fun onBind(itemList: Any, pos: Int) {
            itemView.run {

                hash_item_title_txt.text = titleList[pos]
                hash_item_title_dummy.text = titleList[pos]
                hash_item_title_txt.setOnClickListener {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(linkList[pos])))
                }
                hash_item_recycler_view.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = HashTagItemRecyclerAdapter(context, pos, itemList)
                    setHasFixedSize(true)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        HashTagHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as HashTagHolder).onBind(itemList[position], position)
    }

    override fun getItemCount() = itemList.size

    fun setNewsList(newsList: ArrayList<NewsDataResponse>) {
        this.itemList[TYPE_NEWS] = newsList
        notifyDataSetChanged()
    }

    fun setCovidList(covidList: ArrayList<CovidDataResponse>) {
        this.itemList[TYPE_COVID] = covidList
        notifyDataSetChanged()
    }

    fun setMusicList(musicList: ArrayList<MusicDataResponse>) {
        this.itemList[TYPE_MUSIC] = musicList
        notifyDataSetChanged()
    }

    fun setNoticeList(noticeList: ArrayList<NoticeDataResponse>) {
        this.itemList[TYPE_NOTICE] = noticeList
        notifyDataSetChanged()
    }

    fun setJobList(jobList: ArrayList<JobDataResponse>) {
        this.itemList[TYPE_JOB] = jobList
        notifyDataSetChanged()
    }

}