package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class ContentType {
    LIVE, MOVIE, SERIES, EPISODE
}

enum class AccountType {
    XTREAM, M3U
}

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String,
    val type: AccountType,
    val serverUrl: String = "",
    val username: String = "",
    val password: String = "",
    val playlistUrl: String = "",
    val playlistName: String = "",
    val expiryTimestamp: Long? = null,
    val maxConnections: Int = 1,
    val isActive: Boolean = true
)

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val avatarRes: String = "avatar_1",
    val pinCode: String? = null,
    val isKids: Boolean = false,
    val isSelected: Boolean = false
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val categoryId: String,
    val categoryName: String,
    val parentId: Int = 0,
    val type: ContentType
)

@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey val streamId: Int,
    val num: Int = 0,
    val name: String,
    val streamIcon: String? = null,
    val epgChannelId: String? = null,
    val categoryId: String,
    val streamType: String = "live",
    val streamUrl: String = "",
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false
)

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val streamId: Int,
    val name: String,
    val streamIcon: String? = null,
    val rating: Double = 0.0,
    val year: String? = null,
    val genre: String? = null,
    val plot: String? = null,
    val durationSecs: Int = 0,
    val categoryId: String,
    val containerExtension: String = "mp4",
    val streamUrl: String = "",
    val isFavorite: Boolean = false
)

@Entity(tableName = "series")
data class SeriesEntity(
    @PrimaryKey val seriesId: Int,
    val name: String,
    val cover: String? = null,
    val rating: Double = 0.0,
    val year: String? = null,
    val genre: String? = null,
    val plot: String? = null,
    val categoryId: String,
    val isFavorite: Boolean = false
)

@Entity(tableName = "episodes")
data class EpisodeEntity(
    @PrimaryKey val episodeId: String,
    val seriesId: Int,
    val seasonNum: Int,
    val episodeNum: Int,
    val title: String,
    val durationSecs: Int = 0,
    val plot: String? = null,
    val containerExtension: String = "mp4",
    val streamUrl: String = ""
)

@Entity(tableName = "epg_programs")
data class EpgProgramEntity(
    @PrimaryKey val id: String,
    val channelId: String,
    val title: String,
    val description: String = "",
    val startTimestamp: Long,
    val stopTimestamp: Long,
    val hasCatchup: Boolean = false
)

@Entity(tableName = "watch_progress")
data class WatchProgressEntity(
    @PrimaryKey val contentId: String,
    val profileId: String,
    val title: String,
    val posterUrl: String? = null,
    val contentType: ContentType,
    val positionMs: Long,
    val durationMs: Long,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)

data class DeviceActivationInfo(
    val deviceId: String,
    val activationCode: String,
    val isActivated: Boolean,
    val trialDaysRemaining: Int,
    val expirationDateText: String,
    val planName: String = "Premium Pro"
)
