package net.bis5.mattermost.hometimeline.component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringPropertyBase;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
        item.postIdPropertyImpl().set(post.getId());
        item.profilePicturePropertyImpl().set(profilePicture);
        item.senderNamePropertyImpl().set(senderName);
        item.botPostPropertyImpl().set(Posts.utils().isBotPost(post));
        item.channelDisplayNamePropertyImpl().set(channelDisplayName);
        item.messagePropertyImpl().set(post.getMessage());
        item.createAtPropertyImpl()
                .set(ZonedDateTime.ofInstant(Instant.ofEpochMilli(post.getCreateAt()), ZoneId.systemDefault()));
        return item;
    }

    public PostItem() {
        var view = new ImageView();
        view.setFitWidth(64);
        view.setFitHeight(64);
        view.setStyle("-fx-border-radius: 32px; -fx-background-radius: 32px;");
        view.imageProperty().bind(imageProperty());
        getChildren().add(view);
        VBox postBox = new VBox();
        HBox postMetadata = new HBox();
        postMetadata.setSpacing(5);
        postMetadata.setAlignment(Pos.CENTER_LEFT);
        postBox.getChildren().add(postMetadata);
        var senderInfo = new Label();
        postMetadata.getChildren().add(senderInfo);
        senderInfo.setStyle("-fx-font-size: 16");
        StringExpression senderInfoContents = senderNameProperty() //
                .concat(botIndicatorProperty()) //
                .concat("@") //
                .concat(channelDisplayNameProperty());
        senderInfo.textProperty().bind(senderInfoContents);
        var createAtLabel = new Label();
        postMetadata.getChildren().add(createAtLabel);
        createAtLabel.textProperty().bind(createAtTextProperty());
        var messageLabel = new Label();
        postBox.getChildren().add(messageLabel);
        messageLabel.textProperty().bind(messageProperty());
        getChildren().add(postBox);
    }

    private ReadOnlyStringWrapper postIdProperty;

    public ReadOnlyStringProperty postIdProperty() {
        return postIdPropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyStringWrapper postIdPropertyImpl() {
        if (postIdProperty == null) {
            postIdProperty = new ReadOnlyStringWrapper(null, "postId");
        }
        return postIdProperty;
    }

    private ReadOnlyObjectWrapper<Path> profilePictureProperty;

    public ReadOnlyObjectProperty<Path> profilePictureProperty() {
        return profilePicturePropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyObjectWrapper<Path> profilePicturePropertyImpl() {
        if (profilePictureProperty == null) {
            profilePictureProperty = new ReadOnlyObjectWrapper<>(null, "profilePicture");
            profilePictureProperty.addListener(new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends Path> observable, Path oldValue, Path newValue) {
                    try {
                        imagePropertyImpl().set(new Image(Files.newInputStream(newValue)));
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            });
        }
        return profilePictureProperty;
    }

    private ReadOnlyObjectWrapper<Image> imageProperty;

    private ReadOnlyObjectProperty<Image> imageProperty() {
        return imagePropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyObjectWrapper<Image> imagePropertyImpl() {
        if (imageProperty == null) {
            imageProperty = new ReadOnlyObjectWrapper<>(null, "image");
        }
        return imageProperty;
    }

    private ReadOnlyStringWrapper senderNameProperty;

    public ReadOnlyStringProperty senderNameProperty() {
        return senderNamePropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyStringWrapper senderNamePropertyImpl() {
        if (senderNameProperty == null) {
            senderNameProperty = new ReadOnlyStringWrapper(null, "senderName");
        }
        return senderNameProperty;
    }

    private ReadOnlyBooleanWrapper botPostProperty;

    public ReadOnlyBooleanProperty botPostProperty() {
        return botPostPropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyBooleanWrapper botPostPropertyImpl() {
        if (botPostProperty == null) {
            botPostProperty = new ReadOnlyBooleanWrapper(false, "botPost");
        }
        return botPostProperty;
    }

    private ReadOnlyStringProperty botIndicatorProperty;

    public ReadOnlyStringProperty botIndicatorProperty() {
        if (botIndicatorProperty == null) {
            botIndicatorProperty = new ReadOnlyStringPropertyBase() {
                @Override
                public Object getBean() {
                    return get();
                }

                @Override
                public String getName() {
                    return "botIndicator";
                }

                @Override
                public String get() {
                    return botPostProperty().get() ? "[BOT]" : "";
                }
            };
        }
        return botIndicatorProperty;
    }

    private ReadOnlyStringWrapper channelDisplayNameProperty;

    public ReadOnlyStringProperty channelDisplayNameProperty() {
        return channelDisplayNamePropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyStringWrapper channelDisplayNamePropertyImpl() {
        if (channelDisplayNameProperty == null) {
            channelDisplayNameProperty = new ReadOnlyStringWrapper(null, "channelDisplayName");
        }
        return channelDisplayNameProperty;
    }

    private ReadOnlyObjectWrapper<ZonedDateTime> createAtProperty;

    public ReadOnlyObjectProperty<ZonedDateTime> createAtProperty() {
        return createAtPropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyObjectWrapper<ZonedDateTime> createAtPropertyImpl() {
        if (createAtProperty == null) {
            createAtProperty = new ReadOnlyObjectWrapper<>(null, "createAt");
        }
        return createAtProperty;
    }

    private ReadOnlyStringProperty createAtTextProperty;

    public ReadOnlyStringProperty createAtTextProperty() {
        if (createAtTextProperty == null) {
            createAtTextProperty = new ReadOnlyStringPropertyBase() {
                @Override
                public Object getBean() {
                    return get();
                }

                @Override
                public String getName() {
                    return "createAtText";
                }

                @Override
                public String get() {
                    // TODO formatting
                    return createAtProperty().isNotNull().getValue() ? createAtProperty().get().toString() : null;
                }
            };
        }
        return createAtTextProperty;
    }

    private ReadOnlyStringWrapper messageProperty;

    public ReadOnlyStringProperty messageProperty() {
        return messagePropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyStringWrapper messagePropertyImpl() {
        if (messageProperty == null) {
            messageProperty = new ReadOnlyStringWrapper(null, "message");
        }
        return messageProperty;
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