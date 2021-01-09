package com.harishtk.goldrate.app.util;

import android.annotation.SuppressLint;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Linkify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.annimon.stream.Stream;
import com.harishtk.goldrate.app.data.Link;
import com.harishtk.goldrate.app.util.guava.Optional;
import com.harishtk.goldrate.app.util.guava.OptionalUtil;

import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.HttpUrl;
import timber.log.Timber;

public final class LinkPreviewUtil {

  private static final String TAG = LinkPreviewUtil.class.getSimpleName();

  private static final Pattern DOMAIN_PATTERN             = Pattern.compile("^(https?://)?([^/]+).*$");
  private static final Pattern ALL_ASCII_PATTERN          = Pattern.compile("^[\\x00-\\x7F]*$");
  private static final Pattern ALL_NON_ASCII_PATTERN      = Pattern.compile("^[^\\x00-\\x7F]*$");
  private static final Pattern OPEN_GRAPH_TAG_PATTERN     = Pattern.compile("<\\s*meta[^>]*property\\s*=\\s*\"\\s*og:([^\"]+)\"[^>]*/?\\s*>");
  private static final Pattern ARTICLE_TAG_PATTERN        = Pattern.compile("<\\s*meta[^>]*property\\s*=\\s*\"\\s*article:([^\"]+)\"[^>]*/?\\s*>");
  private static final Pattern OPEN_GRAPH_CONTENT_PATTERN = Pattern.compile("content\\s*=\\s*\"([^\"]*)\"");
  private static final Pattern TITLE_PATTERN              = Pattern.compile("<\\s*title[^>]*>(.*)<\\s*/title[^>]*>");
  private static final Pattern FAVICON_PATTERN            = Pattern.compile("<\\s*link[^>]*rel\\s*=\\s*\".*icon.*\"[^>]*>");
  private static final Pattern FAVICON_HREF_PATTERN       = Pattern.compile("href\\s*=\\s*\"([^\"]*)\"");
  private static final Pattern GOLD_RATE_22K_PATTERN      = Pattern.compile("<\\s*li[^>]*><\\s*span[^>]*>(.+?)</span><\\s*span[^>]*><\\s*span\\s*class=\"price\"[^>]*>(.+?)</span></li>");
  private static final Pattern GOLD_RATE_PATTERN      = Pattern.compile("<\\s*li[^>]*><\\s*span[^>]*>(.+?)</span><\\s*span[^>]*><\\s*span\\s*class=\"price\"[^>]*>(.+?)</span></span></li>");
  private static final Pattern GOLD_RATE_22K_PATTERN0      = Pattern.compile("<\\s*li[^>]*><\\s*span[^>]*>Gold 22k</span><\\s*span[^>]*><\\s*span\\s*class=\"price\"[^>]*>");
  // <li style="margin-left: 8%;"><span class="left">Gold 22k</span><span><span class="price">₹4,754</span></span></li>

  private static final Set<String> INVALID_TOP_LEVEL_DOMAINS = SetUtil.newHashSet("onion", "i2p");

  /**
   * @return All whitelisted URLs in the source text.
   */
  public static @NonNull List<Link> findValidPreviewUrls(@NonNull String text) {
    SpannableString spannable = new SpannableString(text);
    boolean         found     = Linkify.addLinks(spannable, Linkify.WEB_URLS);

    if (!found) {
      return Collections.emptyList();
    }

    return Stream.of(spannable.getSpans(0, spannable.length(), URLSpan.class))
                 .map(span -> new Link(span.getURL(), spannable.getSpanStart(span)))
                 .filter(link -> isValidPreviewUrl(link.getUrl()))
                 .toList();
  }

  public static String getDomain(@NonNull String url) {
    try {
      URI uri = new URI(url);
      String domain = uri.getHost();
      return domain.startsWith("www.") ? domain.substring(4) : domain;
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    return url;
  }

  /**
   * @return True if the host is present in the link whitelist.
   */
  public static boolean isValidPreviewUrl(@Nullable String linkUrl) {
    if (linkUrl == null)                      return false;

    HttpUrl url = HttpUrl.parse(linkUrl);
    return url != null                                   &&
           !TextUtils.isEmpty(url.scheme())              &&
           "https".equals(url.scheme())                  &&
           isLegalUrl(linkUrl);
  }

  public static boolean isLegalUrl(@NonNull String url) {
    Matcher matcher = DOMAIN_PATTERN.matcher(url);

    if (matcher.matches()) {
      String domain         = matcher.group(2);
      String cleanedDomain  = domain.replaceAll("\\.", "");
      String topLevelDomain = parseTopLevelDomain(domain);

      boolean validCharacters = ALL_ASCII_PATTERN.matcher(cleanedDomain).matches() ||
                                ALL_NON_ASCII_PATTERN.matcher(cleanedDomain).matches();

      boolean validTopLevelDomain = !INVALID_TOP_LEVEL_DOMAINS.contains(topLevelDomain);

      return validCharacters &&  validTopLevelDomain;
    } else {
      return false;
    }
  }

  public static @NonNull Map<String, String> parseGoldRates(@Nullable String html) {
    if (html == null) return Collections.emptyMap();
    Map<String, String> map = new HashMap<>();

    Matcher goldMatcher = GOLD_RATE_PATTERN.matcher(html);

    while (goldMatcher.find()) {
      if (goldMatcher.groupCount() >= 2) {
        final String key = goldMatcher.group(1);
        final String val = goldMatcher.group(2);

        Timber.d("Spider: raw %s %s", key, val);
        if (key != null && val != null) {
          map.put(key, val);
        }
      }
    }
    return map;
  }

  public static @NonNull OpenGraph parseOpenGraphFields(@Nullable String html) {
    return parseOpenGraphFields(html, text -> Html.fromHtml(text).toString());
  }

  @VisibleForTesting
  static @NonNull OpenGraph parseOpenGraphFields(@Nullable String html, @NonNull HtmlDecoder htmlDecoder) {
    if (html == null) {
      return new OpenGraph(Collections.emptyMap(), null, null);
    }

    Map<String, String> openGraphTags    = new HashMap<>();
    Matcher             openGraphMatcher = OPEN_GRAPH_TAG_PATTERN.matcher(html);

    while (openGraphMatcher.find()) {
      String tag      = openGraphMatcher.group();
      String property = openGraphMatcher.groupCount() > 0 ? openGraphMatcher.group(1) : null;

      if (property != null) {
        Matcher contentMatcher = OPEN_GRAPH_CONTENT_PATTERN.matcher(tag);
        if (contentMatcher.find() && contentMatcher.groupCount() > 0) {
          String content = htmlDecoder.fromEncoded(contentMatcher.group(1));
          openGraphTags.put(property.toLowerCase(), content);
        }
      }
    }

    Matcher articleMatcher = ARTICLE_TAG_PATTERN.matcher(html);

    while (articleMatcher.find()) {
      String tag      = articleMatcher.group();
      String property = articleMatcher.groupCount() > 0 ? articleMatcher.group(1) : null;

      if (property != null) {
        Matcher contentMatcher = OPEN_GRAPH_CONTENT_PATTERN.matcher(tag);
        if (contentMatcher.find() && contentMatcher.groupCount() > 0) {
          String content = htmlDecoder.fromEncoded(contentMatcher.group(1));
          openGraphTags.put(property.toLowerCase(), content);
        }
      }
    }

    String htmlTitle  = "";
    String faviconUrl = "";

    Matcher titleMatcher = TITLE_PATTERN.matcher(html);
    if (titleMatcher.find() && titleMatcher.groupCount() > 0) {
      htmlTitle = htmlDecoder.fromEncoded(titleMatcher.group(1));
    }

    Matcher faviconMatcher = FAVICON_PATTERN.matcher(html);
    if (faviconMatcher.find()) {
      Matcher faviconHrefMatcher = FAVICON_HREF_PATTERN.matcher(faviconMatcher.group());
      if (faviconHrefMatcher.find() && faviconHrefMatcher.groupCount() > 0) {
        faviconUrl = faviconHrefMatcher.group(1);
      }
    }

    return new OpenGraph(openGraphTags, htmlTitle, faviconUrl);
  }

  private static @Nullable String parseTopLevelDomain(@NonNull String domain) {
    int periodIndex = domain.lastIndexOf(".");

    if (periodIndex >= 0 && periodIndex < domain.length() - 1) {
      return domain.substring(periodIndex + 1);
    } else {
      return null;
    }
  }


  public static final class OpenGraph {

    private final Map<String, String> values;

    private final @Nullable String htmlTitle;
    private final @Nullable String faviconUrl;

    private static final String DEFAULT_TYPE         = "website";

    private static final String KEY_TITLE            = "title";
    private static final String KEY_DESCRIPTION_URL  = "description";
    private static final String KEY_IMAGE_URL        = "image";
    private static final String KEY_PUBLISHED_TIME_1 = "published_time";
    private static final String KEY_PUBLISHED_TIME_2 = "article:published_time";
    private static final String KEY_MODIFIED_TIME_1  = "modified_time";
    private static final String KEY_MODIFIED_TIME_2  = "article:modified_time";
    private static final String KEY_TYPE             = "type";
    private static final String KEY_IMAGE_WIDTH      = "image:width";
    private static final String KEY_IMAGE_HEIGHT     = "image:height";
    private static final String KEY_VIDEO_WIDTH      = "video:width";
    private static final String KEY_VIDEO_HEIGHT     = "video:height";

    public OpenGraph(@NonNull Map<String, String> values, @Nullable String htmlTitle, @Nullable String faviconUrl) {
      this.values     = values;
      this.htmlTitle  = htmlTitle;
      this.faviconUrl = faviconUrl;
    }

    public @NonNull
    Optional<String> getTitle() {
      return OptionalUtil.absentIfEmpty(Util.getFirstNonEmpty(values.get(KEY_TITLE), htmlTitle));
    }

    public @NonNull Optional<String> getImageUrl() {
      return OptionalUtil.absentIfEmpty(Util.getFirstNonEmpty(values.get(KEY_IMAGE_URL), faviconUrl));
    }

    @SuppressLint("ObsoleteSdkInt")
    public long getDate() {
      return Stream.of(values.get(KEY_PUBLISHED_TIME_1),
                       values.get(KEY_PUBLISHED_TIME_2),
                       values.get(KEY_MODIFIED_TIME_1),
                       values.get(KEY_MODIFIED_TIME_2))
                   .map(DateUtils::parseIso8601)
                   .filter(time -> time > 0)
                   .findFirst()
                   .orElse(0L);
    }

    public @NonNull
    Optional<String> getDescription() {
      return OptionalUtil.absentIfEmpty(values.get(KEY_DESCRIPTION_URL));
    }

    public @NonNull
    Optional<String> getType() {
        return OptionalUtil.defaultIfEmpty(values.get(KEY_TYPE), DEFAULT_TYPE);
    }

    public @NonNull Optional<String> getImageSize() {
      Optional<String> width = OptionalUtil.absentIfEmpty(values.get(KEY_IMAGE_WIDTH));
      Optional<String> height = OptionalUtil.absentIfEmpty(values.get(KEY_IMAGE_HEIGHT));
      if (width.isPresent() && height.isPresent()) {
        return Optional.of(width.get() + ":" + height.get());
      } else {
        return Optional.absent();
      }
    }

    public @NonNull Optional<String> getVideoSize() {
      Optional<String> width = OptionalUtil.absentIfEmpty(values.get(KEY_VIDEO_WIDTH));
      Optional<String> height = OptionalUtil.absentIfEmpty(values.get(KEY_VIDEO_HEIGHT));
      if (width.isPresent() && height.isPresent()) {
        return Optional.of(width.get() + ":" + height.get());
      } else {
        return Optional.absent();
      }
    }
  }

  public interface HtmlDecoder {
    @NonNull String fromEncoded(@NonNull String html);
  }
}
