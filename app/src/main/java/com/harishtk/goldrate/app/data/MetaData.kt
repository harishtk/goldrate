package com.harishtk.goldrate.app.data;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ponna on 16-01-2018.
 * <p>
 * Moded by Haris 23 Nov 2020
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public class MetaData implements Serializable {

    public static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile("http(?:s)?://(?:m.)?(?:www\\.)?youtu(?:\\.be/|be\\.com/(?:watch\\?(?:feature=youtu.be&)?v=|v/|embed/|user/(?:[\\w#]+/)+))([^&#?\n]+)", Pattern.CASE_INSENSITIVE);

    private static final List<String> VIDEO_TYPE_LOOKUP = Arrays.asList(
            "video",
            "video.other"
    );
    private static final List<String> IMAGE_TYPE_LOOKUP = Arrays.asList(
            "instapp:photo",
            "image",
            "photo"
    );
    @Expose
    private String url;
    @Expose
    private String imageURL;
    @Expose
    private String title;
    @Expose
    private String description;
    @Expose
    private String siteName;
    @Expose
    private String mediaType;
    @Expose
    private String favicon;
    @Expose
    private String imageSize;
    @Expose
    private String videoSize;

    {
        // Initialization block
        url = imageURL = title = description = siteName = mediaType =
                favicon = imageSize = videoSize = "";
    }

    @NonNull
    public static MetaData empty() {
        return new MetaData();
    }

    public static MetaData fromJson(@NonNull String jsonString) {
        try {
            //noinspection ConstantConditions
            if (jsonString == null || jsonString.length() <= 0)
                throw new IllegalArgumentException("Nothing to parse");
            return new Gson().fromJson(jsonString, new TypeToken<MetaData>() {
            }.getType());
        } catch (Exception e) {
            // e.printStackTrace();
            return MetaData.empty();
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSiteName() {
        return siteName.toLowerCase();
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

    public String getImageSize() {
        return imageSize;
    }

    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public String getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(String videoSize) {
        this.videoSize = videoSize;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public boolean isValidURL() {
        return url != null && url.length() > 0;
    }

    public boolean isValid() {
        return ((title != null && title.length() > 0) ||
                (description != null && description.length() > 0) ||
                (imageURL != null && imageURL.length() > 0));
    }

    public boolean hasVideoSize() {
        return (videoSize != null) &&
                (videoSize.length() > 0) &&
                (videoSize.split(":").length > 1);
    }

    public boolean hasImageSize() {
        return (imageSize != null) &&
                (imageSize.length() > 0) &&
                (imageSize.split(":").length > 1);
    }

    public boolean isYoutubeLink() {
        return url != null && YOUTUBE_URL_PATTERN.matcher(url).find();
    }

    public String getYTVideoID() {
        if (!isYoutubeLink()) {
            IllegalAccessException e = new IllegalAccessException(url + " is not a valid YouTube link");
            e.printStackTrace();
            return null;
        }
        Matcher matcher = YOUTUBE_URL_PATTERN.matcher(url);

        if (matcher.find())
            return matcher.group(1);
        return null;
    }

    @Override
    @NonNull
    public String toString() {
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
                '}';
    }
}
