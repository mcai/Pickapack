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
package net.pickapack.net.proxy;

import java.util.Properties;

/**
 * Proxy helper.
 *
 * @author Min Cai
 */
public class ProxyHelper {
    /**
     * Proxy IP address.
     */
    public static final String PROXY_IP = "localhost";

    /**
     * Proxy port.
     */
    public static final int PROXY_PORT = 8888;

    /**
     * Setup proxy.
     *
     * @param useLocalProxy a value indicating whether the local proxy is used or not
     */
    public static void setupProxy(boolean useLocalProxy) {
        if (useLocalProxy) {
            Properties props = System.getProperties();
            props.put("http.proxyHost", PROXY_IP);
            props.put("http.proxyPort", PROXY_PORT);
            props.put("https.proxyHost", PROXY_IP);
            props.put("https.proxyPort", PROXY_PORT);
        }
    }
}
