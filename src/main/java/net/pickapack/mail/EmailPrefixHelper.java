package net.pickapack.mail;

import java.util.Random;
import java.util.UUID;

public class EmailPrefixHelper {
    private static Random random = new Random();

    public static String generateEmailPrefix() {
        return (char) (97 + random.nextInt(122 - 97)) + UUID.randomUUID().toString().substring(0, 5 + random.nextInt(10)).replaceAll("-", "");
    }
    
    public static void main(String[] args) {
        for(int i = 0; i < 1000; i++) {
            System.out.println(generateEmailPrefix() + "@sohu.com");
        }
    }
}
