/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.volvooncall.internal.json;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link Vehicle} is responsible for storing
 * informations returned by vehicule rest answer
 *
 * @author Gaël L'hopital - Initial contribution
 */
public class Vehicle {
    public static final String ENDPOINT = "vehicles/";

    @SerializedName("attributes")
    private String attributesURL;
    @SerializedName("status")
    private String statusURL;

    // Currently unused in the binding, maybe interesting in the future
    // private String[] vehicleAccountRelations;
    // private String vehicleId;

    public String getAttributesURL() {
        return attributesURL;
    }

    public String getStatusURL() {
        return statusURL;
    }
}
