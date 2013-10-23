package com.pledgeapps.buyingtime;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pledgeapps.buyingtime.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    String previousDisplayTime = "";
    Handler refreshHandler;
    TextView currentTime;
    SimpleDateFormat formatter = new SimpleDateFormat("h:mma");



    public MainFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        refreshHandler= new Handler();
        refreshHandler.postDelayed(refreshRunnable, 1000);


        currentTime = (TextView) rootView.findViewById(R.id.currentTime);
        updateScreen(true);
        return rootView;

    }


    private Runnable refreshRunnable = new Runnable() {
        public void run() {
            try {
                updateScreen(false);
            } catch (Exception e) {}
            refreshHandler.postDelayed(this, 1000);
        }
    };


    public void updateScreen(boolean forceRefresh)
    {
        String displayTime = formatter.format(new Date()).toLowerCase().replace("m", "");
        if (!displayTime.equals(previousDisplayTime))
        {
            //currentTime.setText("Jeremy");
            currentTime.setText(displayTime);
            previousDisplayTime=displayTime;
        }
    }

}