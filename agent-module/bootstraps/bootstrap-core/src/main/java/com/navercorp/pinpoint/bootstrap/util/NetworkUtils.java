/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.bootstrap.util;

import com.navercorp.pinpoint.common.util.NetUtils;
import com.navercorp.pinpoint.common.util.OsType;
import com.navercorp.pinpoint.common.util.OsUtils;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * @author emeroad
 */
public final class NetworkUtils {

    public static final String ERROR_HOST_NAME = "UNKNOWN-HOST";

    public static final String LOOPBACK_ADDRESS_V4_1 = "127.0.0.1";
    public static final String LOOPBACK_ADDRESS_V4_2 = "127.0.1.1";
    public static final String LOOPBACK_ADDRESS_V6 = "0:0:0:0:0:0:0:1";

    private static volatile String LOCAL_HOST_CACHE;

    private static final String[] LOOP_BACK_ADDRESS_LIST = new String[]{
            LOOPBACK_ADDRESS_V4_1,
            LOOPBACK_ADDRESS_V4_2,
            LOOPBACK_ADDRESS_V6
    };

    private NetworkUtils() {
    }

    public static String getHostName() {
        final String hostName = getHostNameFromEnv();
        if (hostName != null) {
            return hostName;
        }

        if (LOCAL_HOST_CACHE != null) {
            return LOCAL_HOST_CACHE;
        }
        LOCAL_HOST_CACHE = getHostNameFromDns();
        return LOCAL_HOST_CACHE;
    }

    static String getHostNameFromDns() {
        try {
            final InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostName();
        } catch (UnknownHostException e) {
            return ERROR_HOST_NAME;
        }
    }

    static String getHostNameFromEnv() {
        final OsType type = OsUtils.getType();
        if (OsType.WINDOW == type) {
            return System.getenv("COMPUTERNAME");
        } else {
            return System.getenv("HOSTNAME");
        }
    }

    public static String getRepresentationHostIp(String hostIp) {
        if (!isLoopbackAddress(hostIp)) {
            return hostIp;
        }

        List<String> ipList = getHostIpList();
        if (!ipList.isEmpty()) {
            return ipList.get(0);
        }

        return LOOPBACK_ADDRESS_V4_1;
    }

    public static String getHostIp() throws UnknownHostException{
        final InetAddress thisIp = InetAddress.getLocalHost();
        return thisIp.getHostAddress();
    }

    public static List<String> getHostIpList() {

        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException ignored) {
            // skip
        }

        if (interfaces == null) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();
        while (interfaces.hasMoreElements()) {
            NetworkInterface current = interfaces.nextElement();
            if (isSkipNetworkInterface(current)) {
                continue;
            }

            Enumeration<InetAddress> addresses = current.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (address.isLoopbackAddress()) {
                    continue;
                }

                String hostAddress = address.getHostAddress();
                if (!isLoopbackAddress(hostAddress)) {
                    result.add(address.getHostAddress());
                }
            }
        }

        return result;
    }

    public static List<String> getHostV4IpList() {
        List<String> hostIpList = getHostIpList();
        List<String> hostV4IpList = new ArrayList<>(hostIpList.size());
        for (String ip : hostIpList) {
            if (validationIpV4FormatAddress(ip)) {
                hostV4IpList.add(ip);
            }
        }

        return hostV4IpList;
    }

    private static boolean isSkipNetworkInterface(NetworkInterface networkInterface) {
        try {
            if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                return true;
            }
            return false;
        } catch (Exception ignored) {
            // skip
        }
        return true;
    }

    public static boolean isLoopbackAddress(String ip) {
        if (ip == null) {
            return true;
        }
        for (String address : LOOP_BACK_ADDRESS_LIST) {
            if (address.equals(ip)) {
                return true;
            }
        }
        return false;
    }

    public static boolean validationIpV4FormatAddress(String address) {
        return NetUtils.validationIpV4FormatAddress(address);
    }


    public static String getHostFromURL(final String urlSpec) {
        if (urlSpec == null) {
            return null;
        }
        try {
            final URL url = new URL(urlSpec);

            final String host = url.getHost();
            final int port = url.getPort();

            if (port == -1) {
                return host;
            } else {
                // TODO should we still specify the port number if default port is used?
                return host + ":" + port;
            }
        } catch (MalformedURLException e) {
            return null;
        }
    }

}
