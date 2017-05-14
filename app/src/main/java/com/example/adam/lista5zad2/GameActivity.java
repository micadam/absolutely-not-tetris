package com.example.adam.lista5zad2;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends AppCompatActivity {

    GameBoard gameBoard;
    private volatile Handler gameHandler;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        gameBoard = (GameBoard)(findViewById(R.id.gameBoard));

        gameHandler = new Handler(Looper.getMainLooper());

        gameBoard.go(gameHandler);
    }

    public void move(View view) {
        int dx;
        if(view.getTag().equals("moveLeft")) {
            dx = -1;
        } else {
            dx = 1;
        }
        gameBoard.move(dx);
    }

    public void drop(View view) {
        gameBoard.drop();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        gameBoard.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameBoard.pause();
    }

    public void rotate(View view) {
        int dr;
        if(view.getTag().equals("rotateLeft")) {
            dr = -1;
        } else {
            dr = 1;
        }
        gameBoard.rotate(dr);
    }

    public void hold(View view) {
        gameBoard.hold();
    }
}
