/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.verisure.internal;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

/**
 * A base JSON thing for other Verisure things to inherit.
 *
 * @author Jarle Hjortland
 *
 */
public class VerisureBaseThingJSON implements VerisureThingJSON {

    @SerializedName("id")
    protected String id;

    @SerializedName("name")
    protected String name;

    @SerializedName("location")
    protected String location;

    @SerializedName("status")
    protected String status;

    protected String siteName;

    protected BigDecimal siteId;

    public VerisureBaseThingJSON() {
        super();
    }

    /**
     *
     * @return
     *         The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     *                   The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the location
     */
    @Override
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    public String getSiteName() {
        return siteName;
    }

    @Override
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    @Override
    public BigDecimal getSiteId() {
        return siteId;
    }

    @Override
    public void setSiteId(BigDecimal siteId) {
        this.siteId = siteId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((siteName == null) ? 0 : siteName.hashCode());
        result = prime * result + ((siteId == null) ? 0 : siteId.hashCode());
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
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        VerisureBaseThingJSON other = (VerisureBaseThingJSON) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }

        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }

        if (location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!location.equals(other.location)) {
            return false;
        }

        if (status == null) {
            if (other.status != null) {
                return false;
            }
        } else if (!status.equals(other.status)) {
            return false;
        }

        if (siteName == null) {
            if (other.siteName != null) {
                return false;
            }
        } else if (!siteName.equals(other.siteName)) {
            return false;
        }

        if (siteId == null) {
            if (other.siteId != null) {
                return false;
            }
        } else if (!siteId.equals(other.siteId)) {
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
        builder.append("VerisureBaseThingJSON [");
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
        if (location != null) {
            builder.append("location=");
            builder.append(location);
            builder.append(", ");
        }
        if (status != null) {
            builder.append("status=");
            builder.append(status);
        }
        if (siteName != null) {
            builder.append("siteName=");
            builder.append(siteName);
        }
        if (siteId != null) {
            builder.append("siteId=");
            builder.append(siteId.toString());
        }
        builder.append("]");
        return builder.toString();
    }
}