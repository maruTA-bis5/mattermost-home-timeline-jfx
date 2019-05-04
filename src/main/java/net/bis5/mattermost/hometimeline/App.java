package net.bis5.mattermost.hometimeline;

import javax.enterprise.inject.se.SeContainer;

import org.jboss.weld.environment.se.Weld;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    private static final Weld weld = new Weld();
    public static SeContainer container;

    public static void main(String... args) {
        container = weld.initialize();
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        container.select(Booter.class).get().start(stage);
    }

}
