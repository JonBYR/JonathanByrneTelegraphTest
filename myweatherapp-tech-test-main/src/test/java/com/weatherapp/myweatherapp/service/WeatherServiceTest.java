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
  void GetForecastTest() throws Exception //throws Exception required
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
  void SameDaylightTest() { //check null is returned if both cities are the exact same
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
  void BothCitiesAreRainingTest() { //check the other method and see if both cities are raining
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
  void BothCitiesAreRainingUpperTest() { //check that the same result is used regardless of case
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
  void FirstCityIsRainingTest() { 
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
  void SecondCityIsRainingTest() { 
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
  void NoCitiesAreRainingTest() { //check if the null condition is picked up if neither city contains rain
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
  void CompareNullExceptionTest() { //test that null pointer exception is thrown when CityA or CityB is null
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
  void NullWeatherServiceTest() {
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
  void NullRainTest() {
    CityInfo cityA = new CityInfo();
    CityInfo cityB = new CityInfo(); //create null objects for the Mock API
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertThrows(NullPointerException.class, () -> {Controller.checkRaining("CityA", "CityB", weatherService); });
    //check that a NullPointerExcpetion is also thrown in the checkRaining function for null CityInfo objects
  }
  @Test
  void NullWeatherServiceRainTest() {
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
  void InvalidDateTimeThrownTest() {
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
  void InvalidDateTimeFormatTest() {
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
  void IncorrectHourAboveTest() {
    CityInfo cityA = new CityInfo(); 
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "06:00:00";
    cityA.currentConditions.sunset = "24:00:00"; //hour cannot exceed 24, therefore if this borderline case passes, the test is true for any number above 24
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
  void IncorrectHourBelowTest() {
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
    //the exception is also caught for any conditions that are below the standard 24 hour date time
  }
  @Test
  void IncorrectMinuteAboveTest() {
    CityInfo cityA = new CityInfo(); 
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "06:00:00";
    cityA.currentConditions.sunset = "06:60:00"; //borderline case
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.sunrise = "06:00:00";
    cityB.currentConditions.sunset = "18:30:00";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("One or more sunrise/sunset times are in invalid format!", Controller.compareDayLight("CityA", "CityB", weatherService));
    //checking the exception is thrown for invalid minutes
  }
  @Test
  void IncorrectMinuteBelowTest() {
    CityInfo cityA = new CityInfo(); 
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "06:-1:00";
    cityA.currentConditions.sunset = "06:59:00";
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
  void IncorrectSecondsAboveTest() {
    CityInfo cityA = new CityInfo(); 
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "06:00:00";
    cityA.currentConditions.sunset = "06:00:60"; //borderline case
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.sunrise = "06:00:00";
    cityB.currentConditions.sunset = "18:30:00";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("One or more sunrise/sunset times are in invalid format!", Controller.compareDayLight("CityA", "CityB", weatherService));
    //checking the exception is thrown for invalid seconds
  }
  @Test
  void IncorrectSecondsBelowTest() {
    CityInfo cityA = new CityInfo(); 
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "06:00:-1";
    cityA.currentConditions.sunset = "06:59:00"; //borderline case
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
  void BorderlineTimeTest() {
    CityInfo cityA = new CityInfo(); 
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "00:00:00";
    cityA.currentConditions.sunset = "23:59:59"; //checks that both borderline conditions of 00:00:00 and 23:59:59 do not thrown an exception
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.sunrise = "06:00:00";
    cityB.currentConditions.sunset = "18:30:00";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("CityA", Controller.compareDayLight("CityA", "CityB", weatherService));
  }
  @Test 
  void SunsetPreceedingSunriseHourTest() {
    CityInfo cityA = new CityInfo(); 
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "06:00:00";
    cityA.currentConditions.sunset = "05:00:00"; //this should throw an error as a value of -1 is created in the method
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.sunrise = "06:00:00";
    cityB.currentConditions.sunset = "18:30:00";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("One or two sunset(s) is/are preceeding sunrise!", Controller.compareDayLight("CityA", "CityB", weatherService));
    //error string returned from function
  }
  @Test 
  void SunsetPreceedingSunriseMinuteTest() { //repeat test for minute
    CityInfo cityA = new CityInfo(); 
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "06:30:00";
    cityA.currentConditions.sunset = "06:29:00"; //borderline case, should cause a value of -1
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.sunrise = "06:00:00";
    cityB.currentConditions.sunset = "18:30:00";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("One or two sunset(s) is/are preceeding sunrise!", Controller.compareDayLight("CityA", "CityB", weatherService));
    //error string returned from function
  }
  @Test 
  void SunsetPreceedingSunriseSecondsTest() { //repeat test for seconds
    CityInfo cityA = new CityInfo(); 
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "06:00:59"; //borderline value so should be true for any larger negative values
    cityA.currentConditions.sunset = "06:00:58"; //this should throw an error as a value of -1 is created in the method
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.sunrise = "06:00:00";
    cityB.currentConditions.sunset = "18:30:00";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("One or two sunset(s) is/are preceeding sunrise!", Controller.compareDayLight("CityA", "CityB", weatherService));
    //error string returned from function
  }
}