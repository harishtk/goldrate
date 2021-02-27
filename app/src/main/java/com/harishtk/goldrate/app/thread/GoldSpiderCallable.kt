package com.harishtk.goldrate.app.thread

import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import com.harishtk.goldrate.app.net.OkHttpUtil
import com.harishtk.goldrate.app.net.UserAgentInterceptor
import com.harishtk.goldrate.app.util.ByteUnit
import com.harishtk.goldrate.app.util.LinkPreviewUtil
import com.harishtk.goldrate.app.util.guava.Optional
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit


class GoldSpiderCallable<T : Optional<Map<String, String>>>(
        private var bundle: Bundle
): Callable<Optional<Map<String, String>>> {

    private var callback: Callback? = null

    private var client: OkHttpClient

    private var canceled = false

    init {
        OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .addInterceptor(UserAgentInterceptor(""))
                .readTimeout(15, TimeUnit.SECONDS)
                .build().also { client = it }
    }

    override fun call(): Optional<Map<String, String>> {
        val url: String = bundle.getString("url", "")
        var optional: Optional<Map<String, String>> = Optional.absent()

        try {
            if (url.isEmpty()) throw IllegalArgumentException("No URL")
            Log.d(TAG, "Crawling: url $url")
            val request: Request = Request.Builder().apply {
                url(url)
                tag(TAG_CRAWL_REQUEST)
                cacheControl(NO_CACHE)
            }.build()
            val response = client.newCall(request).execute()
            if (response.body == null) throw IOException("Response is null for ${request.url}")
            val code = response.code
            if (!(code in 200..299)) throw IOException("Unexpected response for ${request.url}")

            val body    = OkHttpUtil.readAsString(response.body!!, FAILSAFE_MAX_TEXT_SIZE)
            val rateMap = LinkPreviewUtil.parseGoldRates(body)
            if (rateMap.keys.size > 0) {
                optional = Optional.of(rateMap)
            }

            Timber.d("Spider: %s", rateMap)

            if (!canceled && callback != null) {
                callback?.onSuccess(optional)
            }
        } catch (e: IOException) {
            e.printStackTrace();
            if (!canceled) {
                callback?.onError(Error.PREVIEW_NOT_AVAILABLE, Exception ("No Html Received from $url Check your Internet ${e.localizedMessage}"));
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace();
            if (!canceled) {
                callback?.onError(Error.PREVIEW_NOT_AVAILABLE, e);
            }
        }
        return optional
    }

    fun setCallback(@NonNull callback: Callback) {
        this@GoldSpiderCallable.callback = callback
    }

    fun cancel() {
        canceled = true
    }

    interface Callback {
        fun onSuccess(data: Optional<Map<String, String>>)
        fun onError(error: Error, t: Throwable)
    }

    enum class Error {
        PREVIEW_NOT_AVAILABLE, GROUP_LINK_INACTIVE, CANCELLED
    }

    companion object {
        val TAG: String = GoldSpiderCallable::class.java.simpleName
        private val FAILSAFE_MAX_TEXT_SIZE = ByteUnit.MEGABYTES.toBytes(2)

        private val NO_CACHE: CacheControl = CacheControl.Builder().noCache().build()
        private const val TAG_CRAWL_REQUEST = "crawl-request"
    }
}