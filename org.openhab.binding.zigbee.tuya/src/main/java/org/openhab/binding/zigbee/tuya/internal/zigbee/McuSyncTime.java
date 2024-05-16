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
 * MCU Sync Time value object class.
 * <p>
 * Cluster: <b>Tuya Specific</b>. Command ID 0x24 is sent <b>FROM</b> the server.
 * This command is a <b>specific</b> command used for the Tuya Specific cluster.
 * <p>
 * Time synchronization (two-way)
 * <p>
 * Code is auto-generated. Modifications may be overwritten!
 */
@Generated(value = "com.zsmartsystems.zigbee.autocode.ZigBeeCodeGenerator", date = "2024-05-08T10:17:37Z")
public class McuSyncTime extends ZclTuyaSpecificCommand {
    /**
     * The cluster ID to which this command belongs.
     */
    public static int CLUSTER_ID = 0xEF00;

    /**
     * The command ID.
     */
    public static int COMMAND_ID = 0x24;

    /**
     * Standard Timestamp command message field.
     */
    private Integer standardTimestamp;

    /**
     * Local Timestamp command message field.
     */
    private Integer localTimestamp;

    /**
     * Constructor providing all required parameters.
     *
     * @param sequenceNumber {@link Integer} Sequence Number
     * @param standardTimestamp {@link Integer} Standard Timestamp
     * @param localTimestamp {@link Integer} Local Timestamp
     */
    public McuSyncTime(Integer standardTimestamp, Integer localTimestamp) {

        clusterId = CLUSTER_ID;
        commandId = COMMAND_ID;
        genericCommand = false;
        commandDirection = ZclCommandDirection.SERVER_TO_CLIENT;

        this.standardTimestamp = standardTimestamp;
        this.localTimestamp = localTimestamp;
    }

    /**
     * Gets Standard Timestamp.
     *
     * @return the Standard Timestamp
     */
    public Integer getStandardTimestamp() {
        return standardTimestamp;
    }

    /**
     * Gets Local Timestamp.
     *
     * @return the Local Timestamp
     */
    public Integer getLocalTimestamp() {
        return localTimestamp;
    }

    @Override
    public void serialize(final ZclFieldSerializer serializer) {
        serializer.serialize(sequenceNumber, ZclDataType.UNSIGNED_16_BIT_INTEGER);
        serializer.serialize(standardTimestamp, ZclDataType.UNSIGNED_32_BIT_INTEGER);
        serializer.serialize(localTimestamp, ZclDataType.UNSIGNED_32_BIT_INTEGER);
    }

    @Override
    public void deserialize(final ZclFieldDeserializer deserializer) {
        sequenceNumber = deserializer.deserialize(ZclDataType.UNSIGNED_16_BIT_INTEGER);
        standardTimestamp = deserializer.deserialize(ZclDataType.UNSIGNED_32_BIT_INTEGER);
        localTimestamp = deserializer.deserialize(ZclDataType.UNSIGNED_32_BIT_INTEGER);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(119);
        builder.append("McuSyncTime [");
        builder.append(super.toString());
        builder.append(", sequenceNumber=");
        builder.append(sequenceNumber);
        builder.append(", standardTimestamp=");
        builder.append(standardTimestamp);
        builder.append(", localTimestamp=");
        builder.append(localTimestamp);
        builder.append(']');
        return builder.toString();
    }

}
