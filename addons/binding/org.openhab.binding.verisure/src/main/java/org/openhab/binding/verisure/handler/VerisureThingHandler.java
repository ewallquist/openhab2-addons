/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.verisure.handler;

import static org.openhab.binding.verisure.VerisureBindingConstants.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
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
import org.eclipse.smarthome.core.thing.binding.BridgeHandler;
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
 * @author Jarle Hjortland - Initial contribution
 *
 */
@NonNullByDefault
public class VerisureThingHandler extends BaseThingHandler implements DeviceStatusListener {

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Sets.newHashSet(THING_TYPE_ALARM,
            THING_TYPE_SMARTPLUG, THING_TYPE_SMOKEDETECTOR, THING_TYPE_WATERDETETOR, THING_TYPE_SIREN,
            THING_TYPE_SMARTLOCK, THING_TYPE_USERPRESENCE, THING_TYPE_DOORWINDOW, THING_TYPE_BROADBAND_CONNECTION);

    private Logger logger = LoggerFactory.getLogger(VerisureThingHandler.class);

    private @Nullable VerisureSession session;

    private @Nullable String id;

    public VerisureThingHandler(Thing thing) {
        super(thing);
        this.id = thing.getUID().getId();

    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("handleCommand, channel: {}, command: {}", channelUID, command);
        if (command instanceof RefreshType) {
            Bridge bridge = getBridge();
            if (bridge != null) {
                BridgeHandler bridgeHandler = bridge.getHandler();
                if (bridgeHandler != null) {
                    bridgeHandler.handleCommand(channelUID, command);
                }
            }
            if (session != null && this.id != null) {
                VerisureThingJSON thing = session.getVerisureThing(this.id);
                update(thing);
            }
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
        if (session != null && this.id != null) {
            VerisureAlarmJSON alarm = (VerisureAlarmJSON) session.getVerisureThing(this.id);
            if (alarm != null) {
                if (command.toString().equals("0")) {
                    logger.debug("attempting to turn off alarm!");
                    String siteName = alarm.getSiteName();
                    if (siteName != null && session != null) {
                        session.disarmAlarm(siteName);
                        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
                        updateState(cuid, new StringType("pending"));
                    }
                } else if (command.toString().equals("1")) {
                    logger.debug("arming at home");
                    String siteName = alarm.getSiteName();
                    if (siteName != null && session != null) {
                        session.armHomeAlarm(siteName);
                        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
                        updateState(cuid, new StringType("pending"));
                    }
                } else if (command.toString().equals("2")) {
                    logger.debug("arming away!");
                    String siteName = alarm.getSiteName();
                    if (siteName != null && session != null) {
                        session.armAwayAlarm(siteName);
                        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
                        updateState(cuid, new StringType("pending"));
                    }
                } else {
                    logger.debug("unknown command!");
                }
            }
        }
    }

    private void handleSmartLockState(Command command) {
        if (session != null && this.id != null) {
            VerisureSmartLockJSON smartLock = (VerisureSmartLockJSON) session.getVerisureThing(this.id);
            if (smartLock != null) {
                if (command == OnOffType.OFF) {
                    logger.debug("Attempting to unlock!");
                    String siteName = smartLock.getSiteName();
                    if (siteName != null && this.id != null && session != null) {
                        session.unLock(this.id, siteName);
                        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
                        updateState(cuid, new StringType("pending"));
                    }
                } else if (command == OnOffType.ON) {
                    logger.debug("Attempting to lock");
                    String siteName = smartLock.getSiteName();
                    if (siteName != null && this.id != null && session != null) {
                        session.lock(this.id, siteName);
                        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
                        updateState(cuid, new StringType("pending"));
                    }
                } else {
                    logger.debug("unknown command! {}", command);
                }
            }
        }

    }

    private void handleSmartPlugState(Command command) {
        if (session != null && this.id != null) {
            VerisureSmartPlugJSON smartPlug = (VerisureSmartPlugJSON) session.getVerisureThing(this.id);
            if (smartPlug != null) {
                if (command == OnOffType.OFF) {
                    logger.debug("Attempting to turn SmartPlug off!");
                    String siteName = smartPlug.getSiteName();
                    if (siteName != null && this.id != null && session != null) {
                        session.smartPlugOff(this.id, siteName);
                        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
                        updateState(cuid, new StringType("pending"));
                    }
                } else if (command == OnOffType.ON) {
                    logger.debug("Attempting to turn SmartPlug on");
                    String siteName = smartPlug.getSiteName();
                    if (siteName != null && this.id != null && session != null) {
                        session.smartPlugOn(this.id, siteName);
                        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
                        updateState(cuid, new StringType("pending"));
                    }
                } else {
                    logger.debug("unknown command! {}", command);
                }
            }
        }
    }

    private void handleAutoRelock(Command command) {
        if (session != null && this.id != null) {
            VerisureSmartLockJSON smartLock = (VerisureSmartLockJSON) session.getVerisureThing(this.id);
            if (smartLock != null) {
                if (command == OnOffType.ON) {
                    logger.debug("Attempting to turn Auto Relock on");
                    if (session != null) {
                        session.autoRelockOn(this.id, smartLock.getSiteName(), smartLock.getLocation(),
                                smartLock.getDoorLockVolumeSettings());
                        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_AUTO_RELOCK_ENABLED);
                        updateState(cuid, new StringType("true"));
                        smartLock.setAutoRelockEnabled(true);
                    }
                } else if (command == OnOffType.OFF) {
                    logger.debug("Attempting to turn Auto Relock off!");
                    if (session != null) {
                        session.autoRelockOff(this.id, smartLock.getSiteName(), smartLock.getLocation(),
                                smartLock.getDoorLockVolumeSettings());
                        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_AUTO_RELOCK_ENABLED);
                        updateState(cuid, new StringType("false"));
                        smartLock.setAutoRelockEnabled(false);
                    }
                } else {
                    logger.debug("unknown command! {}", command);
                }
            }
        }
    }

    private void handleSmartLockVolume(Command command) {
        if (session != null && this.id != null) {
            VerisureSmartLockJSON smartLock = (VerisureSmartLockJSON) session.getVerisureThing(this.id);
            if (smartLock != null) {
                DoorLockVolumeSettings settings = smartLock.getDoorLockVolumeSettings();
                if (settings != null) {
                    List<String> volumeSettings = settings.getAvailableVolumes();
                    if (volumeSettings != null) {
                        Boolean isVolumeSettingAllowed = Boolean.FALSE;

                        for (String volume : volumeSettings) {
                            if (volume.equals(command.toString())) {
                                isVolumeSettingAllowed = Boolean.TRUE;
                                break;
                            }
                        }

                        if (isVolumeSettingAllowed) {
                            if (session != null) {
                                session.setSmartLockVolume(this.id, smartLock.getSiteName(), smartLock.getLocation(),
                                        smartLock.getAutoRelockEnabled(), smartLock.getDoorLockVolumeSettings(),
                                        command.toString());
                                ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_SMARTLOCK_VOLUME);
                                updateState(cuid, new StringType(command.toString()));
                                settings.setVolume(command.toString());
                            }
                        } else {
                            logger.debug("unknown command! {}", command);
                        }
                    }
                }
            }
        }
    }

    private void handleSmartLockVoiceLevel(Command command) {
        if (session != null && this.id != null) {
            VerisureSmartLockJSON smartLock = (VerisureSmartLockJSON) session.getVerisureThing(this.id);
            if (smartLock != null) {
                DoorLockVolumeSettings settings = smartLock.getDoorLockVolumeSettings();
                if (settings != null) {
                    List<String> voiceLevelSettings = settings.getAvailableVoiceLevels();
                    if (voiceLevelSettings != null) {
                        Boolean isVoiceLevelSettingAllowed = Boolean.FALSE;

                        for (String voiceLevel : voiceLevelSettings) {
                            if (voiceLevel.equals(command.toString())) {
                                isVoiceLevelSettingAllowed = Boolean.TRUE;
                                break;
                            }
                        }

                        if (isVoiceLevelSettingAllowed) {
                            if (session != null) {
                                session.setSmartLockVoiceLevel(this.id, smartLock.getSiteName(),
                                        smartLock.getLocation(), smartLock.getAutoRelockEnabled(),
                                        smartLock.getDoorLockVolumeSettings(), command.toString());
                                ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_SMARTLOCK_VOICE_LEVEL);
                                updateState(cuid, new StringType(command.toString()));
                                settings.setVoiceLevel(command.toString());
                            }
                        } else {
                            logger.debug("unknown command! {}", command);
                        }
                    }
                }
            }
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
        logger.debug("initialize");
        // Do not go online
        Bridge bridge = getBridge();
        if (bridge != null) {
            this.bridgeStatusChanged(bridge.getStatusInfo());
        }
    }

    @Override
    public void dispose() {
        logger.debug("dispose");
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        logger.debug("bridgeStatusChanged");
        if (bridgeStatusInfo.getStatus() == ThingStatus.ONLINE) {
            Bridge bridge = getBridge();
            if (bridge != null) {
                VerisureBridgeHandler vbh = (VerisureBridgeHandler) bridge.getHandler();
                if (vbh != null) {
                    session = vbh.getSession();
                    if (session != null && this.id != null) {
                        update(session.getVerisureThing(this.id));
                        vbh.registerObjectStatusListener(this);
                    }
                }
            }
        }
        super.bridgeStatusChanged(bridgeStatusInfo);
    }

    public synchronized void update(@Nullable VerisureThingJSON thing) {
        logger.debug("update on thing: {}", thing);
        updateStatus(ThingStatus.ONLINE);
        if (getThing().getThingTypeUID().equals(THING_TYPE_SMOKEDETECTOR)) {
            VerisureClimateBaseJSON obj = (VerisureClimateBaseJSON) thing;
            if (obj != null) {
                updateClimateDeviceState(obj);
            }
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_WATERDETETOR)) {
            VerisureClimateBaseJSON obj = (VerisureClimateBaseJSON) thing;
            if (obj != null) {
                updateClimateDeviceState(obj);
            }
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_SIREN)) {
            VerisureClimateBaseJSON obj = (VerisureClimateBaseJSON) thing;
            if (obj != null) {
                updateClimateDeviceState(obj);
            }
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_ALARM)) {
            VerisureAlarmJSON obj = (VerisureAlarmJSON) thing;
            if (obj != null) {
                updateAlarmState(obj);
            }
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_SMARTLOCK)) {
            VerisureSmartLockJSON obj = (VerisureSmartLockJSON) thing;
            if (obj != null) {
                updateSmartLockState(obj);
            }
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_DOORWINDOW)) {
            VerisureDoorWindowsJSON obj = (VerisureDoorWindowsJSON) thing;
            if (obj != null) {
                updateDoorWindowState(obj);
            }
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_USERPRESENCE)) {
            VerisureUserPresenceJSON obj = (VerisureUserPresenceJSON) thing;
            if (obj != null) {
                updateUserPresenceState(obj);
            }
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_BROADBAND_CONNECTION)) {
            VerisureBroadbandConnectionJSON obj = (VerisureBroadbandConnectionJSON) thing;
            if (obj != null) {
                updateBroadbandConnection(obj);
            }
        } else if (getThing().getThingTypeUID().equals(THING_TYPE_SMARTPLUG)) {
            VerisureSmartPlugJSON obj = (VerisureSmartPlugJSON) thing;
            if (obj != null) {
                updateSmartPlugState(obj);
            }
        } else {
            logger.warn("cant handle this thing typeuid: {}", getThing().getThingTypeUID());

        }

    }

    private void updateClimateDeviceState(VerisureClimateBaseJSON status) {
        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_TEMPERATURE);
        String val;
        String temperature = status.getTemperature();
        if (temperature != null) {
            val = temperature.substring(0, temperature.length() - 6).replace(",", ".");
            DecimalType number = new DecimalType(val);
            updateState(cuid, number);
        }

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_HUMIDITY);
        String humidity = status.getHumidity();
        if (humidity != null && humidity.length() > 1) {
            val = humidity.substring(0, humidity.indexOf("%")).replace(",", ".");
            DecimalType hnumber = new DecimalType(val);
            updateState(cuid, hnumber);
        }

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_LASTUPDATE);
        updateState(cuid, new StringType(status.getTimestamp()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_LOCATION);
        updateState(cuid, new StringType(status.getLocation()));

        BigDecimal siteId = status.getSiteId();
        if (siteId != null) {
            cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_ID);
            DecimalType instId = new DecimalType(siteId);
            updateState(cuid, instId);
        }

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_NAME);
        StringType instName = new StringType(status.getSiteName());
        updateState(cuid, instName);
    }

    private void updateAlarmState(VerisureAlarmJSON status) {
        try {
            ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
            String alarmStatus = status.getStatus();
            if (alarmStatus != null) {
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
                BigDecimal siteId = status.getSiteId();
                if (siteId != null) {
                    DecimalType instId = new DecimalType(siteId);
                    updateState(cuid, instId);
                }

                cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_NAME);
                StringType instName = new StringType(status.getSiteName());
                updateState(cuid, instName);
            }

        } catch (Exception e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
        }
    }

    private void updateSmartLockState(VerisureSmartLockJSON status) {
        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
        String smartLockStatus = status.getStatus();
        if (smartLockStatus != null) {
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

            cuid = new ChannelUID(getThing().getUID(), CHANNEL_NUMERIC_STATUS);
            DecimalType val = new DecimalType(0);
            if (smartLockStatus.equals("locked")) {
                val = new DecimalType(1);
            } else {
                val = new DecimalType(0);
            }
            updateState(cuid, val);

            cuid = new ChannelUID(getThing().getUID(), CHANNEL_SMARTLOCK_STATUS);
            smartLockStatus = status.getLabel();
            updateState(cuid, new StringType(smartLockStatus));

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
            if (autoRelock != null && autoRelock) {
                updateState(cuid, OnOffType.ON);
            } else {
                updateState(cuid, OnOffType.OFF);
            }

            cuid = new ChannelUID(getThing().getUID(), CHANNEL_AUTO_RELOCK_ENABLED);
            Boolean autoRelockEnabled = status.getAutoRelockEnabled();
            if (autoRelockEnabled != null) {
                updateState(cuid, new StringType(autoRelockEnabled.toString()));
            }

            DoorLockVolumeSettings dlvs = status.getDoorLockVolumeSettings();
            if (dlvs != null) {
                cuid = new ChannelUID(getThing().getUID(), CHANNEL_SMARTLOCK_VOLUME);
                StringType volume = new StringType(dlvs.getVolume());
                updateState(cuid, volume);

                cuid = new ChannelUID(getThing().getUID(), CHANNEL_SMARTLOCK_VOICE_LEVEL);
                StringType voiceLevel = new StringType(dlvs.getVoiceLevel());
                updateState(cuid, voiceLevel);
            }

            cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_ID);
            BigDecimal siteId = status.getSiteId();
            if (siteId != null) {
                DecimalType instId = new DecimalType(siteId);
                updateState(cuid, instId);
            }

            cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_NAME);
            StringType instName = new StringType(status.getSiteName());
            updateState(cuid, instName);
        }

    }

    private void updateDoorWindowState(VerisureDoorWindowsJSON status) {
        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATE);
        updateState(cuid, new StringType(status.getState()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_LOCATION);
        updateState(cuid, new StringType(status.getArea()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_ID);
        BigDecimal siteId = status.getSiteId();
        if (siteId != null) {
            DecimalType instId = new DecimalType(siteId);
            updateState(cuid, instId);
        }

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
        BigDecimal siteId = status.getSiteId();
        if (siteId != null) {
            DecimalType instId = new DecimalType(siteId);
            updateState(cuid, instId);
        }

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
        BigDecimal siteId = status.getSiteId();
        if (siteId != null) {
            DecimalType instId = new DecimalType(siteId);
            updateState(cuid, instId);
        }

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_NAME);
        StringType instName = new StringType(status.getSiteName());
        updateState(cuid, instName);
    }

    private void updateBroadbandConnection(VerisureBroadbandConnectionJSON status) {
        ChannelUID cuid = new ChannelUID(getThing().getUID(), CHANNEL_TIMESTAMP);
        updateState(cuid, new StringType(status.getDate()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_HAS_WIFI);
        Boolean hasWiFi = status.hasWiFi();
        if (hasWiFi != null) {
            updateState(cuid, new StringType(hasWiFi.toString()));
        }

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_STATUS);
        updateState(cuid, new StringType(status.getStatus()));

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_ID);
        BigDecimal siteId = status.getSiteId();
        if (siteId != null) {
            DecimalType instId = new DecimalType(siteId);
            updateState(cuid, instId);
        }

        cuid = new ChannelUID(getThing().getUID(), CHANNEL_SITE_INSTALLATION_NAME);
        StringType instName = new StringType(status.getSiteName());
        updateState(cuid, instName);
    }

    @Override
    public void onDeviceStateChanged(@Nullable VerisureThingJSON updateObject) {
        if (updateObject != null) {
            String id = updateObject.getId();
            if (id != null) {
                if (id.equals(this.id)) {
                    update(updateObject);
                }
            }
        }
    }

    @Override
    public void onDeviceRemoved(@Nullable VerisureThingJSON updateObject) {
    }

    @Override
    public void onDeviceAdded(@Nullable VerisureThingJSON updateObject) {
    }
}
