package net.bis5.mattermost.hometimeline.store;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import net.bis5.mattermost.client4.MattermostClient;

@Singleton
public class ImageCacheStore {

    private Map<String/* url */, Path/* imageFile */> cacheMap = new ConcurrentHashMap<>();

    public Path downloadProfileImage(MattermostClient webClient, String userId) {
        return cacheMap.computeIfAbsent(userId, id -> downloadProfileImage0(webClient, id));
    }

    private Path downloadProfileImage0(MattermostClient webClient, String userId) {
        byte[] image = webClient.getProfileImage(userId).readEntity();
        try {
            Path imageFile = Files.createTempFile(userId + "_profimage", null);
            try (OutputStream os = Files.newOutputStream(imageFile, StandardOpenOption.TRUNCATE_EXISTING)) {
                os.write(image);
                os.flush();
                os.close();
            }
            return imageFile;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}