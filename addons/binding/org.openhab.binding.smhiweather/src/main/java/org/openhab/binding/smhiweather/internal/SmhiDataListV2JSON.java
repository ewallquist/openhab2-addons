package org.openhab.binding.smhiweather.internal;

import static org.openhab.binding.smhiweather.SmhiWeatherBindingConstants.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openhab.binding.smhiweather.SmhiWeatherBindingConstants;

import com.google.gson.annotations.SerializedName;

/*
 * Implements JSON for SMHI Open REST API pmp2g
 * http://opendata.smhi.se/apidocs/metfcst/parameters.html
 */
public class SmhiDataListV2JSON {

    /**
     * approvedTime: "2016-01-18T16:25:14Z"
     */
    @SerializedName("approvedTime")
    private Date approvedTime;

    /**
     * referenceTime: "2016-01-18T14:00:00Z"
     */
    @SerializedName("referenceTime")
    private Date referenceTime;

    /**
     * geometry: {
     * type: "Point",
     * coordinates: [
     * [
     * 16.017767,
     * 57.999628
     * ]
     * ]
     * }
     */
    @SerializedName("geometry")
    private Geometry geometry;

    /**
     * timeSeries: [
     * {
     * validTime: "2016-01-21T15:00:00Z",
     * parameters: [
     * {
     * name: "msl",
     * levelType: "hmsl",
     * level: 0,
     * unit: "hPa",
     * values: [
     * 1025
     * ]
     * },
     * }
     *
     * @return
     */
    @SerializedName("timeSeries")
    private List<WeatherDataV2> timeSeries;

    public Date getApprovedTime() {
        return this.approvedTime;
    }

    public Date getReferenceTime() {
        return this.referenceTime;
    }

    public Geometry getGeometry() {
        return this.geometry;
    }

    public List<WeatherDataV2> getTimeSeries() {
        return timeSeries;
    }

    public class Geometry {
        private String type;
        private List<List<Double>> coordinates;

        public Geometry() {

        }

        public Geometry(List<List<Double>> coordinates) {
            super();
            this.coordinates = coordinates;
        }

        /**
         * type: "Point"
         */
        @SerializedName("type")
        public String getType() {
            return this.type;
        }

        /**
         * coordinates: [
         * [
         * 16.017767,
         * 57.999628
         * ]
         * ]
         */
        @SerializedName("coordinates")
        public List<List<Double>> getCoordinates() {
            return this.coordinates;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((coordinates == null) ? 0 : coordinates.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Geometry other = (Geometry) obj;
            if (coordinates == null) {
                if (other.coordinates != null) {
                    return false;
                }
            } else if (!coordinates.equals(other.coordinates)) {
                return false;
            }
            return true;
        }
    }

    public static class WeatherDataV2 {

        /**
         * validTime: "2016-01-18T16:25:14Z"
         */
        @SerializedName("validTime")
        private Date validTime;

        /**
         * parameters: [
         * {
         * name: "msl",
         * levelType: "hmsl",
         * level: 0,
         * unit: "hPa",
         * values: [
         * 1025
         * ]
         * },
         * }
         *
         * @return
         */
        @SerializedName("parameters")
        private List<Parameter> parameters;

        private HashMap<String, Parameter> hashParameters = new HashMap<String, Parameter>();

        /**
         * {
         * name: "t",
         * levelType: "hl",
         * level: 2,
         * unit: "Cel",
         * values: [
         * -8.8
         * ]
         * }
         *
         * @return
         */
        public double getTemperature() {
            return hashParameters.get(PARAMETER_TEMPERATURE_JSON).getValues().get(0);
        }

        /**
         * {
         * name: "tstm",
         * levelType: "hl",
         * level: 0,
         * unit: "fraction",
         * values: [
         * 0
         * ]
         * }
         *
         * @return
         */
        public int getProbabilityThunderstorm() {
            return hashParameters.get(PARAMETER_THUNDERSTORM_JSON).getValues().get(0).intValue();
        }

        /**
         * {
         * name: "tcc_mean",
         * levelType: "hl",
         * level: 0,
         * unit: "octas",
         * values: [
         * 4
         * ]
         * }
         *
         * @return
         */
        public int getTotalCloudCover() {
            return hashParameters.get(PARAMETER_TOTAL_CLOUD_COVER_JSON).getValues().get(0).intValue();
        }

        /**
         * value between 0-8.
         * {
         * name: "lcc_mean",
         * levelType: "hl",
         * level: 0,
         * unit: "octas",
         * values: [
         * 4
         * ]
         * }
         *
         * @return
         */
        public int getLowCloudCover() {
            return hashParameters.get(SmhiWeatherBindingConstants.PARAMETER_LOW_CLOUDS_JSON).getValues().get(0)
                    .intValue();
        }

        /**
         * value between 0-8.
         * {
         * name: "mcc_mean",
         * levelType: "hl",
         * level: 0,
         * unit: "octas",
         * values: [
         * 0
         * ]
         * }
         *
         * @return
         */
        public int getMediumCloudCover() {
            return hashParameters.get(SmhiWeatherBindingConstants.PARAMETER_MEDIUM_CLOUD_COVER_JSON).getValues().get(0)
                    .intValue();
        }

        /**
         * value between 0-8.
         * {
         * name: "hcc_mean",
         * levelType: "hl",
         * level: 0,
         * unit: "octas",
         * values: [
         * 0
         * ]
         * }
         *
         * @return
         */
        public int getHighCloudCover() {
            return hashParameters.get(SmhiWeatherBindingConstants.PARAMETER_HIGH_CLOUD_COVER_JSON).getValues().get(0)
                    .intValue();
        }

        /**
         * {
         * name: "r",
         * levelType: "hl",
         * level: 2,
         * unit: "%",
         * values: [
         * 92
         * ]
         * }
         *
         * @return
         */
        public int getHumidity() {
            return hashParameters.get(SmhiWeatherBindingConstants.PARAMETER_HUMIDITY_JSON).getValues().get(0)
                    .intValue();
        }

        /**
         * {
         * name: "pmax",
         * levelType: "hl",
         * level: 0,
         * unit: "kg/m2/h",
         * values: [
         * 0
         * ]
         * }
         *
         * @return
         */
        public double getMaxPrecipitation() {
            return hashParameters.get(SmhiWeatherBindingConstants.PARAMETER_MAX_PRECIPITATION_JSON).getValues().get(0);
        }

        /**
         * {
         * name: "pmin",
         * levelType: "hl",
         * level: 0,
         * unit: "kg/m2/h",
         * values: [
         * 0
         * ]
         * }
         *
         * @return
         */
        public double getMinPrecipitation() {
            return hashParameters.get(SmhiWeatherBindingConstants.PARAMETER_MIN_PRECIPITATION_JSON).getValues().get(0);
        }

        /**
         * {
         * name: "pmean",
         * levelType: "hl",
         * level: 0,
         * unit: "kg/m2/h",
         * values: [
         * 0
         * ]
         * }
         *
         * @return
         */
        public double getMeanPrecipitation() {
            return hashParameters.get(SmhiWeatherBindingConstants.PARAMETER_MEAN_PRECIPITATION_JSON).getValues().get(0);
        }

        /**
         * {
         * name: "pmedian",
         * levelType: "hl",
         * level: 0,
         * unit: "kg/m2/h",
         * values: [
         * 0
         * ]
         * }
         *
         * @return
         */
        public double getMedianPrecipitation() {
            return hashParameters.get(SmhiWeatherBindingConstants.PARAMETER_MEDIAN_PRECIPITATION_JSON).getValues()
                    .get(0);
        }

        /**
         * {
         * name: "pcat",
         * levelType: "hl",
         * level: 0,
         * unit: "category",
         * values: [
         * 0
         * ]
         * }
         *
         * @return
         */
        public int getPrecipitationCategory() {
            return hashParameters.get(SmhiWeatherBindingConstants.PARAMETER_PRECIPITATION_CATEGORY_JSON).getValues()
                    .get(0).intValue();
        }

        /**
         * {
         * name: "spp",
         * levelType: "hl",
         * level: 0,
         * unit: "fraction",
         * values: [
         * -9
         * ]
         * }
         *
         * @return
         */
        public double getFroozenPrecipitation() {
            return hashParameters.get(SmhiWeatherBindingConstants.PARAMETER_FROZEN_PRECIPITATION_JSON).getValues()
                    .get(0);
        }

        /**
         * {
         * name: "msl",
         * levelType: "hmsl",
         * level: 0,
         * unit: "hPa",
         * values: [
         * 1025
         * ]
         * }
         *
         * @return
         */
        public double getPressure() {
            return hashParameters.get(SmhiWeatherBindingConstants.PARAMETER_PRESSURE_JSON).getValues().get(0);
        }

        /**
         * {
         * name: "vis",
         * levelType: "hl",
         * level: 2,
         * unit: "km",
         * values: [
         * 6
         * ]
         * }
         *
         * @return
         */
        public double getVisibility() {
            return hashParameters.get(SmhiWeatherBindingConstants.PARAMETER_VISIBILITY_JSON).getValues().get(0);
        }

        /**
         * {
         * name: "wd",
         * levelType: "hl",
         * level: 10,
         * unit: "degree",
         * values: [
         * 293
         * ]
         * }
         *
         * @return
         */
        public int getWindDirection() {
            return hashParameters.get(SmhiWeatherBindingConstants.PARAMETER_WIND_DIRECTION_JSON).getValues().get(0)
                    .intValue();
        }

        /**
         * {
         * name: "gust",
         * levelType: "hl",
         * level: 10,
         * unit: "m/s",
         * values: [
         * 3
         * ]
         * }
         *
         * @return
         */
        public double getWindGust() {
            return hashParameters.get(SmhiWeatherBindingConstants.PARAMETER_WIND_GUST_JSON).getValues().get(0);
        }

        /**
         * {
         * name: "ws",
         * levelType: "hl",
         * level: 10,
         * unit: "m/s",
         * values: [
         * 1.6
         * ]
         * }
         *
         * @return
         */
        public double getWindVelocity() {
            return hashParameters.get(SmhiWeatherBindingConstants.PARAMETER_WIND_VELOCITY_JSON).getValues().get(0);
        }

        /**
         * {"name" : "Wsymb",
         * "levelType" : "hl",
         * "level" : 0,
         * "unit" : "category",
         * "values" : -[4
         * ]
         * }
         *
         * @return
         */
        public int getWeatherSymbol() {
            return hashParameters.get(PARAMETER_WEATHER_SYMBOL_JSON).getValues().get(0).intValue();
        }

        public Date getValidTime() {
            return this.validTime;
        }

        public List<Parameter> getParameters() {
            return this.parameters;
        }

        public void processData() {
            for (Parameter parameter : this.parameters) {
                hashParameters.put(parameter.name, parameter);
            }
        }

    }

    public static class Parameter {
        /**
         * name: "t"
         */
        @SerializedName("name")
        public String name;

        /**
         * levelType: "hl"
         */
        @SerializedName("levelType")
        private String levelType;

        /**
         * levelType: 2
         */
        @SerializedName("level")
        private int level;

        /**
         * unit: "Cel"
         */
        @SerializedName("unit")
        private String unit;

        /**
         * values: [
         * -4.2
         * ]
         */
        @SerializedName("values")
        private List<Double> values;

        public String getName() {
            return this.name;
        }

        public String getLevelType() {
            return this.levelType;
        }

        public int getLevel() {
            return this.level;
        }

        public String getUnit() {
            return this.unit;
        }

        public List<Double> getValues() {
            return this.values;
        }

    }
}
