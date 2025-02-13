package com.weatherapp.myweatherapp.controller;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;
@Controller
public class WeatherController {

  @Autowired
  WeatherService weatherService;

  @GetMapping("/forecast/{city}")
  public ResponseEntity<CityInfo> forecastByCity(@PathVariable("city") String city) {
    CityInfo ci = weatherService.forecastByCity(city);
    return ResponseEntity.ok(ci);
  }
  /* 
  The function below will access the API endpoint above to get two CityInfo JSON objects for both cities. Then the sunrise and sunset properties of the internal CurrentConditions
  Class are accessed and converted into LocalTime variables by parsing the string, as the string will be in the correct format. This involved making these properties
  public within the CityInfo.Java class, though on reflection I should have used getters and setters instead. I am unsure if I was meant to adapt the CityInfo file
  however I assumed that I required access to this information in some way. After the string is parsed, the individual hours, minutes and seconds are checked between
  both cities, first by hour, then by minute, then by second, to check which City has the longer amount of daylight. The city name with the most daylight is returned as a string.
  If they both happen to have the same amount of daylight null is returned, or an error message for any erroneous input
  */
  public String DayLightHoursComparison(String firstCity, String SecondCity, WeatherService w) 
  {
      CityInfo cityA = w.forecastByCity(firstCity); //this should first call the API to retrieve the information required for each city, then store this information in a CityInfo
    //object
      CityInfo cityB = w.forecastByCity(SecondCity);
      if (cityA == null || cityB == null || w == null) { //if CityInfo retrieval or weatherservice fails throw this exception
        throw new NullPointerException("Either weather service or both cities are null");
      }
      try {
        LocalTime cityAsunrise = LocalTime.parse(cityA.currentConditions.sunrise); 
        LocalTime cityAsunset = LocalTime.parse(cityA.currentConditions.sunset);
        LocalTime cityBsunrise = LocalTime.parse(cityB.currentConditions.sunrise);
        LocalTime cityBsunset = LocalTime.parse(cityB.currentConditions.sunset); //ensure that the times for sunset and sunrise for both cities are converted to
        //LocalTime objects
        long hoursA = ChronoUnit.HOURS.between(cityAsunrise, cityAsunset);
        long minutesA = ChronoUnit.MINUTES.between(cityAsunrise, cityAsunrise);
        long secondsA = ChronoUnit.SECONDS.between(cityAsunrise, cityAsunset); //check time difference between sunrise and sunset for both cities
        long hoursB = ChronoUnit.HOURS.between(cityBsunrise, cityBsunset);
        long minutesB = ChronoUnit.MINUTES.between(cityBsunrise, cityBsunrise);
        long secondsB = ChronoUnit.SECONDS.between(cityBsunrise, cityBsunset);
        if((hoursA < 0 || hoursB < 0) || (minutesA < 0 || minutesB < 0) || (secondsA < 0 || secondsB < 0)) return "One or two sunset(s) is/are preceeding sunrise!";
        //if sunset time is less than sunrise time, the value returned will be less than 0, which would be erroneus, so return error message
        if(hoursA != hoursB) //check hours first, as if one city has less hours, this means that it would automatically have a shorter day
        {
          if(hoursA > hoursB) return firstCity;
          else if(hoursB > hoursA) return SecondCity;
        }
        if (minutesA != minutesB) //check minutes next as the second largest unit of time
        {
          if(minutesA > minutesB) return firstCity;
          else if(minutesB > minutesA) return SecondCity;
        }
        if(secondsA != secondsB) //check seconds if other hours and minutes are exact
        {
          if(secondsA > secondsB) return firstCity;
          else if(secondsB > secondsA) return SecondCity;
        }
        return null; //if both cities are the exact same return null
      } 
      catch (DateTimeParseException e) { //should any one of the datetime.parse statements fail, catch this exception and return this statement
        return "One or more sunrise/sunset times are in invalid format!";
      }
        
  }
  // TODO: given two city names, check which city its currently raining in
  /* 
  The function below will access the API endpoint above to get two CityInfo JSON objects for both cities. Then the conditions property of the CurrentConditions Class
  is accessed, as this will determine in the string if it is currently raining. The string is converted to lower case first to handle any potential case inputs. The strings
  are first checked to see if the word rain is mentioned and if so, a bool is flagged to true. Then the string is analysed for context to see if it was in fact a chance of rain
  or no rain was mentioned. If so, the rain bool is flagged as false instead. If both cities are raining, a string is returned notifying this fact. If one is raining,
  the city name is returned. If none are raining, then null is returned.
  */
  public String RainCheck(String firstCity, String SecondCity, WeatherService w) 
  {
    CityInfo cityA = w.forecastByCity(firstCity);
    CityInfo cityB = w.forecastByCity(SecondCity);
    if (cityA == null || cityB == null || w == null) {
      throw new NullPointerException("Either weather service or both cities are null");
    }
    String dayA = cityA.currentConditions.conditions.toLowerCase(); //get the conditions varaible from currentconditions class
    String dayB = cityB.currentConditions.conditions.toLowerCase(); //convert to lower case to ensure that the check for the rain string is found regardless of case
    boolean dayAisRaining = dayA.contains("rain");
    boolean dayBisRaining = dayB.contains("rain"); //check if either city mentions rain
    boolean chanceOfRainA = dayA.contains("chance of rain");
    boolean chanceOfRainB = dayB.contains("chance of rain");
    boolean noRainA = dayA.contains("no rain");
    boolean noRainB = dayB.contains("no rain"); //if rain is found in the conditions string, check for additional context
    //if it is a chance of rain, then it is not currently raining yet
    //if there is no rain, then it will not be raining yet
    if(chanceOfRainA || noRainA) dayAisRaining = !dayAisRaining; //if eiher chance or no rain is proven, then switch the isRaining bool for the respective city to false
    if(chanceOfRainB || noRainB) dayBisRaining = !dayBisRaining;
    if(dayAisRaining && !dayBisRaining) {
      return firstCity; //if only the first city is raining, return the first city
    }
    else if(!dayAisRaining && dayBisRaining) {
      return SecondCity; //if only the second city is raining, return the second city
    }
    else if(dayAisRaining && dayBisRaining) { 
      return "Both cities are raining"; //should both cities contain rain, return that both cities are raining
    }
    return null;
  }
}
