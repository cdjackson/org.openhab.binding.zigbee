/**
 * Copyright (c) 2014-2017 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.zigbee.converter;

import java.util.concurrent.ExecutionException;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.HSBType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.zigbee.ZigBeeBindingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zsmartsystems.zigbee.ZigBeeEndpoint;
import com.zsmartsystems.zigbee.zcl.ZclAttribute;
import com.zsmartsystems.zigbee.zcl.ZclAttributeListener;
import com.zsmartsystems.zigbee.zcl.clusters.ZclColorControlCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclLevelControlCluster;
import com.zsmartsystems.zigbee.zcl.clusters.ZclOnOffCluster;
import com.zsmartsystems.zigbee.zcl.protocol.ZclClusterType;

import org.openhab.binding.zigbee.converter.color.ColorHelper;

/**
 *
 * @author Chris Jackson - Initial Contribution
 *
 */
public class ZigBeeConverterColorColor extends ZigBeeBaseChannelConverter implements ZclAttributeListener {
    private Logger logger = LoggerFactory.getLogger(ZigBeeConverterColorColor.class);

    private HSBType currentHSB;
    private ZclColorControlCluster clusterColorControl;
    private ZclLevelControlCluster clusterLevelControl;
    private ZclOnOffCluster clusterOnOff;

    private boolean initialised = false;

    private Object colorSync = new Object();
    private boolean supportsHue = false;
    private float lastHue = -1.0f;
    private float lastSaturation = -1.0f;
    private boolean hueChanged = false;
    private boolean saturationChanged = false;
    private float lastX = -1.0f;
    private float lastY = -1.0f;
    private boolean xChanged = false;
    private boolean yChanged = false;

    @Override
    public void initializeConverter() {
        if (initialised == true) {
            return;
        }

        currentHSB = new HSBType();

        clusterColorControl = (ZclColorControlCluster) endpoint.getInputCluster(ZclColorControlCluster.CLUSTER_ID);
        if (clusterColorControl == null) {
            logger.error("Error opening device control controls {}", endpoint.getIeeeAddress());
            return;
        }

        clusterLevelControl = (ZclLevelControlCluster) endpoint.getInputCluster(ZclLevelControlCluster.CLUSTER_ID);
        if (clusterLevelControl == null) {
            logger.error("Error opening device level controls {}", endpoint.getIeeeAddress());
            return;
        }

        clusterOnOff = (ZclOnOffCluster) endpoint.getInputCluster(ZclOnOffCluster.CLUSTER_ID);
        if (clusterOnOff == null) {
            logger.error("Error opening device on/off controls {}", endpoint.getIeeeAddress());
            return;
        }

        try {
            if(!clusterColorControl.discoverAttributes(false).get()) {
                logger.warn("{}: Cannot determine whether device endpoint supports RGB color. Assuming it supports HUE/SAT", endpoint.getEndpointAddress());
                supportsHue = true;
            }
            else if(clusterColorControl.getSupportedAttributes().contains(ZclColorControlCluster.ATTR_CURRENTHUE)) {
                supportsHue = true;
            }
            else if(!clusterColorControl.getSupportedAttributes().contains(ZclColorControlCluster.ATTR_CURRENTX)) {
                logger.debug("{}: Device endpoint does not support RGB color (probably only color temperature)", endpoint.getEndpointAddress());
                return;
            }
        }
        catch(InterruptedException | ExecutionException e) {
                logger.warn("{}: Exception checking whether device endpoint supports RGB color. Assuming it supports HUE/SAT", endpoint.getEndpointAddress(), e);
                supportsHue = true;
        }

        clusterColorControl.bind();
        clusterLevelControl.bind();
        clusterOnOff.bind();

        // Add a listener, then request the status
        clusterColorControl.addAttributeListener(this);
        clusterLevelControl.addAttributeListener(this);
        clusterOnOff.addAttributeListener(this);

        if(supportsHue) {
            clusterColorControl.getCurrentHue(0);
            clusterColorControl.getCurrentSaturation(0);
        }
        else {
            clusterColorControl.getCurrentX(0);
            clusterColorControl.getCurrentY(0);
        }

        clusterLevelControl.getCurrentLevel(0);
        clusterOnOff.getOnOff(0);

        // Configure reporting - no faster than once per second - no slower than 10 minutes.
        try {
            if(supportsHue) {
                clusterColorControl.setCurrentHueReporting(1, 600, 1).get();
                clusterColorControl.setCurrentSaturationReporting(1, 600, 1).get();
            }
            else {
                clusterColorControl.setCurrentXReporting(1, 600, 1).get();
                clusterColorControl.setCurrentYReporting(1, 600, 1).get();
            }

            clusterLevelControl.setCurrentLevelReporting(1, 600, 1).get();
            clusterOnOff.setOnOffReporting(1, 600).get();
        }
	catch (ExecutionException | InterruptedException e) {
            logger.debug("Exception configuring color reporting", e);
        }

        initialised = true;
    }

    @Override
    public void disposeConverter() {
        clusterColorControl.removeAttributeListener(this);
        clusterLevelControl.removeAttributeListener(this);
        clusterOnOff.removeAttributeListener(this);
    }

    @Override
    public void handleRefresh() {
        if(supportsHue) {
            clusterColorControl.getCurrentHue(0);
            clusterColorControl.getCurrentSaturation(0);
        }
        else {
            clusterColorControl.getCurrentX(0);
            clusterColorControl.getCurrentY(0);
        }

        clusterLevelControl.getCurrentLevel(0);
        clusterOnOff.getOnOff(0);
    }

    @Override
    public Runnable handleCommand(final Command command) {
        return new Runnable() {
            protected void changeOnOff(OnOffType onoff) throws InterruptedException, ExecutionException {
                if(onoff == OnOffType.ON) clusterOnOff.onCommand();
                else clusterOnOff.offCommand();
            }

            protected void changeBrightness(PercentType brightness) throws InterruptedException, ExecutionException {
                HSBType oldHSB = currentHSB;
                currentHSB = new HSBType(oldHSB.getHue(), oldHSB.getSaturation(), brightness);
                int level = (int) (brightness.floatValue() * 254.0f / 100.0f + 0.5f);

                clusterLevelControl.moveToLevelWithOnOffCommand(level, 10).get();
            }

            protected void changeColorHueSaturation(HSBType color) throws InterruptedException, ExecutionException {
                HSBType oldHSB = currentHSB;
                currentHSB = new HSBType(color.getHue(), color.getSaturation(), oldHSB.getBrightness());
                int hue = (int) (color.getHue().floatValue() * 254.0f / 360.0f + 0.5f);
                int saturation = (int) (color.getSaturation().floatValue() * 254.0f / 100.0f + 0.5f);

                clusterColorControl.moveToHueAndSaturationCommand(hue, saturation, 10).get();
            }

            protected void changeColorXY(HSBType color) throws InterruptedException, ExecutionException {
                HSBType oldHSB = currentHSB;
                currentHSB = new HSBType(color.getHue(), color.getSaturation(), oldHSB.getBrightness());
                logger.debug("{}: Change Color HSV. {}, {}, {}", endpoint.getEndpointAddress(), color.getHue(), color.getSaturation(), oldHSB.getBrightness());
                float hue = color.getHue().floatValue() / 360.0f;
                float saturation = color.getSaturation().floatValue() / 100.0f;
                // float xy[] = ColorHelper.hsv2xy(hue, saturation, 1.0f);
                float rgb[] = ColorHelper.hsv2rgb(hue, saturation, 1.0f);
                float xy[] = ColorHelper.rgb2xy(rgb[0], rgb[1], rgb[2]);
                logger.debug("{}: Calculated RGB. {}, {}, {}", endpoint.getEndpointAddress(), rgb[0], rgb[1], rgb[2]);
                logger.debug("{}: Calculated XY. {}, {}", endpoint.getEndpointAddress(), xy[0], xy[1]);
                int x = (int) (xy[0] * 65536.0f + 0.5f); // up to 65279
                int y = (int) (xy[1] * 65536.0f + 0.5f); // up to 65279

                clusterColorControl.moveToColorCommand(x, y, 10).get();
            }

            @Override
            public void run() {
                if (initialised == false) {
                    return;
                }

                try {
                    if (command instanceof OnOffType) {
                        changeOnOff((OnOffType) command);
                    }
                    else if (command instanceof PercentType) {
                        changeBrightness((PercentType) command);
                    }
                    if (command instanceof HSBType) {
                        HSBType color = (HSBType) command;
                        PercentType level = color.getBrightness();

                        if(level != currentHSB.getBrightness()) {
                            changeBrightness(level);
                            // Wait for transition to complete
                            // Some lights do not like receiving a level/color change command
                            // while the previous transition is in progress...
                            Thread.sleep(1100);
                        }

                        if(supportsHue) changeColorHueSaturation(color);
                        else changeColorXY(color);
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public Channel getChannel(ThingUID thingUID, ZigBeeEndpoint endpoint) {
        clusterColorControl = (ZclColorControlCluster) endpoint.getInputCluster(ZclColorControlCluster.CLUSTER_ID);

        if (clusterColorControl == null || endpoint.getInputCluster(ZclOnOffCluster.CLUSTER_ID) == null
                || endpoint.getInputCluster(ZclLevelControlCluster.CLUSTER_ID) == null) {
            return null;
        }

        try {
            if(!clusterColorControl.discoverAttributes(false).get()) {
                logger.warn("{}: Cannot determine whether device endpoint supports RGB color. Assuming it does by now (checking again later)", endpoint.getEndpointAddress());
            }
            else if(clusterColorControl.getSupportedAttributes().contains(ZclColorControlCluster.ATTR_CURRENTHUE)) {
                logger.debug("{}: Device endpoint supports HUE/SATURATION color", endpoint.getEndpointAddress());
            }
            else if(clusterColorControl.getSupportedAttributes().contains(ZclColorControlCluster.ATTR_CURRENTX)) {
                logger.debug("{}: Device endpoint supports XY color", endpoint.getEndpointAddress());
            }
            else {
                logger.debug("{}: Device endpoint does not support RGB color (probably only color temperature)", endpoint.getEndpointAddress());
                return null;
            }
        }
        catch(InterruptedException | ExecutionException e) {
                logger.warn("{}: Exception checking whether device endpoint supports RGB color. Assuming it does by now (checking again later)", endpoint.getEndpointAddress(), e);
        }

        return createChannel(thingUID, endpoint, ZigBeeBindingConstants.CHANNEL_COLOR_COLOR,
                ZigBeeBindingConstants.ITEM_TYPE_COLOR, "Color");
    }

    protected void updateOnOff(OnOffType onoff) {
        HSBType oldHSB = currentHSB;
        updateChannelState(onoff);
        logger.debug("{}: OnOff updated. {} -> {}", endpoint.getEndpointAddress(), oldHSB.toString(), onoff.toString());
    }

    protected void updateBrightness(PercentType brightness) {
        HSBType oldHSB = currentHSB;
        HSBType newHSB = new HSBType(oldHSB.getHue(), oldHSB.getSaturation(), brightness);
        currentHSB = newHSB;
        updateChannelState(brightness);
        updateChannelState(newHSB);
        logger.debug("{}: Brightness updated. {} -> {}", endpoint.getEndpointAddress(), oldHSB.toString(), newHSB.toString());
    }

    protected void updateColorHSB(DecimalType hue, PercentType saturation) {
        HSBType oldHSB = currentHSB;
        HSBType newHSB = new HSBType(hue, saturation, oldHSB.getBrightness());
        currentHSB = newHSB;
        updateChannelState(newHSB);
        logger.debug("{}: Color updated. {} -> {}", endpoint.getEndpointAddress(), oldHSB.toString(), newHSB.toString());
    }

    protected void updateColorXY(PercentType x, PercentType y) {
        logger.debug("{}: Update Color XY. {}, {}", endpoint.getEndpointAddress(), x.toString(), y.toString());
        // float hsb[] = ColorHelper.xy2hsv(lastX, lastY);
        float rgb[] = ColorHelper.xy2rgb(x.floatValue() / 100.0f, y.floatValue() / 100.0f);
        float hsb[] = ColorHelper.rgb2hsv(rgb[0], rgb[1], rgb[2]);
        logger.debug("{}: Calculated RGB. {}, {}, {}", endpoint.getEndpointAddress(), rgb[0], rgb[1], rgb[2]);
        logger.debug("{}: Calculated HSV. {}, {}, {}", endpoint.getEndpointAddress(), hsb[0], hsb[1], hsb[2]);
        DecimalType hue = new DecimalType(hsb[0] * 360.0f);
        PercentType saturation = new PercentType(Float.valueOf(hsb[1] * 100.0f).toString());
        updateColorHSB(hue, saturation);
    }

    protected void updateColorHSB() {
        DecimalType hue = new DecimalType(Float.valueOf(lastHue).toString());
        PercentType saturation = new PercentType(Float.valueOf(lastSaturation).toString());
        updateColorHSB(hue, saturation);
        hueChanged = false;
        saturationChanged = false;
    }

    protected void updateColorXY() {
        PercentType x = new PercentType(Float.valueOf(lastX * 100.0f).toString());
        PercentType y = new PercentType(Float.valueOf(lastY * 100.0f).toString());
        updateColorXY(x, y);
        xChanged = false;
        yChanged = false;
    }

    @Override
    public void attributeUpdated(ZclAttribute attribute) {
        logger.debug("ZigBee attribute reports {} from {}", attribute, endpoint.getIeeeAddress());
        synchronized(colorSync) {
            try {
                if (attribute.getCluster().getId() == ZclOnOffCluster.CLUSTER_ID && attribute.getId() == ZclOnOffCluster.ATTR_ONOFF) {
                    Boolean value = (Boolean) attribute.getLastValue();
                    OnOffType onoff = value ? OnOffType.ON : OnOffType.OFF;
                    updateOnOff(onoff);
                }
                else if (attribute.getCluster().getId() == ZclLevelControlCluster.CLUSTER_ID && attribute.getId() == ZclLevelControlCluster.ATTR_CURRENTLEVEL) {
                    Integer value = (Integer) attribute.getLastValue();
                    PercentType brightness = new PercentType(Float.valueOf(value * 100.0f / 254.0f).toString());
                    updateBrightness(brightness);
                }
                else if (attribute.getCluster().getId() == ZclColorControlCluster.CLUSTER_ID && attribute.getId() == ZclColorControlCluster.ATTR_CURRENTHUE) {
                    Integer value = (Integer) attribute.getLastValue();
                    float hue = value * 360.0f / 254.0f;
                    if(hue != lastHue) {
                        lastHue = hue;
                        hueChanged = true;
                    }
                }
                else if (attribute.getCluster().getId() == ZclColorControlCluster.CLUSTER_ID && attribute.getId() == ZclColorControlCluster.ATTR_CURRENTSATURATION) {
                    Integer value = (Integer) attribute.getLastValue();
                    float saturation = value * 100.0f / 254.0f;
                    if(saturation != lastSaturation) {
                        lastSaturation = saturation;
                        saturationChanged = true;
                    }
                }
                else if (attribute.getCluster().getId() == ZclColorControlCluster.CLUSTER_ID && attribute.getId() == ZclColorControlCluster.ATTR_CURRENTX) {
                    Integer value = (Integer) attribute.getLastValue();
                    float x = value / 65536.0f;
                    if(x != lastX) {
                        lastX = x;
                        xChanged = true;
                    }
                }
                else if (attribute.getCluster().getId() == ZclColorControlCluster.CLUSTER_ID && attribute.getId() == ZclColorControlCluster.ATTR_CURRENTY) {
                    Integer value = (Integer) attribute.getLastValue();
                    float y = value / 65536.0f;
                    if(y != lastY) {
                        lastY = y;
                        yChanged = true;
                    }
                }

                if(hueChanged && saturationChanged) {
                    updateColorHSB();
                }
                else if(xChanged && yChanged) {
                    updateColorXY();
                }
                else if(hueChanged || saturationChanged || xChanged || yChanged) {
                    // Wait some time and update anyway if only one attribute in each pair is updated
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                sleep(500);
                                synchronized(colorSync) {
                                    if((hueChanged || saturationChanged) && lastHue >= 0.0f && lastSaturation >= 0.0f) {
                                        updateColorHSB();
                                    }
                                    else if((xChanged || yChanged) && lastX >= 0.0f && lastY >= 0.0f) {
                                        updateColorXY();
                                    }
                                }
                            }
                            catch(Exception e) {
                                logger.debug("{}: Exception in deferred attribute update", endpoint.getEndpointAddress(), e);
                            }
                        }
                    }.start();
                }
            }
            catch(Exception e) {
                logger.debug("{}: Exception in attribute update", endpoint.getEndpointAddress(), e);
            }
        }
    }
}
