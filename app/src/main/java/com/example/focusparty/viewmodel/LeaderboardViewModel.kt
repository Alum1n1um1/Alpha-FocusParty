package com.example.focusparty.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.focusparty.model.Database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


enum class LeaderboardTarget { PLAYERS, ROOMS }
enum class LeaderboardMetric { TIME, POINTS, MILESTONES, LEVEL }

data class LeaderboardEntry(
    val name: String,
    val value: Long
)

class LeaderboardViewModel (
    private val db: Database,
    private val navController: NavController
): ViewModel(){

    private val _target = MutableStateFlow(LeaderboardTarget.PLAYERS)
    val target: StateFlow<LeaderboardTarget> = _target

    private val _metric = MutableStateFlow(LeaderboardMetric.TIME)
    val metric: StateFlow<LeaderboardMetric> = _metric

    private val _ranking = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val ranking: StateFlow<List<LeaderboardEntry>> = _ranking

    init {
        loadRanking()
    }

    fun setTarget(t: LeaderboardTarget) {
        _target.value = t
        loadRanking()
    }

    fun setMetric(m: LeaderboardMetric) {
        _metric.value = m
        loadRanking()
    }

    fun loadRanking() {
        viewModelScope.launch {

            val entries: List<LeaderboardEntry> = when (target.value) {

                LeaderboardTarget.PLAYERS -> {
                    when (metric.value) {
                        LeaderboardMetric.TIME       -> db.getPlayersRankedByTime()
                        LeaderboardMetric.POINTS     -> db.getPlayersRankedByPoints()
                        LeaderboardMetric.MILESTONES -> db.getPlayersRankedByMilestones()
                        LeaderboardMetric.LEVEL      -> db.getPlayersRankedByLevel()
                    }
                }

                LeaderboardTarget.ROOMS -> {
                    when (metric.value) {
                        LeaderboardMetric.TIME       -> db.getRoomsRankedByTime()
                        LeaderboardMetric.POINTS     -> emptyList()              // non applicable
                        LeaderboardMetric.MILESTONES -> db.getRoomsRankedByMilestones()
                        LeaderboardMetric.LEVEL      -> db.getRoomsRankedByLevel()
                    }
                }
            }

            _ranking.value = entries
        }
    }


    fun goToDestination(dest: String) {
        navController.navigate(dest)
    }

}