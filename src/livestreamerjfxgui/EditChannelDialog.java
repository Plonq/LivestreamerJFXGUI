/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package livestreamerjfxgui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
/**
 *
 * @author huoni
 */

public class EditChannelDialog extends Stage implements Initializable {
    
    @FXML
    private Button okButton;
    @FXML
    private TextField aliasTextField;
    @FXML
    private TextField urlTextField;
    
    public boolean okClicked = false;
    private final String editAlias;
    private final String editUrl;

    public EditChannelDialog(Parent parent, String editAlias, String editUrl) {
        if (editAlias.isEmpty() && editUrl.isEmpty()) {
            setTitle("Add Channel");
        } else {
            setTitle("Edit Channel");
        }
        
        this.editAlias = editAlias;
        this.editUrl = editUrl;
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EditChannelDialog.fxml"));
        fxmlLoader.setController(this);

        // Nice to have this in a load() method instead of constructor, but this seems to be the convention.
        try {
            setScene(new Scene((Parent) fxmlLoader.load()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void okButton_OnAction(ActionEvent event) {
        okClicked = true;
        close();
    }

    @FXML
    void cancelButton_OnAction(ActionEvent event) {
        close();
    }
    
    @FXML
    void aliasTextField_OnKeyReleased() {
        // Enable OK button only if both fields aren't empty
        okButton.setDisable(!isFormFilled());
    }
    
    @FXML
    void urlTextField_OnKeyReleased() {
        // Enable OK button only if both fields aren't empty
        okButton.setDisable(!isFormFilled());
    }
    
    public String getAlias()
    {
        return aliasTextField.getText();
    }
    
    public String getURL()
    {
        return urlTextField.getText();
    }
    
    /***
     * Helper methods
     */
    private boolean isFormFilled() {
            return (!aliasTextField.getText().isEmpty() &&
                    !urlTextField.getText().isEmpty());
    }
    
    /*
     * Called when FXML file is loaded (via FXMLLoader.load()).
     * It will execute before the form is shown.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        aliasTextField.setText(editAlias);
        urlTextField.setText(editUrl);
    }
}

