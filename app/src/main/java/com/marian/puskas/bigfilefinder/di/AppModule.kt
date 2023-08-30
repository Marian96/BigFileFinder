package com.marian.puskas.bigfilefinder.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.marian.puskas.bigfilefinder.common.notifications.NotificationProvider
import com.marian.puskas.bigfilefinder.common.notifications.NotificationProviderImpl
import com.marian.puskas.bigfilefinder.common.resources.ResourcesManager
import com.marian.puskas.bigfilefinder.common.resources.ResourcesManagerImpl
import com.marian.puskas.bigfilefinder.data.directories.DirectoriesRepositoryImpl
import com.marian.puskas.bigfilefinder.domain.searchresults.directories.DirectoriesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val DIRECTORIES_PREFERENCES = "directories"

    @Provides
    @Singleton
    @ForApplication
    fun providesApplicationContext(application: Application): Context =
        application.applicationContext

    @Provides
    @Singleton
    fun provideDirectoriesPreferences(@ForApplication context: Context): SharedPreferences =
        context.getSharedPreferences(DIRECTORIES_PREFERENCES, Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideDirectoriesRepository(sharedPreferences: SharedPreferences): DirectoriesRepository = DirectoriesRepositoryImpl(sharedPreferences)

    @Provides
    @Singleton
    fun provideNotificationManager(): NotificationProvider = NotificationProviderImpl()

    @Provides
    @Singleton
    @ForApplication
    fun providesResources(application: Application): Resources = application.resources

    @Provides
    @Singleton
    fun resourcesManager(@ForApplication context: Context, @ForApplication resources: Resources): ResourcesManager = ResourcesManagerImpl(resources, context)

}