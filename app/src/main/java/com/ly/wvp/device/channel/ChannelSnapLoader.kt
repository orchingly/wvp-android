package com.ly.wvp.device.channel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.ly.wvp.auth.HttpConnectionClient
import com.ly.wvp.auth.ServerUrl
import com.ly.wvp.data.storage.SettingsConfig
import okhttp3.Request
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.roundToInt


/**
 * 加载通道快照
 * api/device/query/snap/41010500002000000001/34020000001320000001
 */
class ChannelSnapLoader(private val config: SettingsConfig){
    companion object{
        private const val TAG = "ChannelSnapLoader"
    }

    fun getBitmap(stream: InputStream): Bitmap?{
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        return BitmapFactory.decodeStream(stream, null, options)
    }

    fun getBitmapWithReqSize(byteArray: ByteArray, reqW: Int, reqH: Int): Bitmap?{
        if (reqW <= 0 || reqH <= 0){
            Log.w(TAG, "getBitmapWithReqSize: size must > 0,  reqW: $reqW, reqH: $reqH", )
            return null
        }

        //打印原始图片大小
        val origin  = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        if (origin != null){
            Log.d(TAG, "getBitmapWithReqSize: origin bitmap size " +
                    "${origin.byteCount / 1024} KB")
            origin.recycle()
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
            //采样
            options.inSampleSize = getInSampleSize(options, reqW, reqH)
            options.inJustDecodeBounds = false

            Log.d(TAG, "getBitmapWithReqSize: inSampleSize = ${options.inSampleSize}")
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
            Log.d(TAG, "getBitmapWithReqSize: in sample bitmap size ${bitmap.byteCount / 1024} KB")
            bitmap?.let {
                //压缩
                return createScaledBitmap(bitmap, reqW, reqH)
            }
        }
        return null
    }



    private fun createScaledBitmap(src: Bitmap, reqW: Int, reqH: Int): Bitmap?{
        val dst = Bitmap.createScaledBitmap(src, reqW, reqH, false)
        if (src != dst){
            src.recycle()
        }
        Log.d(TAG, "createScaledBitmap: dst scaled bitmap size ${dst.byteCount / 1024} KB")
        return dst
    }

    fun loadSnap(deviceId: String, channelId: String): ByteArray?{
        val httpUrl = HttpConnectionClient.buildPublicHeader(config)
            .addPathSegments(ServerUrl.API_DEVICE_QUERY)
            .addPathSegment(ServerUrl.SNAP)
            .addPathSegment(deviceId)
            .addPathSegment(channelId)
            .build()
        val request = Request.Builder()
            .url(httpUrl)
            .get()
            .build()

        Log.d(TAG, "loadSnap: url = ${httpUrl.toUrl()}")
        HttpConnectionClient.request(request).run {
            this.body?.let {
               return it.bytes()
            }
        }
        return null
    }


    /**
     * 方法一:
     * 计算采样率
     */
    private fun getInSampleSize1(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int{
        // 源图片的高度和宽度
        val w = options.outWidth
        val h = options.outHeight

        var inSampleSize = 1
        if (h > reqHeight || w > reqHeight) {
            // 计算出实际宽高和目标宽高的比率
            val hRatio = (h.toFloat() / reqHeight.toFloat()).roundToInt()
            val wRatio = (w.toFloat() / reqWidth.toFloat()).roundToInt()
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = if (hRatio < wRatio) hRatio else wRatio
        }
        return inSampleSize
    }

    /**
     *
     * 方法二:
     * 计算采样率
     * inSampleSize只能是2的整数次幂，如果不是的话，向下取得最大的2的整数次幂。因为按照2的次方进行压缩会比较高效和方便。
     * 方法一计算出来的inSampleSize值可能不是2的整数次幂，如计算出来的值是inSampleSize＝7，这时会被decode函数向下取为 inSampleSize＝4。
     * 所以我们一般采用方法二进行计算。此时，将设置了inSampleSize的options传给BitmapFactory.decode函数去获取图片，
     * 得到的会是一张比ImageView稍大的图片，不过这个图片要比原图小了
     */
    fun getInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int{
        // 源图片的高度和宽度
        val h = options.outHeight
        val w = options.outWidth
        var inSampleSize = 1
        if (h > reqHeight || w > reqWidth){
            // 计算出实际宽高和目标宽高的比率
            val hH = h / 2
            val hW = w / 2
            while ((hH / inSampleSize) > reqHeight && (hW / inSampleSize) > reqWidth){
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}