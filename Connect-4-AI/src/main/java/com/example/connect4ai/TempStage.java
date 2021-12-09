package com.example.connect4ai;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TempStage extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        byte[][] board = {{0,0,0,0,0,0,0}, {0,0,2,0,0,0,0}, {1,2,1,2,1,2,0}, {1,1,1,2,2,1,0},
                {1,2,2,1,1,2,0}, {2,1,1,2,2,2,1}};

        Ai ai = new Ai();
        Solution sol1 = ai.miniMax(board, 7, false);
        Solution sol2 = ai.miniMax(board, 7, true);

        System.out.println("(" + sol1.getChoice()[0] + "," + sol1.getChoice()[1] + ")");
        System.out.println(sol1.getRoot().getValue());

        System.out.println("====================================================");

        System.out.println("(" + sol2.getChoice()[0] + "," + sol2.getChoice()[1] + ")");
        System.out.println(sol2.getRoot().getValue());
        TreeView tv = new TreeView();
        tv.DrawTree(sol1.getRoot());
        Group root = new Group();
        Scene scene = new Scene(root, Color.BLUEVIOLET);
        root.getChildren().add(tv);
        stage.setTitle("Connect-4");
        stage.setFullScreen(true);
        stage.setHeight(720);
        stage.setWidth(1024);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

