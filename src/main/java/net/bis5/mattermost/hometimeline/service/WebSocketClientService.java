package net.bis5.mattermost.hometimeline.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.DeploymentException;

import javafx.application.Platform;
import net.bis5.mattermost.client4.MattermostClient;
import net.bis5.mattermost.hometimeline.component.PostItem;
import net.bis5.mattermost.hometimeline.controller.main.MainViewModel;
import net.bis5.mattermost.hometimeline.store.ImageCacheStore;
import net.bis5.mattermost.hometimeline.store.PreferenceStore;
import net.bis5.mattermost.hometimeline.store.PreferenceStore.BasicLoginDetail;
import net.bis5.mattermost.hometimeline.store.PreferenceStore.PreferenceKey;
import net.bis5.mattermost.hometimeline.util.Posts;
import net.bis5.mattermost.model.Post;
import net.bis5.mattermost.websocket.MattermostWebSocketClient;
import net.bis5.mattermost.websocket.WebSocketEvent;
import net.bis5.mattermost.websocket.model.PostEditedEventPayload;
import net.bis5.mattermost.websocket.model.PostDeletedEventPayload;
import net.bis5.mattermost.websocket.model.PostUpdatedEventData;
import net.bis5.mattermost.websocket.model.PostedEventPayload;
import net.bis5.mattermost.websocket.model.PostedEventPayload.PostedEventData;

@Singleton
public class WebSocketClientService {

    @Inject
    PreferenceStore preferences;
    @Inject
    MainViewModel mainViewModel;
    @Inject
    ImageCacheStore imageCacheStore;
    @Inject
    Posts posts;
    ExecutorService executor;
    private MattermostClient webClient;
    private MattermostWebSocketClient wsClient;
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    @PostConstruct
    public void initialize() {
        System.out.println("service init");
        executor = Executors.newCachedThreadPool();
        executor.execute(createInitTask());
    }

    public MattermostClient getWebClient() {
        return webClient;
    }

    private Runnable createInitTask() {
        return () -> {
            System.out.println("task start");
            String serverUrl = preferences.get(PreferenceKey.URL);
            webClient = MattermostClient.builder().url(serverUrl).build();
            BasicLoginDetail loginDetail = preferences.get(PreferenceKey.LOGIN_DETAIL); // TODO ログイン方法ごとに変える
            webClient.login(loginDetail.username, loginDetail.password);
            String accessToken = webClient.getCurrentAccessToken().get();

            String apiUrl = serverUrl + "/api/v4";
            apiUrl = apiUrl + "/websocket"; // FIXME これはMattermost4j側でやること

            wsClient = MattermostWebSocketClient.newInstance(apiUrl, accessToken);
            wsClient.getHandlers().addAllEventHandler(event -> {
                if (event.getEvent() == WebSocketEvent.POSTED) {
                    return;
                }
                // Platform.runLater(() -> mainViewModel.addDebug(event.toString()));
            });
            wsClient.getHandlers().addHandler(WebSocketEvent.POSTED, event -> {
                PostedEventPayload payload = event.cast();
                PostedEventData data = payload.getData();
                Post post = data.getPost();
                if (post.getType() != null && post.getType().getCode().startsWith("system_")) {
                    return;
                }
                Path profilePic = posts.downloadProfilePic(post, serverUrl, webClient);
                PostItem item = PostItem.create(post, serverUrl, data.getSenderName(), data.getChannelDisplayName(),
                        profilePic);
                Platform.runLater(() -> mainViewModel.add(item));
            });
            wsClient.getHandlers().addHandler(WebSocketEvent.POST_EDITED, event -> {
                PostEditedEventPayload payload = event.cast();
                PostUpdatedEventData data = payload.getData();
                Post post = data.getPost();
                Platform.runLater(() -> mainViewModel.update(post));
            });
            wsClient.getHandlers().addHandler(WebSocketEvent.POST_DELETED, event -> {
                PostDeletedEventPayload payload = event.cast();
                PostUpdatedEventData data = payload.getData();
                Post post = data.getPost();
                Platform.runLater(() -> mainViewModel.delete(post));
            });
            AtomicInteger retryCount = new AtomicInteger(0);
            wsClient.getHandlers().addOnCloseHandler(session -> {
                if (shutdown.get()) {
                    return;
                }
                System.out.printf("Retry #%d", retryCount.incrementAndGet());
                openConnection(wsClient, webClient);
            });
            openConnection(wsClient, webClient);
        };
    }

    private void openConnection(MattermostWebSocketClient wsClient, MattermostClient webClient) {
        try {
            wsClient.openConnection();
            System.out.println("connection opened");
        } catch (DeploymentException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void shutdown() {
        shutdown.set(true);
        executor.shutdown();
        try {
            wsClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        webClient.logout();
        webClient.close();
    }

}