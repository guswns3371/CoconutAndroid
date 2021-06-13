package com.example.coconut.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coconut.R
import com.example.coconut.model.response.hashtag.*
import kotlinx.android.synthetic.main.item_covid.view.*
import kotlinx.android.synthetic.main.item_job.view.*
import kotlinx.android.synthetic.main.item_music.view.*
import kotlinx.android.synthetic.main.item_news.view.*
import kotlinx.android.synthetic.main.item_notice.view.*

class HashTagItemRecyclerAdapter(
    val context: Context,
    private val pos: Int,
    private val itemList: Any,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TAG = "HashTagItemRecyclerAdapter"

    inner class CovidItemHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_covid, parent, false)
    ) {
        fun onBind(item: CovidDataResponse) {
            Log.e(TAG, "onBind: ")
            itemView.run {
                val country = item.country
                val diffFromPrevDay = item.diffFromPrevDay
                val total = item.total
                val death = item.death
                val inspection = item.inspection

                covid_country.text = country
                covid_diff.text = diffFromPrevDay
                covid_total.text = total
                covid_death.text = death
                covid_inspection.text = inspection
            }
        }
    }

    inner class NewsItemHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
    ) {
        fun onBind(item: NewsDataResponse) {
            Log.e(TAG, "onBind: ")
            itemView.run {
                val newsUrl = item.newsUrl
                val thumbNailImage = item.thumbNailImage
                val title = item.title
                val newsName = item.newsName
                val siteName = item.siteName

                Glide.with(context)
                    .load(thumbNailImage)
                    .placeholder(R.drawable.newspaper)
                    .into(news_image)

                news_name.text = newsName
                news_title.text = title
                news_site_name.text = siteName

                setOnClickListener {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(newsUrl)
                        )
                    )
                }
            }
        }
    }

    inner class MusicItemHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false)
    ) {
        fun onBind(item: MusicDataResponse, pos: Int) {
            Log.e(TAG, "onBind: ")
            itemView.run {
                val albumImage = item.albumImage
                val albumTitle = item.albumTitle
                val artist = item.artist
                val songTitle = item.songTitle

                Glide.with(context)
                    .load(albumImage)
                    .placeholder(R.drawable.black)
                    .into(music_image)

                music_album_title.text = albumTitle
                music_artist.text = artist
                music_song_title.text = songTitle
                music_num.text = "${pos + 1}위"

                setOnClickListener {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW)
                            .setData(Uri.parse("https://www.youtube.com/results?search_query=${artist}+${songTitle}"))
                            .setPackage("com.google.android.youtube")
                    )
                }
            }
        }
    }

    inner class NoticeItemHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_notice, parent, false)
    ) {
        fun onBind(item: NoticeDataResponse) {
            Log.e(TAG, "onBind: ")
            itemView.run {
                val link = item.link
                val author = item.author
                val date = item.date
                val title = item.title

                notice_title.text = title
                notice_author.text = author
                notice_date.text = date

                setOnClickListener {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(link)
                        )
                    )
                }
            }
        }
    }

    inner class JobItemHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_job, parent, false)
    ) {
        fun onBind(item: JobDataResponse) {
            Log.e(TAG, "onBind: ")
            itemView.run {
                val jobTitle = item.jobTitle
                val jobLink = item.jobLink
                val companyImage = item.companyImage
                val companyName = item.companyName
                val career = item.career
                val location = item.location
                val position = item.position

                Glide.with(context)
                    .load(companyImage)
                    .placeholder(R.drawable.black)
                    .into(job_image)

                job_title.text = jobTitle
                job_company_name.text = companyName
                job_career.text = career
                job_location.text = location
                job_position.text = position

                setOnClickListener {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(jobLink)
                        )
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (pos) {
            HashTagRecyclerAdapter.TYPE_COVID -> CovidItemHolder(parent)
            HashTagRecyclerAdapter.TYPE_NEWS -> NewsItemHolder(parent)
            HashTagRecyclerAdapter.TYPE_MUSIC -> MusicItemHolder(parent)
            HashTagRecyclerAdapter.TYPE_NOTICE -> NoticeItemHolder(parent)
            HashTagRecyclerAdapter.TYPE_JOB -> JobItemHolder(parent)
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CovidItemHolder -> holder.onBind((itemList as ArrayList<CovidDataResponse>)[position])
            is NewsItemHolder -> holder.onBind((itemList as ArrayList<NewsDataResponse>)[position])
            is MusicItemHolder -> holder.onBind(
                (itemList as ArrayList<MusicDataResponse>)[position],
                position
            )
            is NoticeItemHolder -> holder.onBind((itemList as ArrayList<NoticeDataResponse>)[position])
            is JobItemHolder -> holder.onBind((itemList as ArrayList<JobDataResponse>)[position])
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int {
        return when (pos) {
            HashTagRecyclerAdapter.TYPE_COVID -> (itemList as ArrayList<CovidDataResponse>).size
            HashTagRecyclerAdapter.TYPE_NEWS -> (itemList as ArrayList<NewsDataResponse>).size
            HashTagRecyclerAdapter.TYPE_MUSIC -> (itemList as ArrayList<MusicDataResponse>).size
            HashTagRecyclerAdapter.TYPE_NOTICE -> (itemList as ArrayList<NoticeDataResponse>).size
            HashTagRecyclerAdapter.TYPE_JOB -> (itemList as ArrayList<JobDataResponse>).size
            else -> throw IllegalArgumentException()
        }
    }

}