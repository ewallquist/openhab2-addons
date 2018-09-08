/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.verisure.handler;

import static org.openhab.binding.verisure.VerisureBindingConstants.*;

import java.util.Set;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.verisure.VerisureBindingConstants;
import org.openhab.binding.verisure.internal.DeviceStatusListener;
import org.openhab.binding.verisure.internal.VerisureAlarmJSON;
import org.openhab.binding.verisure.internal.VerisureClimateBaseJSON;
import org.openhab.binding.verisure.internal.VerisureDoorWindowsJSON;
import org.openhab.binding.verisure.internal.VerisureObjectJSON;
import org.openhab.binding.verisure.internal.VerisureSession;
import org.openhab.binding.verisure.internal.VerisureSmartPlugJSON;
import org.openhab.binding.verisure.internal.VerisureUserTrackingJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Handler for all of the different object types that Verisure provides.
 *
 * @author Jarle Hjortland
 *
 */
public class VerisureObjectHandler extends BaseThingHandler implements DeviceStatusListener {

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Sets.newHashSet(THING_TYPE_ALARM,
            THING_TYPE_SMARTPLUG, THING_TYPE_SMOKEDETECTOR, THING_TYPE_WATERDETETOR, THING_TYPE_SIREN,
            THING_TYPE_SMARTLOCK, THING_TYPE_USERPRESENCE, THING_TYPE_DOORWINDOW);

    private Logger logger = LoggerFactory.getLogger(VerisureObjectHandler.class);

    private VerisureSession session = null;

    private String id = null;

    public VerisureObjectHandler(Thing thing) {
        super(thing);
        this.id = thing.getUID().getId();

    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            Bridge bridge = getBridge();
            if (bridge != null && bridge.getHandler() != null) {
                bridge.getHandler().handleCommand(channelUID, command);
            }
            update(session.getVerisureObject(this.id));
        } else if (channelUID.getId().equals(CHANNEL_SET_SMARTLOCK_STATUS)) {
            handleSmartLockState(command);
            session.refresh();
        } else {
            logger.warn("unknown command! {}", command);
        }
    }

    private void handleSmartLockState(Command command) {
        if (command.toString().equals("0")) {
            logger.debug("Attempting to unlock!");
            session.unLock(this.id);
            ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
            updateState(cuid, new StringType("pending"));
        } else if (command.toString().equals("1")) {
            logger.debug("Attempting to lock");
            session.lock(this.id);
            ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
            updateState(cuid, new StringType("pending"));
        } else {
            logger.debug("unknown command!", command);
        }
    }

    @Override
    public void initialize() {
        // Do not go online
        if (getBridge() != null) {
            this.bridgeStatusChanged(getBridge().getStatusInfo());
        }
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        if (bridgeStatusInfo.getStatus() == ThingStatus.ONLINE) {
            if (this.getBridge() != null) {
                VerisureBridgeHandler vbh = (VerisureBridgeHandler) this.getBridge().getHandler();
                session = vbh.getSession();
                update(session.getVerisureObject(this.id));
                vbh.registerObjectStatusListener(this);
            }
        }
        super.bridgeStatusChanged(bridgeStatusInfo);
    }

    public synchronized void update(VerisureObjectJSON object) {
        updateStatus(ThingStatus.ONLINE);
        if (getThing().getThingTypeUID().equals(THING_TYPE_SMOKEDETECTOR)) {
            VerisureClimateBaseJSON obj = (VerisureClimateBaseJSON) object;
            updateClimateDeviceState(obj);
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_WATERDETETOR)) {
            VerisureClimateBaseJSON obj = (VerisureClimateBaseJSON) object;
            updateClimateDeviceState(obj);
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_SIREN)) {
            VerisureClimateBaseJSON obj = (VerisureClimateBaseJSON) object;
            updateClimateDeviceState(obj);
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_SMARTLOCK)) {
            VerisureAlarmJSON obj = (VerisureAlarmJSON) object;
            updateSmartLockState(obj);
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_DOORWINDOW)) {
            VerisureDoorWindowsJSON obj = (VerisureDoorWindowsJSON) object;
            updateDoorWindowState(obj);
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_USERPRESENCE)) {
            VerisureUserTrackingJSON obj = (VerisureUserTrackingJSON) object;
            updateUserPresenceState(obj);
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_SMARTPLUG)) {
            VerisureSmartPlugJSON obj = (VerisureSmartPlugJSON) object;
            updateSmartPlugState(obj);
        } else {
            logger.warn("cant handle this thing typeuid: {}", getThing().getThingTypeUID());

        }

    }

    private void updateClimateDeviceState(VerisureClimateBaseJSON obj) {
        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_TEMPERATURE);
        ChannelUID huid = new ChannelUID(getThing().getUID(), CHANNEL_HUMIDITY);
        ChannelUID luid = new ChannelUID(getThing().getUID(), CHANNEL_LASTUPDATE);
        ChannelUID loid = new ChannelUID(getThing().getUID(), CHANNEL_LOCATION);
        String val = obj.getTemperature().substring(0, obj.getTemperature().length() - 6).replace(",", ".");

        DecimalType number = new DecimalType(val);
        updateState(cuid, number);
        if (obj.getHumidity() != null && obj.getHumidity().length() > 1) {
            val = obj.getHumidity().substring(0, obj.getHumidity().indexOf("%")).replace(",", ".");
            DecimalType hnumber = new DecimalType(val);
            updateState(huid, hnumber);
        }
        StringType lastUpdate = new StringType(obj.getTimestamp());
        updateState(luid, lastUpdate);
        StringType location = new StringType(obj.getLocation());
        updateState(loid, location);
    }

    private void updateSmartLockState(VerisureAlarmJSON status) {
        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
        updateState(cuid, new StringType(status.getStatus()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_CHANGEDBYUSER);
        updateState(cuid, new StringType(status.getName()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_TIMESTAMP);
        updateState(cuid, new StringType(status.getDate()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_LOCATION);
        updateState(cuid, new StringType(status.getLocation()));

        cuid = new ChannelUID(getThing().getUID(), VerisureBindingConstants.CHANNEL_ALARM_SMARTLOCK_STATUS);
        updateState(cuid, new StringType(status.getLabel()));
    }

    private void updateDoorWindowState(VerisureDoorWindowsJSON status) {
        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATE);
        updateState(cuid, new StringType(status.getState()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_LOCATION);
        updateState(cuid, new StringType(status.getArea()));

    }

    private void updateSmartPlugState(VerisureSmartPlugJSON status) {

    }

    private void updateUserPresenceState(VerisureUserTrackingJSON status) {
        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_USER_LOCATION_NAME);
        updateState(cuid, new StringType(status.getLocation()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_WEBACCOUNT);
        updateState(cuid, new StringType(status.getWebAccount()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_USER_LOCATION_STATUS);
        updateState(cuid, new StringType(status.getUserLocationStatus()));
    }

    @Override
    public void onDeviceStateChanged(VerisureObjectJSON updateObject) {
        if (updateObject.getId().equals(this.id)) {
            update(updateObject);
        }

    }

    @Override
    public void onDeviceRemoved(VerisureObjectJSON updateObject) {
    }

    @Override
    public void onDeviceAdded(VerisureObjectJSON updateObject) {
    }
}
