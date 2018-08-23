/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.volvooncall.internal.json;

import java.util.List;

/**
 * The {@link VehicleAttributes} is responsible for storing
 * informations returned by vehicule attributes rest answer
 *
 * @author Gaël L'hopital - Initial contribution
 */
public class VehicleAttributes {

    public class Country {
        private String iso2;

        public String getIso2() {
            return iso2;
        }

    }

    private String engineCode;
    private String exteriorCode;
    private String interiorCode;
    private String tyreDimensionCode;
    private Object tyreInflationPressureLightCode;
    private Object tyreInflationPressureHeavyCode;
    private String gearboxCode;
    private String fuelType;
    private Integer fuelTankVolume;
    private Integer grossWeight;
    private Integer modelYear;
    private String vehicleType;
    private String vehicleTypeCode;
    private Integer numberOfDoors;
    private Country country;
    private String registrationNumber;
    private Integer carLocatorDistance;
    private Integer honkAndBlinkDistance;
    private String bCallAssistanceNumber;
    private Boolean carLocatorSupported;
    private Boolean honkAndBlinkSupported;
    private List<String> honkAndBlinkVersionsSupported = null;
    private Boolean remoteHeaterSupported;
    private Boolean unlockSupported;
    private Boolean lockSupported;
    private Boolean journalLogSupported;
    private Boolean assistanceCallSupported;
    private Integer unlockTimeFrame;
    private Integer verificationTimeFrame;
    private Integer timeFullyAccessible;
    private Integer timePartiallyAccessible;
    private String subscriptionType;
    private String subscriptionStartDate;
    private String subscriptionEndDate;
    private String serverVersion;
    private Boolean journalLogEnabled;
    private Boolean highVoltageBatterySupported;
    private Object maxActiveDelayChargingLocations;
    private Boolean preclimatizationSupported;
    private List<String> sendPOIToVehicleVersionsSupported = null;
    private List<String> climatizationCalendarVersionsSupported = null;
    private Integer climatizationCalendarMaxTimers;
    private String vehiclePlatform;
    private String vin;
    private Boolean overrideDelayChargingSupported;
    private Boolean engineStartSupported;
    private Boolean statusParkedIndoorSupported;

    public String getEngineCode() {
        return engineCode;
    }

    public String getExteriorCode() {
        return exteriorCode;
    }

    public String getInteriorCode() {
        return interiorCode;
    }

    public String getTyreDimensionCode() {
        return tyreDimensionCode;
    }

    public Object getTyreInflationPressureLightCode() {
        return tyreInflationPressureLightCode;
    }

    public Object getTyreInflationPressureHeavyCode() {
        return tyreInflationPressureHeavyCode;
    }

    public String getGearboxCode() {
        return gearboxCode;
    }

    public String getFuelType() {
        return fuelType;
    }

    public Integer getFuelTankVolume() {
        return fuelTankVolume;
    }

    public Integer getGrossWeight() {
        return grossWeight;
    }

    public Integer getModelYear() {
        return modelYear;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getVehicleTypeCode() {
        return vehicleTypeCode;
    }

    public Integer getNumberOfDoors() {
        return numberOfDoors;
    }

    public Country getCountry() {
        return country;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public Integer getCarLocatorDistance() {
        return carLocatorDistance;
    }

    public Integer getHonkAndBlinkDistance() {
        return honkAndBlinkDistance;
    }

    public String getBCallAssistanceNumber() {
        return bCallAssistanceNumber;
    }

    public Boolean getCarLocatorSupported() {
        return carLocatorSupported;
    }

    public Boolean getHonkAndBlinkSupported() {
        return honkAndBlinkSupported;
    }

    public List<String> getHonkAndBlinkVersionsSupported() {
        return honkAndBlinkVersionsSupported;
    }

    public Boolean getRemoteHeaterSupported() {
        return remoteHeaterSupported;
    }

    public Boolean getUnlockSupported() {
        return unlockSupported;
    }

    public Boolean getLockSupported() {
        return lockSupported;
    }

    public Boolean getJournalLogSupported() {
        return journalLogSupported;
    }

    public Boolean getAssistanceCallSupported() {
        return assistanceCallSupported;
    }

    public Integer getUnlockTimeFrame() {
        return unlockTimeFrame;
    }

    public Integer getVerificationTimeFrame() {
        return verificationTimeFrame;
    }

    public Integer getTimeFullyAccessible() {
        return timeFullyAccessible;
    }

    public Integer getTimePartiallyAccessible() {
        return timePartiallyAccessible;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public String getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    public String getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public Boolean getJournalLogEnabled() {
        return journalLogEnabled;
    }

    public Boolean getHighVoltageBatterySupported() {
        return highVoltageBatterySupported;
    }

    public Object getMaxActiveDelayChargingLocations() {
        return maxActiveDelayChargingLocations;
    }

    public Boolean getPreclimatizationSupported() {
        return preclimatizationSupported;
    }

    public List<String> getSendPOIToVehicleVersionsSupported() {
        return sendPOIToVehicleVersionsSupported;
    }

    public List<String> getClimatizationCalendarVersionsSupported() {
        return climatizationCalendarVersionsSupported;
    }

    public Integer getClimatizationCalendarMaxTimers() {
        return climatizationCalendarMaxTimers;
    }

    public String getVehiclePlatform() {
        return vehiclePlatform;
    }

    public String getVin() {
        return vin;
    }

    public Boolean getOverrideDelayChargingSupported() {
        return overrideDelayChargingSupported;
    }

    public Boolean getEngineStartSupported() {
        return engineStartSupported;
    }

    public Boolean getStatusParkedIndoorSupported() {
        return statusParkedIndoorSupported;
    }

}
