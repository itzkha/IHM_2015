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
public class Transport {

    private static final String URL = "http://transport.opendata.ch/v1/";
    private static final String CITY = "locations?";
    private static final String LAT = "x=";
    private static final String LON = "&y=";
    private static final String CONN = "connections?";
    private static final String FROM = "from=";
    private static final String TO = "&to=";

    private String mSource;
    private String mDestination = "Lausanne";
    private final Context mContext;
    private double mLatitude;
    private double mLongitude;

    private static final String JSON_KEY_STATIONS = "stations";
    private static final String JSON_KEY_CITY = "name";
    private static final String JSON_KEY_CONNECTIONS = "connections";
    private static final String JSON_KEY_FROM = "from";
    private static final String JSON_KEY_TO = "to";
    private static final String JSON_KEY_STATION = "station";
    private static final String JSON_KEY_DEPARTURE_TIME = "departureTimestamp";
    private static final String JSON_KEY_ARRIVAL_TIME = "arrivalTimestamp";

    private String mDeparture;
    private Date mDepartureTime;
    private String mArrival;
    private Date mArrivalTime;

    private static final int PEBBLE_KEY_DEPARTURE = 400;
    private static final int PEBBLE_KEY_DEPARTURE_TIME = 401;
    private static final int PEBBLE_KEY_ARRIVAL = 402;
    private static final int PEBBLE_KEY_ARRIVAL_TIME = 403;

    public Transport(final Context context) {
        mContext = context;
    }

    public void requestTransport(final OnAPIRequest callback, final Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        new RequestGET(new OnCommunicationRequest<String, Exception>() {
            @Override
            public void success(String s) {
                try {
                    parseSource(new JSONObject(s));
                    new RequestGET(new OnCommunicationRequest<String, Exception>() {
                        @Override
                        public void success(String s) {
                            try {
                                parse(new JSONObject(s));
                                int[] keys = new int[4];
                                String[] values = new String[4];
                                keys[0] = PEBBLE_KEY_DEPARTURE;
                                values[0] = mDeparture;
                                keys[1] = PEBBLE_KEY_DEPARTURE_TIME;
                                values[1] = mDepartureTime.getHours() + ":" + mDepartureTime.getMinutes();
                                keys[2] = PEBBLE_KEY_ARRIVAL;
                                values[2] = mArrival;
                                keys[3] = PEBBLE_KEY_ARRIVAL_TIME;
                                values[3] = mArrivalTime.getHours() + ":" + mArrivalTime.getMinutes();
                                callback.onRequest(Utils.serialize(MessageType.TRANSPORT.ordinal(), keys, values));
                            } catch (JSONException e) {
                                Utils.logAndToast(mContext, e.getMessage());
                            }
                        }

                        @Override
                        public void failure(Exception e) {
                            Utils.logAndToast(mContext, e.getMessage());
                        }
                    }, formatConnection()).execute();
                } catch (JSONException e) {
                    Utils.logAndToast(mContext, e.getMessage());
                }
            }

            @Override
            public void failure(Exception e) {
                Utils.logAndToast(mContext, e.getMessage());
            }
        }, formatSource()).execute();

    }

    public void setDestination(final String destination) {
        mDestination = destination;
    }

    public String getSource() {
        return mSource;
    }

    private void parseSource(final JSONObject object) throws JSONException {
        JSONObject station = object.getJSONArray(JSON_KEY_STATIONS).getJSONObject(0);
        mSource = station.getString(JSON_KEY_CITY);
        if (mSource.contains(",")) {
            mSource = mSource.split(",")[0];
        }
    }

    private void parse(final JSONObject object) throws JSONException {
        JSONObject connection = object.getJSONArray(JSON_KEY_CONNECTIONS).getJSONObject(0);
        JSONObject from = connection.getJSONObject(JSON_KEY_FROM);
        JSONObject station = from.getJSONObject(JSON_KEY_STATION);
        mDeparture = station.getString(JSON_KEY_CITY);
        mDepartureTime = new Date(from.getLong(JSON_KEY_DEPARTURE_TIME) * 1000);
        JSONObject to = connection.getJSONObject(JSON_KEY_TO);
        station = to.getJSONObject(JSON_KEY_STATION);
        mArrival = station.getString(JSON_KEY_CITY);
        mArrivalTime = new Date(to.getLong(JSON_KEY_ARRIVAL_TIME) * 1000);
    }

    private String formatSource() {
        return URL + CITY + LAT + mLatitude + LON + mLongitude;
    }

    private String formatConnection() {
        return URL + CONN + FROM + mSource + TO + mDestination;
    }
}
