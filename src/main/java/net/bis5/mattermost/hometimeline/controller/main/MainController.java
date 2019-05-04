package net.bis5.mattermost.hometimeline.controller.main;

import java.net.URL;
import java.util.ResourceBundle;

import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import net.bis5.mattermost.hometimeline.component.PostItem;
import net.bis5.mattermost.hometimeline.service.WebSocketClientService;
import net.bis5.mattermost.hometimeline.store.PreferenceStore;

public class MainController implements Initializable {

    @Inject
    WebSocketClientService wsClientService;
    @Inject
    PreferenceStore preferences;
    @Inject
    MainViewModel viewModel;

    @FXML
    ListView<PostItem> postList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        postList.itemsProperty().bind(viewModel.itemsProperty);
    }
}