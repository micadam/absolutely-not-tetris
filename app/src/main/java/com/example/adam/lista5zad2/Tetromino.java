package com.example.adam.lista5zad2;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by adam on 10.05.17.
 */

public class Tetromino implements Parcelable {

    /*
1   Cyan I
2   Yellow O
3   Purple T
4   Green S
5   Red Z
6   Blue J
7   Orange L*/

    private int type;
    private int rot;

    private static int[][] bricks = { {},//none
            {0x0F00, 0x2222, 0x00F0, 0x4444}, //I
            {0x6600, 0x6600, 0x6600, 0x6600}, //O
            {0x4E00, 0x4640, 0x0E40, 0x4c40}, //T
            {0x6C00, 0x4620, 0x06C0, 0x8C40}, //S
            {0xC600, 0x2640, 0x0C60, 0x4C80}, //Z
            {0x8E00, 0x6440, 0x0E20, 0x44C0}, //J
            {0x2E00, 0x4460, 0x0E80, 0xC440}, //L
    };


    Tetromino(int type) {
        if(type < 1 || type > 7) {
            throw new IllegalArgumentException("Unknown tetromino type: " + type);
        }
        this.type = type;
        rot = 0;
    }

    Tetromino(int type, int rot) {
        if(type < 1 || type > 7) {
            throw new IllegalArgumentException("Unknown tetromino type: " + type);
        }
        this.type = type;
        if(type < 0 || type > 4) {
            throw new IllegalArgumentException("Unknown rotation: " + type);
        }
        this.rot = rot;
    }

    protected Tetromino(Parcel in) {
        type = in.readInt();
        rot = in.readInt();
    }

    public static final Creator<Tetromino> CREATOR = new Creator<Tetromino>() {
        @Override
        public Tetromino createFromParcel(Parcel in) {
            return new Tetromino(in.readInt(), in.readInt());
        }

        @Override
        public Tetromino[] newArray(int size) {
            return new Tetromino[size];
        }
    };

    public void rotate(int dr) {
        rot += 4 + dr;
        rot = rot % 4;
    }

    public int getType() {
        return type;
    }

    public boolean occupied(int x, int y) {
        if(x < 0 || y < 0 || x > 3 || y > 3) {
            throw new IllegalArgumentException("Unknown tetromino coordinates: " + x + ", " + y);
        }
        int ret = bricks[type][rot];
        ret = ret >> 4 * (3 - y);
        ret = ret & 0x000F;
        ret = ret >> (3 - x);
        ret = ret & (0x0001);
        return ret == 1;
    }

    public void setRot(int rot) {
        if(rot < 0 || rot >= 4) {
            throw new IllegalArgumentException("Unknown tetromino rotation: " + rot);
        }
        this.rot = rot;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeInt(rot);
    }
}
