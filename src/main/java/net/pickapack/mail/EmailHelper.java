package net.pickapack.mail;

import java.util.Random;
import java.util.UUID;

public class EmailHelper {
    private static Random random = new Random();

    public static String generateEmailPrefix() {
        return (char) (97 + random.nextInt(122 - 97)) + UUID.randomUUID().toString().substring(0, 5 + random.nextInt(10)).replaceAll("-", "");
    }

    public static String getEmailDomain(String email) {
        int i = email.indexOf("@");
        return i == -1 ? null : email.substring(i + 1);
    }

    public static String getUserId(String email) {
        int i = email.indexOf("@");
        return i == -1 ? null : email.substring(0, i);
    }
}
