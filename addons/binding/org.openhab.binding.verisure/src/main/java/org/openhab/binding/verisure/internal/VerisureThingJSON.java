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

/**
 * The base identifer of all Verisure response objects.
 *
 * @author Jarle Hjortland - Initial contribution
 *
 */
@NonNullByDefault
public interface VerisureThingJSON {
    public @Nullable String getId();

    public void setId(@Nullable String id);

    public @Nullable String getLocation();

    public void setSiteName(@Nullable String siteName);

    public void setSiteId(@Nullable BigDecimal siteId);

    public @Nullable BigDecimal getSiteId();

}