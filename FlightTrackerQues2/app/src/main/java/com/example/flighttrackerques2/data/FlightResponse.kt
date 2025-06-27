package com.example.flighttrackerques2.data

data class FlightResponse(
    val data: List<FlightData>
)

data class FlightData(
    val flight_date: String?,
    val flight_status: String?,
    val departure: Departure?,
    val arrival: Arrival?,
    val airline: Airline?,
    val flight: FlightInfo?,
    val aircraft: Aircraft?,
    val live: LiveInfo?
)

data class Departure(
    val airport: String?,
    val iata: String?,
    val gate: String?,
    val terminal: String?,
    val delay: Int?,
    val scheduled: String?,
    val estimated: String?,
    val actual: String?
)

data class Arrival(
    val airport: String?,
    val iata: String?,
    val gate: String?,
    val terminal: String?,
    val baggage: String?,
    val delay: Int?,
    val scheduled: String?,
    val estimated: String?,
    val actual: String?
)

data class Airline(
    val name: String?,
    val iata: String?,
    val icao: String?
)

data class FlightInfo(
    val number: String?,
    val iata: String?,
    val icao: String?,
    val codeshared: CodeShared?
)

data class CodeShared(
    val airline_name: String?,
    val airline_iata: String?,
    val airline_icao: String?,
    val flight_number: String?,
    val flight_iata: String?,
    val flight_icao: String?
)

data class Aircraft(
    val registration: String?,
    val iata: String?,
    val icao: String?,
    val icao24: String?
)

data class LiveInfo(
    val updated: String?,
    val latitude: Double?,
    val longitude: Double?,
    val altitude: Double?,
    val direction: Double?,
    val speed_horizontal: Double?,
    val speed_vertical: Double?,
    val is_ground: Boolean?
)
