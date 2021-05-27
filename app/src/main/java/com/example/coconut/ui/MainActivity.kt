package com.example.coconut.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.coconut.*
import com.example.coconut.service.SocketService
import com.example.coconut.util.showToast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnFragmentInteractionListener {


    private val TAG = "MainActivity"
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Log.e(TAG,"onCreate")
        navController = findNavController(R.id.nav_host_fragment)
        setSupportActionBar(baseToolBar).apply { title = getString(R.string.title_account) }
        nav_view.apply {
            setupWithNavController(navController)

            setOnNavigationItemSelectedListener {
                val nextItem = it.itemId
                if (selectedItemId != nextItem)
                    navigateView(it.itemId)
                return@setOnNavigationItemSelectedListener true
            }
        }

        //서비스 시작 => bindService() 후에 Service객체 사용가능
        startService(Intent(this@MainActivity, SocketService::class.java))

    }

    override fun onDestroy() {
        super.onDestroy()
        //서비스 종료
        stopService(Intent(this@MainActivity, SocketService::class.java))
        Log.e(TAG, "onDestroy")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                showToast("search")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "onActivityResult: ")
        if (resultCode != RESULT_OK)
            return
    }

    override fun onFragmentReload() {
        val id = navController.currentDestination?.id
        navigateView(id)
    }

    private fun navigateView(id: Int?) {
        navController.popBackStack(id!!, true)
        navController.navigate(id)
    }
}
