package bjzhou.coolapk.app.util

import android.util.Base64
import java.security.MessageDigest
import java.util.*

object StringHelper {

    @JvmOverloads fun getBase64(paramString: String, paramBoolean: Boolean? = false): String {
        if (paramBoolean!!) {
            return String(Base64.decode(paramString.replace('-', '+').replace('_', '/').replace('~', '='), Base64.NO_WRAP))
        } else {
            return String(Base64.encode(paramString.toByteArray(), Base64.NO_WRAP)).replace('+', '-').replace('/', '_').replace('=', '~')
        }
    }

    fun byteArray2Hex(hash: ByteArray): String {
        val formatter = Formatter()
        for (b in hash) {
            formatter.format("%02x", b)
        }
        return formatter.toString()
    }

    fun getMD5(paramString: String): String? {
        try {
            val localMessageDigest = MessageDigest.getInstance("MD5")
            val arrayOfChar = paramString.toCharArray()
            val arrayOfByte1 = ByteArray(arrayOfChar.size)
            for (i in arrayOfChar.indices)
                arrayOfByte1[i] = arrayOfChar[i].toByte()
            val arrayOfByte2 = localMessageDigest.digest(arrayOfByte1)
            val localStringBuffer = StringBuffer()
            for (j in arrayOfByte2.indices) {
                val k = 0xFF and arrayOfByte2[j].toInt()
                if (k < 16)
                    localStringBuffer.append("0")
                localStringBuffer.append(Integer.toHexString(k))
            }
            return localStringBuffer.toString()
        } catch (localException: Exception) {
        }

        return null
    }

    fun getN27(i: Int): String {
        var str = ""
        if (i < 27)
            str = "0abcdefghijklmnopqrstuvwxyz".substring(i, i + 1)
        else {
            val j = i % 27
            str = getN27(i / 27) + "0abcdefghijklmnopqrstuvwxyz".substring(j, j + 1)
        }
        return str
    }

    fun getVStr(paramString1: String, paramString2: String, paramString3: String, paramInt: Int): String {
        return getBase64(paramString1 + paramInt + getMD5(getBase64(StringBuilder().append(paramString3).append(paramString2).toString()))!!.substring(8, paramInt + 8) + paramString2).replace("~", "")
    }
}