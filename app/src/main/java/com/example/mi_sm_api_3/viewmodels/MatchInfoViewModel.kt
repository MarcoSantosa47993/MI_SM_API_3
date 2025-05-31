package com.example.mi_sm_api_3.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mi_sm_api_3.api.matchService
import com.example.mi_sm_api_3.models.Match
import kotlinx.coroutines.launch

class MatchInfoViewModel : ViewModel() {
    private val _matchState = mutableStateOf(MatchState())
    val matchState: State<MatchState> = _matchState

    fun fetchMatchDetails(matchId: Long) {
        viewModelScope.launch {
            _matchState.value = MatchState(loading = true)
            try {

                val raw: Match = matchService.getMatch(matchId.toString())


                val sorted = raw.goals.sortedBy { it.minute }

                var prev1 = 0
                var prev2 = 0
                val enriched = sorted.map { g ->
                    val isTeam1 = g.scoreTeam1 > prev1
                    val tid = if (isTeam1) raw.team1.teamId else raw.team2.teamId

                    prev1 = g.scoreTeam1
                    prev2 = g.scoreTeam2

                    g.copy(teamId = tid)
                }

                val fixedMatch = raw.copy(goals = enriched)


                _matchState.value = MatchState(match = fixedMatch)
            } catch (e: Exception) {
                _matchState.value = MatchState(error = "Erro ao obter detalhes: ${e.localizedMessage}")
                println("ERROR ▶ fetchMatchDetails exception: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }

    data class MatchState(
        val loading: Boolean = false,
        val match: Match? = null,
        val error: String? = null
    )
}
