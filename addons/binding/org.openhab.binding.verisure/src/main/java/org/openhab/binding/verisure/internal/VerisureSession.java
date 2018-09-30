/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.verisure.internal;

import static org.openhab.binding.verisure.VerisureBindingConstants.*;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.ssl.SslContextFactory;
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
 * @author Jarle Hjortland
 *
 */
public class VerisureSession {
    private HashMap<String, VerisureThingJSON> verisureThings = new HashMap<String, VerisureThingJSON>();

    private Logger logger = LoggerFactory.getLogger(VerisureSession.class);
    private String authstring;
    private String csrf;
    private BigDecimal pinCode;
    private BigDecimal numberOfInstallations;
    private Gson gson = new GsonBuilder().create();

    private List<DeviceStatusListener> deviceStatusListeners = new CopyOnWriteArrayList<>();
    private HttpClient httpClient;

    private class VerisureInstallation {
        private String installationName;
        private BigDecimal installationInstance;
        private BigDecimal installationId;

        public VerisureInstallation(String installationName) {
            this.installationName = installationName;
        }

        public String getInstallationName() {
            return installationName;
        }

        public void setInstallationName(String installationName) {
            this.installationName = installationName;
        }

        public BigDecimal getInstallationId() {
            return installationId;
        }

        public void setInstallationId(BigDecimal installationId) {
            this.installationId = installationId;
        }

        public BigDecimal getInstallationInstance() {
            return installationInstance;
        }

        public void setInstallationInstance(BigDecimal installationInstance) {
            this.installationInstance = installationInstance;
        }

    }

    private Hashtable<String, VerisureInstallation> verisureInstallations = new Hashtable<String, VerisureInstallation>();;

    public void initialize(String _authstring, BigDecimal pinCode, BigDecimal numberOfInstallations) throws Exception {
        logger.debug("VerisureSession:initialize");

        this.authstring = _authstring.substring(0);
        this.pinCode = pinCode;
        this.numberOfInstallations = numberOfInstallations;
        // Instantiate and configure the SslContextFactory
        SslContextFactory sslContextFactory = new SslContextFactory();
        this.httpClient = new HttpClient(sslContextFactory);
        this.httpClient.start();
        logIn();
        getInstallations();
        updateStatus();
    }

    private void getInstallations() {
        logger.debug("VerisureSession:handleInstallations");
        try {
            for (int i = 1; i < numberOfInstallations.intValue() + 1; i++) {
                BigDecimal inst = new BigDecimal(i);
                String html = configureInstallationInstance(inst);
                handleInstallation(html, inst);
            }
        } catch (Exception e) {
            logger.error("Failed in handleInstallations", e);
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
            if (verisureInstallations.get(alarmInstallationName2) == null) {
                verisureInstallation = new VerisureInstallation(alarmInstallationName2);
                verisureInstallation.setInstallationId(new BigDecimal(installationId));
                verisureInstallations.put(alarmInstallationName2, verisureInstallation);
            } else {
                verisureInstallation.setInstallationId(new BigDecimal(installationId));
            }
        }
    }

    private void updateStatus() {
        logger.debug("VerisureSession:updateStatus");
        for (int i = 1; i < numberOfInstallations.intValue() + 1; i++) {
            try {
                BigDecimal inst = new BigDecimal(i);
                configureInstallationInstance(inst);

                VerisureInstallation vInst = null;
                for (Enumeration<VerisureInstallation> num = verisureInstallations.elements(); num.hasMoreElements();) {
                    vInst = num.nextElement();
                    if (vInst.getInstallationInstance().equals(inst)) {
                        break;
                    }
                }

                updateVerisureThings(ALARMSTATUS_PATH, VerisureAlarmJSON[].class, vInst);
                updateVerisureThings(CLIMATEDEVICE_PATH, VerisureClimateBaseJSON[].class, vInst);
                updateVerisureThings(DOORWINDOW_PATH, VerisureDoorWindowsJSON[].class, vInst);
                updateVerisureThings(USERTRACKING_PATH, VerisureUserPresenceJSON[].class, vInst);
                updateVerisureThings(SMARTPLUG_PATH, VerisureSmartPlugJSON[].class, vInst);
                updateVerisureBroadbandStatus(ETHERNETSTATUS_PATH, VerisureBroadbandConnectionJSON.class, vInst);
            } catch (Exception e) {
                logger.error("Failed in updatestatus", e);
            }
        }
    }

    public synchronized void updateVerisureBroadbandStatus(String urlString,
            Class<? extends VerisureThingJSON> jsonClass, VerisureInstallation inst) {

        try {
            VerisureThingJSON thing = callJSONRest(urlString, jsonClass);
            logger.debug("REST Response ({})", thing);
            if (thing != null) {
                thing.setId(inst.getInstallationInstance().toString());
                VerisureThingJSON oldObj = verisureThings.get(thing.getId());
                if (oldObj == null || !oldObj.equals(thing)) {
                    thing.setSiteId(inst.getInstallationId());
                    thing.setSiteName(inst.getInstallationName());
                    verisureThings.put(thing.getId(), thing);
                    notifyListeners(thing);
                }
            }
        } catch (Exception e) {
            logger.info("Failed to get all {}", urlString, e);
        }
    }

    public synchronized void updateVerisureThings(String urlString, Class<? extends VerisureThingJSON[]> jsonClass,
            VerisureInstallation inst) {

        try {
            VerisureThingJSON[] things = callJSONRest(urlString, jsonClass);
            logger.debug("REST Response ({})", (Object[]) things);
            if (things != null) {
                for (VerisureThingJSON thing : things) {
                    if (thing instanceof VerisureUserPresenceJSON) {
                        thing.setId(inst.getInstallationInstance().toString());
                    } else if (thing instanceof VerisureAlarmJSON) {
                        if (((VerisureAlarmJSON) thing).getType().equals("ARM_STATE")) {
                            thing.setId(inst.getInstallationInstance().toString());
                        } else {
                            VerisureThingJSON smartLockThing = callJSONRest(SMARTLOCK_PATH + thing.getId(),
                                    VerisureSmartLockJSON.class);
                            ((VerisureSmartLockJSON) smartLockThing).setDate(((VerisureAlarmJSON) thing).getDate());
                            ((VerisureSmartLockJSON) smartLockThing)
                                    .setNotAllowedReason(((VerisureAlarmJSON) thing).getNotAllowedReason());
                            ((VerisureSmartLockJSON) smartLockThing)
                                    .setChangeAllowed(((VerisureAlarmJSON) thing).getChangeAllowed());
                            ((VerisureSmartLockJSON) smartLockThing).setLabel(((VerisureAlarmJSON) thing).getLabel());
                            ((VerisureSmartLockJSON) smartLockThing).setType(((VerisureAlarmJSON) thing).getType());
                            ((VerisureSmartLockJSON) smartLockThing).setName(((VerisureAlarmJSON) thing).getName());
                            ((VerisureSmartLockJSON) smartLockThing)
                                    .setLocation(((VerisureAlarmJSON) thing).getLocation());
                            ((VerisureSmartLockJSON) smartLockThing).setStatus(((VerisureAlarmJSON) thing).getStatus());
                            String id = smartLockThing.getId();
                            if (id != null) {
                                smartLockThing.setId(id.replaceAll("[^a-zA-Z0-9_]", "_"));
                            }
                            thing = smartLockThing;
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
                        verisureThings.put(thing.getId(), thing);
                        notifyListeners(thing);
                    }
                }
            }
        } catch (Exception e) {
            logger.info("Failed to get all {}", urlString, e);
        }
    }

    private void notifyListeners(VerisureThingJSON cse) {
        for (DeviceStatusListener listener : deviceStatusListeners) {
            listener.onDeviceStateChanged(cse);
        }
    }

    public VerisureThingJSON getVerisureThing(String key) {
        return verisureThings.get(key);
    }

    public HashMap<String, VerisureThingJSON> getVerisureThings() {
        return verisureThings;
    }

    private <T> T callJSONRest(String url, Class<T> jsonClass) throws Exception {
        T result = null;
        logger.debug("HTTP GET: " + BASEURL + url);
        // ContentResponse httpResult = httpClient.GET(BASEURL + url + System.currentTimeMillis());
        ContentResponse httpResult = httpClient.GET(BASEURL + url);
        logger.debug("HTTP Response ({}) Body:{}", httpResult.getStatus(),
                httpResult.getContentAsString().replaceAll("\n+", "\n"));
        if (httpResult.getStatus() == HttpStatus.OK_200) {
            result = gson.fromJson(httpResult.getContentAsString(), jsonClass);
        }
        return result;
    }

    private synchronized void logIn() throws Exception {
        logger.debug("Attempting to log in to mypages.verisure.com");

        String url = BASEURL + LOGON_SUF;
        String source = sendHTTPpost(url, authstring);
        logger.debug("Login URL: " + source);
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

    private String sendHTTPpost(String urlString, String data) {
        try {
            org.eclipse.jetty.client.api.Request request = httpClient.newRequest(urlString).method(HttpMethod.POST);
            request.header("x-csrf-token", csrf).header("Accept", "application/json");
            request.content(new BytesContentProvider(data.getBytes("UTF-8")),
                    "application/x-www-form-urlencoded; charset=UTF-8");

            ContentResponse response = request.send();
            String content = response.getContentAsString();
            logger.debug("HTTP Response ({}) Body:{}", response.getStatus(), content);
            return content;
        } catch (Exception e) {
            logger.warn("had an exception {}", e.toString(), e);
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
                    break;

                default:
                    logger.info("Status code {} body {}", response.getStatus(), response.getContentAsString());
                    break;
            }

        } catch (Exception e) {
            logger.warn("Error:", e);
        }

        return false;

    }

    public boolean refresh() {

        for (int i = 0; i < 3; i++) {
            if (areWeLoggedIn()) {
                updateStatus();
                return true;
            } else {
                try {
                    Thread.sleep(2000);
                    logIn();
                } catch (InterruptedException e) {
                    logger.warn("Interupted waiting for new login ", e);
                } catch (Exception e) {
                    logger.warn("Failed waiting for new login ", e);
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

    public boolean disarmAlarm(String id) {
        logger.debug("Sending command to disarm the alarm!");

        try {
            configureInstallationInstance(new BigDecimal(id));
        } catch (Exception e) {
            logger.error("Failed in disarmAlarm", e);
        }

        String url = BASEURL + ALARM_COMMAND;
        String data = "code=" + pinCode + "&state=DISARMED" + "&_csrf=" + csrf;

        sendHTTPpost(url, data);
        return true;
    }

    public boolean armHomeAlarm(String id) {
        logger.debug("Sending command to arm_home the alarm!");

        try {
            configureInstallationInstance(new BigDecimal(id));
        } catch (Exception e) {
            logger.error("Failed in armHomeAlarm", e);
        }

        String url = BASEURL + ALARM_COMMAND;
        String data = "code=" + pinCode + "&state=ARMED_HOME" + "&_csrf=" + csrf;

        sendHTTPpost(url, data);
        return true;
    }

    public boolean armAwayAlarm(String id) {
        logger.debug("Sending command to arm_away the alarm!");

        try {
            configureInstallationInstance(new BigDecimal(id));
        } catch (Exception e) {
            logger.error("Failed in armAwayAlarm", e);
        }

        String url = BASEURL + ALARM_COMMAND;
        String data = "code=" + pinCode + "&state=ARMED_AWAY" + "&_csrf=" + csrf;

        sendHTTPpost(url, data);
        return true;
    }

    public boolean lock(String id, String installationName) {
        logger.debug("Sending command to lock!");

        VerisureInstallation verisureInstallation = verisureInstallations.get(installationName);

        try {
            configureInstallationInstance(verisureInstallation.getInstallationInstance());
        } catch (Exception e) {
            logger.error("Failed in unLock", e);
        }

        String smartLockUrl = id.replaceAll("_", "");
        String url = BASEURL + SMARTLOCK_LOCK_COMMAND;
        String data = "code=" + pinCode + "&state=LOCKED&deviceLabel=" + smartLockUrl + "&_csrf=" + csrf;

        logger.debug("Trying to lock with URL: " + url + " and data: " + data);
        sendHTTPpost(url, data);
        return true;
    }

    public boolean unLock(String id, String installationName) {
        logger.debug("Sending command to unlock!");
        VerisureInstallation verisureInstallation = verisureInstallations.get(installationName);

        try {
            configureInstallationInstance(verisureInstallation.getInstallationInstance());
        } catch (Exception e) {
            logger.error("Failed in unLock", e);
        }

        String smartLockUrl = id.replaceAll("_", "");
        String url = BASEURL + SMARTLOCK_LOCK_COMMAND;
        String data = "code=" + pinCode + "&state=UNLOCKED&deviceLabel=" + smartLockUrl + "&_csrf=" + csrf;

        logger.debug("Trying to unlock with URL: " + url + " and data: " + data);
        sendHTTPpost(url, data);
        return true;
    }

    public boolean smartPlugOn(String smartPlug, String installationName) {
        logger.debug("Sending command to turn on SmartPlug!");

        VerisureInstallation verisureInstallation = verisureInstallations.get(installationName);

        try {
            configureInstallationInstance(verisureInstallation.getInstallationInstance());
        } catch (Exception e) {
            logger.error("Failed in smartPlugOn", e);
        }

        String smartPlugUrl = smartPlug.replaceAll("_", "+");
        String url = BASEURL + SMARTPLUG_COMMAND;
        String data = "targetDeviceLabel=" + smartPlugUrl + "&targetOn=on";

        logger.debug("Trying to turn on SmartPlug with URL: " + url + " and data:\n" + data);
        sendHTTPpost(url, data);
        return true;
    }

    public boolean smartPlugOff(String smartPlug, String installationName) {
        logger.debug("Sending command to off SmartPlug!");

        VerisureInstallation verisureInstallation = verisureInstallations.get(installationName);

        try {
            configureInstallationInstance(verisureInstallation.getInstallationInstance());
        } catch (Exception e) {
            logger.error("Failed in smartPlugOff", e);
        }

        String smartPlugUrl = smartPlug.replaceAll("_", "+");
        String url = BASEURL + SMARTPLUG_COMMAND;
        String data = "targetDeviceLabel=" + smartPlugUrl + "&targetOn=off";

        logger.debug("Trying to turn off SmartPlug with URL: " + url + " and data:\n" + data);
        sendHTTPpost(url, data);
        return true;
    }

    public boolean autoRelockOn(String id, String installationName, String location,
            DoorLockVolumeSettings lockSettings) {
        logger.debug("Sending command to turn on Auto Relock!");

        VerisureInstallation verisureInstallation = verisureInstallations.get(installationName);

        try {
            configureInstallationInstance(verisureInstallation.getInstallationInstance());
        } catch (Exception e) {
            logger.error("Failed in autoRelockOn", e);
        }

        String deviceLabelUrl = id.replaceAll("_", "+");
        String url = BASEURL + SMARTLOCK_SET_COMMAND;
        String data = "location=" + location + "&autoRelockEnabled=true&_autoRelockEnabled=on" + "&deviceLabel="
                + deviceLabelUrl + "&doorLockVolumeSettings.volume=" + lockSettings.getVolume()
                + "&doorLockVolumeSettings.voiceLevel=" + lockSettings.getVoiceLevel() + "&_csrf=" + csrf;

        logger.debug("Trying to turn on Auto Relock with URL: " + url + " and data:\n" + data);
        sendHTTPpost(url, data);
        return true;
    }

    public boolean autoRelockOff(String id, String installationName, String location,
            DoorLockVolumeSettings lockSettings) {
        logger.debug("Sending command to turn off Auto Relock!");

        VerisureInstallation verisureInstallation = verisureInstallations.get(installationName);

        try {
            configureInstallationInstance(verisureInstallation.getInstallationInstance());
        } catch (Exception e) {
            logger.error("Failed in autoRelockOff", e);
        }

        String deviceLabelUrl = id.replaceAll("_", "+");
        String url = BASEURL + SMARTLOCK_SET_COMMAND;
        String data = "location=" + location + "&_autoRelockEnabled=on" + "&deviceLabel=" + deviceLabelUrl
                + "&doorLockVolumeSettings.volume=" + lockSettings.getVolume() + "&doorLockVolumeSettings.voiceLevel="
                + lockSettings.getVoiceLevel() + "&_csrf=" + csrf;

        logger.debug("Trying to turn off Auto Relock with URL: " + url + " and data:\n" + data);
        sendHTTPpost(url, data);
        return true;
    }

    public boolean setSmartLockVolume(String id, String installationName, String location, Boolean autoRelockEnabled,
            DoorLockVolumeSettings lockSettings, String volumeSetting) {
        logger.debug("Sending command to set SmartLock volume!");

        VerisureInstallation verisureInstallation = verisureInstallations.get(installationName);

        try {
            configureInstallationInstance(verisureInstallation.getInstallationInstance());
        } catch (Exception e) {
            logger.error("Failed in setSmartLockVolume", e);
        }

        String autoRelockStatus;
        if (autoRelockEnabled) {
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

    public boolean setSmartLockVoiceLevel(String id, String installationName, String location,
            Boolean autoRelockEnabled, DoorLockVolumeSettings lockSettings, String voiceLevelSetting) {
        logger.debug("Sending command to set SmartLock voice level!");

        VerisureInstallation verisureInstallation = verisureInstallations.get(installationName);

        try {
            configureInstallationInstance(verisureInstallation.getInstallationInstance());
        } catch (Exception e) {
            logger.error("Failed in setSmartLockVoiceLevel", e);
        }

        String autoRelockStatus;
        if (autoRelockEnabled) {
            autoRelockStatus = "&autoRelockEnabled=true&_autoRelockEnabled=on";
        } else {
            autoRelockStatus = "&_autoRelockEnabled=on";
        }

        String deviceLabelUrl = id.replaceAll("_", "+");
        String url = BASEURL + SMARTLOCK_SET_COMMAND;
        String data = "location=" + location + autoRelockStatus + "&deviceLabel=" + deviceLabelUrl
                + "&doorLockVolumeSettings.volume=" + lockSettings.getVolume() + "&doorLockVolumeSettings.voiceLevel="
                + voiceLevelSetting + "&_csrf=" + csrf;
        logger.debug("Trying to set SmartLock voice level with URL: " + url + " and data:\n" + data);
        sendHTTPpost(url, data);
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openhab.binding.tellstick.handler.TelldusBridgeHandlerIntf#registerDeviceStatusListener(org.openhab.binding.
     * tellstick.handler.DeviceStatusListener)
     */
    public boolean registerDeviceStatusListener(DeviceStatusListener deviceStatusListener) {
        return deviceStatusListeners.add(deviceStatusListener);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.openhab.binding.tellstick.handler.TelldusBridgeHandlerIntf#unregisterDeviceStatusListener(org.openhab.binding
     * .tellstick.handler.DeviceStatusListener)
     */
    public boolean unregisterDeviceStatusListener(DeviceStatusListener deviceStatusListener) {
        boolean result = deviceStatusListeners.remove(deviceStatusListener);
        if (result) {
            // onUpdate();
        }
        return result;
    }

}
