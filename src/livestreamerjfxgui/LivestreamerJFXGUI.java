/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package livestreamerjfxgui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author huoni
 */
public class LivestreamerJFXGUI extends Application {
    public static Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("LivestreamerJFXGUIApp.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("LivestreamerJFXGUI");
        primaryStage.getIcons().add(new Image("/images/icon.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
