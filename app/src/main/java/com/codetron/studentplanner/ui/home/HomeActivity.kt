package com.codetron.studentplanner.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.codetron.studentplanner.R
import com.codetron.studentplanner.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    companion object {
        fun navigate(context: Context) {
            context.startActivity(Intent(context, HomeActivity::class.java))
        }
    }

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding

    private var toast: Toast? = null
    private var timeBackPressed: Long = 0

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setContentView(binding?.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_home) as NavHostFragment
        navController = navHostFragment.findNavController()

        binding?.bottomNav?.setupWithNavController(navController)
    }

    private fun initBinding() {
        _binding = ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.menu_dashboard) {
            if (timeBackPressed + 2000 > System.currentTimeMillis()) {
                finishAffinity()
                return
            } else {
                toast?.cancel()
                makeToast(getString(R.string.press_back_again_to_exit))
            }
            timeBackPressed = System.currentTimeMillis()
        } else {
            super.onBackPressed()
        }
    }

    private fun makeToast(message: String) {
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast?.show()
    }

    override fun onDestroy() {
        toast?.cancel()
        toast = null
        super.onDestroy()
    }

}