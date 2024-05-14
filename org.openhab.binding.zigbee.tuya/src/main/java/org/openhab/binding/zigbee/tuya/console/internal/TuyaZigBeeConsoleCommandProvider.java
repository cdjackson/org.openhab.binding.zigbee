/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.zigbee.tuya.console.internal;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openhab.binding.zigbee.console.ZigBeeConsoleCommandProvider;
import org.openhab.core.thing.ThingTypeUID;
import org.osgi.service.component.annotations.Component;

import com.zsmartsystems.zigbee.console.ZigBeeConsoleCommand;

/**
 * This class provides ZigBee console commands for TuYa devices.
 *
 * @author Chris Jackson - initial contribution
 */
@Component(immediate = true)
public class TuyaZigBeeConsoleCommandProvider implements ZigBeeConsoleCommandProvider {

    public static final List<ZigBeeConsoleCommand> TUYA_COMMANDS = unmodifiableList(
            asList(new ZigBeeConsoleTuyaCommand()));

    private Map<String, ZigBeeConsoleCommand> tuyaCommands = TUYA_COMMANDS.stream()
            .collect(toMap(ZigBeeConsoleCommand::getCommand, identity()));

    @Override
    public ZigBeeConsoleCommand getCommand(String commandName, ThingTypeUID thingTypeUID) {
        return tuyaCommands.get(commandName);

    }

    @Override
    public Collection<ZigBeeConsoleCommand> getAllCommands() {
        return TUYA_COMMANDS;
    }
}
