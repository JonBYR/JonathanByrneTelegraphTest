package com.weatherapp.myweatherapp.controller;

import java.time.LocalTime;
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
  public String compareDayLight(@PathVariable("firstCity") String firstCity, @PathVariable("SecondCity") String SecondCity) 
  {
    
    CityInfo cityA = weatherService.forecastByCity(firstCity); //this should first call the API to retrieve the information required for each city, then store this information in a CityInfo
    //object
    CityInfo cityB = weatherService.forecastByCity(SecondCity);
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
    if(hoursA != hoursB) //if in different time zones, check which city has the greater number of hours
    {
      if(hoursA > hoursB) return firstCity;
      else if(hoursB > hoursA) return SecondCity;
    }
    if (minutesA != minutesB) //if in same time zone, check which has the longer amount of minutes, then seconds
    {
      if(minutesA > minutesB) return firstCity;
      else if(minutesB > minutesA) return SecondCity;
    }
    if(secondsA != secondsB) 
    {
      if(secondsA > secondsB) return firstCity;
      else if(secondsB > secondsA) return SecondCity;
    }
    return null; //if time zones are the exact same, return null
    
  }
  // TODO: given two city names, check which city its currently raining in
  public String checkRaining(String firstCity, String SecondCity) 
  {
    //ResponseEntity<CityInfo> firstCall = forecastByCity(firstCity);
    CityInfo cityA = weatherService.forecastByCity(firstCity);
    //ResponseEntity<CityInfo> secondCall = forecastByCity(firstCity);
    CityInfo cityB = weatherService.forecastByCity(SecondCity);
    String dayA = cityA.currentConditions.conditions; //get the conditions varaible from currentconditions class
    String dayB = cityB.currentConditions.conditions; 
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
