package com.minxing.client;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cisco.jabber.guest.sdk.CallFragment;
import com.cisco.jabber.guest.sdk.JabberGuestCall;
import com.cisco.jabber.guest.sdk.SelfView;

import java.util.List;

/**
 * Created by hechengbin on 2017/9/13.
 */

public class CiscoActivity extends Activity implements View.OnClickListener {

    public static final String CISCO_SERVER = "CiscoServer";
    public static final String CISCO_ADDRESS = "CiscoAddress";
    public static final String CISCO_DTMF = "CiscoDtmf";


    private String DEFAULT_SERVER = "198.18.133.222";
    private String DEFAULT_ADDRESS = "860009";
    private Uri mCallUri;
    private JabberGuestCall mInstance;
    private Activity context = CiscoActivity.this;

//    private Button btCall;
    private SelfView mSelfView;
    private LinearLayout mCallPlaceholder;

    private class JabberGuestCertificateHandler implements JabberGuestCall.JabberGuestInvalidCertificateCallback {

        @Override
        public void onInvalidCertificate(String certFingerprint,
                                         String identifierToDisplay,
                                         String certSubjectCN, String referenceID,
                                         List<String> invalidReason, String subjectCertificateData,
                                         List<String> intermediateCACertificateData,
                                         boolean allowUserToAccept) {
            JabberGuestCall.getInstance().acceptInvalidCertificate(referenceID, subjectCertificateData);
        }
    }

    JabberGuestCertificateHandler mCertificateHandler = new JabberGuestCertificateHandler();


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (JabberGuestCall.ACTION_CALL_STATE_CHANGED.equals(action)) {
                JabberGuestCall.State state = (JabberGuestCall.State) intent.getSerializableExtra(JabberGuestCall.ARG_CALL_STATE_VALUE);

                if (state == JabberGuestCall.State.GuestCallStateDisconnected) {
                    // Show the SelfView
                    adjustSelfView(false);
                    //create a new instance for the next call
                    mInstance = JabberGuestCall.createInstance(context, mCallUri);
                    hideCall();
                } else if (state == JabberGuestCall.State.GuestCallStateConnected) {
                    // Hide the SelfView
                    adjustSelfView(true);
                    showCall();
                } else if (state == JabberGuestCall.State.GuestCallStateDisconnecting) {
                    Toast.makeText(getApplicationContext(), "Ending call......", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cisco);

        Intent intent = getIntent();
        DEFAULT_SERVER = intent.getStringExtra(CISCO_SERVER);
        DEFAULT_ADDRESS = intent.getStringExtra(CISCO_ADDRESS);

        mSelfView = (SelfView) this.findViewById(R.id.selfView);
        mCallPlaceholder = (LinearLayout) findViewById(R.id.placeHolderLinearLayout);

        mCallUri = JabberGuestCall.createUri(DEFAULT_SERVER, DEFAULT_ADDRESS, null);
        Log.i("hcb", mCallUri.toString());
        mInstance = JabberGuestCall.createInstance(this, mCallUri);
        mInstance.registerContext(this);
        mInstance.setSelfTextureView(mSelfView.getTextureView());
        JabberGuestCall.registerInvalidCertificateHandler(mCertificateHandler);

//        btCall = (Button) findViewById(R.id.call);
//        btCall.setOnClickListener(this);
        showCall();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call:
                showCall();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        JabberGuestCall.registerReceiver(getApplicationContext(), mBroadcastReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JabberGuestCall.unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        hideCall();
    }

    //    protected void processCallStateChanged(Intent intent) {
//        JabberGuestCall.State state = (JabberGuestCall.State) intent.getSerializableExtra(JabberGuestCall.ARG_CALL_STATE_VALUE);
//
//        if (state == JabberGuestCall.State.GuestCallStateDisconnecting) {
//            Toast.makeText(getApplicationContext(), "Ending call......", Toast.LENGTH_LONG).show();
//        } else if (state == JabberGuestCall.State.GuestCallStateConnecting) {
//            Toast.makeText(getApplicationContext(), "Connecting......", Toast.LENGTH_LONG).show();
//        } else if (state == JabberGuestCall.State.GuestCallStateDisconnected) {
//            Intent newIntent = getIntent();
//            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(newIntent);
//            finish();
//        }
//
//    }

    /**
     * Show the Jabber Guest Call Fragment.
     */
    private void showCall() {
//        btCall.setVisibility(View.GONE);

        CustomCallFragment callFragment = (CustomCallFragment) getFragmentManager().findFragmentByTag("CallFragment");

        if (callFragment == null)
            callFragment = new CustomCallFragment();

        getFragmentManager().beginTransaction().replace(R.id.placeHolderLinearLayout, callFragment, "CallFragment").commit();

//        CallFragment callFragment = new CallFragment();
//        getFragmentManager().beginTransaction().replace(R.id.placeHolderLinearLayout, callFragment).commit();

        mCallPlaceholder.setVisibility(View.VISIBLE);

        if (mInstance != null) {
            mInstance.start();
        }
    }

    /**
     * Hide the Jabber Guest Call Fragment.
     */
    private void hideCall() {
//        btCall.setVisibility(View.VISIBLE);
//        // Hide the placeholder and progress bar
        mCallPlaceholder.setVisibility(View.GONE);
        if (mInstance != null) {
            // Reattach the SelfView to JabberGuestCall object and restart
            // the selfview video capture.
            mInstance.setSelfTextureView(mSelfView.getTextureView());
        }
        context.setResult(200);
        context.finish();
    }

    /**
     * Minimize or restore the SelfView to/from the bottom left corner and adjust the size
     * @param visible
     */
    private void adjustSelfView(boolean visible) {

//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mSelfView.getLayoutParams());
        if (visible) {
//            lp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, this.getResources().getDisplayMetrics());
//            lp.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, this.getResources().getDisplayMetrics());
//            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            mSelfView.setVisibility(View.VISIBLE);

        }
        else {
//            lp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, this.getResources().getDisplayMetrics());
//            lp.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 360, this.getResources().getDisplayMetrics());
//            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mSelfView.setVisibility(View.GONE);
        }

//        mSelfView.setLayoutParams(lp);
    }
}
