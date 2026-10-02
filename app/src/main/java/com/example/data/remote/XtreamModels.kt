package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonElement

@JsonClass(generateAdapter = true)
data class XtreamAuthResponse(
    @Json(name = "user_info") val userInfo: XtreamUserInfo?,
    @Json(name = "server_info") val serverInfo: XtreamServerInfo?
)

@JsonClass(generateAdapter = true)
data class XtreamUserInfo(
    @Json(name = "username") val username: String?,
    @Json(name = "status") val status: String?,
    @Json(name = "exp_date") val expDate: String?,
    @Json(name = "is_trial") val isTrial: String?,
    @Json(name = "active_cons") val activeCons: String?,
    @Json(name = "max_connections") val maxConnections: String?
)

@JsonClass(generateAdapter = true)
data class XtreamServerInfo(
    @Json(name = "url") val url: String?,
    @Json(name = "port") val port: String?,
    @Json(name = "server_protocol") val serverProtocol: String?,
    @Json(name = "timezone") val timezone: String?
)

@JsonClass(generateAdapter = true)
data class XtreamCategory(
    @Json(name = "category_id") val categoryId: String,
    @Json(name = "category_name") val categoryName: String,
    @Json(name = "parent_id") val parentId: Int = 0
)

@JsonClass(generateAdapter = true)
data class XtreamLiveStream(
    @Json(name = "num") val num: Int? = 0,
    @Json(name = "name") val name: String,
    @Json(name = "stream_type") val streamType: String? = "live",
    @Json(name = "stream_id") val streamId: Int,
    @Json(name = "stream_icon") val streamIcon: String?,
    @Json(name = "epg_channel_id") val epgChannelId: String?,
    @Json(name = "category_id") val categoryId: String?
)

@JsonClass(generateAdapter = true)
data class XtreamVodStream(
    @Json(name = "num") val num: Int? = 0,
    @Json(name = "name") val name: String,
    @Json(name = "stream_type") val streamType: String? = "movie",
    @Json(name = "stream_id") val streamId: Int,
    @Json(name = "stream_icon") val streamIcon: String?,
    @Json(name = "rating") val rating: JsonElement? = null,
    @Json(name = "category_id") val categoryId: String?,
    @Json(name = "container_extension") val containerExtension: String? = "mp4",
    @Json(name = "releasedate") val releaseDate: String? = null,
    @Json(name = "genre") val genre: String? = null,
    @Json(name = "plot") val plot: String? = null,
    @Json(name = "duration_secs") val durationSecs: Int? = 0
)

@JsonClass(generateAdapter = true)
data class XtreamSeriesItem(
    @Json(name = "series_id") val seriesId: Int,
    @Json(name = "name") val name: String,
    @Json(name = "cover") val cover: String?,
    @Json(name = "plot") val plot: String?,
    @Json(name = "cast") val cast: String?,
    @Json(name = "genre") val genre: String?,
    @Json(name = "releaseDate") val releaseDate: String?,
    @Json(name = "rating") val rating: Double? = 0.0,
    @Json(name = "category_id") val categoryId: String?
)


@JsonClass(generateAdapter = true)
data class XtreamSeriesInfoResponse(
    @Json(name = "seasons") val seasons: List<XtreamSeason>? = emptyList(),
    @Json(name = "episodes") val episodes: Map<String, List<XtreamEpisode>>? = emptyMap()
)

@JsonClass(generateAdapter = true)
data class XtreamSeason(
    @Json(name = "season_number") val seasonNumber: Int,
    @Json(name = "name") val name: String? = null
)

@JsonClass(generateAdapter = true)
data class XtreamEpisode(
    @Json(name = "id") val id: String? = null,
    @Json(name = "episode_num") val episodeNum: Int? = 0,
    @Json(name = "title") val title: String? = null,
    @Json(name = "container_extension") val containerExtension: String? = "mp4",
    @Json(name = "info") val info: XtreamEpisodeInfo? = null,
    @Json(name = "season") val season: Int? = 1
)

@JsonClass(generateAdapter = true)
data class XtreamEpisodeInfo(
    @Json(name = "plot") val plot: String? = null,
    @Json(name = "duration_secs") val durationSecs: Int? = 0
)
