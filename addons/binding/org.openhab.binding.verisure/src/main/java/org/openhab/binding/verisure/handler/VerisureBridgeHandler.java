/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.verisure.handler;

import static org.openhab.binding.verisure.VerisureBindingConstants.THING_TYPE_BRIDGE;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.verisure.internal.DeviceStatusListener;
import org.openhab.binding.verisure.internal.VerisureBridgeConfiguration;
import org.openhab.binding.verisure.internal.VerisureSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link VerisureBridgeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author l3rum - Initial contribution
 */
@NonNullByDefault
public class VerisureBridgeHandler extends BaseBridgeHandler {

    private static final int REFRESH_DELAY = 10;

    @Override
    protected void updateThing(Thing thing) {
        super.updateThing(thing);
    }

    @Override
    protected void updateConfiguration(Configuration configuration) {
        stopAutomaticRefresh();
        stopImmediateRefresh();
        super.updateConfiguration(configuration);
        initialize();
    }

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Collections.singleton(THING_TYPE_BRIDGE);

    private Logger logger = LoggerFactory.getLogger(VerisureBridgeHandler.class);

    private BigDecimal refresh = new BigDecimal(600);
    private @Nullable String authstring;
    private @Nullable BigDecimal pinCode;
    private @Nullable BigDecimal numberOfInstallations;
    private @Nullable ScheduledFuture<?> refreshJob;
    private @Nullable ScheduledFuture<?> immediateRefreshJob;
    ReentrantLock immediateRefreshJobLock = new ReentrantLock();

    private @Nullable VerisureSession session;
    private @Nullable HttpClient httpClient;

    Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            logger.debug("VerisureBridgeHandler - Polling thread is up'n running!");
            try {
                if (session != null) {
                    boolean success = session.refresh();
                    if (success) {
                        updateStatus(ThingStatus.ONLINE);
                    } else {
                        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
                    }
                }
            } catch (Exception e) {
                logger.debug("Exception occurred during execution: {}", e.getMessage(), e);
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
            }
        }
    };

    public VerisureBridgeHandler(Bridge bridge, HttpClient httpClient) {
        super(bridge);
        this.httpClient = httpClient;
        session = new VerisureSession(this.httpClient);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("Handle command {} ", command);
        if (command instanceof RefreshType) {
            scheduleImmediateRefresh();
        } else {
            logger.warn("unknown command! {}", command);
        }
    }

    public @Nullable VerisureSession getSession() {
        return session;
    }

    @Override
    public void initialize() {
        logger.debug("Initializing Verisure Binding");
        VerisureBridgeConfiguration config = getConfigAs(VerisureBridgeConfiguration.class);

        this.refresh = config.refresh;
        this.pinCode = config.pin;
        this.numberOfInstallations = config.numberOfInstallations;

        if (config.username == null || config.password == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Must configure username and password");
        } else {
            authstring = "j_username=" + config.username + "&j_password=" + config.password;
            try {
                if (session != null) {
                    session.initialize(authstring, pinCode, numberOfInstallations);
                    startAutomaticRefresh();
                }
            } catch (Error e) {
                logger.error("Failed", e);
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
            } catch (Exception e) {
                logger.error("Failed", e);
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
            }
        }
    }

    void scheduleImmediateRefresh() {
        this.immediateRefreshJobLock.lock();
        try {
            // We schedule in 10 sec, to avoid multiple updates
            if (refreshJob != null) {
                logger.trace("Current remaining delay {}", refreshJob.getDelay(TimeUnit.SECONDS));
                if (refreshJob != null && refreshJob.getDelay(TimeUnit.SECONDS) > REFRESH_DELAY) {
                    if (immediateRefreshJob == null || ((immediateRefreshJob != null)
                            && (immediateRefreshJob.getDelay(TimeUnit.SECONDS) <= 0))) {
                        if (immediateRefreshJob != null) {
                            logger.trace("Current immediateRefreshJob delay {}",
                                    immediateRefreshJob.getDelay(TimeUnit.SECONDS));
                        }
                        // Note we are using getDelay() instead of isDone() as we want to allow Things to schedule a
                        // refresh if their status is pending. As the status update happens inside the pollingRunnable
                        // execution the isDone() will return false and would not allow the rescheduling of the task.
                        immediateRefreshJob = scheduler.schedule(pollingRunnable, REFRESH_DELAY, TimeUnit.SECONDS);
                    }
                }
            }
        } finally {
            this.immediateRefreshJobLock.unlock();
        }
    }

    private void startAutomaticRefresh() {
        if (refreshJob == null || (refreshJob != null && refreshJob.isCancelled())) {
            logger.debug("Scheduling at fixed delay");
            refreshJob = scheduler.scheduleWithFixedDelay(pollingRunnable, REFRESH_DELAY, refresh.intValue(),
                    TimeUnit.SECONDS);
        }
    }

    private void stopImmediateRefresh() {
        if (immediateRefreshJob == null || (immediateRefreshJob != null && immediateRefreshJob.isCancelled())) {
            if (immediateRefreshJob != null) {
                immediateRefreshJob.cancel(true);
            }
            immediateRefreshJob = null;
        }
    }

    private void stopAutomaticRefresh() {
        if (refreshJob == null || (refreshJob != null && refreshJob.isCancelled())) {
            if (refreshJob != null) {
                refreshJob.cancel(true);
            }
            refreshJob = null;
        }
    }

    @Override
    public void dispose() {
        logger.debug("Handler disposed.");
        stopAutomaticRefresh();
        stopImmediateRefresh();
        if (session != null) {
            session.dispose();
        }
    }

    public boolean registerObjectStatusListener(DeviceStatusListener deviceStatusListener) {
        if (session != null) {
            return session.registerDeviceStatusListener(deviceStatusListener);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.smarthome.core.thing.binding.BaseThingHandler#handleRemoval()
     */
    @Override
    public void handleRemoval() {
        stopAutomaticRefresh();
        stopImmediateRefresh();
    }

}
