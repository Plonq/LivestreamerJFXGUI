/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package livestreamerjfxgui;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javax.swing.JOptionPane;
import libraries.WinRegistry;
/**
 *
 * @author huoni
 */
public class LivestreamerJFXGUIApp implements Initializable {
    
    @FXML
    private ChoiceBox<Channel> channelChoiceBox;
    @FXML
    private ChoiceBox streamChoiceBox;
    @FXML
    private Label labelMessage;
    @FXML
    private ChoiceBox playerChoiceBox;
    
    private Preferences prefs = Preferences.userNodeForPackage(this.getClass());
    
    /***
     * FXML event methods
     */
    
    @FXML
    private void addButton_OnAction(ActionEvent event) {
        Channel newChan = new Channel();
        
        if (editChannel(newChan)) {
            addChannel(newChan);
            savePreferences();
        }
    }
    
    @FXML
    private void editButton_OnAction(ActionEvent event) {
        Channel selected = channelChoiceBox.getSelectionModel().getSelectedItem();
        Channel newChan = new Channel(selected.getAlias(), selected.getUrl());
        if (editChannel(newChan)) {
            replaceChannel(newChan, selected);
            savePreferences();
        }
    }
    
    @FXML
    private void removeButton_OnAction(ActionEvent event) {
        // Removes the selected channel and selects the next item
        channelChoiceBox.getItems().remove(channelChoiceBox.getValue());
        channelChoiceBox.setValue(channelChoiceBox.getItems().get(0));
        savePreferences();
    }
    
    @FXML
    private void launchButton_OnAction(ActionEvent event) {
        String url = channelChoiceBox.getValue().getUrl();
        String stream = streamChoiceBox.getValue().toString();
        
        launchStream(url, stream);
    }
    
    // End FXML event methods
    
     /***
     * Helper methods
     */
    
    private void addChannel(Channel chan) {
        channelChoiceBox.getItems().add(chan);
        channelChoiceBox.setValue(chan);
    }
    
    private void replaceChannel(Channel newChan, Channel oldChan) {
        int i = channelChoiceBox.getItems().indexOf(oldChan);
        channelChoiceBox.getItems().remove(oldChan);
        channelChoiceBox.getItems().add(i, newChan);
        channelChoiceBox.setValue(newChan);
    }
    
    private boolean editChannel(Channel chan) {
        EditChannelDialog dialog = new EditChannelDialog(null, chan.getAlias(), chan.getUrl());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
        
        if (dialog.okClicked) {
            chan.setAlias(dialog.getAlias());
            chan.setUrl(dialog.getURL());
            return true;
        }
        return false;
    }
    
    private void savePreferences() {
        // I think it's easier to put all this in one method, and save
        // everything whenever something changes. I believe this isn't a problem
        // with only a few preferences.
        prefs.put("STREAM", streamChoiceBox.getValue().toString());
        prefs.put("PLAYER", playerChoiceBox.getValue().toString());
        
        // Channels
        try {
            prefs.node("Channels").clear();
            for (Channel c : channelChoiceBox.getItems()) {
                prefs.node("Channels").put(c.getAlias(), c.getUrl());
            }
        } catch (BackingStoreException e) {
            // Caused by clear() if there are no keys. Can ignore.
        }
        
    }
    
    private void loadPreferences() {
        ObservableList<Channel> channels = FXCollections.observableArrayList();
        try {
            String[] channelNames = prefs.node("Channels").keys();
            if (channelNames.length != 0) {
                for (String alias : channelNames) {
                    String value = prefs.node("Channels").get(alias, "Key not found");
                    channels.add(new Channel(alias, value));
                }
                channelChoiceBox.setItems(channels);
                channelChoiceBox.setValue(channelChoiceBox.getItems().get(0));
            }
        } catch (BackingStoreException e) {
            // No saved channels, ignore
        }
        
        streamChoiceBox.setValue(
            prefs.get("STREAM", streamChoiceBox.getItems().get(0).toString()) // Second value is the default, i.e., if no saved prefs, select first item
        );
        
        playerChoiceBox.setValue(
            prefs.get("PLAYER", "VLC (default)")
        );
    }
    
    private void launchStream(String url, String stream) {
        setMessage("Launching " + url, 0);
        Livestreamer ls = new Livestreamer(url, stream, getPlayerPath());
        ls.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                // Deal with result of Livestreamer here
                String result = ls.getValue();
                if (result.contains("error: No streams found")) {
                    setMessage(url + " is not live", 1);
                } else if (result.contains("error: The specified stream(s)")) {
                    setMessage("Selected stream (" + stream + ") could not be found", 1);
                } else if (result.contains("Stream ended")) {
                    setMessage(url + " stream ended", 0);
                }
            }
        });

        ls.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                System.err.println("Error launching Livestreamer");
            }
        });
        
        ls.restart();
    }
    
    private void setMessage(String msg, int type) {
        String hex;
        if (type == 1) {
            hex = "#FF0000";
        } else {
            hex = "#000000";
        }
        labelMessage.setTextFill(Color.web(hex));
        labelMessage.setText(msg);
    }
    
    private String getPlayerPath() {
        String player = playerChoiceBox.getValue().toString();
        if (player.equals("VLC (default)")) {
            return "VLC";
        } else if (player.equals("MPC-HC")) {
            String value = null;
            try {
                value = WinRegistry.readString(
                        WinRegistry.HKEY_CURRENT_USER,
                        "Software\\MPC-HC\\MPC-HC",
                        "ExePath");
                if (value == null) {
                    setMessage("Could not find path to MPC-HC. Is it installed?", 1);
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
                Logger.getLogger(LivestreamerJFXGUIApp.class.getName()).log(Level.SEVERE, "Error reading registry", ex);
            }
            return value;
        }
        return "VLC";
    }
    
    // End Helper methods
    
    /*
     * Called when FXML file is loaded (via FXMLLoader.load()).
     * It will execute before the form is shown.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        LivestreamerJFXGUI.primaryStage.setResizable(false);
        
        // Set pre-determined items
        ObservableList streams = FXCollections.observableArrayList();
        streams.addAll("low", "medium", "high", "source",
                new Separator(), "480p", "720p", "1080p",
                new Separator(), "worst", "best");
        streamChoiceBox.setItems(streams);
        
        ObservableList players = FXCollections.observableArrayList();
        players.addAll("VLC (default)", "MPC-HC");
        playerChoiceBox.setItems(players);
        playerChoiceBox.setValue("VLC (default)");
        
        // Load saved preferences
        loadPreferences();
        
        // Listen for changes to the comboboxes, saving all prefs when they are changed
        streamChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue<? extends String> selected, String oldSelection, String newSelection) {
                savePreferences();
            }
        });
        playerChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue<? extends String> selected, String oldSelection, String newSelection) {
                savePreferences();
            }
        });
    }
}
