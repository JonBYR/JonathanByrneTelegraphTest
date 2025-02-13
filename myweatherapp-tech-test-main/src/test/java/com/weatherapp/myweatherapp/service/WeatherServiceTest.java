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

@WebMvcTest(WeatherController.class) //establishes one instance of the WeatherController Class for testing
class WeatherServiceTest {
  @Autowired //autowired keyword is used to inject the mock into the controller
  MockMvc mock; //MockMvc is required for unit testing API data
  @MockBean //annotation establishes that a mock of the weatherService is created
  WeatherService weatherService; //mock the weatherService object. This is so that it's behaviour can be tested and verified 
  // TODO: 12/05/2023 write unit tests
  /* Test to make sure that the MockAPI works as expected */
  @Test
  void GetForecastTest() throws Exception //throws Exception required
  {
    mock.perform(get("/forecast/London")).andExpect(status().isOk()); //this test will simulate a HTTP request to fetch forecast data
  }
   
  @Test
  /* 
  First set of tests checks that the correct cities are returned based on various hour, minute and second information. A null check is also tested for when both cities
  have the same amount of daylight 
  */
  void HoursTest() { 
    CityInfo cityA = new CityInfo(); //create mock CityInfo for the Mock weatherService
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.sunrise = "06:00";
    cityA.currentConditions.sunset = "18:00";
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions(); //had to establish a public constructor in the city info class for this method to work
    cityB.currentConditions.sunrise = "06:00";
    cityB.currentConditions.sunset = "19:00";
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB); 
    //when specifies the behaviour of the mock object. This means that, when the weatherService API is called on a string, the relevant CityInfo object is returned.
    //this ensures that the method returns the right result and verifies that the method works as expected
    //as a result, the unit test to check the DayLightHoursComparison method can run, as the forecastByCity method used within the function will work correctly
    var Controller = new WeatherController(); //instance of API Controller is needed
    assertEquals("CityB", Controller.DayLightHoursComparison("CityA", "CityB", weatherService));
    //use these mock cities to check if the correct information is returned. If so, then this will mean that the same method can work for a real API call
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
    assertEquals("CityA", Controller.DayLightHoursComparison("CityA", "CityB", weatherService));
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
    assertEquals("CityB", Controller.DayLightHoursComparison("CityA", "CityB", weatherService));
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
    assertEquals("CityA", Controller.DayLightHoursComparison("CityA", "CityB", weatherService));
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
    assertEquals("CityB", Controller.DayLightHoursComparison("CityA", "CityB", weatherService));
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
    assertEquals("CityA", Controller.DayLightHoursComparison("CityA", "CityB", weatherService));
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
    assertNull(Controller.DayLightHoursComparison("CityA", "CityB", weatherService));
  }
  /* 
  Second set of tests check for if the correct string is returned when either one, or both cities are raining. A null check is also done for when
  no cities are raining.
   */
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
    assertEquals("Both cities are raining", Controller.RainCheck("CityA", "CityB", weatherService));
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
    assertEquals("Both cities are raining", Controller.RainCheck("CityA", "CityB", weatherService));
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
    assertEquals("CityA", Controller.RainCheck("CityA", "CityB", weatherService));
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
    assertEquals("CityB", Controller.RainCheck("CityA", "CityB", weatherService));
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
    assertNull(Controller.RainCheck("CityA", "CityB", weatherService));
  }
  /* 
  The third set of tests will check that if CityInfo or WeatherService is Null, both functions return a NullPointerException 
  */
  @Test
  void CompareNullExceptionTest() { //test that null pointer exception is thrown when CityA or CityB is null
    CityInfo cityA = new CityInfo();
    CityInfo cityB = new CityInfo(); //create null objects for the Mock API
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertThrows(NullPointerException.class, () -> {Controller.DayLightHoursComparison("CityA", "CityB", weatherService); });
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
    assertThrows(NullPointerException.class, () -> {Controller.DayLightHoursComparison("CityA", "CityB", null); });
    //ensure that the NullPointerException is thrown if there is no WeatherService API call
  }
  @Test
  void NullRainTest() {
    CityInfo cityA = new CityInfo();
    CityInfo cityB = new CityInfo(); //create null objects for the Mock API
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertThrows(NullPointerException.class, () -> {Controller.RainCheck("CityA", "CityB", weatherService); });
    //check that a NullPointerExcpetion is also thrown in the RainCheck function for null CityInfo objects
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
    assertThrows(NullPointerException.class, () -> {Controller.RainCheck("CityA", "CityB", null); });
    //check that a NullPointerExcpetion is also thrown in the RainCheck function for a null weather service as well
  }
  /*
  The fourth set of tests check that the correct error message is returned when the DateTimeParseException is caught, which occurs for invalid strings such as
  a word given for the sunrise/sunset variable or borderline cases such as the time exceeding the hour, minute or seconds coloum.
  */
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
    assertEquals("One or more sunrise/sunset times are in invalid format!", Controller.DayLightHoursComparison("CityA", "CityB", weatherService));
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
    assertEquals("One or more sunrise/sunset times are in invalid format!", Controller.DayLightHoursComparison("CityA", "CityB", weatherService));
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
    assertEquals("One or more sunrise/sunset times are in invalid format!", Controller.DayLightHoursComparison("CityA", "CityB", weatherService));
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
    assertEquals("One or more sunrise/sunset times are in invalid format!", Controller.DayLightHoursComparison("CityA", "CityB", weatherService));
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
    assertEquals("One or more sunrise/sunset times are in invalid format!", Controller.DayLightHoursComparison("CityA", "CityB", weatherService));
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
    assertEquals("One or more sunrise/sunset times are in invalid format!", Controller.DayLightHoursComparison("CityA", "CityB", weatherService));
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
    assertEquals("One or more sunrise/sunset times are in invalid format!", Controller.DayLightHoursComparison("CityA", "CityB", weatherService));
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
    assertEquals("One or more sunrise/sunset times are in invalid format!", Controller.DayLightHoursComparison("CityA", "CityB", weatherService));
  }
  /* 
  This test ensures that if a CityTime is at the very edge of being erroneus, but is still a valid time, that the error string is not returned
  */
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
    assertEquals("CityA", Controller.DayLightHoursComparison("CityA", "CityB", weatherService));
  }
  /*
  The fifth set of tests ensure that the correct error string is returned if the sunset time preceeds the sunrise time, as this would effectively mean
  that the hours of daylight are negative, which is an erroneous value.
  */
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
    assertEquals("One or two sunset(s) is/are preceeding sunrise!", Controller.DayLightHoursComparison("CityA", "CityB", weatherService));
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
    assertEquals("One or two sunset(s) is/are preceeding sunrise!", Controller.DayLightHoursComparison("CityA", "CityB", weatherService));
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
    assertEquals("One or two sunset(s) is/are preceeding sunrise!", Controller.DayLightHoursComparison("CityA", "CityB", weatherService));
    //error string returned from function
  }
  /*
  The sixth and final set of tests ensure that null is returned when one or both cities have a chance or no chance of rain.
  Tests are also done for when one city does contain rain, while the other city has no chance or chance of rain, as this should return
  the city with rain.
  */
  @Test
  void BothCitiesHaveAChanceOfRainTest() { //check null is returned if both cities have a chance of rain
    CityInfo cityA = new CityInfo();
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.conditions = "Chance of Rain"; 
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.conditions = "Chance of Rain"; 
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertNull(Controller.RainCheck("CityA", "CityB", weatherService));
  }
  @Test
  void OneCityHasAChanceOfRainTest() { //check that if one city has a chance of rain forecast and the other city does not mention rain, that null is returned
    CityInfo cityA = new CityInfo();
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.conditions = "Chance of Rain"; 
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.conditions = "Cloudy"; 
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertNull(Controller.RainCheck("CityA", "CityB", weatherService));
  }
  @Test
  void BothCitiesHaveNoRainTest() { //check both cities are forecast to have no rain and thus returns null
    CityInfo cityA = new CityInfo();
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.conditions = "No Rain"; 
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.conditions = "No Rain"; 
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertNull(Controller.RainCheck("CityA", "CityB", weatherService));
  }
  @Test
  void OneCityHasNoRainTest() { //check that if one city has no rain forecast and the other city does not mention rain, that null is returned
    CityInfo cityA = new CityInfo();
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.conditions = "No Rain"; 
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.conditions = "Cloudy"; 
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertNull(Controller.RainCheck("CityA", "CityB", weatherService));
  }
  @Test
  void OneCityHasNoRainTheOtherRainTest() { //make sure that the city with rain is returned if the other has no rain
    CityInfo cityA = new CityInfo();
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.conditions = "No Rain"; 
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.conditions = "Rain"; 
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("CityB", Controller.RainCheck("CityA", "CityB", weatherService));
  }
  @Test
  void OneCityHasChanceOfRainTheOtherRainTest() { //make sure that the city with rain is returned if the other has a chance of rain
    CityInfo cityA = new CityInfo();
    cityA.currentConditions = new CurrentConditions();
    cityA.currentConditions.conditions = "Chance of Rain"; 
    CityInfo cityB = new CityInfo();
    cityB.currentConditions = new CurrentConditions();
    cityB.currentConditions.conditions = "Rain"; 
    when(weatherService.forecastByCity("CityA")).thenReturn(cityA);
    when(weatherService.forecastByCity("CityB")).thenReturn(cityB);
    var Controller = new WeatherController();
    assertEquals("CityB", Controller.RainCheck("CityA", "CityB", weatherService));
  }
}