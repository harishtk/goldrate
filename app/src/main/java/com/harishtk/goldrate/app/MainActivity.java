package com.harishtk.goldrate.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.harishtk.goldrate.app.thread.BackgroundThreadFactory;
import com.harishtk.goldrate.app.thread.GoldSpiderCallable;
import com.harishtk.goldrate.app.util.guava.Optional;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    public static final String CRAWL_URL = "https://thangamayil.com";

    public ExecutorService mExecutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Bundle b = new Bundle();
        b.putString("url", CRAWL_URL);
        GoldSpiderCallable<?> callable = new GoldSpiderCallable<>(b);
        callable.setCallback(new GoldSpiderCallable.Callback() {

            @Override
            public void onSuccess(@NonNull @NotNull Optional<Map<String, String>> data) {

            }

            @Override
            public void onError(@NonNull @NotNull GoldSpiderCallable.Error error, @NonNull @NotNull Throwable t) {
                Timber.e(t);
            }
        });

        findViewById(R.id.textView).setOnClickListener(v -> {
            mExecutorService.submit(callable);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mExecutorService = Executors.newSingleThreadExecutor(new BackgroundThreadFactory());
    }

    @Override
    protected void onStop() {
        super.onStop();

        mExecutorService.shutdownNow();
    }
}