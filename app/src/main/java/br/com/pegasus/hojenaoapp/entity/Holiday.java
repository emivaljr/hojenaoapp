package br.com.pegasus.hojenaoapp.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by emival on 6/5/15.
 */
public class Holiday {

    private Integer id;

    private Date date;

    private String description;

    private String location;

    private String locationType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd/MM/yyyy", Locale.getDefault());

    @Override
    public String toString() {
        return dateFormat.format(date)+" - "+description;
    }
}
