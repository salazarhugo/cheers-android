package com.salazar.cheers.core.util

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import linc.com.amplituda.Amplituda

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideAmplituda(
        @ApplicationContext context: Context,
    ): Amplituda = Amplituda(context)
}