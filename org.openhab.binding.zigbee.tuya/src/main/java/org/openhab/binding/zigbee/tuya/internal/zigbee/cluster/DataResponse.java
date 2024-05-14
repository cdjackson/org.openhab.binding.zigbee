/**
 * Copyright (c) 2016-2024 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.zigbee.tuya.internal.zigbee.cluster;

import javax.annotation.Generated;

import com.zsmartsystems.zigbee.zcl.ZclFieldDeserializer;
import com.zsmartsystems.zigbee.zcl.ZclFieldSerializer;
import com.zsmartsystems.zigbee.zcl.field.ByteArray;
import com.zsmartsystems.zigbee.zcl.protocol.ZclCommandDirection;
import com.zsmartsystems.zigbee.zcl.protocol.ZclDataType;

/**
 * Data Response value object class.
 * <p>
 * Cluster: <b>Tuya Specific</b>. Command ID 0x01 is sent <b>FROM</b> the server.
 * This command is a <b>specific</b> command used for the Tuya Specific cluster.
 * <p>
 * The MCU responds to a data request.
 * <p>
 * Code is auto-generated. Modifications may be overwritten!
 */
@Generated(value = "com.zsmartsystems.zigbee.autocode.ZigBeeCodeGenerator", date = "2024-05-08T10:17:37Z")
public class DataResponse extends ZclTuyaSpecificCommand {
    /**
     * The cluster ID to which this command belongs.
     */
    public static int CLUSTER_ID = 0xEF00;

    /**
     * The command ID.
     */
    public static int COMMAND_ID = 0x01;

    /**
     * DPID command message field.
     */
    private Integer dpid;

    /**
     * Type command message field.
     */
    private Integer type;

    /**
     * Value command message field.
     */
    private ByteArray value;

    /**
     * Constructor providing all required parameters.
     *
     * @param dpid {@link Integer} DPID
     * @param type {@link Integer} Type
     * @param unused {@link Integer} Unused
     * @param value {@link ByteArray} Value
     */
    public DataResponse(Integer dpid, Integer type, ByteArray value) {

        clusterId = CLUSTER_ID;
        commandId = COMMAND_ID;
        genericCommand = false;
        commandDirection = ZclCommandDirection.SERVER_TO_CLIENT;

        this.dpid = dpid;
        this.type = type;
        this.value = value;
    }

    /**
     * Gets DPID.
     *
     * @return the DPID
     */
    public Integer getDpid() {
        return dpid;
    }

    /**
     * Gets Type.
     *
     * @return the Type
     */
    public Integer getType() {
        return type;
    }

    /**
     * Gets Value.
     *
     * @return the Value
     */
    public ByteArray getValue() {
        return value;
    }

    @Override
    public void serialize(final ZclFieldSerializer serializer) {
        serializer.serialize(sequenceNumber, ZclDataType.UNSIGNED_16_BIT_INTEGER);
        serializer.serialize(dpid, ZclDataType.UNSIGNED_8_BIT_INTEGER);
        serializer.serialize(type, ZclDataType.UNSIGNED_8_BIT_INTEGER);
        serializer.serialize(0, ZclDataType.UNSIGNED_8_BIT_INTEGER);
        serializer.serialize(value, ZclDataType.BYTE_ARRAY);
    }

    @Override
    public void deserialize(final ZclFieldDeserializer deserializer) {
        sequenceNumber = deserializer.deserialize(ZclDataType.UNSIGNED_16_BIT_INTEGER);
        dpid = deserializer.deserialize(ZclDataType.UNSIGNED_8_BIT_INTEGER);
        type = deserializer.deserialize(ZclDataType.UNSIGNED_8_BIT_INTEGER);
        deserializer.deserialize(ZclDataType.UNSIGNED_8_BIT_INTEGER);
        value = deserializer.deserialize(ZclDataType.BYTE_ARRAY);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(148);
        builder.append("DataResponse [");
        builder.append(super.toString());
        builder.append(", sequenceNumber=");
        builder.append(sequenceNumber);
        builder.append(", dpid=");
        builder.append(dpid);
        builder.append(", type=");
        builder.append(type);
        builder.append(", value=");
        builder.append(value);
        builder.append(']');
        return builder.toString();
    }

}
