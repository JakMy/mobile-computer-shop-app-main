package com.example.myshopapp

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.myshopapp.fragment.CartFragment
import com.example.myshopapp.fragment.ConfiguratorFragment
import com.example.myshopapp.fragment.HomeFragment
import com.example.myshopapp.fragment.SearchFragment
import com.example.myshopapp.fragment.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rootLayout = findViewById<RelativeLayout>(R.id.root_layout)
        val animDrawable = rootLayout.background as AnimationDrawable

        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> loadFragment(HomeFragment())
                R.id.action_search -> { loadFragment(SearchFragment()) }
                R.id.action_cart -> { loadFragment(CartFragment()) }
                R.id.action_settings -> { loadFragment(SettingsFragment()) }
                R.id.action_pc_configurator -> { loadFragment(ConfiguratorFragment()) }
            }
            true
        }

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}
