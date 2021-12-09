package com.example.connect4ai;

import javafx.scene.shape.Circle;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.LinkedList;

public class BoardState extends Circle {

    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8','9', 'A', 'B', 'C',
            'D', 'E', 'F', 'G', 'H','I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q'};
    private static final int rows = 6, cols = 7;

    private int row;
    private int col;
    // TODO.
    private BoardState parent;
    private LinkedList<BoardState> children;

    private int value;
    private String ID;
    private boolean isMax;
    private boolean isMarked;

    // Added By Mostafa
    private Color color = Color.rgb(235, 52, 52);
    TreeView tv ;

    public boolean getIsMax(){
        return isMax;
    }

    public Color getColor(){
        return color;
    }
    public void setColor(Color color){
        this.color = color;
    }

    public void setNodeToBeDrawn(TreeView tv){
        this.tv = tv;
        setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setFill(color.darker());
                setCursor(Cursor.HAND);
            }
        });
        setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                setFill(color);
                setCursor(Cursor.DEFAULT);
            }
        });
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                tv.DrawTree(BoardState.this);
            }
        });
    }
    //

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
    }

    public BoardState (byte[][] board, boolean isMax){
        ID = calcID(board);
        row = 0;
        col = 0;
        this.isMax = isMax;
        value = 0;
        isMarked = false;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getID() {
        return ID;
    }

    public LinkedList<BoardState> calcChildren(byte[][] board) {
        if (board == null)
            return null;
        LinkedList<BoardState> children = new LinkedList<>();
        for (int j = 0; j < cols; j++) {
            if (board[0][j] != 0)
                continue;
            BoardState child;
            for (int i = 1; i < rows; i++) {
                if (board[i][j] != 0){
                    if (isMax)
                        board[i-1][j] = 2;
                    else
                        board[i-1][j] = 1;
                    child = new BoardState(board, !isMax);
                    child.setRow(i-1);
                    child.setCol(j);
                    child.setParent(this);
                    children.add(child);

                    // reset.
                    board[i-1][j] = 0;
                    break;
                }
                else if (i == rows-1){
                    if (isMax)
                        board[i][j] = 2;
                    else
                        board[i][j] = 1;
                    child = new BoardState(board, !isMax);
                    child.setRow(i);
                    child.setCol(j);
                    child.setParent(this);
                    children.add(child);

                    // reset.
                    board[i][j] = 0;
                }
            }
        }
        this.children = children;
        return children;
    }

    private String calcID(byte[][] board){
        if (board == null)
            return null;
        StringBuilder idBuilder = new StringBuilder();
        int index = 0;
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i += 3) {
                index += board[i][j] + board[i+1][j] * 3 + board[i+2][j] * 9;
                idBuilder.append(DIGITS[index]);
                index = 0;
            }
        }
        return idBuilder.toString();
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public BoardState getBoardParent() {
        return parent;
    }

    public void setParent(BoardState parent) {
        this.parent = parent;
    }

    public LinkedList<BoardState> getChildren(){
        return children;
    }
}

