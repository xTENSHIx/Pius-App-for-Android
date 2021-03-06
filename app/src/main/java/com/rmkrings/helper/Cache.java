package com.rmkrings.helper;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.logging.Logger;

import com.rmkrings.pius_app_for_android;

public class Cache {
    private final static Logger logger = Logger.getLogger(Cache.class.getName());

    public boolean fileExists(String filename) {
        File file = pius_app_for_android.getAppContext().getFileStreamPath(filename);
        return file.exists();
    }

    // Store data under given filename in cache directory.
    public void store(String filename, String data) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(pius_app_for_android.getAppContext().openFileOutput(filename, Context.MODE_PRIVATE));
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write(data);
            bw.close();
        }
        catch (java.io.FileNotFoundException e) {
            logger.severe(String.format("Failed to open file %s: File not found.", filename));
        }
        catch (java.io.IOException e) {
            logger.severe(String.format("Failed to write to file %s: %s", filename, e.toString()));
        }
    }

    // Read data from filename in cache directory. If data cannot
    // be read returns nil. Supposes that file content is a string.
    public String read(String filename) {
        String d;
        StringBuilder data = new StringBuilder();

        try {
            InputStreamReader reader = new InputStreamReader(pius_app_for_android.getAppContext().openFileInput(filename));
            BufferedReader br = new BufferedReader(reader);

            while ((d = br.readLine()) != null) {
                data.append(d);
            }

            br.close();
        }
        catch (java.io.FileNotFoundException e) {
            logger.severe(String.format("Failed to open file %s: File not found.", filename));
        }
        catch (java.io.IOException e) {
            logger.severe(String.format("Failed to read from file %s: %s", filename, e.toString()));
        }

        return data.toString();
    }
}
