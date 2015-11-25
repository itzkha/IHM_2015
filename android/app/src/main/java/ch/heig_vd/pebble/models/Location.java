package ch.heig_vd.pebble.models;

import ch.heig_vd.pebble.interfaces.OnAPIRequest;
import ch.heig_vd.pebble.services.LocationService;
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
public class Location {

    private static boolean isThreaded = false;

    private static final int PEBBLE_KEY_LATITUDE = 100;
    private static final int PEBBLE_KEY_LONGITUDE = 101;
    private static final int PEBBLE_KEY_DISTANCE = 102;
    private static final int PEBBLE_KEY_DIRECTION = 103;

    private double mFixedLatitude;
    private double mFixedLongitude;

    public double getLatitude() {
        return LocationService.getLatitude();
    }

    public double getLongitude() {
        return LocationService.getLongitude();
    }

    public void requestLocation(final OnAPIRequest callback) {
        int[] keys = new int[2];
        String[] values = new String[2];
        keys[0] = PEBBLE_KEY_LATITUDE;
        values[0] = String.valueOf(getLatitude());
        keys[1] = PEBBLE_KEY_LONGITUDE;
        values[1] = String.valueOf(getLongitude());
        callback.onRequest(Utils.serialize(MessageType.CURRENT_LOCATION.ordinal(), keys, values));
    }

    public void fixLocation() {
        mFixedLatitude = getLatitude();
        mFixedLongitude = getLongitude();
    }

    public void startThreadedLocation(final OnAPIRequest callback) {
        if (mFixedLatitude == 0.0 || mFixedLongitude == 0.0) {
            fixLocation();
        }
        isThreaded = true;
        new Thread() {
            @Override
            public void run() {
                while (isThreaded) {
                    try {
                        int[] keys = new int[2];
                        String[] values = new String[2];
                        keys[0] = PEBBLE_KEY_DISTANCE;
                        values[0] = String.valueOf(distance(getLatitude(), getLongitude(), mFixedLatitude, mFixedLongitude));
                        keys[1] = PEBBLE_KEY_DIRECTION;
                        values[1] = direction(getLatitude(), getLongitude(), mFixedLatitude, mFixedLongitude);
                        callback.onRequest(Utils.serialize(MessageType.START_FIXED_LOCATION.ordinal(), keys, values));
                        sleep(1000);
                    } catch (InterruptedException e) {
                        Utils.log(e.getMessage());
                    }
                }

            }
        }.start();
    }


    public void stopThreadedLocation() {
        isThreaded = false;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344;
        return dist;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    private String direction(double lat1, double lon1, double lat2, double lon2) {
        double longDiff = lon2 - lon1;
        double y = Math.sin(longDiff) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(longDiff);
        return Utils.formatDirection((Math.toDegrees(Math.atan2(y, x)) + 360) % 360);
    }
}
