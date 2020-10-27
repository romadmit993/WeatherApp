package Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "weathers")
public class Weather {
    @PrimaryKey
    @NonNull
    public String cityName;
    public String description;

    @Ignore
    public Weather() {
    }

    public Weather(String cityName, String description) {
        this.cityName = cityName;
        this.description = description;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getTemp() {
        return description;
    }

    public void setTemp(String description) {
        this.description = description;
    }
}
