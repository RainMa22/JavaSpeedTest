package com.example.myapplication;

import fr.bmartel.speedtest.SpeedTestSocket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;

public class downloadThread extends Thread {
    protected boolean running = true;
    private SpeedTestSocket speedTestSocket;
    private MainActivity activity;
    private String location;
    private String file;
    protected downloadThread(SpeedTestSocket speedTestSocket, MainActivity activity, String[] url) {
        location=url[0];
        file=url[1];
        System.out.println(location+","+file);
        this.speedTestSocket = speedTestSocket;
        this.activity = activity;
    }

    @Override
    public void run() {
        speedTestSocket.startFixedDownload("http://"+location+file, 10000, 10000);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BigDecimal downloadSpeed = speedTestSocket.getLiveReport().getTransferRateBit().divide(BigDecimal.valueOf(8 * 1024 * 1024));
        String result = "Download Speed: " + Double.valueOf(String.valueOf(downloadSpeed)) + "MiB/s";
        activity.update(result, false);
        activity.update("Running Upload Test...", true);
        uploadThread upload = new uploadThread(activity,location);
        upload.start();
        while (upload.running) {
            try {
                Thread.sleep(10010);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        activity.update(result + "\n" + upload.finalMessage, false);
        pingThread ping=new pingThread(activity,location);
                ping.start();
        while (ping.running) {
            try {
                Thread.sleep(10010);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        activity.update(result + "\n" + upload.finalMessage+"\n"+ping.finalMessage, false);
        running = false;
    }
}

class uploadThread extends Thread {
    private String location;
    protected boolean running = true;
    protected String finalMessage;
    private SpeedTestSocket speedTestSocket;
    private MainActivity activity;

    protected uploadThread(MainActivity activity,String location) {
        this.speedTestSocket = activity.uploadSocket;
        this.activity = activity;
        this.location=location;
    }

    @Override
    public void run() {
        speedTestSocket.startFixedUpload("http://"+location, 100000000, 10000, 10000);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BigDecimal downloadSpeed = speedTestSocket.getLiveReport().getTransferRateBit().divide(BigDecimal.valueOf(8 * 1024 * 1024));
        finalMessage = "Upload Speed: " + Double.valueOf(String.valueOf(downloadSpeed)) + "MiB/s";
        running = false;
    }
}

class pingThread extends Thread {
    protected boolean running = true;
    protected String finalMessage;
    private MainActivity activity;
    private String location;
    protected pingThread(MainActivity activity,String location) {
        this.activity = activity;
        this.location=location;
    }

    @Override
    public void run() {
        Runtime runtime = Runtime.getRuntime();
        ArrayList<String> inputs = new ArrayList<>(0);
        Process pinger = null;
        BufferedReader br = null;
        try {
            pinger = runtime.exec("ping -c 4 "+location);
            br = new BufferedReader(new InputStreamReader(pinger.getInputStream()));
            String s;
            pinger.waitFor();
            while ((s=br.readLine())!=null) {
                inputs.add(s);
            }
            pinger.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<Integer> temp=new ArrayList<>(0);
        for (String s : inputs) {
            for (String ss : s.split(" ")) {
                    if (ss.contains("time=")) {
                        ss=ss.replace("time=","");
                        temp.add(Integer.parseInt(ss));
                        break;
                }
            }
        }
        double d=0;
        for (int i = 0; i < temp.size(); i++) {
            d+=temp.get(i);
        }
        d/=temp.size();
        finalMessage="ping: "+d+"ms";
        if (temp.size()==0){
            finalMessage="pinging failed! please try again:(";
        }
        running = false;
    }
}