package net.bis5.mattermost.hometimeline.util;

import net.bis5.mattermost.model.Post;

public class Posts {
    private Posts() {
        throw new AssertionError();
    }

    public static String getProfilePictureUrl(Post post, String siteUrl) {
        if (post.getProps() != null) {
            String overrideIcon = (String) post.getProps().getOrDefault("override_icon", null);
            if (overrideIcon != null) {
                return overrideIcon;
            }
        }
        return String.format("%s/api/v4/users/%s/image", siteUrl, post.getUserId());
    }

    public static boolean isBotPost(Post post) {
        if (post.getProps() == null) {
            return false;
        }
        // これだけで良い? IntegrationのBot accountはこれで判定できる?
        return Boolean.parseBoolean((String) post.getProps().getOrDefault("from_webhook", "false"));
    }

    public static boolean hasOverrideIconUrl(Post post) {
        if (post.getProps() == null) {
            return false;
        }
        return post.getProps().get("override_icon_url") != null;
    }
}