package org.openhab.binding.zigbee.tuya.handler;

import org.openhab.binding.zigbee.converter.ZigBeeChannelConverterFactory;
import org.openhab.binding.zigbee.handler.ZigBeeBaseThingHandler;
import org.openhab.binding.zigbee.handler.ZigBeeIsAliveTracker;
import org.openhab.core.thing.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.ZigBeeNode;

/**
 * Generic handler for Tuya devices
 *
 * @author Chris Jackson - Initial Contribution
 *
 */
public class TuyaThingHandler extends ZigBeeBaseThingHandler {
    private Logger logger = LoggerFactory.getLogger(TuyaThingHandler.class);

    public TuyaThingHandler(Thing zigbeeDevice, ZigBeeChannelConverterFactory channelFactory,
            ZigBeeIsAliveTracker zigbeeIsAliveTracker) {
        super(zigbeeDevice, channelFactory, zigbeeIsAliveTracker);
    }

    @Override
    protected void doNodeInitialisation(ZigBeeNode node) {
        ZigBeeEndpoint endpoint = node.getEndpoint(1);
        if (endpoint == null) {
            logger.error("{}: Tuya handler couldn't find endpoint 1", node.getIeeeAddress());
            return;
        }
    }

}
