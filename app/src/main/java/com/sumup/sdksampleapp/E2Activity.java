package com.sumup.sdksampleapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.sumup.merchant.Models.TransactionInfo;
import com.sumup.merchant.api.SumUpAPI;
import com.sumup.merchant.api.SumUpPayment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.UUID;

public class E2Activity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_PAYMENT = 2;
    private static final int REQUEST_CODE_PAYMENT_SETTINGS = 3;

    private Socket mSocket;

    {
        try {
            //mSocket = IO.socket("http://192.168.43.172:3456/");
            mSocket = IO.socket("http://192.168.0.58:3456/");
            Log.d("kiadta", "socket connected");
            attemptSend();

        } catch (URISyntaxException e) {
            Log.d("kiadta", "connection error");
        }
    }

    private void attemptSend() {
        Log.d("kiadta", "AS");

        String message = "hello";
        if (TextUtils.isEmpty(message)) {
            return;
        }
        mSocket.emit("subscribe", message);
    }

    private Emitter.Listener onNewPayment = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("kiadta", "newpayment");
                    try {
                        int SumPrice = 0;
                        JSONObject data = (JSONObject) args[0];
                        JSONArray prod = data.getJSONArray("products");
                        for(int i=0;i<prod.length();i++)
                        {
                            JSONObject product= prod.getJSONObject(i);
                            int price = product.getInt("price");
                            int cnt = product.getInt("cnt");
                            SumPrice += (price * cnt);
                            String name = product.getString("name");
                        }
                        int id = data.getInt("txid");

                        Log.d("kiadta price", String.valueOf(SumPrice));
                        Log.d("kiadta id", String.valueOf(id));

                        String uuid = UUID.randomUUID().toString();

                        //send card-tx-started (id, uuid, tipAmount)
                        mSocket.emit("card-tx-started", id, uuid);

                        Intent openActivity = new Intent(E2Activity.this, TipGetterActivity.class);
                        openActivity.putExtra("EXTRA_UUID", uuid);
                        openActivity.putExtra("EXTRA_AMOUNT", SumPrice);
                        openActivity.putExtra("EXTRA_ID", id);
                        openActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(openActivity, REQUEST_CODE_PAYMENT);
                        //get tip in
                        /*

                        SumUpPayment payment = SumUpPayment.builder()
                                // mandatory parameters
                                .total(new BigDecimal(SumPrice)) // minimum 1.00
                                .currency(SumUpPayment.Currency.HUF)
                                .tip(new BigDecimal("10"))
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

                        SumUpAPI.checkout(E2Activity.this, payment, REQUEST_CODE_PAYMENT);
                        */
                    } catch (Exception e) {
                        Log.d("kiadta", "error in json parsing");
                        return;
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_e2);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mSocket.on("newpayment-card", onNewPayment);
        mSocket.connect();
        SumUpAPI.prepareForCheckout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE_PAYMENT:
                if (data != null) {
                    Log.d("kiadta", "E2 result");
                    Bundle extra = data.getExtras();

                    String uuid = data.getStringExtra("uuid");

                    if(resultCode == SumUpAPI.Response.ResultCode.SUCCESSFUL)
                    {
                        TransactionInfo transactionInfo = extra.getParcelable(SumUpAPI.Response.TX_INFO);
                        mSocket.emit("card-tx-finished", uuid, resultCode, transactionInfo.getTipAmount(), transactionInfo.getAmount());
                    }
                    else{
                        mSocket.emit("card-tx-finished", uuid, resultCode);
                    }
                }
                break;


            default:
                break;
        }
    }
}
