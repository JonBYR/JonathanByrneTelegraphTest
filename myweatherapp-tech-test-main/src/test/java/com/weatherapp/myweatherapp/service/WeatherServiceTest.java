package com.weatherapp.myweatherapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc; //required to use MockMvc as a framework for testing
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.weatherapp.myweatherapp.controller.WeatherController;
import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.model.CityInfo.CurrentConditions;

@WebMvcTest(WeatherController.class) //establishes the class that is being tested in the MVC framework
class WeatherServiceTest {
  @Autowired
  MockMvc mock; //framework required to perform unit tests
  @MockBean
  WeatherService weatherService; //create a mock RESTful API service for testing
  // TODO: 12/05/2023 write unit tests
  @Test
  void GetForecast() throws Exception 
  {
    mock.perform(get("/forecast/London")).andExpect(status().isOk()); //this test will simulate a HTTP request to fetch forecast data
  }
   
  @Test
  void HoursTest() { 
    CityInfo cityA = new CityInfo(); //create mock CityInfo for the Mock API
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "06:00";
    cityA.currentConditions.sunset = "18:00";
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions(); //had to establish a public constructor in the city info class for this method to work
    cityB.currentConditions.sunrise = "06:00";
    cityB.currentConditions.sunset = "19:00";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB); 
    //when will mock the forecastByCity method so that a CityInfo object is returned with the specified mock info 
    var Controller = new WeatherController(); //instance of API Controller is needed
    assertEquals("CityB", Controller.compareDayLight("CityA", "CityB", weatherService));
    //use these mock cities within the mock api and to check if the correct information is returned. If so, then this will mean that the same method
    //can work for a real API call
  }
  @Test
  void HoursTestOtherCity() { //check that the first City is correctly picked when it has more hours
    CityInfo cityA = new CityInfo(); 
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "07:00";
    cityA.currentConditions.sunset = "20:00";
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.sunrise = "06:00";
    cityB.currentConditions.sunset = "18:00";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("CityA", Controller.compareDayLight("CityA", "CityB", weatherService));
  }
  @Test
  void MinutesTest() { //perform same method but check for minutes
    CityInfo cityA = new CityInfo(); 
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "06:00";
    cityA.currentConditions.sunset = "18:29";
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.sunrise = "06:00";
    cityB.currentConditions.sunset = "18:30";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("CityB", Controller.compareDayLight("CityA", "CityB", weatherService));
  }
  @Test
  void MinutesTestOtherCity() { //check that the first City is correctly picked when it has more minutes
    CityInfo cityA = new CityInfo(); 
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "06:00";
    cityA.currentConditions.sunset = "18:30";
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.sunrise = "06:00";
    cityB.currentConditions.sunset = "18:29";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("CityA", Controller.compareDayLight("CityA", "CityB", weatherService));
  }
  @Test
  void SecondsTest() { //perform same method but check for seconds
    CityInfo cityA = new CityInfo(); 
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "06:00:00";
    cityA.currentConditions.sunset = "18:30:01";
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.sunrise = "06:00:00";
    cityB.currentConditions.sunset = "18:30:02"; //borderline value, meaning that if the test passes, it should pass for larger intervals
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("CityB", Controller.compareDayLight("CityA", "CityB", weatherService));
  }
  @Test
  void SecondsTestOtherCity() { //check seconds works for the other city
    CityInfo cityA = new CityInfo(); 
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "06:00:00";
    cityA.currentConditions.sunset = "18:30:02";
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.sunrise = "06:00:00";
    cityB.currentConditions.sunset = "18:30:01";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("CityA", Controller.compareDayLight("CityA", "CityB", weatherService));
  }
  @Test
  void SameDaylight() { //check null is returned if both cities are the exact same
    CityInfo cityA = new CityInfo(); 
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "06:00:00";
    cityA.currentConditions.sunset = "18:30:00";
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.sunrise = "06:00:00";
    cityB.currentConditions.sunset = "18:30:00";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertNull(Controller.compareDayLight("CityA", "CityB", weatherService));
  }
  @Test
  void BothCitiesAreRaining() { //check the other method and see if both cities are raining
    CityInfo cityA = new CityInfo();
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.conditions = "rain";
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.conditions = "rain";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("Both cities are raining", Controller.checkRaining("CityA", "CityB", weatherService));
  }
  @Test
  void BothCitiesAreRainingUpper() { //check that the same result is used regardless of case
    CityInfo cityA = new CityInfo();
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.conditions = "RAIN";
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.conditions = "RAIN";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("Both cities are raining", Controller.checkRaining("CityA", "CityB", weatherService));
  }
  @Test
  void FirstCityIsRaining() { 
    CityInfo cityA = new CityInfo();
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.conditions = "RAIN"; //check if the first city is raining, that the test returns the first city
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.conditions = "Cloudy";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("CityA", Controller.checkRaining("CityA", "CityB", weatherService));
  }
  @Test
  void SecondCityIsRaining() { 
    CityInfo cityA = new CityInfo();
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.conditions = "Cloudy"; 
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.conditions = "rain"; //check if the second city is raining, that the test returns the second city
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("CityB", Controller.checkRaining("CityA", "CityB", weatherService));
  }
  @Test
  void NoCitiesAreRaining() { //check if the null condition is picked up if neither city contains rain
    CityInfo cityA = new CityInfo();
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.conditions = "Cloudy"; 
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.conditions = "Cloudy"; 
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertNull(Controller.checkRaining("CityA", "CityB", weatherService));
  }
}