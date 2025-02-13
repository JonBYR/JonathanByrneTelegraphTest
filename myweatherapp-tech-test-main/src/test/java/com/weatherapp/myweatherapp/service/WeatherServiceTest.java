package com.weatherapp.myweatherapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get; //required to use MockMvc as a framework for testing
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.weatherapp.myweatherapp.controller.WeatherController;
import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.model.CityInfo.CurrentConditions; //in order to make a constructor for CurrentConditions, the class must also be included

@WebMvcTest(WeatherController.class) //establishes the class that is being tested in the MVC framework
class WeatherServiceTest {
  @Autowired
  MockMvc mock; //framework required to perform unit tests
  @MockBean
  WeatherService weatherService; //create a mock RESTful API service for testing
  // TODO: 12/05/2023 write unit tests
  @Test
  void GetForecast() throws Exception //throws Exception required
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
  @Test
  void CompareNullException() { //test that null pointer exception is thrown when CityA or CityB is null
    CityInfo cityA = new CityInfo();
    CityInfo cityB = new CityInfo(); //create null objects for the Mock API
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertThrows(NullPointerException.class, () -> {Controller.compareDayLight("CityA", "CityB", weatherService); });
    //assertThrows requires the exception class that is expected and a lambda statement for the code required to throw the exception
    //assertThrows statement will throw NullPointerException as cityA/cityB are null
  }
  @Test
  void NullWeatherService() {
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
    assertThrows(NullPointerException.class, () -> {Controller.compareDayLight("CityA", "CityB", null); });
    //ensure that the NullPointerException is thrown if there is no WeatherService API call
  }
  @Test
  void NullRain() {
    CityInfo cityA = new CityInfo();
    CityInfo cityB = new CityInfo(); //create null objects for the Mock API
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertThrows(NullPointerException.class, () -> {Controller.checkRaining("CityA", "CityB", weatherService); });
    //check that a NullPointerExcpetion is also thrown in the checkRaining function for null CityInfo objects
  }
  @Test
  void NullWeatherServiceRain() {
    CityInfo cityA = new CityInfo();
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.conditions = "RAIN"; 
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.conditions = "Cloudy";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertThrows(NullPointerException.class, () -> {Controller.checkRaining("CityA", "CityB", null); });
    //check that a NullPointerExcpetion is also thrown in the checkRaining function for a null weather service as well
  }
  @Test
  void InvalidDateTimeThrown() {
    CityInfo cityA = new CityInfo(); 
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "06:00:00";
    cityA.currentConditions.sunset = "anytime"; //this will cause the exception to be caught
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.sunrise = "06:00:00";
    cityB.currentConditions.sunset = "18:30:00";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("One or more sunrise/sunset times are in invalid format!", Controller.compareDayLight("CityA", "CityB", weatherService));
    //rather than an exception being thrown, the DateTimeParseException is instead caught, and the message shown above would be returned instead
  }
  @Test
  void InvalidDateTimeFormat() {
    CityInfo cityA = new CityInfo(); 
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "0600:00"; //borderline case where colon is missed, to check that the same error is returned as the test above
    cityA.currentConditions.sunset = "18:30:00";
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.sunrise = "06:00:00";
    cityB.currentConditions.sunset = "18:30:00";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("One or more sunrise/sunset times are in invalid format!", Controller.compareDayLight("CityA", "CityB", weatherService));
  }
  @Test
  void IncorrectHourAbove() {
    CityInfo cityA = new CityInfo(); 
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "06:00:00";
    cityA.currentConditions.sunset = "25:30:00";
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.sunrise = "06:00:00";
    cityB.currentConditions.sunset = "18:30:00";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("One or more sunrise/sunset times are in invalid format!", Controller.compareDayLight("CityA", "CityB", weatherService));
    //the exception is also caught for any conditions that are above the standard 24 hour date time
  }
  @Test
  void IncorrectHourBelow() {
    CityInfo cityA = new CityInfo(); 
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "-1:00:00";
    cityA.currentConditions.sunset = "23:30:00";
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.sunrise = "06:00:00";
    cityB.currentConditions.sunset = "18:30:00";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("One or more sunrise/sunset times are in invalid format!", Controller.compareDayLight("CityA", "CityB", weatherService));
    //the exception is also caught for any conditions that are above the standard 24 hour date time
  }
}