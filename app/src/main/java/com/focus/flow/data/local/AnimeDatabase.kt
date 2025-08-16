package com.focus.flow.data.local

import androidx.room.*
import com.focus.flow.domain.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Database(
    entities = [Anime::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AnimeDatabase : RoomDatabase() {
    abstract fun animeDao(): AnimeDao
}

class Converters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromBroadcast(broadcast: Broadcast?): String? {
        return gson.toJson(broadcast)
    }
    
    @TypeConverter
    fun toBroadcast(broadcastString: String?): Broadcast? {
        return if (broadcastString != null) {
            gson.fromJson(broadcastString, Broadcast::class.java)
        } else null
    }
    
    @TypeConverter
    fun fromProducerList(producers: List<Producer>?): String? {
        return gson.toJson(producers)
    }
    
    @TypeConverter
    fun toProducerList(producerString: String?): List<Producer>? {
        return if (producerString != null) {
            gson.fromJson(producerString, object : TypeToken<List<Producer>>() {}.type)
        } else null
    }
    
    @TypeConverter
    fun fromLicensorList(licensors: List<Licensor>?): String? {
        return gson.toJson(licensors)
    }
    
    @TypeConverter
    fun toLicensorList(licensorString: String?): List<Licensor>? {
        return if (licensorString != null) {
            gson.fromJson(licensorString, object : TypeToken<List<Licensor>>() {}.type)
        } else null
    }
    
    @TypeConverter
    fun fromStudioList(studios: List<Studio>?): String? {
        return gson.toJson(studios)
    }
    
    @TypeConverter
    fun toStudioList(studioString: String?): List<Studio>? {
        return if (studioString != null) {
            gson.fromJson(studioString, object : TypeToken<List<Studio>>() {}.type)
        } else null
    }
    
    @TypeConverter
    fun fromGenreList(genres: List<Genre>?): String? {
        return gson.toJson(genres)
    }
    
    @TypeConverter
    fun toGenreList(genreString: String?): List<Genre>? {
        return if (genreString != null) {
            gson.fromJson(genreString, object : TypeToken<List<Genre>>() {}.type)
        } else null
    }
    
    @TypeConverter
    fun fromImages(images: Images?): String? {
        return gson.toJson(images)
    }
    
    @TypeConverter
    fun toImages(imagesString: String?): Images? {
        return if (imagesString != null) {
            gson.fromJson(imagesString, Images::class.java)
        } else null
    }
    
    @TypeConverter
    fun fromTrailer(trailer: Trailer?): String? {
        return gson.toJson(trailer)
    }
    
    @TypeConverter
    fun toTrailer(trailerString: String?): Trailer? {
        return if (trailerString != null) {
            gson.fromJson(trailerString, Trailer::class.java)
        } else null
    }
    
    @TypeConverter
    fun fromAired(aired: Aired?): String? {
        return gson.toJson(aired)
    }
    
    @TypeConverter
    fun toAired(airedString: String?): Aired? {
        return if (airedString != null) {
            gson.fromJson(airedString, Aired::class.java)
        } else null
    }
}
