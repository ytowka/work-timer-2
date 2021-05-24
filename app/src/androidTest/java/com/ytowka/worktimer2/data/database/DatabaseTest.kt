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
import com.ytowka.worktimer2.utils.getOrAwaitValue
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
    fun getSingleSet() = runBlockingTest{
        val testName = "test name"
        val id1 = 1;
        val id2 = 2;

        val actionInfo = SetInfo(name = testName, setId = id1)
        val actionInfo2 = SetInfo(name = "${testName}+2", setId = id2)

        setDao.insertSetInfo(actionInfo)
        setDao.insertSetInfo(actionInfo2)

        val set = setDao.getSetAsLiveData(id1).getOrAwaitValue()
        assertThat(set.setInfo.name).isEqualTo(testName)
    }


    @Test
    fun insertAction() = runBlockingTest {
        val setId = 5

        val testName = "test name"
        val actionInfo = SetInfo(name = testName, setId = setId)
        setDao.insertSetInfo(actionInfo)

        val action1 = Action( "action 1", 1, 1, true,1, setId)
        val action2 = Action( "action 2", 1, 1, true,2, setId,)

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
        setDao.insertSetInfo(actionInfo)

        val action1 = Action( "action 1", 1, 1, true, 1, setId,)
        val action2 = Action( "action 2", 1, 1, true, 2, setId,)

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

        setDao.insertSetInfo(actionInfo)
        setDao.insertSetInfo(actionInfo2)

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
        var actionInfo = SetInfo(name = testName)
        setDao.insertSetInfo(actionInfo)

        actionInfo = setDao.getSets()[0].setInfo

        val newTestName = "new test name"
        actionInfo = SetInfo(actionInfo.setId, newTestName)
        setDao.updateSetInfo(actionInfo)

        val sets = setDao.getSets()

        assertThat(sets[0].setInfo.name).isEqualTo(newTestName)

    }
}