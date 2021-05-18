package com.ytowka.worktimer2.data.database

import android.content.Context
import androidx.room.Room
import com.ytowka.worktimer2.utils.C
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideSetDao(database: LocalDatabase): SetDao{
        return database.getSetDao()
    }

    @Provides
    @Singleton
    fun provideActionDao(database: LocalDatabase): ActionDao{
        return database.getActionDao()
    }

    @Provides
    @Singleton
    fun provideActionTypesDao(database: LocalDatabase) = database.getActionTypesDao()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LocalDatabase{
        return Room.databaseBuilder(context,LocalDatabase::class.java, C.DB_NAME).build()
    }
}