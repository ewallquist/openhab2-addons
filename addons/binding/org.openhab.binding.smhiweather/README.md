##Smhi Weather Binding##

This is an OpenHAB binding for fetching weather data from SMHI supporting [SMHI API PMP2g](http://www.smhi.se/klimatdata/ladda-ner-data/api-for-pmp-dokumentation-1.76980). 

This binding is based on the work made by Mattias Markehed on https://github.com/ibaton/OpenHAB-binding-SMHI with some quite big code changes including support for latest API version and also
configuration for your home position.
The number of queries needed have also been optimized using cached results per position.

###Supported Things###

This binding supports the following thing types:

* Weather information from SMHI

###Binding Configuration###
You will have to configure your desired position (longitude/latitude). Latitude and latitude for your location can be found using [bing](http://www.bing.com/maps).
You can either configure your location in Paper UI or by using an own .things file, see below for an example!
Latitude must be between 52.50 and 70.75.
Longitude must be between 2.25 and 38.00. 
 
###Discovery###
After the configuration the latest SMHI weather data for your position will be auto-discovered.

###Thing Configuration###
NA

###Channels###
* *Temperature* - In Celsius (C).
* *Humidity* - Relative humidity in percent (%).
* *Pressure* - Air pressure in mbar.
* *Wind Speed* - Wind speed in m/s.
* *Wind Direction* - In degrees.
* *Thunder Probability* - In percent (%).
* *Weather Symbol* - A value beteen 1-6 indicating a wather symbol. 

###Full Example###

smhiweather.things
```
smhiweather:weather:huddinge [ longitude=17.98192, latitude=59.23705 ]
```
smhiweather.items
```
Number   Smhi_Huddinge_Outdoor_Temperature         "Huddinge Outdoor Temperature [%.1f <C2><B0>C]"   <temperature> (gTemperatures_Ch)  {channel="smhiweather:weather:huddinge:temperature"}
Number   Smhi_Huddinge_Outdoor_Humidity            "Huddinge Outdoor Humidity [%.1f %%]"      <humidity>  (gHumidities_Ch)    {channel="smhiweather:weather:huddinge:humidity"}
Number   Smhi_Huddinge_Outdoor_Pressure            "Huddinge Outdoor Pressure [%.1f mbar]"  <pressure> (gPressures_Ch)     {channel="smhiweather:weather:huddinge:pressure"}
Number   Smhi_Huddinge_Outdoor_Wind_Speed          "Huddinge Outdoor Wind Speed [%.1f m/s]"    <wind>                    {channel="smhiweather:weather:huddinge:wind_speed"}
Number   Smhi_Huddinge_Outdoor_Wind_Direction      "Huddinge Outdoor Wind Direction [%.1f degrees]"                {channel="smhiweather:weather:huddinge:wind_direction"}
Number   Smhi_Huddinge_Outdoor_Thunder_Probability "Huddinge Outdoor Thunder Probability [%d %%]"                  {channel="smhiweather:weather:huddinge:thunder_probability"}
Number   Smhi_Huddinge_Outdoor_Weather_Symbol      "Huddinge Weather Symbol [%d]"                                  {channel="smhiweather:weather:huddinge:weather_symbol"}
```