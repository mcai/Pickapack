package net.pickapack.automation;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;
import net.pickapack.Reference;
import net.pickapack.action.UntypedPredicate;
import net.pickapack.io.cmd.CommandLineHelper;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Win32Helper {
    private static final String REGQUERY_UTIL = "reg query ";
    private static final String REGSTR_TOKEN = "REG_SZ";
    private static final String DESKTOP_FOLDER_CMD = REGQUERY_UTIL +
            "\"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\"
            + "Explorer\\Shell Folders\" /v DESKTOP";

    public static List<String> getRunningProcesses() {
        List<String> processes = new ArrayList<String>();
        try {
            String line;
            Process p = Runtime.getRuntime().exec("tasklist.exe /fo csv /nh");
            BufferedReader input = new BufferedReader
                    (new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (!line.trim().equals("")) {
                    // keep only the process name
                    line = line.substring(1);
                    processes.add(line.substring(0, line.indexOf("\"")));
                }

            }
            input.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
        return processes;
    }

    public static String getCurrentUserDesktopPath() {
        String result = StringUtils.join(CommandLineHelper.invokeNativeCommandAndGetResult(DESKTOP_FOLDER_CMD), "\r\n");

        int p = result.indexOf(REGSTR_TOKEN);

        if (p == -1) {
            return null;
        }

        return result.substring(p + REGSTR_TOKEN.length()).trim();
    }

    public interface Win32Library extends StdCallLibrary {
        Map<String, Object> UNICODE_OPTIONS = new HashMap<String, Object>() {
            {
                put(OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
                put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
            }
        };

        Map<String, Object> ASCII_OPTIONS = new HashMap<String, Object>() {
            {
                put(OPTION_TYPE_MAPPER, W32APITypeMapper.ASCII);
                put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.ASCII);
            }
        };

        Map DEFAULT_OPTIONS = Boolean.getBoolean("w32.ascii") ? ASCII_OPTIONS : UNICODE_OPTIONS;
    }

    public static interface User32 extends Win32Library {
        interface WNDENUMPROC extends StdCallCallback {
            boolean callback(int hWnd, Pointer arg);
        }

        int FindWindow(String lpClassName, String lpWindowName);

        int FindWindowEx(int hwndParent, int hwndChildAfter, String lpszClass, String lpszWindow);

        boolean SetForegroundWindow(int hwnd);

        int GetForegroundWindow();

        boolean EnumWindows(WNDENUMPROC lpEnumFunc, Pointer arg);

        boolean EnumChildWindows(int hwnd, WNDENUMPROC lpEnumFunc, Pointer arg);

        int RegisterWindowMessage(String lpstring);

        int RealGetWindowClass(int hwnd, byte[] pszType, int cchType);

        int GetWindowText(int hwnd, byte[] lpString, int nMaxCount);

        boolean IsWindowVisible(int hwnd);

        void SetFocus(int hwnd);

        public static User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class, DEFAULT_OPTIONS);
    }

    public static class WindowInfo {
        private int hwnd;
        private String clz;
        private String name;
        private boolean visible;

        public WindowInfo(int hwnd, String clz, String name, boolean visible) {
            this.hwnd = hwnd;
            this.clz = clz;
            this.name = name;
            this.visible = visible;
        }

        public int getHwnd() {
            return hwnd;
        }

        public String getClz() {
            return clz;
        }

        public String getName() {
            return name;
        }

        public boolean isVisible() {
            return visible;
        }

        @Override
        public String toString() {
            return String.format("WindowInfo{hwnd=%d, clz='%s', name='%s', visible=%s}", hwnd, clz, name, visible);
        }
    }

    public static String getWindowClass(int hwnd) {
        try {
            byte[] clz = new byte[512];
            User32.INSTANCE.RealGetWindowClass(hwnd, clz, clz.length);
            return new String(clz, "UTF-16LE").trim();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getWindowText(int hwnd) {
        try {
            byte[] text = new byte[512];
            User32.INSTANCE.GetWindowText(hwnd, text, text.length);
            return new String(text, "UTF-16LE").trim();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<WindowInfo> getTopWindows() {
        final List<WindowInfo> topWindows = new ArrayList<WindowInfo>();

        Win32Helper.User32.INSTANCE.EnumWindows(new Win32Helper.User32.WNDENUMPROC() {
            public boolean callback(int hwnd, Pointer arg1) {
                topWindows.add(new WindowInfo(hwnd, getWindowClass(hwnd), getWindowText(hwnd), User32.INSTANCE.IsWindowVisible(hwnd)));
                return true;
            }
        }, null);

        return topWindows;
    }

    public static List<WindowInfo> getSubWindows(final int parentHwnd) {
        final List<WindowInfo> subWindows = new ArrayList<WindowInfo>();

        Win32Helper.User32.INSTANCE.EnumChildWindows(parentHwnd, new Win32Helper.User32.WNDENUMPROC() {
            public boolean callback(int hwnd, Pointer arg1) {
                subWindows.add(new WindowInfo(hwnd, getWindowClass(hwnd), getWindowText(hwnd), User32.INSTANCE.IsWindowVisible(hwnd)));
                return true;
            }
        }, null);

        return subWindows;
    }

    public static WindowInfo getSubWindow(int parentHwnd, String clz, String text) {
        List<Win32Helper.WindowInfo> subWindows = Win32Helper.getSubWindows(parentHwnd);

        for (Win32Helper.WindowInfo subWindow : subWindows) {
            if (subWindow.getClz().equals(clz) && subWindow.getName().equals(text)) {
                return subWindow;
            }
        }

        return null;
    }

    public static int waitForHandle(final String lpClassName, final String lpWindowName) throws InterruptedException {
        final Reference<Integer> handle = new Reference<Integer>(0);

        waitTill(new UntypedPredicate() {
            @Override
            public boolean apply() {
                handle.set(Win32Helper.User32.INSTANCE.FindWindow(lpClassName, lpWindowName));
                return handle.get() != 0;
            }
        }, 60, 1000);

        return handle.get();
    }

    public static boolean waitForHandleInForeground(final int handle) throws InterruptedException {
        return waitTill(new UntypedPredicate() {
            @Override
            public boolean apply() {
                return handle == Win32Helper.User32.INSTANCE.GetForegroundWindow();
            }
        }, 60, 1000);
    }

    public static boolean waitForWindowExW(final int handleiTunes, final String lpszClass, final String lpszWindow) throws InterruptedException {
        return waitTill(new UntypedPredicate() {
            @Override
            public boolean apply() {
                try {
                    return Win32Helper.User32.INSTANCE.FindWindowEx(handleiTunes, 0, lpszClass, new String(lpszWindow.getBytes("UTF-16LE"), "UTF-16LE")) != 0;
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 20, 500);
    }

    public static int getHandleOfWindowExW(int handleiTunes, String lpszClass, String lpszWindow) {
        try {
            return Win32Helper.User32.INSTANCE.FindWindowEx(handleiTunes, 0, lpszClass, new String(lpszWindow.getBytes("UTF-16LE"), "UTF-16LE"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean waitTill(UntypedPredicate pred, int numTries, int periodInSeconds) {
        for (int i = 0; i < numTries; i++) {
            try {
                Thread.sleep(periodInSeconds);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (pred.apply()) {
                return true;
            }
        }

        return false;
    }

    public static void killProcess(String serviceName) throws IOException, InterruptedException {
        Runtime.getRuntime().exec("taskkill /IM " + serviceName + " /F /T").waitFor();

        for(int i = 0; i < 50; i++) {
            if(!Win32Helper.getRunningProcesses().contains(serviceName)) {
                return;
            }

            Thread.sleep(100);
        }

        killProcess(serviceName);
    }
}
