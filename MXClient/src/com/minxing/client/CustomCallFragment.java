/*
 * Copyright (c) 2015 Cisco and/or its affiliates.  All rights reserved.
 */

package com.minxing.client;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cisco.jabber.guest.sdk.CallBarView;
import com.cisco.jabber.guest.sdk.RemoteView;

/**
 * Fragment that uses a custom layout of Jabber Guest components.
 *
 */
public class CustomCallFragment extends Fragment {

    protected RemoteView mRemoteView;
    protected CallBarView mCallBarView;

    /*
     * (non-Javadoc)
     * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_customcallfragment, null);

        mCallBarView = (CallBarView) v.findViewById(R.id.callBarView);
        mCallBarView.setButtons(CallBarView.BUTTON_ALL);

        return v;
    }

}
