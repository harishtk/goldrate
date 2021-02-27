package com.harishtk.goldrate.app.net

import com.harishtk.goldrate.app.util.ByteUnit
import okhttp3.ResponseBody
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*

object OkHttpUtil {
    @Throws(IOException::class)
    @JvmStatic
    fun readAsBytes(bodyStream: InputStream, sizeLimit: Long): ByteArray {
        val outputStream = ByteArrayOutputStream()

        val buffer = ByteArray(ByteUnit.KILOBYTES.toBytes(32).toInt())
        var readLength: Int
        var totalLength = 0

        while (bodyStream.read(buffer).also { readLength = it } >= 0) {
            if (totalLength + readLength > sizeLimit) {
                throw IOException("Exceeded maximum size during read!")
            }
            outputStream.write(buffer, 0, readLength)
            totalLength += readLength
        }

        return outputStream.toByteArray()
    }

    @Throws(IOException::class)
    @JvmStatic
    fun readAsString(body: ResponseBody, sizeLimit: Long): String {
        if (body.contentLength() > sizeLimit) {
            throw IOException("Content-Length exceeded maximum size!")
        }

        val data = readAsBytes(body.byteStream(), sizeLimit)
        val contentType = body.contentType()
        val charset = if (contentType != null) {
            contentType.charset(StandardCharsets.UTF_8)
        } else {
            StandardCharsets.UTF_8
        }

        return String(data, Objects.requireNonNull(charset!!))
    }
}