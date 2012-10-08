/*******************************************************************************
 * Copyright (c) 2010-2012 by Min Cai (min.cai.china@gmail.com).
 *
 * This file is part of the PickaPack library.
 *
 * PickaPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PickaPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PickaPack. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.pickapack.io.cmd;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Min Cai
 */
public class CommandLineHelper {
    /**
     *
     * @param cmd
     * @param waitFor
     * @return
     */
    public static int invokeNativeCommand(String[] cmd, boolean waitFor) {
        try {
            Runtime r = Runtime.getRuntime();
            Process ps = r.exec(cmd);
//            ProcessBuilder pb = new ProcessBuilder(cmd);
//            Process ps = pb.start();

            if(waitFor) {
                int exitValue = ps.waitFor();
                if (exitValue != 0) {
                    System.out.println("WARN: Process exits with non-zero code: " + exitValue);
                }

                ps.destroy();

                return exitValue;
            }

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     *
     * @param args
     * @return
     */
    public static int invokeNativeCommand(String args) {
        return invokeNativeCommand(args, true);
    }

    /**
     *
     * @param args
     * @return
     */
    public static int invokeShellCommand(String args) {
        return invokeShellCommand(args, true);
    }

    /**
     *
     * @param args
     * @param waitFor
     * @return
     */
    public static int invokeNativeCommand(String args, boolean waitFor) {
        return invokeNativeCommand(args.split(" "), waitFor);
    }

    /**
     *
     * @param args
     * @param waitFor
     * @return
     */
    public static int invokeShellCommand(String args, boolean waitFor) {
        return invokeNativeCommand(new String[]{"sh", "-c", args}, waitFor);
    }

    /**
     *
     * @param cmd
     * @return
     */
    public static List<String> invokeNativeCommandAndGetResult(String[] cmd) {
        List<String> outputList = new ArrayList<String>();

        try {
            Runtime r = Runtime.getRuntime();
            Process ps = r.exec(cmd);
//            ProcessBuilder pb = new ProcessBuilder(cmd);
//            pb.redirectErrorStream(true);
//            Process ps = pb.start();

            BufferedReader rdr = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            String in = rdr.readLine();
            while (in != null) {
                outputList.add(in);
                in = rdr.readLine();
            }

            int exitValue = ps.waitFor();
            if (exitValue != 0) {
                System.out.println("WARN: Process exits with non-zero code: " + exitValue);
            }

            ps.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputList;
    }

    /**
     *
     * @param args
     * @return
     */
    public static List<String> invokeNativeCommandAndGetResult(String args) {
        return invokeNativeCommandAndGetResult(args.split(" "));
    }

    /**
     *
     * @param args
     * @return
     */
    public static List<String> invokeShellCommandAndGetResult(String args) {
        return invokeNativeCommandAndGetResult(new String[]{"sh", "-c", args});
    }
}
