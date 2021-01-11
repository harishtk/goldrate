package com.harishtk.goldrate.app.thread;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.harishtk.goldrate.app.net.OkHttpUtil;
import com.harishtk.goldrate.app.net.UserAgentInterceptor;
import com.harishtk.goldrate.app.util.ByteUnit;
import com.harishtk.goldrate.app.util.LinkPreviewUtil;
import com.harishtk.goldrate.app.util.guava.Optional;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class GoldSpiderCallable<T extends Optional<Map<String, String>>> implements Callable<Optional<Map<String, String>>> {

    private static final String TAG = GoldSpiderCallable.class.getSimpleName();

    private static final long FAILSAFE_MAX_TEXT_SIZE  = ByteUnit.MEGABYTES.toBytes(2);

    private static final CacheControl NO_CACHE = new CacheControl.Builder().noCache().build();
    private static final String TAG_CRAWL_REQUEST = "crawl-request";

    private Callback mCallback;

    private final Bundle mBundle;

    private final OkHttpClient client;

    private boolean canceled = false;

    public GoldSpiderCallable(@NonNull Bundle b) {
        mBundle = b;
        this.client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .addInterceptor(new UserAgentInterceptor(""))
                .retryOnConnectionFailure(false)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public Optional<Map<String, String>> call() {
        final String url = mBundle.getString("url");
        Optional<Map<String, String>> optional = Optional.absent();

        try {
            if (url == null) throw new IllegalArgumentException("No URL");
            Log.d(TAG, String.format("Crawling: url %s", url));
            Request request = new Request.Builder().url(url).tag(TAG_CRAWL_REQUEST).cacheControl(NO_CACHE).build();
            Response response = client.newCall(request).execute();
            if (response.body() == null) throw new IOException("Response is null for " + request.url());
            final int code = response.code();
            if (!(code >= 200 && code < 300)) throw new IOException("Unexpected response for " + request.url());

            String           body        = OkHttpUtil.readAsString(response.body(), FAILSAFE_MAX_TEXT_SIZE);
            Map<String, String> rateMap  = LinkPreviewUtil.parseGoldRates(body);
            if (rateMap.keySet().size() > 0) {
                optional = Optional.of(rateMap);
            }

            Timber.d("Spider: %s", rateMap);

            if (!canceled && mCallback != null) {
                mCallback.onSuccess(optional);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (!canceled && mCallback != null) {
                mCallback.onError(Error.PREVIEW_NOT_AVAILABLE, new Exception("No Html Received from " + url + " Check your Internet " + e.getLocalizedMessage()));
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            if (!canceled && mCallback != null) {
                mCallback.onError(Error.PREVIEW_NOT_AVAILABLE, e);
            }
        }
        return optional;
    }

    public void setCallback(@NonNull Callback callback) {
        mCallback = callback;
    }

    public void cancel() {
        canceled = true;
    }

    public interface Callback {
        void onSuccess(@NonNull Optional<Map<String, String>> data);

        void onError(@NonNull Error error, @NonNull Throwable t);
    }

    public enum Error {
        PREVIEW_NOT_AVAILABLE,
        GROUP_LINK_INACTIVE,
        CANCELLED
    }
}