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

/**
 * The {@link TripDetail} is responsible for storing
 * trip details returned by trip rest answer
 *
 * @author Gaël L'hopital - Initial contribution
 */
public class TripDetail {
    private Integer fuelConsumption;
    private Integer electricalConsumption;
    private Integer electricalRegeneration;
    private Integer distance;
    private Integer startOdometer;
    private ZonedDateTime startTime;
    private TripPosition startPosition;
    private Integer endOdometer;
    private ZonedDateTime endTime;
    private TripPosition endPosition;

    public Integer getFuelConsumption() {
        return fuelConsumption;
    }

    public Integer getElectricalConsumption() {
        return electricalConsumption;
    }

    public Integer getElectricalRegeneration() {
        return electricalRegeneration;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getStartOdometer() {
        return startOdometer;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public TripPosition getStartPosition() {
        return startPosition;
    }

    public Integer getEndOdometer() {
        return endOdometer;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public TripPosition getEndPosition() {
        return endPosition;
    }

}
