package com.garethevans.church.opensongtablet.nearby;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.collection.SimpleArrayMap;

import com.garethevans.church.opensongtablet.R;
import com.garethevans.church.opensongtablet.autoscroll.AutoscrollActions;
import com.garethevans.church.opensongtablet.filemanagement.StorageAccess;
import com.garethevans.church.opensongtablet.interfaces.MainActivityInterface;
import com.garethevans.church.opensongtablet.interfaces.NearbyInterface;
import com.garethevans.church.opensongtablet.interfaces.NearbyReturnActionsInterface;
import com.garethevans.church.opensongtablet.preferences.Preferences;
import com.garethevans.church.opensongtablet.songprocessing.ProcessSong;
import com.garethevans.church.opensongtablet.sqlite.CommonSQL;
import com.garethevans.church.opensongtablet.sqlite.SQLiteHelper;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class NearbyConnections implements NearbyInterface {

    private final Context context;
    private final Preferences preferences;
    private final StorageAccess storageAccess;
    private final ProcessSong processSong;
    private final SQLiteHelper sqLiteHelper;
    private final CommonSQL commonSQL;
    private MainActivityInterface mainActivityInterface;

    NearbyReturnActionsInterface nearbyReturnActionsInterface;

    private final ArrayList<String> connectedEndPoints;
    private final ArrayList<String> connectedEndPointsNames;
    public String deviceName, connectionLog;
    public boolean isConnected, isHost, receiveHostFiles, keepHostFiles, usingNearby;

    public NearbyConnections(Context context, Preferences preferences, StorageAccess storageAccess,
                             ProcessSong processSong, SQLiteHelper sqLiteHelper, CommonSQL commonSQL) {
        this.context = context;
        this.preferences = preferences;
        this.storageAccess = storageAccess;
        this.processSong = processSong;
        this.sqLiteHelper = sqLiteHelper;
        this.commonSQL = commonSQL;
        connectedEndPoints = new ArrayList<>();
        connectedEndPointsNames = new ArrayList<>();
    }

    // The stuff used for Google Nearby for connecting devices
    String serviceId = "com.garethevans.church.opensongtablet";
    private void updateConnectionLog(String newMessage) {
        Log.d("Nearby","message="+newMessage+"   mainActivityInterface="+mainActivityInterface);
        if (newMessage!=null && mainActivityInterface!=null) {
            connectionLog += newMessage + "\n";
            try {
                mainActivityInterface.updateConnectionsLog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setMainActivityInterface(MainActivityInterface mainActivityInterface) {
        this.mainActivityInterface = mainActivityInterface;
    }

    @Override
    public void startAdvertising(AutoscrollActions autoscrollActions) {
        try {
            if (!isConnected) {
                AdvertisingOptions advertisingOptions =
                        new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
                Nearby.getConnectionsClient(context)
                        .startAdvertising(
                                getUserNickname(), serviceId, connectionLifecycleCallback(autoscrollActions), advertisingOptions)
                        .addOnSuccessListener(
                                (Void unused) -> {
                                    // We're advertising!
                                    updateConnectionLog(context.getResources().getString(R.string.connections_advertise) + " " + getUserNickname());

                                })
                        .addOnFailureListener(
                                (Exception e) -> {
                                    // We were unable to start advertising.
                                    updateConnectionLog(context.getResources().getString(R.string.error));
                                    e.printStackTrace();
                                });
            }
        } catch (Exception e) {
            Log.d("NearbyConnections","Can't start advertising");
        }
    }

    @Override
    public void startDiscovery(AutoscrollActions autoscrollActions) {
        try {
            if (!isConnected) {
                DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
                Nearby.getConnectionsClient(context)
                        .startDiscovery(serviceId, endpointDiscoveryCallback(autoscrollActions), discoveryOptions)
                        .addOnSuccessListener(
                                (Void unused) -> {
                                    // We're discovering!
                                    updateConnectionLog(context.getResources().getString(R.string.connections_discover));
                                })
                        .addOnFailureListener(
                                (Exception e) -> {
                                    // We're unable to start discovering.
                                    updateConnectionLog(context.getResources().getString(R.string.error));
                                    stopDiscovery();
                                    e.printStackTrace();
                                });
            }
        } catch (Exception e) {
            Log.d("NearbyConnections","Can't start discovery");
        }
    }

    @Override
    public void stopAdvertising() {
        try {
            updateConnectionLog(context.getString(R.string.connections_advertise_stop));
            Nearby.getConnectionsClient(context).stopAdvertising();
            Log.d("d", "stopAdvertising()");
        } catch (Exception e) {
            updateConnectionLog(context.getString(R.string.error));
            Log.d("NearbyConnections","Can't stop advertising");
        }
    }

    @Override
    public void stopDiscovery() {
        try {
            updateConnectionLog(context.getString(R.string.connections_discover_stop));
            Nearby.getConnectionsClient(context).stopDiscovery();
            Log.d("d", "stopDiscovery()");
        } catch (Exception e) {
            updateConnectionLog(context.getString(R.string.error));
            Log.d("NearbyConnections","Can't stop discovery");
        }
    }
    private String getDeviceNameFromId(String endpointId) {
        // When requesting connections, the proper device name is stored in an arraylist like endpointId__deviceName
        for (String s:connectedEndPointsNames) {
            if (s.startsWith(endpointId+"__")) {
                return s.replace(endpointId+"__","");
            }
        }
        return endpointId;
    }

    public String getUserNickname() {
        String model = android.os.Build.MODEL.trim();
        // If the user has saved a value for their device name, use that instead
        // Don't need to save the device name unless the user edits it to make it custom
        return deviceName = preferences.getMyPreferenceString(context,"deviceName", model);
    }

    private ConnectionLifecycleCallback connectionLifecycleCallback(AutoscrollActions autoscrollActions) {
        return new ConnectionLifecycleCallback() {
            @Override
            public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                updateConnectionLog(context.getString(R.string.connections_device_found) + ": " + connectionInfo.getEndpointName());
                new AlertDialog.Builder(context)
                        .setTitle(context.getResources().getString(R.string.connections_accept) + " " + connectionInfo.getEndpointName())
                        .setMessage(context.getResources().getString(R.string.connections_accept_code) + " " + connectionInfo.getAuthenticationToken())
                        .setPositiveButton(
                                context.getResources().getString(android.R.string.ok),
                                (DialogInterface dialog, int which) -> {
                                    // For stability add a small delay
                                    Handler waitAccept = new Handler();
                                    waitAccept.postDelayed(() -> {
                                        // Add a note of the nice name matching the endpointId
                                        connectedEndPointsNames.add(endpointId + "__" + connectionInfo.getEndpointName());
                                        // The user confirmed, so we can accept the connection.
                                        Nearby.getConnectionsClient(context)
                                                .acceptConnection(endpointId, payloadCallback(autoscrollActions));
                                    },200);
                                })
                        .setNegativeButton(
                                context.getResources().getString(android.R.string.cancel),
                                (DialogInterface dialog, int which) ->
                                        // The user canceled, so we should reject the connection.
                                        Nearby.getConnectionsClient(context).rejectConnection(endpointId))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

            @Override
            public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution) {
                switch (connectionResolution.getStatus().getStatusCode()) {
                    case ConnectionsStatusCodes.STATUS_OK:
                        // We're connected! Can now start sending and receiving data.
                        connectedEndPoints.add(endpointId);
                        isConnected = true;
                        updateConnectionLog(context.getResources().getString(R.string.connections_connected) + " " + getDeviceNameFromId(endpointId));
                        // try to send the current song payload
                        sendSongPayload();
                        if (!isHost) {
                            // We can stop discovery now
                            stopDiscovery();
                        }
                        break;
                    case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                    case ConnectionsStatusCodes.STATUS_ERROR:
                        // The connection broke before it was able to be accepted.
                        // The connection was rejected by one or both sides.
                        updateConnectionLog(context.getResources().getString(R.string.connections_failure) + ": " + getUserNickname()+" <-> "+getDeviceNameFromId(endpointId));
                        break;
                    default:
                        // Unknown status code
                        Log.d("NearbyConnections","Unknown status code");
                        break;
                }
            }

            @Override
            public void onDisconnected(@NonNull String endpointId) {
                Log.d("NearbyConnections","On disconnect");
                connectedEndPoints.remove(endpointId);
                updateConnectionLog(context.getResources().getString(R.string.connections_disconnect) + " " + getDeviceNameFromId(endpointId));
                // Try to connect again after 2 seconds
                if (!isHost) {
                    Handler h = new Handler();
                    h.postDelayed(() -> startDiscovery(autoscrollActions), 2000);
                }
                // Check if we have valid connections
                isConnected = stillValidConnections();
            }
        };
    }
    private boolean stillValidConnections() {
        try {
            if (connectedEndPoints.size() >= 1) {
                for (String s : connectedEndPoints) {
                    if (s != null && !s.isEmpty()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    private EndpointDiscoveryCallback endpointDiscoveryCallback(AutoscrollActions autoscrollActions) {
        return new EndpointDiscoveryCallback() {
            @Override
            public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
                Nearby.getConnectionsClient(context)
                        .requestConnection(getUserNickname(), endpointId, connectionLifecycleCallback(autoscrollActions))
                        .addOnSuccessListener(
                                (Void unused) -> {
                                    // We successfully requested a connection. Now both sides
                                    // must accept before the connection is established.
                                    updateConnectionLog(context.getResources().getString(R.string.connections_searching));
                                })
                        .addOnFailureListener(
                                (Exception e) -> {
                                    // Nearby Connections failed to request the connection.
                                    updateConnectionLog(context.getResources().getString(R.string.connections_failure) + ": " + getDeviceNameFromId(endpointId));
                                    e.printStackTrace();
                                });
            }

            @Override
            public void onEndpointLost(@NonNull String endpointId) {
                Log.d("NearbyConnections","onEndPointlost");
                updateConnectionLog(context.getResources().getString(R.string.connections_disconnect) + " " + getDeviceNameFromId(endpointId));
            }
        };
    }
    public void sendSongPayload() {
        String infoPayload;
        Payload payloadFile = null;
        String infoFilePayload = mainActivityInterface.getSong().getFolder() + "_xx____xx_" + mainActivityInterface.getSong().getFilename() +
                "_xx____xx_" + mainActivityInterface.getSong().getNextDirection();
        if (mainActivityInterface.getSong().getIsSong()) {
            // By default, this should be smaller than 32kb, so probably going to send as bytes
            // We'll measure the actual size later to check though
            infoPayload = mainActivityInterface.getSong().getFolder() + "_xx____xx_" +
                    mainActivityInterface.getSong().getFilename() + "_xx____xx_" +
                    mainActivityInterface.getSong().getNextDirection() + "_xx____xx_" +
                    processSong.getXML(mainActivityInterface.getSong());
        } else {
            // We will send as a file instead
            infoPayload=null;
        }

        if (infoPayload!=null) {
            // Check the size.  If it is bigger than the 32kb (go 30kb to play safe!) allowed for bytes, switch to file
            byte[] mybytes = infoPayload.getBytes();
            if (mybytes.length>30000) {
                infoPayload = null;
            }
        }

        if (infoPayload==null) {
            // We will send as a file
            try {
                Uri uri = storageAccess.getUriForItem(context, preferences, "Songs", mainActivityInterface.getSong().getFolder(), mainActivityInterface.getSong().getFilename());
                ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
                if (parcelFileDescriptor!=null) {
                    payloadFile = Payload.fromFile(parcelFileDescriptor);
                    infoFilePayload = "FILE:"+payloadFile.getId()+":"+infoFilePayload;
                }
            } catch (Exception e) {
                e.printStackTrace();
                payloadFile = null;
            }
        }

        for (String endpointId:connectedEndPoints) {
            if (payloadFile!=null) {
                // Send the file descriptor as bytes, then the file
                Nearby.getConnectionsClient(context).sendPayload(endpointId,Payload.fromBytes(infoFilePayload.getBytes()));
                Nearby.getConnectionsClient(context).sendPayload(endpointId,payloadFile);
            } else if (infoPayload!=null){
                // Just send the bytes
                Nearby.getConnectionsClient(context).sendPayload(endpointId,Payload.fromBytes(infoPayload.getBytes()));
            }
        }
    }
    @Override
    public void doSendPayloadBytes(String infoPayload) {
        for (String endpointId:connectedEndPoints) {
            Nearby.getConnectionsClient(context).sendPayload(endpointId,Payload.fromBytes(infoPayload.getBytes()));
        }
    }
    private void payloadAutoscroll(AutoscrollActions autoScrollActions, String incoming) {
        // It sends autoscroll startstops as autoscroll_start or autoscroll_stop
        if (mainActivityInterface.getMode().equals("Performance")) {
            // Adjust only when not already in the correct state
            if (nearbyReturnActionsInterface != null && !(autoScrollActions.getIsAutoscrolling()==incoming.equals("autoscroll_start"))) {
                nearbyReturnActionsInterface.gesture5();
            }
        }
    }
    private void payloadSection(String incoming) {
        if (!mainActivityInterface.getMode().equals("Performance")) {
            int mysection = processSong.getNearbySection(incoming);
            if (mysection >= 0) {
                if (nearbyReturnActionsInterface != null) {
                    // Look for a section being sent
                    mainActivityInterface.getSong().setCurrentSection(mysection);
                    nearbyReturnActionsInterface.selectSection(mysection);
                }
            }
        }
    }
    private void payloadOpenSong(String incoming) {
        // New method sends OpenSong songs in the format of
        //  FOLDER_xx____xx_FILENAME_xx____xx_R2L/L2R_xx____xx_<?xml>

        ArrayList<String> receivedBits = processSong.getNearbyIncoming(incoming);
        Log.d("NearbyConnections","payloadOpenSong");
        Uri properUri = storageAccess.getUriForItem(context, preferences, "Songs", receivedBits.get(0), receivedBits.get(1));
        Uri tempUri = storageAccess.getUriForItem(context, preferences, "Received", "", "ReceivedSong");
        OutputStream outputStream;

        // Only songs sent via bytes payload trigger this.
        // Receiving an OpenSong file via bytes.  PDFs etc are sent separately
        boolean songReceived = (receivedBits.size()>=4);

        Log.d("NearbyConnections","isHost="+isHost+"   isConnected="+isConnected+"  usingNearby="+usingNearby);
        Log.d("NearbyConnections","receiveHostFiles="+receiveHostFiles+"   keepHostFiles="+keepHostFiles+"  songReceived="+songReceived);

        if (!isHost && isConnected && songReceived && receiveHostFiles) {
            // We want to receive host files (we aren't the host either!) and an OpenSong song has been sent/received
            mainActivityInterface.getSong().setNextDirection(receivedBits.get(2));

            // If the user wants to keep the host file, we will save it to our storage.
            // If we already have it, it will overwrite it, if not, we add it
            if (keepHostFiles) {
                // Prepare the output stream in the client Songs folder
                // Check the folder exists, if not, create it
                storageAccess.createFile(context,preferences, DocumentsContract.Document.MIME_TYPE_DIR,"Songs",receivedBits.get(0),"");
                // Create the file if it doesn't exist
                storageAccess.lollipopCreateFileForOutputStream(context, preferences, properUri, null, "Songs", receivedBits.get(0), receivedBits.get(1));
                outputStream = storageAccess.getOutputStream(context, properUri);
                mainActivityInterface.getSong().setFolder(receivedBits.get(0));
                mainActivityInterface.getSong().setFilename(receivedBits.get(1));
                // Add to the sqldatabase
                sqLiteHelper.createSong(context,storageAccess,commonSQL,mainActivityInterface.getSong().getFolder(),mainActivityInterface.getSong().getFilename());

            } else {
                // Prepare the output stream in the Received folder - just keep a temporary version
                storageAccess.lollipopCreateFileForOutputStream(context, preferences, tempUri, null, "Received", "", "ReceivedSong");
                outputStream = storageAccess.getOutputStream(context, tempUri);
                mainActivityInterface.getSong().setFolder("../Received");
                mainActivityInterface.getSong().setFilename("ReceivedSong");
            }

            // Write the file to the desired output stream
            if (nearbyReturnActionsInterface != null) {
                storageAccess.writeFileFromString(receivedBits.get(3), outputStream);
                nearbyReturnActionsInterface.prepareSongMenu();
            }


        } else if (!isHost && isConnected && songReceived) {
            // We just want to trigger loading the song on our device (if we have it).
            // If not, we get notified it doesn't exits
            mainActivityInterface.getSong().setFolder(receivedBits.get(0));
            mainActivityInterface.getSong().setFilename(receivedBits.get(1));
            Log.d("d","received: "+mainActivityInterface.getSong().getFolder()+"/"+mainActivityInterface.getSong().getFilename());
            mainActivityInterface.getSong().setNextDirection(receivedBits.get(2));
        }

        // Now load the song (from wherever it has ended up!)
        if (nearbyReturnActionsInterface != null) {
            nearbyReturnActionsInterface.loadSong();
        }
    }
    private void payloadFile(Payload payload, String foldernamepair) {
        // If songs are too big, then we receive them as a file rather than bytes
        Log.d("d","foldernamepair="+foldernamepair);
        String[] bits = foldernamepair.split("_xx____xx_");
        String folder = bits[0];
        String filename = bits[1];
        mainActivityInterface.getSong().setNextDirection(bits[2]);
        boolean movepage = false;
        if ((folder.equals(mainActivityInterface.getSong().getFolder())||mainActivityInterface.getSong().getFolder().equals("../Received"))
                && filename.equals(mainActivityInterface.getSong().getFilename()) && filename.toLowerCase(mainActivityInterface.getLocale()).endsWith(".pdf")) {
            // We are likely trying to move page to an already received file
            movepage = true;
        } else {
            mainActivityInterface.getSong().setPdfPageCurrent(0);
        }
        Uri newLocation = null;
        if (!isHost && isConnected && receiveHostFiles && keepHostFiles) {
            // The new file goes into our main Songs folder
            newLocation = storageAccess.getUriForItem(context,preferences,"Songs",folder,filename);
            storageAccess.lollipopCreateFileForOutputStream(context,preferences,newLocation,null,"Songs",folder,filename);
        } else if (!isHost && isConnected && receiveHostFiles) {
            // The new file goes into our Received folder
            folder = "../Received";
            newLocation = storageAccess.getUriForItem(context, preferences, "Received", "", filename);
            storageAccess.lollipopCreateFileForOutputStream(context, preferences, newLocation, null, "Received", "", filename);
        }
        mainActivityInterface.getSong().setFolder(folder);
        mainActivityInterface.getSong().setFilename(filename);
        Log.d("d","newLocation="+newLocation);
        if (movepage) {
            if (mainActivityInterface.getSong().getNextDirection().equals("L2R")) {
                // Go back
                if (nearbyReturnActionsInterface!=null) {
                    nearbyReturnActionsInterface.goToPreviousItem();
                }
            } else {
                // Go forward
                if (nearbyReturnActionsInterface!=null) {
                    nearbyReturnActionsInterface.goToNextItem();
                }
            }
        } else if (newLocation!=null && payload!=null && payload.asFile()!=null) { // i.e. we have received the file by choice
            InputStream inputStream = new FileInputStream(Objects.requireNonNull(payload.asFile()).asParcelFileDescriptor().getFileDescriptor());
            Uri originalUri = Uri.parse(Objects.requireNonNull(payload.asFile()).asParcelFileDescriptor().getFileDescriptor().toString());
            OutputStream outputStream = storageAccess.getOutputStream(context, newLocation);
            if (storageAccess.copyFile(inputStream, outputStream)) {
                if (nearbyReturnActionsInterface!=null) {
                    nearbyReturnActionsInterface.loadSong();
                }
            }
            Log.d("d","originalUri="+originalUri);
            storageAccess.deleteFile(context, originalUri);
        } else {
            if (nearbyReturnActionsInterface!=null) {
                nearbyReturnActionsInterface.loadSong();
            }
        }
    }
    private final SimpleArrayMap<Long, Payload> incomingFilePayloads = new SimpleArrayMap<>();
    private final SimpleArrayMap<Long, String> fileNewLocation = new SimpleArrayMap<>();
    private PayloadCallback payloadCallback(AutoscrollActions autoscrollActions) {
        return new PayloadCallback() {
            @Override
            public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
                if (!isHost) {
                    // We can deal with the incoming payload!
                    if (payload.getType() == Payload.Type.FILE) {
                        // Make a note of it.  Nothing happens until complete
                        incomingFilePayloads.put(payload.getId(), payload);

                    } else if (payload.getType() == Payload.Type.BYTES) {
                        // We're dealing with bytes
                        String incoming = null;
                        if (payload.asBytes() != null) {
                            byte[] bytes = payload.asBytes();
                            if (bytes!=null) {
                                incoming = new String(bytes);
                            }
                        }
                        if (incoming != null && incoming.startsWith("FILE:")) {
                            // Add the file location to the arraymap
                            incoming = incoming.replaceFirst("FILE:", "");
                            String id = incoming.substring(0, incoming.indexOf(":"));
                            id = id.replace(":", "");
                            String foldernamepair = incoming.substring(incoming.indexOf(":"));
                            foldernamepair = foldernamepair.replace(":", "");
                            fileNewLocation.put(Long.parseLong(id), foldernamepair);

                        } else if (incoming != null && incoming.contains("autoscroll_")) {
                            payloadAutoscroll(autoscrollActions, incoming);
                        } else if (incoming != null && incoming.contains("___section___")) {
                            payloadSection(incoming);
                        } else if (incoming != null) {
                            payloadOpenSong(incoming);
                        }
                    }
                    // not dealing with files as it is complex with scoped storage access
                    // also don't want user's download folder getting clogged!
                }
            }


            @Override
            public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
                if (payloadTransferUpdate.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {
                    // For bytes this is sent automatically, but it's the file we are interested in here
                    Payload payload;
                    if (incomingFilePayloads.containsKey(payloadTransferUpdate.getPayloadId())) {
                        payload = incomingFilePayloads.get(payloadTransferUpdate.getPayloadId());
                        String foldernamepair = fileNewLocation.get(payloadTransferUpdate.getPayloadId());
                        if (foldernamepair==null) {
                            foldernamepair = "../Received_xx____xx_ReceivedSong";
                        }
                        incomingFilePayloads.remove(payloadTransferUpdate.getPayloadId());
                        fileNewLocation.remove(payloadTransferUpdate.getPayloadId());

                        payloadFile(payload,foldernamepair);
                    }
                }
            }
        };
    }
    @Override
    public void turnOffNearby() {
        try {
            Log.d("d", "turnOffNearby()");
            Nearby.getConnectionsClient(context).stopAllEndpoints();
        } catch (Exception e) {
            Log.d("NearbyConnections","Can't turn off nearby");
        }
        isConnected = false;
        usingNearby = false;
    }
}