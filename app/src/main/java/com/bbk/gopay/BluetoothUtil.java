package com.bbk.gopay;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import java.util.UUID;

/**
 * Created by gopaychan on 2016/12/13.
 */
public class BluetoothUtil {
    private Context mContext;

    public static final int SERVER_PORT = 5161;
    public static final int ENABLE_REQUEST_CODE = 0x1000;
    private static final String TAG = "BluetoothUtil";
    private BluetoothUtil(){

    }

    public static void openBluetooth(Activity activity){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent,ENABLE_REQUEST_CODE);
    }

    public static UUID getUuid(Context context){
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        return new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
    }

//    private void startRegistration() {
//        //  Create a string map containing information about your service.
//        Map record = new HashMap();
//        record.put("listenport", String.valueOf(SERVER_PORT));
//        record.put("buddyname", "John Doe" + (int) (Math.random() * 1000));
//        record.put("available", "visible");
//
//        // Service information.  Pass it an instance name, service type
//        // _protocol._transportlayer , and the map containing
//        // information other devices will want once they connect to this one.
//        WifiP2pDnsSdServiceInfo serviceInfo =
//                WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record);
//
//        // Add the local service, sending the service info, network channel,
//        // and listener that will be used to indicate success or failure of
//        // the request.
//        WifiP2pManager mManager = (WifiP2pManager)mContext.getSystemService(Context.WIFI_P2P_SERVICE);
//        mManager.addLocalService(WifiP2pManager.Channel, serviceInfo, new WifiP2pManager.ActionListener() {
//            @Override
//            public void onSuccess() {
//                // Command successful! Code isn't necessarily needed here,
//                // Unless you want to update the UI or add logging statements.
//            }
//
//            @Override
//            public void onFailure(int arg0) {
//                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
//            }
//        });
//    }
//
//    final HashMap<String, String> buddies = new HashMap<String, String>();
//    private void discoverService() {
//        WifiP2pManager mManager = (WifiP2pManager)mContext.getSystemService(Context.WIFI_P2P_SERVICE);
//        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
//            @Override
//        /* Callback includes:
//         * fullDomain: full domain name: e.g "printer._ipp._tcp.local."
//         * record: TXT record dta as a map of key/value pairs.
//         * device: The device running the advertised service.
//         */
//
//            public void onDnsSdTxtRecordAvailable(
//                    String fullDomain, Map record, WifiP2pDevice device) {
//                Log.d(TAG, "DnsSdTxtRecord available -" + record.toString());
//                buddies.put(device.deviceAddress, String.valueOf(record.get("buddyname")));
//            }
//        };
//
//        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
//            @Override
//            public void onDnsSdServiceAvailable(String instanceName, String registrationType,
//                                                WifiP2pDevice resourceType) {
//
//                // Update the device name with the human-friendly version from
//                // the DnsTxtRecord, assuming one arrived.
//                resourceType.deviceName = buddies
//                        .containsKey(resourceType.deviceAddress) ? buddies
//                        .get(resourceType.deviceAddress) : resourceType.deviceName;
//
//                // Add to the custom adapter defined specifically for showing
//                // wifi devices.
//                WiFiDirectServicesList fragment = (WiFiDirectServicesList) getFragmentManager()
//                        .findFragmentById(R.id.frag_peerlist);
//                WiFiDevicesAdapter adapter = ((WiFiDevicesAdapter) fragment
//                        .getListAdapter());
//
//                adapter.add(resourceType);
//                adapter.notifyDataSetChanged();
//                Log.d(TAG, "onBonjourServiceAvailable " + instanceName);
//            }
//        };
//
//        mManager.setDnsSdResponseListeners(channel, servListener, txtListener);
//    }
}
