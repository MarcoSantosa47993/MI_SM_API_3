package com.example.mi_sm_api_3.models



import java.time.LocalDate

data class Match(
    val matchID: Long,
    val matchDateTime: String,
    val team1: Team,
    val team2: Team,
    val matchResults: List<MatchResult>,
    val group: Group?
)

data class Team(
    val teamId: Long,
    val teamName: String,
    val teamIconUrl: String
)
data class Group(
    val groupID: Int,
    val groupName: String,
    val groupOrderID: Int,
    val startDate: LocalDate?,
    val endDate: LocalDate?
)
data class MatchResult(val resultTypeID: Int, val pointsTeam1: Int, val pointsTeam2: Int)
data class TableEntry(val position: Int, val teamName: String, val points: Int)



