package com.example.coconut.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coconut.IntentID
import com.example.coconut.R
import com.example.coconut.adapter.ZoomableRecyclerAdapter
import kotlinx.android.synthetic.main.activity_account_info.*
import kotlinx.android.synthetic.main.activity_inner_chat.*
import kotlinx.android.synthetic.main.activity_zoomable_image.*
import org.koin.android.ext.android.inject

class ZoomableImageActivity : AppCompatActivity() {

    private val recyclerAdapter: ZoomableRecyclerAdapter by inject()
    private var itemList = arrayListOf<String>()
    private var itemIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoomable_image)

        getIntentData()
        initViews()
        initListeners()
    }

    private fun getIntentData() {
        intent.getStringExtra(IntentID.USER_IMAGE)?.let { itemList.add(it) }

        intent.getStringArrayListExtra(IntentID.CHAT_IMAGES)?.let { itemList = it }

        intent.getIntExtra(IntentID.CHAT_IMAGE_INDEX, 0).let { itemIndex = it }
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {

        image_number_txt.text = "${itemIndex + 1}/${itemList.size}"

        zoomable_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@ZoomableImageActivity).apply {
                orientation = LinearLayoutManager.HORIZONTAL
                stackFromEnd = true
                scrollToPosition(itemIndex)
            }
            adapter = recyclerAdapter
            setHasFixedSize(true)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    val visiblePosition =
                        (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && newState != RecyclerView.SCROLL_STATE_DRAGGING
                    ) {
                        smoothScrollToPosition((layoutManager as LinearLayoutManager).findLastVisibleItemPosition())
                        image_number_txt.text = "${visiblePosition + 1}/${itemList.size}"

                    }
                }

            })
        }

        recyclerAdapter.setItemList(itemList)

    }

    private fun initListeners() {
        cancel_button.setOnClickListener {
            this@ZoomableImageActivity.finish()
        }
    }
}
