package com.example.flighttrackerques2.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flighttrackerques2.data.*
import com.example.flighttrackerques2.data.database.FlightDatabase
import com.example.flighttrackerques2.data.database.FlightHistory
import com.example.flighttrackerques2.data.database.SelectedFlightFull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrackedFlightsScreen(navController: NavController, isDarkTheme: Boolean, onToggleTheme: () -> Unit) {
    val context = LocalContext.current
    val dao = remember { FlightDatabase.getDatabase(context).flightDao() }
    val coroutineScope = rememberCoroutineScope()
    var flights by remember { mutableStateOf<List<SelectedFlightFull>>(emptyList()) }

    val avgJourneyTime = remember { mutableStateMapOf<String, Int>() }
    val avgDepDelay = remember { mutableStateMapOf<String, Int>() }
    val avgArrDelay = remember { mutableStateMapOf<String, Int>() }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            dao.deleteOlderThan(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L)
            val current = dao.getSelectedFlightsFull()

            // Refresh if last updated over 1 day
            current.forEach { flight ->
                if (System.currentTimeMillis() - flight.lastUpdated > 86_400_000L) {
                    try {
                        val response = ApiClient.api.getFlight("9828120c57fe69e54421f404a033eb3d", flight.iata)
                        val updated = response.data.firstOrNull() ?: return@forEach
                        dao.saveSelectedFlightFull(
                            SelectedFlightFull(
                                iata = updated.flight?.iata ?: return@forEach,
                                airlineName = updated.airline?.name,
                                flightDate = updated.flight_date,
                                depIata = updated.departure?.iata,
                                depGate = updated.departure?.gate,
                                depTerminal = updated.departure?.terminal,
                                depScheduled = updated.departure?.scheduled,
                                depActual = updated.departure?.actual,
                                arrIata = updated.arrival?.iata,
                                arrGate = updated.arrival?.gate,
                                arrTerminal = updated.arrival?.terminal,
                                arrScheduled = updated.arrival?.scheduled,
                                arrActual = updated.arrival?.actual,
                                lastUpdated = System.currentTimeMillis(),
                                insertedAt = flight.insertedAt
                            )
                        )
                        // Add to history
                        dao.saveHistory(
                            FlightHistory(
                                flightIata = updated.flight?.iata ?: return@forEach,
                                date = updated.flight_date,
                                depScheduled = updated.departure?.scheduled,
                                depActual = updated.departure?.actual,
                                arrScheduled = updated.arrival?.scheduled,
                                arrActual = updated.arrival?.actual,
                                depDelayMinutes = updated.departure?.delay,
                                arrDelayMinutes = updated.arrival?.delay
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            flights = dao.getSelectedFlightsFull()

            // Compute averages
            avgJourneyTime.clear()
            avgDepDelay.clear()
            avgArrDelay.clear()

            for (flight in flights) {
                val history = dao.getAllForFlight(flight.iata)
                if (history.isNotEmpty()) {
                    val journeyMins = history.mapNotNull {
                        try {
                            val dep = it.depActual ?: return@mapNotNull null
                            val arr = it.arrActual ?: return@mapNotNull null
                            val depMs = ZonedDateTime.parse(dep).toInstant().toEpochMilli()
                            val arrMs = ZonedDateTime.parse(arr).toInstant().toEpochMilli()
                            (arrMs - depMs).toInt() / 60_000
                        } catch (e: Exception) { null }
                    }

                    val avgJourney = journeyMins.takeIf { it.isNotEmpty() }?.average()?.toInt()
                    val avgDep = history.mapNotNull { it.depDelayMinutes }.takeIf { it.isNotEmpty() }?.average()?.toInt()
                    val avgArr = history.mapNotNull { it.arrDelayMinutes }.takeIf { it.isNotEmpty() }?.average()?.toInt()

                    avgJourneyTime[flight.iata] = avgJourney ?: 0
                    avgDepDelay[flight.iata] = avgDep ?: 0
                    avgArrDelay[flight.iata] = avgArr ?: 0
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tracked Flights") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onToggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Filled.Brightness7 else Icons.Filled.Brightness4,
                            contentDescription = "Toggle Theme"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (flights.isEmpty()) {
            Column(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("No tracked flights. Go to search screen to select some.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(flights) { flight ->
                    val updated = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        .format(Date(flight.lastUpdated))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "✈ ${flight.iata} - ${flight.airlineName}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text("📅 Date: ${flight.flightDate}", style = MaterialTheme.typography.bodyMedium)

                            Spacer(Modifier.height(12.dp))
                            Text("🛫 Departure", style = MaterialTheme.typography.labelMedium)
                            Text("Airport: ${flight.depIata}", style = MaterialTheme.typography.bodySmall)
                            Text("Gate: ${flight.depGate ?: "-"}, Terminal: ${flight.depTerminal ?: "-"}")
                            Text("Scheduled: ${flight.depScheduled ?: "-"}")
                            Text("Actual: ${flight.depActual ?: "-"}")

                            Spacer(Modifier.height(8.dp))
                            Text("🛬 Arrival", style = MaterialTheme.typography.labelMedium)
                            Text("Airport: ${flight.arrIata}", style = MaterialTheme.typography.bodySmall)
                            Text("Gate: ${flight.arrGate ?: "-"}, Terminal: ${flight.arrTerminal ?: "-"}")
                            Text("Scheduled: ${flight.arrScheduled ?: "-"}")
                            Text("Actual: ${flight.arrActual ?: "-"}")

                            Spacer(Modifier.height(12.dp))
                            Text("📊 Avg Journey Time: ${avgJourneyTime[flight.iata] ?: "-"} mins")
                            Text("⏱️ Avg Departure Delay: ${avgDepDelay[flight.iata] ?: "-"} mins")
                            Text("🛬 Avg Arrival Delay: ${avgArrDelay[flight.iata] ?: "-"} mins")

                            Spacer(Modifier.height(8.dp))
                            Text(
                                "🕒 Last Updated: $updated",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

            }
        }
    }
}
