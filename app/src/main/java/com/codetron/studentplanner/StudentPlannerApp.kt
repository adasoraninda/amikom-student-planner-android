package com.codetron.studentplanner

import android.app.Application
import com.codetron.studentplanner.di.component.AppComponent
import com.codetron.studentplanner.di.component.DaggerAppComponent

class StudentPlannerApp : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }

}