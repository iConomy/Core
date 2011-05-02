package com.iConomy.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URLConnection;
import java.net.URL;

public class Downloader {

    protected static int count, total, itemCount, itemTotal;
    protected static long lastModified;
    protected static String error;
    protected static boolean cancelled;

    public Downloader() { }

    public synchronized void cancel() {
        cancelled = true;
    }

    public static void install(String location, String filename) {
        try {
            cancelled = false;
            count = total = itemCount = itemTotal = 0;
            System.out.println("[iConomy] Downloading Dependencies");
            if (cancelled) {
                return;
            }
            System.out.println("   + " + filename + " downloading...");
            download(location, filename);
            System.out.println("   - " + filename + " finished.");
            System.out.println("[iConomy] Downloading " + filename + "...");
        } catch (IOException ex) {
            System.out.println("[iConomy] Error Downloading File: " + ex);
        }
    }

    protected static synchronized void download(String location, String filename)throws IOException {
        URLConnection connection = new URL(location).openConnection();
        connection.setUseCaches(false);
        lastModified = connection.getLastModified();
        int filesize = connection.getContentLength();
        String destination = "lib" + File.separator + filename;
        File parentDirectory = new File(destination).getParentFile();

        if (parentDirectory != null) {
            parentDirectory.mkdirs();
        }

        InputStream in = connection.getInputStream();
        OutputStream out = new FileOutputStream(destination);

        byte[] buffer = new byte[65536];
        int currentCount = 0;
        for (;;) {
            if (cancelled) {
                break;
            }

            int count = in.read(buffer);

            if (count < 0) {
                break;
            }

            out.write(buffer, 0, count);
            currentCount += count;
        }

        in.close();
        out.close();
    }

    public long getLastModified() {
        return lastModified;
    }
}
