/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.verisure;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link VerisureBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author l3rum - Initial contribution
 */
public class VerisureBindingConstants {

    public static final String BINDING_ID = "verisure";

    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_BRIDGE = new ThingTypeUID(BINDING_ID, "bridge");
    public final static ThingTypeUID THING_TYPE_ALARM = new ThingTypeUID(BINDING_ID, "verisureAlarm");
    public final static ThingTypeUID THING_TYPE_SMARTPLUG = new ThingTypeUID(BINDING_ID, "smartPlug");
    public final static ThingTypeUID THING_TYPE_SMOKEDETECTOR = new ThingTypeUID(BINDING_ID, "smokeDetector");
    public final static ThingTypeUID THING_TYPE_WATERDETETOR = new ThingTypeUID(BINDING_ID, "waterDetector");
    public final static ThingTypeUID THING_TYPE_SIREN = new ThingTypeUID(BINDING_ID, "siren");
    public final static ThingTypeUID THING_TYPE_DOORWINDOW = new ThingTypeUID(BINDING_ID, "doorWindowSensor");
    public final static ThingTypeUID THING_TYPE_USERPRESENCE = new ThingTypeUID(BINDING_ID, "userPresence");
    public final static ThingTypeUID THING_TYPE_SMARTLOCK = new ThingTypeUID(BINDING_ID, "smartLock");
    public final static ThingTypeUID THING_TYPE_BROADBAND_CONNECTION = new ThingTypeUID(BINDING_ID,
            "broadbandConnection");

    // List of all Channel ids
    public final static String CHANNEL_NUMERIC_STATUS = "numericStatus";
    public final static String CHANNEL_TEMPERATURE = "temperature";
    public final static String CHANNEL_HUMIDITY = "humidity";
    public final static String CHANNEL_LASTUPDATE = "lastUpdate";
    public final static String CHANNEL_LOCATION = "location";
    public final static String CHANNEL_STATUS = "status";
    public final static String CHANNEL_STATE = "state";
    public final static String CHANNEL_LABEL = "label";
    public final static String CHANNEL_WEBACCOUNT = "webAccount";
    public final static String CHANNEL_USER_LOCATION_NAME = "userLocationName";
    public final static String CHANNEL_USER_LOCATION_STATUS = "userLocationStatus";
    public final static String CHANNEL_SET_ALARM_STATUS = "setAlarmStatus";
    public final static String CHANNEL_SET_SMARTLOCK_STATUS = "setSmartLockStatus";
    public final static String CHANNEL_SET_SMARTPLUG_STATUS = "setSmartPlugStatus";
    public final static String CHANNEL_SMARTPLUG_STATUS = "smartPlugStatus";
    public static final String CHANNEL_ALARM_SMARTLOCK_STATUS = "alarmSmartLockStatus";
    public final static String CHANNEL_CHANGED_BY_USER = "changedByUser";
    public final static String CHANNEL_TIMESTAMP = "timestamp";
    public final static String CHANNEL_HAS_WIFI = "hasWifi";
    public final static String CHANNEL_HAZARDOUS = "hazardous";

    // REST URI constants
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String BASEURL = "https://mypages.verisure.com";
    public static final String LOGON_SUF = "/j_spring_security_check?locale=en_GB";
    public static final String ALARM_COMMAND = "/remotecontrol/armstatechange.cmd";
    public static final String SMARTLOCK_COMMAND = "/remotecontrol/lockunlock.cmd";
    public static final String SMARTPLUG_COMMAND = "/settings/smartplug/onoffplug.cmd";
    public static final String START_SUF = "/uk/start.html";

    public static final String ALARMSTATUS_PATH = "/remotecontrol?_=";
    public static final String DOORWINDOW_PATH = "/settings/doorwindow?_=";
    public static final String USERTRACKING_PATH = "/overview/usertrackingcontacts?_=";
    public static final String CLIMATEDEVICE_PATH = "/overview/climatedevice?_=";
    public static final String SMARTPLUG_PATH = "/settings/smartplug?_=";
    public static final String ETHERNETSTATUS_PATH = "/overview/ethernetstatus?_=";
}
