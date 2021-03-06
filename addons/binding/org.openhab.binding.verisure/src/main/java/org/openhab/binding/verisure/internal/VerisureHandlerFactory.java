/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.verisure.internal;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.eclipse.smarthome.io.net.http.HttpClientFactory;
import org.openhab.binding.verisure.handler.VerisureBridgeHandler;
import org.openhab.binding.verisure.handler.VerisureThingHandler;
import org.openhab.binding.verisure.internal.discovery.VerisureThingDiscoveryService;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * The {@link VerisureHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Jarle Hjortland - Initial contribution
 */
@NonNullByDefault
@Component(service = ThingHandlerFactory.class, configurationPid = "binding.verisure")
public class VerisureHandlerFactory extends BaseThingHandlerFactory {

    private final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Sets
            .union(VerisureBridgeHandler.SUPPORTED_THING_TYPES, VerisureThingHandler.SUPPORTED_THING_TYPES);

    private Map<ThingUID, ServiceRegistration<?>> discoveryServiceRegs = new HashMap<>();
    private @NonNullByDefault({}) HttpClient httpClient;
    private Logger logger = LoggerFactory.getLogger(VerisureHandlerFactory.class);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES.contains(thingTypeUID);
    }

    @Override
    @Nullable
    protected ThingHandler createHandler(Thing thing) {
        logger.debug("createHandler this: {}", thing);
        ThingHandler thingHandler = null;
        if (VerisureBridgeHandler.SUPPORTED_THING_TYPES.contains(thing.getThingTypeUID())) {
            logger.debug("Create VerisureBridgeHandler");
            thingHandler = new VerisureBridgeHandler((Bridge) thing, httpClient);
            registerObjectDiscoveryService((VerisureBridgeHandler) thingHandler);
        } else if (VerisureThingHandler.SUPPORTED_THING_TYPES.contains(thing.getThingTypeUID())) {
            logger.debug("Create VerisureThingHandler {}", thing.getThingTypeUID());
            thingHandler = new VerisureThingHandler(thing);
        }
        return thingHandler;
    }

    private synchronized void registerObjectDiscoveryService(VerisureBridgeHandler bridgeHandler) {
        VerisureThingDiscoveryService discoveryService = new VerisureThingDiscoveryService(bridgeHandler);
        discoveryService.activate();
        this.discoveryServiceRegs.put(bridgeHandler.getThing().getUID(), bundleContext
                .registerService(DiscoveryService.class.getName(), discoveryService, new Hashtable<String, Object>()));
    }

    @Reference
    protected void setHttpClientFactory(HttpClientFactory httpClientFactory) {
        logger.debug("setHttpClientFactory this: {}", this);
        this.httpClient = httpClientFactory.getCommonHttpClient();
    }

    protected void unsetHttpClientFactory(HttpClientFactory httpClientFactory) {
        logger.debug("unsetHttpClientFactory this: {}", this);
        this.httpClient = null;
    }

}
