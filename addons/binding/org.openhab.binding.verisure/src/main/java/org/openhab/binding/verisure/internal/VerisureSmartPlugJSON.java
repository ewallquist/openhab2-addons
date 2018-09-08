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
public class VerisureSmartPlugJSON implements VerisureObjectJSON {

    @SerializedName("location")
    private String location;

    @SerializedName("status")
    private String status;

    @SerializedName("statusText")
    private String statusText;

    @SerializedName("hazardous")
    private String hazardous;

    @SerializedName("deviceLabel")
    private String deviceLabel;

    public VerisureSmartPlugJSON(String id, String location, String status, String statusText, String hazardous) {
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
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((deviceLabel == null) ? 0 : deviceLabel.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof VerisureSmartPlugJSON)) {
            return false;
        }
        VerisureSmartPlugJSON other = (VerisureSmartPlugJSON) obj;
        if (location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!location.equals(other.location)) {
            return false;
        }
        if (deviceLabel == null) {
            if (other.deviceLabel != null) {
                return false;
            }
        } else if (!deviceLabel.equals(other.deviceLabel)) {
            return false;
        }
        if (status == null) {
            if (other.status != null) {
                return false;
            }
        } else if (!status.equals(other.status)) {
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

    public String getHazardous() {
        return hazardous;
    }

    public void setHazardous(String hazardous) {
        this.status = hazardous;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.status = statusText;
    }

    @Override
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        if (location != null) {
            builder.append("location=");
            builder.append(location);
            builder.append(", ");
        }
        if (status != null) {
            builder.append("status=");
            builder.append(status);
            builder.append(", ");
        }
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
        return builder.toString();
    }

}
