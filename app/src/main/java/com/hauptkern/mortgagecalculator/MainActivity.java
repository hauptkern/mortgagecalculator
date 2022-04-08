package com.hauptkern.mortgagecalculator;

import androidx.appcompat.app.AppCompatActivity;
//aGF1cHRrZXJuCg==
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Mortgage Calculator");
        this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#99cc00")));
        SeekBar loanSeekBar=findViewById(R.id.seekBar);
        FloatingActionButton doneButton=findViewById(R.id.floatingActionButton3);
        doneButton.setOnClickListener(btnClickListener);
        loanSeekBar.setOnSeekBarChangeListener(seekListener);
    }
    private final SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            TextView loandurationLabel=findViewById(R.id.loandurationvalue);
            if (i==0){
                seekBar.setProgress(1);
                loandurationLabel.setText("1 Year");
            }
            else{
                loandurationLabel.setText(i+" Years");
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    private final View.OnClickListener btnClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WebView browser=findViewById(R.id.webview);
            TextView purchaseEntry=findViewById(R.id.purchaseEntry);
            TextView downPaymentEntry=findViewById(R.id.downPaymentEntry);
            TextView interestRateEntry=findViewById(R.id.interestRateEntry);
            SeekBar loanDurationSBar=findViewById(R.id.seekBar);

            String purchaseEntryStr=purchaseEntry.getText().toString();
            String downPaymentEntryStr=downPaymentEntry.getText().toString();
            String interestRateEntryStr=interestRateEntry.getText().toString();
            String loadDuratingSBarStr=String.valueOf(loanDurationSBar.getProgress());

            if (purchaseEntry.length()<1 || downPaymentEntry.length()<1 || interestRateEntry.length()<1 || loadDuratingSBarStr.length()<1){
                Toast.makeText(MainActivity.this, "Please fill all of the fields.", Toast.LENGTH_SHORT).show();
                return;
            }
            int purchaseEntryVal=Integer.parseInt(purchaseEntryStr);
            int downPaymentEntryVal=Integer.parseInt(downPaymentEntryStr);
            double interestRateEntryVal=Double.parseDouble(interestRateEntryStr);
            int loanDurationSBarVal=loanDurationSBar.getProgress();
            String htmlOutput=tableBuilder(purchaseEntryVal,downPaymentEntryVal,interestRateEntryVal,loanDurationSBarVal);
            browser.loadDataWithBaseURL("file:///android_asset/",htmlOutput, "text/html", "base64",null);
        }
    };
    public String tableBuilder(int purchasePrice,int downPayment,double interestRate,int loanDuration){
        StringBuilder output = new StringBuilder();
        int remaining=purchasePrice-downPayment;
        double monthlyInterest=(interestRate/100)/12;
        int totalMonthlyLoanDuration=loanDuration*12;
        double monthlyPaymentAmount = remaining*(((monthlyInterest*Math.pow(1+monthlyInterest,totalMonthlyLoanDuration))/((Math.pow(1+monthlyInterest,totalMonthlyLoanDuration))-1)));
        double priceWithInterest=monthlyPaymentAmount*totalMonthlyLoanDuration;
        output.append("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no\"><link rel=\"stylesheet\" href=\"bootstrap.css\"></head>");
        output.append("<body><center><div class=\"alert alert-success\">"+"Loan:"+String.valueOf(remaining)+"$<br>"+"Monthly Payment:"+String.valueOf(Math.floor(monthlyPaymentAmount*100)/100)+"$<br>"+"Interest Paid:"+String.valueOf(Math.floor((priceWithInterest-remaining)*100)/100)+"$</div><table class=\"table table-striped\"><tr><th>Month</th><th>Payment</th><th>Remaining</th></tr>");
        for(int i=1;i<totalMonthlyLoanDuration+1;i++){
            priceWithInterest-=monthlyPaymentAmount;
            output.append("<tr>");
            output.append("<th>"+String.valueOf(i)+"</th>");
            output.append("<th>"+String.valueOf(Math.floor(monthlyPaymentAmount*100)/100)+"$</th>");
            output.append("<th>"+String.valueOf(Math.floor(priceWithInterest*100)/100)+"$</th>");
            output.append("</tr>");
        }
        output.append("</table></center></body></html>");
        return output.toString();
    }
}