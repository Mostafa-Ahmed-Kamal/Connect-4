package sample;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class Main extends Application {

    // AI Algorithm INPUT
    private int k=5;
    private Disc[][] grid = new Disc[COLUMNS][ROWS];

    private boolean computerTurn=false;
    private Stage primaryStage;
    private Scene gameScene;
    private Scene treeScene;
    private static final int TILE_SIZE = 80;
    private static final int COLUMNS = 7;
    private int remaining =COLUMNS*ROWS;
    private static final int ROWS = 6;
    private boolean redMove = true;
    private boolean playerColorRed = true;
    private Pane discRoot = new Pane();
    private Group GRoot;
    private Pane root;
    private Button start;
    private Button end;
    private Button showTree;
    private SwitchButton toggle;
    private SwitchButton playerColorSwitch;
    private TextField kLevels;
    private List<Rectangle> columns=new ArrayList<>();
    private Text computerScoreValue;
    private Text playerScoreValue;
    private Text playerWinner;
    private Text computerWinner;
    private Text tied;
    boolean clicked=false;
    boolean canPlay=true;
    int temp=0;
    private List<Circle> circles = new ArrayList<>();
    private int currentOn=0;
    private int redScore=0;
    private int yellowScore=0;
    boolean gameStarted=false;
    boolean alphaBetaPruning=false;
    private boolean gameOvered=false;
    private Ai ai=new Ai();

    private void printGrid(Disc[][] grid){
        for (int x=0;x<grid[0].length;x++){
            for (Disc[] discs : grid) {
                if (discs[x] == null) {
                    System.out.print("WHITE  " + "    ");
                } else {
                    System.out.print((discs[x].getFill() == Color.RED ? "RED   " : "YELLOW") + "     ");
                }
            }
            System.out.println();
        }
    }

    private byte[][] getBoard(Disc[][] grid){
        byte[][] Board=new byte[grid[0].length][grid.length];
        for (int x=0;x<grid[0].length;x++){
            for (int y=0;y<grid.length;y++) {
                if (grid[y][x] == null) {
                    Board[x][y]=0;
                } else {
                    if (playerColorRed) {
                        if (grid[y][x].getFill() == Color.RED) {
                            Board[x][y] = 1;
                        }
                        else Board[x][y] = 2;
                    }
                    else {
                        if (grid[y][x].getFill() == Color.RED) {
                            Board[x][y] = 2;
                        }
                        else Board[x][y] = 1;
                    }
                }
            }
        }
        return Board;
    }

    private Parent createContent(Stage primaryStage) {
        this.primaryStage=primaryStage;
        GRoot=new Group();
        root = new Pane();
        Pane functions=new Pane();

        Shape shape = new Rectangle((COLUMNS + 1) * TILE_SIZE, (ROWS + 1) * TILE_SIZE);
        ((Rectangle) shape).setY((1.1) * TILE_SIZE);
        shape.setFill(Color.LIGHTSLATEGRAY);
        root.getChildren().add(shape);

        GRoot.getChildren().add(root);
        root.getChildren().add(discRoot);

        Shape gridShape = makeGrid();
        root.getChildren().add(gridShape);

        Shape GrayRect = new Rectangle((COLUMNS + 1) * TILE_SIZE, (1.1) * TILE_SIZE);
        GrayRect.setFill(Color.LIGHTSLATEGRAY);

        root.getChildren().add(GrayRect);

        Shape BlackRect = new Rectangle(3.75*TILE_SIZE, (1.1) * TILE_SIZE);
        BlackRect.setTranslateX((COLUMNS + 1) * TILE_SIZE);
        BlackRect.setTranslateY(0);
        light(BlackRect);
        BlackRect.setFill(Color.BLACK);
        root.getChildren().add(BlackRect);

        Text title = new Text();
        title.setText("CONNECT 4\n    GAME");
        title.setX((COLUMNS + 1.9) * TILE_SIZE);
        title.setY((0.5) * TILE_SIZE);
        title.setFill(Color.GOLDENROD);
        title.setFont(Font.font("Verdana",25));
        GRoot.getChildren().add(title);

        Shape scoreRect = new Rectangle(3.75*TILE_SIZE, 3.25*TILE_SIZE);
        scoreRect.setTranslateX((COLUMNS + 1) * TILE_SIZE);
        scoreRect.setTranslateY((1.1) * TILE_SIZE);
        light(scoreRect);
        scoreRect.setFill(Color.CORNFLOWERBLUE);
        root.getChildren().add(scoreRect);

        Text playerScore = new Text();
        playerScore.setText("PLAYER SCORE:");
        playerScore.setX((COLUMNS + 1) * TILE_SIZE+20);
        playerScore.setY((1.6) * TILE_SIZE);
        playerScore.setFill(Color.BLACK);
        playerScore.setFont(Font.font("Verdana",25));
        GRoot.getChildren().add(playerScore);

        playerScoreValue = new Text();
        playerScoreValue.setText(String.valueOf(playerColorRed?redScore:yellowScore));
        playerScoreValue.setX((COLUMNS + 1) * TILE_SIZE+20);
        playerScoreValue.setY((2.35) * TILE_SIZE);
        playerScoreValue.setFill(Color.BLACK);
        playerScoreValue.setFont(Font.font("Verdana",25));
        GRoot.getChildren().add(playerScoreValue);

        playerWinner = new Text();
        playerWinner.setText("WINNER");
        playerWinner.setX((COLUMNS + 2) * TILE_SIZE+20);
        playerWinner.setY((2.35) * TILE_SIZE);
        playerWinner.setFill(Color.GOLDENROD);
        playerWinner.setFont(Font.font("Verdana",25));
        playerWinner.setVisible(false);

        Text computerScore = new Text();
        computerScore.setText("COMPUTER SCORE:");
        computerScore.setX((COLUMNS + 1) * TILE_SIZE+20);
        computerScore.setY((3.1) * TILE_SIZE);
        computerScore.setFill(Color.BLACK);
        computerScore.setFont(Font.font("Verdana",25));
        GRoot.getChildren().add(computerScore);

        computerScoreValue = new Text();
        computerScoreValue.setText(String.valueOf(playerColorRed?yellowScore:redScore));
        computerScoreValue.setX((COLUMNS + 1) * TILE_SIZE+20);
        computerScoreValue.setY((3.85) * TILE_SIZE);
        computerScoreValue.setFill(Color.BLACK);
        computerScoreValue.setFont(Font.font("Verdana",25));
        GRoot.getChildren().add(computerScoreValue);

        computerWinner = new Text();
        computerWinner.setText("WINNER");
        computerWinner.setX((COLUMNS + 2) * TILE_SIZE+20);
        computerWinner.setY((3.85) * TILE_SIZE);
        computerWinner.setFill(Color.GOLDENROD);
        computerWinner.setFont(Font.font("Verdana",25));
        computerWinner.setVisible(false);

        tied = new Text();
        tied.setText("GAME ended in a draw");
        tied.setX((COLUMNS +0.85) * TILE_SIZE+20);
        tied.setY((2.6) * TILE_SIZE);
        tied.setFill(Color.GOLDENROD);
        tied.setFont(Font.font("Verdana",25));
        tied.setRotate(-40);
        tied.setVisible(false);
        Text playerColorText = new Text();
        playerColorText.setText("PLAYER DISK COLOR:");
        playerColorText.setX((COLUMNS + 1.15) * TILE_SIZE);
        playerColorText.setY((4.85) * TILE_SIZE);
        playerColorText.setFill(Color.BLACK);
        playerColorText.setFont(Font.font("Verdana",25));
        GRoot.getChildren().add(playerColorText);

        playerColorSwitch =new SwitchButton(true);
        playerColorSwitch.setTranslateX((COLUMNS + 2.2) * TILE_SIZE+20);
        playerColorSwitch.setTranslateY((5.05) * TILE_SIZE);
        GRoot.getChildren().add(playerColorSwitch);

        Text kLevelsText = new Text();
        kLevelsText.setText("K Levels num:");
        kLevelsText.setX((COLUMNS + 1.75) * TILE_SIZE);
        kLevelsText.setY((6.05) * TILE_SIZE);
        kLevelsText.setFill(Color.BLACK);
        kLevelsText.setFont(Font.font("Verdana",25));
        GRoot.getChildren().add(kLevelsText);

        kLevels = new TextField("DEFAULT=5");
        kLevels.setFont(Font.font("Verdana",15));
        kLevels.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        final BooleanProperty firstTime = new SimpleBooleanProperty(true);
        kLevels.focusedProperty().addListener((observable,  oldValue,  newValue) -> {
            if(newValue && firstTime.get()){
                GRoot.requestFocus();
                firstTime.setValue(false);
            }
        });
        kLevels.setTranslateX((COLUMNS + 2.15) * TILE_SIZE);
        kLevels.setTranslateY((6.2) * TILE_SIZE);
        kLevels.setMaxWidth(110);
        kLevels.setOnMouseClicked(e -> kLevels.setText(""));
        GRoot.getChildren().add(kLevels);

        Text warningLabel = new Text();
        warningLabel.setText("   Please enter right k \n(positive integers only)");
        warningLabel.setX((COLUMNS + 1.75) * TILE_SIZE);
        warningLabel.setY((6.8) * TILE_SIZE);
        warningLabel.setFill(Color.DARKRED);
        warningLabel.setFont(Font.font("Verdana",15));
        warningLabel.setVisible(false);
        GRoot.getChildren().add(warningLabel);

        Group tmp1=new Group();
        Group tmp2=new Group();
        Group tmp3=new Group();
        Group tmp4=new Group();
        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(-4);
        shadow.setOffsetY(6);
        shadow.setColor(Color.BLACK);
        tmp1.setEffect(shadow);
        GRoot.getChildren().add(tmp1);
        tmp2.setEffect(shadow);
        GRoot.getChildren().add(tmp2);
        tmp3.setEffect(shadow);
        GRoot.getChildren().add(tmp3);

        DropShadow shadow1 = new DropShadow();
        shadow1.setOffsetX(-4);
        shadow1.setOffsetY(6);
        shadow1.setColor(Color.RED);
        tmp4.setEffect(shadow1);
        GRoot.getChildren().add(tmp4);

        tmp4.getChildren().add(playerWinner);
        tmp4.getChildren().add(computerWinner);
        tmp4.getChildren().add(tied);

        start= new Button();
        start.setText("START");
        start.setTranslateX((COLUMNS + 1.275) * TILE_SIZE);
        start.setTranslateY((7.15) * TILE_SIZE);
        start.setFont(Font.font("Verdana",25));
        String buttonStyleOn = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 0.2, 0.0, 0.0, 2); -fx-background-color: CORNFLOWERBLUE;";
        start.setDisable(gameStarted);
        start.setStyle(buttonStyleOn);
        EventHandler<MouseEvent> buttonStartPressed = event -> {
            //start.setStyle(buttonStyleOn);
            tmp1.setEffect(null);
            event.consume();
        };
        EventHandler<MouseEvent> buttonStartReleased = event -> {
            //start.setStyle(buttonStyleOff);
            tmp1.setEffect(shadow);
            event.consume();
        };
        EventHandler<MouseEvent> buttonStartClicked = event -> {
            if (!kLevels.getText().equals("DEFAULT=5")) {
                try {
                    k = Integer.parseInt(kLevels.getText());
                    if (k > 0) {
                        warningLabel.setVisible(false);
                    } else {
                        warningLabel.setVisible(true);
                        return;
                    }
                } catch (NumberFormatException nfe) {
                    warningLabel.setVisible(true);
                    return;
                }
            }
            else {
                k=5;
            }
            gameStarted=true;
            columns=makeColumns();
            root.getChildren().addAll(columns);
            start.setDisable(gameStarted);
            end.setDisable(!gameStarted);
            kLevels.setDisable(gameStarted);
            playerColorSwitch.setDisable(gameStarted);
            redMove=!playerColorSwitch.state;
            playerColorRed=!playerColorSwitch.state;
            alphaBetaPruning= toggle.state;
        };
        start.setOnMousePressed(buttonStartPressed);
        start.setOnMouseReleased(buttonStartReleased);
        start.setOnMouseClicked(buttonStartClicked);
        tmp1.getChildren().add(start);


        end= new Button();
        end.setText("RESET");
        end.setTranslateX((COLUMNS + 3.075) * TILE_SIZE);
        end.setTranslateY((7.15) * TILE_SIZE);
        end.setFont(Font.font("Verdana",25));
        end.setDisable(!gameStarted);
        end.setStyle(buttonStyleOn);
        EventHandler<MouseEvent> buttonEndPressed = event -> {
            //end.setStyle(buttonStyleOn);
            tmp2.setEffect(null);
            event.consume();
        };
        EventHandler<MouseEvent> buttonEndReleased = event -> {
            //end.setStyle(buttonStyleOff);
            tmp2.setEffect(shadow);
            event.consume();
        };
        EventHandler<MouseEvent> buttonEndClicked = event -> {
            if (!computerTurn&&!clicked&&canPlay) {
                try {
                    k = 5;
                    remaining = COLUMNS * ROWS;
                    redMove = true;
                    playerColorRed = true;
                    grid = new Disc[COLUMNS][ROWS];
                    discRoot = new Pane();
                    columns = new ArrayList<>();
                    clicked = false;
                    canPlay=true;
                    circles = new ArrayList<>();
                    currentOn = 0;
                    redScore = 0;
                    yellowScore = 0;
                    temp=0;
                    gameStarted = false;
                    alphaBetaPruning = false;
                    gameOvered = false;
                    computerTurn = false;
                    start(primaryStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        end.setOnMousePressed(buttonEndPressed);
        end.setOnMouseReleased(buttonEndReleased);
        end.setOnMouseClicked(buttonEndClicked);
        tmp2.getChildren().add(end);

        showTree= new Button();
        showTree.setText("Show Tree");
        showTree.setTranslateX((COLUMNS + 1.9) * TILE_SIZE);
        showTree.setTranslateY((8) * TILE_SIZE);
        showTree.setFont(Font.font("Verdana",20));
        String buttonTreeStyleOn = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 0.2, 0.0, 0.0, 2); -fx-background-color: DARKGRAY;";
        showTree.setStyle(buttonTreeStyleOn);
        EventHandler<MouseEvent> buttonTreePressed = event -> {
            //showTree.setStyle(buttonTreeStyleOn);
            tmp3.setEffect(null);
            event.consume();
        };
        EventHandler<MouseEvent> buttonTreeReleased = event -> {
            //showTree.setStyle(buttonTreeStyleOff);
            tmp3.setEffect(shadow);
            event.consume();
        };
        EventHandler<MouseEvent> buttonTreeClicked = event -> {
            // TODO showing tree
            if (!computerTurn&&!clicked&&canPlay) {
                //primaryStage.hide();
                //private Parent createContent(Stage primaryStage) {}
                //send the current scene to the method of the tree which return parent to be able to return to the gameScene
                //instead of the "createContent(primaryStage)" in the coming line
                treeScene=new Scene(createContent(primaryStage));
                primaryStage.setScene(treeScene);
                primaryStage.show();
            }
        };
        showTree.setOnMousePressed(buttonTreePressed);
        showTree.setOnMouseReleased(buttonTreeReleased);
        showTree.setOnMouseClicked(buttonTreeClicked);
        tmp3.getChildren().add(showTree);

        for (int x = 0; x < COLUMNS; x++) {
            Circle circle = new Circle(TILE_SIZE >> 1);
            circle.setCenterX(TILE_SIZE >> 1);
            circle.setCenterY((6*TILE_SIZE) / 10);
            circle.setTranslateX(x * (TILE_SIZE + 5) + (TILE_SIZE >> 2));
            //light(circle);
            circle.setFill(Color.LIGHTSLATEGRAY);
            circles.add(circle);
        }

        root.getChildren().addAll(circles);
        columns=makeColumns();
        //root.getChildren().addAll(columns);

        toggle =new SwitchButton(false);
        toggle.setTranslateX(170);
        toggle.setTranslateY(650);
        GRoot.getChildren().add(toggle);

        Text pruning = new Text();
        pruning.setText("Alpha–beta pruning");
        pruning.setX(240);
        pruning.setY(675);
        pruning.setFill(Color.BLACK);
        pruning.setFont(Font.font("Verdana",25));
        GRoot.getChildren().add(pruning);

        return GRoot;
    }

    private Shape makeGrid() {
        Shape shape = new Rectangle((COLUMNS + 1) * TILE_SIZE, (ROWS + 1) * TILE_SIZE);
        ((Rectangle) shape).setY((1.1) * TILE_SIZE);
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                Circle circle = new Circle(TILE_SIZE >> 1);
                circle.setCenterX(TILE_SIZE >> 1);
                circle.setCenterY((TILE_SIZE >> 1) +(1.1) * TILE_SIZE);
                circle.setTranslateX(x * (TILE_SIZE + 5) + (TILE_SIZE >> 2));
                circle.setTranslateY(y * (TILE_SIZE + 5) + (TILE_SIZE >> 2));

                shape = Shape.subtract(shape, circle);
            }
        }

        shape.setFill(Color.BLUE);
        light(shape);

        return shape;
    }

    private List<Rectangle> makeColumns() {
        if (gameStarted) {
            List<Rectangle> list = new ArrayList<>();

            for (int x = 0; x < COLUMNS; x++) {
                Rectangle rect = new Rectangle(TILE_SIZE, (ROWS + 2) * TILE_SIZE);
                rect.setTranslateX(x * (TILE_SIZE + 5) + (TILE_SIZE >> 2));
                rect.setFill(Color.TRANSPARENT);
                Circle circle = circles.get(x);
                int finalX = x;
                EventHandler<MouseEvent> handlerOn = event -> {
                    if (!gameOvered&&!computerTurn) {
                        IntStream.range(0, COLUMNS).forEach(y -> circles.get(y).setFill(Color.LIGHTSLATEGRAY));
                        for (int y = 0; y < COLUMNS; y++) {
                            columns.get(y).setFill(Color.rgb(0,0,0,0));
                        }
                        rect.setFill(Color.rgb(153, 153, 153, 0.3));
                        currentOn = finalX;
                        if (!clicked) {
                            for (int y = 0; y < COLUMNS; y++) {
                                circles.get(y).setFill(Color.LIGHTSLATEGRAY);
                                circles.get(y).setEffect(null);
                            }
                            if (redMove==playerColorRed) {
                                circle.setFill(redMove ? Color.RED : Color.YELLOW);
                                light(circle);
                            }
                        }
                        event.consume();
                    }
                };
                EventHandler<MouseEvent> handlerOff = event -> {
                    if (!gameOvered&&!computerTurn) {
                        rect.setFill(Color.TRANSPARENT);
                        circle.setFill(Color.LIGHTSLATEGRAY);
                        circle.setEffect(null);
                        event.consume();
                    }
                };
                rect.setOnMouseEntered(handlerOn);
                rect.setOnMouseExited(handlerOff);

                final int column = x;
                EventHandler<MouseEvent> clickedOn = event -> {
                    if (!gameOvered&&!computerTurn&&canPlay) {
                        circle.setFill(Color.LIGHTSLATEGRAY);
                        circle.setEffect(null);
                        placeDisc(new Disc(redMove), column,computerTurn==true);
                        event.consume();
                    }
                };
                rect.setOnMouseClicked(clickedOn);

                list.add(rect);
            }

            return list;
        }
        else return new ArrayList<>();
    }

    private void placeDisc(Disc disc, int column,boolean computerTurn) {
        if (clicked==computerTurn) {
            clicked=true;
            canPlay=false;
            int row = ROWS - 1;
            do {
                if (getDisc(column, row).isEmpty()) {
                    break;
                }
                row--;
            }
            while (row >= 0);

            if (row < 0) {
                for (int y = 0; y < COLUMNS; y++) {
                    circles.get(y).setFill(Color.LIGHTSLATEGRAY);
                    circles.get(y).setEffect(null);
                }
                circles.get(currentOn).setFill(redMove ? Color.RED : Color.YELLOW);
                light(circles.get(currentOn));
                //printGrid(grid);
                clicked=false;
                canPlay=true;
                return;
            }

            grid[column][row] = disc;
            discRoot.getChildren().add(disc);
            disc.setTranslateX(column * (TILE_SIZE + 5) + (TILE_SIZE >> 2));

            TranslateTransition animation = new TranslateTransition(Duration.seconds(0.5), disc);
            animation.setToY((row+1) * (TILE_SIZE + 5) + (TILE_SIZE >> 2));
            int finalRow = row;
            animation.setOnFinished(e -> finishAnimation(column, finalRow));
            animation.play();

        }
    }

    private void finishAnimation(int column, int row){
        remaining--;
        int scoreIncreament=playEnded(column,row);
        if (scoreIncreament>0) {
            playOver(scoreIncreament);
        }
        if (gameEnded()) {
            gameOver();
            return;
        }
        for (int y = 0; y < COLUMNS; y++) {
            circles.get(y).setFill(Color.LIGHTSLATEGRAY);
            circles.get(y).setEffect(null);
        }
        try {
            for (int x = 0; x < COLUMNS; x++) {
                columns.get(x).setFill(Color.rgb(0,0,0,0));
            }
        }
        catch (IndexOutOfBoundsException e){}
        redMove = !redMove;
        if (redMove==playerColorRed) {
            IntStream.range(0, COLUMNS).forEach(y -> circles.get(y).setFill(Color.LIGHTSLATEGRAY));
            circles.get(currentOn).setFill(redMove ? Color.RED : Color.YELLOW);
            light(circles.get(currentOn));
            for (int x = 0; x < COLUMNS; x++) {
                columns.get(x).setFill(Color.rgb(0,0,0,0));
            }
            columns.get(currentOn).setFill(Color.rgb(153, 153, 153, 0.3));
        }
        //printGrid(grid);
        if (! computerTurn){
            computerTurn=true;
            // AI ALGORITHMS TAKES THE TYPE , THE GRID AND THE K_LEVELS
            //get the board state from the grid like in printGrid
            Solution computerColumn=ai.miniMax(getBoard(grid),k,alphaBetaPruning);
            placeDisc(new Main.Disc(redMove), computerColumn.getChoice()[1],computerTurn==true);
            computerTurn=false;
        }
        if (temp==0){
            temp++;
        }
        else {
            temp--;
            canPlay=true;
        }
        clicked=false;
    }

    private int playEnded(int column, int row) {
        List<Point2D> vertical = IntStream.rangeClosed(row - 3, row + 3).mapToObj(r -> new Point2D(column, r)).collect(Collectors.toList());

        List<Point2D> horizontal = IntStream.rangeClosed(column - 3, column + 3).mapToObj(c -> new Point2D(c, row)).collect(Collectors.toList());

        Point2D topLeft = new Point2D(column - 3, row - 3);
        List<Point2D> diagonal1 = IntStream.rangeClosed(0, 6).mapToObj(i -> topLeft.add(i, i)).collect(Collectors.toList());

        Point2D botLeft = new Point2D(column - 3, row + 3);
        List<Point2D> diagonal2 = IntStream.rangeClosed(0, 6).mapToObj(i -> botLeft.add(i, -i)).collect(Collectors.toList());

        return checkRange(vertical) + checkRange(horizontal) + checkRange(diagonal1) + checkRange(diagonal2);
    }

    private boolean gameEnded() {
        return remaining <=0;
    }

    private int checkRange(List<Point2D> points) {
        int chain = 0;
        int scoreIncreament=0;
        for (Point2D p : points) {
            int column = (int) p.getX();
            int row = (int) p.getY();

            Disc disc = getDisc(column, row).orElse(new Disc(!redMove));
            if (disc.red == redMove) {
                chain++;
                if (chain >= 4) {
                    scoreIncreament++;
                }
            } else {
                chain = 0;
            }
        }
        return scoreIncreament;
    }

    private void playOver(int scoreIncreament) {
        if (redMove){
            redScore+=scoreIncreament;
            System.out.println("Score : "+redScore);
        }
        else {
            yellowScore+=scoreIncreament;
            System.out.println("Score : "+yellowScore);
        }
        System.out.println("Winner: " + (redMove ? "RED" : "YELLOW"));
        if (playerColorRed){
            computerScoreValue.setText(String.valueOf(yellowScore));
            playerScoreValue.setText(String.valueOf(redScore));
        }
        else {
            computerScoreValue.setText(String.valueOf(redScore));
            playerScoreValue.setText(String.valueOf(yellowScore));
        }

    }

    private void gameOver() {
        gameOvered=true;
        canPlay=true;
        if (redScore>yellowScore){
            System.out.println("Winner: RED");
            if (playerColorRed){
                playerWinner.setVisible(true);
            }
            else {
                computerWinner.setVisible(true);
            }
        }
        else if (yellowScore>redScore){
            System.out.println("Winner: YELLOW");
            if (playerColorRed){
                computerWinner.setVisible(true);
            }
            else {
                playerWinner.setVisible(true);
            }
        }
        else{
            System.out.println("Tied");
            tied.setVisible(true);
        }
        for (int y = 0; y < COLUMNS; y++) {
            circles.get(y).setFill(Color.LIGHTSLATEGRAY);
            circles.get(y).setEffect(null);
        }
        for (int x = 0; x < COLUMNS; x++) {
            columns.get(x).setFill(Color.rgb(0,0,0,0));
        }
    }

    private Optional<Disc> getDisc(int column, int row) {
        if (column < 0 || column >= COLUMNS || row < 0 || row >= ROWS) {
            return Optional.empty();
        }
        else {
            return Optional.ofNullable(grid[column][row]);
        }
    }

    private class Disc extends Circle {
        private final boolean red;
        private Disc(boolean red) {
            super(TILE_SIZE >> 1, red ? Color.RED : Color.YELLOW);
            this.red = red;
            light(this);
            setCenterX(TILE_SIZE >> 1);
            setCenterY((TILE_SIZE >> 1) +3);
        }
    }

    private void light(Node shape){
        Light.Distant light = new Light.Distant();
        light.setAzimuth(45.0);
        light.setElevation(30.0);
        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSurfaceScale(5.0);
        shape.setEffect(lighting);
    }

    private class SwitchButton extends StackPane {
        private final Rectangle back = new Rectangle(60, 30, Color.BLACK);

        private final Button button = new Button();
        private String buttonStyleOff = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 0.2, 0.0, 0.0, 2); -fx-background-color: WHITE;";
        private String buttonStyleOn = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 0.2, 0.0, 0.0, 2); -fx-background-color: #00893d;";
        private boolean state;

        private void init(boolean playerChoosing) {
            getChildren().addAll(back, button);
            setMinSize(50, 35);
            back.maxWidth(50);
            back.minWidth(50);
            back.maxHeight(30);
            back.minHeight(30);
            back.setArcHeight(back.getHeight());
            back.setArcWidth(back.getHeight());
            if (!playerChoosing) {
                back.setFill(Color.DARKGRAY);
            }
            else {
                back.setFill(Color.DARKRED);
            }
            Double r = 6.0;
            button.setShape(new Circle(r));
            setAlignment(button, Pos.CENTER_LEFT);
            button.setMaxSize(35, 30);
            button.setMinSize(35, 30);
            button.setStyle(buttonStyleOff);
        }

        private SwitchButton(boolean playerChoosing) {
            if (!playerChoosing) {
                init(playerChoosing);
                EventHandler<Event> click = new EventHandler<Event>() {
                    @Override
                    public void handle(Event e) {
                        if(!computerTurn&&canPlay) {
                            if (state) {
                                button.setStyle(buttonStyleOff);
                                back.setFill(Color.DARKGRAY);
                                setAlignment(button, Pos.CENTER_LEFT);
                                state = false;
                            } else {
                                button.setStyle(buttonStyleOn);
                                back.setFill(Color.valueOf("#80C49E"));
                                setAlignment(button, Pos.CENTER_RIGHT);
                                state = true;
                            }
                        }
                    }
                };

                button.setFocusTraversable(false);
                setOnMouseClicked(click);
                button.setOnMouseClicked(click);
            }
            else {
                buttonStyleOff = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 0.2, 0.0, 0.0, 2); -fx-background-color: RED;";
                buttonStyleOn = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 0.2, 0.0, 0.0, 2); -fx-background-color: YELLOW;";
                init(playerChoosing);
                EventHandler<Event> click = new EventHandler<Event>() {
                    @Override
                    public void handle(Event e) {
                        if (state) {
                            button.setStyle(buttonStyleOff);
                            back.setFill(Color.DARKRED);
                            setAlignment(button, Pos.CENTER_LEFT);
                            state = false;
                        } else {
                            button.setStyle(buttonStyleOn);
                            back.setFill(Color.rgb(255, 204, 0));
                            setAlignment(button, Pos.CENTER_RIGHT);
                            state = true;
                        }
                    }
                };
                button.setFocusTraversable(false);
                setOnMouseClicked(click);
                button.setOnMouseClicked(click);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setResizable(false);
        primaryStage.setTitle("Connect - 4 GEMA");
        gameScene =new Scene(createContent(primaryStage));
        gameScene.setFill(Color.GRAY);
        primaryStage.setScene(gameScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}