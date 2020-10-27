package Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Collection;
import java.util.List;

import Model.Weather;

@Dao
public interface WeatherDao {

    @Insert
    public long addWeather(Weather weather);

    @Update
    public void updateWeather(Weather weather);

    @Delete
    public void deleteWeather(Weather weather);

    @Query("SELECT * FROM WEATHERS")
    public List<Weather> loadAllWeather();

    @Query("SELECT * FROM weathers WHERE cityName ==:cityName")
    public List<Weather> getWeather(String cityName);

    @Query("DELETE FROM weathers")
    public void clearTable();
}
