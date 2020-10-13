import database.tools.Connector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ui.fragments.AppFragment;
import ui.utils.AppProps;

public class App extends Application {

    public static Scene mainScene;

    @Override
    public void start(Stage stage) throws Exception {
        Connector.getInstance().connect();
        AppProps.setSize();
        Scene scene = new Scene(new AppFragment().createAppFragment(), AppProps.appWidth, AppProps.appHeight, Color.TRANSPARENT);
        AppProps.appScene = scene;
        AppFragment.initScene(scene);
        scene.getStylesheets().add(getClass().getResource("/vendors/app.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("MARC");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/vendors/app-icon.png")));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
