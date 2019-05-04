package net.bis5.mattermost.hometimeline.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import net.bis5.mattermost.client4.MattermostClient;

@Singleton
public class ImageCacheStore {

    private Map<String/* url or id */, Path/* imageFile */> cacheMap = new ConcurrentHashMap<>();

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

    public Path downloadExternalImage(String iconUrl) {
        return cacheMap.computeIfAbsent(iconUrl, this::downloadExternalImage0);
    }

    private Path downloadExternalImage0(String iconUrl) {
        Client jaxrsClient = ClientBuilder.newClient();
        InputStream is = jaxrsClient.target(iconUrl) //
                .request() //
                .get(InputStream.class);
        try {
            Path imageFile = Files.createTempFile("externalimg", null);
            Files.copy(is, imageFile, StandardCopyOption.REPLACE_EXISTING);
            return imageFile;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Path downloadInternalImage(MattermostClient webClient, String iconUrl) {
        return cacheMap.computeIfAbsent(iconUrl, url -> downloadInternalImage0(webClient, url));
    }

    private Path downloadInternalImage0(MattermostClient webClient, String iconUrl) {
        Client jaxrsClient = ClientBuilder.newClient();
        InputStream is = jaxrsClient.target(iconUrl) //
                .request() //
                .header("Authorization", String.format("Bearer %s", webClient.getCurrentAccessToken().orElse(""))) //
                .get(InputStream.class);
        try {
            Path imageFile = Files.createTempFile("internalimg", null);
            Files.copy(is, imageFile, StandardCopyOption.REPLACE_EXISTING);
            return imageFile;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}