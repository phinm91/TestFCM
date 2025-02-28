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
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random

@Entity
data class EventConfig(
    @PrimaryKey
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val notifyDate: String = "",
    val firstNotifyTime: String = "",
    val lastNotifyTime: String = "",
    val notificationInterval: String = NOTIFICATION_INTERVAL_FORMAT_NONE
) {
    companion object {
        const val NOTIFICATION_INTERVAL_FORMAT_NONE = "NONE"
        const val NOTIFICATION_INTERVAL_FORMAT_DAILY = "d"
        const val NOTIFICATION_INTERVAL_FORMAT_MINUTES = "m"

        private val random: Random by lazy { Random.Default }
        fun randomEvent(): EventConfig {
            val ranId = random.nextLong(0, 1000)
            return EventConfig(
                id = UUID.randomUUID().toString(),
                title = "Title : $ranId",
                description = "Description : $ranId",
                notifyDate = LocalDate.now().toString(),
            )
        }

        fun updateEvent(eventConfig: EventConfig): EventConfig {
            val ranId = random.nextLong(0, 1000)
            return eventConfig.copy(
                title = "Title updated : $ranId",
                description = "Description updated : $ranId",
                notifyDate = LocalDate.now().toString(),
            )
        }
    }

    fun isRepeatedDaily(): Boolean {
        return notificationInterval != NOTIFICATION_INTERVAL_FORMAT_NONE
    }

    fun toFirebaseEvent(): FirebaseEvent {
        val notifyTimes = if (!isRepeatedDaily() || firstNotifyTime == lastNotifyTime) {
            listOf(firstNotifyTime)
        } else {
            //TODO : Tính toán thời gian thông báo trong ngày dựa theo các mốc thời gian đã chọn.
            listOf(firstNotifyTime, lastNotifyTime)
        }
        return FirebaseEvent(
            id = id,
            title = title,
            description = description,
            notifyDate = notifyDate,
            notifyTimes = notifyTimes,
        )
    }
}

@Dao
interface EventDao {
    @Query("SELECT * FROM EventConfig")
    fun getEvents(): Flow<List<EventConfig>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvent(eventConfig: EventConfig)

    @Update
    suspend fun updateEvent(eventConfig: EventConfig)

    @Delete
    suspend fun deleteEvent(eventConfig: EventConfig)
}

class EventRepository(private val eventDao: EventDao) {
    fun getEvents(): Flow<List<EventConfig>> = eventDao.getEvents()
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