package es.escriva.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import es.escriva.dao.DayDao
import es.escriva.dao.TokenDao
import es.escriva.dao.VehicleRecordDao
import es.escriva.domain.Day
import es.escriva.domain.Token
import es.escriva.domain.VehicleRecord

@Database(entities = [Token::class, Day::class, VehicleRecord::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tokenDao(): TokenDao

    abstract fun dayDao(): DayDao

    abstract fun vehicleRecordDao(): VehicleRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "escriva-parking_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}