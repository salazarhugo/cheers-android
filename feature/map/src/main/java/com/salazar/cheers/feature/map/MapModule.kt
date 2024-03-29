package com.salazar.cheers.feature.map

import android.content.Context
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import com.salazar.cheers.data.map.MapRepository
import com.salazar.cheers.data.map.MapRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MapModule {

    @Provides
    @Singleton
    fun provideMapRepository(
        impl: MapRepositoryImpl,
    ): MapRepository {
        return impl
    }

    @Singleton
    @Provides
    fun provideSearchEngine(
        @ApplicationContext context: Context,
    ): SearchEngine {
        return SearchEngine.createSearchEngineWithBuiltInDataProviders(
            SearchEngineSettings()
        )
//            SearchEngineSettings(context.getString(R.string.mapbox_access_token))
    }
}
