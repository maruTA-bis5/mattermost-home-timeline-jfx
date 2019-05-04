package net.bis5.mattermost.hometimeline.producer;

import java.lang.reflect.InvocationTargetException;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Singleton;

import javafx.fxml.FXMLLoader;
import net.bis5.mattermost.hometimeline.App;

@Singleton
public class FXMLLoaderProducer {

    @Produces
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public FXMLLoader createInstance() {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(cls -> {
            // @FXMLなフィールドが解決できなかったので変えた
            // System.out.println("select class: " + cls);
            // Object controller = instances.select(cls).get();
            // System.out.println(System.identityHashCode(controller));
            try {
                Object controller = cls.getConstructor().newInstance();
                // inject controller's field
                BeanManager beanManager = App.container.getBeanManager();
                CreationalContext creationalContext = beanManager.createCreationalContext(null);

                AnnotatedType annotatedType = beanManager.createAnnotatedType(cls);
                InjectionTarget injectionTarget = beanManager.createInjectionTarget(annotatedType);
                injectionTarget.inject(controller, creationalContext);

                return controller;
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                throw new IllegalStateException(e);
            }
        });
        return loader;
    }
}