package com.example.nijatmcmudow.solis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by nijatmcmudow on 02/04/15.
 */
public class CurrentWeatherData {
    private String nIcon;
    private long nTime;
    private double nTemperature;
    private double nHumidity;
    private double nPrecipChance;
    private String nSummary;
    private String nTimeZone;

    public String getnTimeZone() {
        return nTimeZone;
    }

    public void setnTimeZone(String nTimeZone) {
        this.nTimeZone = nTimeZone;
    }

    public String getnIcon() {
        return nIcon;
    }

    public void setnIcon(String icon) {
        nIcon = icon;
    }

    public int getIconId() {
        int iconId = R.mipmap.clear_day;

        if (nIcon.equals("clear-day")) {
            iconId = R.mipmap.clear_day;
        }
        else if (nIcon.equals("clear-night")) {
            iconId = R.mipmap.clear_night;
        }
        else if (nIcon.equals("rain")) {
            iconId = R.mipmap.rain;
        }
        else if (nIcon.equals("snow")) {
            iconId = R.mipmap.snow;
        }
        else if (nIcon.equals("sleet")) {
            iconId = R.mipmap.sleet;
        }
        else if (nIcon.equals("wind")) {
            iconId = R.mipmap.wind;
        }
        else if (nIcon.equals("fog")) {
            iconId = R.mipmap.fog;
        }
        else if (nIcon.equals("cloudy")) {
            iconId = R.mipmap.cloudy;
        }
        else if (nIcon.equals("partly-cloudy-day")) {
            iconId = R.mipmap.partly_cloudy;
        }
        else if (nIcon.equals("partly-cloudy-night")) {
            iconId = R.mipmap.cloudy_night;
        }

        return iconId;
    }

    public long getnTime() {
        return nTime;
    }

    public void setnTime(long nTime) {
        this.nTime = nTime;
    }

    public String getfTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getnTimeZone()));
        Date dateTime = new Date(getnTime() * 1000);
        String timeString = formatter.format(dateTime);

        return timeString;
    }

    // Fahrenheit to Celcius conversion extended with one decimal value.
    public float getnTemperature() {
        return (float)Math.round((((nTemperature-32)*5/9)*10))/10;
        //return (int)Math.round((nTemperature - 32)*0.5);
        //return Math.round((nTemperature - 32)*5/9);
    }

    public void setnTemperature(double nTemperature) {
        this.nTemperature = nTemperature;
    }

    public double getnHumidity() {
        return nHumidity;
    }

    public void setnHumidity(double nHumidity) {
        this.nHumidity = nHumidity;
    }

    public int getnPrecipChance() {
        double precipPercentage = nPrecipChance * 100;
        return (int)Math.round(precipPercentage);
    }

    public void setnPrecipChance(double nPrecipChance) {
        this.nPrecipChance = nPrecipChance;
    }

    public String getnSummary() {
        return nSummary;
    }

    public void setnSummary(String nSummary) {
        this.nSummary = nSummary;
    }

}
