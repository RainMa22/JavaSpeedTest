package com.example.myapplication;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
    private Spinner spinner;
    String[] servers,locations,files;
    public void speedTest(View view) throws InterruptedException {
        int io=0;
        for (int i = 0; i < locations.length; i++) {
            if (locations[i]==spinner.getSelectedItem())io=i;
        }
        download=new downloadThread(downloadSocket,this,new String[]{servers[io],files[io]});
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
        locations= new String[]{
                "Fastest Avaliable",
                "Beauharnois, Canada",
                "Vitry-sur-Seine, France",
                "Singapore",
                "Sydney, Australia",
                "Tokyo, Japan",
                "Toronto, Canada",
                "London, Britain",
                "Dallas, America",
                "Frankfurt, Germany",
                "Mumbai, India"
        };
        servers= new String[]{
                "ovh.net",
                "bhs.proof.ovh.net",
                "ipv4.scaleway.testdebit.info",
                "speedtest-sgp.apac-tools.ovh",
                "speedtest-syd.apac-tools.ovh",
                "speedtest.tokyo2.linode.com",
                "speedtest.toronto1.linode.com",
                "speedtest.london.linode.com",
                "speedtest.dallas.linode.com",
                "speedtest.frankfurt.linode.com",
                "speedtest.mumbai1.linode.com"
        };
        files= new String[]{
                "/files/10Gb.dat",
                "/files/10Gb.dat",
                "/10G.iso",
                "/files/10Gb.dat",
                "/files/10Gb.dat",
                "/1GB-tokyo.bin",
                "/1GB-toronto.bin",
                "/1GB-london.bin",
                "/1GB-dallas.bin",
                "/1GB-frankfurt.bin",
                "/1GB-mumbai.bin"
        };
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner=findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,R.layout.support_simple_spinner_dropdown_item,locations);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        status= findViewById(R.id.Status);
        downloadSocket=init();
        uploadSocket=init();
    }
        }