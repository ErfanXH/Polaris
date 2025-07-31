package com.netwatcher.polaris.di

import android.content.Context
import androidx.room.Room
import com.netwatcher.polaris.domain.model.NetworkDataDao
import com.netwatcher.polaris.domain.model.NetworkDataDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NetworkDataDatabase {
        return Room.databaseBuilder(
            context,
            NetworkDataDatabase::class.java,
            "network_data.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideNetworkDataDao(db: NetworkDataDatabase): NetworkDataDao {
        return db.networkDataDao()
    }
}