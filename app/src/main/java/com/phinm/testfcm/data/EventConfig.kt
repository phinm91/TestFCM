package com.phinm.testfcm.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.phinm.testfcm.util.fromUTCTimeToLocaleTime
import com.phinm.testfcm.util.generateTimePoints
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Entity
data class EventConfig(
    @PrimaryKey
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var notifyDate: String = "",
    var firstNotifyTime: String = "",
    var lastNotifyTime: String = "",
    var notificationInterval: String = NOTIFICATION_INTERVAL_FORMAT_NONE
) {
    companion object {
        const val NOTIFICATION_INTERVAL_FORMAT_NONE = "NONE"
        const val NOTIFICATION_INTERVAL_FORMAT_DAILY = "d"
        const val NOTIFICATION_INTERVAL_FORMAT_MINUTES = "m"

        fun String.isRepeated() = isRepeatedDaily() || isRepeatedMultiTimePerDay()

        fun String.isRepeatedDaily(): Boolean = this.endsWith(NOTIFICATION_INTERVAL_FORMAT_DAILY)

        fun String.isRepeatedMultiTimePerDay() = this.endsWith(NOTIFICATION_INTERVAL_FORMAT_MINUTES)
    }

    fun isRepeated(): Boolean {
        return notificationInterval.isRepeated()
    }

    fun toFirebaseEvent(): List<FirebaseEvent> {
        val notifyTimes = if (!isRepeated() || firstNotifyTime == lastNotifyTime) {
            listOf(firstNotifyTime)
        } else {
            generateTimePoints(
                startTimeStr = firstNotifyTime.fromUTCTimeToLocaleTime(),
                endTimeStr = lastNotifyTime.fromUTCTimeToLocaleTime(),
                intervalMinutesStr = notificationInterval
            )
        }

        return notifyTimes.map {
            FirebaseEvent(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                notifyDate = notifyDate,
                notifyTime = it,
            )
        }
    }
}

@Dao
interface EventDao {
    @Query("SELECT * FROM EventConfig")
    fun getEvents(): Flow<List<EventConfig>>

    @Query("SELECT * FROM EventConfig WHERE id = :id")
    fun getEventById(id: String): Flow<EventConfig?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvent(eventConfig: EventConfig)

    @Update
    suspend fun updateEvent(eventConfig: EventConfig)

    @Delete
    suspend fun deleteEvent(eventConfig: EventConfig)
}

class EventRepository(private val eventDao: EventDao) {
    fun getEvents(): Flow<List<EventConfig>> = eventDao.getEvents()
    fun getEventById(id: String) = eventDao.getEventById(id)
    suspend fun insertEvent(eventConfig: EventConfig) = eventDao.insertEvent(eventConfig)
    suspend fun updateEvent(eventConfig: EventConfig) = eventDao.updateEvent(eventConfig)
    suspend fun deleteEvent(eventConfig: EventConfig) = eventDao.deleteEvent(eventConfig)
}

@Database(entities = [EventConfig::class], version = 1, exportSchema = false)
abstract class EventDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    companion object {
        private const val DATABASE_NAME = "event_database"

        @Volatile
        private var Instance: EventDatabase? = null

        fun getDatabase(context: Context): EventDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, EventDatabase::class.java, DATABASE_NAME)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}