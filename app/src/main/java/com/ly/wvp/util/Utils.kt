package com.ly.wvp.util

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Utils {
    /**
     * Java中的md5
     * @param content 输入的值
     * @return 输出md5加密后的值
     */
    fun md5Encoding(content: String): String {
        val hash: ByteArray = try {
            MessageDigest.getInstance("MD5").digest(content.toByteArray())
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("NoSuchAlgorithmException", e)
        }
        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            if (b.toInt() and 0xFF < 0x10) {
                hex.append(0)
            }
            hex.append(Integer.toHexString(b.toInt() and 0xff))
        }
        return hex.toString()
    }
}