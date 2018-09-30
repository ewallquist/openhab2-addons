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
 * An sensor in the verisure system, normally smoke detectors.
 *
 * @author Jarle Hjortland
 *
 */
public class VerisureClimateBaseJSON extends VerisureBaseThingJSON {

    @SerializedName("temperatureBelowMinAlertValue")
    protected String temperatureBelowMinAlertValue;

    @SerializedName("temperatureAboveMaxAlertValue")
    protected String temperatureAboveMaxAlertValue;

    @SerializedName("temperature")
    protected String temperature;

    @SerializedName("plottable")
    protected Boolean plottable;

    @SerializedName("monitorable")
    protected Boolean monitorable;

    @SerializedName("humidity")
    protected String humidity;

    @SerializedName("humidityBelowMinAlertValue")
    protected String humidityBelowMinAlertValue;

    @SerializedName("humidityAboveMaxAlertValue")
    protected String humidityAboveMaxAlertValue;

    @SerializedName("type")
    protected String type;

    @SerializedName("timestamp")
    protected String timestamp;

    /**
     *
     * @return
     *         The temperatureBelowMinAlertValue
     */
    public String getTemperatureBelowMinAlertValue() {
        return temperatureBelowMinAlertValue;
    }

    /**
     *
     * @param temperatureBelowMinAlertValue
     *                                          The temperatureBelowMinAlertValue
     */
    public void setTemperatureBelowMinAlertValue(String temperatureBelowMinAlertValue) {
        this.temperatureBelowMinAlertValue = temperatureBelowMinAlertValue;
    }

    /**
     *
     * @return
     *         The temperatureAboveMaxAlertValue
     */
    public String getTemperatureAboveMaxAlertValue() {
        return temperatureAboveMaxAlertValue;
    }

    /**
     *
     * @param temperatureAboveMaxAlertValue
     *                                          The temperatureAboveMaxAlertValue
     */
    public void setTemperatureAboveMaxAlertValue(String temperatureAboveMaxAlertValue) {
        this.temperatureAboveMaxAlertValue = temperatureAboveMaxAlertValue;
    }

    /**
     *
     * @return
     *         The temperature
     */
    public String getTemperature() {
        return temperature;
    }

    /**
     *
     * @param temperature
     *                        The temperature
     */
    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    /**
     *
     * @return
     *         The plottable
     */
    public Boolean getPlottable() {
        return plottable;
    }

    /**
     *
     * @param plottable
     *                      The plottable
     */
    public void setPlottable(Boolean plottable) {
        this.plottable = plottable;
    }

    /**
     *
     * @return
     *         The monitorable
     */
    public Boolean getMonitorable() {
        return monitorable;
    }

    /**
     *
     * @param monitorable
     *                        The monitorable
     */
    public void setMonitorable(Boolean monitorable) {
        this.monitorable = monitorable;
    }

    /**
     *
     * @return
     *         The humidity
     */
    public String getHumidity() {
        return humidity;
    }

    /**
     *
     * @param humidity
     *                     The humidity
     */
    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    /**
     *
     * @return
     *         The humidityBelowMinAlertValue
     */
    public String getHumidityBelowMinAlertValue() {
        return humidityBelowMinAlertValue;
    }

    /**
     *
     * @param humidityBelowMinAlertValue
     *                                       The humidityBelowMinAlertValue
     */
    public void setHumidityBelowMinAlertValue(String humidityBelowMinAlertValue) {
        this.humidityBelowMinAlertValue = humidityBelowMinAlertValue;
    }

    /**
     *
     * @return
     *         The humidityAboveMaxAlertValue
     */
    public String getHumidityAboveMaxAlertValue() {
        return humidityAboveMaxAlertValue;
    }

    /**
     *
     * @param humidityAboveMaxAlertValue
     *                                       The humidityAboveMaxAlertValue
     */
    public void setHumidityAboveMaxAlertValue(String humidityAboveMaxAlertValue) {
        this.humidityAboveMaxAlertValue = humidityAboveMaxAlertValue;
    }

    /**
     *
     * @return
     *         The timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     *
     * @param timestamp
     *                      The timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     *
     * @return
     *         The type of climate sensor
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     *                 The type of cliemate sensor
     */
    public void setType(String type) {
        this.type = type;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((humidity == null) ? 0 : humidity.hashCode());
        result = prime * result + ((humidityAboveMaxAlertValue == null) ? 0 : humidityAboveMaxAlertValue.hashCode());
        result = prime * result + ((humidityBelowMinAlertValue == null) ? 0 : humidityBelowMinAlertValue.hashCode());
        result = prime * result + ((monitorable == null) ? 0 : monitorable.hashCode());
        result = prime * result + ((plottable == null) ? 0 : plottable.hashCode());
        result = prime * result + ((temperature == null) ? 0 : temperature.hashCode());
        result = prime * result
                + ((temperatureAboveMaxAlertValue == null) ? 0 : temperatureAboveMaxAlertValue.hashCode());
        result = prime * result
                + ((temperatureBelowMinAlertValue == null) ? 0 : temperatureBelowMinAlertValue.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
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

        if (!super.equals(obj)) {
            return false;
        }

        VerisureClimateBaseJSON other = (VerisureClimateBaseJSON) obj;
        if (humidity == null) {
            if (other.humidity != null) {
                return false;
            }
        } else if (!humidity.equals(other.humidity)) {
            return false;
        }
        if (humidityAboveMaxAlertValue == null) {
            if (other.humidityAboveMaxAlertValue != null) {
                return false;
            }
        } else if (!humidityAboveMaxAlertValue.equals(other.humidityAboveMaxAlertValue)) {
            return false;
        }
        if (humidityBelowMinAlertValue == null) {
            if (other.humidityBelowMinAlertValue != null) {
                return false;
            }
        } else if (!humidityBelowMinAlertValue.equals(other.humidityBelowMinAlertValue)) {
            return false;
        }
        if (monitorable == null) {
            if (other.monitorable != null) {
                return false;
            }
        } else if (!monitorable.equals(other.monitorable)) {
            return false;
        }
        if (plottable == null) {
            if (other.plottable != null) {
                return false;
            }
        } else if (!plottable.equals(other.plottable)) {
            return false;
        }
        if (temperature == null) {
            if (other.temperature != null) {
                return false;
            }
        } else if (!temperature.equals(other.temperature)) {
            return false;
        }
        if (temperatureAboveMaxAlertValue == null) {
            if (other.temperatureAboveMaxAlertValue != null) {
                return false;
            }
        } else if (!temperatureAboveMaxAlertValue.equals(other.temperatureAboveMaxAlertValue)) {
            return false;
        }
        if (temperatureBelowMinAlertValue == null) {
            if (other.temperatureBelowMinAlertValue != null) {
                return false;
            }
        } else if (!temperatureBelowMinAlertValue.equals(other.temperatureBelowMinAlertValue)) {
            return false;
        }
        if (timestamp == null) {
            if (other.timestamp != null) {
                return false;
            }
        } else if (!timestamp.equals(other.timestamp)) {
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
        builder.append("VerisureClimateBaseJSON [");
        if (temperature != null) {
            builder.append("temperature=");
            builder.append(temperature);
            builder.append(", ");
        }
        if (humidity != null) {
            builder.append("humidity=");
            builder.append(humidity);
            builder.append(", ");
        }
        if (timestamp != null) {
            builder.append("timestamp=");
            builder.append(timestamp);
            builder.append(", ");
        }
        if (type != null) {
            builder.append("type=");
            builder.append(type);
            builder.append(", ");
        }
        builder.append("]");
        return super.toString() + "\n" + builder.toString();
    }
}
