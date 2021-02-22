package com.ytowka.worktimer2.data.database

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.ytowka.worktimer2.data.models.Action
import com.ytowka.worktimer2.data.models.SetInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@ExperimentalCoroutinesApi
class DatabaseTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: LocalDatabase
    private lateinit var setDao: SetDao
    private lateinit var actionDao: ActionDao

    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LocalDatabase::class.java
        ).allowMainThreadQueries().build()
        setDao = database.getSetDao()
        actionDao = database.getActionDao()
    }
    @After
    fun teardown(){
        database.close()
    }


    @Test
    fun insertAction() = runBlockingTest {
        val setId = 5

        val testName = "test name"
        val actionInfo = SetInfo(name = testName, setId = setId)
        setDao.addSetInfo(actionInfo)

        val action1 = Action(1, setId, "action 1", 1, 1, true)
        val action2 = Action(2, setId, "action 2", 1, 1, true)

        actionDao.insertAction(action1)
        actionDao.insertAction(action2)

        val sets = setDao.getSets()
        assertThat(sets[0].actions).contains(action1)
    }

    @Test
    fun deleteSet() = runBlockingTest {
        val setId = 5

        val testName = "test name"
        val actionInfo = SetInfo(name = testName, setId = setId)
        setDao.addSetInfo(actionInfo)

        val action1 = Action(1, setId, "action 1", 1, 1, true)
        val action2 = Action(2, setId, "action 2", 1, 1, true)

        actionDao.insertAction(action1)
        actionDao.insertAction(action2)

        var sets = setDao.getSets()
        Log.i("debug","${sets[0].setInfo.setId}")

        setDao.deleteSet(sets[0])
        sets = setDao.getSets()

        val actions = actionDao.getSetActions(setId)

        assertThat(actions).isEmpty()
    }

    @Test
    fun insertSet() = runBlockingTest {
        val testName = "test name"

        val actionInfo = SetInfo(name = testName)
        val actionInfo2 = SetInfo(name = "${testName}+2")

        setDao.addSetInfo(actionInfo)
        setDao.addSetInfo(actionInfo2)

        val sets = setDao.getSets()


        if(sets.isNotEmpty()){
            val set = sets.get(0)
            assertThat(set.setInfo.name).isEqualTo(testName)
        }else{
            assertThat(false).isEqualTo(true)
        }
    }

    @Test
    fun updateSetInfo() = runBlockingTest {
        val testName = "test name"
        val actionInfo = SetInfo(name = testName)
        setDao.addSetInfo(actionInfo)

        val newTestName = "new name"
        actionInfo.name = newTestName
        setDao.updateSetInfo(actionInfo)

        val sets = setDao.getSets()

    }
}