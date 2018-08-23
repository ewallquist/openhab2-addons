/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.volvooncall.internal.json;

import java.util.List;

/**
 * The {@link Trip} is responsible for storing
 * trip informations returned by trip rest answer
 *
 * @author Gaël L'hopital - Initial contribution
 */
public class Trip {
    private Integer id;
    private List<TripDetail> tripDetails = null;
    // Currently unused in the binding, maybe interesting in the future
    // private String name;
    // private String category;
    // private String userNotes;
    // @SerializedName("trip")
    // private String tripURL;

    public Integer getId() {
        return id;
    }

    public List<TripDetail> getTripDetails() {
        return tripDetails;
    }

}
