/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.verisure.internal;

import java.math.BigDecimal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * A base JSON thing for other Verisure things to inherit.
 *
 * @author Jarle Hjortland - Initial contribution
 *
 */
@NonNullByDefault
public class VerisureBaseThingJSON implements VerisureThingJSON {

    @SerializedName("id")
    protected @Nullable String id;

    @SerializedName("name")
    protected @Nullable String name;

    @SerializedName("location")
    protected @Nullable String location;

    @SerializedName("status")
    protected @Nullable String status;

    protected @Nullable String siteName;

    protected @Nullable BigDecimal siteId;

    public VerisureBaseThingJSON() {
        super();
    }

    /**
     *
     * @return
     *         The status
     */
    public @Nullable String getStatus() {
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
    public @Nullable String getName() {
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
    public @Nullable String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    @Override
    public void setId(@Nullable String id) {
        this.id = id;
    }

    /**
     * @return the location
     */
    @Override
    public @Nullable String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    public @Nullable String getSiteName() {
        return siteName;
    }

    @Override
    public void setSiteName(@Nullable String siteName) {
        this.siteName = siteName;
    }

    @Override
    public @Nullable BigDecimal getSiteId() {
        return siteId;
    }

    @Override
    public void setSiteId(@Nullable BigDecimal siteId) {
        this.siteId = siteId;
    }

    @SuppressWarnings("null")
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
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof VerisureBaseThingJSON)) {
            return false;
        }

        VerisureBaseThingJSON other = (VerisureBaseThingJSON) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (id != null && !id.equals(other.id)) {
            return false;
        }

        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (name != null && !name.equals(other.name)) {
            return false;
        }

        if (location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (location != null && !location.equals(other.location)) {
            return false;
        }

        if (status == null) {
            if (other.status != null) {
                return false;
            }
        } else if (status != null && !status.equals(other.status)) {
            return false;
        }

        if (siteName == null) {
            if (other.siteName != null) {
                return false;
            }
        } else if (siteName != null && !siteName.equals(other.siteName)) {
            return false;
        }

        if (siteId == null) {
            if (other.siteId != null) {
                return false;
            }
        } else if (siteId != null && !siteId.equals(other.siteId)) {
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
            if (siteId != null) {
                builder.append(siteId.toString());
            }
        }
        builder.append("]");
        return builder.toString();
    }
}