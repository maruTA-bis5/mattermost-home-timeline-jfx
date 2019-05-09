package net.bis5.mattermost.hometimeline.controller.main;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.inject.Singleton;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.bis5.mattermost.hometimeline.component.PostItem;
import net.bis5.mattermost.model.Post;

@Singleton
public class MainViewModel {

    Property<ObservableList<PostItem>> itemsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private Set<String> postIds = new HashSet<>();

    public void add(PostItem item) {
        itemsProperty.getValue().add(item);
        postIds.add(item.postIdProperty().get());
    }

    public void addDebug(String message) {
        itemsProperty.getValue().add(PostItem.create(message));
    }

	public Optional<PostItem> find(String targetId) {
        if (!postIds.contains(targetId)) {
            return Optional.empty();
        }
        return itemsProperty.getValue().stream().filter(i -> i.postIdProperty().isEqualTo(targetId).get()).findFirst();
	}

	public void update(Post updatedPost) {
        String postId = updatedPost.getId();
        find(postId).ifPresent(item -> updateItem(item, updatedPost));
    }
    
    private void updateItem(PostItem item, Post updatedPost) {
        item.update(updatedPost);
    }

	public void delete(Post deletedPost) {
        String postId = deletedPost.getId();
        find(postId).ifPresent(item -> removeItem(item));
	}

    private void removeItem(PostItem item) {
        itemsProperty.getValue().remove(item);
    }

}