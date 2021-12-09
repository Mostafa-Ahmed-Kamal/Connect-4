package com.example.connect4ai;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;

import java.util.Stack;

public class TreeView extends Group {
    private int screenWidth = (int) Screen.getScreens().get(0).getBounds().getMaxX();
    private final int LevelGap = 50;
    private final int radius = 17;
    private final int TILE_SIZE = 50;
    private final int COLUMNS = 7;
    private final int ROWS = 6;
    private byte[][] board;
    private Stack<BoardState> history = new Stack<>();
    private Shape shape ;
    private Color AiColor = Color.RED;
    private Color HumanColor = Color.YELLOW;

    public TreeView(){
        this.shape = makeGrid();
    }
    public void clearHistory(){
        history.clear();
    }

    public void setColors(boolean playerColorRed){
        if(playerColorRed){
            AiColor = Color.YELLOW;
            HumanColor = Color.RED;
        }
        else{
            AiColor = Color.RED;
            HumanColor = Color.YELLOW;
        }
    }

    public void setBoard(byte[][] board){
        this.board = board;
        //System.out.println("i am heeeere");
        for(int i=0 ; i<board.length; i++){
            for (int j = 0; j <board[0].length ; j++) {
                //System.out.print(board[i][j]+" ");
            }
            //System.out.println();
        }
    }
    private void drawAdditionalMoves(BoardState state){
        BoardState looper = state;
        while(looper.getBoardParent()!=null){
            Circle circle = new Circle(TILE_SIZE >> 1);
            circle.setCenterX(TILE_SIZE >> 1);
            circle.setCenterY((TILE_SIZE >> 1) + (1.1) * TILE_SIZE);
            circle.setTranslateX(looper.getCol() * (TILE_SIZE + 5) + (TILE_SIZE >> 2));
            circle.setTranslateY(looper.getRow() * (TILE_SIZE + 5) + (TILE_SIZE >> 2));
            if(looper.getIsMax()){
                circle.setFill(HumanColor);
            }
            else{
                circle.setFill(AiColor);
            }
            circle.setCenterX(circle.getCenterX() + screenWidth / 2 - 200);
            circle.setCenterY(circle.getCenterY() + 300);
            getChildren().add(circle);
            looper = looper.getBoardParent();
        }



    }
    private void drawBoard(){
        for (int y=0 ; y < ROWS; y++){
            for (int x=0 ; x < COLUMNS ;x++){
                if(board[y][x]!=0) {
                    Circle circle = new Circle(TILE_SIZE >> 1);
                    circle.setCenterX(TILE_SIZE >> 1);
                    circle.setCenterY((TILE_SIZE >> 1) + (1.1) * TILE_SIZE);
                    circle.setTranslateX(x * (TILE_SIZE + 5) + (TILE_SIZE >> 2));
                    circle.setTranslateY(y * (TILE_SIZE + 5) + (TILE_SIZE >> 2));
                    if (board[y][x] == 1) {
                        circle.setFill(HumanColor);
                    } else if (board[y][x] == 2) {
                        circle.setFill(AiColor);
                    }
                    circle.setCenterX(circle.getCenterX() + screenWidth / 2 - 200);
                    circle.setCenterY(circle.getCenterY() + 300);
                    getChildren().add(circle);
                }
            }
        }
    }
    private Shape makeGrid() {
        Shape shape = new Rectangle((COLUMNS + 1) * TILE_SIZE, (ROWS + 1) * TILE_SIZE);
        ((Rectangle) shape).setY((1.1) * TILE_SIZE);
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                Circle circle = new Circle(TILE_SIZE >> 1);
                circle.setCenterX(TILE_SIZE >> 1);
                circle.setCenterY((TILE_SIZE >> 1) + (1.1) * TILE_SIZE);
                circle.setTranslateX(x * (TILE_SIZE + 5) + (TILE_SIZE >> 2));
                circle.setTranslateY(y * (TILE_SIZE + 5) + (TILE_SIZE >> 2));
                shape = Shape.subtract(shape, circle);
            }
        }
        shape.setFill(Color.BLUE);
        shape.setLayoutX(screenWidth/2-200);
        shape.setLayoutY(300);
        return shape;
    }

    public void backStep(){
        if(history.size()<=1)return;
        //System.out.println("size = " + history.size());
        history.pop();
        DrawTree(history.pop());
    }
    public void DrawTree(BoardState root){
        if(history.size()==0 || history.peek() != root)history.add(root);
        if(root==null)return;
        getChildren().clear();
        getChildren().add(shape);
        drawBoard();
        drawAdditionalMoves(root);
        int NofChildren = root.getChildren()==null? 0:root.getChildren().size();
        int gapDivisor = NofChildren==0 ? 1 : NofChildren;
        DrawTreeRecursion(root, screenWidth / 2, LevelGap,
                screenWidth / (2*gapDivisor) , 0);
    }

    public void DrawTreeRecursion(BoardState node , int x , int y ,int gap , int level){
        if(!(node==null || level>=5)) {
            int NofChildren = node.getChildren() == null ? 0 : node.getChildren().size();
            int gapStarter = NofChildren - 1;
            int gapDivisor;
            for (int i = 0; i < NofChildren; i++) {
                int position = x + (2 * i - gapStarter) * gap;
                Line l = new Line(position, y + LevelGap, x, y);
                if(node.getChildren().get(i).isMarked())l.setStrokeWidth(5);
                getChildren().add(l);
                int NofChildrenofChildren = node.getChildren().get(i).getChildren() == null ? 0 : node.getChildren().get(i).getChildren().size();
                gapDivisor = NofChildrenofChildren == 0 ? 1 : NofChildrenofChildren;
                DrawTreeRecursion(node.getChildren().get(i), position, y + LevelGap, gap / gapDivisor, level + 1);
            }
        }
        node.setNodeToBeDrawn(this);
        node.setCenterX(x);
        node.setCenterY(y);
        node.setRadius(radius);
        node.setColor(node.getIsMax()? AiColor : HumanColor);
        node.setFill(node.getColor());
        node.setStroke(Color.BLACK);
        Text t = new Text(x - 12, y + 4,node.getValue()+"");
        //t.setFont(new Font(9));
        t.setFill(Color.BLACK.darker().darker());
        getChildren().addAll(node,t);
    }

}

