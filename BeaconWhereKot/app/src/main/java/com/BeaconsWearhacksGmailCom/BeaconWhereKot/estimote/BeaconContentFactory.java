package com.BeaconsWearhacksGmailCom.BeaconWhereKot.estimote;

public interface BeaconContentFactory {

    void getContent(BeaconID beaconID, Callback callback);

    interface Callback {
        void onContentReady(Object content);
    }
}
