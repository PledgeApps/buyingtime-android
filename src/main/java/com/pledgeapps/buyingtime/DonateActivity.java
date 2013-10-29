package com.pledgeapps.buyingtime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
    Button donateButton;
    Spinner donationAmount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_donate);

        currentPledge = (TextView) findViewById(R.id.currentPledge);
        totalDonated = (TextView) findViewById(R.id.totalDonated);
        explanation = (TextView) findViewById(R.id.explanation);

        donationAmount = (Spinner) findViewById(R.id.donationAmount);
        donateButton = (Button) findViewById(R.id.donateButton);

        donateButton.setOnClickListener( new View.OnClickListener() {public void onClick(View view) {donate();}} );



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.donation_amounts, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        donationAmount.setAdapter(adapter);


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

    public void donate()
    {
        Object selectedItem = donationAmount.getSelectedItem();
        String selectedValue = selectedItem.toString().replace("$","").replace(".00","");
        int amount = Integer.parseInt(selectedValue);
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
