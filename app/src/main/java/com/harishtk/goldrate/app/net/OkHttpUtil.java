package com.harishtk.goldrate.app.net;

import androidx.annotation.NonNull;

import com.harishtk.goldrate.app.util.ByteUnit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.ResponseBody;

public final class OkHttpUtil {

    private OkHttpUtil() {}

    public static byte[] readAsBytes(@NonNull InputStream bodyStream, long sizeLimit) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer      = new byte[(int) ByteUnit.KILOBYTES.toBytes(32)];
        int    readLength  = 0;
        int    totalLength = 0;

        while ((readLength = bodyStream.read(buffer)) >= 0) {
            if (totalLength + readLength > sizeLimit) {
                throw new IOException("Exceeded maximum size during read!");
            }

            outputStream.write(buffer, 0, readLength);
            totalLength += readLength;
        }

        return outputStream.toByteArray();
    }
    public static String readAsString(@NonNull ResponseBody body, long sizeLimit) throws IOException {
        if (body.contentLength() > sizeLimit) {
            throw new IOException("Content-Length exceeded maximum size!");
        }

        byte[]    data        = readAsBytes(body.byteStream(), sizeLimit);
        MediaType contentType = body.contentType();
        Charset   charset     = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            charset = contentType != null
                    ? contentType.charset(StandardCharsets.UTF_8) :
                    StandardCharsets.UTF_8;
        } else {
            charset = Charset.forName("UTF-8");
        }

        return new String(data, Objects.requireNonNull(charset));
    }
}
