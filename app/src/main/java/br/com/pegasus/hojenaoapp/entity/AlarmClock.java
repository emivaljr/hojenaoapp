package br.com.pegasus.hojenaoapp.entity;

import java.io.Serializable;
import java.util.Timer;

/**
 * Created by emival on 5/28/15.
 */
public class AlarmClock implements Serializable {


    private Integer id;

    private String name;

    private String daysofweek;

    private Integer hour;

    private Integer minute;

    private Boolean active;

    private Integer snooze = 10;

    private Boolean holiday;

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSnooze() {
        return snooze;
    }

    public void setSnooze(Integer snooze) {
        this.snooze = snooze;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDaysofweek() {
        return daysofweek;
    }

    public void setDaysofweek(String daysofweek) {
        this.daysofweek = daysofweek;
    }

    public Boolean getHoliday() {
        return holiday;
    }

    public void setHoliday(Boolean holiday) {
        this.holiday = holiday;
    }

    @Override
    public String toString() {
        return "" + hour +
                ":" + minute;
    }
}
