/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package livestreamerjfxgui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.concurrent.*;

/**
 *
 * @author huoni
 */
public class Livestreamer extends Service<String> {
    private final String path = "livestreamer.exe";
    
    private final String url;
    private final String stream;
    private final String playerPath;
    
    public Livestreamer(String url, String stream) {
        this(url, stream, "VLC");
    }
      
    public Livestreamer(String url, String stream, String playerPath) {
        this.url = url;
        this.stream = stream;
        this.playerPath = playerPath;
    }
    
    @Override
    protected Task<String> createTask() {        
        return new Task<String>() {
            @Override
            protected String call() {
                ProcessBuilder pb;
                if (playerPath.equals("VLC")) {
                    pb = new ProcessBuilder(path, "--no-version-check", url, stream);
                } else {
                    pb = new ProcessBuilder(path, "--no-version-check", "-p "+playerPath, url, stream);
                }
                    
                String result = null;
                try {
                    Process p = pb.start();
                    p.waitFor();  // wait for process to finish then continue.

                    BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    while ((line = bri.readLine()) != null) {
                        result += line;
                        //System.out.println(line);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

                return result;
            }
        };
    }
}