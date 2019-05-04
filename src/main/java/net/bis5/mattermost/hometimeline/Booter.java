package net.bis5.mattermost.hometimeline;

import java.io.IOException;

import javax.inject.Inject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.bis5.mattermost.hometimeline.store.PreferenceStore;

public class Booter {
    @Inject
    FXMLLoader loader;
    @Inject
    PreferenceStore preferences;

    public void start(Stage primaryStage) throws IOException {
        Parent root;
        if (preferences.isEmpty()) {
            // TODO ユーザープロファイルがなければ新規作成してSettingWindowを開く
            root = loader.load(getClass().getResourceAsStream("SettingWindow.fxml"));
        } else {
            // TODO ユーザープロファイルがあればロードしてMainWindowを開く
            root = loader.load(getClass().getResourceAsStream("MainWindow.fxml"));
            var container = root.getChildrenUnmodifiable().get(0);
            AnchorPane.setBottomAnchor(container, 0d);
            AnchorPane.setTopAnchor(container, 0d);
            AnchorPane.setLeftAnchor(container, 0d);
            AnchorPane.setRightAnchor(container, 0d);
        }
        primaryStage.setTitle("mattermost-home-timeline");
        Scene scene = new Scene(root);
        primaryStage.setWidth(640);
        primaryStage.setHeight(480);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}