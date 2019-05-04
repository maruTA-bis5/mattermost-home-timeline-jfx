package net.bis5.mattermost.hometimeline.controller;

import java.util.Arrays;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import net.bis5.mattermost.hometimeline.store.PreferenceStore;
import net.bis5.mattermost.hometimeline.store.PreferenceStore.PreferenceKey;

@Dependent
public class SettingController {

    @FXML
    private TextField urlInput;
    @FXML
    private RadioButton basicLoginMethod;
    @FXML
    private TextField basicLoginUsername;
    @FXML
    private PasswordField basicLoginPassword;
    @FXML
    private Spinner<Integer> maxPosts;
    @FXML
    private RadioButton enableFormatting;
    @FXML
    private RadioButton disableFormatting;

    @Inject
    private PreferenceStore preferences;

    public void initialize() {
        setupToggleGroup(basicLoginMethod);
        setupToggleGroup(enableFormatting, disableFormatting);
        if (preferences.isEmpty()) {
            setDefaultValues();
        } else {
            applyPreferences();
        }
    }

    private void applyPreferences() {
        urlInput.setText(preferences.get(PreferenceKey.URL));
        // TODO basicLoginMethod
        // TODO basicLoginUserName
        // TODO basicLoginPassword
        maxPosts.getEditor().setText(String.valueOf(preferences.get(PreferenceKey.MAX_POSTS)));
        // TODO enableFormatting, disableFormatting
    }

    private void setDefaultValues() {
        basicLoginMethod.setSelected(true);
        maxPosts.getEditor().setText(String.valueOf(200));
        enableFormatting.setSelected(true);
    }

    private void setupToggleGroup(RadioButton button, RadioButton... buttons) {
        ToggleGroup group = new ToggleGroup();
        button.setToggleGroup(group);
        Arrays.asList(buttons).forEach(b -> b.setToggleGroup(group));
    }

    public void onSave() {
        // TODO validate
        // TODO store
        // TODO close and open MainWindow
    }
}