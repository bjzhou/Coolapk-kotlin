package bjzhou.coolapk.app.util;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class StringHelper {
    public static String getBase64(String paramString) {
        return getBase64(paramString, false);
    }

    public static String getBase64(String paramString, Boolean paramBoolean) {
        if (paramBoolean) {
            return new String(Base64.decode(paramString.replace('-', '+').replace('_', '/').replace('~', '='), Base64.NO_WRAP));
        } else {
            return new String(Base64.encode(paramString.getBytes(), Base64.NO_WRAP)).replace('+', '-').replace('/', '_').replace('=', '~');
        }
    }

    public static String getFileMD5(File file) throws IOException {
        if (!file.exists())
            throw new IOException("The file is not exist.");

        FileInputStream fis = null;
        DigestInputStream dis = null;
        byte[] buff = new byte[1024];
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            dis = new DigestInputStream(fis, md);

            // Read bytes from the file.
            while (dis.read(buff) != -1) ;

            byte[] md5Digests = md.digest();
            return byteArray2Hex(md5Digests);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            buff = null;
            if (fis != null) fis.close();
            if (dis != null) dis.close();
        }
        return null;
    }

    public static String byteArray2Hex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public static String getMD5(String paramString) {
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            char[] arrayOfChar = paramString.toCharArray();
            byte[] arrayOfByte1 = new byte[arrayOfChar.length];
            for (int i = 0; i < arrayOfChar.length; i++)
                arrayOfByte1[i] = (byte) arrayOfChar[i];
            byte[] arrayOfByte2 = localMessageDigest.digest(arrayOfByte1);
            StringBuffer localStringBuffer = new StringBuffer();
            for (int j = 0; j < arrayOfByte2.length; j++) {
                int k = 0xFF & arrayOfByte2[j];
                if (k < 16)
                    localStringBuffer.append("0");
                localStringBuffer.append(Integer.toHexString(k));
            }
            return localStringBuffer.toString();
        } catch (Exception localException) {
        }
        return null;
    }

    public static String getN27(int i) {
        String str = "";
        if (i < 27)
            str = "0abcdefghijklmnopqrstuvwxyz".substring(i, i + 1);
        else {
            int j = i % 27;
            str = getN27(i / 27) + "0abcdefghijklmnopqrstuvwxyz".substring(j, j + 1);
        }
        return str;
    }

    public static String getVStr(String paramString1, String paramString2, String paramString3, int paramInt) {
        return getBase64(paramString1 + paramInt + getMD5(getBase64(new StringBuilder().append(paramString3).append(paramString2).toString())).substring(8, paramInt + 8) + paramString2).replace("~", "");
    }
}