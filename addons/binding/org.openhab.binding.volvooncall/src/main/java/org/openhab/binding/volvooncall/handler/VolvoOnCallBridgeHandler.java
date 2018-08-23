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
package org.openhab.binding.volvooncall.handler;

import static org.openhab.binding.volvooncall.VolvoOnCallBindingConstants.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.OpenClosedType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.io.net.http.HttpUtil;
import org.openhab.binding.volvooncall.internal.config.VolvoOnCallBridgeConfiguration;
import org.openhab.binding.volvooncall.internal.json.CustomerAccount;
import org.openhab.binding.volvooncall.internal.json.VehicleAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

/**
 * The {@link VolvoOnCallBridgeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Gaël L'hopital - Initial contribution
 */
@NonNullByDefault
public class VolvoOnCallBridgeHandler extends BaseBridgeHandler {
    private final Logger logger = LoggerFactory.getLogger(VolvoOnCallBridgeHandler.class);
    private final static int REQUEST_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(30);
    private final Properties httpHeader;

    private Gson gson;

    @Nullable
    private VolvoOnCallBridgeConfiguration configuration;

    @Nullable
    private CustomerAccount customerAccount;

    public VolvoOnCallBridgeHandler(Bridge bridge) {
        super(bridge);

        httpHeader = createHttpHeader();

        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(ZonedDateTime.class,
                (JsonDeserializer<ZonedDateTime>) (json, type, jsonDeserializationContext) -> ZonedDateTime
                        .parse(json.getAsJsonPrimitive().getAsString().replaceAll("\\+0000", "Z")));

        gsonBuilder.registerTypeAdapter(OpenClosedType.class, (JsonDeserializer<OpenClosedType>) (json, type,
                jsonDeserializationContext) -> json.getAsBoolean() ? OpenClosedType.OPEN : OpenClosedType.CLOSED);

        gsonBuilder.registerTypeAdapter(OnOffType.class, (JsonDeserializer<OnOffType>) (json, type,
                jsonDeserializationContext) -> json.getAsBoolean() ? OnOffType.ON : OnOffType.OFF);

        gson = gsonBuilder.create();
    }

    @Override
    public void initialize() {
        logger.debug("Initializing VolvoOnCall API bridge handler.");
        configuration = getConfigAs(VolvoOnCallBridgeConfiguration.class);

        @SuppressWarnings("null")
        byte[] authorization = Base64.getEncoder()
                .encode((configuration.username + ":" + configuration.password).getBytes());

        try {
            httpHeader.setProperty("Authorization", "Basic " + new String(authorization, "UTF-8"));

            try {
                customerAccount = getURL(SERVICE_URL + CustomerAccount.ENDPOINT, CustomerAccount.class);
                updateStatus(ThingStatus.ONLINE);
            } catch (IOException e) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getMessage());
            }
        } catch (UnsupportedEncodingException e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getMessage());
        }

    }

    private Properties createHttpHeader() {
        Properties headers = new Properties();
        headers.put("X-Device-Id", "Device");
        headers.put("X-OS-Type", "Android");
        headers.put("X-Originator-Type", "App");
        headers.put("X-OS-Version", "22");
        headers.put("Content-Type", JSON_CONTENT_TYPE);

        return headers;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("VolvoOnCall Bridge is read-only and does not handle commands");
    }

    @SuppressWarnings("null")
    public String[] getVehiclesRelationsURL() {
        if (customerAccount != null && customerAccount.getAccountVehicleRelationsURL() != null) {
            return customerAccount.getAccountVehicleRelationsURL();
        } else {
            return new String[0];
        }
    }

    public <T> T getURL(String URL, Class<T> objectClass) throws IOException {
        String jsonResponse = HttpUtil.executeUrl("GET", URL, httpHeader, null, JSON_CONTENT_TYPE, REQUEST_TIMEOUT);
        if (objectClass == VehicleAttributes.class) {
            String answer = HttpUtil.executeUrl("GET", URL, httpHeader, null, JSON_CONTENT_TYPE, REQUEST_TIMEOUT);
            logger.debug(answer);
        }
        return gson.fromJson(jsonResponse, objectClass);
    }
}
