package com.raizmarket.app


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class BuyerActivity : AppCompatActivity() {
    private lateinit var nav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buyer)
        nav = findViewById(R.id.bottom_nav)
        nav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_search -> CatalogFragment()
                R.id.nav_cart -> CartFragment()
                R.id.nav_orders -> BuyerOrdersFragment()
                R.id.nav_profile -> BuyerProfileFragment()
                else -> BuyerHomeFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
            true
        }
        if (savedInstanceState == null) nav.selectedItemId = R.id.nav_home
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getStringExtra("open") == "orders") nav.selectedItemId = R.id.nav_orders
    }
}
