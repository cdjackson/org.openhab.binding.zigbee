/**
 * Copyright (c) 2016-2024 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.zigbee.tuya.internal.zigbee;

import javax.annotation.Generated;

import com.zsmartsystems.zigbee.zcl.ZclFieldDeserializer;
import com.zsmartsystems.zigbee.zcl.ZclFieldSerializer;
import com.zsmartsystems.zigbee.zcl.protocol.ZclCommandDirection;
import com.zsmartsystems.zigbee.zcl.protocol.ZclDataType;

/**
 * MCU Version Request value object class.
 * <p>
 * Cluster: <b>Tuya Specific</b>. Command ID 0x10 is sent <b>FROM</b> the server.
 * This command is a <b>specific</b> command used for the Tuya Specific cluster.
 * <p>
 * The gateway sends a query to the ZigBee module for the MCU firmware version.
 * <p>
 * Code is auto-generated. Modifications may be overwritten!
 */
@Generated(value = "com.zsmartsystems.zigbee.autocode.ZigBeeCodeGenerator", date = "2024-05-08T09:59:49Z")
public class McuVersionRequest extends ZclTuyaSpecificCommand {
    /**
     * The cluster ID to which this command belongs.
     */
    public static int CLUSTER_ID = 0xEF00;

    /**
     * The command ID.
     */
    public static int COMMAND_ID = 0x10;

    /**
     * Default constructor.
     *
     */
    public McuVersionRequest() {
        clusterId = CLUSTER_ID;
        commandId = COMMAND_ID;
        genericCommand = false;
        commandDirection = ZclCommandDirection.SERVER_TO_CLIENT;
    }

    @Override
    public void serialize(final ZclFieldSerializer serializer) {
        serializer.serialize(sequenceNumber, ZclDataType.UNSIGNED_16_BIT_INTEGER);
    }

    @Override
    public void deserialize(final ZclFieldDeserializer deserializer) {
        sequenceNumber = deserializer.deserialize(ZclDataType.UNSIGNED_16_BIT_INTEGER);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(54);
        builder.append("McuVersionRequest [");
        builder.append(super.toString());
        builder.append(", sequenceNumber=");
        builder.append(sequenceNumber);
        builder.append(']');
        return builder.toString();
    }

}
