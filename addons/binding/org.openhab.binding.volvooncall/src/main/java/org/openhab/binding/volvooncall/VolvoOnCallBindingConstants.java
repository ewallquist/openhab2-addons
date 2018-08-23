/**
 * Copyright (c) 2014,2018 by the respective copyright holders.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.volvooncall;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link VolvoOnCallBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author glhopital - Initial contribution
 */
@NonNullByDefault
public class VolvoOnCallBindingConstants {

    public static final String BINDING_ID = "volvooncall";

    // Vehicle properties
    public static final String VIN = "vin";

    // The URL to use to connect to VocAPI with.
    // TODO : for North America and China syntax changes to vocapi-cn.xxx
    public static final String SERVICE_URL = "https://vocapi.wirelesscar.net/customerapi/rest/v3.0/";

    // The JSON content type used when talking to VocAPI.
    public static final String JSON_CONTENT_TYPE = "application/json";

    // List of Bridge Type UIDs
    public static final ThingTypeUID APIBRIDGE_THING_TYPE = new ThingTypeUID(BINDING_ID, "vocapi");
    public static final ThingTypeUID VEHICLE_THING_TYPE = new ThingTypeUID(BINDING_ID, "vehicle");

    // List of all Channel id's
    public static final String TAILGATE = "tailgate";
    public static final String REAR_RIGHT = "rearRight";
    public static final String REAR_LEFT = "rearLeft";
    public static final String FRONT_RIGHT = "frontRight";
    public static final String FRONT_LEFT = "frontLeft";
    public static final String HOOD = "hood";
    public static final String REAR_RIGHT_WND = "rearRightWnd";
    public static final String REAR_LEFT_WND = "rearLeftWnd";
    public static final String FRONT_RIGHT_WND = "frontRightWnd";
    public static final String FRONT_LEFT_WND = "frontLeftWnd";
    public static final String ODOMETER = "odometer";
    public static final String TRIPMETER1 = "tripmeter1";
    public static final String TRIPMETER2 = "tripmeter2";
    public static final String DISTANCE_TO_EMPTY = "distanceToEmpty";
    public static final String FUEL_AMOUNT = "fuelAmount";
    public static final String FUEL_LEVEL = "fuelLevel";
    public static final String FUEL_CONSUMPTION = "fuelConsumption";
    public static final String CALCULATED_LOCATION = "calculatedLocation";
    public static final String ACTUAL_LOCATION = "location";
    public static final String LOCATION_TIMESTAMP = "locationTimestamp";
    public static final String HEADING = "heading";
    public static final String CAR_LOCKED = "carLocked";
    public static final String ENGINE_RUNNING = "engineRunning";

    // Optional Channels depends upon car version
    public static final String CAR_LOCATOR = "carLocator";
    public static final String JOURNAL_LOG = "journalLog";

    public static final String ENGINE_START = "engineStart";
    public static final String UNLOCK = "unlock";
    public static final String LOCK = "lock";
    public static final String HONK = "honk";
    public static final String BLINK = "blink";
    public static final String HONK_BLINK = "honkAndBlink";
    public static final String REMOTE_HEATER = "remoteHeater";
    public static final String PRECLIMATIZATION = "preclimatization";

    // List of all adressable things in OH = SUPPORTED_DEVICE_THING_TYPES_UIDS + the virtual bridge
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Stream
            .of(APIBRIDGE_THING_TYPE, VEHICLE_THING_TYPE).collect(Collectors.toSet());
}
