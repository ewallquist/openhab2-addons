/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.volvooncall.internal.discovery;

import static org.openhab.binding.volvooncall.VolvoOnCallBindingConstants.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.volvooncall.handler.VolvoOnCallBridgeHandler;
import org.openhab.binding.volvooncall.internal.json.AccountVehicleRelation;
import org.openhab.binding.volvooncall.internal.json.Vehicle;
import org.openhab.binding.volvooncall.internal.json.VehicleAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link VolvoOnCallDiscoveryService} searches for available
 * cars discoverable through VocAPI
 *
 * @author Gaël L'hopital - Initial contribution
 *
 */
@NonNullByDefault
public class VolvoOnCallDiscoveryService extends AbstractDiscoveryService {
    private static final int SEARCH_TIME = 2;
    private final Logger logger = LoggerFactory.getLogger(VolvoOnCallBridgeHandler.class);
    private final VolvoOnCallBridgeHandler bridgeHandler;

    public VolvoOnCallDiscoveryService(VolvoOnCallBridgeHandler bridgeHandler) {
        super(SUPPORTED_THING_TYPES_UIDS, SEARCH_TIME);
        this.bridgeHandler = bridgeHandler;
    }

    @Override
    public void startScan() {
        String[] relations = bridgeHandler.getVehiclesRelationsURL();

        for (String relationURL : relations) {
            try {
                AccountVehicleRelation accountVehicle = bridgeHandler.getURL(relationURL, AccountVehicleRelation.class);
                logger.debug("Found vehicle : {}", accountVehicle.getVehicleId());

                Vehicle vehicle = bridgeHandler.getURL(accountVehicle.getVehicleURL(), Vehicle.class);
                VehicleAttributes attributes = bridgeHandler.getURL(vehicle.getAttributesURL(),
                        VehicleAttributes.class);

                ThingUID vehicleUID = new ThingUID(VEHICLE_THING_TYPE, accountVehicle.getVehicleId());
                Map<String, Object> properties = new HashMap<>();
                properties.put(VIN, attributes.getVin());
                properties.put(CAR_LOCATOR, attributes.getCarLocatorSupported());
                properties.put(HONK, attributes.getHonkAndBlinkSupported()
                        && attributes.getHonkAndBlinkVersionsSupported().contains("honkAndOrBlink"));
                properties.put(BLINK, attributes.getHonkAndBlinkSupported()
                        && attributes.getHonkAndBlinkVersionsSupported().contains("honkAndOrBlink"));
                properties.put(HONK_BLINK, attributes.getHonkAndBlinkSupported()
                        && attributes.getHonkAndBlinkVersionsSupported().contains(HONK_BLINK));
                properties.put(REMOTE_HEATER, attributes.getRemoteHeaterSupported());
                properties.put(UNLOCK, attributes.getUnlockSupported());
                properties.put(LOCK, attributes.getLockSupported());
                properties.put(JOURNAL_LOG, attributes.getJournalLogSupported() && attributes.getJournalLogEnabled());
                properties.put(PRECLIMATIZATION, attributes.getPreclimatizationSupported());
                properties.put(ENGINE_START, attributes.getEngineStartSupported());

                thingDiscovered(DiscoveryResultBuilder.create(vehicleUID)
                        .withLabel(attributes.getVehicleType() + " " + attributes.getRegistrationNumber())
                        .withProperties(properties).withBridge(bridgeHandler.getThing().getUID())
                        .withRepresentationProperty(accountVehicle.getVehicleId()).build());
            } catch (IOException e) {
                logger.error("Error while discovering vehicle", e.getMessage());
            }

        }
        stopScan();
    }

}
