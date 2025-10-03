package com.finalproject.oopproject.controllers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class WeatherController {
    private final String API_KEY = System.getenv("API_KEY");
    private final String BASE_URL = System.getenv("BASE_URL");
    private final String FORECAST_URL = System.getenv("FORECAST_URL");
    private String city;
    private String weatherResponse;
    private String forecastResponse;

    public String getCity() {
        return city;
    }

    public String getCountry() {
        JSONObject json = new JSONObject(weatherResponse);
        String countryCode = json.getJSONObject("sys").getString("country");
        return new Locale("", countryCode).getDisplayCountry();
    }

    public static String getLocation() throws IOException {
        String response = new String(new URL("http://ip-api.com/json").openStream().readAllBytes());
        JSONObject json = new JSONObject(response);
        return json.getString("city");
    }

    public WeatherController(String city) {
        setCity(city);
    }

    public String getWeatherData() {
        String url = String.format(BASE_URL, city, API_KEY);
        return new RestTemplate().getForObject(url, String.class);
    }

    private String getForecastData() {
        String url = String.format(FORECAST_URL, city, API_KEY);
        return new RestTemplate().getForObject(url, String.class);
    }

    public String getDescription() {
        JSONObject json = new JSONObject(weatherResponse);
        return json.getJSONArray("weather").getJSONObject(0).getString("description");
    }

    public String getIconCode() {
        JSONObject json = new JSONObject(weatherResponse);
        return json.getJSONArray("weather").getJSONObject(0).getString("icon");
    }

    public String getIconUrl() {
        return "https://openweathermap.org/img/wn/" + getIconCode() + "@2x.png";
    }

    public double getTemperature() {
        return new JSONObject(weatherResponse).getJSONObject("main").getDouble("temp");
    }

    public double getFeelsLike() {
        return new JSONObject(weatherResponse).getJSONObject("main").getDouble("feels_like");
    }

    public String getSunrise() {
        long ts = new JSONObject(weatherResponse).getJSONObject("sys").getLong("sunrise");
        return convertUnixToTime(ts);
    }

    public String getSunset() {
        long ts = new JSONObject(weatherResponse).getJSONObject("sys").getLong("sunset");
        return convertUnixToTime(ts);
    }

    private String convertUnixToTime(long unixTimestamp) {
        return new SimpleDateFormat("HH:mm").format(new Date(unixTimestamp * 1000));
    }

    public double getMinTemperature() {
        return new JSONObject(weatherResponse).getJSONObject("main").getDouble("temp_min");
    }

    public double getMaxTemperature() {
        return new JSONObject(weatherResponse).getJSONObject("main").getDouble("temp_max");
    }

    public double getHumidity() {
        return new JSONObject(weatherResponse).getJSONObject("main").getDouble("humidity");
    }

    public double getPressure() {
        return new JSONObject(weatherResponse).getJSONObject("main").getDouble("pressure");
    }

    public double getWindSpeed() {
        return new JSONObject(weatherResponse).getJSONObject("wind").getDouble("speed");
    }

    public double getWindDirection() {
        return new JSONObject(weatherResponse).getJSONObject("wind").getDouble("deg");
    }

    public double getCurrentRainChance() {
        JSONObject json = new JSONObject(weatherResponse);
        if (json.has("rain") && json.getJSONObject("rain").has("1h")) {
            return json.getJSONObject("rain").getDouble("1h");
        }
        return 0;
    }



    public double getDailyMinTemp(int dayIndex) {
        return getDailyTemps(dayIndex)[0];
    }

    public double getDailyMaxTemp(int dayIndex) {
        return getDailyTemps(dayIndex)[1];
    }

    public String getDailyIcon(int dayIndex) {
        JSONArray list = new JSONObject(forecastResponse).getJSONArray("list");
        Map<Integer, List<String>> icons = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_YEAR);

        for (int i = 0; i < list.length(); i++) {
            JSONObject entry = list.getJSONObject(i);
            long ts = entry.getLong("dt");
            cal.setTimeInMillis(ts * 1000);
            int entryDay = cal.get(Calendar.DAY_OF_YEAR);
            int offset = entryDay - today;

            if (offset >= 0 && offset < 7) {
                String icon = entry.getJSONArray("weather").getJSONObject(0).getString("icon");
                icons.putIfAbsent(offset, new ArrayList<>());
                icons.get(offset).add(icon);
            }
        }

        List<String> iconList = icons.get(dayIndex);
        if (iconList != null && !iconList.isEmpty()) {
            return "https://openweathermap.org/img/wn/" + iconList.get(iconList.size() / 2) + "@2x.png";
        }
        return "";
    }

    private double[] getDailyTemps(int dayIndex) {
        JSONArray list = new JSONObject(forecastResponse).getJSONArray("list");
        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_YEAR);
        List<Double> temps = new ArrayList<>();

        for (int i = 0; i < list.length(); i++) {
            JSONObject entry = list.getJSONObject(i);
            long ts = entry.getLong("dt");
            double temp = entry.getJSONObject("main").getDouble("temp");

            cal.setTimeInMillis(ts * 1000);
            int entryDay = cal.get(Calendar.DAY_OF_YEAR);

            if (entryDay - today == dayIndex) {
                temps.add(temp);
            }
        }

        if (!temps.isEmpty()) {
            double min = Collections.min(temps);
            double max = Collections.max(temps);
            return new double[]{min, max};
        } else {
            return new double[]{0, 0};
        }
    }

    public String getForecastDayName(int index) {
        LocalDate forecastDate = LocalDate.now().plusDays(index);
        DayOfWeek dayOfWeek = forecastDate.getDayOfWeek();
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    public int getCloudCoverage() {
        return new JSONObject(weatherResponse).getJSONObject("clouds").getInt("all");
    }

    public void setCity(String city) {
        this.city = city;
        this.weatherResponse = getWeatherData();
        this.forecastResponse = getForecastData();
    }

    public int getVisibility() {
        JSONObject json = new JSONObject(weatherResponse);
        return json.getInt("visibility");
    }

}
