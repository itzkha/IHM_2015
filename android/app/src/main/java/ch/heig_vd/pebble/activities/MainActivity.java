package ch.heig_vd.pebble.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;
import java.util.UUID;

import ch.heig_vd.pebble.R;
import ch.heig_vd.pebble.interfaces.OnAPIRequest;
import ch.heig_vd.pebble.models.Elevation;
import ch.heig_vd.pebble.models.Location;
import ch.heig_vd.pebble.models.Transport;
import ch.heig_vd.pebble.models.Weather;
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
public class MainActivity extends Activity {

    private Switch mSwitch;
    private Button mButton;
    private TextView mPebbleStatus;
    private static TextView mLocationStatus;

    private PebbleKit.PebbleDataReceiver mDataReceiver;
    private UUID APP_UUID;
    private static final String PREFS_SESSION = "ihm_pebble";
    private static final String PREFS_UUID = "uuid";
    private static final String PEBBLE_KEY_MESSAGE = "key";

    private Elevation mElevation;
    private Location mLocation;
    private Transport mTransport;
    private Weather mWeather;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();
        mSwitch = (Switch) findViewById(R.id.switch_enable_api);
        mSwitch.setEnabled(false);
        configureUUID();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        if (mSwitch != null) {
            mSwitch.setChecked(false);
        }
        super.onPause();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode != Activity.RESULT_OK) {
                finish();
            }
        }
    }

    private void configureUUID() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        final SharedPreferences prefs = getSharedPreferences(PREFS_SESSION, Context.MODE_PRIVATE);
        final Editor editor = prefs.edit();
        edittext.setText(prefs.getString(PREFS_UUID, ""));
        alert.setMessage("UUID :");
        alert.setTitle("Pebble Configuration");
        alert.setView(edittext);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String uuid = edittext.getText().toString();
                if (uuid.isEmpty()) {
                    configureUUID();
                } else {
                    try {
                        APP_UUID = UUID.fromString(uuid);
                        editor.putString(PREFS_UUID, uuid);
                        editor.apply();
                        setupUI();
                    } catch (IllegalArgumentException e) {
                        Utils.logAndToast(MainActivity.this, e.getMessage());
                        configureUUID();
                    }
                }
            }
        });
        alert.show();
    }

    public void configureTransport(final View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        alert.setMessage("Please configure destination :");
        alert.setTitle("Transport API");
        alert.setView(edittext);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mTransport.setDestination(edittext.getText().toString());
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    private void setupUI() {
        Utils.getBLEAdapter(this);
        Utils.checkLocation(this);
        mSwitch.setEnabled(true);
        mPebbleStatus = (TextView) findViewById(R.id.pebble_status);
        mLocationStatus = (TextView) findViewById(R.id.location_status);
        mButton = (Button) findViewById(R.id.button_configure_transport);
        mButton.setEnabled(false);
        final Intent intent = new Intent(this, LocationService.class);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getPairedDevices();
                    startService(intent);
                    mLocation = new Location();
                    mLocationStatus.setText(getString(R.string.on));
                    mLocationStatus.setTextColor(getResources().getColor(R.color.green));
                    mWeather = new Weather(MainActivity.this);
                    mElevation = new Elevation(MainActivity.this);
                    mTransport = new Transport(MainActivity.this);
                    mButton.setEnabled(true);
                } else {
                    mPebbleStatus.setText(getString(R.string.off));
                    mPebbleStatus.setTextColor(getResources().getColor(R.color.red));
                    mLocationStatus.setText(getString(R.string.off));
                    mLocationStatus.setTextColor(getResources().getColor(R.color.red));
                    stopService(intent);
                    mTransport = null;
                    mElevation = null;
                    mWeather = null;
                    mLocation = null;
                    if (mDataReceiver != null) {
                        try {
                            unregisterReceiver(mDataReceiver);
                        } catch (IllegalArgumentException e) {
                            Utils.log(e.getMessage());
                        }
                    }
                    mButton.setEnabled(false);
                }
            }
        });
    }

    private void getPairedDevices() {
        BluetoothAdapter adapter = Utils.getBLEAdapter(this);
        BluetoothDevice pebble = null;
        if (adapter != null) {
            Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().contains(getString(R.string.pebble_name))) {
                        pebble = device;
                        Utils.log(pebble.getName() + " : " + pebble.getAddress());
                    }
                }
            }
        }
        if (pebble == null) {
            Utils.logAndToast(this, getString(R.string.no_pebble_paired));
            mSwitch.setChecked(false);
        } else {
            mPebbleStatus.setText(getString(R.string.on));
            mPebbleStatus.setTextColor(getResources().getColor(R.color.green));
            registerReceiver();
        }
    }

    private void registerReceiver() {
        if (mDataReceiver == null) {
            mDataReceiver = new PebbleKit.PebbleDataReceiver(APP_UUID) {
                @Override
                public void receiveData(final Context c, final int id, final PebbleDictionary dict) {
                    PebbleKit.sendAckToPebble(c, id);
                    try {
                        JSONArray arr = new JSONArray(dict.toJsonString());
                        JSONObject obj = arr.getJSONObject(0);
                        MessageType type = MessageType.getById(obj.getInt(PEBBLE_KEY_MESSAGE));
                        switch (type) {
                            case CURRENT_LOCATION:
                                mLocation.requestLocation(request);
                                break;
                            case FIX_LOCATION:
                                mLocation.fixLocation();
                                break;
                            case START_FIXED_LOCATION:
                                mLocation.startThreadedLocation(request);
                                break;
                            case STOP_FIXED_LOCATION:
                                mLocation.stopThreadedLocation();
                                break;
                            case ELEVATION:
                                mElevation.requestElevation(request, mLocation);
                                break;
                            case WEATHER:
                                mWeather.requestWeather(request, mLocation);
                                break;
                            case TEMPERATURE:
                                mWeather.requestTemperature(request, mLocation);
                                break;
                            case PRESSURE:
                                mWeather.requestPressure(request, mLocation);
                                break;
                            case HUMIDITY:
                                mWeather.requestHumidity(request, mLocation);
                                break;
                            case WIND:
                                mWeather.requestWind(request, mLocation);
                                break;
                            case SUNRISE:
                                mWeather.requestSunrise(request, mLocation);
                                break;
                            case SUNSET:
                                mWeather.requestSunset(request, mLocation);
                                break;
                            case TRANSPORT:
                                mTransport.requestTransport(request, mLocation);
                                break;
                        }
                    } catch (JSONException e) {
                        Utils.logAndToast(c, e.getMessage());
                    }
                }
            };
            PebbleKit.registerReceivedDataHandler(this, mDataReceiver);
        }
    }

    private OnAPIRequest request = new OnAPIRequest() {
        @Override
        public void onRequest(PebbleDictionary data) {
            sendData(data);
        }
    };

    private void sendData(final PebbleDictionary data) {
        PebbleKit.sendDataToPebble(getApplicationContext(), APP_UUID, data);
    }
}
