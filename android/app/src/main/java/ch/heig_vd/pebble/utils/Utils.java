package ch.heig_vd.pebble.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.getpebble.android.kit.util.PebbleDictionary;

import ch.heig_vd.pebble.R;

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
public class Utils {

    /***********************************************************************************************
     * Bluetooth specific
     **********************************************************************************************/
    public static final int REQUEST_ENABLE_BLUETOOTH = 1;

    public static BluetoothAdapter getBLEAdapter(final Activity a) {
        BluetoothManager bm = (BluetoothManager) a.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = bm.getAdapter();
        if (adapter == null) {
            finishingAlert(a, R.string.bluetooth_error_title,
                    R.string.bluetooth_error_not_detected);
        } else if (!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            a.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        } else if (!adapter.isMultipleAdvertisementSupported()) {
            finishingAlert(a, R.string.bluetooth_error_title,
                    R.string.bluetooth_error_multiple_advertising_not_supported);
        } else if (!adapter.isOffloadedFilteringSupported()) {
            finishingAlert(a, R.string.bluetooth_error_title,
                    R.string.bluetooth_error_filtering_not_supported);
        } else if (!adapter.isOffloadedScanBatchingSupported()) {
            finishingAlert(a, R.string.bluetooth_error_title,
                    R.string.bluetooth_error_scan_batching_not_supported);
        }
        return adapter;
    }

    /***********************************************************************************************
     * Location specific
     **********************************************************************************************/
    public static void checkLocation(final Activity a) {
        LocationManager lm = (LocationManager) a.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            log(ex.getMessage());
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            log(ex.getMessage());
        }

        if (!gpsEnabled && !networkEnabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(a);
            dialog.setMessage(a.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(a.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    a.startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(a.getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    a.finish();
                }
            });
            dialog.show();
        }
    }


    public static String formatDirection(final double degree) {
        if (degree >= 11.25 && degree < 33.75) {
            return "NNE";
        } else if (degree >= 33.75 && degree < 56.25) {
            return "NE";
        } else if (degree >= 56.25 && degree < 78.75) {
            return "ENE";
        } else if (degree >= 78.75 && degree < 101.25) {
            return "E";
        } else if (degree >= 101.25 && degree < 123.75) {
            return "ESE";
        } else if (degree >= 123.75 && degree < 146.25) {
            return "SE";
        } else if (degree >= 146.25 && degree < 168.75) {
            return "SSE";
        } else if (degree >= 168.75 && degree < 191.25) {
            return "S";
        } else if (degree >= 191.25 && degree < 213.75) {
            return "SSW";
        } else if (degree >= 213.75 && degree < 236.25) {
            return "SW";
        } else if (degree >= 236.25 && degree < 258.75) {
            return "WSW";
        } else if (degree >= 258.75 && degree < 281.25) {
            return "W";
        } else if (degree >= 281.25 && degree < 303.75) {
            return "WNW";
        } else if (degree >= 303.75 && degree < 326.25) {
            return "NW";
        } else if (degree >= 326.25 && degree < 348.75) {
            return "NNW";
        } else {
            return "N";
        }
    }

    /***********************************************************************************************
     * Pebble specific
     **********************************************************************************************/
    private static final int PEBBLE_KEY_VALUE = 1;

    public static PebbleDictionary serialize(final int value, final int[] keys, final String[] values) {
        PebbleDictionary data = new PebbleDictionary();
        data.addInt32(PEBBLE_KEY_VALUE, value);
        for (int i = 0; i < keys.length; ++i) {
            data.addString(keys[i], values[i]);
        }
        return data;
    }


    /***********************************************************************************************
     * Log specific
     **********************************************************************************************/
    private static final String TAG = "Pebble IHM";

    public static void finishingAlert(final Activity a, final int resTitle, final int resMessage) {
        new AlertDialog.Builder(a)
                .setTitle(a.getString(resTitle))
                .setMessage(a.getString(resMessage))
                .setPositiveButton(a.getString(R.string.dialog_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                a.finish();
                            }
                        }).show();
    }

    public static void logAndToast(final Context c, final String message) {
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
        log(message);
    }

    public static void log(final String message) {
        Log.i(TAG, message);
    }
}
