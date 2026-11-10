package com.raizmarket.app


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        val drawer = findViewById<DrawerLayout>(R.id.drawer)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val nav = findViewById<NavigationView>(R.id.nav_view)
        toolbar.setNavigationIcon(R.drawable.ic_menu)
        toolbar.setNavigationOnClickListener { drawer.openDrawer(GravityCompat.START) }

        nav.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_logout -> logout()
                else -> {
                    val fragment: Fragment = when (item.itemId) {
                        R.id.nav_users -> AdminUsersFragment()
                        R.id.nav_moderation -> AdminProductsFragment()
                        R.id.nav_reports -> AdminReportsFragment()
                        else -> AdminDashboardFragment()
                    }
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
                    toolbar.title = item.title
                }
            }
            drawer.closeDrawers()
            true
        }
        if (savedInstanceState == null) {
            nav.setCheckedItem(R.id.nav_dashboard)
            supportFragmentManager.beginTransaction().replace(R.id.container, AdminDashboardFragment()).commit()
        }
    }
}
