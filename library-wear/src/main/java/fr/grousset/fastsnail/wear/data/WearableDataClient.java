package fr.grousset.fastsnail.wear.data;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import groovy.lang.Closure;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;
import rx.util.async.Async;

/**
 * Client object to manage Wear data API communication.
 * Based on https://github.com/Mariuxtheone/Teleport/blob/master/teleportlib/src/main/java/com/mariux/teleport/lib/TeleportClient.java
 * For some reason, this class cannot be written in Groovy.
 * @author gi.grousset@gmail.com
 */
public class WearableDataClient implements DataApi.DataListener,
        MessageApi.MessageListener, NodeApi.NodeListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "WearableDataClient";

    private GoogleApiClient mGoogleApiClient;

    private Closure mOnSyncDataItem;
    private Closure mOnGetMessage;

    public WearableDataClient(Context context) {

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    public void connect() {
        Log.d(TAG, "Connect");
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        Log.d(TAG, "Disconnect");
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        Wearable.NodeApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Wearable.NodeApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);

        for (DataEvent event : events) {

            if (event.getType() == DataEvent.TYPE_CHANGED) {

                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                final DataMap dataMap = dataMapItem.getDataMap();

                if (getOnSyncDataItem() != null) {

                    Async.start(new Func0() {
                        @Override
                       public Object call() {
                            return getOnSyncDataItem().call(dataMap);
                        }
                    }, AndroidSchedulers.mainThread());
                }

            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.d(TAG, "DataItem Deleted ${event.dataItem}");
            }
        }

    }


    public void syncString(String key, String item) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/" + key);
        putDataMapRequest.getDataMap().putString(key, item);
        syncDataItem(putDataMapRequest);
    }

    public void syncInt(String key, int item) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/" + key);
        putDataMapRequest.getDataMap().putInt(key, item);
        syncDataItem(putDataMapRequest);
    }

    public void syncLong(String key, long item) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/" + key);
        putDataMapRequest.getDataMap().putLong(key, item);
        syncDataItem(putDataMapRequest);
    }

    public void syncBoolean(String key, boolean item) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/" + key);
        putDataMapRequest.getDataMap().putBoolean(key, item);
        syncDataItem(putDataMapRequest);
    }

    public void syncByteArray(String key, byte[] item) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/" + key);
        putDataMapRequest.getDataMap().putByteArray(key, item);
        syncDataItem(putDataMapRequest);
    }

    public void syncByte(String key, byte item) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/" + key);
        putDataMapRequest.getDataMap().putByte(key, item);
        syncDataItem(putDataMapRequest);
    }

    public void syncAsset(String key, Asset item) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/" + key);
        putDataMapRequest.getDataMap().putAsset(key, item);
        syncDataItem(putDataMapRequest);
    }

    public void syncAll(DataMap item) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/dataMap");
        putDataMapRequest.getDataMap().putAll(item);
        syncDataItem(putDataMapRequest);
    }

    public void syncDataItem(PutDataMapRequest putDataMapRequest) {

        PutDataRequest request = putDataMapRequest.asPutDataRequest();

        Log.d(TAG, "Generating DataItem: " + request);
        if (mGoogleApiClient.isConnected()) {

            Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    if (!dataItemResult.getStatus().isSuccess()) {
                        Log.e(TAG, "Failed to putDataItem, status code: " + dataItemResult.getStatus().getStatusCode());
                    }

                }
            });
        }

    }

    private Collection<String> getNodes() {

        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

        for(Node node : nodes.getNodes()) {
            results.add(node.getId());
        }

        return results;
    }

    private void propagateMessageToNode(String node, String path, byte[] payload) {

        Wearable.MessageApi.sendMessage(
                mGoogleApiClient, node, path, payload).setResultCallback(

                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to send message with status code: " + sendMessageResult.getStatus().getStatusCode());
                        }
                    }
                }
        );

    }

    public void sendMessage(final String path, final byte[] payload) {

        Async.start(new Func0() {
            @Override
            public Object call() {
                Collection<String> nodes = getNodes();
                for(String node : nodes) {
                    propagateMessageToNode(node, path, payload);
                }

                return null;
            }
        }, Schedulers.io());

    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {

        Log.d(TAG, "Message received: " + messageEvent.getRequestId() + " " +  messageEvent.getPath());

        if (getOnGetMessage() !=null) {
            Async.start(new Func0() {
                @Override
                public Object call() {
                    return getOnGetMessage().call(messageEvent);
                }
            }, AndroidSchedulers.mainThread());
        }
    }

    @Override
    public void onPeerConnected(com.google.android.gms.wearable.Node node) {

    }

    @Override
    public void onPeerDisconnected(com.google.android.gms.wearable.Node node) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public Closure getOnSyncDataItem() {
        return mOnSyncDataItem;
    }

    public void setOnSyncDataItem(Closure onSyncDataItem) {
        this.mOnSyncDataItem = onSyncDataItem;
    }

    public Closure getOnGetMessage() {
        return mOnGetMessage;
    }

    public void setOnGetMessage(Closure onGetMessage) {
        this.mOnGetMessage = onGetMessage;
    }
}