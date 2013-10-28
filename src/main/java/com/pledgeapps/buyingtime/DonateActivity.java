package com.pledgeapps.buyingtime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pledgeapps.buyingtime.billing.IabHelper;
import com.pledgeapps.buyingtime.billing.IabResult;
import com.pledgeapps.buyingtime.billing.Inventory;
import com.pledgeapps.buyingtime.billing.Purchase;
import com.pledgeapps.buyingtime.data.Transaction;
import com.pledgeapps.buyingtime.data.Transactions;
import java.util.Date;

public class DonateActivity extends Activity {

    IabHelper mHelper;
    static final int RC_REQUEST = 10001;

    TextView currentPledge;
    TextView totalDonated;
    TextView explanation;
    Button donateButton1;
    Button donateButton2;
    Button donateButton3;
    Button donateButton4;
    Button donateButton5;
    Button donateButton10;
    Button donateButton20;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_donate);

        currentPledge = (TextView) findViewById(R.id.currentPledge);
        totalDonated = (TextView) findViewById(R.id.totalDonated);
        explanation = (TextView) findViewById(R.id.explanation);


        donateButton1 = (Button) findViewById(R.id.donateButton1);
        donateButton2 = (Button) findViewById(R.id.donateButton2);
        donateButton3 = (Button) findViewById(R.id.donateButton3);
        donateButton4 = (Button) findViewById(R.id.donateButton4);
        donateButton5 = (Button) findViewById(R.id.donateButton5);
        donateButton10 = (Button) findViewById(R.id.donateButton10);
        donateButton20 = (Button) findViewById(R.id.donateButton20);


        donateButton1.setOnClickListener( new View.OnClickListener() {public void onClick(View view) {donate(1);}} );
        donateButton2.setOnClickListener( new View.OnClickListener() {public void onClick(View view) {donate(2);}} );
        donateButton3.setOnClickListener( new View.OnClickListener() {public void onClick(View view) {donate(3);}} );
        donateButton4.setOnClickListener( new View.OnClickListener() {public void onClick(View view) {donate(4);}} );
        donateButton5.setOnClickListener( new View.OnClickListener() {public void onClick(View view) {donate(5);}} );
        donateButton10.setOnClickListener( new View.OnClickListener() {public void onClick(View view) {donate(10);}} );
        donateButton20.setOnClickListener( new View.OnClickListener() {public void onClick(View view) {donate(20);}} );

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArb6ycGs7oQ3fCZvCqa9jT/z+W9zNdYvOFQOJfg3/qYE9b43HVk+18H0NEuvRztrNnDB7XkoRtNyi4/0IXLlDdMOF+ZpoLG+MqLB/WZwwBcbGbUNkVcNl8/fPCl9bnutQ5Xn7jqOjQ3UBCsjzDj/Dl5fex0P4WESVDtuGRWagfFMkqxexGoPYc6ZjhDrHWMSrHUgrMiYmnFjVNyM++sFwzoBQSasTUDN6KfJDjuqvPvvJQo600BWHsMjDi6aAw8fIW6ydJhbb8PwaxLEEbFL0H8aN5/XHOUboUzIHJDq6rMwheNm+ygP4lafk0DKKTnAtwR6zRypjfTYAO7KdAV7DXwIDAQAB";
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    return;
                }
                if (mHelper == null) return;
                mHelper.queryInventoryAsync(inventoryListener);
            }
        });
        refreshScreen();
    }

    IabHelper.QueryInventoryFinishedListener inventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (mHelper == null) return;
            if (result.isFailure()) return;

            int[] amounts = new int[]{1,2,3,4,5,10,20};

            for (int amount : amounts)
            {
                String sku = "donate_" + Integer.toString(amount);
                Purchase purchase = inventory.getPurchase(sku);
                if (purchase!=null) mHelper.consumeAsync(inventory.getPurchase(sku), consumeListener);
                return;
            }
        }
    };


    public void refreshScreen()
    {
        currentPledge.setText("Current Pledge: " + "$" + String.format("%1.2f", Transactions.getCurrent().getCurrentPledge()));
        totalDonated.setText("Total Donated: " + "$" + String.format("%1.2f", Transactions.getCurrent().getTotalDonated()));
    }

    public void donate(int amount)
    {
        String sku = "donate_" + Integer.toString(amount);
        mHelper.launchPurchaseFlow(this, sku, RC_REQUEST, purchaseListener, "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) return;
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) super.onActivityResult(requestCode, resultCode, data);
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener purchaseListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (mHelper == null) return;
            if (result.isFailure()) return;
            mHelper.consumeAsync(purchase, consumeListener);
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener consumeListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (mHelper == null) return;
            if (result.isSuccess()) {
                int amount = Integer.parseInt(purchase.getSku().replace("donate_",""));
                logPayment(amount);
            }
            finish();
        }
    };

    private void logPayment(double amount)
    {
        Transaction t = new Transaction();
        t.date = new Date();
        t.amount = -amount;
        Transactions.getCurrent().add(t);
        Transactions.getCurrent().save(this);
    }


}
