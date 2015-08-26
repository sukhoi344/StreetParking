package chau.streetparking;

import java.math.BigInteger;

import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 7/27/15.
 */
public class FileManager {
    /**
     * File name of the raw (uncropped) avatar image
     */
    public static final String AVATAR_UNCROPPED_FILE_NAME = "avatar_uncropped.png";

    /**
     * File name of the processed (cropped to get the square shape) avatar image
     */
    public static final String AVATAR_CROPPED_FILE_NAME = "avatar_cropped.png";

    /**
     * Get unique avatar file name
     * @param mobile phone number of the user
     * @return unique file name with .png extension
     */
    public static String getFileAvatarName(String mobile) {
        try {
            return toHex(System.currentTimeMillis() + mobile) + ".png";
        } catch (Exception e) {
            Logger.printStackTrace(e);
        }

        return System.currentTimeMillis() + ".png";
    }

    public static String getPhotoFileName(String userId) {
        try {
            return toHex(userId + System.currentTimeMillis()) + ".png";
        } catch (Exception e) {
            Logger.printStackTrace(e);
        }

        return System.currentTimeMillis() + ".png";
    }

    private static String toHex(String arg) {
        return String.format("%x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }
}
