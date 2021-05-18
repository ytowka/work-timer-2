package com.ytowka.worktimer2.data

import android.content.Context
import com.ytowka.worktimer2.R
import com.ytowka.worktimer2.data.database.ActionDao
import com.ytowka.worktimer2.data.database.ActionTypesDao
import com.ytowka.worktimer2.data.database.SetDao
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.ActionType
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
    fun provideRepository(setDao: SetDao, actionDao: ActionDao, actionTypesDao: ActionTypesDao): Repository{
        return LocalRepository(setDao, actionDao, actionTypesDao)
    }
    @Provides
    @Singleton
    fun provideFinishAction(@ApplicationContext context: Context): Action{
        return Action(context.getString(R.string.finish),1,context.getColor(R.color.white),false)
    }

    @Provides
    @Singleton
    fun provideDefaultTypes(@ApplicationContext context: Context): List<ActionType>{
        return listOf(
            ActionType(context.getString(R.string.rest),context.getColor(R.color.rest)),
            ActionType(context.getString(R.string.training),context.getColor(R.color.training)),
            ActionType("+",context.getColor(R.color.plus))
        )
    }
}