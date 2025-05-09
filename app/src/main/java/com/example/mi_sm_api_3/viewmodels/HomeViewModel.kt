package com.example.mi_sm_api_3.viewmodels



import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mi_sm_api_3.api.matchService
import com.example.mi_sm_api_3.models.Group
import com.example.mi_sm_api_3.models.Match
import com.example.mi_sm_api_3.models.Team

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel() {
    enum class SortOrder { ASC, DESC }

    private val _matchState = mutableStateOf(MatchState())
    val matchState: State<MatchState> = _matchState

    private val _teamState = mutableStateOf(TeamState())
    val teamState: State<TeamState> = _teamState

    private val _currentSortOrder = mutableStateOf(SortOrder.DESC)
    val currentSortOrder: State<SortOrder> get() = _currentSortOrder

    private val _selectedGroup = mutableStateOf<Group?>(null)
    val selectedGroup: State<Group?> = _selectedGroup

    private val _showSortIcon = mutableStateOf(false)
    val showSortIcon: State<Boolean> get() = _showSortIcon

    private val _currentLeague = mutableStateOf("bl1")
    val currentLeague: State<String> get() = _currentLeague


    private val _currentSeason = mutableStateOf(2024)
    val currentSeason: State<Int> get() = _currentSeason

    private val _selectedTeam = mutableStateOf<Team?>(null)
    val selectedTeam: State<Team?> get() = _selectedTeam

    private var allMatches: List<Match> = emptyList()
    private var groupsData: Map<Group, List<Match>> = emptyMap()

    val availableGroups: List<Group> get() = groupsData.keys.toList().sortedBy { it.groupOrderID }

    init {
        _currentSeason.value = 2024
        fetchAllData()
    }

    fun updateFilter(league: String, season: Int) {
        if (league != _currentLeague.value || season != _currentSeason.value) {
            _currentLeague.value = league
            _currentSeason.value = season
            _selectedTeam.value = null
            _selectedGroup.value = null
            _showSortIcon.value = false
            _currentSortOrder.value = SortOrder.DESC
            fetchAllData()
        }
    }

    fun updateTeam(team: Team?) {
        _selectedTeam.value = team
        _showSortIcon.value = team != null
        if (team != null) {
            _currentSortOrder.value = SortOrder.DESC
        }
        filterMatches()
    }

    fun updateGroup(group: Group?) {
        _selectedGroup.value = group
        filterMatches()
    }



    fun toggleSortOrder() {
        _currentSortOrder.value = when (_currentSortOrder.value) {
            SortOrder.ASC -> SortOrder.DESC
            SortOrder.DESC -> SortOrder.ASC
        }
        filterMatches()
    }

    private fun fetchAllData() {
        viewModelScope.launch {
            _matchState.value = MatchState(loading = true)
            _teamState.value = TeamState(loading = true)

            try {
                val matchesDeferred = async(Dispatchers.IO) {
                    matchService.getAllMatches(_currentLeague.value, _currentSeason.value.toString())
                }
                val teamsDeferred = async(Dispatchers.IO) {
                    matchService.getTeams(_currentLeague.value, _currentSeason.value.toString())
                }

                allMatches = matchesDeferred.await()
                val teams = teamsDeferred.await()

                groupsData = withContext(Dispatchers.Default) {
                    allMatches
                        .filter { it.group != null }
                        .groupBy { it.group!! }
                        .map { (group, matches) ->
                            val dates = matches.map { LocalDateTime.parse(it.matchDateTime, DateTimeFormatter.ISO_DATE_TIME) }
                            val start = dates.minOrNull()?.toLocalDate()
                            val end = dates.maxOrNull()?.toLocalDate()
                            group.copy(startDate = start, endDate = end) to matches
                        }
                        .toMap()
                }


                _teamState.value = TeamState(list = teams)


                filterMatches()

            } catch (e: Exception) {
                val msg = "Erro ao obter dados: ${'$'}{e.localizedMessage}"
                _matchState.value = MatchState(loading = false, error = msg)
                _teamState.value = TeamState(loading = false, error = msg)
            }
        }
    }


    private fun filterMatches() {
        var filtered = allMatches


        _selectedGroup.value?.let { group ->
            filtered = groupsData[group] ?: emptyList()
        }


        _selectedTeam.value?.let { team ->
            filtered = filtered.filter {
                it.team1.teamId == team.teamId || it.team2.teamId == team.teamId
            }
        }

        val sortedMatches = when (_currentSortOrder.value) {
            SortOrder.ASC -> filtered.sortedBy { parseDateTime(it.matchDateTime) }
            SortOrder.DESC -> filtered.sortedByDescending { parseDateTime(it.matchDateTime) }
        }

        val error = when {
            sortedMatches.isEmpty() && (_selectedTeam.value != null || _selectedGroup.value != null) -> "Nenhum jogo encontrado para os filtros selecionados"
            sortedMatches.isEmpty() -> "Nenhum jogo encontrado"
            else -> null
        }

        _matchState.value = MatchState(
            loading = false,
            list = sortedMatches,
            error = error
        )
    }

    private fun parseDateTime(dateTimeString: String): LocalDateTime {
        return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
    }

    data class MatchState(
        val loading: Boolean = false,
        val list: List<Match> = emptyList(),
        val error: String? = null
    )

    data class TeamState(
        val loading: Boolean = false,
        val list: List<Team> = emptyList(),
        val error: String? = null
    )
}