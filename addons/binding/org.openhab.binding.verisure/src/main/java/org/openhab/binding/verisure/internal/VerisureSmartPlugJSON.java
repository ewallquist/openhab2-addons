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
 * A Verisure SmartPlug.
 *
 * @author Jarle Hjortland
 *
 */
public class VerisureSmartPlugJSON extends VerisureBaseThingJSON {

    @SerializedName("statusText")
    private String statusText;

    @SerializedName("hazardous")
    private Boolean hazardous;

    @SerializedName("deviceLabel")
    private String deviceLabel;

    public VerisureSmartPlugJSON(String id, String location, String status, String statusText, Boolean hazardous) {
        super();
        this.location = location;
        this.status = status;
        this.statusText = statusText;
        this.hazardous = hazardous;
        this.deviceLabel = id;
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
        result = prime * result + ((deviceLabel == null) ? 0 : deviceLabel.hashCode());
        result = prime * result + ((statusText == null) ? 0 : statusText.hashCode());
        result = prime * result + ((hazardous == null) ? 0 : hazardous.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (!super.equals(obj)) {
            return false;
        }

        VerisureSmartPlugJSON other = (VerisureSmartPlugJSON) obj;
        if (deviceLabel == null) {
            if (other.deviceLabel != null) {
                return false;
            }
        } else if (!deviceLabel.equals(other.deviceLabel)) {
            return false;
        }
        if (statusText == null) {
            if (other.statusText != null) {
                return false;
            }
        } else if (!statusText.equals(other.statusText)) {
            return false;
        }
        if (hazardous == null) {
            if (other.hazardous != null) {
                return false;
            }
        } else if (!hazardous.equals(other.hazardous)) {
            return false;
        }

        return true;
    }

    @Override
    public String getId() {
        return deviceLabel;
    }

    @Override
    public void setId(String id) {
        this.deviceLabel = id;
    }

    public Boolean getHazardous() {
        return hazardous;
    }

    public void setHazardous(Boolean hazardous) {
        this.hazardous = hazardous;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.status = statusText;
    }

    public String getDeviceLabel() {
        return deviceLabel;
    }

    public void setDeviceLabel(String deviceLabel) {
        this.deviceLabel = deviceLabel;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("VerisureSmartPlugJSON [");
        if (statusText != null) {
            builder.append("statusText=");
            builder.append(statusText);
            builder.append(", ");
        }
        if (hazardous != null) {
            builder.append("hazardous=");
            builder.append(hazardous);
            builder.append(", ");
        }
        if (deviceLabel != null) {
            builder.append("deviceLabel=");
            builder.append(deviceLabel);
        }
        builder.append("]");
        return super.toString() + "\n" + builder.toString();
    }

}
