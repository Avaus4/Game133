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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
        text.setFill(Color.MEDIUMSLATEBLUE);
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

        pond = new Circle(20);
        pond.setFill(Color.LIGHTSKYBLUE);
        add(pond);
        translate(rand.nextInt(rand.nextInt(250) + 100),
                (rand.nextInt(350) + 200));

        text = new GameText(String.valueOf(size));
        add(text);
    }

}

class Cloud extends GameObject{
    Random rand = new Random();
    Circle cloud;
    double size = 30;
    int r;
    GameText text;
    public Cloud(){
        r = rand.nextInt(20)+30;
        cloud = new Circle(r);
        cloud.setFill(Color.WHITE);
        add(cloud);
        translate(rand.nextInt(rand.nextInt(250) + 100),
                (rand.nextInt(350) + 200));

        text = new GameText(String.valueOf(size));
        //lock
        add(text);
    }

    public int getRadius() { return r;}
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
    boolean ignition;
    boolean shoot;
    GameText text;
    int fuel = 1000;
    public Helicopter() {
        Rectangle rect = new Rectangle(5, 40);
        rect.setStroke(Color.LIMEGREEN);
        rect.setFill(Color.LIGHTSLATEGRAY);
        add(rect);
        Circle circ = new Circle(20);
        circ.setStroke(Color.LIMEGREEN);
        add(circ);
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

class Game{
    double old = -1;
    double elapsedTime = 0;
    double ft;
    double conv_to_sec = 1e9;
    int frameCount_avg = 30;
    int frameCount = 0;

    public void Collision(Helicopter heli, Cloud cloud, Pond pond){
        if(heli.myTranslation.getX()
                > (cloud.myTranslation.getX() - cloud.getRadius())
                && heli.myTranslation.getX() <
                (cloud.myTranslation.getX() + cloud.getRadius())
                && heli.myTranslation.getY() <
                (cloud.myTranslation.getY() + cloud.getRadius())
                && heli.myTranslation.getY() >
                (cloud.myTranslation.getY() - cloud.getRadius())
        ){
            System.out.println("COLLISION");

            if(heli.shoot){
                pond.scale(pond.getScaleX() + 10, pond.getScaleY() + 10);  //TODO fix scaling
                System.out.println("PEW PEW PEW");
                System.out.println(pond.getScaleX());
            }

        }
    }


    public Game(Pane root, Helicopter heli, Set<KeyCode> keysDown) {

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
                    heli.shoot = true;
                    Collision(heli, (Cloud) root.getChildren().get(2),
                            (Pond) root.getChildren().get(1));
                }
                //System.out.println(keysDown); // FOR TEST PURPOSES

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
