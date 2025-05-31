package com.example.mi_sm_api_3.models





import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class Match(
    val matchID: Long,
    val matchDateTime: String,
    val timeZoneID: String,
    val leagueId: Int,
    val leagueName: String,
    val leagueSeason: Int,
    val leagueShortcut: String,
    val matchDateTimeUTC: String,
    val group: Group?,
    val team1: Team,
    val team2: Team,
    val lastUpdateDateTime: String,
    val matchIsFinished: Boolean,
    val matchResults: List<MatchResult>,
    val goals: List<Goal>,
    val location: Location?,
    val numberOfViewers: Int?
)

data class Team(
    val teamId: Long,
    val teamName: String,
    val teamIconUrl: String
)

data class Location(
    val locationID: Int,
    val locationCity: String,
    val locationStadium: String
)

data class Group(
    val groupID: Int,
    val groupName: String,
    val groupOrderID: Int,
    val startDate: LocalDate?,
    val endDate: LocalDate?
)

data class MatchResult(
    val resultTypeID: Int,
    val pointsTeam1: Int,
    val pointsTeam2: Int
)

data class TableEntry(
    val position: Int,
    val teamName: String,
    val points: Int
)

data class Goal(
    @SerializedName("goalID")
    val goalID: Long,

    @SerializedName("matchMinute")
    val minute: Int,

    @SerializedName("goalGetterID")
    val scorerId: Long,

    @SerializedName("goalGetterName")
    val scorerName: String?,

    @SerializedName("isPenalty")
    val isPenalty: Boolean,

    @SerializedName("isOwnGoal")
    val isOwnGoal: Boolean,

    @SerializedName("isOvertime")
    val isOverTime: Boolean,

    @SerializedName("scoreTeam1")
    val scoreTeam1: Int,

    @SerializedName("scoreTeam2")
    val scoreTeam2: Int,

    @SerializedName("comment")
    val comment: String?,

    val teamId: Long = 0L
) {

    fun deriveTeamId(team1Id: Long, team2Id: Long): Long {
        return if (scoreTeam1 > scoreTeam2) team1Id else team2Id
    }
}