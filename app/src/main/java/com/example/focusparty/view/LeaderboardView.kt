package com.example.focusparty.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.focusparty.ui.components.HighSurface
import com.example.focusparty.ui.components.HomeBottomBar
import com.example.focusparty.ui.theme.*
import com.example.focusparty.viewmodel.LeaderboardEntry
import com.example.focusparty.viewmodel.LeaderboardMetric
import com.example.focusparty.viewmodel.LeaderboardTarget
import com.example.focusparty.viewmodel.LeaderboardViewModel

@Composable
fun LeaderboardScreen(
    vm: LeaderboardViewModel
) {
    val target by vm.target.collectAsState()
    val metric by vm.metric.collectAsState()
    val ranking by vm.ranking.collectAsState()

    Scaffold(
        bottomBar = {
            HomeBottomBar(
                currentDestination = "Leaderboard",
                onNavigate = { dest -> vm.goToDestination(dest) }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                )
        ) {

            // Sélecteur Joueurs / Salons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FilterChip(
                    selected = target == LeaderboardTarget.PLAYERS,
                    onClick = { vm.setTarget(LeaderboardTarget.PLAYERS) },
                    label = { Text("Joueurs") }
                )
                Spacer(Modifier.width(12.dp))
                FilterChip(
                    selected = target == LeaderboardTarget.ROOMS,
                    onClick = { vm.setTarget(LeaderboardTarget.ROOMS) },
                    label = { Text("Salons") }
                )
            }

            Spacer(Modifier.height(20.dp))

            // Sélecteur Temps / Points / Jalons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FilterChip(
                    selected = metric == LeaderboardMetric.TIME,
                    onClick = { vm.setMetric(LeaderboardMetric.TIME) },
                    label = { Text("Temps") }
                )
                Spacer(Modifier.width(12.dp))

                if (target == LeaderboardTarget.PLAYERS) {
                    FilterChip(
                        selected = metric == LeaderboardMetric.POINTS,
                        onClick = { vm.setMetric(LeaderboardMetric.POINTS) },
                        label = { Text("Points") }
                    )
                    Spacer(Modifier.width(12.dp))
                }

                FilterChip(
                    selected = metric == LeaderboardMetric.MILESTONES,
                    onClick = { vm.setMetric(LeaderboardMetric.MILESTONES) },
                    label = { Text("Jalons") }
                )
            }

            Spacer(Modifier.height(24.dp))

            // LISTE DU CLASSEMENT — MANQUANTE !!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                itemsIndexed(ranking) { index, entry ->
                    RowItem(index, entry, metric)
                }
            }
        }
    }
}




@Composable
fun RowItem(index: Int, entry: LeaderboardEntry, metric: LeaderboardMetric) {

    HighSurface(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        color = when (index) {
            0 -> colorPurpledark1
            1 -> colorPurple
            2 -> colorPurplelight1
            else -> colorPurplelight2
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "${index + 1}. ${entry.name}",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )

            Text(
                text = when (metric) {
                    LeaderboardMetric.TIME -> formatTime(entry)
                    LeaderboardMetric.POINTS -> formatPoints(entry)
                    LeaderboardMetric.MILESTONES -> formatJalons(entry)
                },
                modifier = Modifier
                    .width(92.dp)
                    .padding(end = 4.dp),
                textAlign = TextAlign.End
            )
        }
    }
}


fun formatTime(entry : LeaderboardEntry):String{
    val totalMinutes = entry.value / 60000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return "${hours}h ${minutes}min"
}

fun formatPoints(entry : LeaderboardEntry) :String {
    return "${entry.value} points"
}

fun formatJalons(entry : LeaderboardEntry) :String {
    return "${entry.value} jalons"
}
