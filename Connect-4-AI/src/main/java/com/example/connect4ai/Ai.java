package com.example.connect4ai;

import javafx.geometry.Point2D;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.System.exit;

public class Ai {

//	private int[] test;
//	private int ctr;

    private static final int rows = 6, cols = 7;
    private static  final byte[][] GRIDHEURISTIC =
            {{3,4,5,7,5,4,3},
            {4,6,8,10,8,6,4},
            {5,8,11,13,11,8,5},
            {5,8,11,13,11,8,5},
            {4,6,8,10,8,6,4},
            {3,4,5,7,5,4,3}
    };
    private static int MAX_DEPTH;
    private byte[][] Board;
    private HashMap<String, Integer> explored;

    //without pruning
    public byte[][] getBoard(){
        return this.Board;
    }
    public Solution miniMax(byte[][] board, int maxDepth, boolean isPruning){
        System.out.println("kaka");
        printBoard(board);
        MAX_DEPTH = maxDepth;
        Board = board;
        //root.
        BoardState root = new BoardState(Board, true);
        root.setMarked(true);
        explored = new HashMap<>();
        BoardState bestChild;
        if (isPruning)
            bestChild = maximize(root, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        else
            bestChild = maximize(root, 1);
        int[] choice = mark(bestChild);
        //print(root);
        this.Board[choice[0]][choice[1]] = 0;
        System.out.println("kiki");
        printBoard(this.Board);
        return new Solution(root, choice);
    }

    private BoardState maximize(BoardState state, int depth){
        if (explored.containsKey(state.getID())) {
            state.setValue(explored.get(state.getID()));
            return state;
        }
        // Terminal state.
        // 1. max depth is reached.
        // 2. Board is full.
        if (depth == MAX_DEPTH || isFull()){
            int val = Heuristic(state);
            state.setValue(val);
            // reset.
            Board[state.getRow()][state.getCol()] = 0;
            return state;
        }
        depth++;
        BoardState maxChild = new BoardState(null, false);
        maxChild.setValue(Integer.MIN_VALUE);
        for (BoardState child : state.calcChildren(Board)) {
            Board[child.getRow()][child.getCol()] = 2;
            BoardState temp = minimize(child, depth);

            explored.put(child.getID(), child.getValue());

            Board[child.getRow()][child.getCol()] = 0;
            if (temp.getValue() > maxChild.getValue())
                maxChild = temp;
        }
        state.setValue(maxChild.getValue());
        return maxChild;
    }

    private BoardState minimize(BoardState state, int depth){
        if (explored.containsKey(state.getID())) {
            state.setValue(explored.get(state.getID()));
            return state;
        }
        if (depth == MAX_DEPTH || isFull()){
            int val = Heuristic(state);
            state.setValue(val);
            // reset.
            Board[state.getRow()][state.getCol()] = 0;
            return state;
        }
        depth++;
        BoardState minChild = new BoardState(null, true);
        minChild.setValue(Integer.MAX_VALUE);
        for (BoardState child : state.calcChildren(Board)) {
            Board[child.getRow()][child.getCol()] = 1;
            BoardState temp = maximize(child, depth);

            explored.put(child.getID(), child.getValue());

            Board[child.getRow()][child.getCol()] = 0;
            if (temp.getValue() < minChild.getValue())
                minChild = temp;
        }
        state.setValue(minChild.getValue());
        return minChild;
    }

    // Pruning.

    private BoardState maximize(BoardState state, int depth, int alpha, int beta){
        if (explored.containsKey(state.getID())) {
            state.setValue(explored.get(state.getID()));
            return state;
        }
        // Terminal state.
        // 1. max depth is reached.
        // 2. Board is full.
        // 3. state already visited.
        if (depth == MAX_DEPTH || isFull()){
            int val = Heuristic(state);
            state.setValue(val);
            Board[state.getRow()][state.getCol()] = 0;
            return state;
        }
        depth++;
        BoardState maxChild = new BoardState(null, false);
        maxChild.setValue(Integer.MIN_VALUE);
        for (BoardState child : state.calcChildren(Board)) {
            Board[child.getRow()][child.getCol()] = 2;
            BoardState temp = minimize(child, depth, alpha, beta);

            explored.put(child.getID(), child.getValue());

            Board[child.getRow()][child.getCol()] = 0;
            if (temp.getValue() > maxChild.getValue())
                maxChild = temp;
            // Pruning.
            if (beta <= maxChild.getValue())
                break;
            if (alpha < maxChild.getValue())
                alpha = maxChild.getValue();
        }
        state.setValue(maxChild.getValue());
        return maxChild;
    }

    private BoardState minimize(BoardState state, int depth, int alpha, int beta){
        if (explored.containsKey(state.getID())) {
            state.setValue(explored.get(state.getID()));
            return state;
        }
        if (depth == MAX_DEPTH || isFull()){
            int val = Heuristic(state);
            state.setValue(val);
            Board[state.getRow()][state.getCol()] = 0;
            return state;
        }
        depth++;
        BoardState minChild = new BoardState(null, true);
        minChild.setValue(Integer.MAX_VALUE);
        for (BoardState child : state.calcChildren(Board)) {
            Board[child.getRow()][child.getCol()] = 1;
            BoardState temp = maximize(child, depth, alpha, beta);

            explored.put(child.getID(), child.getValue());

            Board[child.getRow()][child.getCol()] = 0;
            if (temp.getValue() < minChild.getValue())
                minChild = temp;
            // Pruning.
            if (alpha >= minChild.getValue())
                break;
            if (beta > minChild.getValue())
                beta = minChild.getValue();
        }
        state.setValue(minChild.getValue());
        return minChild;
    }


    private boolean isFull(){
        boolean flag = true;
        for (int i = 0; i < cols; i++)
            flag = flag && Board[0][i] != 0;
        return flag;
    }
    private int Heuristic(BoardState state) {
        System.out.println("state" + state.getRow() + " " + state.getCol());
        printBoard(this.Board);
        System.out.println();

        boolean[][] visited = new boolean[6][7];
        int sum = 0;
        int temp;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if(this.Board[i][j]==0)continue;
                visited[i][j]= true;
                temp =playEnded(j,i,visited);
                if(this.Board[i][j]==1){
                    temp*=-1;
                }
                System.out.println("i,j :" +i + " " + j+ " i am temp: " + temp);
                sum+=temp;
            }
        }
        return sum;
    }
    private int playEnded(int column, int row , boolean[][] visited) {
        int[] heuristics = new int[4];

        List<Point2D> vertical = IntStream.rangeClosed(row - 3, row + 3).mapToObj(r -> new Point2D(column, r)).collect(Collectors.toList());
        Point2D[] startEnd = getRange(vertical , visited);
        int vericalChain = calculateChain(startEnd);
        heuristics[0] = CalculateFeature(vericalChain , startEnd , 2 , visited);

        List<Point2D> horizontal = IntStream.rangeClosed(column - 3, column + 3).mapToObj(c -> new Point2D(c, row)).collect(Collectors.toList());
        startEnd = getRange(horizontal, visited);
        int horizontalChain = calculateChain(startEnd);
        heuristics[1] = CalculateFeature(horizontalChain,startEnd,1,visited);

        Point2D topLeft = new Point2D(column - 3, row - 3);
        List<Point2D> diagonal1 = IntStream.rangeClosed(0, 6).mapToObj(i -> topLeft.add(i, i)).collect(Collectors.toList());
        startEnd = getRange(diagonal1, visited);
        int topLeftChain = calculateChain(startEnd);
        heuristics[2] = CalculateFeature(topLeftChain , startEnd , 3 , visited);
        Point2D botLeft = new Point2D(column - 3, row + 3);
        List<Point2D> diagonal2 = IntStream.rangeClosed(0, 6).mapToObj(i -> botLeft.add(i, -i)).collect(Collectors.toList());
        startEnd = getRange(diagonal2, visited);
        int botleftChain = calculateChain(startEnd);
        heuristics[3] = CalculateFeature(botleftChain , startEnd ,4 ,visited);

        return heuristics[0] + heuristics[1] +heuristics[2] +heuristics[3] ;
    }
    public int CalculateFeature(int chain , Point2D[] startEnd , int position , boolean[][] visited){
            if(chain==1){
                //System.out.println("feature 4");
                return Feature4(startEnd);
            }
            if(chain==2){
                //System.out.println("feature 3");
                return Feature3(startEnd,position,visited);
            }
            if(chain==3){
                //System.out.println("feature 2");
                return Feature2(startEnd,position,visited);
            }
            //System.out.println("feature 1");
            return Feature1();
    }
    public int calculateChain(Point2D[] startEnd){
        return 1 + (int) Math.max( Math.abs(startEnd[0].getX() - startEnd[1].getX()) , Math.abs(startEnd[0].getY() - startEnd[1].getY()) );
    }
    private Point2D[] getRange(List<Point2D> points , boolean[][] visited) {
        Point2D[] strEnd=new Point2D[2];
        Point2D start=new Point2D(points.get(3).getY(),points.get(3).getX()),end=new Point2D(points.get(3).getY(),points.get(3).getX());
        for (int i=4;i<points.size();i++){
            byte disc = getDiscForRange((int) points.get(i).getX(), (int) points.get(i).getY(),visited).orElse((byte) (Board[(int) points.get(3).getY()][(int) points.get(3).getX()]==2 ? 1 : 2));
            if (disc==Board[(int) points.get(3).getY()][(int) points.get(3).getX()]){
                end=new Point2D(points.get(i).getY(),points.get(i).getX());
            }
            else {
                break;
            }
        }
        for (int i=2;i>-1;i--){
            byte disc = getDiscForRange((int) points.get(i).getX(), (int) points.get(i).getY(),visited).orElse((byte) (Board[(int) points.get(3).getY()][(int) points.get(3).getX()]==2 ? 1 : 2));
            if (disc==Board[(int) points.get(3).getY()][(int) points.get(3).getX()]){
                start=new Point2D(points.get(i).getY(),points.get(i).getX());
            }
            else {
                break;
            }
        }
        strEnd[0]=start;
        strEnd[1]=end;
        return strEnd;
    }

    private Optional<Byte> getDiscForRange(int column, int row , boolean[][] visited) {
        if (column < 0 || column >= 7 || row < 0 || row >= 6) {
            //System.out.println("test " + row + "  " + column);
            return Optional.empty();
        }
        if(visited[row][column]==true){
            return Optional.empty();
        }
        else {
            //System.out.println(" "  + this.Board[row][column] + "   " + row + " " + column);
            return Optional.ofNullable(this.Board[row][column]);
        }
    }



    /*

    Feature 1 : 4 connected tokens -----> set node value = 100k
    Feature 2 : 3 connected tokens -----> if left & right are empty and playable = 100k
                                          if left Or right are empty and playable = 90k
                                          if left & right are occupied = 0
    Feature 3 : 2 connected tokens -----> if 1 left/right & 2 right/left are empty and playable = 40k
                                          if 1 left & 1 right are empty and playable = 30k
                                          if 1 left or 1 right are empty and playable = 20k
    Feature 4 : 1 connected token ------> give heuristic value depending on the position of the node
     */

    public int Feature1(){
        return 100000;
    }
    // position : 1 horizontal , 2 vertical , 3 topleftDiagonal , 4 botleftDiagonal
    public int Feature2(Point2D[] startEnd , int position , boolean[][] visited){
        boolean leftRowBlocked = false;
        boolean rightRowBlocked = false;
        if(position==1){
            leftRowBlocked = !checkEmpty((int) (startEnd[0].getX()), (int) startEnd[0].getY()-1, visited);
            rightRowBlocked = !checkEmpty((int) (startEnd[1].getX()), (int) startEnd[1].getY()+1, visited);
        }
        else if(position==2){
            leftRowBlocked = !checkEmpty((int) startEnd[0].getX()+1, (int) (startEnd[0].getY()), visited);
            rightRowBlocked = !checkEmpty((int) startEnd[1].getX()-1, (int) (startEnd[1].getY()), visited);
        }
        else if(position==3){
            leftRowBlocked = !checkEmpty((int) startEnd[0].getX()-1, (int) (startEnd[0].getY()-1), visited);
            rightRowBlocked = !checkEmpty((int) startEnd[1].getX()+1, (int) (startEnd[1].getY()+1), visited);
        }
        else if(position==4){
            leftRowBlocked = !checkEmpty((int) startEnd[0].getX()+1, (int) (startEnd[0].getY()-1), visited);
            rightRowBlocked = !checkEmpty((int) startEnd[1].getX()-1, (int) (startEnd[1].getY()+1), visited);
        }
        if(!leftRowBlocked && !rightRowBlocked)return 100000;
        if(leftRowBlocked && rightRowBlocked)return 0;
        return 90000;
    }
    public  int Feature3(Point2D[] startEnd , int position , boolean[][] visited){
        boolean[] leftRowBlocked = new boolean[2];
        boolean[] rightRowBlocked = new boolean[2];
        if(position==1){
            leftRowBlocked[0] = !checkEmpty((int) (startEnd[0].getX()), (int) startEnd[0].getY()-1, visited);
            rightRowBlocked[0] = !checkEmpty((int) (startEnd[1].getX()), (int) startEnd[1].getY()+1, visited);
            leftRowBlocked[1] = !checkEmpty((int) (startEnd[0].getX()), (int) startEnd[0].getY()-2, visited);
            rightRowBlocked[1] = !checkEmpty((int) (startEnd[1].getX()), (int) startEnd[1].getY()+2, visited);
        }
        else if(position==2){
            leftRowBlocked[0] = !checkEmpty((int) startEnd[0].getX()+1, (int) (startEnd[0].getY()), visited);
            rightRowBlocked[0] = !checkEmpty((int) startEnd[1].getX()-1, (int) (startEnd[1].getY()), visited);
            leftRowBlocked[1] = !checkEmpty((int) startEnd[0].getX()+2, (int) (startEnd[0].getY()), visited);
            rightRowBlocked[1] = !checkEmpty((int) startEnd[1].getX()-2, (int) (startEnd[1].getY()), visited);
        }
        else if(position==3){
            leftRowBlocked[0] = !checkEmpty((int) startEnd[0].getX()-1, (int) (startEnd[0].getY()-1), visited);
            rightRowBlocked[0] = !checkEmpty((int) startEnd[1].getX()+1, (int) (startEnd[1].getY()+1), visited);
            leftRowBlocked[1] = !checkEmpty((int) startEnd[0].getX()-2, (int) (startEnd[0].getY()-2), visited);
            rightRowBlocked[1] = !checkEmpty((int) startEnd[1].getX()+2, (int) (startEnd[1].getY()+2), visited);
        }
        else if(position==4){
            leftRowBlocked[0] = !checkEmpty((int) startEnd[0].getX()+1, (int) (startEnd[0].getY()-1), visited);
            rightRowBlocked[0] = !checkEmpty((int) startEnd[1].getX()-1, (int) (startEnd[1].getY()+1), visited);
            leftRowBlocked[1] = !checkEmpty((int) startEnd[0].getX()+2, (int) (startEnd[0].getY()-2), visited);
            rightRowBlocked[1] = !checkEmpty((int) startEnd[1].getX()-2, (int) (startEnd[1].getY()+2), visited);
        }
        if(leftRowBlocked[0] && rightRowBlocked[0])return 0;
        if(!leftRowBlocked[0]){
            if((!leftRowBlocked[1] && !rightRowBlocked[0] )|| (!rightRowBlocked[0] && !rightRowBlocked[1]))return 30000;
            if (!rightRowBlocked[0])return 20000;
            return 10000;
        }
        if(!rightRowBlocked[1]) return 20000;
        else return 10000;
    }
    public int Feature4(Point2D[] startEnd){
        return 1000*GRIDHEURISTIC[(int) startEnd[0].getX()][(int) startEnd[0].getY()];
    }

    public boolean checkEmpty(int row , int column , boolean[][] visited){
        if (column < 0 || column >= 7 || row < 0 || row >= 6) {
            return false;
        }
        if(visited[row][column]){
            return false;
        }
        if(this.Board[row][column] == 0){
            if (row+1 >= 6 || this.Board[row+1][column]!=0) {
                return true;
            }
            return false;
        }
        return false;
    }
    public byte[][] leafBoard(BoardState state){
        byte[][] b = new byte[this.Board.length][this.Board[0].length];
        for(int i=0 ; i < b.length ; i++){
            for(int j=0 ; j < b[0].length ; j++){
                b[i][j] = this.Board[i][j];
            }
        }
        BoardState looper = state;
        while(looper.getBoardParent()!=null){
            b[looper.getRow()][looper.getCol()] = (byte) (looper.getIsMax()? 2 : 1);
            looper = looper.getBoardParent();
        }
        printBoard(b);
        return b;
    }
    public void printBoard(byte[][] board){
        for(int i=0 ; i<board.length; i++){
            for(int j=0 ; j<board[0].length ;j++){
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    private int[] mark(BoardState state){
        int i = 0, j = 0;
        while (state.getBoardParent() != null){
            state.setMarked(true);
            i = state.getRow();
            j = state.getCol();
            state = state.getBoardParent();
        }
        return new int[]{i, j};
    }

    // level order.
    public void print(BoardState root){
        Queue<BoardState> queue = new LinkedList<>();
        queue.add(root);
        int i = 0;
        while (!queue.isEmpty()) {
            BoardState temp = queue.poll();
            System.out.println(temp.getValue() + " " + i++);
            if (temp.getChildren() == null)
                continue;
            queue.addAll(temp.getChildren());
        }
    }

    /*public static void main(String[] args) {
        byte[][] board = {{0,0,2,1,0,0,0}, {0,1,2,2,2,1,0}, {0,2,1,2,1,2,1}, {1,1,1,2,2,1,2},
                {1,2,2,1,1,2,1}, {2,1,1,2,2,2,1}};

        // byte[][] board = new byte[rows][cols];
        Ai ai = new Ai();

        Solution sol1 = ai.miniMax(board, 5, false);
        System.out.println("====================================");
        // Solution sol2 = ai.miniMax(board, 2, true);

        System.out.println("(" + sol1.getChoice()[0] + "," + sol1.getChoice()[1] + ")");
        System.out.println(sol1.getRoot().getValue());

        System.out.println("====================================================");

//		System.out.println("(" + sol2.getChoice()[0] + "," + sol2.getChoice()[1] + ")");
//		System.out.println(sol2.getRoot().getValue());
    }*/
    public static void main(String[] args) {
        Ai t = new Ai();
        t.Board = new byte[][]{{0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}};
        byte[][] b = new byte[][] {{0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 1, 0}};
        //t.Heuristic(null);
        t.miniMax(b , 3, false);
        t.printBoard(t.Board);
    }
}

