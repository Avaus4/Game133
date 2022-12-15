import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import java.util.*;

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

class Background{
    public Background(Pane root){
        Image image = new Image("csc133MAP.jpg");
        ImageView view = new ImageView(image);
        view.setScaleY(-1);
        view.setFitHeight(800);//TODO set back to 800
        view.setFitWidth(800);
        root.getChildren().add(view);
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
    int size = 30;
    public Pond(){
        pond = new Circle(size);
        pond.setFill(Color.LIGHTSKYBLUE);
        add(pond);
        translate(rand.nextInt(rand.nextInt(650) + 200),
                (rand.nextInt(350) + 200));

        text = new GameText(size + " %");
        add(text);
    }

    public void beingSeeded(int n){
        if(n > 1){
            if(size<=99){
                size++;
                this.scale(myScale.getX() + 0.05, myScale.getY() + 0.05);
                text.setText(size + " %");
            }
        }
    }

}

class Cloud extends GameObject{
    Random rand = new Random();
    Circle cloud;
    int r;
    int seed = 0;
    double cloudSpeed = (rand.nextDouble(.75)+.01);
    GameText text;
    public Cloud(){
        r = rand.nextInt(20)+30;
        cloud = new Circle(r);
        cloud.setFill(Color.WHITE);
        add(cloud);
        translate(rand.nextInt(rand.nextInt(250) + 100),
                (rand.nextInt(350) + 200));

        text = new GameText(seed + " %");
        add(text);
    }
    public int getRadius() { return r;}
    public int seeding(){
        if(seed <= 100){ seed++; }
        else if(seed>0){ seed--; }
        text.setText(String.valueOf(seed + "%"));
        return seed;
    }

    public void deSeed(){
        if(seed>0){ seed--; }
        text.setText(String.valueOf(seed + "%"));
    }

    public void update(){
        myTranslation.setX(myTranslation.getX() + cloudSpeed);
        if(myTranslation.getX() > 810){
            myTranslation.setX(0);
            myTranslation.setY(rand.nextInt(550)+200);
            myTranslation.setX(myTranslation.getX() + cloudSpeed);
        }
    }
}

class DistanceLines{
    Line line0 = new Line();
    Line line1 = new Line();
    Line line2 = new Line();
    public DistanceLines(Pane linePane){
        line0.setStroke(Color.TRANSPARENT);
        line0.setStrokeWidth(3.0);
        line1.setStroke(Color.TRANSPARENT);
        line1.setStrokeWidth(3.0);
        line2.setStroke(Color.TRANSPARENT);
        line2.setStrokeWidth(3.0);
        linePane.getChildren().addAll(line0, line1, line2);
    }

    public void update(Cloud cloud, ArrayList<Pond> ponds){
        line0.setStartX(cloud.myTranslation.getX());
        line0.setStartY(cloud.myTranslation.getY());
        line0.setEndX(ponds.get(0).myTranslation.getX());
        line0.setEndY(ponds.get(0).myTranslation.getY());
        line1.setStartX(cloud.myTranslation.getX());
        line1.setStartY(cloud.myTranslation.getY());
        line1.setEndX(ponds.get(1).myTranslation.getX());
        line1.setEndY(ponds.get(1).myTranslation.getY());
        line2.setStartX(cloud.myTranslation.getX());
        line2.setStartY(cloud.myTranslation.getY());
        line2.setEndX(ponds.get(2).myTranslation.getX());
        line2.setEndY(ponds.get(2).myTranslation.getY());
    }

}
class Helipad extends GameObject {
    public Helipad(){

        Rectangle rect = new Rectangle(100, 100);
        rect.setStroke(Color.LIMEGREEN);
        rect.setTranslateX(350);
        rect.setTranslateY(10);
        add(rect);

        Circle circ = new Circle(40);
        circ.setStroke(Color.LIMEGREEN);
        circ.setTranslateX(400);
        circ.setTranslateY(60);
        add(circ);
    }

    @Override
    public void update() {

    }
}

class HeloBlade extends GameObject{
    Rectangle rotor;
    public HeloBlade(){
        rotor = new Rectangle(5, 80);
        rotor.setFill(Color.HOTPINK);
        rotor.setTranslateY(-40);
        rotor.setTranslateX(-2);
        add(rotor);
    }

    double rotationalSpeed = 0;//starting rotor speed
    double maxSpeed = 15;
    boolean off = true;
    boolean starting;
    boolean ready;
    public void update(){
       rotate(getMyRotation()+rotationalSpeed);
    }
}

class HeloBody extends Group{
    public HeloBody(){
        Group hbGroup = new Group();

        Circle body = new Circle(10);
        body.setScaleY(2);
        body.setTranslateY(10);
        body.setFill(Color.SEAGREEN);

        Circle window = new Circle(8);
        window.setScaleY(2);
        window.setTranslateY(14);
        window.setFill(Color.BLUE);

        Rectangle cutOff = new Rectangle(16, 20);
        cutOff.setTranslateY(-4);
        cutOff.setTranslateX(-8);
        cutOff.setFill(Color.SEAGREEN);

        Rectangle leftSkid = new Rectangle(5, 40);
        leftSkid.setTranslateX(-20);
        leftSkid.setTranslateY(-12);
        leftSkid.setFill(Color.SEAGREEN);

        Rectangle rightSkid = new Rectangle(5, 40);
        rightSkid.setTranslateX(15);
        rightSkid.setTranslateY(-12);
        rightSkid.setFill(Color.SEAGREEN);

        Rectangle tail = new Rectangle(5, 40);
        tail.setTranslateX(-3);
        tail.setTranslateY(-50);
        tail.setFill(Color.SEAGREEN);

        Rectangle leftConnect1 = new Rectangle(4, 3);
        leftConnect1.setTranslateX(-14);
        leftConnect1.setTranslateY(-2);
        leftConnect1.setFill(Color.SEAGREEN);

        Rectangle leftConnect2 = new Rectangle(4, 3);
        leftConnect2.setTranslateX(-14);
        leftConnect2.setTranslateY(15);
        leftConnect2.setFill(Color.SEAGREEN);

        Rectangle rightConnect1 = new Rectangle(4, 3);
        rightConnect1.setTranslateX(10);
        rightConnect1.setTranslateY(-2);
        rightConnect1.setFill(Color.SEAGREEN);

        Rectangle rightConnect2 = new Rectangle(4, 3);
        rightConnect2.setTranslateX(10);
        rightConnect2.setTranslateY(15);
        rightConnect2.setFill(Color.SEAGREEN);

        Rectangle rotorConnect = new Rectangle(4,3);
        rotorConnect.setTranslateX(3);
        rotorConnect.setTranslateY(-45);
        rotorConnect.setFill(Color.SEAGREEN);

        Rectangle rearRotor = new Rectangle(3,22);
        rearRotor.setTranslateX(8);
        rearRotor.setTranslateY(-55);
        rearRotor.setFill(Color.SEAGREEN);

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
    HeloBlade blade;
    HeloBody body;
    public Helicopter() {
      body = new HeloBody();
      blade = new HeloBlade();
      add(body);
      add(blade);


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

    public boolean isHeliOnHP(){
        return (myTranslation.getX() >= 350) && (myTranslation.getX() <= 450) &&
                (myTranslation.getY() >= 10) && (myTranslation.getY() <= 110);
    }

    @Override
    public void update() {
        blade.update();

        if(blade.off && ignition && isHeliOnHP()){
            blade.starting = true;
            blade.off = false;
            blade.ready = false;
        }
        else if(blade.starting){
            if(blade.rotationalSpeed < blade.maxSpeed){
                blade.rotationalSpeed+=.5;
            }
            if(blade.rotationalSpeed >= blade.maxSpeed){
                blade.starting = false;
                blade.ready = true;
            }
        }
        else if(blade.ready){
            if(myRotation.getAngle() !=  0) {
                myTranslation.setX(myTranslation.getX() + vx);
                myTranslation.setY(myTranslation.getY() + vy);
            }
            else {
                myTranslation.setY(myTranslation.getY() + velocity);
            }
            pivot();
        }
        if(!ignition && isHeliOnHP()) {
            if (blade.rotationalSpeed > 0)
                blade.rotationalSpeed--;
            if (blade.rotationalSpeed < 0)
                blade.rotationalSpeed = 0;
            blade.ready = false;
            blade.off = true;
        }
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
    boolean resetPressed = false;
    boolean visible = false;

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
    public void init(Pane root, ArrayList<Cloud> clouds,
                     ArrayList<Pond> ponds){

        Helicopter heli = (Helicopter) root.getChildren().get(4);
        heli.myTranslation.setX(400);
        heli.myTranslation.setY(50);
        heli.throttle(false);
        heli.ignition = false;
        heli.myRotation.setAngle(0);
        heli.fuel = 1000;
        heli.text.setText(String.valueOf(heli.fuel));
        heli.velocity = 0;
        heli.blade.rotationalSpeed = 0;


        Pane pondPane = (Pane) root.getChildren().get(2);
        pondPane.getChildren().removeAll(ponds);
        Pond pond0 = new Pond();
        Pond pond1 = new Pond();
        Pond pond2 = new Pond();
        pondPane.getChildren().add(pond0);
        pondPane.getChildren().add(pond1);
        pondPane.getChildren().add(pond2);
        ponds.set(0, pond0);
        ponds.set(1, pond1);
        ponds.set(2, pond2);

        Pane cloudPane = (Pane) root.getChildren().get(3);
        cloudPane.getChildren().removeAll(clouds);
        Cloud cloud0 = new Cloud();
        Cloud cloud1 = new Cloud();
        Cloud cloud2 = new Cloud();
        cloudPane.getChildren().add(cloud0);
        cloudPane.getChildren().add(cloud1);
        cloudPane.getChildren().add(cloud2);
        clouds.set(0, cloud0);
        clouds.set(1, cloud1);
        clouds.set(2, cloud2);

        root.getChildren().set(4, heli);
    }

    public void ignitCheck(Helicopter heli){
        if(heli.ignition && heli.isHeliOnHP())
            heli.ignition = false;
        else
            heli.ignition = true;
        System.out.println(heli.ignition);
    }

    public void invisiLine(DistanceLines dl0, DistanceLines dl1,
                           DistanceLines dl2){
        if(!visible){
            visible = true;
            dl0.line0.setStroke(Color.AQUAMARINE);
            dl0.line1.setStroke(Color.AQUAMARINE);
            dl0.line2.setStroke(Color.AQUAMARINE);
            dl1.line0.setStroke(Color.AQUAMARINE);
            dl1.line1.setStroke(Color.AQUAMARINE);
            dl1.line2.setStroke(Color.AQUAMARINE);
            dl2.line0.setStroke(Color.AQUAMARINE);
            dl2.line1.setStroke(Color.AQUAMARINE);
            dl2.line2.setStroke(Color.AQUAMARINE);
        }
        else if(visible) {
            visible = false;
            dl0.line0.setStroke(Color.TRANSPARENT);
            dl0.line1.setStroke(Color.TRANSPARENT);
            dl0.line2.setStroke(Color.TRANSPARENT);
            dl1.line0.setStroke(Color.TRANSPARENT);
            dl1.line1.setStroke(Color.TRANSPARENT);
            dl1.line2.setStroke(Color.TRANSPARENT);
            dl2.line0.setStroke(Color.TRANSPARENT);
            dl2.line1.setStroke(Color.TRANSPARENT);
            dl2.line2.setStroke(Color.TRANSPARENT);
        }
    }
    public Game(Pane root, ArrayList<Cloud> Clouds, ArrayList<Pond> Ponds,
                Set<KeyCode> keysDown) {

        Helicopter heli = (Helicopter) root.getChildren().get(4);
        Helipad hp = (Helipad) root.getChildren().get(1);

        Pane linePane = (Pane) root.getChildren().get(5);
        DistanceLines dl0 = new DistanceLines(linePane);
        DistanceLines dl1 = new DistanceLines(linePane);
        DistanceLines dl2 = new DistanceLines(linePane);
        AnimationTimer loop = new AnimationTimer() {
            @Override
            public void handle(long nano) {

                Pond pond0 = Ponds.get(0);
                Pond pond1 = Ponds.get(1);
                Pond pond2 = Ponds.get(2);

                Cloud cloud0 = Clouds.get(0);
                Cloud cloud1 = Clouds.get(1);
                Cloud cloud2 = Clouds.get(2);
                cloud0.update(); cloud1.update(); cloud2.update();

                heli.update();
                dl0.update(cloud0, Ponds);
                dl1.update(cloud1, Ponds);
                dl2.update(cloud2, Ponds);

                heli.rotate(heli.getMyRotation());
                if (keysDown.contains(KeyCode.UP)){
                    heli.throttle(true);
                }
                if (keysDown.contains(KeyCode.DOWN)){
                    heli.throttle(false);
                }
                if (keysDown.contains(KeyCode.LEFT)) {
                    heli.rotate(heli.getMyRotation() + 15);
                    heli.moveLeft();
                }
                if(keysDown.contains(KeyCode.RIGHT)) {
                    heli.rotate(heli.getMyRotation() - 15);
                    heli.moveRight();
                }
                if(keysDown.contains(KeyCode.I)){
                    if ((frameCount % 8 == 0)){
                        ignitCheck(heli);
                    }
                }
                if(keysDown.contains(KeyCode.SPACE)){
                   if(isHeliCloudCollision(heli, cloud0)){
                       cloud0.seeding();
                   }
                    if(isHeliCloudCollision(heli, cloud1)){
                        cloud1.seeding();
                    }
                    if(isHeliCloudCollision(heli, cloud2)){
                        cloud2.seeding();
                    }
                }
                if(keysDown.contains(KeyCode.R)){
                    resetPressed = true;
                }
                if(keysDown.contains(KeyCode.D)){
                    if(frameCount % 8 == 0)
                        invisiLine(dl0, dl1, dl2);
                }
                if ((frameCount % 8 == 0) && (resetPressed)) {
                    init(root, Clouds, Ponds);
                    resetPressed = false;
                }

                if (frameCount % 60 == 0){
                    pond0.beingSeeded(cloud0.seed);
                    pond1.beingSeeded(cloud1.seed);
                    pond2.beingSeeded(cloud2.seed);
                    cloud0.deSeed();
                    cloud1.deSeed();
                    cloud2.deSeed();
                }

                //System.out.println(keysDown); // FOR TEST PURPOSES

                if (old < 0) old = nano;
                double delta = (nano - old) / conv_to_sec;

                old = nano;
                elapsedTime += delta;
                double fps = (1 / delta);

                if (frameCount % frameCount_avg == 0) {
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
    private static final int GAME_WIDTH = 800;

    private static final int GAME_HEIGHT = 800;//TODO set back to 800

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        root.setScaleY(-1);
        Helicopter heli = new Helicopter();
        Helipad hp = new Helipad();

        Pond pond0 = new Pond();
        Pond pond1 = new Pond();
        Pond pond2 = new Pond();
        ArrayList<Pond> pondList = new ArrayList<Pond>();
        pondList.add(pond0);
        pondList.add(pond1);
        pondList.add(pond2);
        Pane pondPane = new Pane();
        pondPane.getChildren().addAll(pondList);

        Cloud cloud0 = new Cloud();
        Cloud cloud1 = new Cloud();
        Cloud cloud2 = new Cloud();
        ArrayList<Cloud> cloudList = new ArrayList<Cloud>();
        cloudList.add(cloud0);
        cloudList.add(cloud1);
        cloudList.add(cloud2);
        Pane cloudPane = new Pane();
        cloudPane.getChildren().addAll(cloudList);

        Pane linePane = new Pane();

        Background bg = new Background(root);


        root.getChildren().addAll(hp, pondPane, cloudPane, heli,linePane);
        heli.translate(400, 50);
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



        Game game = new Game(root, cloudList, pondList, /*dl,*/ keysdown);


    }

    public static void main(String[] args) {
        Application.launch();
    }
}