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
  public String compareDayLight(String firstCity, String SecondCity, WeatherService w) 
  {
      CityInfo cityA = w.forecastByCity(firstCity); //this should first call the API to retrieve the information required for each city, then store this information in a CityInfo
    //object
      CityInfo cityB = w.forecastByCity(SecondCity);
      if (cityA == null || cityB == null || w == null) {
        throw new NullPointerException("Either weather service or both cities are null");
      }
      try {
        LocalTime cityAsunrise = LocalTime.parse(cityA.currentConditions.sunrise); //CityInfo currentConditions class converted to public for access
        //unsure on why information is not public after being deserialised from JSON
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
        if(secondsA != secondsB) //check seconds if other two are exact
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
  public String checkRaining(String firstCity, String SecondCity, WeatherService w) 
  {
    CityInfo cityA = w.forecastByCity(firstCity);
    CityInfo cityB = w.forecastByCity(SecondCity);
    if (cityA == null || cityB == null || w == null) {
      throw new NullPointerException("Either weather service or both cities are null");
    }
    String dayA = cityA.currentConditions.conditions.toLowerCase(); //get the conditions varaible from currentconditions class
    String dayB = cityB.currentConditions.conditions.toLowerCase(); //convert to lower case to ensure that the check for the rain string is found regardless of case
    boolean dayAisRaining = dayA.contains("rain");
    boolean dayBisRaining = dayB.contains("rain"); //check if, in conditions, the word rain is mentioned, as this means it is raining
    if(dayAisRaining && dayBisRaining) {
      return "Both cities are raining"; //should both cities contain rain, print that both cities are raining
    }
    else if(dayAisRaining && !dayBisRaining) {
      return firstCity; //if only the first city is raining, return the first city
    }
    else if(!dayAisRaining && dayBisRaining) {
      return SecondCity; //if only the second city is raining, return the second city
    }
    return null;
  }
}
