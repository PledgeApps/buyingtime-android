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

import java.util.Random;

public class DismissFragment extends DialogFragment {

    private Handler handler;
    EditText answerText;
    int answer = 0;

    static DismissFragment newInstance(Handler h)
    {
        DismissFragment modal = new DismissFragment();
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
        View view = inflater.inflate(R.layout.fragment_dismiss, null);

        TextView equationText = (TextView) view.findViewById(R.id.equationText);
        answerText = (EditText) view.findViewById(R.id.answerText);
        equationText.setText(Integer.toString(num1) + " + " + Integer.toString(num2) + " =");

        builder.setView(view)
                .setPositiveButton("Solve", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {}
                })
                .setNegativeButton("Snooze", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { DismissFragment.this.getDialog().cancel();     }
                });
        return builder.create();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (solve()) {
                        handler.sendMessage(new Message());
                        DismissFragment.this.getDialog().dismiss();
                    }
                }
            });
        }
    }

    public boolean solve()
    {
        int userAnswer = Integer.parseInt(answerText.getText().toString());
        return userAnswer == answer;
    }



}
