package com.codetron.studentplanner.di.component

import android.content.Context
import com.codetron.studentplanner.di.module.FirebaseModule
import com.codetron.studentplanner.di.module.ValidationModule
import com.codetron.studentplanner.ui.auth.signin.SignInFragment
import com.codetron.studentplanner.ui.auth.signup.SignUpFragment
import com.codetron.studentplanner.ui.home.dashboard.DashboardFragment
import com.codetron.studentplanner.ui.home.profile.ProfileFragment
import com.codetron.studentplanner.ui.home.tasks.TasksFragment
import com.codetron.studentplanner.ui.profile.EditProfileActivity
import com.codetron.studentplanner.ui.splash.SplashActivity
import com.codetron.studentplanner.ui.task.TaskActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [ValidationModule::class, FirebaseModule::class]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(activity: SplashActivity)

    fun inject(fragment: SignUpFragment)
    fun inject(fragment: SignInFragment)

    fun inject(fragment: DashboardFragment)
    fun inject(fragment: TasksFragment)
    fun inject(fragment: ProfileFragment)

    fun inject(activity: EditProfileActivity)
    fun inject(activity: TaskActivity)

}