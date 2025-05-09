package com.example.mi_sm_api_3.api




import com.example.mi_sm_api_3.models.Match
import com.example.mi_sm_api_3.models.TableEntry
import com.example.mi_sm_api_3.models.Team
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private val retrofit = Retrofit.Builder()
    .baseUrl("https://api.openligadb.de/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val matchService = retrofit.create(ApiService::class.java)

interface ApiService {
    @GET("getmatchdata/{leagueShortcut}/{season}/{group}")
    suspend fun getMatchesByGroup(
        @Path("leagueShortcut") league: String,
        @Path("season") season: String,
        @Path("group") group: Int
    ): List<Match>

    @GET("getmatchdata/{leagueShortcut}/{season}")
    suspend fun getAllMatches(
        @Path("leagueShortcut") league: String,
        @Path("season") season: String
    ): List<Match>

    @GET("getbltable/{leagueShortcut}/{season}")
    suspend fun getTable(
        @Path("leagueShortcut") league: String,
        @Path("season") season: String
    ): List<TableEntry>

    @GET("getavailableteams/{leagueShortcut}/{season}")
    suspend fun getTeams(
        @Path("leagueShortcut") league: String,
        @Path("season") season: String
    ): List<Team>
}