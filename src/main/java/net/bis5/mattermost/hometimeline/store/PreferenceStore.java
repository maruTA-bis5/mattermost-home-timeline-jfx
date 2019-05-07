package net.bis5.mattermost.hometimeline.store;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PreferenceStore {

    public enum PreferenceKey {
        URL, LOGIN_METHOD, LOGIN_DETAIL, MAX_POSTS, FORMATTING;
    }

    private final Map<PreferenceKey, Object> preferences = new EnumMap<>(PreferenceKey.class);

    @PostConstruct
    public void loadPreferences() {
        // TODO 永続化ストアから読み込む
        // 以下はダミー
        put(PreferenceKey.URL, System.getenv("MATTERMOST_URL"));
        put(PreferenceKey.LOGIN_METHOD, LoginMethod.BASIC);
        BasicLoginDetail loginDetail = new BasicLoginDetail();
        loginDetail.username = System.getenv("MATTERMOST_USERNAME");
        loginDetail.password = System.getenv("MATTERMOST_PASSWORD");
        put(PreferenceKey.LOGIN_DETAIL, loginDetail);
        put(PreferenceKey.MAX_POSTS, 200);
        put(PreferenceKey.FORMATTING, true);
    }

    public boolean isEmpty() {
        return preferences.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(PreferenceKey key) {
        return (T) preferences.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T computeIfAbsent(PreferenceKey key, Function<PreferenceKey, T> mappingFunction) {
        return (T) preferences.computeIfAbsent(key, mappingFunction);
    }

    public <T> void put(PreferenceKey key, T value) {
        // XXX こっち側で、値のバリデーションを実施した方が良いんだろうな
        preferences.put(key, value);
    }

    public static class BasicLoginDetail {
        public String username;
        public String password;
        // TODO accessors
    }

    public static enum LoginMethod {
        BASIC;
        // TODO OAUTH, SAML
    }

}