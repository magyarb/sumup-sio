package com.sumup.sdksampleapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.sumup.merchant.Models.TransactionInfo;
import com.sumup.merchant.api.SumUpAPI;
import com.sumup.merchant.api.SumUpPayment;

import java.math.BigDecimal;

import static java.lang.Math.round;

public class TipGetterActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_PAYMENT = 2;
    private static final int REQUEST_CODE_PAYMENT_SETTINGS = 3;

    public int tip = 0;
    public int amount = 0;
    public int id = 0;
    public String uuid = "";

    protected void doTx()
    {

        SumUpPayment payment = SumUpPayment.builder()
                // mandatory parameters
                .total(new BigDecimal(amount)) // minimum 1.00
                .currency(SumUpPayment.Currency.HUF)
                .tip(new BigDecimal(tip))
                // optional: add details
                .title("Tx #" + id)
                .receiptEmail("tx@kiadta.com")
                .receiptSMS("+36204091668")
                // optional: Add metadata
                .addAdditionalInfo("txid", String.valueOf(id))
                // optional: foreign transaction ID, must be unique!
                .foreignTransactionId(uuid) // can not exceed 128 chars
                .skipSuccessScreen()
                .build();

        SumUpAPI.checkout(TipGetterActivity.this, payment, REQUEST_CODE_PAYMENT);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        uuid = getIntent().getStringExtra("EXTRA_UUID");
        amount = getIntent().getIntExtra("EXTRA_AMOUNT", 0);
        id = getIntent().getIntExtra("EXTRA_ID", 0);
        setContentView(R.layout.activity_tip_getter);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //buttons
        final Button button0 = findViewById(R.id.button);
        final Button button5 = findViewById(R.id.button2);
        final Button button10 = findViewById(R.id.button3);
        final Button button15 = findViewById(R.id.button4);
        final Button button20 = findViewById(R.id.button6);

        button0.setText("0% - " + round(amount * 1.0) + " HUF");
        button5.setText("5% - " + round(amount * 1.05) + " HUF");
        button10.setText("10% - " + round(amount * 1.1) + " HUF");
        button15.setText("15% - " + round(amount * 1.15) + " HUF");
        button20.setText("20% - " + round(amount * 1.2) + " HUF");

        button0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                tip = (int) round(amount * 1.0) - amount;
                doTx();
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                tip = (int) round(amount * 1.05) - amount;
                doTx();
            }
        });

        button10.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                tip = (int) round(amount * 1.10) - amount;
                doTx();
            }
        });

        button15.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                tip = (int) round(amount * 1.15) - amount;
                doTx();
            }
        });

        button20.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                tip = (int) round(amount * 1.20) - amount;
                doTx();
            }
        });



        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // getIntent() should always return the most recent
        Log.d("kiadta", "new intent");
        setIntent(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE_PAYMENT:
                if (data != null) {
                    Log.d("kiadta", "TG result");
                    Bundle extra = data.getExtras();
                    int resultcode = extra.getInt(SumUpAPI.Response.RESULT_CODE);
                    if(resultcode == SumUpAPI.Response.ResultCode.SUCCESSFUL){
                        TransactionInfo transactionInfo = extra.getParcelable(SumUpAPI.Response.TX_INFO);
                        //success tx
                        String payment_uuid = extra.getString("foreign-tx-id");
                        Intent result = new Intent("return");
                        result.putExtra("uuid", payment_uuid);
                        result.putExtra("txinfo", transactionInfo);
                        setResult(resultcode, result);
                        finish();
                    }
                    else{
                        String payment_uuid = extra.getString("foreign-tx-id");
                        Intent result = new Intent("return");
                        result.putExtra("uuid", payment_uuid);
                        setResult(resultcode, result);
                        finish();
                    }


                }
                break;


            default:
                break;
        }
    }
}
