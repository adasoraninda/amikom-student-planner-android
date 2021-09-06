package com.codetron.studentplanner.di.module

import com.codetron.studentplanner.data.validation.student.AccountAuthValidation
import com.codetron.studentplanner.data.validation.student.AccountProfileValidation
import com.codetron.studentplanner.data.validation.student.StudentAuthValidation
import com.codetron.studentplanner.data.validation.student.StudentProfileValidation
import com.codetron.studentplanner.data.validation.task.TaskValidation
import com.codetron.studentplanner.data.validation.task.TaskValidationImpl
import dagger.Binds
import dagger.Module

@Module
abstract class ValidationModule {

    @Binds
    abstract fun provideStudentAuthValidation(studentValidation: StudentAuthValidation): AccountAuthValidation

    @Binds
    abstract fun provideStudentProfileValidation(studentProfileValidation: StudentProfileValidation): AccountProfileValidation

    @Binds
    abstract fun provideTaskValidation(taskValidation: TaskValidationImpl): TaskValidation

}