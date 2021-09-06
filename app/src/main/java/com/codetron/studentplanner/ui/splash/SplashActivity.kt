package com.codetron.studentplanner.ui.splash

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.codetron.studentplanner.R
import com.codetron.studentplanner.StudentPlannerApp
import com.codetron.studentplanner.ui.auth.AuthActivity
import com.codetron.studentplanner.ui.home.HomeActivity
import com.codetron.studentplanner.utils.ViewModelFactory
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ViewModelFactory

    private val viewModel by viewModels<SplashViewModel> { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as StudentPlannerApp).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        observeNavigateToHome()
        observeNavigateToWelcome()

    }

    private fun observeNavigateToHome() {
        viewModel.isNavigateToHome.observe(this, {
            if (it) {
                navigateToHome()
                viewModel.doneNavigate()
            }
        })
    }

    private fun observeNavigateToWelcome() {
        viewModel.isNavigateWelcome.observe(this, {
            if (it) {
                navigateToAuth()
                viewModel.doneNavigate()
            }
        })
    }

    private fun navigateToHome() {
        HomeActivity.navigate(this)
        finishAffinity()
    }

    private fun navigateToAuth() {
        AuthActivity.navigate(this)
        finishAffinity()
    }
}