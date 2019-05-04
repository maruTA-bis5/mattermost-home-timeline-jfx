package net.bis5.mattermost.hometimeline.controller.main;

import javax.inject.Singleton;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.bis5.mattermost.hometimeline.component.PostItem;

@Singleton
public class MainViewModel {

    Property<ObservableList<PostItem>> itemsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

    public void add(PostItem item) {
        itemsProperty.getValue().add(item);
    }

    public void addDebug(String message) {
        itemsProperty.getValue().add(PostItem.create(message));
    }

}