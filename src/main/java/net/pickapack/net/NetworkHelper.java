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
package net.pickapack.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Network helper.
 *
 * @author Min Cai
 */
public class NetworkHelper {
    /**
     * Default timeout in milliseconds used in the network reachability test.
     */
    public static final int DEFAULT_TIMEOUT = 400;

    /**
     * Get a value indicating whether the specified host is reachable using the specified timeout.
     *
     * @param host the host
     * @param timeout the timeout in milliseconds used in the network reachability test
     * @return a value indicating whether the specified host is reachable using the specified timeout
     */
    public static boolean isHostReachable(String host, int timeout) {
        try {
            return InetAddress.getByName(host).isReachable(timeout);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Get a value indicating whether the specified host is reachable using the default timeout.
     *
     * @param host the host
     * @return a value indicating whether the specified host is reachable using the default timeout
     */
    public static boolean isHostReachable(String host) {
        return isHostReachable(host, DEFAULT_TIMEOUT);
    }

    /**
     * Get a value indicating whether the specified port is reachable using the specified timeout.
     *
     * @param host the host
     * @param port the port
     * @param timeout the timeout in milliseconds used in the network reachability test
     * @return a value indicating whether the specified port is reachable using the specified timeout
     */
    public static boolean isPortReachable(String host, int port, int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), timeout);
            socket.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * Get a value indicating whether the specified port is reachable using the default timeout.
     *
     * @param host the host
     * @param port the port
     * @return a value indicating whether the specified port is reachable using the default timeout
     */
    public static boolean isPortReachable(String host, int port) {
        return isPortReachable(host, port, DEFAULT_TIMEOUT);
    }
}
