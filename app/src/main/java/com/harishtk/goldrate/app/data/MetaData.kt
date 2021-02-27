package com.harishtk.goldrate.app.data

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.util.*
import java.util.regex.Pattern

/**
 * Created by ponna on 16-01-2018.
 *
 *
 * Moded by Haris 23 Nov 2020
 */
class MetaData : Serializable {
    @Expose
    var url: String? = null

    @Expose
    var imageURL: String? = null

    @Expose
    var title: String? = null

    @Expose
    var description: String? = null

    @Expose
    private var siteName: String? = null

    @Expose
    var mediaType: String? = null

    @Expose
    var favicon: String? = null

    @Expose
    var imageSize: String? = null

    @Expose
    var videoSize: String? = null
    fun getSiteName(): String {
        return siteName!!.toLowerCase()
    }

    fun setSiteName(siteName: String?) {
        this.siteName = siteName
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }

    val isValidURL: Boolean
        get() = url != null && url!!.length > 0
    val isValid: Boolean
        get() = title != null && title!!.length > 0 ||
                description != null && description!!.length > 0 ||
                imageURL != null && imageURL!!.length > 0

    fun hasVideoSize(): Boolean {
        return videoSize != null &&
                videoSize!!.length > 0 &&
                videoSize!!.split(":".toRegex()).toTypedArray().size > 1
    }

    fun hasImageSize(): Boolean {
        return imageSize != null &&
                imageSize!!.length > 0 &&
                imageSize!!.split(":".toRegex()).toTypedArray().size > 1
    }

    val isYoutubeLink: Boolean
        get() = url != null && YOUTUBE_URL_PATTERN.matcher(url).find()
    val yTVideoID: String?
        get() {
            if (!isYoutubeLink) {
                val e = IllegalAccessException("$url is not a valid YouTube link")
                e.printStackTrace()
                return null
            }
            val matcher = YOUTUBE_URL_PATTERN.matcher(url)
            return if (matcher.find()) matcher.group(1) else null
        }

    override fun toString(): String {
        return "MetaData{" +
                "url='" + url + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", siteName='" + siteName + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", favicon='" + favicon + '\'' +
                ", imageSize='" + imageSize + '\'' +
                ", videoSize='" + videoSize + '\'' +
                '}'
    }

    companion object {
        val YOUTUBE_URL_PATTERN = Pattern.compile("http(?:s)?://(?:m.)?(?:www\\.)?youtu(?:\\.be/|be\\.com/(?:watch\\?(?:feature=youtu.be&)?v=|v/|embed/|user/(?:[\\w#]+/)+))([^&#?\n]+)", Pattern.CASE_INSENSITIVE)
        private val VIDEO_TYPE_LOOKUP = Arrays.asList(
                "video",
                "video.other"
        )
        private val IMAGE_TYPE_LOOKUP = Arrays.asList(
                "instapp:photo",
                "image",
                "photo"
        )

        fun empty(): MetaData {
            return MetaData()
        }

        fun fromJson(jsonString: String): MetaData {
            return try {
                require(!(jsonString == null || jsonString.length <= 0)) { "Nothing to parse" }
                Gson().fromJson(jsonString, object : TypeToken<MetaData?>() {}.type)
            } catch (e: Exception) {
                // e.printStackTrace();
                empty()
            }
        }
    }

    init {
        // Initialization block
        videoSize = ""
        imageSize = videoSize
        favicon = imageSize
        mediaType = favicon
        siteName = mediaType
        description = siteName
        title = description
        imageURL = title
        url = imageURL
    }
}