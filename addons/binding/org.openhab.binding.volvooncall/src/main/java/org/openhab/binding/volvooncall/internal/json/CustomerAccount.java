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
 * The {@link CustomerAccount} is responsible for storing
 * informations returned by customerAccount rest answer
 *
 * @author Gaël L'hopital - Initial contribution
 */
public class CustomerAccount {
    public final static String ENDPOINT = "customeraccounts";

    @SerializedName("accountVehicleRelations")
    private String[] accountVehicleRelationsURL;

    // Currently unused in the binding, maybe interesting in the future
    // private String username;
    // private String firstName;
    // private String lastName;
    // private String accountId;
    // private String account;

    public String[] getAccountVehicleRelationsURL() {
        return accountVehicleRelationsURL;
    }
}
