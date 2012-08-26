package net.pickapack.automation;

import net.pickapack.io.cmd.CommandLineHelper;
import net.pickapack.io.file.IterableBigTextFile;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class QqHelper {
    private static Keyboard keyboard;

    static {
        try {
            keyboard = new Keyboard();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public static void Login(String userId, String password) throws IOException, InterruptedException, AWTException {
        logout();

        CommandLineHelper.invokeNativeCommand("C:\\Program Files\\Tencent\\QQ\\Bin\\QQ.exe", false);

        Win32Helper.waitForHandle("TXGuiFoundation", "QQ2012");

        keyboard.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_TAB);

        keyboard.type(userId);
        keyboard.type("\t");
        keyboard.type(password);

        keyboard.type("\n");

        Thread.sleep(10000); //TODO: to be replaced with screen recognition
    }

    public static void sendMessage(String to, String message) throws InterruptedException {
        for(int i = 0; i < 7; i++) {
            keyboard.type("\t");
        }

        Thread.sleep(5000);

        keyboard.type(to);

        Thread.sleep(5000);

        keyboard.type("\n");

        Thread.sleep(5000);

        keyboard.paste(message);
        keyboard.type("\n");

        Thread.sleep(5000);
    }

    public static void logout() throws IOException, InterruptedException {
        Win32Helper.killProcess("QQ.exe");
    }

    public static void main(String[] args) throws InterruptedException, AWTException, IOException {
        IterableBigTextFile messages = new IterableBigTextFile(new InputStreamReader(QqHelper.class.getResourceAsStream("/by.txt"), Charset.forName("utf-8")));

        for(String message : messages) {
            System.out.println(message);

            Login("2437106554", "1026@ustc");
            sendMessage("1041301030", message);
            logout();
        }
    }
}
