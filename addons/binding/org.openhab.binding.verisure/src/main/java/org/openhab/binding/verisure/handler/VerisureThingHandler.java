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

import java.util.List;
import java.util.Set;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.verisure.internal.DeviceStatusListener;
import org.openhab.binding.verisure.internal.VerisureAlarmJSON;
import org.openhab.binding.verisure.internal.VerisureBroadbandConnectionJSON;
import org.openhab.binding.verisure.internal.VerisureClimateBaseJSON;
import org.openhab.binding.verisure.internal.VerisureDoorWindowsJSON;
import org.openhab.binding.verisure.internal.VerisureSession;
import org.openhab.binding.verisure.internal.VerisureSmartLockJSON;
import org.openhab.binding.verisure.internal.VerisureSmartLockJSON.DoorLockVolumeSettings;
import org.openhab.binding.verisure.internal.VerisureSmartPlugJSON;
import org.openhab.binding.verisure.internal.VerisureThingJSON;
import org.openhab.binding.verisure.internal.VerisureUserPresenceJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Handler for all of the different thing types that Verisure provides.
 *
 * @author Jarle Hjortland
 *
 */
public class VerisureThingHandler extends BaseThingHandler implements DeviceStatusListener {

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Sets.newHashSet(THING_TYPE_ALARM,
            THING_TYPE_SMARTPLUG, THING_TYPE_SMOKEDETECTOR, THING_TYPE_WATERDETETOR, THING_TYPE_SIREN,
            THING_TYPE_SMARTLOCK, THING_TYPE_USERPRESENCE, THING_TYPE_DOORWINDOW, THING_TYPE_BROADBAND_CONNECTION);

    private Logger logger = LoggerFactory.getLogger(VerisureThingHandler.class);

    private VerisureSession session = null;

    private String id = null;

    public VerisureThingHandler(Thing thing) {
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
            update(session.getVerisureThing(this.id));
        } else if (channelUID.getId().equals(CHANNEL_SET_ALARM_STATUS)) {
            handleAlarmState(command);
            scheduleImmediateRefresh();
        } else if (channelUID.getId().equals(CHANNEL_SET_SMARTLOCK_STATUS)) {
            handleSmartLockState(command);
            scheduleImmediateRefresh();
        } else if (channelUID.getId().equals(CHANNEL_SET_AUTO_RELOCK)) {
            handleAutoRelock(command);
            scheduleImmediateRefresh();
        } else if (channelUID.getId().equals(CHANNEL_SET_SMARTLOCK_VOLUME)) {
            handleSmartLockVolume(command);
            scheduleImmediateRefresh();
        } else if (channelUID.getId().equals(CHANNEL_SET_SMARTLOCK_VOICE_LEVEL)) {
            handleSmartLockVoiceLevel(command);
            scheduleImmediateRefresh();
        } else if (channelUID.getId().equals(CHANNEL_SET_SMARTPLUG_STATUS)) {
            handleSmartPlugState(command);
            scheduleImmediateRefresh();
        } else {
            logger.warn("unknown command! {}", command);
        }
    }

    private void handleAlarmState(Command command) {
        VerisureAlarmJSON alarm = (VerisureAlarmJSON) session.getVerisureThing(this.id);

        if (command.toString().equals("0")) {
            logger.debug("attempting to turn off alarm!");
            session.disarmAlarm(alarm.getId());
            ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
            updateState(cuid, new StringType("pending"));
        } else if (command.toString().equals("1")) {
            logger.debug("arming at home");
            session.armHomeAlarm(alarm.getId());
            ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
            updateState(cuid, new StringType("pending"));

        } else if (command.toString().equals("2")) {
            logger.debug("arming away!");
            session.armAwayAlarm(alarm.getId());
            ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
            updateState(cuid, new StringType("pending"));
        } else {
            logger.debug("unknown command!");
        }
    }

    private void handleSmartLockState(Command command) {
        VerisureSmartLockJSON smartLock = (VerisureSmartLockJSON) session.getVerisureThing(this.id);
        if (command == OnOffType.OFF) {
            logger.debug("Attempting to unlock!");
            session.unLock(this.id, smartLock.getSiteName());
            ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
            updateState(cuid, new StringType("pending"));
        } else if (command == OnOffType.ON) {
            logger.debug("Attempting to lock");
            session.lock(this.id, smartLock.getSiteName());
            ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
            updateState(cuid, new StringType("pending"));
        } else {
            logger.debug("unknown command!", command);
        }
    }

    private void handleSmartPlugState(Command command) {
        VerisureSmartPlugJSON smartPlug = (VerisureSmartPlugJSON) session.getVerisureThing(this.id);
        if (command == OnOffType.OFF) {
            logger.debug("Attempting to turn SmartPlug off!");
            session.smartPlugOff(this.id, smartPlug.getSiteName());
            ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
            updateState(cuid, new StringType("pending"));
        } else if (command == OnOffType.ON) {
            logger.debug("Attempting to turn SmartPlug on");
            session.smartPlugOn(this.id, smartPlug.getSiteName());
            ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
            updateState(cuid, new StringType("pending"));
        } else {
            logger.debug("unknown command!", command.toString());
        }
    }

    private void handleAutoRelock(Command command) {
        VerisureSmartLockJSON smartLock = (VerisureSmartLockJSON) session.getVerisureThing(this.id);
        if (command == OnOffType.OFF) {
            logger.debug("Attempting to turn Auto Relock off!");
            session.autoRelockOff(this.id, smartLock.getSiteName(), smartLock.getLocation(),
                    smartLock.getDoorLockVolumeSettings());
            ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_AUTO_RELOCK_ENABLED);
            updateState(cuid, new StringType("false"));
            smartLock.setAutoRelockEnabled(false);
        } else if (command == OnOffType.ON) {
            logger.debug("Attempting to turn Auto Relock on");
            session.autoRelockOn(this.id, smartLock.getSiteName(), smartLock.getLocation(),
                    smartLock.getDoorLockVolumeSettings());
            ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_AUTO_RELOCK_ENABLED);
            updateState(cuid, new StringType("true"));
            smartLock.setAutoRelockEnabled(true);
        } else {
            logger.debug("unknown command!", command.toString());
        }
    }

    private void handleSmartLockVolume(Command command) {
        VerisureSmartLockJSON smartLock = (VerisureSmartLockJSON) session.getVerisureThing(this.id);
        DoorLockVolumeSettings settings = smartLock.getDoorLockVolumeSettings();
        List<String> volumeSettings = settings.getAvailableVolumes();
        Boolean isVolumeSettingAllowed = Boolean.FALSE;

        for (String volume : volumeSettings) {
            if (volume.equals(command.toString())) {
                isVolumeSettingAllowed = Boolean.TRUE;
                break;
            }
        }

        if (isVolumeSettingAllowed) {
            session.setSmartLockVolume(this.id, smartLock.getSiteName(), smartLock.getLocation(),
                    smartLock.getAutoRelockEnabled(), smartLock.getDoorLockVolumeSettings(), command.toString());
            ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_SMARTLOCK_VOLUME);
            updateState(cuid, new StringType(command.toString()));
            settings.setVolume(command.toString());
        } else {
            logger.debug("unknown command!", command.toString());
        }
    }

    private void handleSmartLockVoiceLevel(Command command) {
        VerisureSmartLockJSON smartLock = (VerisureSmartLockJSON) session.getVerisureThing(this.id);
        DoorLockVolumeSettings settings = smartLock.getDoorLockVolumeSettings();
        List<String> voiceLevelSettings = settings.getAvailableVoiceLevels();
        Boolean isVoiceLevelSettingAllowed = Boolean.FALSE;

        for (String voiceLevel : voiceLevelSettings) {
            if (voiceLevel.equals(command.toString())) {
                isVoiceLevelSettingAllowed = Boolean.TRUE;
                break;
            }
        }

        if (isVoiceLevelSettingAllowed) {
            session.setSmartLockVoiceLevel(this.id, smartLock.getSiteName(), smartLock.getLocation(),
                    smartLock.getAutoRelockEnabled(), smartLock.getDoorLockVolumeSettings(), command.toString());
            ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_SMARTLOCK_VOICE_LEVEL);
            updateState(cuid, new StringType(command.toString()));
            settings.setVoiceLevel(command.toString());
        } else {
            logger.debug("unknown command!", command.toString());
        }
    }

    private void scheduleImmediateRefresh() {
        Bridge brige = getBridge();
        if (brige != null && brige.getHandler() != null) {
            VerisureBridgeHandler vbh = (VerisureBridgeHandler) brige.getHandler();
            if (vbh != null) {
                vbh.scheduleImmediateRefresh();
            }
        }
    }

    @Override
    public void initialize() {
        // Do not go online
        Bridge bridge = getBridge();
        if (bridge != null) {
            this.bridgeStatusChanged(bridge.getStatusInfo());
        }
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        if (bridgeStatusInfo.getStatus() == ThingStatus.ONLINE) {
            Bridge bridge = getBridge();
            if (bridge != null) {
                VerisureBridgeHandler vbh = (VerisureBridgeHandler) bridge.getHandler();
                if (vbh != null) {
                    session = vbh.getSession();
                    update(session.getVerisureThing(this.id));
                    vbh.registerObjectStatusListener(this);
                }
            }
        }
        super.bridgeStatusChanged(bridgeStatusInfo);
    }

    public synchronized void update(VerisureThingJSON thing) {
        updateStatus(ThingStatus.ONLINE);
        if (getThing().getThingTypeUID().equals(THING_TYPE_SMOKEDETECTOR)) {
            VerisureClimateBaseJSON obj = (VerisureClimateBaseJSON) thing;
            updateClimateDeviceState(obj);
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_WATERDETETOR)) {
            VerisureClimateBaseJSON obj = (VerisureClimateBaseJSON) thing;
            updateClimateDeviceState(obj);
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_SIREN)) {
            VerisureClimateBaseJSON obj = (VerisureClimateBaseJSON) thing;
            updateClimateDeviceState(obj);
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_ALARM)) {
            VerisureAlarmJSON obj = (VerisureAlarmJSON) thing;
            updateAlarmState(obj);
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_SMARTLOCK)) {
            VerisureSmartLockJSON obj = (VerisureSmartLockJSON) thing;
            updateSmartLockState(obj);
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_DOORWINDOW)) {
            VerisureDoorWindowsJSON obj = (VerisureDoorWindowsJSON) thing;
            updateDoorWindowState(obj);
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_USERPRESENCE)) {
            VerisureUserPresenceJSON obj = (VerisureUserPresenceJSON) thing;
            updateUserPresenceState(obj);
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_BROADBAND_CONNECTION)) {
            VerisureBroadbandConnectionJSON obj = (VerisureBroadbandConnectionJSON) thing;
            updateBroadbandConnection(obj);
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_SMARTPLUG)) {
            VerisureSmartPlugJSON obj = (VerisureSmartPlugJSON) thing;
            updateSmartPlugState(obj);
        } else {
            logger.warn("cant handle this thing typeuid: {}", getThing().getThingTypeUID());

        }

    }

    private void updateClimateDeviceState(VerisureClimateBaseJSON status) {
        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_TEMPERATURE);
        String val = status.getTemperature().substring(0, status.getTemperature().length() - 6).replace(",", ".");

        DecimalType number = new DecimalType(val);
        updateState(cuid, number);

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_HUMIDITY);
        if (status.getHumidity() != null && status.getHumidity().length() > 1) {
            val = status.getHumidity().substring(0, status.getHumidity().indexOf("%")).replace(",", ".");
            DecimalType hnumber = new DecimalType(val);
            updateState(cuid, hnumber);
        }

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_LASTUPDATE);
        updateState(cuid, new StringType(status.getTimestamp()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_LOCATION);
        updateState(cuid, new StringType(status.getLocation()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_ID);
        DecimalType instId = new DecimalType(status.getSiteId());
        updateState(cuid, instId);

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_NAME);
        StringType instName = new StringType(status.getSiteName());
        updateState(cuid, instName);
    }

    private void updateAlarmState(VerisureAlarmJSON status) {
        try {
            ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
            String alarmStatus = status.getStatus();
            updateState(cuid, new StringType(alarmStatus));

            cuid = new ChannelUID(getThing().getUID(), CHANNEL_NUMERIC_STATUS);
            DecimalType val = new DecimalType(0);

            if (alarmStatus.equals("unarmed")) {
                val = new DecimalType(0);
            } else if (alarmStatus.equals("armedhome")) {
                val = new DecimalType(1);
            } else if (alarmStatus.equals("armedaway")) {
                val = new DecimalType(2);
            }
            updateState(cuid, val);

            cuid = new ChannelUID(getThing().getUID(), CHANNEL_CHANGED_BY_USER);
            String changedByUser = status.getName();
            updateState(cuid, new StringType(changedByUser));

            cuid = new ChannelUID(getThing().getUID(), CHANNEL_TIMESTAMP);
            String alarmTimeStamp = status.getDate();
            updateState(cuid, new StringType(alarmTimeStamp));

            cuid = new ChannelUID(getThing().getUID(), CHANNEL_ALARM_STATUS);
            String label = status.getLabel();
            updateState(cuid, new StringType(label));

            cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_ID);
            DecimalType instId = new DecimalType(status.getSiteId());
            updateState(cuid, instId);

            cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_NAME);
            StringType instName = new StringType(status.getSiteName());
            updateState(cuid, instName);

        } catch (Exception e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
        }
    }

    private void updateSmartLockState(VerisureSmartLockJSON status) {
        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
        String smartLockStatus = status.getStatus();
        updateState(cuid, new StringType(smartLockStatus));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SMARTLOCK_STATUS);
        if ("locked".equals(smartLockStatus)) {
            updateState(cuid, OnOffType.ON);
        } else if ("unlocked".equals(smartLockStatus)) {
            updateState(cuid, OnOffType.OFF);
        } else if ("pending".equals(smartLockStatus)) {
            // Schedule another refresh.
            this.scheduleImmediateRefresh();
        }

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SMARTLOCK_STATUS);
        smartLockStatus = status.getLabel();
        updateState(cuid, new StringType(smartLockStatus));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_NUMERIC_STATUS);
        DecimalType val = new DecimalType(0);
        if (smartLockStatus.equals("locked")) {
            val = new DecimalType(1);
        } else {
            val = new DecimalType(0);
        }
        updateState(cuid, val);

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_CHANGED_BY_USER);
        String changedByUser = status.getName();
        updateState(cuid, new StringType(changedByUser));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_TIMESTAMP);
        String alarmTimeStamp = status.getDate();
        updateState(cuid, new StringType(alarmTimeStamp));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_LOCATION);
        updateState(cuid, new StringType(status.getLocation()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_AUTO_RELOCK_ENABLED);
        Boolean autoRelock = status.getAutoRelockEnabled();
        if (autoRelock) {
            updateState(cuid, OnOffType.ON);
        } else {
            updateState(cuid, OnOffType.OFF);
        }

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_AUTO_RELOCK_ENABLED);
        updateState(cuid, new StringType(status.getAutoRelockEnabled().toString()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SMARTLOCK_VOLUME);
        StringType volume = new StringType(status.getDoorLockVolumeSettings().getVolume());
        updateState(cuid, volume);

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SMARTLOCK_VOICE_LEVEL);
        StringType voiceLevel = new StringType(status.getDoorLockVolumeSettings().getVoiceLevel());
        updateState(cuid, voiceLevel);

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_ID);
        DecimalType instId = new DecimalType(status.getSiteId());
        updateState(cuid, instId);

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_NAME);
        StringType instName = new StringType(status.getSiteName());
        updateState(cuid, instName);
    }

    private void updateDoorWindowState(VerisureDoorWindowsJSON status) {
        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATE);
        updateState(cuid, new StringType(status.getState()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_LOCATION);
        updateState(cuid, new StringType(status.getArea()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_ID);
        DecimalType instId = new DecimalType(status.getSiteId());
        updateState(cuid, instId);

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_NAME);
        StringType instName = new StringType(status.getSiteName());
        updateState(cuid, instName);
    }

    private void updateSmartPlugState(VerisureSmartPlugJSON status) {
        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
        String smartPlugStatus = status.getStatus();
        updateState(cuid, new StringType(smartPlugStatus));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SMARTPLUG_STATUS);
        if ("on".equals(smartPlugStatus)) {
            updateState(cuid, OnOffType.ON);
        } else if ("off".equals(smartPlugStatus)) {
            updateState(cuid, OnOffType.OFF);
        } else if ("pending".equals(smartPlugStatus)) {
            // Schedule another refresh.
            this.scheduleImmediateRefresh();
        }

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SMARTPLUG_STATUS);
        updateState(cuid, new StringType(status.getStatusText()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_LOCATION);
        updateState(cuid, new StringType(status.getLocation()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_HAZARDOUS);
        updateState(cuid, new StringType(status.getHazardous().toString()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_ID);
        DecimalType instId = new DecimalType(status.getSiteId());
        updateState(cuid, instId);

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_NAME);
        StringType instName = new StringType(status.getSiteName());
        updateState(cuid, instName);
    }

    private void updateUserPresenceState(VerisureUserPresenceJSON status) {
        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_USER_LOCATION_NAME);
        updateState(cuid, new StringType(status.getLocation()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_WEBACCOUNT);
        updateState(cuid, new StringType(status.getWebAccount()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_USER_LOCATION_STATUS);
        updateState(cuid, new StringType(status.getUserLocationStatus()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_ID);
        DecimalType instId = new DecimalType(status.getSiteId());
        updateState(cuid, instId);

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_NAME);
        StringType instName = new StringType(status.getSiteName());
        updateState(cuid, instName);
    }

    private void updateBroadbandConnection(VerisureBroadbandConnectionJSON status) {
        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_TIMESTAMP);
        updateState(cuid, new StringType(status.getDate()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_HAS_WIFI);
        updateState(cuid, new StringType(status.hasWiFi().toString()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
        updateState(cuid, new StringType(status.getStatus()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_ID);
        DecimalType instId = new DecimalType(status.getSiteId());
        updateState(cuid, instId);

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_NAME);
        StringType instName = new StringType(status.getSiteName());
        updateState(cuid, instName);
    }

    @Override
    public void onDeviceStateChanged(VerisureThingJSON updateObject) {
        if (updateObject.getId().equals(this.id)) {
            update(updateObject);
        }

    }

    @Override
    public void onDeviceRemoved(VerisureThingJSON updateObject) {
    }

    @Override
    public void onDeviceAdded(VerisureThingJSON updateObject) {
    }
}
