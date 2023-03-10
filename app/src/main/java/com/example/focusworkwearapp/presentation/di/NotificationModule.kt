package com.example.focusworkwearapp.presentation.di

import android.app.NotificationManager
import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.app.NotificationCompat
import com.example.focusworkwearapp.R
import com.example.focusworkwearapp.presentation.data.repository.PreferenceStore
import com.example.focusworkwearapp.presentation.service.ServiceHelper
import com.example.focusworkwearapp.utils.NOTIFICATION_CHANNEL_ID
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@ExperimentalAnimationApi
@Module
@InstallIn(ServiceComponent::class)
object NotificationModule {

    @ServiceScoped
    @Provides
    fun provideNotificationBuilder(
        @ApplicationContext context: Context,
        preferenceStore:PreferenceStore
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Stopwatch")
            .setContentText("00:00:00")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .addAction(0, "Stop", ServiceHelper.stopPendingIntent(context))
            .addAction(0, "Cancel", ServiceHelper.cancelPendingIntent(context,preferenceStore))
            .setContentIntent(ServiceHelper.clickPendingIntent(context))
    }

    @ServiceScoped
    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

}