package com.elgaban.mrkhalid.di.module

import android.content.Context
import com.elgaban.mrkhalid.repository.AuthRepository
import com.elgaban.mrkhalid.repository.ConnectivityRepository
import com.elgaban.mrkhalid.repository.DataStoreRepositoryImpl
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.Module
import dagger.Provides

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext appContext: Context): Context {
        return appContext
    }

    @Provides
    fun provideAuthRepository() = AuthRepository()

    @Provides
    fun provideConnectivityRepository(@ApplicationContext context: Context) = ConnectivityRepository(context)

    @Singleton
    @Provides
    fun provideDataStoreRepositoryImpl(@ApplicationContext context: Context) =
        DataStoreRepositoryImpl(context)
}