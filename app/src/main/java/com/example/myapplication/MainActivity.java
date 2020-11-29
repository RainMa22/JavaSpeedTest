package com.example.myapplication;

import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

import java.math.BigDecimal;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public ArrayList<String> logs=new ArrayList<>(0);
    protected SpeedTestSocket init(){
        SpeedTestSocket speedTestSocket = new SpeedTestSocket();
// add a listener to wait for speedtest completion and progress
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

            @Override
            public void onCompletion(SpeedTestReport report) {
                // called when download/upload is complete
                System.out.println("[COMPLETED] rate in octet/s : " + report.getTransferRateOctet()+'\n');
                System.out.println("[COMPLETED] rate in bit/s   : " + report.getTransferRateBit()+'\n');
            }

            @Override
            public void onError(SpeedTestError speedTestError, String errorMessage) {
                System.out.println(errorMessage);
            }

            @Override
            public void onProgress(float percent, SpeedTestReport report) {
                // called to notify download/upload progress
                System.out.println("running");
                System.out.println("[PROGRESS] progress : " + percent + "%\n");
                System.out.println("[PROGRESS] rate in octet/s : " + report.getTransferRateOctet()+'\n');
                System.out.println("[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());

            }
        });
        return speedTestSocket;
    }
    protected SpeedTestSocket downloadSocket,uploadSocket;
    private TextView status;
    private downloadThread download;
    public void speedTest(View view) throws InterruptedException {
        download=new downloadThread(downloadSocket,this);
        download.start();
        }
        protected void update(String s,boolean concat){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    if (concat) {
                        status.setText(status.getText()+"\n"+s);
                    }else {
                        status.setText(s);
                    }
                }
            }
        });
        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status= findViewById(R.id.Status);
        downloadSocket=init();
        uploadSocket=init();
    }
        }