import javafx.animation.AnimationTimer;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

interface Updatable {
    void update();
}

abstract class GameObject extends Group implements Updatable {
    protected Translate myTranslation;
    protected Rotate myRotation;
    protected Scale myScale;

    void add(Node node){this.getChildren().add(node);}

    public GameObject(){
        myTranslation = new Translate();
        myRotation = new Rotate();
        myScale = new Scale();
        this.getTransforms().addAll(myTranslation,myRotation,myScale);
    }

    public void rotate(double degrees) {
        myRotation.setAngle(degrees);
    }

    public void scale(double sx, double sy) {
        myScale.setX(sx);
        myScale.setY(sy);
    }
    public double getMyRotation(){
        return myRotation.getAngle();
    }

    public void translate(double tx, double ty) {
        myTranslation.setX(tx);
        myTranslation.setY(ty);
    }
}


class GameText {

}

class Pond {
    Random rand = new Random();
    Circle pond;
    public Pond(){
        pond = new Circle(20);
        pond.setTranslateY(rand.nextInt(230) + 530);
        pond.setTranslateX(rand.nextInt(290) + 10);
        pond.setFill(Color.LIGHTSKYBLUE);
    }

}

class Cloud{
    Random rand = new Random();
    Circle cloud;
    public Cloud(){
        cloud = new Circle(rand.nextInt(20)+30);
        cloud.setTranslateY(rand.nextInt(230) + 530);
        cloud.setTranslateX(rand.nextInt(290) + 10);
        cloud.setFill(Color.WHITE);
    }

}

class PondAndCloud {

    public PondAndCloud(Pane root){
        Pond pond = new Pond();
        Cloud cloud = new Cloud();

        root.getChildren().addAll(pond.pond, cloud.cloud);
    }

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


    @Override
    public void update() {

    }
}


class Helicopter extends GameObject{

    double velocity = 3;
    int rotN = 15;

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

    @Override
    public void update() {
        Rotate rot = new Rotate();
        rot.setPivotY(getTranslateY());
        rot.setPivotX(getTranslateX());
        getTransforms().add(rot);
    }
}

class Game {

    public Game(Pane root, Helicopter heli, Set<KeyCode> keysDown) {
        Helipad hp = new Helipad();
        AnimationTimer loop = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if (keysDown.contains(KeyCode.W))
                    heli.setTranslateY(heli.getTranslateY() + heli.velocity);
                if (keysDown.contains(KeyCode.S))
                    heli.setTranslateY(heli.getTranslateY() - heli.velocity);
                if (keysDown.contains(KeyCode.A)) {
                    heli.setRotate(heli.rotN);
                    //heli.rotN += 15;
                    heli.rotate(heli.getMyRotation()+1);
                    System.out.println(heli.getMyRotation());
                }
                if (keysDown.contains(KeyCode.D))
                    heli.setRotate(heli.getRotate() - heli.velocity);


                // System.out.println(keysDown);
            }
        };
        loop.start();

        root.getChildren().addAll(hp, heli);
    }
}

public class GameApp extends Application {
    private static final int GAME_WIDTH = 400;

    private static final int GAME_HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) {
        // Create group as root for all nodes in the scene graph
        Pane root = new Pane();
        root.setScaleY(-1);
        Helicopter heli = new Helicopter();
        PondAndCloud pc = new PondAndCloud(root);


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

        Set<KeyCode> keysdown = new HashSet<>();
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                keysdown.add(event.getCode());
            }
        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                keysdown.remove(event.getCode());
            }
        });

        Game game = new Game(root, heli, keysdown);

    } // end of start()

    public static void main(String[] args) {
        Application.launch();
    }
}
