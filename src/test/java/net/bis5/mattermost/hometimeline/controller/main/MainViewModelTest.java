package net.bis5.mattermost.hometimeline.controller.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import net.bis5.mattermost.hometimeline.component.PostItem;
import net.bis5.mattermost.model.Post;

@ExtendWith(ApplicationExtension.class)
public class MainViewModelTest {

    @Test
    public void testFindItemExists() throws URISyntaxException {
        MainViewModel viewModel = new MainViewModel();
        String siteUrl = "http://localhost:8065";
        String senderName = "sender";
        String channelDisplayName = "ChannelDispName";
        Path profilePicture = Paths.get(getClass().getResource("/incoming_webhook.jpg").toURI());
        Post post1 = createDummyPost();
        Post post2 = createDummyPost();
        Post post3 = createDummyPost();
        PostItem item1 = PostItem.create(post1, siteUrl, senderName, channelDisplayName, profilePicture);
        PostItem item2 = PostItem.create(post2, siteUrl, senderName, channelDisplayName, profilePicture);
        PostItem item3 = PostItem.create(post3, siteUrl, senderName, channelDisplayName, profilePicture);
        viewModel.add(item1);
        viewModel.add(item2);
        viewModel.add(item3);
        String targetId = post2.getId();

        Optional<PostItem> foundItem = viewModel.find(targetId);
        assertTrue(foundItem.isPresent());
        assertSame(item2, foundItem.get());
    }

    @Test
    public void testUpdatePostItem() throws URISyntaxException {
        MainViewModel viewModel = new MainViewModel();
        String siteUrl = "http://localhost:8065";
        String senderName = "sender";
        String channelDisplayName = "ChannelDispName";
        Path profilePicture = Paths.get(getClass().getResource("/incoming_webhook.jpg").toURI());
        Post post1 = createDummyPost();
        Post post2 = createDummyPost();
        Post post3 = createDummyPost();
        PostItem item1 = PostItem.create(post1, siteUrl, senderName, channelDisplayName, profilePicture);
        PostItem item2 = PostItem.create(post2, siteUrl, senderName, channelDisplayName, profilePicture);
        PostItem item3 = PostItem.create(post3, siteUrl, senderName, channelDisplayName, profilePicture);
        viewModel.add(item1);
        viewModel.add(item2);
        viewModel.add(item3);

        Post updatedPost = createDummyPost();
        updatedPost.setId(post2.getId());
        updatedPost.setMessage("New Message HERE!!!");

        viewModel.update(updatedPost);

        PostItem postItem = viewModel.find(updatedPost.getId()).get();
        assertEquals(updatedPost.getMessage(), postItem.messageProperty().get());
    }

    private Post createDummyPost() {
        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setMessage("Hello World!" + ThreadLocalRandom.current().nextInt());
        return post;
    }
}