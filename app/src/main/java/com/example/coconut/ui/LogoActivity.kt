package com.example.coconut.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.coconut.R
import com.example.coconut.ui.auth.login.LoginActivity

class LogoActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT : Long = 1500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)

        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        },SPLASH_TIME_OUT)
    }
}
