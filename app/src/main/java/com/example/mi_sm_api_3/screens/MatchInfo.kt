package com.example.mi_sm_api_3.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mi_sm_api_3.models.Goal
import com.example.mi_sm_api_3.models.Match
import com.example.mi_sm_api_3.models.Team
import com.example.mi_sm_api_3.utils.CoiledImage
import com.example.mi_sm_api_3.viewmodels.MatchInfoViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MatchInfoScreen(navController: NavController, matchId: Long) {
    val viewModel: MatchInfoViewModel = viewModel()
    val matchState by viewModel.matchState

    LaunchedEffect(matchId) {
        viewModel.fetchMatchDetails(matchId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                matchState.loading -> LoadingState(Modifier.fillMaxSize())
                matchState.error != null -> ErrorState(
                    message = matchState.error!!,
                    modifier = Modifier.fillMaxSize()
                )
                matchState.match != null -> MatchDetailFull(
                    navController = navController,
                    match = matchState.match!!,
                    modifier = Modifier.fillMaxSize()
                )
                else -> EmptyState(Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun MatchDetailFull(
    navController: NavController,
    match: Match,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Voltar"
                )
            }
            Text(
                text = "Data: ${formatMatchDate(match.matchDateTime)}",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TeamsAndGoalsSection(match = match)
        }
    }
}

@Composable
private fun TeamsAndGoalsSection(match: Match) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.TopStart
            ) {
                TeamHeader(
                    team = match.team1,
                    horizontalAlignment = Alignment.Start,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                )
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                ScoreDisplay(match = match)
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.TopEnd
            ) {
                TeamHeader(
                    team = match.team2,
                    horizontalAlignment = Alignment.End,
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                match.goals
                    .filter { it.teamId == match.team1.teamId }
                    .forEach { goal ->
                        Text(
                            text = "${goal.scorerName} (${goal.minute}')",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                match.goals
                    .filter { it.teamId == match.team2.teamId }
                    .forEach { goal ->
                        Text(
                            text = "${goal.scorerName} (${goal.minute}')",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
            }
        }
    }
}

@Composable
private fun TeamHeader(
    team: Team,
    horizontalAlignment: Alignment.Horizontal,
    textAlign: androidx.compose.ui.text.style.TextAlign
) {
    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        verticalArrangement = Arrangement.Center
    ) {
        CoiledImage(
            url = team.teamIconUrl,
            contentDescription = "${team.teamName} logo",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = team.teamName,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
            textAlign = textAlign
        )
    }
}

@Composable
private fun ScoreDisplay(match: Match) {
    val result = match.matchResults.find { it.resultTypeID == 2 }
    Column(
        modifier = Modifier
            .height(120.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (result != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = result.pointsTeam1.toString(),
                    style = MaterialTheme.typography.displayMedium
                )
                Text(
                    text = "–",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = result.pointsTeam2.toString(),
                    style = MaterialTheme.typography.displayMedium
                )
            }
        } else {
            Text(
                text = "A definir",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Carregando detalhes da partida…")
    }
}

@Composable
private fun ErrorState(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Erro: $message",
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Nenhum dado disponível")
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
