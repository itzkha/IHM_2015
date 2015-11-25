package ch.heig_vd.pebble.models;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import ch.heig_vd.pebble.communications.RequestGET;
import ch.heig_vd.pebble.interfaces.OnAPIRequest;
import ch.heig_vd.pebble.interfaces.OnCommunicationRequest;
import ch.heig_vd.pebble.utils.MessageType;
import ch.heig_vd.pebble.utils.Utils;


/***************************************************************************************************
 * HEIG-VD // heig-vd.ch
 * Haute Ecole d'Ingénierie et de Gestion du Canton de Vaud
 * School of Business and Engineering in Canton de Vaud
 * *************************************************************************************************
 * Author               : Jonathan Bischof
 * Email                : jonathan.bischof@heig-vd.ch
 * Date                 : 28 october 2015
 * Project              : Pebble IHM
 * *************************************************************************************************
 * Modifications :
 * Ver      Date          Engineer                                Comments
 * 1.0      28.10.2015    Jonathan Bischof                        Creation
 * *************************************************************************************************
 * ...
 **************************************************************************************************/
public class Weather {

    private static final String URL = "http://api.openweathermap.org/data/2.5/weather?";
    private static final String LAT = "lat=";
    private static final String LON = "&lon=";
    private static final String KEY = "&appid=4fa63c122f086ed2cf953c378a0b9083";
    private static final String UNITS = "&units=metric";

    private static final String JSON_KEY_WEATHER = "weather";
    private static final String JSON_KEY_MAIN = "main";
    private static final String JSON_KEY_DESCRIPTION = "description";
    private static final String JSON_KEY_TEMP = "temp";
    private static final String JSON_KEY_PRESSURE = "pressure";
    private static final String JSON_KEY_HUMIDITY = "humidity";
    private static final String JSON_KEY_WIND = "wind";
    private static final String JSON_KEY_WIND_SPEED = "speed";
    private static final String JSON_KEY_WIND_DEG = "deg";
    private static final String JSON_KEY_SYS = "sys";
    private static final String JSON_KEY_SUNRISE = "sunrise";
    private static final String JSON_KEY_SUNSET = "sunset";

    private final Context mContext;
    private double mLatitude;
    private double mLongitude;

    private String mStatus;
    private String mDescription;

    private double mTemp;

    private double mPressure;

    private double mHumidity;

    private double mWindSpeed;
    private String mWindDirection;

    private Date mSunrise;

    private Date mSunset;

    private static final int PEBBLE_KEY_STATUS = 300;
    private static final int PEBBLE_KEY_DESCRIPTION = 301;
    private static final int PEBBLE_KEY_TEMP = 302;
    private static final int PEBBLE_KEY_PRESSURE = 303;
    private static final int PEBBLE_KEY_HUMIDITY = 304;
    private static final int PEBBLE_KEY_WIND_SPEED = 305;
    private static final int PEBBLE_KEY_WIND_DIRECTION = 306;
    private static final int PEBBLE_KEY_SUNRISE = 307;
    private static final int PEBBLE_KEY_SUNSET = 308;

    public Weather(final Context context) {
        mContext = context;
    }

    public void requestWeather(final OnAPIRequest callback, final Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        new RequestGET(new OnCommunicationRequest<String, Exception>() {
            @Override
            public void success(String s) {
                try {
                    parse(new JSONObject(s));
                    int[] keys = new int[2];
                    String[] values = new String[2];
                    keys[0] = PEBBLE_KEY_STATUS;
                    values[0] = mStatus;
                    keys[1] = PEBBLE_KEY_DESCRIPTION;
                    values[1] = mDescription;
                    callback.onRequest(Utils.serialize(MessageType.WEATHER.ordinal(), keys, values));
                } catch (JSONException e) {
                    Utils.logAndToast(mContext, e.getMessage());
                }
            }

            @Override
            public void failure(Exception e) {
                Utils.logAndToast(mContext, e.getMessage());
            }
        }, format()).execute();
    }

    public void requestTemperature(final OnAPIRequest callback, final Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        new RequestGET(new OnCommunicationRequest<String, Exception>() {
            @Override
            public void success(String s) {
                try {
                    parse(new JSONObject(s));
                    int[] keys = new int[1];
                    String[] values = new String[1];
                    keys[0] = PEBBLE_KEY_TEMP;
                    values[0] = String.valueOf(mTemp);
                    callback.onRequest(Utils.serialize(MessageType.TEMPERATURE.ordinal(), keys, values));
                } catch (JSONException e) {
                    Utils.logAndToast(mContext, e.getMessage());
                }
            }

            @Override
            public void failure(Exception e) {
                Utils.logAndToast(mContext, e.getMessage());
            }
        }, format()).execute();
    }

    public void requestPressure(final OnAPIRequest callback, final Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        new RequestGET(new OnCommunicationRequest<String, Exception>() {
            @Override
            public void success(String s) {
                try {
                    parse(new JSONObject(s));
                    int[] keys = new int[1];
                    String[] values = new String[1];
                    keys[0] = PEBBLE_KEY_PRESSURE;
                    values[0] = String.valueOf(mPressure);
                    callback.onRequest(Utils.serialize(MessageType.PRESSURE.ordinal(), keys, values));
                } catch (JSONException e) {
                    Utils.logAndToast(mContext, e.getMessage());
                }
            }

            @Override
            public void failure(Exception e) {
                Utils.logAndToast(mContext, e.getMessage());
            }
        }, format()).execute();
    }

    public void requestHumidity(final OnAPIRequest callback, final Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        new RequestGET(new OnCommunicationRequest<String, Exception>() {
            @Override
            public void success(String s) {
                try {
                    parse(new JSONObject(s));
                    int[] keys = new int[1];
                    String[] values = new String[1];
                    keys[0] = PEBBLE_KEY_HUMIDITY;
                    values[0] = String.valueOf(mHumidity);
                    callback.onRequest(Utils.serialize(MessageType.HUMIDITY.ordinal(), keys, values));
                } catch (JSONException e) {
                    Utils.logAndToast(mContext, e.getMessage());
                }
            }

            @Override
            public void failure(Exception e) {
                Utils.logAndToast(mContext, e.getMessage());
            }
        }, format()).execute();
    }

    public void requestWind(final OnAPIRequest callback, final Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        new RequestGET(new OnCommunicationRequest<String, Exception>() {
            @Override
            public void success(String s) {
                try {
                    parse(new JSONObject(s));
                    int[] keys = new int[2];
                    String[] values = new String[2];
                    keys[0] = PEBBLE_KEY_WIND_SPEED;
                    values[0] = String.valueOf(mWindSpeed);
                    keys[1] = PEBBLE_KEY_WIND_DIRECTION;
                    values[1] = mWindDirection;
                    callback.onRequest(Utils.serialize(MessageType.WIND.ordinal(), keys, values));
                } catch (JSONException e) {
                    Utils.logAndToast(mContext, e.getMessage());
                }
            }

            @Override
            public void failure(Exception e) {
                Utils.logAndToast(mContext, e.getMessage());
            }
        }, format()).execute();
    }

    public void requestSunrise(final OnAPIRequest callback, final Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        new RequestGET(new OnCommunicationRequest<String, Exception>() {
            @Override
            public void success(String s) {
                try {
                    parse(new JSONObject(s));
                    int[] keys = new int[1];
                    String[] values = new String[1];
                    keys[0] = PEBBLE_KEY_SUNRISE;
                    values[0] = mSunrise.toString();
                    callback.onRequest(Utils.serialize(MessageType.SUNRISE.ordinal(), keys, values));
                } catch (JSONException e) {
                    Utils.logAndToast(mContext, e.getMessage());
                }
            }

            @Override
            public void failure(Exception e) {
                Utils.logAndToast(mContext, e.getMessage());
            }
        }, format()).execute();
    }

    public void requestSunset(final OnAPIRequest callback, final Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        new RequestGET(new OnCommunicationRequest<String, Exception>() {
            @Override
            public void success(String s) {
                try {
                    parse(new JSONObject(s));
                    int[] keys = new int[1];
                    String[] values = new String[1];
                    keys[0] = PEBBLE_KEY_SUNSET;
                    values[0] = mSunset.toString();
                    callback.onRequest(Utils.serialize(MessageType.SUNSET.ordinal(), keys, values));
                } catch (JSONException e) {
                    Utils.logAndToast(mContext, e.getMessage());
                }
            }

            @Override
            public void failure(Exception e) {
                Utils.logAndToast(mContext, e.getMessage());
            }
        }, format()).execute();
    }

    private void parse(final JSONObject obj) throws JSONException {
        JSONObject weather = obj.getJSONArray(JSON_KEY_WEATHER).getJSONObject(0);
        mStatus = weather.getString(JSON_KEY_MAIN);
        mDescription = weather.getString(JSON_KEY_DESCRIPTION);
        JSONObject main = obj.getJSONObject(JSON_KEY_MAIN);
        mTemp = main.getDouble(JSON_KEY_TEMP);
        mPressure = main.getDouble(JSON_KEY_PRESSURE);
        mHumidity = main.getDouble(JSON_KEY_HUMIDITY);
        JSONObject wind = obj.getJSONObject(JSON_KEY_WIND);
        mWindSpeed = wind.getDouble(JSON_KEY_WIND_SPEED);
        mWindDirection = Utils.formatDirection(wind.getDouble(JSON_KEY_WIND_DEG));
        JSONObject sys = obj.getJSONObject(JSON_KEY_SYS);
        mSunrise = new Date(sys.getLong(JSON_KEY_SUNRISE) * 1000);
        mSunset = new Date(sys.getLong(JSON_KEY_SUNSET) * 1000);
    }

    private String format() {
        return URL + LAT + mLatitude + LON + mLongitude + KEY + UNITS;
    }
}
