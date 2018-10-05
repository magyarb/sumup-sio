package com.sumup.sdksampleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sumup.merchant.Models.TransactionInfo;
import com.sumup.merchant.api.SumUpAPI;
import com.sumup.merchant.api.SumUpLogin;
import com.sumup.merchant.api.SumUpPayment;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.UUID;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_PAYMENT = 2;
    private static final int REQUEST_CODE_PAYMENT_SETTINGS = 3;

    private TextView mResultCode;
    private TextView mResultMessage;
    private TextView mTxCode;
    private TextView mReceiptSent;
    private TextView mTxInfo;
/*
    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("http://192.168.43.172:3456/");
            Log.d("kiadta", "MUKODIK2");
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

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("kiadta", "run()");
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }

                    // add the message to view
                    addMessage(username, message);
                }
            });
        }
        private void addMessage(String username, String message) {
            Log.d("kiadta", "addmessage()");
        }
    };

    private Emitter.Listener onNewC = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("kiadta", "newc      run()");
                    //JSONObject data = (JSONObject) args[0];
                    //JSONObject data;
                    String username = "";
                    String message = "";
                    try {
                        Log.d("kiadta", (String)args[0]);
                        //username = data.getString("username");
                        //message = data.getString("message");
                    } catch (Exception e) {
                        return;
                    }

                    // add the message to view
                    addMessage(username, message);
                }
            });
        }


        private void addMessage(String username, String message) {
            Log.d("kiadta", "addmessage()");
        }
    };

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

                        SumUpAPI.checkout(MainActivity.this, payment, REQUEST_CODE_PAYMENT);
                    } catch (Exception e) {
                        Log.d("kiadta", "error in json parsing");
                        return;
                    }
                }
            });
        }


        private void addMessage(String username, String message) {
            Log.d("kiadta", "addmessage()");
        }
    };
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("kiadta", "start");

        findViews();
        //mSocket.on("new message", onNewMessage);
        //mSocket.on("newc", onNewC);
        //mSocket.on("newpayment-card", onNewPayment);
        //mSocket.connect();
        Button login = (Button) findViewById(R.id.button_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("kiadta", "MUKODIK");
                // Please go to https://me.sumup.com/developers to get your Affiliate Key by entering the application ID of your app. (e.g. com.sumup.sdksampleapp)
                SumUpLogin sumupLogin = SumUpLogin.builder("7ca84f17-84a5-4140-8df6-6ebeed8540fc").build();
                SumUpAPI.openLoginActivity(MainActivity.this, sumupLogin, REQUEST_CODE_LOGIN);
            }
        });

        Button btnCharge = (Button) findViewById(R.id.button_charge);
        final TextView ipfield = (TextView) findViewById(R.id.editText);

        btnCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openActivity = new Intent(MainActivity.this, E2Activity.class);
                openActivity.putExtra("IP", ipfield.getText().toString());
                startActivity(openActivity);

            }
        });

        Button paymentSettings = (Button) findViewById(R.id.button_payment_settings);
        paymentSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpAPI.openPaymentSettingsActivity(MainActivity.this, REQUEST_CODE_PAYMENT_SETTINGS);
            }
        });

        Button prepareCardTerminal = (Button) findViewById(R.id.button_prepare_card_terminal);
        prepareCardTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpAPI.prepareForCheckout();
            }
        });

        Button btnLogout = (Button) findViewById(R.id.button_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SumUpAPI.logout();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        resetViews();

        switch (requestCode) {
            case REQUEST_CODE_LOGIN:
                if (data != null) {
                    Bundle extra = data.getExtras();
                    mResultCode.setText("Result code: " + extra.getInt(SumUpAPI.Response.RESULT_CODE));
                    mResultMessage.setText("Message: " + extra.getString(SumUpAPI.Response.MESSAGE));
                }
                break;

            case REQUEST_CODE_PAYMENT:
                if (data != null) {
                    Bundle extra = data.getExtras();

                    mResultCode.setText("Result code: " + extra.getInt(SumUpAPI.Response.RESULT_CODE));
                    mResultMessage.setText("Message: " + extra.getString(SumUpAPI.Response.MESSAGE));

                    String txCode = extra.getString(SumUpAPI.Response.TX_CODE);
                    mTxCode.setText(txCode == null ? "" : "Transaction Code: " + txCode);

                    boolean receiptSent = extra.getBoolean(SumUpAPI.Response.RECEIPT_SENT);
                    mReceiptSent.setText("Receipt sent: " + receiptSent);

                    TransactionInfo transactionInfo = extra.getParcelable(SumUpAPI.Response.TX_INFO);
                    mTxInfo.setText(transactionInfo == null ? "" : "Transaction Info : " + transactionInfo);

                }
                break;

            case REQUEST_CODE_PAYMENT_SETTINGS:
                if (data != null) {
                    Bundle extra = data.getExtras();
                    mResultCode.setText("Result code: " + extra.getInt(SumUpAPI.Response.RESULT_CODE));
                    mResultMessage.setText("Message: " + extra.getString(SumUpAPI.Response.MESSAGE));
                }
                break;

            default:
                break;
        }
    }

    private void resetViews() {
        mResultCode.setText("");
        mResultMessage.setText("");
        mTxCode.setText("");
        mReceiptSent.setText("");
        mTxInfo.setText("");
    }

    private void findViews() {
        mResultCode = (TextView) findViewById(R.id.result);
        mResultMessage = (TextView) findViewById(R.id.result_msg);
        mTxCode = (TextView) findViewById(R.id.tx_code);
        mReceiptSent = (TextView) findViewById(R.id.receipt_sent);
        mTxInfo = (TextView) findViewById(R.id.tx_info);
    }
}
