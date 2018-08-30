/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.smhiweather;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link smhiweatherBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Jan Gustafsson - Initial contribution
 */
public class SmhiWeatherBindingConstants {

    public static final String BINDING_ID = "smhiweather";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_WEATHER = new ThingTypeUID(BINDING_ID, "weather");

    // List of all Channel ids
    public static final String CHANNEL_TEMPERATURE = "temperature";
    public static final String CHANNEL_HUMIDITY = "humidity";
    public static final String CHANNEL_PRESSURE = "pressure";
    public static final String CHANNEL_WIND_SPEED = "wind_speed";
    public static final String CHANNEL_WIND_DIRECTION = "wind_direction";
    public static final String CHANNEL_THUNDER_PROBABILITY = "thunder_probability";
    public static final String CHANNEL_WEATHER_SYMBOL = "weather_symbol";

    public static final String PARAMETER_TEMPERATURE = "temperature";
    public static final String PARAMETER_THUNDERSTORM = "probability_thunderstorm";
    public static final String PARAMETER_PRESSURE = "pressure";
    public static final String PARAMETER_VISIBILITY = "visibility";
    public static final String PARAMETER_WIND_DIRECTION = "wind_direction";
    public static final String PARAMETER_WIND_VELOCITY = "wind_velocity";
    public static final String PARAMETER_WIND_GUST = "gust";
    public static final String PARAMETER_HUMIDITY = "humidity";
    public static final String PARAMETER_TOTAL_CLOUD_COVER = "total_cloud_cover";
    public static final String PARAMETER_HIGH_CLOUD_COVER = "high_cloud_cover";
    public static final String PARAMETER_MEDIUM_CLOUD_COVER = "medium_cloud_cover";
    public static final String PARAMETER_LOW_CLOUD_COVER = "low_cloud_cover";
    public static final String PARAMETER_MAX_PRECIPITATION = "max_precipitation";
    public static final String PARAMETER_MIN_PRECIPITATION = "min_precipitation";
    public static final String PARAMETER_FROZEN_PRECIPITATION = "froozen_precipitation";
    public static final String PARAMETER_PRECIPITATION_CATEGORY = "precipitation_category";
    public static final String PARAMETER_MEAN_PRECIPITATION = "mean_precipitation";
    public static final String PARAMETER_MEDIAN_PRECIPITATION = "median_precipitation";
    public static final String PARAMETER_WEATHER_SYMBOL = "weather_symbol";
    public static final String PARAMETER_TEMPERATURE_JSON = "t";
    public static final String PARAMETER_THUNDERSTORM_JSON = "tstm";
    public static final String PARAMETER_PRESSURE_JSON = "msl";
    public static final String PARAMETER_VISIBILITY_JSON = "vis";
    public static final String PARAMETER_WIND_DIRECTION_JSON = "wd";
    public static final String PARAMETER_WIND_VELOCITY_JSON = "ws";
    public static final String PARAMETER_WIND_GUST_JSON = "gust";
    public static final String PARAMETER_HUMIDITY_JSON = "r";
    public static final String PARAMETER_TOTAL_CLOUD_COVER_JSON = "tcc_mean";
    public static final String PARAMETER_HIGH_CLOUD_COVER_JSON = "hcc_mean";
    public static final String PARAMETER_MEDIUM_CLOUD_COVER_JSON = "mcc_mean";
    public static final String PARAMETER_LOW_CLOUDS_JSON = "lcc_mean";
    public static final String PARAMETER_MAX_PRECIPITATION_JSON = "pmax";
    public static final String PARAMETER_MIN_PRECIPITATION_JSON = "pmin";
    public static final String PARAMETER_FROZEN_PRECIPITATION_JSON = "spp";
    public static final String PARAMETER_PRECIPITATION_CATEGORY_JSON = "pcat";
    public static final String PARAMETER_MEAN_PRECIPITATION_JSON = "pmean";
    public static final String PARAMETER_MEDIAN_PRECIPITATION_JSON = "pmedian";
    public static final String PARAMETER_WEATHER_SYMBOL_JSON = "Wsymb";

}
