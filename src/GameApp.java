import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
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

import java.io.File;
import java.util.*;
/*Used for the gameObjects with an updated position throughout the game
using the animation timer to continuously update gameObjects position*/
interface Updatable {
    void update();
}
/*GameObjects use the methods in this class to control the transformations as
 well as adding multiple shapes to one gameObject so that it is looked at
 as one object*/
abstract class GameObject extends Group implements Updatable{
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

class Sound{
    MediaPlayer mp;
    public Sound(){
        String snd = "heli-startUp.mp3";
        Media heliStart = new Media(new File(snd).toURI().toString());
        mp = new MediaPlayer(heliStart);
    }
    public void audio(){
        mp.play();
    }
}
/*sets the background to an imported jpg that is fitted to the dimensions of
 the game and added to the root pane*/
class Background{
    int GAME_WIDTH = 800;
    int GAME_HEIGHT = 700;//TODO set back to 800
    public Background(Pane root){
        Image image = new Image("csc133MAP.jpg");
        ImageView view = new ImageView(image);
        view.setScaleY(-1);
        view.setFitHeight(GAME_HEIGHT);
        view.setFitWidth(GAME_WIDTH);
        root.getChildren().add(view);
    }
}
/*gametext is used to write all the texts on gameobjects which include the
 percents on the clouds, ponds, and fuel on the helicopter. It also makes
 sure to lock the text on the updatable gameobjects so the text moves with
 the object*/
class GameText extends GameObject{
    Text text;
    public GameText(String textString){
        text = new Text(textString);
        text.setScaleY(-1);
        text.setFont(Font.font(16));
        text.setFill(Color.FIREBRICK);
        add(text);
    }
    public void setText(String textString){
        text.setText(textString);
    }
    public void setTextLoc(GameObject object){
        this.myTranslation = object.myTranslation;
        this.myRotation = object.myRotation;
    }
}
/*Makes the ponds for the game and sets them in random locations and also
 includes the method of when the pond is being seeded and how its
 behavior/transforms should act*/
class Pond extends GameObject {
    Random rand = new Random();
    Circle pond;
    GameText text;
    int size = 30;
    int x_bound = 650;
    int y_bound = 350;
    int offset = 200;
    int maxSize = 99;
    int min = 1;
    double scaleSize = 0.05;
    public Pond(){
        pond = new Circle(size);
        pond.setFill(Color.LIGHTSKYBLUE);
        add(pond);
        translate(rand.nextInt(rand.nextInt(x_bound) + offset),
                (rand.nextInt(y_bound) + offset));

        text = new GameText(size + " %");
        text.translate(-3, 5);
        add(text);
    }

    public void beingSeeded(int n){
        if(n > min){
            if(size <= maxSize){
                size++;
                this.scale(myScale.getX() + scaleSize,
                        myScale.getY() + scaleSize);
                text.setText(size + " %");
            }
        }
    }
}
/*Makes the clouds of the game, sets them in a random location, gives them a
 game text to keep track of seeding. Also includes the methods of returning
 the radius of the cloud,cloud seeding, how it should deseed over time, how
 it updates based on wind speed as well as a special update case to make
 sure 3-5 clouds appear in the game*/
class Cloud extends GameObject{
    Random rand = new Random();
    Circle cloud;
    int seed = 0;
    int cloudRadius = 40;
    double cloudOpacity = 0.75;
    int x_bound = 550;
    int y_bound = 450;
    int offset = 200;
    int gameBounds = 830;
    int special_bound = 200;
    int special_offset = 400;
    int maxSeed = 100;
    double WIND_SPEED = (rand.nextDouble(.75)+.01);
    GameText text;
    public Cloud(){
        cloud = new Circle(cloudRadius);
        cloud.setFill(Color.WHITE);
        cloud.setOpacity(cloudOpacity);
        add(cloud);
        translate(rand.nextInt(rand.nextInt(x_bound) + offset),
                (rand.nextInt(y_bound) + offset));

        text = new GameText(seed + " %");
        add(text);
    }
    public int getRadius() { return cloudRadius;}
    public int seeding(){
        if(seed <= maxSeed){ seed++; }
        else if(seed>0){ seed--; }
        text.setText(seed + "%");
        return seed;
    }

    public void deSeed(){
        if(seed>0){ seed--; }
        text.setText(seed + "%");
    }

    public void update(){
        myTranslation.setX(myTranslation.getX() + WIND_SPEED);
        if(myTranslation.getX() > gameBounds){
            myTranslation.setX(0);
            myTranslation.setY(rand.nextInt(y_bound)+offset);
            myTranslation.setX(myTranslation.getX() + WIND_SPEED);
        }
    }

    public void specialUpdate(){
        myTranslation.setX(myTranslation.getX() + WIND_SPEED);
        if(myTranslation.getX() > gameBounds){
            myTranslation.setX(-1*(rand.nextInt(special_bound)+special_offset));
            myTranslation.setY(rand.nextInt(y_bound)+offset);
            myTranslation.setX(myTranslation.getX() + WIND_SPEED);
        }
    }
}
/*makes the modal dialogue window that displays a game won/over prompt,
 score, and offers the user to play or end the game. one method in this class
 set a Game object so that we can use the init(restart) method if the user
 chooses to play again  and the other one is used in the Game class to test
 the win/lose condition and behave appropriately*/
class WinOrLose{
    Game rainMaker;
    int score;
    int pondSum;
    double pondAvg;
    int winningSize = 240;
    int maxSize = 300;

    public void setGame(Game game){
        rainMaker = game;
    }
    public void endGame(Pane root, ArrayList<Pond> ponds,
                        ArrayList<Cloud> clouds, Helicopter heli,
                        Set<KeyCode> keysDown){
        Alert a = new Alert(Alert.AlertType.INFORMATION," ",
                ButtonType.YES, ButtonType.NO);
        pondSum = ponds.get(0).size + ponds.get(1).size + ponds.get(2).size;
        pondAvg = (double) pondSum/maxSize;
        score = (int) (pondAvg * heli.fuel);
        if((pondSum >= winningSize) && (heli.ignition == false)){
            rainMaker.hasExecuted = true;
            keysDown.clear();
            a.setContentText("CONGRATS,YOU WON | SCORE: "+ score +" | PLAY " +
                    "AGAIN?");
            a.show();
            a.setOnHidden(event -> {
                if(a.getResult() == ButtonType.NO){
                a.close();
                System.exit(0);

                }
                else if(a.getResult() == ButtonType.YES){
                    rainMaker.hasExecuted = false;
                    rainMaker.init(root, clouds, ponds);
                }
            });
        }
        else if(heli.fuel <= 0){
            rainMaker.hasExecuted = true;
            keysDown.clear();
            a.setContentText("GAME OVER, PLAY AGAIN?");
            a.show();
            a.setOnHidden(event -> {
                if(a.getResult() == ButtonType.NO){
                    a.close();
                    System.exit(0);
                }
                else if(a.getResult() == ButtonType.YES){
                    rainMaker.hasExecuted = false;
                    rainMaker.init(root, clouds, ponds);
                }
            });
        }
    }
}

/*Creates 3 lines that have starting positions linked to one cloud and ending
 positions linked to each of the ponds. We also create booleans to test if
 the pond is in range (4*radius) of the cloud to help with the seeding.The
 update method helps lock the lines on the moving gameObjects and change
 color based on if they should be visible or not*/
class DistanceLines{
    Line line0 = new Line();
    Line line1 = new Line();
    Line line2 = new Line();
    double strokeWidth = 3.0;
    int lineCount = 3;
    ArrayList<Line> lineList;

    public boolean isPondInRange( Line line, Cloud cloud){
        double xs = Math.pow((line.getEndX() - line.getStartX()), 2);
        double ys = Math.pow((line.getEndY() - line.getStartY()), 2);
        double ans = Math.sqrt(xs + ys);
        return (ans <= (cloud.getRadius() * 4));
    }

    public DistanceLines(Pane linePane){
        line0.setStroke(Color.TRANSPARENT);
        line0.setStrokeWidth(strokeWidth);
        line1.setStroke(Color.TRANSPARENT);
        line1.setStrokeWidth(strokeWidth);
        line2.setStroke(Color.TRANSPARENT);
        line2.setStrokeWidth(strokeWidth);

        lineList = new ArrayList<>();
        lineList.add(line0);lineList.add(line1);lineList.add(line2);

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

        for(int i=0; i<lineCount; i++){
                lineList.get(i).setVisible(true);
                if(isPondInRange(lineList.get(i), cloud))
                    lineList.get(i).setStroke(Color.AQUAMARINE);
                else
                    lineList.get(i).setStroke(Color.TRANSPARENT);
        }
    }
}
/*Rectangle and Circle combined to make the helipad and positioned in the
 middle of the game*/
class Helipad extends GameObject {
    int rect_width = 100;
    int rect_height = 100;
    int rect_xPos = 350;
    int rect_yPos = 10;
    int circ_xPos = 400;
    int circ_yPos = 60;
    int rad = 40;
    public Helipad(){
        Rectangle rect = new Rectangle(rect_width, rect_height);
        rect.setStroke(Color.LIMEGREEN);
        rect.setTranslateX(rect_xPos);
        rect.setTranslateY(rect_yPos);
        add(rect);

        Circle circ = new Circle(rad);
        circ.setStroke(Color.LIMEGREEN);
        circ.setTranslateX(circ_xPos);
        circ.setTranslateY(circ_yPos);
        add(circ);
    }
}

/*Specifically made for the rotors of the helicopter in making the shapes of
 them and an update method that controls the spinning motion of the rotors
 based on a rotational speed*/
class HeloBlade extends GameObject{
    Rectangle rotor;
    double rotationalSpeed = 0;
    double maxSpeed = 15;
    boolean off = true;
    boolean starting;
    boolean ready;
    int rotor_yPos = -40;
    int rotor_xPos = -2;
    public HeloBlade(){
        rotor = new Rectangle(5, 80);
        rotor.setFill(Color.HOTPINK);
        rotor.setTranslateY(rotor_yPos);
        rotor.setTranslateX(rotor_xPos);
        add(rotor);
    }
    public void update(){
       rotate(getMyRotation()+rotationalSpeed);
    }
}
/*Creates the body of the helicopter by combining/layering many shapes onto
 each other*/
class HeloBody extends Group{
    public HeloBody(){
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
/*Combines the HeloBody and HeloBlade to bring the Helicopter together and
 makes it one GameObject. The speed/velocity of the helicopter is handled
 in this class which affects the forward/back movement. The left/right
 movements is a property of the heli so its also in this class but is based
 on user key inputs in the Game class The fuel of the helicopter is set to
 max. A boolean is made to check the collision/criteria of if the
 helicopter is on the helipad.The final part of the class is the states of
 the rotors and how they should behave based on if the state is off,
 starting, running, and off*/
class Helicopter extends GameObject{
    double velocity = 0;
    double vy;
    double vx;
    boolean ignition;
    GameText text;
    int fuel = 25000;
    int maxVel = 10;
    int minVel = -2;
    double velPosChange = .1;
    double velNegChange = -.2;
    double bladeChange = .5;
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
                if(velocity < maxVel)
                    velocity += velPosChange;
            }
            else{
                if(velocity > minVel)
                    velocity += velNegChange;
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
                blade.rotationalSpeed += bladeChange;
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
//This class handles a lot of the work of the game by dealing with behaviors
// and rules including keeping the animation of the game using the
// animationTimer, key inputs from the user, cloud and heli collisions,
// making sure ponds dont spawn on each other, reinitialization of the game,
// checking the ignition behavior of the heli, and updating of the
// GameObjects including seeding behaviors
class Game extends Pane{
    double old = -1;
    double elapsedTime = 0;
    double conv_to_sec = 1e9;
    int frameCount_avg = 30;
    int frameCount = 0;
    boolean isResetPressed = false;
    boolean isVisible = false;
    boolean hasExecuted = false;

    public boolean isHeliCloudCollision(Helicopter heli,Cloud cloud){

        return  heli.myTranslation.getX()
                > (cloud.myTranslation.getX() - cloud.getRadius())
                && heli.myTranslation.getX() <
                (cloud.myTranslation.getX() + cloud.getRadius())
                && heli.myTranslation.getY() <
                (cloud.myTranslation.getY() + cloud.getRadius())
                && heli.myTranslation.getY() >
                (cloud.myTranslation.getY() - cloud.getRadius());
    }
    public double findDistance(double X1, double Y1, double X2, double Y2){
        double XS = Math.pow((X2-X1), 2);
        double YS = Math.pow((Y2-Y1), 2);
        double ans = Math.sqrt(XS + YS);
        return ans;
    }

    public void isPondCollision(Pond pond0, Pond pond1, Pond pond2){
        Random rand = new Random();
        double rad = pond0.pond.getRadius();
        double diam = rad*2;
        double X0 = pond0.myTranslation.getX();
        double Y0 = pond0.myTranslation.getY();
        double X1 = pond1.myTranslation.getX();
        double Y1 = pond1.myTranslation.getY();
        double X2 = pond2.myTranslation.getX();
        double Y2 = pond2.myTranslation.getY();

        double dist0_1 = findDistance(X0, Y0, X1, Y1);
        double dist0_2 = findDistance(X0, Y0, X2, Y2);
        double dist1_2 = findDistance(X1, Y1, X2, Y2);
        if((dist0_1 <= diam) || (dist0_2 <= diam)){
            pond0.setTranslateX(rand.nextInt(500) + 100);
            pond0.setTranslateY(rand.nextInt(350) + 200);
        }
        else if(dist1_2 <= diam){
            pond1.setTranslateX(rand.nextInt(500) + 100);
            pond1.setTranslateY(rand.nextInt(350) + 200);
        }

    }

    public void init(Pane root, ArrayList<Cloud> clouds,
                     ArrayList<Pond> ponds){

        Helicopter heli = (Helicopter) root.getChildren().get(4);
        heli.myTranslation.setX(400);
        heli.myTranslation.setY(50);
        heli.throttle(false);
        heli.ignition = false;
        heli.myRotation.setAngle(0);
        heli.fuel = 25000;
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
        isPondCollision(pond0, pond1, pond2);

        Pane cloudPane = (Pane) root.getChildren().get(3);
        cloudPane.getChildren().removeAll(clouds);
        Cloud cloud0 = new Cloud();
        Cloud cloud1 = new Cloud();
        Cloud cloud2 = new Cloud();
        Cloud cloud3 = new Cloud();
        Cloud cloud4 = new Cloud();
        cloudPane.getChildren().add(cloud0);
        cloudPane.getChildren().add(cloud1);
        cloudPane.getChildren().add(cloud2);
        cloudPane.getChildren().add(cloud3);
        cloudPane.getChildren().add(cloud4);
        clouds.set(0, cloud0);
        clouds.set(1, cloud1);
        clouds.set(2, cloud2);
        clouds.set(3, cloud3);
        clouds.set(4, cloud4);

        root.getChildren().set(4, heli);
    }
    public void ignitCheck(Helicopter heli){
        if(heli.ignition && heli.isHeliOnHP())
            heli.ignition = false;
        else
            heli.ignition = true;
    }
    public void invisiLine(ArrayList<Line> lineList0,ArrayList<Line> lineList1,
                           ArrayList<Line> lineList2, ArrayList<Line> lineList3,
                           ArrayList<Line> lineList4){
        if(!isVisible){
            isVisible = true;
        }
        else if(isVisible) {
            isVisible = false;
            for(int i=0; i<3; i++) {
                lineList0.get(i).setVisible(false);
                lineList1.get(i).setVisible(false);
                lineList2.get(i).setVisible(false);
                lineList3.get(i).setVisible(false);
                lineList4.get(i).setVisible(false);
            }
        }
    }

    public Game(Pane root, ArrayList<Cloud> clouds, ArrayList<Pond> ponds,
                Set<KeyCode> keysDown, WinOrLose wl) {
        wl.setGame(this);
        Helicopter heli = (Helicopter) root.getChildren().get(4);
        Helipad hp = (Helipad) root.getChildren().get(1);
        Pane linePane = (Pane) root.getChildren().get(5);
        DistanceLines dl0 = new DistanceLines(linePane);
        DistanceLines dl1 = new DistanceLines(linePane);
        DistanceLines dl2 = new DistanceLines(linePane);
        DistanceLines dl3 = new DistanceLines(linePane);
        DistanceLines dl4 = new DistanceLines(linePane);

        Sound sound = new Sound();
        AnimationTimer loop = new AnimationTimer() {
            @Override
            public void handle(long nano) {

                Pond pond0 = ponds.get(0);
                Pond pond1 = ponds.get(1);
                Pond pond2 = ponds.get(2);

                Cloud cloud0 = clouds.get(0);
                Cloud cloud1 = clouds.get(1);
                Cloud cloud2 = clouds.get(2);
                Cloud cloud3 = clouds.get(3);
                Cloud cloud4 = clouds.get(4);
                cloud0.update(); cloud1.update(); cloud2.update();
                cloud3.specialUpdate(); cloud4.specialUpdate();

                heli.update();
                heli.rotate(heli.getMyRotation());
                if (keysDown.contains(KeyCode.UP)){
                    if(frameCount % 2 == 0){
                        heli.throttle(true);
                        heli.fuel -= 100;
                    }
                }
                if (keysDown.contains(KeyCode.DOWN)){
                    if(frameCount % 2 == 0)
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
                        sound.audio();
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
                    if(isHeliCloudCollision(heli, cloud3)){
                        cloud3.seeding();
                    }
                    if(isHeliCloudCollision(heli, cloud4)){
                        cloud4.seeding();
                    }
                }
                if(keysDown.contains(KeyCode.R)){
                    isResetPressed = true;
                }
                if(keysDown.contains(KeyCode.D)){
                    if(frameCount % 6 == 0)
                        invisiLine(dl0.lineList, dl1.lineList, dl2.lineList,
                                dl3.lineList, dl4.lineList);
                }
                if(isVisible){
                    dl0.update(cloud0, ponds);
                    dl1.update(cloud1, ponds);
                    dl2.update(cloud2, ponds);
                    dl3.update(cloud3, ponds);
                    dl4.update(cloud4, ponds);
                }

                if ((frameCount % 8 == 0) && (isResetPressed)) {
                    init(root, clouds, ponds);
                    isResetPressed = false;
                }

                if (frameCount % 60 == 0){
                    if(dl0.line0.getStroke() == Color.AQUAMARINE){
                        pond0.beingSeeded(cloud0.seed);
                    }
                    if(dl0.line1.getStroke() == Color.AQUAMARINE){
                        pond1.beingSeeded(cloud0.seed);
                    }
                    if(dl0.line2.getStroke() == Color.AQUAMARINE){
                        pond2.beingSeeded(cloud0.seed);
                    }
                    if(dl1.line0.getStroke() == Color.AQUAMARINE){
                        pond0.beingSeeded(cloud1.seed);
                    }
                    if(dl1.line1.getStroke() == Color.AQUAMARINE){
                        pond1.beingSeeded(cloud1.seed);
                    }
                    if(dl1.line2.getStroke() == Color.AQUAMARINE){
                        pond2.beingSeeded(cloud1.seed);
                    }
                    if(dl2.line0.getStroke() == Color.AQUAMARINE){
                        pond0.beingSeeded(cloud2.seed);
                    }
                    if(dl2.line1.getStroke() == Color.AQUAMARINE){
                        pond1.beingSeeded(cloud2.seed);
                    }
                    if(dl2.line2.getStroke() == Color.AQUAMARINE){
                        pond2.beingSeeded(cloud2.seed);
                    }
                    if(dl2.line0.getStroke() == Color.AQUAMARINE){
                        pond0.beingSeeded(cloud2.seed);
                    }
                    if(dl2.line1.getStroke() == Color.AQUAMARINE){
                        pond1.beingSeeded(cloud2.seed);
                    }
                    if(dl2.line2.getStroke() == Color.AQUAMARINE){
                        pond2.beingSeeded(cloud2.seed);
                    }
                    if(dl2.line0.getStroke() == Color.AQUAMARINE){
                        pond0.beingSeeded(cloud2.seed);
                    }
                    if(dl2.line1.getStroke() == Color.AQUAMARINE){
                        pond1.beingSeeded(cloud2.seed);
                    }
                    if(dl2.line2.getStroke() == Color.AQUAMARINE){
                        pond2.beingSeeded(cloud2.seed);
                    }
                    if(dl3.line0.getStroke() == Color.AQUAMARINE){
                        pond0.beingSeeded(cloud3.seed);
                    }
                    if(dl3.line1.getStroke() == Color.AQUAMARINE){
                        pond1.beingSeeded(cloud3.seed);
                    }
                    if(dl3.line2.getStroke() == Color.AQUAMARINE){
                        pond2.beingSeeded(cloud3.seed);
                    }
                    if(dl4.line0.getStroke() == Color.AQUAMARINE){
                        pond0.beingSeeded(cloud4.seed);
                    }
                    if(dl4.line1.getStroke() == Color.AQUAMARINE){
                        pond1.beingSeeded(cloud4.seed);
                    }
                    if(dl4.line2.getStroke() == Color.AQUAMARINE){
                        pond2.beingSeeded(cloud4.seed);
                    }
                    cloud0.deSeed();
                    cloud1.deSeed();
                    cloud2.deSeed();
                    cloud3.deSeed();
                    cloud4.deSeed();
                }
                if(!hasExecuted){
                    wl.endGame(root, ponds, clouds, heli, keysDown);
                }

                if (old < 0) old = nano;
                double delta = (nano - old) / conv_to_sec;

                old = nano;
                elapsedTime += delta;
                if (frameCount % frameCount_avg == 0) {
                    if (heli.ignition) {
                        heli.fuel -= 10;
                        heli.text.setText(String.valueOf(heli.fuel));
                    }
                }
                frameCount++;
            }
        };
        loop.start();
    }
}
//The GameApp class essentially brings mostly all  the objects together and
// and adds them to the base root/scene of the game and deals with some
// inheritance between classes
public class GameApp extends Application {
    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 700;//TODO set back to 800
    Game game;
    @Override
    public void start(Stage primaryStage) {
        WinOrLose wl = new WinOrLose();

        Pane root = new Pane();
        root.setScaleY(-1);
        Helicopter heli = new Helicopter();
        Helipad hp = new Helipad();

        Pond pond0 = new Pond();
        Pond pond1 = new Pond();
        Pond pond2 = new Pond();
        ArrayList<Pond> pondList = new ArrayList<>();
        pondList.add(pond0);
        pondList.add(pond1);
        pondList.add(pond2);
        Pane pondPane = new Pane();
        pondPane.getChildren().addAll(pondList);

        Cloud cloud0 = new Cloud();
        Cloud cloud1 = new Cloud();
        Cloud cloud2 = new Cloud();
        Cloud cloud3 = new Cloud();
        Cloud cloud4 = new Cloud();
        ArrayList<Cloud> cloudList = new ArrayList<>();
        cloudList.add(cloud0);
        cloudList.add(cloud1);
        cloudList.add(cloud2);
        cloudList.add(cloud3);
        cloudList.add(cloud4);
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
        game = new Game(root, cloudList, pondList, keysdown, wl);
        game.isPondCollision(pond0, pond1, pond2);
    }
    public static void main(String[] args) {
        Application.launch();
    }
}