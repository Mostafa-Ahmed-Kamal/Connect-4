package sample;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Ai {

    private static final int rows = 6, cols = 7;

    private static int MAX_DEPTH;
    private byte[][] Board;
    private HashMap<String, Integer> explored;

    //without pruning
    public Solution miniMax(byte[][] board, int maxDepth, boolean isPruning){
        MAX_DEPTH = maxDepth;
        Board = board;
        // root.
        BoardState root = new BoardState(Board, true);
        root.setMarked(true);
        explored = new HashMap<>();
        BoardState bestChild;
        if (isPruning)
            bestChild = maximize(root, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        else
            bestChild = maximize(root, 1);
        int[] choice = mark(bestChild);
        return new Solution(root, choice);
    }

    private BoardState maximize(BoardState state, int depth){
        // Terminal state.
        // 1. max depth is reached.
        // 2. Board is full.
        if (depth == MAX_DEPTH || isFull()){
            Board[state.getRow()][state.getCol()] = 2;
            int val = Heuristic(state);
            state.setValue(val);
            Board[state.getRow()][state.getCol()] = 0;
            return state;
        }
        depth++;
        BoardState maxChild = new BoardState(null, false);
        maxChild.setValue(Integer.MIN_VALUE);
        for (BoardState child : state.calcChildren(Board)) {
            if (explored.containsKey(child.getID()))
                continue;

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
        if (depth == MAX_DEPTH || isFull()){
            Board[state.getRow()][state.getCol()] = 1;
            int val = Heuristic(state);
            state.setValue(val);
            Board[state.getRow()][state.getCol()] = 0;
            return state;
        }
        depth++;
        BoardState minChild = new BoardState(null, true);
        minChild.setValue(Integer.MAX_VALUE);
        for (BoardState child : state.calcChildren(Board)) {
            if (explored.containsKey(child.getID()))
                continue;

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
        // Terminal state.
        // 1. max depth is reached.
        // 2. Board is full.
        // 3. state already visited.
        if (depth == MAX_DEPTH || isFull()){
            Board[state.getRow()][state.getCol()] = 2;
            int val = Heuristic(state);
            state.setValue(val);
            Board[state.getRow()][state.getCol()] = 0;
            return state;
        }
        depth++;
        BoardState maxChild = new BoardState(null, false);
        maxChild.setValue(Integer.MIN_VALUE);
        for (BoardState child : state.calcChildren(Board)) {
            if (explored.containsKey(child.getID()))
                continue;

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
        if (depth == MAX_DEPTH || isFull()){
            Board[state.getRow()][state.getCol()] = 1;
            int val = Heuristic(state);
            state.setValue(val);
            Board[state.getRow()][state.getCol()] = 0;
            return state;
        }
        depth++;
        BoardState minChild = new BoardState(null, true);
        minChild.setValue(Integer.MAX_VALUE);
        for (BoardState child : state.calcChildren(Board)) {
            if (explored.containsKey(child.getID()))
                continue;

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
        Random r = new Random();
        return r.nextInt(100);
    }

    private int[] mark(BoardState state){
        int i = 0, j = 0;
        while (state.getParent() != null){
            state.setMarked(true);
            i = state.getRow();
            j = state.getCol();
            state = state.getParent();
        }
        return new int[]{i, j};
    }

    // level order.
    public void print(BoardState root){
        Queue<BoardState> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            BoardState temp = queue.poll();
            System.out.println(temp.getValue());
            if (temp.getChildren() == null)
                continue;
            queue.addAll(temp.getChildren());
        }
    }

    public static void main(String[] args) {
        byte[][] board = {{0,0,0,0,0,0,0}, {0,0,2,0,0,0,0}, {1,2,1,2,1,2,0}, {1,1,1,2,2,1,0},
                {1,2,2,1,1,2,0}, {2,1,1,2,2,2,1}};

        Ai ai = new Ai();

        Solution sol1 = ai.miniMax(board, 3, false);
        Solution sol2 = ai.miniMax(board, 3, true);

        System.out.println("(" + sol1.getChoice()[0] + "," + sol1.getChoice()[1] + ")");
        System.out.println(sol1.getRoot().getValue());

        System.out.println("====================================================");

        System.out.println("(" + sol2.getChoice()[0] + "," + sol2.getChoice()[1] + ")");
        System.out.println(sol2.getRoot().getValue());
    }
}