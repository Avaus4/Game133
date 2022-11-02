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

    public GameObject() {
        myTranslation = new Translate();
        myRotation = new Rotate();
        myScale = new Scale();
        this.getTransforms().addAll(myRotation, myTranslation,  myScale);
    }

    public void rotate(double degrees) {
        myRotation.setAngle(degrees);
    }

    public void scale(double sx, double sy) {
        myScale.setX(sx);
        myScale.setY(sy);
    }

    public void translate(double tx, double ty) {
        myTranslation.setX(tx + myTranslation.getX());
        myTranslation.setY(ty + myTranslation.getY());
    }

    public double getMyRotation() {
        return myRotation.getAngle();
    }

    void add(Node node) {
        this.getChildren().add(node);
    }

    public void update() {
        for (Node n : getChildren()) {
            if (n instanceof Updatable)
                ((Updatable) n).update();
        }
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

    double velocity = 0;
    double vy;
    double vx;

    public Helicopter() {
        Rectangle rect = new Rectangle(5, 40);
        rect.setStroke(Color.LIMEGREEN);
        rect.setFill(Color.LIGHTSLATEGRAY);
        add(rect);
        Circle circ = new Circle(20);
        circ.setStroke(Color.LIMEGREEN);
        add(circ);
    }

    @Override
    public void update() {
        if(myRotation.getAngle() !=  0) {
            myTranslation.setX(myTranslation.getX() + vx);
        }
        myTranslation.setY(myTranslation.getY() + vy);
        pivot();
    }

    public void moveLeft(){
        myRotation.setAngle(getMyRotation());
        double deg = getMyRotation();
        vx = velocity * Math.sin(Math.toRadians(deg) * -1);
        vy = velocity * Math.cos(Math.toRadians(deg));
    }

    public void moveRight(){
        myRotation.setAngle(getMyRotation());
        double deg = getMyRotation();
        vx = velocity * Math.sin(Math.toRadians(deg) * -1);
        vy = velocity * Math.cos(Math.toRadians(deg));
    }

    public void pivot(){
        myRotation.setPivotY(myTranslation.getY());
        myRotation.setPivotX(myTranslation.getX());
    }
}

class Game {

    public Game(Pane root, Helicopter heli, Set<KeyCode> keysDown) {
        //Helipad hp = new Helipad();

        AnimationTimer loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                heli.update();
                heli.rotate(heli.getMyRotation());
                if (keysDown.contains(KeyCode.W)){
                    heli.velocity+= .1; //TODO make sure this doesnt backfire
                }
                if (keysDown.contains(KeyCode.S))
                    heli.setTranslateY(heli.getTranslateY() - heli.velocity);
                if (keysDown.contains(KeyCode.A)) {
                    heli.rotate(heli.getMyRotation() + 15);
                    heli.moveLeft();
                    System.out.println(heli.vy); //TEST case
                }
                if (keysDown.contains(KeyCode.D)) {
                    heli.rotate(heli.getMyRotation() - 15);
                    heli.moveRight();
                }
                if(keysDown.contains(KeyCode.I)){
                    //ignition of helicopter
                }
                // System.out.println(keysDown);
            }
        };
        loop.start();

//        root.getChildren().addAll(hp, heli);
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
        Helipad hp = new Helipad();
        root.getChildren().addAll(hp, heli);
        heli.translate(200, 50);
        heli.pivot();
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
