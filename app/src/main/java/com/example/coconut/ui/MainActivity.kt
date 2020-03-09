package com.example.coconut.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.coconut.*
import com.example.coconut.service.SocketService
import com.example.coconut.util.showToast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Log.e(TAG,"onCreate")
        setSupportActionBar(baseToolBar).apply { title = getString(R.string.title_account) }
        nav_view.setupWithNavController(findNavController(R.id.nav_host_fragment))

        //서비스 시작 => bindService() 후에 Service객체 사용가능
        startService(Intent(this@MainActivity,SocketService::class.java))

    }

    override fun onStart() {
        //Log.e(TAG,"onStart")
        super.onStart()
    }

    override fun onStop() {
        //Log.e(TAG,"onStop")
        super.onStop()

    }

    override fun onDestroy() {
        //Log.e(TAG,"onDestroy")

        //서비스 종료
        stopService(Intent(this@MainActivity,SocketService::class.java))
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_search->{ showToast("search") }
        }
        return super.onOptionsItemSelected(item)
    }
}
