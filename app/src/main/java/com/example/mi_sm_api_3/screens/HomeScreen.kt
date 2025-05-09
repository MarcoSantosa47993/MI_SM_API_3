package com.example.mi_sm_api_3.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mi_sm_api_3.models.Group
import com.example.mi_sm_api_3.models.Match
import com.example.mi_sm_api_3.models.Team
import com.example.mi_sm_api_3.viewmodels.HomeViewModel


import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val homeViewModel: HomeViewModel = viewModel()
    val matchState by homeViewModel.matchState
    val teamState by homeViewModel.teamState
    val showSortIcon by homeViewModel.showSortIcon
    val currentLeague by homeViewModel.currentLeague
    val currentSeason by homeViewModel.currentSeason
    val selectedTeam by homeViewModel.selectedTeam
    val availableGroups = homeViewModel.availableGroups

    var leagueExpanded by remember { mutableStateOf(false) }
    var seasonExpanded by remember { mutableStateOf(false) }
    var teamExpanded by remember { mutableStateOf(false) }
    var groupExpanded by remember { mutableStateOf(false) }

    val sortOrder by remember { derivedStateOf { homeViewModel.currentSortOrder.value } }

    val years = remember { (2020..2024).toList().reversed() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 60.dp, horizontal = 20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Estatísticas da Bundesliga",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 20.dp),
                textAlign = TextAlign.Center
            )
        }


        FilterSection(
            currentLeague = currentLeague,
            currentSeason = currentSeason,
            selectedTeam = selectedTeam,
            selectedGroup = homeViewModel.selectedGroup.value,
            availableGroups = availableGroups,
            years = years,
            teamList = teamState.list,
            onLeagueSelected = { league -> homeViewModel.updateFilter(league, currentSeason) },
            onYearSelected = { year -> homeViewModel.updateFilter(currentLeague, year) },
            onTeamSelected = { team -> homeViewModel.updateTeam(team) },
            onGroupSelected = { group -> homeViewModel.updateGroup(group) },
            onSortOrderChanged = { homeViewModel.toggleSortOrder() },
            showSortIcon = showSortIcon,
            sortOrder = sortOrder,
            leagueExpanded = leagueExpanded,
            seasonExpanded = seasonExpanded,
            teamExpanded = teamExpanded,
            groupExpanded = groupExpanded,
            onLeagueExpandedChange = { leagueExpanded = it },
            onSeasonExpandedChange = { seasonExpanded = it },
            onTeamExpandedChange = { teamExpanded = it },
            onGroupExpandedChange = { groupExpanded = it }
        )

        Spacer(modifier = Modifier.padding(8.dp))



        when {
            matchState.loading -> LoadingState()
            matchState.error != null -> ErrorState(message = matchState.error!!)
            matchState.list.isEmpty() -> EmptyState()
            else -> MatchGrid(matches = matchState.list)
        }
    }
}

@Composable
private fun FilterSection(
    currentLeague: String,
    currentSeason: Int,
    selectedTeam: Team?,
    selectedGroup: Group?,
    availableGroups: List<Group>,
    years: List<Int>,
    teamList: List<Team>,
    onLeagueSelected: (String) -> Unit,
    onYearSelected: (Int) -> Unit,
    onTeamSelected: (Team?) -> Unit,
    onGroupSelected: (Group?) -> Unit,
    onSortOrderChanged: () -> Unit,
    showSortIcon: Boolean,
    sortOrder: HomeViewModel.SortOrder,
    leagueExpanded: Boolean,
    seasonExpanded: Boolean,
    teamExpanded: Boolean,
    groupExpanded: Boolean,
    onLeagueExpandedChange: (Boolean) -> Unit,
    onSeasonExpandedChange: (Boolean) -> Unit,
    onTeamExpandedChange: (Boolean) -> Unit,
    onGroupExpandedChange: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterDropdown(
                modifier = Modifier.weight(1f),
                label = "Liga",
                selectedValue = currentLeague.uppercase(),
                icon = Icons.Filled.Create,
                expanded = leagueExpanded,
                onExpandedChange = onLeagueExpandedChange
            ) {
                listOf("bl1", "bl2").forEach { league ->
                    DropdownMenuItem(
                        text = { Text(league.uppercase()) },
                        onClick = {
                            onLeagueSelected(league)
                            onLeagueExpandedChange(false)
                        }
                    )
                }
            }

            FilterDropdown(
                modifier = Modifier.weight(1f),
                label = "Temporada",
                selectedValue = currentSeason.toString(),
                icon = Icons.Outlined.DateRange,
                expanded = seasonExpanded,
                onExpandedChange = onSeasonExpandedChange
            ) {
                years.forEach { year ->
                    DropdownMenuItem(
                        text = { Text(year.toString()) },
                        onClick = {
                            onYearSelected(year)
                            onSeasonExpandedChange(false)
                        }
                    )
                }
            }
        }


        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterDropdown(
                modifier = Modifier.weight(1f),
                label = "Time",
                selectedValue = selectedTeam?.teamName ?: "Todos",
                icon = Icons.Outlined.Person,
                expanded = teamExpanded,
                onExpandedChange = onTeamExpandedChange
            ) {
                DropdownMenuItem(
                    text = { Text("Todas as Equipas") },
                    onClick = {
                        onTeamSelected(null)
                        onTeamExpandedChange(false)
                    }
                )
                teamList.forEach { team ->
                    DropdownMenuItem(
                        text = { Text(team.teamName) },
                        onClick = {
                            onTeamSelected(team)
                            onTeamExpandedChange(false)
                        }
                    )
                }
            }

            FilterDropdown(
                modifier = Modifier.weight(1f),
                label = "Jornada",
                selectedValue = selectedGroup?.let { formatGroupText(it) } ?: "Todas",
                icon = Icons.Filled.Home,
                expanded = groupExpanded,
                onExpandedChange = onGroupExpandedChange
            ) {
                DropdownMenuItem(
                    text = { Text("Todas as Jornadas") },
                    onClick = {
                        onGroupSelected(null)
                        onGroupExpandedChange(false)
                    }
                )
                availableGroups.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(formatGroupText(group)) },
                        onClick = {
                            onGroupSelected(group)
                            onGroupExpandedChange(false)
                        }
                    )
                }
            }
        }

        if (showSortIcon) {
            IconButton(
                onClick = onSortOrderChanged,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .size(54.dp)
            ) {
                Icon(
                    imageVector = when (sortOrder) {
                        HomeViewModel.SortOrder.ASC -> Icons.Filled.KeyboardArrowUp
                        HomeViewModel.SortOrder.DESC -> Icons.Filled.KeyboardArrowDown
                    },
                    contentDescription = "Ordenação",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

fun formatGroupText(group: Group): String {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val startDate = group.startDate?.format(dateFormatter) ?: "N/A"
    val endDate = group.endDate?.format(dateFormatter) ?: "N/A"
    return "Jornada ${group.groupOrderID} ($startDate - $endDate)"
}

@Composable
private fun FilterDropdown(
    modifier: Modifier = Modifier,
    label: String,
    selectedValue: String,
    icon: ImageVector,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { onExpandedChange(!expanded) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = selectedValue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            content()
        }
    }
}

@Composable
private fun MatchGrid(matches: List<Match>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(matches) { match ->
            MatchCard(match)
        }
    }
}

@Composable
private fun MatchCard(match: Match) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            TeamInfoCompact(
                team = match.team1,
                modifier = Modifier.weight(1f),
                alignment = Alignment.Start
            )


            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1.2f)
            ) {

                MatchStatusHeader(match)


                ScoreDisplay(match)


                MatchDetails(match)
            }


            TeamInfoCompact(
                team = match.team2,
                modifier = Modifier.weight(1f),
                alignment = Alignment.End
            )
        }
    }
}

@Composable
private fun TeamInfoCompact(team: Team, modifier: Modifier, alignment: Alignment.Horizontal) {
    Column(
        modifier = modifier,
        horizontalAlignment = alignment,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = team.teamIconUrl,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Fit
        )

        Text(
            text = team.teamName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = if (alignment == Alignment.Start) TextAlign.Start else TextAlign.End,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(0.8f)
        )
    }
}

@Composable
private fun MatchStatusHeader(match: Match) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Jornada ${match.group?.groupOrderID ?: ""}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = formatMatchDate(match.matchDateTime),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun ScoreDisplay(match: Match) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(vertical = 12.dp)
    ) {
        val result = match.matchResults.find { it.resultTypeID == 2 }

        if (result != null) {
            Text(
                text = result.pointsTeam1.toString(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "–",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = result.pointsTeam2.toString(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black
            )
        } else {
            Text(
                text = "VS",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun MatchDetails(match: Match) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = if (match.matchResults.isEmpty()) "Agendado" else "Finalizado",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun formatMatchDate(dateTimeString: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val dateTime = LocalDateTime.parse(dateTimeString, formatter)
        dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy • HH:mm"))
    } catch (e: Exception) {
        "Data inválida"
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Carregando jogos...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "⚠️ $message",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Nenhum jogo encontrado",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}