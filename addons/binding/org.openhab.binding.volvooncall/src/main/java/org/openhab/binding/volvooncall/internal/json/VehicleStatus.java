/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.volvooncall.internal.json;

import java.time.ZonedDateTime;
import java.util.List;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.OpenClosedType;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link VehicleStatus} is responsible for storing
 * informations returned by vehicule status rest answer
 *
 * @author Gaël L'hopital - Initial contribution
 */
public class VehicleStatus {

    public class Doors {

        private OpenClosedType tailgateOpen;
        private OpenClosedType rearRightDoorOpen;
        private OpenClosedType rearLeftDoorOpen;
        private OpenClosedType frontRightDoorOpen;
        private OpenClosedType frontLeftDoorOpen;
        private OpenClosedType hoodOpen;
        private ZonedDateTime timestamp;

        public OpenClosedType getTailgateOpen() {
            return tailgateOpen;
        }

        public OpenClosedType getRearRightDoorOpen() {
            return rearRightDoorOpen;
        }

        public OpenClosedType getRearLeftDoorOpen() {
            return rearLeftDoorOpen;
        }

        public OpenClosedType getFrontRightDoorOpen() {
            return frontRightDoorOpen;
        }

        public OpenClosedType getFrontLeftDoorOpen() {
            return frontLeftDoorOpen;
        }

        public OpenClosedType getHoodOpen() {
            return hoodOpen;
        }

        public ZonedDateTime getTimestamp() {
            return timestamp;
        }

    }

    public class ERS {

        private String status;
        private ZonedDateTime timestamp;
        private String engineStartWarning;
        private ZonedDateTime engineStartWarningTimestamp;

        public String getStatus() {
            return status;
        }

        public ZonedDateTime getTimestamp() {
            return timestamp;
        }

        public String getEngineStartWarning() {
            return engineStartWarning;
        }

        public ZonedDateTime getEngineStartWarningTimestamp() {
            return engineStartWarningTimestamp;
        }

    }

    public class TyrePressure {

        private String frontLeftTyrePressure;
        private String frontRightTyrePressure;
        private String rearLeftTyrePressure;
        private String rearRightTyrePressure;
        private ZonedDateTime timestamp;

        public String getFrontLeftTyrePressure() {
            return frontLeftTyrePressure;
        }

        public String getFrontRightTyrePressure() {
            return frontRightTyrePressure;
        }

        public String getRearLeftTyrePressure() {
            return rearLeftTyrePressure;
        }

        public String getRearRightTyrePressure() {
            return rearRightTyrePressure;
        }

        public ZonedDateTime getTimestamp() {
            return timestamp;
        }

    }

    public class Windows {

        private OpenClosedType frontLeftWindowOpen;
        private OpenClosedType frontRightWindowOpen;
        private OpenClosedType rearLeftWindowOpen;
        private OpenClosedType rearRightWindowOpen;
        private ZonedDateTime timestamp;

        public OpenClosedType getFrontLeftWindowOpen() {
            return frontLeftWindowOpen;
        }

        public OpenClosedType getFrontRightWindowOpen() {
            return frontRightWindowOpen;
        }

        public ZonedDateTime getTimestamp() {
            return timestamp;
        }

        public OpenClosedType getRearLeftWindowOpen() {
            return rearLeftWindowOpen;
        }

        public OpenClosedType getRearRightWindowOpen() {
            return rearRightWindowOpen;
        }

    }

    @SerializedName("ERS")
    private ERS ers;
    private Double averageFuelConsumption;
    private ZonedDateTime averageFuelConsumptionTimestamp;
    private Integer averageSpeed;
    private ZonedDateTime averageSpeedTimestamp;
    private String brakeFluid;
    private ZonedDateTime brakeFluidTimestamp;
    private List<String> bulbFailures = null;
    private ZonedDateTime bulbFailuresTimestamp;
    private OnOffType carLocked;
    private ZonedDateTime carLockedTimestamp;
    private Integer distanceToEmpty;
    private ZonedDateTime distanceToEmptyTimestamp;
    private Doors doors;
    private OnOffType engineRunning;
    private ZonedDateTime engineRunningTimestamp;
    private Integer fuelAmount;
    private Integer fuelAmountLevel;
    private ZonedDateTime fuelAmountLevelTimestamp;
    private ZonedDateTime fuelAmountTimestamp;
    private Integer odometer;
    private ZonedDateTime odometerTimestamp;
    private Boolean privacyPolicyEnabled;
    private ZonedDateTime privacyPolicyEnabledTimestamp;
    private String remoteClimatizationStatus;
    private ZonedDateTime remoteClimatizationStatusTimestamp;
    private String serviceWarningStatus;
    private ZonedDateTime serviceWarningStatusTimestamp;
    private Object theftAlarm;
    private String timeFullyAccessibleUntil;
    private String timePartiallyAccessibleUntil;
    private Integer tripMeter1;
    private ZonedDateTime tripMeter1Timestamp;
    private Integer tripMeter2;
    private ZonedDateTime tripMeter2Timestamp;
    private TyrePressure tyrePressure;
    private String washerFluidLevel;
    private ZonedDateTime washerFluidLevelTimestamp;
    private Windows windows;

    public ERS getErs() {
        return ers;
    }

    public Double getAverageFuelConsumption() {
        return averageFuelConsumption;
    }

    public ZonedDateTime getAverageFuelConsumptionTimestamp() {
        return averageFuelConsumptionTimestamp;
    }

    public Integer getAverageSpeed() {
        return averageSpeed;
    }

    public ZonedDateTime getAverageSpeedTimestamp() {
        return averageSpeedTimestamp;
    }

    public String getBrakeFluid() {
        return brakeFluid;
    }

    public ZonedDateTime getBrakeFluidTimestamp() {
        return brakeFluidTimestamp;
    }

    public List<String> getBulbFailures() {
        return bulbFailures;
    }

    public ZonedDateTime getBulbFailuresTimestamp() {
        return bulbFailuresTimestamp;
    }

    public OnOffType getCarLocked() {
        return carLocked;
    }

    public ZonedDateTime getCarLockedTimestamp() {
        return carLockedTimestamp;
    }

    public Integer getDistanceToEmpty() {
        return distanceToEmpty;
    }

    public ZonedDateTime getDistanceToEmptyTimestamp() {
        return distanceToEmptyTimestamp;
    }

    public Doors getDoors() {
        return doors;
    }

    public OnOffType getEngineRunning() {
        return engineRunning;
    }

    public ZonedDateTime getEngineRunningTimestamp() {
        return engineRunningTimestamp;
    }

    public Integer getFuelAmount() {
        return fuelAmount;
    }

    public Integer getFuelAmountLevel() {
        return fuelAmountLevel;
    }

    public ZonedDateTime getFuelAmountLevelTimestamp() {
        return fuelAmountLevelTimestamp;
    }

    public ZonedDateTime getFuelAmountTimestamp() {
        return fuelAmountTimestamp;
    }

    public Integer getOdometer() {
        return odometer;
    }

    public ZonedDateTime getOdometerTimestamp() {
        return odometerTimestamp;
    }

    public Boolean getPrivacyPolicyEnabled() {
        return privacyPolicyEnabled;
    }

    public ZonedDateTime getPrivacyPolicyEnabledTimestamp() {
        return privacyPolicyEnabledTimestamp;
    }

    public String getRemoteClimatizationStatus() {
        return remoteClimatizationStatus;
    }

    public ZonedDateTime getRemoteClimatizationStatusTimestamp() {
        return remoteClimatizationStatusTimestamp;
    }

    public String getServiceWarningStatus() {
        return serviceWarningStatus;
    }

    public ZonedDateTime getServiceWarningStatusTimestamp() {
        return serviceWarningStatusTimestamp;
    }

    public Object getTheftAlarm() {
        return theftAlarm;
    }

    public String getTimeFullyAccessibleUntil() {
        return timeFullyAccessibleUntil;
    }

    public String getTimePartiallyAccessibleUntil() {
        return timePartiallyAccessibleUntil;
    }

    public Integer getTripMeter1() {
        return tripMeter1;
    }

    public ZonedDateTime getTripMeter1Timestamp() {
        return tripMeter1Timestamp;
    }

    public Integer getTripMeter2() {
        return tripMeter2;
    }

    public ZonedDateTime getTripMeter2Timestamp() {
        return tripMeter2Timestamp;
    }

    public TyrePressure getTyrePressure() {
        return tyrePressure;
    }

    public String getWasherFluidLevel() {
        return washerFluidLevel;
    }

    public ZonedDateTime getWasherFluidLevelTimestamp() {
        return washerFluidLevelTimestamp;
    }

    public Windows getWindows() {
        return windows;
    }

}
