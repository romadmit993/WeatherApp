package Data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import Model.Weather;

@Database(entities = {Weather.class}, version = 1)
public abstract class WeathersAppDB extends RoomDatabase {
    public abstract WeatherDao getWeatherDao();
}
