package com.weatherapp.myweatherapp.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CityInfo {

  @JsonProperty("address")
  String address;

  @JsonProperty("description")
  String description;

  @JsonProperty("currentConditions")
  public CurrentConditions currentConditions;

  @JsonProperty("days")
  List<Days> days;

  public static class CurrentConditions {
    public CurrentConditions() {

    }
    @JsonProperty("temp")
    String currentTemperature;

    @JsonProperty("sunrise")
    public String sunrise;

    @JsonProperty("sunset")
    public String sunset;

    @JsonProperty("feelslike")
    String feelslike;

    @JsonProperty("humidity")
    String humidity;

    @JsonProperty("conditions")
    public String conditions;
  }

  static class Days {

    @JsonProperty("datetime")
    String date;

    @JsonProperty("temp")
    String currentTemperature;

    @JsonProperty("tempmax")
    String maxTemperature;

    @JsonProperty("tempmin")
    String minTemperature;

    @JsonProperty("conditions")
    String conditions;

    @JsonProperty("description")
    String description;

  }

}
