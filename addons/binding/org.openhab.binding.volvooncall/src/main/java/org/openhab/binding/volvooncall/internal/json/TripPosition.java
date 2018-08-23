/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.volvooncall.internal.json;

/**
 * The {@link TripPosition} is responsible for storing
 * informations of a trip start/stop point as returned
 * by trip rest answer
 *
 * @author Gaël L'hopital - Initial contribution
 */
public class TripPosition {
    private Double longitude;
    private Double latitude;
    // Currently unused in the binding, maybe interesting in the future
    // private String streetAddress;
    // private String postalCode;
    // private String city;
    // private String iSO2CountryCode;
    // private String region;

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }
}
