package com.example.flighttrackerques2.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FlightHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val flightIata: String,
    val date: String?,
    val arrScheduled: String?,
    val arrActual: String?,
    val depScheduled: String?,
    val depActual: String?,
    val depDelayMinutes: Int?,
    val arrDelayMinutes: Int?,
)

@Entity
data class TrackedFlight(
    @PrimaryKey val iata: String,
    val lastUpdated: Long
)

@Entity
data class SelectedFlightFull(
    @PrimaryKey val iata: String,
    val airlineName: String?,
    val flightDate: String?,
    val depIata: String?,
    val depGate: String?,
    val depTerminal: String?,
    val depScheduled: String?,
    val depActual: String?,
    val arrIata: String?,
    val arrGate: String?,
    val arrTerminal: String?,
    val arrScheduled: String?,
    val arrActual: String?,
    val lastUpdated: Long,
    val insertedAt: Long
)

