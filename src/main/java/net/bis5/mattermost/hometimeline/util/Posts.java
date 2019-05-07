package net.bis5.mattermost.hometimeline.util;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Singleton;

import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.hometimeline.store.ImageCacheStore;
import net.bis5.mattermost.model.Post;

@Singleton
public class Posts {

    public static Utils utils() {
        return Utils.INSTANCE;
    }

    public static class Utils {
        private static final Utils INSTANCE = new Utils();

        private Utils() {
        }

        public boolean isBotPost(Post post) {
            if (post.getProps() == null) {
                return false;
            }
            // これだけで良い? IntegrationのBot accountはこれで判定できる?
            return Boolean.parseBoolean((String) post.getProps().getOrDefault("from_webhook", "false"));
        }

        public boolean hasOverrideIconUrl(Post post) {
            if (post.getProps() == null) {
                return false;
            }
            String overrideIconUrl = (String) post.getProps().get("override_icon_url");
            return overrideIconUrl != null && !overrideIconUrl.isEmpty();
        }

        public boolean isUseUserIcon(Post post) {
            if (post.getProps() == null) {
                return false;
            }
            return Boolean.parseBoolean((String) post.getProps().getOrDefault("use_user_icon", "false"));
        }
    }

    public String getProfilePictureUrl(Post post, String siteUrl) {
        if (post.getProps() != null) {
            String overrideIcon = (String) post.getProps().getOrDefault("override_icon", null);
            if (overrideIcon != null) {
                return overrideIcon;
            }
        }
        return String.format("%s/api/v4/users/%s/image", siteUrl, post.getUserId());
    }

    @Inject
    ImageCacheStore imageStore;

    public Path downloadProfilePic(Post post, String serverUrl, MattermostClient webClient) {
        Path profilePic;
        if (utils().hasOverrideIconUrl(post)) {
            String iconUrl = (String) post.getProps().get("override_icon_url");
            if (iconUrl.startsWith(serverUrl) || iconUrl.startsWith("/")) {
                profilePic = imageStore.downloadInternalImage(webClient, iconUrl);
            } else {
                profilePic = imageStore.downloadExternalImage(iconUrl);
            }
        } else {
            if (utils().isBotPost(post) && !utils().isUseUserIcon(post)) {
                // from webhook post but override_icon_url does not specified
                try {
                    profilePic = Paths.get(getClass().getResource("/incoming_webhook.jpg").toURI());
                } catch (URISyntaxException e) {
                    throw new InternalError(e);
                }
            } else {
                profilePic = imageStore.downloadProfileImage(webClient, post.getUserId());
            }
        }
        return profilePic;
    }
}