package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by Administrator on 2017-5-9.
 */
public class test {
    private static final Logger logger = LoggerFactory.getLogger(test.class);
    public static void main_(String[] args) {
        Random ranGen = new SecureRandom();
        byte[] aesKey = new byte[20];
        ranGen.nextBytes(aesKey);  //Generates a user-specified number of random bytes.
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < aesKey.length; i++) {
            String hex = Integer.toHexString(0xff & aesKey[i]);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        logger.info("hexString:{}",hexString );
    }
}
