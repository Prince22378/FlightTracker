package com.example.flighttrackerques2.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.flighttrackerques2.data.*
import com.example.flighttrackerques2.data.database.FlightDatabase
import com.example.flighttrackerques2.data.database.FlightHistory
import com.example.flighttrackerques2.data.database.SelectedFlightFull
import com.example.flighttrackerques2.viewmodel.FlightViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchScreen(navController: NavController, isDarkTheme: Boolean, onToggleTheme: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val viewModel: FlightViewModel = viewModel()
    val dao = remember { FlightDatabase.getDatabase(context).flightDao() }

    var depIata by remember { mutableStateOf("") }
    var arrIata by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    var flightOptions by remember { mutableStateOf<List<FlightData>>(emptyList()) }
    val selectedFlights = remember { mutableStateListOf<FlightData>() }
    val snackbarHostState = remember { SnackbarHostState() }
    var hasSearched by remember { mutableStateOf(false) }
    val notEnoughFlights = hasSearched && flightOptions.size in 1..2

    var searchDisabled by remember { mutableStateOf(false) }




    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Flights", style = MaterialTheme.typography.headlineSmall)
                },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
                actions = {
                    IconButton(onClick = { onToggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Filled.Brightness7 else Icons.Filled.Brightness4,
                            contentDescription = "Toggle Theme"
                        )
                    }
                    IconButton(onClick = { navController.navigate("trackedFlights") }) {
                        Icon(Icons.Default.List, contentDescription = "Tracked Flights")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = depIata,
                onValueChange = { depIata = it.uppercase() },
                label = { Text("From Airport IATA") },
                placeholder = { Text("e.g. FUK") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                enabled = !searchDisabled,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = arrIata,
                onValueChange = { arrIata = it.uppercase() },
                label = { Text("To Airport IATA") },
                placeholder = { Text("e.g. ITM") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                enabled = !searchDisabled,
                modifier = Modifier
                    .fillMaxWidth()
            )


        Spacer(Modifier.height(20.dp))


            Button(onClick = {
                if (depIata.isNotBlank() && arrIata.isNotBlank()) {
                    loading = true
                    error = null
                    flightOptions = emptyList()
                    selectedFlights.clear()
                    coroutineScope.launch {
                        try {
                            val response = ApiClient.api.getFlightsBetween(
                                accessKey = "9828120c57fe69e54421f404a033eb3d",
                                depIata = depIata,
                                arrIata = arrIata
                            )
                            flightOptions = response.data
                            searchDisabled = true
                        } catch (e: Exception) {
                            error = "Failed: ${e.message}"
                        } finally {
                            loading = false
                            hasSearched = true
                            searchDisabled = flightOptions.isNotEmpty()
                        }
                    }
                }
            },
                enabled = !searchDisabled,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                .widthIn(min = 202.dp)) {
                Text("Search Flights")
            }

            Spacer(Modifier.height(16.dp))

            if (loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

            error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

//            LazyColumn(modifier = Modifier.weight(1f)) {
//                items(flightOptions) { flight ->
//                    val selected = selectedFlights.contains(flight)
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 6.dp)
//                            .clickable {
//                                if (selected) {
//                                    selectedFlights.remove(flight)
//                                } else if (selectedFlights.size < 3) {
//                                    selectedFlights.add(flight)
//                                }
//                            },
//                        shape = MaterialTheme.shapes.medium,
//                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
//                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
//                    ) {
//                        Column(Modifier.padding(12.dp)) {
//                            Text("✈️ ${flight.flight?.iata} | ${flight.airline?.name}", style = MaterialTheme.typography.titleMedium)
//                            Text("${flight.departure?.iata} → ${flight.arrival?.iata}", style = MaterialTheme.typography.bodyMedium)
//                            Text("${flight.flight_date ?: "-"}", style = MaterialTheme.typography.bodySmall)
//
//                            Row(
//                                Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.End
//                            ) {
//                                Checkbox(
//                                    checked = selected,
//                                    onCheckedChange = null
//                                )
//                            }
//                        }
//                    }
//                }
//            }

//

            if (hasSearched && !loading && flightOptions.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "❌ Flight not exists for this path.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Or maybe you've entered wrong IATA code.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(flightOptions) { flight ->
                        val selected = selectedFlights.contains(flight)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    if (selected) {
                                        selectedFlights.remove(flight)
                                    } else if (selectedFlights.size < 3) {
                                        selectedFlights.add(flight)
                                    }
                                }
                                .then(
                                    if (selected)
                                        Modifier
                                            .background(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                shape = MaterialTheme.shapes.medium
                                            )
                                            .border(
                                                width = 2.dp,
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = MaterialTheme.shapes.medium
                                            )
                                    else Modifier
                                ),
                            shape = MaterialTheme.shapes.medium,
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp, horizontal = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "✈️ ${flight.flight?.iata} | ${flight.airline?.name}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "   ${flight.departure?.iata} → ${flight.arrival?.iata}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = " ${flight.flight_date ?: "-"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            if (notEnoughFlights) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚠️ Need 3 flights for confirmation.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try again with another IATA.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            depIata = ""
                            arrIata = ""
                            flightOptions = emptyList()
                            selectedFlights.clear()
                            searchDisabled = false
                            hasSearched = false
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Restart")
                    }
                }
            }




            if (selectedFlights.size == 3 && !notEnoughFlights) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val now = System.currentTimeMillis()
                            dao.clearSelectedFlightsFull()
                            selectedFlights.forEach {
                                dao.saveSelectedFlightFull(
                                    SelectedFlightFull(
                                        iata = it.flight?.iata ?: return@forEach,
                                        airlineName = it.airline?.name,
                                        flightDate = it.flight_date,
                                        depIata = it.departure?.iata,
                                        depGate = it.departure?.gate,
                                        depTerminal = it.departure?.terminal,
                                        depScheduled = it.departure?.scheduled,
                                        depActual = it.departure?.actual,
                                        arrIata = it.arrival?.iata,
                                        arrGate = it.arrival?.gate,
                                        arrTerminal = it.arrival?.terminal,
                                        arrScheduled = it.arrival?.scheduled,
                                        arrActual = it.arrival?.actual,
                                        lastUpdated = now,
                                        insertedAt = now
                                    )
                                )
                                dao.saveHistory(
                                    FlightHistory(
                                        flightIata = it.flight?.iata ?: return@forEach,
                                        date = it.flight_date,
                                        depScheduled = it.departure?.scheduled,
                                        depActual = it.departure?.actual,
                                        arrScheduled = it.arrival?.scheduled,
                                        arrActual = it.arrival?.actual,
                                        depDelayMinutes = it.departure?.delay,
                                        arrDelayMinutes = it.arrival?.delay
                                    )
                                )
                            }
                            // Reset all UI state
                            depIata = ""
                            arrIata = ""
                            flightOptions = emptyList()
                            selectedFlights.clear()
                            searchDisabled = false
                            hasSearched = false

                            // Show confirmation
                            snackbarHostState.showSnackbar("✅ Flights updated in the database to track")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirm Selection")
                }
            }
        }
    }
}
