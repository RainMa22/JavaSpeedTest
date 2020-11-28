package com.example.myapplication;

import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class MainActivity extends AppCompatActivity {
    final TextView status=findViewById(R.id.Status);
    protected SpeedTestSocket init(){
        SpeedTestSocket speedTestSocket = new SpeedTestSocket();
// add a listener to wait for speedtest completion and progress
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

            @Override
            public void onCompletion(SpeedTestReport report) {
                // called when download/upload is complete
                status.setText("[COMPLETED] rate in octet/s : " + report.getTransferRateOctet()+'\n');
                status.setText(status.getText()+"[COMPLETED] rate in bit/s   : " + report.getTransferRateBit()+'\n');
            }

            @Override
            public void onError(SpeedTestError speedTestError, String errorMessage) {
                // called when a download/upload error occur
            }

            @Override
            public void onProgress(float percent, SpeedTestReport report) {
                // called to notify download/upload progress
                status.setText("[PROGRESS] progress : " + percent + "%\n");
                status.setText(status.getText()+"[PROGRESS] rate in octet/s : " + report.getTransferRateOctet()+'\n');
                status.setText(status.getText()+"[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());
            }
        });
        return speedTestSocket;
    }
    SpeedTestSocket speedTestSocket;
    public void speedTest(View view){
        speedTestSocket.startFixedDownload("http://ipv4.scaleway.testdebit.info/10G.iso", 10000);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speedTestSocket=init();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}