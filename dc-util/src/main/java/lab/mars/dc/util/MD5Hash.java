package lab.mars.dc.util;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Author:yaoalong.
 * Date:2016/4/26.
 * Email:yaoalong@foxmail.com
 */
public class MD5Hash {
    public static final ThreadLocal<MessageDigest> MD5 = new ThreadLocal<MessageDigest>() {
        @Override
        protected final MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("++++ no md5 algorythm found");
            }
        }
    };

    public static long md5HashingAlg(String key) {
        MessageDigest md5 = MD5.get();
        md5.reset();
        md5.update(key.getBytes(Charset.forName("utf-8")));
        byte[] bKey = md5.digest();
        long res = ((long) (bKey[3] & 0xFF) << 24)
                | ((long) (bKey[2] & 0xFF) << 16)
                | ((long) (bKey[1] & 0xFF) << 8) | (long) (bKey[0] & 0xFF);
        return res;
    }
}
