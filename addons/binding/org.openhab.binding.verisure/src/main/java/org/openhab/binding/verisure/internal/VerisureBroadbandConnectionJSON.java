/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.verisure.internal;

import com.google.gson.annotations.SerializedName;

/**
 * THe Broadband connection in Verisure.
 *
 * @author Jan Gustafsson
 *
 */
public class VerisureBroadbandConnectionJSON implements VerisureObjectJSON {
    @SerializedName("date")
    private String date;

    @SerializedName("hasWifi")
    private Boolean hasWifi;

    @SerializedName("status")
    private String status;

    private String id;
    private String location;

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return broadband status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status of broadband connection
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return WiFi status
     */
    public Boolean hasWiFi() {
        return hasWifi;
    }

    /**
     * @param hasWiFi
     */
    public void setHasWifi(Boolean hasWifi) {
        this.hasWifi = hasWifi;
    }

    @Override
    public String getId() {
        return status;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getLocation() {
        return location;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((hasWifi == null) ? 0 : hasWifi.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof VerisureBroadbandConnectionJSON)) {
            return false;
        }
        VerisureBroadbandConnectionJSON other = (VerisureBroadbandConnectionJSON) obj;
        if (status == null) {
            if (other.status != null) {
                return false;
            }
        } else if (!status.equals(other.status)) {
            return false;
        }
        if (hasWifi == null) {
            if (other.hasWifi != null) {
                return false;
            }
        } else if (!hasWifi.equals(other.hasWifi)) {
            return false;
        }
        if (date == null) {
            if (other.date != null) {
                return false;
            }
        } else if (!date.equals(other.date)) {
            return false;
        }
        return true;
    }
}
