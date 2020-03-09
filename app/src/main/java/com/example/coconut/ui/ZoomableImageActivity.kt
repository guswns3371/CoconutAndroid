package com.example.coconut.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.coconut.Constant
import com.example.coconut.IntentID
import com.example.coconut.R
import com.github.chrisbanes.photoview.PhotoView
import com.github.chrisbanes.photoview.PhotoViewAttacher
import kotlinx.android.synthetic.main.activity_account_info.*
import kotlinx.android.synthetic.main.activity_zoomable_image.*

class ZoomableImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoomable_image)

        setImageView()
    }

    private fun setImageView(){
        intent.getStringExtra(IntentID.USER_IMAGE)?.let {
            val imageView = findViewById<PhotoView>(R.id.zoomable_image)
            Glide.with(this@ZoomableImageActivity)
                .load(Constant.BASE_URL+it)
                .placeholder(R.drawable.black)
                .into(imageView)
        }
    }
}
