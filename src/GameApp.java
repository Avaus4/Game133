import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

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
        this.setManaged(false);
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


class GameText extends GameObject{
    Text text;
    public GameText(String textString){
        text = new Text(textString);
        text.setScaleY(-1);
        text.setFont(Font.font(16));
        text.setFill(Color.FIREBRICK);
        add(text);
    }
    public GameText(){
        this("");
    }
    public void setText(String textString){
        text.setText(textString);
    }
    public void setTextLoc(GameObject object){
        this.myTranslation = object.myTranslation;
        this.myRotation = object.myRotation;
    }

}

class Pond extends GameObject {
    Random rand = new Random();
    Circle pond;
    GameText text;
    int size = 20;
    public Pond(){

        pond = new Circle(size);
        pond.setFill(Color.LIGHTSKYBLUE);
        add(pond);
        translate(rand.nextInt(rand.nextInt(250) + 100),
                (rand.nextInt(350) + 200));

        text = new GameText(String.valueOf(size));
        add(text);
    }

    public void beingSeeded(int n){
        if(n > 1){
            size++;
            this.scale(myScale.getX() + 0.05, myScale.getY() + 0.05);
            text.setText(size + " %");
        }
    }

}

class Cloud extends GameObject{
    Random rand = new Random();
    Circle cloud;
    int r;
    int seed = 0;
    GameText text;
    public Cloud(){
        r = rand.nextInt(20)+30;
        cloud = new Circle(r);
        cloud.setFill(Color.WHITE);
        add(cloud);
        translate(rand.nextInt(rand.nextInt(250) + 100),
                (rand.nextInt(350) + 200));

        text = new GameText(String.valueOf(seed) + " %");
        add(text);
    }
    public int getRadius() { return r;}

    public int seeding(){
        if(seed <= 100){ seed++; }
        else if(seed>0){ seed--; }
        text.setText(String.valueOf(seed + "%"));
        return seed;
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

class HeloBody extends Group{
    public HeloBody(){
        Group hbGroup = new Group();

        Circle body = new Circle(10);
        body.setScaleY(2);
        body.setTranslateY(10);
        body.setFill(Color.PERU);

        Circle window = new Circle(8);
        window.setScaleY(2);
        window.setTranslateY(14);
        window.setFill(Color.SKYBLUE);

        Rectangle cutOff = new Rectangle(16, 20);
        cutOff.setTranslateY(-4);
        cutOff.setTranslateX(-8);
        cutOff.setFill(Color.PERU);

        Rectangle leftSkid = new Rectangle(5, 40);
        leftSkid.setTranslateX(-20);
        leftSkid.setTranslateY(-12);
        leftSkid.setFill(Color.PERU);

        Rectangle rightSkid = new Rectangle(5, 40);
        rightSkid.setTranslateX(15);
        rightSkid.setTranslateY(-12);
        rightSkid.setFill(Color.PERU);

        Rectangle tail = new Rectangle(5, 40);
        tail.setTranslateX(-3);
        tail.setTranslateY(-50);
        tail.setFill(Color.PERU);

        Rectangle leftConnect1 = new Rectangle(4, 3);
        leftConnect1.setTranslateX(-14);
        leftConnect1.setTranslateY(-2);
        leftConnect1.setFill(Color.PERU);

        Rectangle leftConnect2 = new Rectangle(4, 3);
        leftConnect2.setTranslateX(-14);
        leftConnect2.setTranslateY(15);
        leftConnect2.setFill(Color.PERU);

        Rectangle rightConnect1 = new Rectangle(4, 3);
        rightConnect1.setTranslateX(10);
        rightConnect1.setTranslateY(-2);
        rightConnect1.setFill(Color.PERU);

        Rectangle rightConnect2 = new Rectangle(4, 3);
        rightConnect2.setTranslateX(10);
        rightConnect2.setTranslateY(15);
        rightConnect2.setFill(Color.PERU);

        Rectangle rotorConnect = new Rectangle(4,3);
        rotorConnect.setTranslateX(3);
        rotorConnect.setTranslateY(-45);
        rotorConnect.setFill(Color.PERU);

        Rectangle rearRotor = new Rectangle(3,22);
        rearRotor.setTranslateX(8);
        rearRotor.setTranslateY(-55);
        rearRotor.setFill(Color.PERU);

        Circle dot = new Circle(5);
        dot.setFill(Color.LIME);

        this.getChildren().addAll(body, window,  cutOff, leftSkid,
                rightSkid, tail, leftConnect1, leftConnect2, rightConnect1,
                rightConnect2, rotorConnect, rearRotor, dot);
    }
}


class Helicopter extends GameObject{
    double velocity = 0;
    double vy;
    double vx;
    boolean ignition;
    GameText text;
    int fuel = 1000;
    public Helicopter() {
      HeloBody hb = new HeloBody();
      add(hb);

        text = new GameText(String.valueOf(fuel));
        text.myTranslation.setX(0);
        text.myTranslation.setY(0);
        add(text);
        text.setTextLoc(this);
    }

    public void throttle(Boolean b){
        if(ignition){
            if(b){
                if(velocity < 10)
                    velocity+=.1;
            }
            else{
                if(velocity > -10)
                    velocity+= -.2;
            }
        }
    }


    @Override
    public void update() {
        if(myRotation.getAngle() !=  0) {
            myTranslation.setX(myTranslation.getX() + vx);
            myTranslation.setY(myTranslation.getY() + vy);
        }
        else {
            myTranslation.setY(myTranslation.getY() + velocity);
        }
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

class Game extends Pane{
    double old = -1;
    double elapsedTime = 0;
    double conv_to_sec = 1e9;
    int frameCount_avg = 30;
    int frameCount = 0;



    public boolean isHeliCloudCollision(Helicopter heli, Cloud cloud){
        return  heli.myTranslation.getX()
                > (cloud.myTranslation.getX() - cloud.getRadius())
                && heli.myTranslation.getX() <
                (cloud.myTranslation.getX() + cloud.getRadius())
                && heli.myTranslation.getY() <
                (cloud.myTranslation.getY() + cloud.getRadius())
                && heli.myTranslation.getY() >
                (cloud.myTranslation.getY() - cloud.getRadius());
    }



    public Game(Pane root, Helicopter heli, Set<KeyCode> keysDown) {

        Pond pond = (Pond) root.getChildren().get(1);
        Cloud cloud = (Cloud) root.getChildren().get(2);

        AnimationTimer loop = new AnimationTimer() {
            @Override
            public void handle(long nano) {
                heli.update();
                heli.rotate(heli.getMyRotation());
                if (keysDown.contains(KeyCode.W)){
                   heli.throttle(true);
                }
                if (keysDown.contains(KeyCode.S)){
                    heli.throttle(false);
                }
                if (keysDown.contains(KeyCode.A)) {
                    heli.rotate(heli.getMyRotation() + 15);
                    heli.moveLeft();
                }
                if(keysDown.contains(KeyCode.D)) {
                    heli.rotate(heli.getMyRotation() - 15);
                    heli.moveRight();
                }
                if(keysDown.contains(KeyCode.I)){
                   heli.ignition = true;
                }
                if(keysDown.contains(KeyCode.SPACE)){
                   if(isHeliCloudCollision(heli, cloud)){
                       cloud.seeding();
                   }
                }

                if (frameCount % 60 == 0){
                    pond.beingSeeded(cloud.seed);
                }
               // System.out.println(keysDown); // FOR TEST PURPOSES

                if (old < 0) old = nano;
                double delta = (nano - old) / conv_to_sec;

                old = nano;
                elapsedTime += delta;
                double fps = (1 / delta);

                if (frameCount % frameCount_avg == 0) { //TODO need to revisit when throttle leads to more fuel usage
                   if(heli.ignition == true){
                       heli.fuel -= 1;
                       heli.text.setText(String.valueOf(heli.fuel));
                   }

                }

                frameCount++;
            }

        };
        loop.start();
    }
}

public class GameApp extends Application {
    private static final int GAME_WIDTH = 400;

    private static final int GAME_HEIGHT = 800;//TODO set back to 800

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        root.setScaleY(-1);
        Helicopter heli = new Helicopter();
        Helipad hp = new Helipad();
        Pond pond = new Pond();
        Cloud cloud = new Cloud();
        root.getChildren().addAll(hp, pond, cloud, heli);
        heli.translate(200, 50);
        heli.pivot();



        // show the initial Scene for your application
        Scene scene = new Scene(root, GAME_WIDTH, GAME_HEIGHT, Color.BLACK);

        // set the title of the Stage
        primaryStage.setTitle("Rain Maker");

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

    }

    public static void main(String[] args) {
        Application.launch();
    }
}
