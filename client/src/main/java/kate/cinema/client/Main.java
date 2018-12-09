package kate.cinema.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import kate.cinema.client.controller.MainController;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;


public class Main extends Application {
    private static final Logger logger = LogManager.getLogger(Main.class);

    private static final String WINDOW_TITLE = "Cinema";
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 400;

    private Stage window;
    private Scene startPage;

    private BorderPane mainView;

    @Override
    public void start(Stage primaryStage) {

        this.window = primaryStage;
        window.setTitle(WINDOW_TITLE);

        initRootLayout();
    }


    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/view/mainView.fxml"));
            mainView = (BorderPane) loader.load();

            Scene scene = new Scene(mainView, WINDOW_WIDTH, WINDOW_HEIGHT);
            window.setScene(scene);

            //Give the controller access to the main.
            MainController controller = loader.getController();
            controller.setMain(this);

            window.show();
        } catch (IOException e) {
            logger.error("Cannot init root layout" + e);
        }
    }

    public void showGuestMainView() {
        logger.debug("show guestMainView view from main");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/view/guestMainView.fxml"));
            AnchorPane guestMainView = (AnchorPane) loader.load();

            mainView.setCenter(guestMainView);
        } catch (IOException e) {
            logger.error("Cannot show guestMainView" + e);
        }
    }

    public void showView(String viewPath) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource(viewPath));
            AnchorPane view = (AnchorPane) loader.load();

            mainView.setCenter(view);
        } catch (IOException e) {
            logger.error("Cannot show view " + viewPath + " ! " + e);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
