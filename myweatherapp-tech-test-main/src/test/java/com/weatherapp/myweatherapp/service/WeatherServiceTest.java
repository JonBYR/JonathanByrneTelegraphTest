package com.weatherapp.myweatherapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.weatherapp.myweatherapp.controller.WeatherController;

class WeatherServiceTest {
  // TODO: 12/05/2023 write unit tests
  @Test
  void MinutesTest() { //at time of testing, Lincoln has a longer day than London by the number of minutes
    var Controller = new WeatherController(); //instance of API Controller is needed
    assertEquals("Lincoln", Controller.compareDayLight("Lincoln", "London"));
  }
  @Test
  void HoursTest() {
    var Controller = new WeatherController();
    assertEquals("Sydney", Controller.compareDayLight("London", "Sydney"));
  }
}