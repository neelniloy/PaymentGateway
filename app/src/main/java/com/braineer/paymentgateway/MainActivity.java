package com.braineer.paymentgateway;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.braineer.paymentgateway.databinding.ActivityMainBinding;
import com.sslcommerz.library.payment.model.datafield.MandatoryFieldModel;
import com.sslcommerz.library.payment.model.dataset.TransactionInfo;
import com.sslcommerz.library.payment.model.util.CurrencyType;
import com.sslcommerz.library.payment.model.util.ErrorKeys;
import com.sslcommerz.library.payment.model.util.SdkCategory;
import com.sslcommerz.library.payment.model.util.SdkType;
import com.sslcommerz.library.payment.viewmodel.listener.OnPaymentResultListener;
import com.sslcommerz.library.payment.viewmodel.management.PayUsingSSLCommerz;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mainBinding.getRoot();
        setContentView(view);

        mainBinding.message.setVisibility(View.GONE);
        mainBinding.button.setVisibility(View.GONE);


        mainBinding.choosePaymentMethod.setOnClickListener(view1 -> {

            String amount = mainBinding.etAmount.getText().toString().trim();

            if (amount.isEmpty()){

                mainBinding.editTextAmountLayout.setError("*Amount required");
                mainBinding.etAmount.requestFocus();

            }else if (Double.parseDouble(amount)<10){
                mainBinding.editTextAmountLayout.setError("*Amount must be 10 or above 10");
                mainBinding.etAmount.requestFocus();
            }
            else {

                mainBinding.editTextAmountLayout.setErrorEnabled(false);

                MandatoryFieldModel mandatoryFieldModel = new MandatoryFieldModel("brain624ee9a1a3da8", "brain624ee9a1a3da8@ssl", amount, "Your transaction id", CurrencyType.BDT, SdkType.TESTBOX, SdkCategory.BANK_LIST);

                PayUsingSSLCommerz.getInstance().setData(MainActivity.this, mandatoryFieldModel, new OnPaymentResultListener() {
                    @Override
                    public void transactionSuccess(TransactionInfo transactionInfo) {

                        mainBinding.message.setVisibility(View.VISIBLE);
                        mainBinding.button.setVisibility(View.VISIBLE);

                        mainBinding.editTextAmountLayout.setVisibility(View.GONE);
                        mainBinding.choosePaymentMethod.setVisibility(View.GONE);

                        // If payment is success and risk label is 0 get payment details from here
                        if (transactionInfo.getRiskLevel().equals("0")) {

                            mainBinding.message.setText("Payment Successful\n\nYour Transaction id is "+transactionInfo.getValId());

                        }
                        // Payment is success but payment is not complete yet. Card on hold now.
                        else {

                            mainBinding.message.setText("Payment Request Failed\n"+transactionInfo.getRiskTitle());
                        }
                    }

                    @Override
                    public void transactionFail(String s) {
                        Log.e(TAG, s);
                    }


                    @Override
                    public void error(int errorCode) {
                        switch (errorCode) {
                            // Your provides information is not valid.
                            case ErrorKeys.USER_INPUT_ERROR:
                                Log.e(TAG, "User Input Error");
                                break;
                            // Internet is not connected.
                            case ErrorKeys.INTERNET_CONNECTION_ERROR:
                                Log.e(TAG, "Internet Connection Error");
                                break;
                            // Server is not giving valid data.
                            case ErrorKeys.DATA_PARSING_ERROR:
                                Log.e(TAG, "Data Parsing Error");
                                break;
                            // User press back button or canceled the transaction.
                            case ErrorKeys.CANCEL_TRANSACTION_ERROR:
                                Log.e(TAG, "User Cancel The Transaction");
                                break;
                            // Server is not responding.
                            case ErrorKeys.SERVER_ERROR:
                                Log.e(TAG, "Server Error");
                                break;
                            // For some reason network is not responding
                            case ErrorKeys.NETWORK_ERROR:
                                Log.e(TAG, "Network Error");
                                break;
                        }
                    }
                });
            }

        });


        mainBinding.button.setOnClickListener(view1 -> {

            Intent intent = getIntent();
            finish();
            startActivity(intent);

        });
    }
}