package com.focus.flow.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class AnimeDeserializer : JsonDeserializer<Anime> {
    
    private inline fun <reified T> parseArrayField(
        jsonElement: JsonElement?,
        context: JsonDeserializationContext?
    ): List<T>? {
        return jsonElement?.takeIf { !it.isJsonNull }?.let { element ->
            try {
                if (element.isJsonArray) {
                    val array = element.asJsonArray
                    array.map { context?.deserialize<T>(it, T::class.java) }.filterNotNull()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Anime {
        val jsonObject = json?.asJsonObject
        return Anime(
            id = jsonObject?.get("mal_id")?.takeIf { !it.isJsonNull }?.asInt ?: 0,
            title = jsonObject?.get("title")?.takeIf { !it.isJsonNull }?.asString,
            titleEnglish = jsonObject?.get("title_english")?.takeIf { !it.isJsonNull }?.asString,
            titleJapanese = jsonObject?.get("title_japanese")?.takeIf { !it.isJsonNull }?.asString,
            type = jsonObject?.get("type")?.takeIf { !it.isJsonNull }?.asString,
            source = jsonObject?.get("source")?.takeIf { !it.isJsonNull }?.asString,
            episodes = jsonObject?.get("episodes")?.takeIf { !it.isJsonNull }?.asInt,
            status = jsonObject?.get("status")?.takeIf { !it.isJsonNull }?.asString,
            airing = jsonObject?.get("airing")?.takeIf { !it.isJsonNull }?.asBoolean,
            duration = jsonObject?.get("duration")?.takeIf { !it.isJsonNull }?.asString,
            rating = jsonObject?.get("rating")?.takeIf { !it.isJsonNull }?.asString,
            score = jsonObject?.get("score")?.takeIf { !it.isJsonNull }?.asDouble,
            scoredBy = jsonObject?.get("scored_by")?.takeIf { !it.isJsonNull }?.asInt,
            rank = jsonObject?.get("rank")?.takeIf { !it.isJsonNull }?.asInt,
            popularity = jsonObject?.get("popularity")?.takeIf { !it.isJsonNull }?.asInt,
            members = jsonObject?.get("members")?.takeIf { !it.isJsonNull }?.asInt,
            favorites = jsonObject?.get("favorites")?.takeIf { !it.isJsonNull }?.asInt,
            synopsis = jsonObject?.get("synopsis")?.takeIf { !it.isJsonNull }?.asString,
            season = jsonObject?.get("season")?.takeIf { !it.isJsonNull }?.asString,
            year = jsonObject?.get("year")?.takeIf { !it.isJsonNull }?.asInt,
            broadcast = jsonObject?.get("broadcast")?.takeIf { !it.isJsonNull }?.let { context?.deserialize<Broadcast>(it, Broadcast::class.java) },
            producer = parseArrayField<Producer>(jsonObject?.get("producers"), context),
            licensor = parseArrayField<Licensor>(jsonObject?.get("licensors"), context),
            studio = parseArrayField<Studio>(jsonObject?.get("studios"), context),
            genre = parseArrayField<Genre>(jsonObject?.get("genres"), context),
            explicitGenre = parseArrayField<Genre>(jsonObject?.get("explicit_genres"), context),
            theme = parseArrayField<Genre>(jsonObject?.get("themes"), context),
            demographic = parseArrayField<Genre>(jsonObject?.get("demographics"), context),
            images = jsonObject?.get("images")?.takeIf { !it.isJsonNull }?.let { context?.deserialize<Images>(it, Images::class.java) },
            trailer = jsonObject?.get("trailer")?.takeIf { !it.isJsonNull }?.let { context?.deserialize<Trailer>(it, Trailer::class.java) },
            url = jsonObject?.get("url")?.takeIf { !it.isJsonNull }?.asString,
            aired = jsonObject?.get("aired")?.takeIf { !it.isJsonNull }?.let { context?.deserialize<Aired>(it, Aired::class.java) },
            isFavorite = false,
            lastUpdated = System.currentTimeMillis()
        )
    }
}

@Entity(tableName = "anime")
data class Anime(
    @PrimaryKey 
    val id: Int,
    val title: String?,
    val titleEnglish: String?,
    val titleJapanese: String?,
    val type: String?,
    val source: String?,
    val episodes: Int?,
    val status: String?,
    val airing: Boolean?,
    val duration: String?,
    val rating: String?,
    val score: Double?,
    val scoredBy: Int?,
    val rank: Int?,
    val popularity: Int?,
    val members: Int?,
    val favorites: Int?,
    val synopsis: String?,
    val season: String?,
    val year: Int?,
    val broadcast: Broadcast?,
    val producer: List<Producer>?,
    val licensor: List<Licensor>?,
    val studio: List<Studio>?,
    val genre: List<Genre>?,
    val explicitGenre: List<Genre>?,
    val theme: List<Genre>?,
    val demographic: List<Genre>?,
    val images: Images?,
    val trailer: Trailer?,
    val url: String?,
    val aired: Aired?,
    val isFavorite: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val displayTitle: String
        get() = title ?: titleEnglish ?: titleJapanese ?: "Unknown Title"
        
    val displayType: String
        get() = type ?: "Unknown"
        
    val displayStatus: String
        get() = status ?: "Unknown"
        
    val displayDuration: String
        get() = duration ?: "Unknown"
        
    val displaySource: String
        get() = source ?: "Unknown"
        
    val displaySynopsis: String
        get() = synopsis ?: "No synopsis available"
        
    val displayUrl: String
        get() = url ?: ""
}

data class Broadcast(
    val day: String?,
    val time: String?,
    val timezone: String?,
    val string: String?
)

data class Producer(
    @SerializedName("mal_id")
    val malId: Int,
    val type: String,
    val name: String,
    val url: String
)

data class Licensor(
    @SerializedName("mal_id")
    val malId: Int,
    val type: String,
    val name: String,
    val url: String
)

data class Studio(
    @SerializedName("mal_id")
    val malId: Int,
    val type: String,
    val name: String,
    val url: String
)

data class Genre(
    @SerializedName("mal_id")
    val malId: Int,
    val type: String,
    val name: String,
    val url: String
)

data class Images(
    val jpg: ImageUrls?,
    val webp: ImageUrls?
)

data class ImageUrls(
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("small_image_url")
    val smallImageUrl: String?,
    @SerializedName("large_image_url")
    val largeImageUrl: String?
)

data class Trailer(
    @SerializedName("youtube_id")
    val youtubeId: String?,
    val url: String?,
    @SerializedName("embed_url")
    val embedUrl: String?,
    val images: TrailerImages?
)

data class TrailerImages(
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("small_image_url")
    val smallImageUrl: String?,
    @SerializedName("medium_image_url")
    val mediumImageUrl: String?,
    @SerializedName("large_image_url")
    val largeImageUrl: String?,
    @SerializedName("maximum_image_url")
    val maximumImageUrl: String?
)

data class Aired(
    val from: String?,
    val to: String?,
    val prop: AiredProp?,
    val string: String?
)

data class AiredProp(
    val from: AiredDate?,
    val to: AiredDate?
)

data class AiredDate(
    val day: Int?,
    val month: Int?,
    val year: Int?
)

data class AnimeListResponse(
    val pagination: Pagination,
    val data: List<Anime>
)

data class Pagination(
    @SerializedName("last_visible_page")
    val lastVisiblePage: Int,
    @SerializedName("has_next_page")
    val hasNextPage: Boolean,
    @SerializedName("current_page")
    val currentPage: Int,
    val items: PaginationItems
)

data class PaginationItems(
    val count: Int,
    val total: Int,
    @SerializedName("per_page")
    val perPage: Int
)

data class AnimeDetailResponse(
    val data: Anime
)
