package net.bis5.mattermost.hometimeline.component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.bis5.mattermost.hometimeline.util.Posts;
import net.bis5.mattermost.model.Post;

public class PostItem extends HBox {

    public static PostItem create(Post post, String siteUrl, String senderName, String channelDisplayName,
            Path profilePicture) {
        PostItem item = new PostItem();
        try {
            var view = new ImageView(new Image(Files.newInputStream(profilePicture)));
            view.setFitWidth(64);
            view.setFitHeight(64);
            view.setStyle("-fx-border-radius: 32px; -fx-background-radius: 32px;");
            item.getChildren().add(view);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        VBox postBox = new VBox();
        HBox postMetadata = new HBox();
        postMetadata.setSpacing(5);
        postMetadata.setAlignment(Pos.CENTER_LEFT);
        postBox.getChildren().add(postMetadata);
        var senderInfo = new HBox();
        postMetadata.getChildren().add(senderInfo);
        senderInfo.setStyle("-fx-font-size: 16");
        senderInfo.getChildren().add(new Label(senderName));
        if (Posts.utils().isBotPost(post)) {
            senderInfo.getChildren().add(new Label("[BOT]"));
        }
        senderInfo.getChildren().add(new Label("@" + channelDisplayName));
        postMetadata.getChildren().add(new Label(
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(post.getCreateAt()), ZoneId.systemDefault()).toString()));
        postBox.getChildren().add(new Label(post.getMessage()));
        item.getChildren().add(postBox);
        return item;
    }

    public static PostItem create(String message) {
        return new WsDebugItem(message);
    }

    public static class WsDebugItem extends PostItem {
        public WsDebugItem(String message) {
            getChildren().add(new Label(message));
        }
    }
}