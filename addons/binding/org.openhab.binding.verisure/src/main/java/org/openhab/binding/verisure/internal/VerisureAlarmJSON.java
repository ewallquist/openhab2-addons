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
 * The Alarm state of the Verisure System.
 *
 * @author Jarle Hjortland
 *
 */
public class VerisureAlarmJSON extends VerisureBaseObjectJSON {

    @SerializedName("date")
    private String date;

    @SerializedName("notAllowedReason")
    private String notAllowedReason;

    @SerializedName("changeAllowed")
    private Boolean changeAllowed;

    @SerializedName("label")
    private String label;

    @SerializedName("type")
    private String type;

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
     * @return the notAllowedReason
     */
    public String getNotAllowedReason() {
        return notAllowedReason;
    }

    /**
     * @param notAllowedReason the notAllowedReason to set
     */
    public void setNotAllowedReason(String notAllowedReason) {
        this.notAllowedReason = notAllowedReason;
    }

    /**
     * @return the changeAllowed
     */
    public Boolean getChangeAllowed() {
        return changeAllowed;
    }

    /**
     * @param changeAllowed the changeAllowed to set
     */
    public void setChangeAllowed(Boolean changeAllowed) {
        this.changeAllowed = changeAllowed;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */ /**
         * @return the label
         */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((changeAllowed == null) ? 0 : changeAllowed.hashCode());
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((notAllowedReason == null) ? 0 : notAllowedReason.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        VerisureAlarmJSON other = (VerisureAlarmJSON) obj;
        if (changeAllowed == null) {
            if (other.changeAllowed != null) {
                return false;
            }
        } else if (!changeAllowed.equals(other.changeAllowed)) {
            return false;
        }
        if (date == null) {
            if (other.date != null) {
                return false;
            }
        } else if (!date.equals(other.date)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (label == null) {
            if (other.label != null) {
                return false;
            }
        } else if (!label.equals(other.label)) {
            return false;
        }
        if (location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!location.equals(other.location)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (notAllowedReason == null) {
            if (other.notAllowedReason != null) {
                return false;
            }
        } else if (!notAllowedReason.equals(other.notAllowedReason)) {
            return false;
        }
        if (status == null) {
            if (other.status != null) {
                return false;
            }
        } else if (!status.equals(other.status)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("VerisureAlarmJSON [");
        if (date != null) {
            builder.append("date=");
            builder.append(date);
            builder.append(", ");
        }
        if (name != null) {
            builder.append("name=");
            builder.append(name);
            builder.append(", ");
        }
        if (id != null) {
            builder.append("id=");
            builder.append(id);
            builder.append(", ");
        }
        if (label != null) {
            builder.append("label=");
            builder.append(label);
            builder.append(", ");
        }
        if (location != null) {
            builder.append("location=");
            builder.append(location);
            builder.append(", ");
        }
        if (status != null) {
            builder.append("status=");
            builder.append(status);
        }
        builder.append("]");
        return builder.toString();
    }

}
