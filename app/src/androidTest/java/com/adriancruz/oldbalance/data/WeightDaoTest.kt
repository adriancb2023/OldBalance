package com.adriancruz.oldbalance.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class WeightDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: WeightDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.weightDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetEntry() = runBlocking {
        val entry = WeightEntry(id = 1, date = 1000L, weightKg = 80.0f)
        dao.insertEntry(entry)
        val allEntries = dao.getAllEntriesFlow().first()
        assertThat(allEntries).contains(entry)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetGoal() = runBlocking {
        val goal = WeightGoal(id = 1, targetKg = 75.0f)
        dao.insertGoal(goal)
        val activeGoals = dao.getActiveGoalsFlow().first()
        assertThat(activeGoals).contains(goal)
    }

    @Test
    @Throws(Exception::class)
    fun insertReplacesOnConflict() = runBlocking {
        val entry1 = WeightEntry(date = 1000L, weightKg = 80.0f)
        val entry2 = WeightEntry(date = 1000L, weightKg = 81.0f) // Same date
        dao.insertEntry(entry1)
        dao.insertEntry(entry2)

        val allEntries = dao.getAllEntriesFlow().first()
        assertThat(allEntries.size).isEqualTo(1)
        assertThat(allEntries[0].weightKg).isEqualTo(81.0f)
    }

    @Test
    @Throws(Exception::class)
    fun deleteEntryRemovesIt() = runBlocking {
        val entry = WeightEntry(id = 1, date = 1000L, weightKg = 80.0f)
        dao.insertEntry(entry)
        dao.deleteEntry(entry)
        val allEntries = dao.getAllEntriesFlow().first()
        assertThat(allEntries).isEmpty()
    }
}
