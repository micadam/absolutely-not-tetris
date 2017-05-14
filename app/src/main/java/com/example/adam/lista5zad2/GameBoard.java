package com.example.adam.lista5zad2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by adam on 10.05.17.
 */

public class GameBoard extends View {

    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 22;
    public static final int STANDARD_DELAY = 400;
    /*
1   Cyan I
2   Yellow O
3   Purple T
4   Green S
5   Red Z
6   Blue J
7   Orange L*/

    private ArrayList<Tetromino> tetrominoBag;
    private ArrayList<Tetromino> nextBag;
    private Tetromino currentTetromino;
    private Tetromino holdTetromino;
    private int currentX;
    private int currentY;
    private int[][] gameBoard;
    private Handler handler;

    Random random;
    Runnable gravityRunnable;

    Runnable finishFallingRunnable;

    public static final int COLORS[] = {Color.TRANSPARENT, Color.CYAN, Color.YELLOW, Color.parseColor("#551a8b"),
            Color.GREEN, Color.RED, Color.BLUE, Color.parseColor("#ffa500")};

    Paint fillPaint;

    private boolean gameOver;

    public GameBoard(Context context) {
        super(context);
        startGame();
    }

    public GameBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        startGame();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable start = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", start);
        bundle.putParcelableArrayList("tetrominoBag", tetrominoBag);
        bundle.putParcelableArrayList("nextBag", nextBag);
        bundle.putInt("currentX", currentX);
        bundle.putInt("currentY", currentY);
        bundle.putParcelable("currentTetromino", currentTetromino);
        bundle.putSerializable("gameBoard", gameBoard);
        bundle.putBoolean("gameOver", gameOver);
        handler.removeCallbacks(gravityRunnable);
        handler.removeCallbacks(finishFallingRunnable);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle) {
            Bundle bundle = (Bundle)state;
            super.onRestoreInstanceState(bundle.getParcelable("superState"));
            tetrominoBag = bundle.getParcelableArrayList("tetrominoBag");
            nextBag = bundle.getParcelableArrayList("nextBag");
            currentX = bundle.getInt("currentX");
            currentY = bundle.getInt("currentY");
            currentTetromino = bundle.getParcelable("currentTetromino");
            gameBoard = (int[][])bundle.getSerializable("gameBoard");
            gameOver = bundle.getBoolean("gameOver");
        }
    }


    private void startGame() {
        gameBoard = new int[BOARD_HEIGHT][BOARD_WIDTH];
        random = new Random();
        fillPaint = new Paint();
        tetrominoBag = new ArrayList<>();
        nextBag = new ArrayList<>();
    }

    private Tetromino getTetromino() {
        Tetromino ret;
        while(tetrominoBag.size() == 0) {
            tetrominoBag = nextBag;
            nextBag = new ArrayList<>();
            for(int i = 1; i <= 7; i++) {
                nextBag.add(new Tetromino(i));
            }
            Collections.shuffle(nextBag);
        }
        ret = tetrominoBag.get(0);
        tetrominoBag.remove(0);
        return ret;
    }

    public void go(final Handler handler) {
        this.handler = handler;
        currentTetromino = getTetromino();
        currentX = 3;
        currentY = 0;


        gravityRunnable = new Runnable() {
            @Override
            public void run() {
                if(occupiable(currentX, currentY + 1, currentTetromino)) {
                    if(gameBoard != null) {
                        currentY += 1;
                        handler.postDelayed(this, STANDARD_DELAY);
                        invalidate();
                    }
                } else {
                    handler.postDelayed(finishFallingRunnable, STANDARD_DELAY);
                }
            }
        };

        finishFallingRunnable = new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 4; i++) {
                    for(int j = 0; j < 4; j++) {
                        if(currentTetromino.occupied(i, j)) {
                            gameBoard[currentY + j][currentX + i] = currentTetromino.getType();
                        }
                    }
                }

                //loss condition - a brick is in one of the invisible rows
                for(int j = 0; j < BOARD_WIDTH; j++) {
                    if(gameBoard[0][j] > 0 || gameBoard[1][j] > 0) {
                        gameOver = true;
                        invalidate();
                        return;
                    }
                }

                //clear rows
                //remove filled rows
                for(int i = 0; i < BOARD_HEIGHT; i++) {
                    boolean filled = true;
                    for(int j = 0; j < BOARD_WIDTH; j++) {
                        if(gameBoard[i][j] == 0) {
                            filled = false;
                        }
                    }
                    if(filled) {
                        gameBoard[i] = null;
                    }
                }
                //move rows above empty rows down as much as possible
                for(int i = BOARD_HEIGHT - 2; i >= 0; i--) {
                    int j = i;
                    while(j < BOARD_HEIGHT - 1 && gameBoard[j + 1] == null) {
                        gameBoard[j + 1] = gameBoard[j];
                        gameBoard[j] = null;
                        j++;
                    }
                }
                //create empty rows at the top
                for(int i = 0; i < BOARD_HEIGHT; i++) {
                    if(gameBoard[i] == null) {
                        gameBoard[i] = new int[BOARD_WIDTH];
                    }
                }
                currentTetromino = getTetromino();
                currentX = 3;
                currentY = 0;
                invalidate();
                handler.postDelayed(gravityRunnable, STANDARD_DELAY);
            }
        };

        handler.post(gravityRunnable);
    }

    private boolean occupiable(int x, int y, Tetromino tetromino) {
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                if(tetromino.occupied(i, j) && (x + i >= BOARD_WIDTH || y + j >= BOARD_HEIGHT ||
                        x + i < 0 || y + j < 0 || gameBoard[y + j][x + i] > 0) ) {
                    return false;
                }
            }
        }
        return true;
    }

    private void drawTetromino(Canvas canvas, int x, int y, float brickWidth, float brickHeight,
                               Tetromino tetromino, Paint paint) {
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                if(tetromino.occupied(i, j)) {
                    int alpha = paint.getAlpha();
                    paint.setColor(COLORS[tetromino.getType()]);
                    paint.setAlpha(alpha);
                    canvas.drawRect((x + i) * brickWidth, (y + j) * brickHeight,
                            (x+ i + 1) * brickWidth, (y + j + 1) * brickHeight, paint);
                }
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = canvas.getWidth();
        float height = canvas.getHeight();

        //game background
        fillPaint.setColor(ResourcesCompat.getColor(getResources(), R.color.bgcolor, null));
        canvas.drawRect(0, 0, width, height, fillPaint);

        float brickWidth = width/BOARD_WIDTH;
        float brickHeight = height/(BOARD_HEIGHT);


        for(int i = 0; i < BOARD_HEIGHT; i++) {
            for(int j = 0; j < BOARD_WIDTH; j++) {
                fillPaint.setColor(COLORS[gameBoard[i][j]]);
                canvas.drawRect(j * brickWidth, i * brickHeight,
                        (j+1) * brickWidth, (i+1)*brickHeight, fillPaint);
            }
        }


        //ghost tetromino
        int ghostY = currentY;
        while(occupiable(currentX, ghostY + 1, currentTetromino)) {
            ghostY++;
        }
        fillPaint.setAlpha(50);
        drawTetromino(canvas, currentX, ghostY, brickWidth, brickHeight,
                currentTetromino, fillPaint);
        fillPaint.setAlpha(254);

        fillPaint.setStyle(Paint.Style.STROKE);
        drawTetromino(canvas, currentX, ghostY, brickWidth, brickHeight,
                currentTetromino, fillPaint);
        fillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        //current tetromino
        drawTetromino(canvas, currentX, currentY, brickWidth, brickHeight,
                currentTetromino, fillPaint);

        fillPaint.setColor(ResourcesCompat.getColor(getResources(), R.color.fgcolor, null));
        canvas.drawRect(0, 0, width, 2 * brickHeight, fillPaint);

        //held tetromino
        if(holdTetromino != null) {
            drawTetromino(canvas, 0, 1, brickWidth/2, brickHeight/2, holdTetromino, fillPaint);
        }

        for(int i = 0; i < 4; i++) {
            Tetromino upcomingTetromino = tetrominoBag.size() > i ? tetrominoBag.get(i)
                    : nextBag.get(i - tetrominoBag.size());

            drawTetromino(canvas, 4 * (i + 1), 1, brickWidth/2, brickHeight/2,
                    upcomingTetromino, fillPaint);
        }
        fillPaint.setTextAlign(Paint.Align.LEFT);
        fillPaint.setTextSize((float)0.45 * brickHeight);
        fillPaint.setColor(Color.BLACK);
        canvas.drawLine(2 * brickWidth, 0, 2 * brickWidth, 2 * brickHeight, fillPaint);
        canvas.drawText("HOLD", 0, (float)0.45 * brickHeight, fillPaint);
        canvas.drawText("NEXT", 2 * brickWidth, (float)0.45 * brickHeight, fillPaint);
        fillPaint.setColor(Color.WHITE);
        if(gameOver) {
            fillPaint.setTextAlign(Paint.Align.CENTER);
            fillPaint.setTextSize(brickHeight);
            canvas.drawText("GAME\nOVER", width/2, height/2, fillPaint);
        }
    }

    public void move(int dx) {
        if(!gameOver) {
            handler.removeCallbacks(gravityRunnable);
            handler.removeCallbacks(finishFallingRunnable);
            if(occupiable(currentX + dx, currentY, currentTetromino)) {
                currentX += dx;
                invalidate();
            }
            handler.postDelayed(gravityRunnable, STANDARD_DELAY);
        }
    }

    public void drop() {
        if(!gameOver) {
            handler.removeCallbacks(gravityRunnable);
            int newY = currentY;
            while(occupiable(currentX, newY + 1, currentTetromino)) {
                newY++;
            }
            currentY = newY;
            handler.post(finishFallingRunnable);
        }
    }

    public void rotate(int dr) {
        if(!gameOver) {
            handler.removeCallbacks(gravityRunnable);
            handler.removeCallbacks(finishFallingRunnable);
            currentTetromino.rotate(dr);
            if(! occupiable(currentX, currentY, currentTetromino)) {
                currentTetromino.rotate(-dr);
            }
            invalidate();
            handler.postDelayed(gravityRunnable, STANDARD_DELAY);
        }
    }

    public void hold() {
        if(!gameOver) {
            handler.removeCallbacks(finishFallingRunnable);
            handler.removeCallbacks(gravityRunnable);
            currentTetromino.setRot(0);
            Tetromino tempTetromino = currentTetromino;
            currentTetromino = holdTetromino == null ? getTetromino() : holdTetromino;
            holdTetromino = tempTetromino;
            currentY = 0;
            currentX = 3;
            handler.postDelayed(gravityRunnable, STANDARD_DELAY);
        }
    }

    public void pause() {
        handler.removeCallbacks(gravityRunnable);
        handler.removeCallbacks(finishFallingRunnable);
    }

    public void resume() {
        handler.removeCallbacks(gravityRunnable);
        handler.postDelayed(gravityRunnable,STANDARD_DELAY);
    }
}


