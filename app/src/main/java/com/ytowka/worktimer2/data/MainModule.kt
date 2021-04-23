package com.ytowka.worktimer2.data

import android.content.Context
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.data.database.ActionDao
import com.ytowka.worktimer2.data.database.SetDao
import com.ytowka.worktimer2.data.models.Action
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
    fun provideFinishAction(@ApplicationContext context: Context): Action{
        return Action(context.getString(R.string.finish),1,context.getColor(R.color.white),false)
    }
}