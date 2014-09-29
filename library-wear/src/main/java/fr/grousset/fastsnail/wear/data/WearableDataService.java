package fr.grousset.fastsnail.wear.data;

import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import groovy.lang.Closure;

/**
 * Service to manage Wear data API communication.
 * Based on https://github.com/Mariuxtheone/Teleport/blob/master/teleportlib/src/main/java/com/mariux/teleport/lib/TeleportClient.java
 * For some reason, this class cannot be written in Groovy.
 * @author gi.grousset@gmail.com
 */
public class WearableDataService extends WearableListenerService {

    private WearableDataClient mClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mClient = new WearableDataClient(this);
        mClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mClient.disconnect();
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
        mClient.onPeerConnected(peer);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        mClient.onDataChanged(dataEvents);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        mClient.onMessageReceived(messageEvent);
    }

    public Closure getOnSyncDataItem() {
        return mClient.getOnSyncDataItem();
    }

    public void setOnSyncDataItem(Closure onSyncDataItem) {
        this.mClient.setOnSyncDataItem(onSyncDataItem);
    }

    public Closure getOnGetMessage() {
        return mClient.getOnGetMessage();
    }

    public void setOnGetMessage(Closure onGetMessage) {
        mClient.setOnGetMessage(onGetMessage);
    }
}
