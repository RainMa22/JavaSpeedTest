package com.example.myapplication;

import android.app.Activity;
import fr.bmartel.speedtest.SpeedTestSocket;

import java.math.BigDecimal;

public class downloadThread extends Thread{
    private SpeedTestSocket speedTestSocket;
    protected boolean running=true;
    private MainActivity activity;
    protected downloadThread(SpeedTestSocket speedTestSocket, MainActivity activity){
        this.speedTestSocket=speedTestSocket;
        this.activity=activity;
    }
    @Override
    public void run() {
        speedTestSocket.startFixedDownload("http://ipv4.scaleway.testdebit.info/10G.iso", 10000,10000);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BigDecimal downloadSpeed=speedTestSocket.getLiveReport().getTransferRateBit().divide(BigDecimal.valueOf(8*1024*1024));
        String result="Download Speed: "+Double.valueOf(String.valueOf(downloadSpeed))+"MiB/s";
        activity.update(result,false);
        activity.update("Running Upload Test...",true);
        uploadThread upload=new uploadThread(activity);
        upload.start();
        while (upload.running){
            try {
                Thread.sleep(10010);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        activity.update(result+"\n"+upload.finalMessage,false);
        running=false;
    }
}
class uploadThread extends Thread{
    private SpeedTestSocket speedTestSocket;
    protected boolean running=true;
    protected String finalMessage;
    private MainActivity activity;
    protected uploadThread(MainActivity activity){
        this.speedTestSocket=activity.uploadSocket;
        this.activity=activity;
    }
    @Override
    public void run() {
        speedTestSocket.startFixedUpload("http://scaleway.testdebit.info/", 100000000,10000,10000);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BigDecimal downloadSpeed=speedTestSocket.getLiveReport().getTransferRateBit().divide(BigDecimal.valueOf(8*1024*1024));
        finalMessage="Upload Speed: "+Double.valueOf(String.valueOf(downloadSpeed))+"MiB/s";
        running=false;
    }
}