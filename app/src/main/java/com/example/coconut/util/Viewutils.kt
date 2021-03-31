package com.example.coconut.util

import android.view.View
import android.widget.ProgressBar

fun View.show(){
    visibility = View.VISIBLE
}

fun View.hide(){
    visibility = View.INVISIBLE
}

fun View.gone(){
    visibility = View.GONE
}

fun View.disable() {
    isEnabled = false
}

fun View.enable() {
    isEnabled = true
}