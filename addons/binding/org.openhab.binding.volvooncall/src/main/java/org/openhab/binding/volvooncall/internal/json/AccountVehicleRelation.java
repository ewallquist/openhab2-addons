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
 * The {@link AccountVehicleRelation} is responsible for storing
 * informations returned by Vehicule Account Relations rest answer
 *
 * @author Gaël L'hopital - Initial contribution
 */
public class AccountVehicleRelation {
    @SerializedName("vehicle")
    private String vehicleURL;
    private String vehicleId;

    // Currently unused in the binding, maybe interesting in the future
    // private String account;
    // private String username;
    // private String status;
    // private Integer customerVehicleRelationId;
    // private String accountId;
    // private String accountVehicleRelation;

    public String getVehicleId() {
        return vehicleId;
    }

    public String getVehicleURL() {
        return vehicleURL;
    }
}
