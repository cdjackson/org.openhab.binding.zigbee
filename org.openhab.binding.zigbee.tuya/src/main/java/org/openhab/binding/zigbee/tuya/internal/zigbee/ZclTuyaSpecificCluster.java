/**
 * Copyright (c) 2016-2024 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.zigbee.tuya.internal.zigbee;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Future;

import javax.annotation.Generated;

import com.zsmartsystems.zigbee.CommandResult;
import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclCluster;
import com.zsmartsystems.zigbee.zcl.ZclCommand;

/**
 * <b>Tuya Specific</b> cluster implementation (<i>Cluster ID 0xEF00</i>).
 * <p>
 * Code is auto-generated. Modifications may be overwritten!
 */
@Generated(value = "com.zsmartsystems.zigbee.autocode.ZigBeeCodeGenerator", date = "2024-05-13T09:00:09Z")
public class ZclTuyaSpecificCluster extends ZclCluster {
    /**
     * The ZigBee Cluster Library Cluster ID
     */
    public static final int CLUSTER_ID = 0xEF00;

    /**
     * The ZigBee Cluster Library Cluster Name
     */
    public static final String CLUSTER_NAME = "Tuya Specific";

    private Integer txSequence = 0;

    @Override
    protected Map<Integer, ZclAttribute> initializeClientAttributes() {
        Map<Integer, ZclAttribute> attributeMap = super.initializeClientAttributes();

        return attributeMap;
    }

    @Override
    protected Map<Integer, ZclAttribute> initializeServerAttributes() {
        Map<Integer, ZclAttribute> attributeMap = super.initializeServerAttributes();

        return attributeMap;
    }

    @Override
    protected Map<Integer, Class<? extends ZclCommand>> initializeServerCommands() {
        Map<Integer, Class<? extends ZclCommand>> commandMap = new ConcurrentSkipListMap<>();

        commandMap.put(0x0001, DataResponse.class);
        commandMap.put(0x0002, DataReport.class);
        commandMap.put(0x0003, DataQuery.class);
        commandMap.put(0x0010, McuVersionRequest.class);
        commandMap.put(0x0011, McuVersionResponse.class);
        commandMap.put(0x0024, McuSyncTime.class);

        return commandMap;
    }

    @Override
    protected Map<Integer, Class<? extends ZclCommand>> initializeClientCommands() {
        Map<Integer, Class<? extends ZclCommand>> commandMap = new ConcurrentSkipListMap<>();

        commandMap.put(0x0000, DataRequest.class);

        return commandMap;
    }

    /**
     * Default constructor to create a Tuya Specific cluster.
     *
     * @param zigbeeEndpoint the {@link ZigBeeEndpoint} this cluster is contained within
     */
    public ZclTuyaSpecificCluster(final ZigBeeEndpoint zigbeeEndpoint) {
        super(zigbeeEndpoint, CLUSTER_ID, CLUSTER_NAME);
    }

    /**
     * Sends a {@link ZclTuyaSpecificCommand} and returns the {@link Future} to the result which will complete when the
     * remote
     * device response is received, or the request times out.
     *
     * @param command the {@link ZclTuyaSpecificCommand} to send
     * @return the command result future
     */
    public Future<CommandResult> sendCommand(ZclTuyaSpecificCommand command) {
        command.setSequenceNumber(txSequence++);
        return super.sendCommand(command);
    }

    /**
     * Sends a response to the command. This method sets all the common elements of the response based on the command -
     * eg transactionId, direction, address...
     *
     * @param command the {@link ZclTuyaSpecificCommand} to which the response is being sent
     * @param response the {@link ZclTuyaSpecificCommand} to send
     */
    public Future<CommandResult> sendResponse(ZclTuyaSpecificCommand command, ZclTuyaSpecificCommand response) {
        command.setSequenceNumber(txSequence++);
        return super.sendResponse(command, response);
    }
}
