package com.example.flighttrackerques2.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FlightHistory::class, SelectedFlightFull::class, TrackedFlight::class], version = 1, exportSchema = false)
abstract class FlightDatabase : RoomDatabase() {
    abstract fun flightDao(): FlightDao

    companion object {
        @Volatile private var instance: FlightDatabase? = null

        fun getDatabase(context: Context): FlightDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    FlightDatabase::class.java, "flight_db"
                ).build().also { instance = it }
            }
    }
}
