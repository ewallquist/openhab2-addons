/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.verisure.internal.discovery;

import static org.openhab.binding.verisure.VerisureBindingConstants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.verisure.handler.VerisureBridgeHandler;
import org.openhab.binding.verisure.handler.VerisureThingHandler;
import org.openhab.binding.verisure.internal.VerisureAlarmJSON;
import org.openhab.binding.verisure.internal.VerisureBroadbandConnectionJSON;
import org.openhab.binding.verisure.internal.VerisureClimateBaseJSON;
import org.openhab.binding.verisure.internal.VerisureDoorWindowsJSON;
import org.openhab.binding.verisure.internal.VerisureSmartPlugJSON;
import org.openhab.binding.verisure.internal.VerisureThingJSON;
import org.openhab.binding.verisure.internal.VerisureUserPresenceJSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * The discovery service, notified by a listener on the VerisureSession.
 *
 * @author jarle hjortland
 *
 */
public class VerisureThingDiscoveryService extends AbstractDiscoveryService {
    private final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Sets
            .union(VerisureBridgeHandler.SUPPORTED_THING_TYPES, VerisureThingHandler.SUPPORTED_THING_TYPES);

    private Logger logger = LoggerFactory.getLogger(VerisureThingDiscoveryService.class);

    private final static int SEARCH_TIME = 60;

    private VerisureBridgeHandler verisureBridgeHandler;

    public VerisureThingDiscoveryService(VerisureBridgeHandler bridgeHandler) throws IllegalArgumentException {
        // super(SEARCH_TIME);
        super(SUPPORTED_THING_TYPES, SEARCH_TIME);

        this.verisureBridgeHandler = bridgeHandler;

    }

    @Override
    public void startScan() {
        removeOlderResults(getTimestampOfLastScan());
        logger.debug("VerisureThingDiscoveryService:startScan");

        HashMap<String, VerisureThingJSON> verisureThings = verisureBridgeHandler.getSession().getVerisureThings();

        for (Map.Entry<String, VerisureThingJSON> entry : verisureThings.entrySet()) {
            logger.info(entry.getValue().toString());
            onThingAddedInternal(entry.getValue());
        }
    }

    private void onThingAddedInternal(VerisureThingJSON value) {
        logger.debug("VerisureThingDiscoveryService:OnThingAddedInternal");
        ThingUID thingUID = getThingUID(value);
        if (thingUID != null) {
            ThingUID bridgeUID = verisureBridgeHandler.getThing().getUID();
            DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withBridge(bridgeUID)
                    .withLabel(value.getId()).build();

            logger.debug("thinguid: {}, bridge {}, label {}", thingUID.toString(), bridgeUID, value.getId());
            thingDiscovered(discoveryResult);
        } else {
            logger.debug("discovered unsupported thing of type '{}' with id {}", value.getId(), value.getId());
        }

    }

    public void activate() {
    }

    private ThingUID getThingUID(VerisureThingJSON voj) {
        ThingUID bridgeUID = verisureBridgeHandler.getThing().getUID();

        ThingUID tuid = null;
        if (voj instanceof VerisureAlarmJSON) {
            if (((VerisureAlarmJSON) voj).getType().equals("ARM_STATE")) {
                tuid = new ThingUID(THING_TYPE_ALARM, bridgeUID, voj.getId().replaceAll("[^a-zA-Z0-9_]", "_"));
            } else if (((VerisureAlarmJSON) voj).getType().equals("DOOR_LOCK")) {
                tuid = new ThingUID(THING_TYPE_SMARTLOCK, bridgeUID, voj.getId().replaceAll("[^a-zA-Z0-9_]", "_"));
            } else {
                logger.error("Unknown alarm/lock device:" + ((VerisureAlarmJSON) voj).getType());
            }
        } else if (voj instanceof VerisureUserPresenceJSON) {
            tuid = new ThingUID(THING_TYPE_USERPRESENCE, bridgeUID, voj.getId().replaceAll("[^a-zA-Z0-9_]", "_"));
        } else if (voj instanceof VerisureDoorWindowsJSON) {
            tuid = new ThingUID(THING_TYPE_DOORWINDOW, bridgeUID, voj.getId().replaceAll("[^a-zA-Z0-9_]", "_"));
        } else if (voj instanceof VerisureSmartPlugJSON) {
            tuid = new ThingUID(THING_TYPE_SMARTPLUG, bridgeUID, voj.getId().replaceAll("[^a-zA-Z0-9_]", "_"));
        } else if (voj instanceof VerisureClimateBaseJSON) {
            if (((VerisureClimateBaseJSON) voj).getType().equalsIgnoreCase("Smoke detector")) {
                tuid = new ThingUID(THING_TYPE_SMOKEDETECTOR, bridgeUID, voj.getId().replaceAll("[^a-zA-Z0-9_]", "_"));
            } else if (((VerisureClimateBaseJSON) voj).getType().equalsIgnoreCase("Water detector")) {
                tuid = new ThingUID(THING_TYPE_WATERDETETOR, bridgeUID, voj.getId().replaceAll("[^a-zA-Z0-9_]", "_"));
            } else if (((VerisureClimateBaseJSON) voj).getType().equalsIgnoreCase("Siren")) {
                tuid = new ThingUID(THING_TYPE_SIREN, bridgeUID, voj.getId().replaceAll("[^a-zA-Z0-9_]", "_"));
            } else {
                logger.error("Unknown climate device:" + ((VerisureClimateBaseJSON) voj).getType());
            }
        } else if (voj instanceof VerisureBroadbandConnectionJSON) {
            tuid = new ThingUID(THING_TYPE_BROADBAND_CONNECTION, bridgeUID,
                    voj.getId().replaceAll("[^a-zA-Z0-9_]", "_"));
        } else {
            logger.error("Unsupported JSON!");
        }

        return tuid;
    }

}
