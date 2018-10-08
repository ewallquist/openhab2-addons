/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.verisure.internal;

import static org.openhab.binding.verisure.VerisureBindingConstants.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openhab.binding.verisure.internal.VerisureSmartLockJSON.DoorLockVolumeSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This class performs the communication with Verisure My Pages.
 *
 * @author Jarle Hjortland - Initial contribution
 *
 */
@NonNullByDefault
public class VerisureSession {

    private HashMap<String, @Nullable VerisureThingJSON> verisureThings = new HashMap<String, @Nullable VerisureThingJSON>();

    private Logger logger = LoggerFactory.getLogger(VerisureSession.class);
    private @Nullable String authstring;
    private @Nullable String csrf;
    private @Nullable BigDecimal pinCode;
    private @Nullable BigDecimal numberOfInstallations;
    private Gson gson = new GsonBuilder().create();

    private List<DeviceStatusListener> deviceStatusListeners = new CopyOnWriteArrayList<>();
    private HttpClient httpClient;
    private Boolean areWeLoggedOut = new Boolean(true);

    private class VerisureInstallation {
        private @Nullable String installationName;
        private @Nullable BigDecimal installationInstance;
        private @Nullable BigDecimal installationId;

        public VerisureInstallation(@Nullable String installationName) {
            this.installationName = installationName;
        }

        public @Nullable String getInstallationName() {
            return installationName;
        }

        public @Nullable BigDecimal getInstallationId() {
            return installationId;
        }

        public void setInstallationId(BigDecimal installationId) {
            this.installationId = installationId;
        }

        public @Nullable BigDecimal getInstallationInstance() {
            return installationInstance;
        }

        public void setInstallationInstance(BigDecimal installationInstance) {
            this.installationInstance = installationInstance;
        }

    }

    private Hashtable<String, @Nullable VerisureInstallation> verisureInstallations = new Hashtable<String, @Nullable VerisureInstallation>();;

    public VerisureSession(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void initialize(@Nullable String authstring, @Nullable BigDecimal pinCode,
            @Nullable BigDecimal numberOfInstallations) throws Exception {
        logger.debug("VerisureSession:initialize");

        if (authstring != null) {
            this.authstring = authstring.substring(0);
            this.pinCode = pinCode;
            this.numberOfInstallations = numberOfInstallations;
            // Try to login to Verisure
            if (logIn()) {
                getInstallations();
                // updateStatus(); Do it in polling thread instead
            } else {
                logger.error("Failed to login to Verisure!");
            }
        }
    }

    private void getInstallations() {
        logger.debug("VerisureSession:handleInstallations");
        if (numberOfInstallations != null) {
            int ni = numberOfInstallations.intValue();
            try {
                for (int i = 1; i < ni + 1; i++) {
                    BigDecimal inst = new BigDecimal(i);
                    String html = configureInstallationInstance(inst);
                    handleInstallation(html, inst);
                }
            } catch (Exception e) {
                logger.error("Failed in handleInstallations", e);
            }
        }
    }

    private void handleInstallation(String htmlText, BigDecimal installationInstance) {
        Document htmlDocument = Jsoup.parse(htmlText);
        Element div = htmlDocument.select("span.global-navigation-item-no-shrink--text").first();
        String alarmInstallationName = div.text();

        VerisureInstallation verisureInstallation = verisureInstallations.get(alarmInstallationName);
        if (verisureInstallation == null) {
            verisureInstallation = new VerisureInstallation(alarmInstallationName);
            verisureInstallation.setInstallationInstance(installationInstance);
            verisureInstallation.setInstallationId(installationInstance);
            verisureInstallations.put(alarmInstallationName, verisureInstallation);
        } else {
            verisureInstallation.setInstallationInstance(installationInstance);
        }

        Element inst = htmlDocument.select("a.installation-select-link").first();
        if (inst != null) {
            String alarmInstallationName2 = inst.text();

            Element instId = htmlDocument.select("input.giid").first();
            String installationId = instId.attr("value");

            verisureInstallation = verisureInstallations.get(alarmInstallationName2);
            if (verisureInstallation != null) {
                if (verisureInstallations.get(alarmInstallationName2) == null) {
                    verisureInstallation = new VerisureInstallation(alarmInstallationName2);
                    verisureInstallation.setInstallationId(new BigDecimal(installationId));
                    verisureInstallations.put(alarmInstallationName2, verisureInstallation);
                } else {
                    verisureInstallation.setInstallationId(new BigDecimal(installationId));
                }
            }
        }
    }

    private void updateStatus() {
        logger.debug("VerisureSession:updateStatus");
        if (numberOfInstallations != null) {
            int ni = numberOfInstallations.intValue();
            for (int i = 1; i < ni + 1; i++) {
                try {
                    BigDecimal inst = new BigDecimal(i);
                    configureInstallationInstance(inst);
                    VerisureInstallation vInst = null;
                    for (Enumeration<@Nullable VerisureInstallation> num = verisureInstallations.elements(); num
                            .hasMoreElements();) {
                        vInst = num.nextElement();
                        if (vInst != null) {
                            BigDecimal insInst = vInst.getInstallationInstance();
                            if (insInst != null && insInst.equals(inst)) {
                                break;
                            }
                        }
                    }
                    if (vInst != null) {
                        updateVerisureThings(ALARMSTATUS_PATH, VerisureAlarmJSON[].class, vInst);
                        updateVerisureThings(CLIMATEDEVICE_PATH, VerisureClimateBaseJSON[].class, vInst);
                        updateVerisureThings(DOORWINDOW_PATH, VerisureDoorWindowsJSON[].class, vInst);
                        updateVerisureThings(USERTRACKING_PATH, VerisureUserPresenceJSON[].class, vInst);
                        updateVerisureThings(SMARTPLUG_PATH, VerisureSmartPlugJSON[].class, vInst);
                        updateVerisureBroadbandStatus(ETHERNETSTATUS_PATH, VerisureBroadbandConnectionJSON.class,
                                vInst);
                    }
                } catch (Exception e) {
                    logger.error("Failed in updatestatus", e);
                }
            }
        }
    }

    public synchronized void updateVerisureBroadbandStatus(String urlString,
            Class<? extends VerisureThingJSON> jsonClass, VerisureInstallation inst) {

        try {
            VerisureThingJSON thing = callJSONRest(urlString, jsonClass);
            logger.debug("REST Response ({})", thing);
            if (thing != null) {
                BigDecimal instInst = inst.getInstallationInstance();
                if (instInst != null) {
                    thing.setId(instInst.toString());
                    VerisureThingJSON oldObj = verisureThings.get(thing.getId());
                    if (oldObj == null || !oldObj.equals(thing)) {
                        thing.setSiteId(inst.getInstallationId());
                        thing.setSiteName(inst.getInstallationName());
                        String id = thing.getId();
                        if (id != null) {
                            verisureThings.put(id, thing);
                            notifyListeners(thing);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.info("Failed to get all {}", urlString, e);
        }
    }

    public synchronized void updateVerisureThings(String urlString, Class<? extends VerisureThingJSON[]> jsonClass,
            @Nullable VerisureInstallation inst) {
        if (inst != null) {
            try {
                VerisureThingJSON[] things = callJSONRest(urlString, jsonClass);
                logger.debug("REST Response ({})", (Object[]) things);
                if (things != null) {
                    for (VerisureThingJSON thing : things) {
                        BigDecimal instInst = inst.getInstallationInstance();
                        if (instInst != null) {
                            if (thing instanceof VerisureUserPresenceJSON) {
                                thing.setId(instInst.toString());
                            } else if (thing instanceof VerisureAlarmJSON) {
                                String type = ((VerisureAlarmJSON) thing).getType();
                                if (type != null && type.equals("ARM_STATE")) {
                                    thing.setId(instInst.toString());
                                } else {
                                    VerisureThingJSON smartLockThing = callJSONRest(SMARTLOCK_PATH + thing.getId(),
                                            VerisureSmartLockJSON.class);
                                    if (smartLockThing != null) {
                                        String date = ((VerisureAlarmJSON) thing).getDate();
                                        if (date != null) {
                                            ((VerisureSmartLockJSON) smartLockThing).setDate(date);
                                        }
                                        String notAllowedReason = ((VerisureAlarmJSON) thing).getNotAllowedReason();
                                        if (notAllowedReason != null) {
                                            ((VerisureSmartLockJSON) smartLockThing)
                                                    .setNotAllowedReason(notAllowedReason);
                                        }
                                        Boolean changeAllowed = ((VerisureAlarmJSON) thing).getChangeAllowed();
                                        if (changeAllowed != null) {
                                            ((VerisureSmartLockJSON) smartLockThing).setChangeAllowed(changeAllowed);
                                        }
                                        String label = ((VerisureAlarmJSON) thing).getLabel();
                                        if (label != null) {
                                            ((VerisureSmartLockJSON) smartLockThing).setLabel(label);
                                        }
                                        if (type != null) {
                                            ((VerisureSmartLockJSON) smartLockThing).setType(type);
                                        }
                                        String name = ((VerisureAlarmJSON) thing).getName();
                                        if (name != null) {
                                            ((VerisureSmartLockJSON) smartLockThing).setName(name);
                                        }
                                        String location = ((VerisureAlarmJSON) thing).getLocation();
                                        if (location != null) {
                                            ((VerisureSmartLockJSON) smartLockThing).setLocation(location);
                                        }
                                        String status = ((VerisureAlarmJSON) thing).getStatus();
                                        if (status != null) {
                                            ((VerisureSmartLockJSON) smartLockThing).setStatus(status);
                                        }
                                        String id = smartLockThing.getId();
                                        if (id != null) {
                                            smartLockThing.setId(id.replaceAll("[^a-zA-Z0-9_]", "_"));
                                        }
                                        thing = smartLockThing;
                                    }
                                }
                            } else {
                                String id = thing.getId();
                                if (id != null) {
                                    thing.setId(id.replaceAll("[^a-zA-Z0-9_]", "_"));
                                }
                            }
                            VerisureThingJSON oldObj = verisureThings.get(thing.getId());
                            if (oldObj == null || !oldObj.equals(thing)) {
                                thing.setSiteId(inst.getInstallationId());
                                thing.setSiteName(inst.getInstallationName());
                                String id = thing.getId();
                                if (id != null) {
                                    verisureThings.put(id, thing);
                                    notifyListeners(thing);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.info("Failed to get all {}", urlString, e);
            }
        }
    }

    private void notifyListeners(VerisureThingJSON cse) {
        for (DeviceStatusListener listener : deviceStatusListeners) {
            listener.onDeviceStateChanged(cse);
        }
    }

    public @Nullable VerisureThingJSON getVerisureThing(String key) {
        return verisureThings.get(key);
    }

    public HashMap<String, @Nullable VerisureThingJSON> getVerisureThings() {
        return verisureThings;
    }

    private @Nullable <T> T callJSONRest(String url, Class<T> jsonClass) throws Exception {
        T result = null;
        logger.debug("HTTP GET: " + BASEURL + url);
        ContentResponse httpResult = httpClient.GET(BASEURL + url + "?_=" + System.currentTimeMillis());
        logger.debug("HTTP Response ({}) Body:{}", httpResult.getStatus(),
                httpResult.getContentAsString().replaceAll("\n+", "\n"));
        if (httpResult.getStatus() == HttpStatus.OK_200) {
            result = gson.fromJson(httpResult.getContentAsString(), jsonClass);
        }
        return result;
    }

    private synchronized Boolean logIn() {
        logger.debug("Attempting to log in to mypages.verisure.com");

        String url = BASEURL + LOGON_SUF;
        String source = sendHTTPpost(url, authstring);
        logger.debug("Login URL: {}", url);
        if (source == null) {
            logger.debug("Failed to login");
            return Boolean.FALSE;
        } else {
            logger.debug("Login result: {}" + source);
            return Boolean.TRUE;
        }
    }

    private String configureInstallationInstance(BigDecimal installationInstance) throws Exception {
        logger.debug("Attempting to configure installation instance");

        String url = BASEURL + START_SUF + "?inst=" + installationInstance.toString();
        logger.debug("Start URL: " + url);
        ContentResponse resp = httpClient.GET(url);
        String source = resp.getContentAsString();
        csrf = getCsrfToken(source);
        logger.trace(source);
        logger.debug("Got CSRF: {}", csrf);
        return source;
    }

    @Nullable
    private String sendHTTPpost(String urlString, @Nullable String data) {
        if (data != null) {
            try {
                org.eclipse.jetty.client.api.Request request = httpClient.newRequest(urlString).method(HttpMethod.POST);
                request.header("x-csrf-token", csrf).header("Accept", "application/json");
                request.content(new BytesContentProvider(data.getBytes("UTF-8")),
                        "application/x-www-form-urlencoded; charset=UTF-8");
                ContentResponse response = request.send();
                String content = response.getContentAsString();
                String contentUTF8 = new String(content.getBytes("UTF-8"), "ISO-8859-1");
                logger.debug("HTTP Response ({}) Body:{}", response.getStatus(), contentUTF8);
                return contentUTF8;
            } catch (ExecutionException e) {
                logger.warn("Caught ExecutionException {}", e);
            } catch (UnsupportedEncodingException e) {
                logger.warn("Caught UnsupportedEncodingException {}", e);
            } catch (InterruptedException e) {
                logger.warn("Caught InterruptedException {}", e);
            } catch (TimeoutException e) {
                logger.warn("Caught TimeoutException {}", e);
            }
        }
        return null;
    }

    private boolean areWeLoggedIn() {
        logger.debug("areWeLoggedIn() - Checking if we are logged in");
        String urlString = BASEURL + ALARMSTATUS_PATH;

        try {

            ContentResponse response = httpClient.newRequest(urlString).method(HttpMethod.HEAD).send();
            logger.debug("HTTP HEAD response: " + response.getContentAsString());

            switch (response.getStatus()) {
                case 200:
                    // Redirection
                    logger.debug("Status code 200. Probably logged in");
                    return true;

                case 302:
                    // Redirection
                    logger.debug("Status code 302. Redirected. Probably not logged in");
                    return false;

                case 404:
                    // not found
                    logger.debug("Status code 404. Probably logged on too");
                    return true;

                default:
                    logger.info("Status code {} body {}", response.getStatus(), response.getContentAsString());
                    break;
            }

        } catch (ExecutionException e) {
            logger.warn("ExecutionException: {}", e);
        } catch (InterruptedException e) {
            logger.warn("InterruptedException: {}", e);
        } catch (TimeoutException e) {
            logger.warn("TimeoutException: {}", e);
        }
        return false;
    }

    public boolean refresh() {
        // Try to refresh 3 times
        for (int i = 0; i < 3; i++) {
            if (!areWeLoggedOut && areWeLoggedIn()) {
                updateStatus();
                return true;
            } else {
                try {
                    Thread.sleep(2000);
                    Boolean success = logIn();
                    if (success) {
                        areWeLoggedOut = false;
                    } else {
                        areWeLoggedOut = true;
                    }
                } catch (InterruptedException e) {
                    logger.warn("InterruptedException waiting for new login {}", e);
                }
            }
        }

        return false;
    }

    public void dispose() {
        logger.debug("Should dispose allocated stuff here in session");

    }

    private String getCsrfToken(String htmlText) {
        Document htmlDocument = Jsoup.parse(htmlText);
        Element nameInput = htmlDocument.select("input[name=_csrf]").first();
        String csrf = nameInput.attr("value");

        return csrf;
    }

    public boolean disarmAlarm(String installationName) {
        logger.debug("Sending command to disarm the alarm! Installation name: " + installationName);

        VerisureInstallation verisureInstallation = verisureInstallations.get(installationName);
        if (verisureInstallation != null) {
            try {
                BigDecimal instInst = verisureInstallation.getInstallationInstance();
                if (instInst != null) {
                    configureInstallationInstance(instInst);
                    String url = BASEURL + ALARM_COMMAND;
                    String data = "code=" + pinCode + "&state=DISARMED" + "&_csrf=" + csrf;
                    logger.debug("Trying to disarmAlarm with URL: " + url + " and data: " + data);
                    sendHTTPpost(url, data);
                    return true;
                }
            } catch (Exception e) {
                logger.error("Failed in unLock", e);
            }
        }
        return false;
    }

    public boolean armHomeAlarm(String installationName) {
        logger.debug("Sending command to arm_home the alarm! Installation name:" + installationName);

        VerisureInstallation verisureInstallation = verisureInstallations.get(installationName);
        if (verisureInstallation != null) {
            try {
                BigDecimal instInst = verisureInstallation.getInstallationInstance();
                if (instInst != null) {
                    configureInstallationInstance(instInst);
                    String url = BASEURL + ALARM_COMMAND;
                    String data = "code=" + pinCode + "&state=ARMED_HOME" + "&_csrf=" + csrf;
                    logger.debug("Trying to armHomeAlarm with URL: " + url + " and data: " + data);

                    sendHTTPpost(url, data);
                    return true;
                }
            } catch (Exception e) {
                logger.error("Failed in unLock", e);
            }
        }
        return false;
    }

    public boolean armAwayAlarm(String installationName) {
        logger.debug("Sending command to arm_away the alarm! Installation name:" + installationName);

        VerisureInstallation verisureInstallation = verisureInstallations.get(installationName);
        if (verisureInstallation != null) {
            try {
                BigDecimal instInst = verisureInstallation.getInstallationInstance();
                if (instInst != null) {
                    configureInstallationInstance(instInst);
                    String url = BASEURL + ALARM_COMMAND;
                    String data = "code=" + pinCode + "&state=ARMED_AWAY" + "&_csrf=" + csrf;

                    logger.debug("Trying to armAwayAlarm with URL: " + url + " and data: " + data);
                    sendHTTPpost(url, data);
                    return true;
                }
            } catch (Exception e) {
                logger.error("Failed in unLock", e);
            }
        }
        return false;
    }

    public boolean lock(String id, String installationName) {
        logger.debug("Sending command to lock!");

        VerisureInstallation verisureInstallation = verisureInstallations.get(installationName);
        if (verisureInstallation != null) {
            try {
                BigDecimal instInst = verisureInstallation.getInstallationInstance();
                if (instInst != null) {
                    configureInstallationInstance(instInst);
                    String smartLockUrl = id.replaceAll("_", "");
                    String url = BASEURL + SMARTLOCK_LOCK_COMMAND;
                    String data = "code=" + pinCode + "&state=LOCKED&deviceLabel=" + smartLockUrl + "&_csrf=" + csrf;

                    logger.debug("Trying to lock with URL: " + url + " and data: " + data);
                    sendHTTPpost(url, data);
                    return true;
                }
            } catch (Exception e) {
                logger.error("Failed in unLock", e);
            }
        }
        return false;
    }

    public boolean unLock(String id, String installationName) {
        logger.debug("Sending command to unlock!");
        VerisureInstallation verisureInstallation = verisureInstallations.get(installationName);
        if (verisureInstallation != null) {
            try {
                BigDecimal instInst = verisureInstallation.getInstallationInstance();
                if (instInst != null) {
                    configureInstallationInstance(instInst);
                    String smartLockUrl = id.replaceAll("_", "");
                    String url = BASEURL + SMARTLOCK_LOCK_COMMAND;
                    String data = "code=" + pinCode + "&state=UNLOCKED&deviceLabel=" + smartLockUrl + "&_csrf=" + csrf;

                    logger.debug("Trying to unlock with URL: " + url + " and data: " + data);
                    sendHTTPpost(url, data);
                    return true;
                }
            } catch (Exception e) {
                logger.error("Failed in unLock", e);
            }
        }
        return false;
    }

    public boolean smartPlugOn(String smartPlug, String installationName) {
        logger.debug("Sending command to turn on SmartPlug!");

        VerisureInstallation verisureInstallation = verisureInstallations.get(installationName);
        if (verisureInstallation != null) {
            try {
                BigDecimal instInst = verisureInstallation.getInstallationInstance();
                if (instInst != null) {
                    configureInstallationInstance(instInst);
                    String smartPlugUrl = smartPlug.replaceAll("_", "+");
                    String url = BASEURL + SMARTPLUG_COMMAND;
                    String data = "targetDeviceLabel=" + smartPlugUrl + "&targetOn=on";

                    logger.debug("Trying to turn on SmartPlug with URL: " + url + " and data:\n" + data);
                    sendHTTPpost(url, data);
                    return true;
                }
            } catch (Exception e) {
                logger.error("Failed in smartPlugOn", e);
            }
        }
        return false;
    }

    public boolean smartPlugOff(String smartPlug, String installationName) {
        logger.debug("Sending command to off SmartPlug!");

        VerisureInstallation verisureInstallation = verisureInstallations.get(installationName);
        if (verisureInstallation != null) {
            try {
                BigDecimal instInst = verisureInstallation.getInstallationInstance();
                if (instInst != null) {
                    configureInstallationInstance(instInst);
                    String smartPlugUrl = smartPlug.replaceAll("_", "+");
                    String url = BASEURL + SMARTPLUG_COMMAND;
                    String data = "targetDeviceLabel=" + smartPlugUrl + "&targetOn=off";

                    logger.debug("Trying to turn off SmartPlug with URL: " + url + " and data:\n" + data);
                    sendHTTPpost(url, data);
                    return true;
                }
            } catch (Exception e) {
                logger.error("Failed in smartPlugOff", e);
            }
        }
        return false;
    }

    public boolean autoRelockOn(@Nullable String id, @Nullable String installationName, @Nullable String location,
            @Nullable DoorLockVolumeSettings lockSettings) {
        logger.debug("Sending command to turn on Auto Relock!");

        VerisureInstallation verisureInstallation = verisureInstallations.get(installationName);
        if (verisureInstallation != null) {
            try {
                BigDecimal instInst = verisureInstallation.getInstallationInstance();
                if (instInst != null && id != null && lockSettings != null) {
                    configureInstallationInstance(instInst);
                    String deviceLabelUrl = id.replaceAll("_", "+");
                    String locationUTF8 = URLEncoder.encode(location, "utf-8");
                    String url = BASEURL + SMARTLOCK_SET_COMMAND;
                    String data = "location=" + locationUTF8 + "&autoRelockEnabled=true&_autoRelockEnabled=on"
                            + "&deviceLabel=" + deviceLabelUrl + "&doorLockVolumeSettings.volume="
                            + lockSettings.getVolume() + "&doorLockVolumeSettings.voiceLevel="
                            + lockSettings.getVoiceLevel() + "&_csrf=" + csrf;
                    logger.debug("Trying to turn on Auto Relock with URL: " + url + " and data:\n" + data);
                    sendHTTPpost(url, data);
                    return true;
                }
            } catch (Exception e) {
                logger.error("Failed in autoRelockOn", e);
            }
        }
        return false;
    }

    public boolean autoRelockOff(@Nullable String id, @Nullable String installationName, @Nullable String location,
            @Nullable DoorLockVolumeSettings lockSettings) {
        logger.debug("Sending command to turn off Auto Relock!");

        VerisureInstallation verisureInstallation = verisureInstallations.get(installationName);
        if (verisureInstallation != null) {
            try {
                BigDecimal instInst = verisureInstallation.getInstallationInstance();
                if (instInst != null && id != null && lockSettings != null) {
                    configureInstallationInstance(instInst);
                    String deviceLabelUrl = id.replaceAll("_", "+");
                    String locationUTF8 = URLEncoder.encode(location, "utf-8");
                    String url = BASEURL + SMARTLOCK_SET_COMMAND;
                    String data = "location=" + locationUTF8 + "&_autoRelockEnabled=on" + "&deviceLabel="
                            + deviceLabelUrl + "&doorLockVolumeSettings.volume=" + lockSettings.getVolume()
                            + "&doorLockVolumeSettings.voiceLevel=" + lockSettings.getVoiceLevel() + "&_csrf=" + csrf;
                    logger.debug("Trying to turn on Auto Relock with URL: " + url + " and data:\n" + data);
                    sendHTTPpost(url, data);
                    return true;
                }
            } catch (Exception e) {
                logger.error("Failed in autoRelockOn", e);
            }
        }
        return false;
    }

    public boolean setSmartLockVolume(@Nullable String id, @Nullable String installationName, @Nullable String location,
            @Nullable Boolean autoRelockEnabled, @Nullable DoorLockVolumeSettings lockSettings,
            @Nullable String volumeSetting) {
        logger.debug("Sending command to set SmartLock volume!");

        VerisureInstallation verisureInstallation = verisureInstallations.get(installationName);
        if (verisureInstallation != null) {
            try {
                BigDecimal instInst = verisureInstallation.getInstallationInstance();
                if (instInst != null && id != null && lockSettings != null) {
                    configureInstallationInstance(instInst);
                    String autoRelockStatus;
                    if (autoRelockEnabled != null && autoRelockEnabled) {
                        autoRelockStatus = "&autoRelockEnabled=true&_autoRelockEnabled=on";
                    } else {
                        autoRelockStatus = "&_autoRelockEnabled=on";
                    }

                    String deviceLabelUrl = id.replaceAll("_", "+");
                    String url = BASEURL + SMARTLOCK_SET_COMMAND;
                    String data = "location=" + location + autoRelockStatus + "&deviceLabel=" + deviceLabelUrl
                            + "&doorLockVolumeSettings.volume=" + volumeSetting + "&doorLockVolumeSettings.voiceLevel="
                            + lockSettings.getVoiceLevel() + "&_csrf=" + csrf;
                    logger.debug("Trying to set SmartLock volume with URL: " + url + " and data:\n" + data);
                    sendHTTPpost(url, data);
                    return true;
                }
            } catch (Exception e) {
                logger.error("Failed in setSmartLockVolume", e);
            }
        }
        return false;
    }

    public boolean setSmartLockVoiceLevel(@Nullable String id, @Nullable String installationName,
            @Nullable String location, @Nullable Boolean autoRelockEnabled,
            @Nullable DoorLockVolumeSettings lockSettings, @Nullable String voiceLevelSetting) {
        logger.debug("Sending command to set SmartLock voice level!");

        VerisureInstallation verisureInstallation = verisureInstallations.get(installationName);
        if (verisureInstallation != null) {
            try {
                BigDecimal instInst = verisureInstallation.getInstallationInstance();
                if (instInst != null && id != null && lockSettings != null) {
                    configureInstallationInstance(instInst);
                    String autoRelockStatus;
                    if (autoRelockEnabled != null && autoRelockEnabled) {
                        autoRelockStatus = "&autoRelockEnabled=true&_autoRelockEnabled=on";
                    } else {
                        autoRelockStatus = "&_autoRelockEnabled=on";
                    }

                    String deviceLabelUrl = id.replaceAll("_", "+");
                    String url = BASEURL + SMARTLOCK_SET_COMMAND;
                    String data = "location=" + location + autoRelockStatus + "&deviceLabel=" + deviceLabelUrl
                            + "&doorLockVolumeSettings.volume=" + lockSettings.getVolume()
                            + "&doorLockVolumeSettings.voiceLevel=" + voiceLevelSetting + "&_csrf=" + csrf;
                    logger.debug("Trying to set SmartLock voice level with URL: " + url + " and data:\n" + data);
                    sendHTTPpost(url, data);
                    return true;
                }
            } catch (Exception e) {
                logger.error("Failed in setSmartLockVoiceLevel", e);
            }
        }
        return false;
    }

    public boolean registerDeviceStatusListener(DeviceStatusListener deviceStatusListener) {
        return deviceStatusListeners.add(deviceStatusListener);
    }

    public boolean unregisterDeviceStatusListener(DeviceStatusListener deviceStatusListener) {
        boolean result = deviceStatusListeners.remove(deviceStatusListener);
        if (result) {
            // onUpdate();
        }
        return result;
    }

}
