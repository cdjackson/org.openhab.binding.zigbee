/**
 * Copyright (c) 2016-2024 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.zigbee.tuya.internal.zigbee;

import javax.annotation.Generated;

import com.zsmartsystems.zigbee.zcl.ZclCommand;

/**
 * Abstract base command class for all commands in the <b>Tuya Specific</b> cluster (<i>Cluster ID 0xEF00</i>).
 * All commands sent through the {@link ZclTuyaSpecificCluster} must extend this class.
 * <p>
 * Code is auto-generated. Modifications may be overwritten!
 */
@Generated(value = "com.zsmartsystems.zigbee.autocode.ZigBeeCodeGenerator", date = "2024-05-07T10:04:44Z")
public abstract class ZclTuyaSpecificCommand extends ZclCommand {
    /**
     * Sequence Number command message field.
     */
    protected Integer sequenceNumber;

    /**
     * Gets Sequence Number.
     *
     * @return the Sequence Number
     */
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Sets Sequence Number.
     *
     * @param sequenceNumber the Sequence Number
     */
    public void setSequenceNumber(final Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

}
