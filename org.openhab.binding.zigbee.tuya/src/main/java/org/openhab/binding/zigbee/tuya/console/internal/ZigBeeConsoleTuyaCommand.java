/**
 * Copyright (c) 2016-2024 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.zigbee.tuya.console.internal;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.openhab.binding.zigbee.tuya.internal.zigbee.cluster.DataQuery;
import org.openhab.binding.zigbee.tuya.internal.zigbee.cluster.DataResponse;
import org.openhab.binding.zigbee.tuya.internal.zigbee.cluster.McuSyncTime;
import org.openhab.binding.zigbee.tuya.internal.zigbee.cluster.McuVersionRequest;
import org.openhab.binding.zigbee.tuya.internal.zigbee.cluster.ZclTuyaSpecificCluster;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.ZigBeeNetworkManager;
import com.zsmartsystems.zigbee.console.ZigBeeConsoleAbstractCommand;

/**
 *
 * @author Chris Jackson
 *
 */
public class ZigBeeConsoleTuyaCommand extends ZigBeeConsoleAbstractCommand {

    @Override
    public String getCommand() {
        return "tuya";
    }

    @Override
    public String getDescription() {
        return "Interface with the Tuya specific cluster data points.";
    }

    @Override
    public String getSyntax() {
        return "ENDPOINT [VERSION | DPID=dpid]";
    }

    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public void process(ZigBeeNetworkManager networkManager, String[] args, PrintStream out)
            throws IllegalArgumentException, InterruptedException, ExecutionException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Invalid number of arguments");
        }

        final ZigBeeEndpoint endpoint = getEndpoint(networkManager, args[1]);
        final ZclTuyaSpecificCluster cluster = (ZclTuyaSpecificCluster) endpoint
                .getInputCluster(ZclTuyaSpecificCluster.CLUSTER_ID);

        boolean sync = false;
        boolean query = false;
        boolean version = false;
        for (int i = 2; i < args.length; i++) {
            switch (args[i].toLowerCase()) {
                case "sync":
                    sync = true;
                    continue;
                case "query":
                    query = true;
                    continue;
                case "version":
                    version = true;
                    continue;
            }
        }

        if (sync) {
            McuSyncTime request = new McuSyncTime((int) (System.currentTimeMillis() / 1000),
                    (int) (System.currentTimeMillis() / 1000));
            cluster.sendCommand(request);
        }
        if (query) {
            DataQuery request = new DataQuery();
            cluster.sendCommand(request);
        }
        if (version) {
            McuVersionRequest request = new McuVersionRequest();
            cluster.sendCommand(request);
        }

    }

    String formatDpid(DataResponse response) {
        byte[] payload = response.getValue().get();
        switch (response.getType()) {
            case 0: // Raw
                return "RAW - add decoder";
            case 1: // Boolean
                if (response.getValue().get()[0] == 0) {
                    return "false";
                } else {
                    return "true";
                }
            case 2: // Integer
                Integer.toString(payload[0] + (payload[1] << 8) + (payload[2] << 16) + (payload[3] << 24));
            case 3: // String
                try {
                    return new String(
                            Arrays.copyOfRange(response.getValue().get(), 0, response.getValue().get().length),
                            "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    return "ERROR";
                }
            case 4: // Enum
                return Integer.toString(response.getValue().get()[0]);
            case 5: // Bitmap
                return "BITMAP - add decoder";
        }

        return "";
    }
}
