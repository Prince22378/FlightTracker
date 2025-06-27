package com.example.flighttrackerques2.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FlightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveHistory(history: FlightHistory)

    @Query("SELECT * FROM FlightHistory WHERE flightIata = :iata")
    suspend fun getAllForFlight(iata: String): List<FlightHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTrackedFlight(flight: TrackedFlight)

    @Query("SELECT * FROM TrackedFlight WHERE iata = :iata")
    suspend fun getTrackedFlight(iata: String): TrackedFlight?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSelectedFlightFull(flight: SelectedFlightFull)

    @Query("SELECT * FROM SelectedFlightFull")
    suspend fun getSelectedFlightsFull(): List<SelectedFlightFull>

    @Query("DELETE FROM SelectedFlightFull WHERE insertedAt < :cutoff")
    suspend fun deleteOlderThan(cutoff: Long)

    @Query("DELETE FROM SelectedFlightFull")
    suspend fun clearSelectedFlightsFull()

}
