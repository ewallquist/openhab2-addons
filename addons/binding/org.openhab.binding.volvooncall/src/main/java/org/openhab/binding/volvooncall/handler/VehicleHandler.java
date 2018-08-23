/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.volvooncall.handler;

import static org.eclipse.smarthome.core.library.unit.MetricPrefix.KILO;
import static org.openhab.binding.volvooncall.VolvoOnCallBindingConstants.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.measure.quantity.Length;
import javax.measure.quantity.Volume;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.unit.SIUnits;
import org.eclipse.smarthome.core.library.unit.SmartHomeUnits;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.builder.ChannelBuilder;
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.volvooncall.internal.config.VehicleConfiguration;
import org.openhab.binding.volvooncall.internal.json.Vehicle;
import org.openhab.binding.volvooncall.internal.json.VehiclePosition;
import org.openhab.binding.volvooncall.internal.json.VehicleStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link VehicleHandler} is responsible for handling commands, which are sent
 * to one of the channels.
 *
 * @author Gaël L'hopital - Initial contribution
 */
@NonNullByDefault
public class VehicleHandler extends BaseThingHandler {
    private final Logger logger = LoggerFactory.getLogger(VehicleHandler.class);

    @Nullable
    private ScheduledFuture<?> refreshJob;

    @Nullable
    VehicleConfiguration configuration;

    @Nullable
    VolvoOnCallBridgeHandler bridgeHandler;

    @Nullable
    Vehicle vehicle;

    @Nullable
    VehiclePosition vehiclePosition;

    public VehicleHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.trace("Initializing the Volvo On Call handler for {}", getThing().getUID());
        updateStatus(ThingStatus.UNKNOWN);

        configuration = getConfigAs(VehicleConfiguration.class);
        bridgeHandler = (VolvoOnCallBridgeHandler) getBridge().getHandler();

        try {
            if (bridgeHandler != null) {
                vehicle = bridgeHandler.getURL(SERVICE_URL + Vehicle.ENDPOINT + configuration.vin, Vehicle.class);
                addOptionalChannels();
                updateStatus(ThingStatus.ONLINE);
                startAutomaticRefresh();
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_UNINITIALIZED);
            }
        } catch (IOException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.HANDLER_INITIALIZING_ERROR, e.getMessage());
        }

    }

    private void addOptionalChannels() {
        Map<String, String> properties = getThing().getProperties();
        ThingBuilder thingBuilder = editThing();

        addIfAvailable(ENGINE_START, properties, thingBuilder);
        addIfAvailable(UNLOCK, properties, thingBuilder);
        addIfAvailable(LOCK, properties, thingBuilder);
        addIfAvailable(HONK, properties, thingBuilder);
        addIfAvailable(BLINK, properties, thingBuilder);
        addIfAvailable(HONK_BLINK, properties, thingBuilder);
        addIfAvailable(REMOTE_HEATER, properties, thingBuilder);
        addIfAvailable(PRECLIMATIZATION, properties, thingBuilder);

        updateThing(thingBuilder.build());
    }

    private void addIfAvailable(String option, Map<String, String> properties, ThingBuilder thingBuilder) {
        if ("true".equals(properties.get(option))) {
            ChannelTypeUID channelUID = new ChannelTypeUID(BINDING_ID, option);
            if (getThing().getChannel(channelUID.getId()) == null) {

                Channel channel = ChannelBuilder.create(new ChannelUID(getThing().getUID(), option), "Switch")
                        .withType(channelUID).build();
                thingBuilder.withChannel(channel);
            }
        }
    }

    /**
     * Start the job refreshing the vehicle data
     */
    @SuppressWarnings("null")
    private void startAutomaticRefresh() {
        if (refreshJob == null || refreshJob.isCancelled()) {
            Runnable runnable = () -> {
                try {
                    VehicleStatus vehicleStatus = bridgeHandler.getURL(vehicle.getStatusURL(), VehicleStatus.class);
                    vehiclePosition = bridgeHandler.getURL(
                            SERVICE_URL + Vehicle.ENDPOINT + configuration.vin + VehiclePosition.ENDPOINT,
                            VehiclePosition.class);

                    // Update all channels from the updated data
                    for (Channel channel : getThing().getChannels()) {

                        if (isLinked(channel.getUID())) {
                            State state = getValue(channel.getUID().getIdWithoutGroup(), vehicleStatus);
                            updateState(channel.getUID(), state);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Exception occurred during execution: {}", e.getMessage(), e);
                }
            };

            refreshJob = scheduler.scheduleWithFixedDelay(runnable, 0, configuration.refresh.intValue(),
                    TimeUnit.MINUTES);
        }
    }

    @Override
    public void dispose() {
        logger.trace("Disposing the Volvo On Call handler for {}", getThing().getUID());
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        String channelID = channelUID.getId();

        if (command instanceof RefreshType) {
            // if (isAwake()) {
            // Request the state of all known variables. This is sub-optimal, but the requests get scheduled and
            // throttled so we are safe not to break the Tesla SLA
            // requestData(DRIVE_STATE);
            // requestData(VEHICLE_STATE);
            // requestData(CHARGE_STATE);
            // requestData(CLIMATE_STATE);
            // requestData(GUI_STATE);
            // }
        }
    }

    public State getValue(String channelId, VehicleStatus status) throws Exception {
        switch (channelId) {
            case TAILGATE:
                return status.getDoors().getTailgateOpen();
            case REAR_RIGHT:
                return status.getDoors().getRearRightDoorOpen();
            case REAR_LEFT:
                return status.getDoors().getRearLeftDoorOpen();
            case FRONT_RIGHT:
                return status.getDoors().getFrontRightDoorOpen();
            case FRONT_LEFT:
                return status.getDoors().getFrontLeftDoorOpen();
            case HOOD:
                return status.getDoors().getHoodOpen();
            case REAR_RIGHT_WND:
                return status.getWindows().getRearRightWindowOpen();
            case REAR_LEFT_WND:
                return status.getWindows().getRearLeftWindowOpen();
            case FRONT_RIGHT_WND:
                return status.getWindows().getFrontRightWindowOpen();
            case FRONT_LEFT_WND:
                return status.getWindows().getFrontLeftWindowOpen();
            case ODOMETER:
                return new QuantityType<Length>(status.getOdometer(), SIUnits.METRE);
            case TRIPMETER1:
                return new QuantityType<Length>(status.getTripMeter1(), SIUnits.METRE);
            case TRIPMETER2:
                return new QuantityType<Length>(status.getTripMeter2(), SIUnits.METRE);
            case DISTANCE_TO_EMPTY:
                return new QuantityType<Length>(status.getDistanceToEmpty(), KILO(SIUnits.METRE));
            case FUEL_AMOUNT:
                return new QuantityType<Volume>(status.getFuelAmount(), SmartHomeUnits.LITRE);
            case FUEL_LEVEL:
                return new QuantityType<>(status.getFuelAmountLevel(), SmartHomeUnits.PERCENT);
            case FUEL_CONSUMPTION:
                return new DecimalType(status.getAverageFuelConsumption() / 10);
            case ACTUAL_LOCATION:
                return vehiclePosition.getPosition();
            case CALCULATED_LOCATION:
                return vehiclePosition.getCalculatedPosition();
            case HEADING:
                return vehiclePosition.getHeading();
            case LOCATION_TIMESTAMP:
                return vehiclePosition.getLocationTimestamp();
            case CAR_LOCKED:
                return status.getCarLocked();
            case ENGINE_RUNNING:
                return status.getEngineRunning();
        }

        return UnDefType.NULL;
    }

}
