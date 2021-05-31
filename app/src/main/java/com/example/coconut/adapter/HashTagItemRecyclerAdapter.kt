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
import com.example.coconut.model.response.hashtag.CovidDataResponse
import com.example.coconut.model.response.hashtag.MusicDataResponse
import com.example.coconut.model.response.hashtag.NewsDataResponse
import kotlinx.android.synthetic.main.item_covid.view.*
import kotlinx.android.synthetic.main.item_music.view.*
import kotlinx.android.synthetic.main.item_news.view.*

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
                val incidence = item.incidence
                val inspection = item.inspection

                covid_country.text = country
                covid_diff.text = diffFromPrevDay
                covid_total.text = total
                covid_death.text = death
                covid_incidence.text = incidence
                covid_inspection.text = inspection
            }
        }
    }

    inner class NewsItemHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
    ) {
        fun onBind(item: NewsDataResponse, pos: Int) {
            Log.e(TAG, "onBind: ")
            itemView.run {
                val newsUrl = item.newsUrl
                val thumbNailImage = item.thumbNailImage
                val title = item.title
                val newsName = item.newsName

                Glide.with(context)
                    .load(thumbNailImage)
                    .placeholder(R.drawable.newspaper)
                    .into(news_image)
                news_name.text = newsName
                news_title.text = title
                news_num.text = "${pos + 1}"

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
                music_num.text = "${pos + 1}"

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (pos) {
            HashTagRecyclerAdapter.TYPE_COVID -> CovidItemHolder(parent)
            HashTagRecyclerAdapter.TYPE_NEWS -> NewsItemHolder(parent)
            HashTagRecyclerAdapter.TYPE_MUSIC -> MusicItemHolder(parent)
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CovidItemHolder -> holder.onBind((itemList as ArrayList<CovidDataResponse>)[position])
            is NewsItemHolder -> holder.onBind(
                (itemList as ArrayList<NewsDataResponse>)[position],
                position
            )
            is MusicItemHolder -> holder.onBind(
                (itemList as ArrayList<MusicDataResponse>)[position],
                position
            )
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int {
        return when (pos) {
            HashTagRecyclerAdapter.TYPE_COVID -> (itemList as ArrayList<CovidDataResponse>).size
            HashTagRecyclerAdapter.TYPE_NEWS -> (itemList as ArrayList<NewsDataResponse>).size
            HashTagRecyclerAdapter.TYPE_MUSIC -> (itemList as ArrayList<MusicDataResponse>).size
            else -> throw IllegalArgumentException()
        }
    }

}