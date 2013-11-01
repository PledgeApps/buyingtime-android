package com.pledgeapps.buyingtime;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pledgeapps.buyingtime.data.Transaction;
import com.pledgeapps.buyingtime.data.Transactions;

import java.util.Date;
import java.util.Random;

public class DonationAmountFragment extends DialogFragment {

    private Handler handler;
    EditText amountText;
    int answer = 0;

    static DonationAmountFragment newInstance(Handler h)
    {
        DonationAmountFragment modal = new DonationAmountFragment();
        modal.handler = h;
        return modal;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Random rnd = new Random();
        int num1 = rnd.nextInt(99) + 1;
        int num2 = rnd.nextInt(99) + 1;
        answer = num1 + num2;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_donation_amount, null);
        amountText = (EditText) view.findViewById(R.id.amountText);

        builder.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        donate();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { DonationAmountFragment.this.getDialog().cancel();     }
                });
        return builder.create();
    }


    private void donate()
    {
        double amount = Double.parseDouble(amountText.getText().toString());
        Transaction t = new Transaction();
        t.date = new Date();
        t.amount = -amount;
        Transactions.getCurrent().add(t);
        Transactions.getCurrent().save(getActivity());
        Message m = new Message();
        handler.sendMessage(m);
    }


}
