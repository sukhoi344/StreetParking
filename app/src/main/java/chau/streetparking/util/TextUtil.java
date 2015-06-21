package chau.streetparking.util;

/**
 * Created by Chau Thai on 6/20/2015.
 */
public class TextUtil {

    public static String getCodedNumber(String number) {
        if (number != null && number.length() >= 14) {
            String last4 = number.substring(number.length() - 4);
            return "•••• " + last4;
        }

        return number;
    }
}
