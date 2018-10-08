/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.verisure.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * The status of a door or window.
 *
 * @author Jarle Hjortland - Initial contribution
 *
 */
@NonNullByDefault
public class VerisureDoorWindowsJSON extends VerisureBaseThingJSON {

    @SerializedName("area")
    private @Nullable String area;

    @SerializedName("state")
    private @Nullable String state;

    @SerializedName("deviceLabel")
    private @Nullable String deviceLabel;

    public VerisureDoorWindowsJSON(String id, String state, String location) {
        super();
        this.area = location;
        this.state = state;
        this.deviceLabel = id;
    }

    public @Nullable String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public @Nullable String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public @Nullable String getDeviceLabel() {
        return deviceLabel;
    }

    public void setDeviceLabel(String deviceLabel) {
        this.area = deviceLabel;
    }

    @Override
    public @Nullable String getLocation() {
        return area;
    }

    @Override
    public void setId(@Nullable String id) {
        this.deviceLabel = id;
    }

    @Override
    public @Nullable String getId() {
        return deviceLabel;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @SuppressWarnings("null")
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((area == null) ? 0 : area.hashCode());
        result = prime * result + ((deviceLabel == null) ? 0 : deviceLabel.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(@Nullable Object obj) {

        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof VerisureDoorWindowsJSON)) {
            return false;
        }

        VerisureDoorWindowsJSON other = (VerisureDoorWindowsJSON) obj;
        if (area == null) {
            if (other.area != null) {
                return false;
            }
        } else if (area != null && !area.equals(other.area)) {
            return false;
        }
        if (deviceLabel == null) {
            if (other.deviceLabel != null) {
                return false;
            }
        } else if (deviceLabel != null && !deviceLabel.equals(other.deviceLabel)) {
            return false;
        }
        if (state == null) {
            if (other.state != null) {
                return false;
            }
        } else if (state != null && !state.equals(other.state)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("VerisureDoorWindowsJSON [");
        if (area != null) {
            builder.append("area=");
            builder.append(area);
            builder.append(", ");
        }
        if (state != null) {
            builder.append("state=");
            builder.append(state);
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
