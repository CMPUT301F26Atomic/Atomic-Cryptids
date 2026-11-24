package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

/**
 * generates campsite qr codes
 */
object QrCodeUtil {

    private const val PREFIX = "campsiteapp://campsite/"

    /** build link that will be encoded in the qr*/
    fun encodePayload(campsiteId: String): String = "$PREFIX$campsiteId"

    /**
     * write qr code to bitmap
     */
    fun generateBitmap(content: String, size: Int = 512): Bitmap? = try {
        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
            EncodeHintType.MARGIN to 1,
        )
        val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).apply {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}