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
 * Data Request value object class.
 * <p>
 * Cluster: <b>Tuya Specific</b>. Command ID 0x00 is sent <b>TO</b> the server.
 * This command is a <b>specific</b> command used for the Tuya Specific cluster.
 * <p>
 * The gateway sends a data request to the ZigBee device.
 * <p>
 * Code is auto-generated. Modifications may be overwritten!
 */
@Generated(value = "com.zsmartsystems.zigbee.autocode.ZigBeeCodeGenerator", date = "2024-05-13T08:01:52Z")
public class DataRequest extends ZclTuyaSpecificCommand {
    /**
     * The cluster ID to which this command belongs.
     */
    public static int CLUSTER_ID = 0xEF00;

    /**
     * The command ID.
     */
    public static int COMMAND_ID = 0x00;

    /**
     * DPID command message field.
     */
    private Integer dpid;

    /**
     * Constructor providing all required parameters.
     *
     * @param dpid {@link Integer} DPID
     */
    public DataRequest(Integer dpid) {

        clusterId = CLUSTER_ID;
        commandId = COMMAND_ID;
        genericCommand = false;
        commandDirection = ZclCommandDirection.CLIENT_TO_SERVER;

        this.dpid = dpid;
    }

    /**
     * Gets DPID.
     *
     * @return the DPID
     */
    public Integer getDpid() {
        return dpid;
    }

    @Override
    public void serialize(final ZclFieldSerializer serializer) {
        serializer.serialize(sequenceNumber, ZclDataType.UNSIGNED_16_BIT_INTEGER);
        serializer.serialize(dpid, ZclDataType.UNSIGNED_8_BIT_INTEGER);
    }

    @Override
    public void deserialize(final ZclFieldDeserializer deserializer) {
        sequenceNumber = deserializer.deserialize(ZclDataType.UNSIGNED_16_BIT_INTEGER);
        dpid = deserializer.deserialize(ZclDataType.UNSIGNED_8_BIT_INTEGER);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(72);
        builder.append("DataRequest [");
        builder.append(super.toString());
        builder.append(", sequenceNumber=");
        builder.append(sequenceNumber);
        builder.append(", dpid=");
        builder.append(dpid);
        builder.append(']');
        return builder.toString();
    }

}
