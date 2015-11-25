package ch.heig_vd.pebble.models;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

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
public class Elevation {

    private static final String URL = "https://maps.googleapis.com/maps/api/elevation/json?";
    private static final String LAT = "locations=";
    private static final String LON = ",";
    private static final String KEY = "&key=AIzaSyDiALs9VHnADqntRIYB7u97Fs7VTBkae_Q";

    private final Context mContext;
    private double mLatitude;
    private double mLongitude;

    private static final String JSON_KEY_RESULTS = "results";
    private static final String JSON_KEY_ELEVATION = "elevation";

    private double mElevation;

    private static final int PEBBLE_KEY_ELEVATION = 200;

    public Elevation(final Context context) {
        mContext = context;
    }

    public void requestElevation(final OnAPIRequest callback, final Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        new RequestGET(new OnCommunicationRequest<String, Exception>() {
            @Override
            public void success(String s) {
                try {
                    parse(new JSONObject(s));
                    int[] keys = new int[1];
                    String[] values = new String[1];
                    keys[0] = PEBBLE_KEY_ELEVATION;
                    values[0] = String.valueOf(mElevation);
                    callback.onRequest(Utils.serialize(MessageType.ELEVATION.ordinal(), keys, values));
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

    private void parse(final JSONObject object) throws JSONException {
        JSONObject elevation = object.getJSONArray(JSON_KEY_RESULTS).getJSONObject(0);
        mElevation = elevation.getDouble(JSON_KEY_ELEVATION);
    }

    private String format() {
        return URL + LAT + mLatitude + LON + mLongitude + KEY;
    }
}
