/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.smhiweather.handler;

import static org.openhab.binding.smhiweather.SmhiWeatherBindingConstants.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

//import org.eclipse.smarthome.binding.smhiweather.internal.ExpiringCache;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.eclipse.smarthome.io.net.http.HttpUtil;
import org.openhab.binding.smhiweather.internal.SmhiDataListV2JSON;
import org.openhab.binding.smhiweather.internal.SmhiDataListV2JSON.WeatherDataV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * The {@link SmhiWeatherHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Jan Gustafsson - Initial contribution
 */
public class SmhiWeatherHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(SmhiWeatherHandler.class);
    private static final String LOCATION_LONG = "longitude";
    private static final String LOCATION_LAT = "latitude";

    private BigDecimal refresh;

    // Keeps track of the actual weather
    private WeatherDataV2 weatherData = null;

    // URL to SMHI pmp2g public REST API
    // http://opendata.smhi.se/apidocs/metfcst/parameters.html
    protected static final String URL = "http://opendata-download-metfcst.smhi.se/api/category/pmp2g/version/2/geotype/point/lon/%s/lat/%s/data.json";

    // Timeout for weather data requests.
    private static final int SMHI_TIMEOUT = 5000;

    // Are optionally read from openhab.cfg
    private double homeLatitude = 0.0;
    private double homeLongitude = 0.0;

    private Gson gson = new GsonBuilder().create();

    ScheduledFuture<?> refreshJob;

    public SmhiWeatherHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing SMHI weather handler.");

        Configuration config = getThing().getConfiguration();

        homeLongitude = ((BigDecimal) config.get(LOCATION_LONG)).doubleValue();
        homeLatitude = ((BigDecimal) config.get(LOCATION_LAT)).doubleValue();

        try {
            refresh = (BigDecimal) config.get("refresh");
        } catch (Exception e) {
            logger.debug("Cannot set refresh parameter.", e);
        }

        if (refresh == null) {
            // let's go for the default
            refresh = new BigDecimal(60);
        }

        startAutomaticRefresh();

        updateStatus(ThingStatus.ONLINE);

    }

    @Override
    public void dispose() {
        refreshJob.cancel(true);
    }

    private void startAutomaticRefresh() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    boolean success = updateWeatherData();
                    if (success) {
                        updateState(new ChannelUID(getThing().getUID(), CHANNEL_TEMPERATURE), getTemperature());
                        updateState(new ChannelUID(getThing().getUID(), CHANNEL_HUMIDITY), getHumidity());
                        updateState(new ChannelUID(getThing().getUID(), CHANNEL_PRESSURE), getPressure());
                        updateState(new ChannelUID(getThing().getUID(), CHANNEL_WIND_SPEED), getWindSpeed());
                        updateState(new ChannelUID(getThing().getUID(), CHANNEL_WIND_DIRECTION), getWindDirection());
                        updateState(new ChannelUID(getThing().getUID(), CHANNEL_THUNDER_PROBABILITY),
                                getThunderProbability());
                        updateState(new ChannelUID(getThing().getUID(), CHANNEL_WEATHER_SYMBOL), getWeatherSymbol());
                    }
                } catch (Exception e) {
                    logger.debug("Exception occurred during execution: {}", e.getMessage(), e);
                }
            }
        };

        refreshJob = scheduler.scheduleAtFixedRate(runnable, 0, refresh.intValue(), TimeUnit.SECONDS);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        // TODO: handle command

        // Note: if communication with thing fails for some reason,
        // indicate that by setting the status with detail information
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
        // "Could not control device at IP address x.x.x.x");

        if (command instanceof RefreshType) {
            boolean success = updateWeatherData();
            if (success) {
                switch (channelUID.getId()) {
                    case CHANNEL_TEMPERATURE:
                        updateState(channelUID, getTemperature());
                        break;
                    case CHANNEL_HUMIDITY:
                        updateState(channelUID, getHumidity());
                        break;
                    case CHANNEL_PRESSURE:
                        updateState(channelUID, getPressure());
                        break;
                    case CHANNEL_WIND_SPEED:
                        updateState(channelUID, getWindSpeed());
                        break;
                    case CHANNEL_WIND_DIRECTION:
                        updateState(channelUID, getWindDirection());
                        break;
                    case CHANNEL_THUNDER_PROBABILITY:
                        updateState(channelUID, getThunderProbability());
                        break;
                    case CHANNEL_WEATHER_SYMBOL:
                        updateState(channelUID, getWeatherSymbol());
                        break;
                    default:
                        logger.debug("Command received for an unknown channel: {}", channelUID.getId());
                        break;
                }
            } else {
                logger.warn("Update of weather data failed!");
            }
        } else {
            logger.debug("Command {} is not supported for channel: {}", command, channelUID.getId());
        }
    }

    private synchronized boolean updateWeatherData() {
        SmhiDataListV2JSON data = executeQuery(homeLongitude, homeLatitude);
        if (data != null) {
            // Find in time matching time serie
            Date now = new Date();
            List<WeatherDataV2> timeSeries = data.getTimeSeries();
            while (timeSeries.get(0).getValidTime().before(now) && timeSeries.size() > 1) {
                timeSeries.remove(0);
            }

            // Fetch actual data serie
            weatherData = timeSeries.get(0);
            // Populate values
            weatherData.processData();
            updateStatus(ThingStatus.ONLINE);
            return true;
        } else {
            logger.warn("Error accessing SMHI weather");
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR,
                    "Error accessing SMHI REST API");
            return false;
        }
    }

    private SmhiDataListV2JSON executeQuery(double longitude, double latitude) {
        // SMHI API only supports 6 digits in API call
        DecimalFormat df = new DecimalFormat("##.######");
        DecimalFormatSymbols custom = new DecimalFormatSymbols();
        custom.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(custom);
        String apiRequest = String.format(URL, df.format(longitude), df.format(latitude));

        String apiResponseJson = null;
        SmhiDataListV2JSON dataList = null;

        try {
            apiResponseJson = HttpUtil.executeUrl("GET", apiRequest, null, null, "application/json", SMHI_TIMEOUT);
            logger.debug("Quering SMHI API: " + apiRequest);
            dataList = gson.fromJson(apiResponseJson, SmhiDataListV2JSON.class);
        } catch (final Exception e) {
            logger.error("'Exception trace:'" + e.toString());
        }
        return dataList;
    }

    private State getHumidity() {
        if (weatherData != null) {
            DecimalType humidity = new DecimalType(weatherData.getHumidity());
            if (humidity != null) {
                return humidity;
            }
        }
        return UnDefType.UNDEF;
    }

    private State getPressure() {
        if (weatherData != null) {
            DecimalType pressure = new DecimalType(weatherData.getPressure());
            if (pressure != null) {
                return pressure;
            }
        }
        return UnDefType.UNDEF;
    }

    private State getTemperature() {
        if (weatherData != null) {
            DecimalType temp = new DecimalType(weatherData.getTemperature());
            if (temp != null) {
                return temp;
            }
        }
        return UnDefType.UNDEF;
    }

    private State getWindSpeed() {
        if (weatherData != null) {
            DecimalType windSpeed = new DecimalType(weatherData.getWindVelocity());
            if (windSpeed != null) {
                return windSpeed;
            }
        }
        return UnDefType.UNDEF;
    }

    private State getWindDirection() {
        if (weatherData != null) {
            DecimalType windDirection = new DecimalType(weatherData.getWindDirection());
            if (windDirection != null) {
                return windDirection;
            }
        }
        return UnDefType.UNDEF;
    }

    private State getThunderProbability() {
        if (weatherData != null) {
            DecimalType thunderProbability = new DecimalType(weatherData.getProbabilityThunderstorm());
            if (thunderProbability != null) {
                return thunderProbability;
            }
        }
        return UnDefType.UNDEF;
    }

    private State getWeatherSymbol() {
        if (weatherData != null) {
            DecimalType weatherSymbol = new DecimalType(weatherData.getWeatherSymbol());
            if (weatherSymbol != null) {
                return weatherSymbol;
            }
        }
        return UnDefType.UNDEF;
    }
}
