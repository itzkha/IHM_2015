package ch.heig_vd.pebble.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

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
public class LocationService extends Service {

    private static Double mCurrentLatitude = 46.779380;
    private static Double mCurrentLongitude = 6.659498;

    private LocationManager mManager;
    private LocationListener mListener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mListener = new Listener();
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = mManager.getBestProvider(criteria, true);
        long minTime = 1000;
        float distance = 0;
        mManager.requestLocationUpdates(provider, minTime, distance, mListener);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mManager.removeUpdates(mListener);
    }

    public static Double getLatitude() {
        return mCurrentLatitude;
    }

    public static Double getLongitude() {
        return mCurrentLongitude;
    }

    private class Listener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            mCurrentLatitude = location.getLatitude();
            mCurrentLongitude = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }
}