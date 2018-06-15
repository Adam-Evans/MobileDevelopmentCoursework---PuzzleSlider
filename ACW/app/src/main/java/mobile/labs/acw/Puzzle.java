package mobile.labs.acw;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class Puzzle {

    private int id;
    private String name;
    private String pictureSet;
    private String layout;
    private String baseLayout;
    private int highScore;


    public Puzzle(String pName, String pPictureSet, String pLayout) {
        name = pName;
        pictureSet = pPictureSet;
        layout = pLayout;
    }

    public Puzzle(int pID, String pName, String pPictureSet, String pBaseLayout, String pLayout,
                  int pHighScore) {
        id = pID;
        name = pName;
        pictureSet = pPictureSet;
        baseLayout = pBaseLayout;
        layout = pLayout;
        highScore = pHighScore;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(name)
                .append(" - ")
                .append(highScore).toString();
    }

    public void setID(int pID)
    {
        id = pID;
    }

    public int getID(){
        return id;
    }

    public void setName(String pName) {
        name = pName;
    }

    public String getName()
    {
      return name.replaceAll(".json", "");
    }

    public void setPictureSet(String pPicSet)
    {
        pictureSet = pPicSet;
    }

    public String getPictureSet()
    {
        return pictureSet;
    }

    public String getBaseLayout(){return  baseLayout;}

    public String getLayoutTile(int row, int col) {
        String parse = layout.replace("\"", "");
        parse = parse.replace("|", ",");
        String[] split = parse.split(",");
        return split[row  + col * getLayoutWidth()];
    }

    public void setLayoutTile(int row, int col, String tile){
        String parse = layout.replace("\"", "");
        parse = parse.replace("|", ",");
        String[] split = parse.split(",");
        split[col * getLayoutWidth() + row] = tile;
        for (int i = 0; i < getLayoutHeight(); ++i)
        {
            for (int j = 0; j < getLayoutWidth(); ++j){
                split[i * getLayoutHeight() + j] += "\"";
                if (j == getLayoutWidth() - 1)
                    split[i * getLayoutHeight() + j] += "|";
                else
                    split[i * getLayoutHeight() + j] += ",";
                split[i * getLayoutHeight() + j] += "\"";
            }
        }
        layout = Arrays.toString(split);
    }

    public void setBaseLayout(String pBaseLayout){baseLayout = pBaseLayout;}

    public void setLayout(String pLayout)
    {
        layout = pLayout;
    }

    public String getLayout() {
        return layout;
    }

    public int getLayoutWidth() {
        String[] split = layout.split("\\|");
        String[] splitS = split[0].split(",");
        return splitS.length;
    }

    public int getLayoutHeight() {
        String[] split = layout.split("\\|");
        return split.length;
    }

    public Integer[] layoutToIndexArray()
    {
        int size = getLayoutHeight() * getLayoutWidth();
        Integer[] indexes = new Integer[size];
        for (int i = 0; i < getLayoutHeight(); ++i)
        {
            for (int j = 0; j < getLayoutWidth(); ++j)
            {
                indexes[j + i * getLayoutWidth()] = layoutToIndex(getLayoutTile(j, i));
            }
        }
        return indexes;
    }

    public int layoutToIndex(String tile)
    {
        int value = 100;
        /*switch (tile)
        {
            case ("21") : value = 1;
                break;
            case ("31") : value = 2;
                break;
            case ("41") : value = 3;
                break;
            case ("12") : value = 4;
                break;
            case ("22") : value = 5;
                break;
            case ("32") : value = 6;
                break;
            case ("42") : value = 7;
                break;
            case ("13") : value = 8;
                break;
            case ("23") : value = 9;
                break;
            case ("33") : value = 10;
                break;
            case ("43") : value = 11;
                break;
            case ("14") : value = 12;
                break;
            case ("24") : value = 13;
                break;
            case ("34") : value = 14;
                break;
            case ("44") : value = 15;
                break;
            default: value = 100;
                break;
        }
        */
        try
        {
            if (!tile.equals("empty")) {
                int[] split = new int[2];
                for (int i = 0; i < split.length; ++i) {
                    split[i] = Character.getNumericValue(tile.charAt(i));
                }
                value = split[0] - 1 + ((split[1] - 1) * getLayoutWidth());
            }
            else
                value = 100;
        }
        catch(Exception e)
        {
            //cant parse int, most likely empty block.
            value = 100;
        }
        return value;
    }

    public String indexToLayout(int index)
    {
        String layout = "empty";
        switch (index) {
            case(1): layout = "21";
                break;
            case (2) : layout = "31";
                break;
            case (3): layout = "12";
                break;
            case (4): layout = "22";
                break;
            case(5): layout = "32";
                break;
            case(6): layout = "13";
                break;
            case (7): layout = "23";
                break;
            case (8):layout = "33";
                break;
            case (9): layout = "14";
                break;
            case (10): layout = "24";
                break;
            case (11): layout = "34";
                break;
                default: break;
        }
        return layout;
    }

    public Integer[] getWinningCondition()
    {
        int w = getLayoutWidth();
        int h = getLayoutHeight();
        Integer[] win = new Integer[w*h];
        win[0] = 100;
        for (int i = 1; i < w * h; ++i)
        {
            win[i] = i;
        }
        return win;
    }

    public int getHighscore() {return highScore;}

    public void setHighScore(int pHs){highScore = pHs;}

    public static Puzzle getPuzzle(Context context,int id)
    {
        SQLiteDatabase db = new PuzzleDBHelper(context).getReadableDatabase();

        String[] projection = {
                PuzzleDBContract.PuzzleEntry._ID,
                PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME,
                PuzzleDBContract.PuzzleEntry.COLUMN_NAME_PICTURESET,
                PuzzleDBContract.PuzzleEntry.COLUMN_NAME_BASE_LAYOUT,
                PuzzleDBContract.PuzzleEntry.COLUMN_NAME_LAYOUT,
                PuzzleDBContract.PuzzleEntry.COLUMN_NAME_HIGHSCORE
        };



        Cursor c = db.query(
                PuzzleDBContract.PuzzleEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        ArrayList puzzleList = new ArrayList<Puzzle>();

        c.moveToFirst();
        boolean found = false;
        Puzzle p = null;

        while (c.moveToNext() && !found) {
            int ID = c.getInt(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry._ID));
            String name = c.getString(c.getColumnIndexOrThrow(PuzzleDBContract.
                    PuzzleEntry.COLUMN_NAME_NAME));
            String PicSet = c.getString(c.getColumnIndexOrThrow(PuzzleDBContract.
                    PuzzleEntry.COLUMN_NAME_PICTURESET));
            String baseLayout = c.getString(c.getColumnIndexOrThrow(PuzzleDBContract.
                    PuzzleEntry.COLUMN_NAME_BASE_LAYOUT));
            String layout = c.getString(c.getColumnIndexOrThrow(PuzzleDBContract
                    .PuzzleEntry.COLUMN_NAME_LAYOUT));
            int highscore = c.getInt(c.getColumnIndexOrThrow(PuzzleDBContract.PuzzleEntry
                    .COLUMN_NAME_HIGHSCORE));
            if (ID == id) {
                found = true;
                p = new Puzzle(ID, name, PicSet, baseLayout, layout, highscore);
            }
        }
        c.close();
        return p;
    }

    public void savePuzzleToDB(Context context)
    {
        PuzzleDBHelper pDbHelper = new PuzzleDBHelper(context);
        ContentValues values = new ContentValues();
        SQLiteDatabase db = pDbHelper.getWritableDatabase();

        try {
            values.put(PuzzleDBContract.PuzzleEntry._ID, getID());
            values.put(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME, getName());
            values.put(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_PICTURESET, getPictureSet());
            values.put(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_BASE_LAYOUT, baseLayout);
            values.put(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_LAYOUT, layout);
            values.put(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_HIGHSCORE, highScore);
            db.insertWithOnConflict(PuzzleDBContract.PuzzleEntry.TABLE_NAME,
                    null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
        catch (Exception e)
        {
            Log.i("test", e.getMessage());
        }
        db.close();
    }
}
