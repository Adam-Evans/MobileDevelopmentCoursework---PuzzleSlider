package mobile.labs.acw;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * Created by AdamTheSecond on 2018-04-09.
 */

public class puzzleBoard {

    private Context mContext;
    private ArrayList<Bitmap> mPuzzlePieces;
    private GridView mGridView;
    private Puzzle mPuzzle;
    private Point mBlankLocation;
    private Point mMoveLocation;
    private Point[] mMovableLocations;
    private int[] mBoard;

    public puzzleBoard(Context aContext,
                       ArrayList<Bitmap> aPuzzlePieces,
                       GridView aGridView,
                       Puzzle aPuzzle)
    {
        mContext = aContext;
        mPuzzle = aPuzzle;
        mPuzzlePieces = aPuzzlePieces;
        mGridView = aGridView;
        mBoard = new int[aPuzzlePieces.size()];
        mBlankLocation = new Point();
        mMoveLocation = new Point();
        mMovableLocations = new Point[4];
        initialiseBoard();
    }

    public void initialiseBoard()
    {

    }
}
