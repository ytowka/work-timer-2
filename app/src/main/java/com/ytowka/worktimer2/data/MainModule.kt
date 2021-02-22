package com.ytowka.worktimer2.data

import android.content.Context
import androidx.room.Room
import com.ytowka.worktimer2.data.database.ActionDao
import com.ytowka.worktimer2.data.database.LocalDatabase
import com.ytowka.worktimer2.data.database.SetDao
import com.ytowka.worktimer2.utils.Consts.Companion.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    @Singleton
    fun provideRepository(@ApplicationContext context: Context, setDao: SetDao, actionDao: ActionDao): Repository{
        return LocalRepository(context, setDao, actionDao)
    }

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
    fun provideDatabase(@ApplicationContext context: Context): LocalDatabase{
        return Room.databaseBuilder(context,LocalDatabase::class.java,DB_NAME).build()
    }

}