import javafx.application.Application; // JavaFX application support
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

interface Updatable {

}

abstract class GameObject extends Group {
    void add(Node node){this.getChildren().add(node);}
}


class GameText {

}

class Pond {

}

class Cloud {

}

class PondAndCloud {

}

class Helipad extends GameObject {
    public Helipad(){

        Rectangle rect = new Rectangle(100, 100);
        rect.setStroke(Color.LIMEGREEN);
        rect.setTranslateX(150);
        rect.setTranslateY(10);
        add(rect);

        Circle circ = new Circle(40);
        circ.setStroke(Color.LIMEGREEN);
        circ.setTranslateX(200);
        circ.setTranslateY(60);
        add(circ);

    }


}


class Helicopter extends  GameObject{

    public Helicopter() {
        Rectangle rect = new Rectangle(5, 40);
        rect.setStroke(Color.LIMEGREEN);
        rect.setTranslateX(198);
        rect.setTranslateY(50);
        rect.setFill(Color.LIGHTSLATEGRAY);
        add(rect);

        Circle circ = new Circle(20);
        circ.setStroke(Color.LIMEGREEN);
        circ.setTranslateX(200);
        circ.setTranslateY(50);
        add(circ);
    }


}

class Game {

    public Game() {

    }

}

public class GameApp extends Application {
    /**
     * The width of the game window.
     */
    private static final int GAME_WIDTH = 400;
    /**
     * The height of the game window.
     */
    private static final int GAME_HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) {
        // Create group as root for all nodes in the scene graph
        Pane root = new Pane();
        root.setScaleY(-1);

        Helipad hp = new Helipad();
        Helicopter heli = new Helicopter();

        root.getChildren().addAll(hp, heli);

        // show the initial Scene for your application
        Scene scene = new Scene(root, GAME_WIDTH, GAME_HEIGHT, Color.BLACK);

        // set the title of the Stage
        primaryStage.setTitle("GAME_WINDOW_TITLE");

        // add the scene to the Stage
        primaryStage.setScene(scene);

        // prevent window resizing by user
        primaryStage.setResizable(false);

        // display the Stage
        primaryStage.show();


    } // end of start()

    /**
     * This method is the main entry point for the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch();
    }
}
