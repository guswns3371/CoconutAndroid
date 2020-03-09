package com.example.coconut.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.coconut.R
import com.example.coconut.ui.auth.login.LoginActivity
import com.example.coconut.util.MyPreference
import com.example.coconut.util.log
import org.koin.android.ext.android.inject

class LogoActivity : AppCompatActivity() {

    private val TAG = "LogoActivity2"
    private val SPLASH_TIME_OUT : Long = 900

    private val pref : MyPreference by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)

        Handler().postDelayed({
            log("user id : ${pref.UserId}")

            when(pref.UserId == null){
                true->{ startActivity(Intent(applicationContext, LoginActivity::class.java)) }
                false->{ startActivity(Intent(applicationContext, MainActivity::class.java)) }
            }
            finish()
        },SPLASH_TIME_OUT)
    }
}
