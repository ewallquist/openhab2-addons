/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.volvooncall.internal.json;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PointType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;

/**
 * The {@link VehiclePosition} is responsible for storing
 * informations returned by vehicle position rest
 * answer
 *
 * @author Gaël L'hopital - Initial contribution
 */
public class VehiclePosition {
    public static final String ENDPOINT = "/position";

    public class Position {
        private Double longitude;
        private Double latitude;
        private ZonedDateTime timestamp;
        private String speed;
        private Boolean heading;

        public Double getLongitude() {
            return longitude;
        }

        public Double getLatitude() {
            return latitude;
        }

        public String getSpeed() {
            return speed;
        }

        public Boolean getHeading() {
            return heading;
        }

        public ZonedDateTime getTimestamp() {
            return timestamp;
        }
    }

    private Position position;
    private Position calculatedPosition;

    public State getPosition() {
        if (position.getLatitude() != null) {
            return getPositionAsState(position);
        } else if (calculatedPosition.getLatitude() != null) {
            return getPositionAsState(calculatedPosition);
        } else {
            return UnDefType.NULL;
        }
    }

    public State getCalculatedPosition() {
        return calculatedPosition.getLatitude() != null ? OnOffType.ON : OnOffType.OFF;
    }

    public State getHeading() {
        return (position.getHeading() || calculatedPosition.getHeading()) ? OnOffType.ON : OnOffType.OFF;
    }

    private State getPositionAsState(Position position2) {
        if (position2.getLatitude() != null && position2.getLongitude() != null) {
            return new PointType(position2.getLatitude() + "," + position2.getLongitude());
        } else {
            return UnDefType.NULL;
        }
    }

    public State getLocationTimestamp() {
        if (position.getTimestamp() != null) {
            return new DateTimeType(position.getTimestamp().withZoneSameInstant(ZoneId.systemDefault()));
        } else if (calculatedPosition.getTimestamp() != null) {
            return new DateTimeType(calculatedPosition.getTimestamp().withZoneSameInstant(ZoneId.systemDefault()));
        } else {
            return UnDefType.NULL;
        }
    }
}
