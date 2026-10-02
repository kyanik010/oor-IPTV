package com.example.data.parser

import com.example.data.model.CategoryEntity
import com.example.data.model.ChannelEntity
import com.example.data.model.ContentType
import com.example.data.model.MovieEntity
import com.example.data.model.SeriesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern

data class ParsedPlaylistResult(
    val categories: List<CategoryEntity>,
    val channels: List<ChannelEntity>,
    val movies: List<MovieEntity>,
    val series: List<SeriesEntity>
)

object M3uParser {

    private val TVG_ID_PATTERN = Pattern.compile("tvg-id=\"([^\"]*)\"")
    private val TVG_NAME_PATTERN = Pattern.compile("tvg-name=\"([^\"]*)\"")
    private val TVG_LOGO_PATTERN = Pattern.compile("tvg-logo=\"([^\"]*)\"")
    private val GROUP_TITLE_PATTERN = Pattern.compile("group-title=\"([^\"]*)\"")

    suspend fun parseFromUrl(playlistUrl: String): ParsedPlaylistResult = withContext(Dispatchers.IO) {
        val url = URL(playlistUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 15000
        connection.readTimeout = 30000
        connection.requestMethod = "GET"
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
        connection.connect()

        connection.inputStream.use { inputStream ->
            parseFromStream(inputStream)
        }
    }

    suspend fun parseFromStream(inputStream: InputStream): ParsedPlaylistResult = withContext(Dispatchers.Default) {
        val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8), 32768)

        val channels = ArrayList<ChannelEntity>()
        val movies = ArrayList<MovieEntity>()
        val series = ArrayList<SeriesEntity>()
        val categoryMap = LinkedHashMap<String, CategoryEntity>()

        var currentExtInf: String? = null
        var lineIndex = 0
        var streamIdCounter = 1

        var line: String? = reader.readLine()
        while (line != null) {
            val trimmed = line.trim()
            if (trimmed.startsWith("#EXTINF:")) {
                currentExtInf = trimmed
            } else if (trimmed.isNotEmpty() && !trimmed.startsWith("#") && currentExtInf != null) {
                val streamUrl = trimmed
                val extInf = currentExtInf

                // Parse attributes
                val tvgId = extractAttribute(extInf, TVG_ID_PATTERN)
                val tvgLogo = extractAttribute(extInf, TVG_LOGO_PATTERN)
                val groupTitle = extractAttribute(extInf, GROUP_TITLE_PATTERN) ?: "General"
                val channelName = extInf.substringAfterLast(",").trim().ifEmpty {
                    extractAttribute(extInf, TVG_NAME_PATTERN) ?: "Channel $streamIdCounter"
                }

                val lowerGroup = groupTitle.lowercase()
                val lowerUrl = streamUrl.lowercase()

                // Determine classification
                val isVod = lowerGroup.contains("movie") || lowerGroup.contains("vod") ||
                        lowerGroup.contains("أفلام") || lowerUrl.endsWith(".mp4") || lowerUrl.endsWith(".mkv")
                val isSeries = lowerGroup.contains("series") || lowerGroup.contains("مسلسل") ||
                        lowerGroup.contains("season") || lowerGroup.contains("s0")

                val categoryId = groupTitle.hashCode().toString()
                val contentType = when {
                    isSeries -> ContentType.SERIES
                    isVod -> ContentType.MOVIE
                    else -> ContentType.LIVE
                }

                if (!categoryMap.containsKey(categoryId)) {
                    categoryMap[categoryId] = CategoryEntity(
                        categoryId = categoryId,
                        categoryName = groupTitle,
                        type = contentType
                    )
                }

                val id = streamIdCounter++
                when {
                    isSeries -> {
                        series.add(
                            SeriesEntity(
                                seriesId = id,
                                name = channelName,
                                cover = tvgLogo,
                                categoryId = categoryId
                            )
                        )
                    }
                    isVod -> {
                        movies.add(
                            MovieEntity(
                                streamId = id,
                                name = channelName,
                                streamIcon = tvgLogo,
                                categoryId = categoryId,
                                streamUrl = streamUrl
                            )
                        )
                    }
                    else -> {
                        channels.add(
                            ChannelEntity(
                                streamId = id,
                                num = channels.size + 1,
                                name = channelName,
                                streamIcon = tvgLogo,
                                epgChannelId = tvgId,
                                categoryId = categoryId,
                                streamUrl = streamUrl
                            )
                        )
                    }
                }

                currentExtInf = null
            }
            lineIndex++
            line = reader.readLine()
        }

        ParsedPlaylistResult(
            categories = categoryMap.values.toList(),
            channels = channels,
            movies = movies,
            series = series
        )
    }

    private fun extractAttribute(line: String, pattern: Pattern): String? {
        val matcher = pattern.matcher(line)
        return if (matcher.find()) matcher.group(1)?.trim() else null
    }
}
