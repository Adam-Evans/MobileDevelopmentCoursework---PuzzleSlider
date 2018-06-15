package mobile.labs.acw;

import android.provider.BaseColumns;

public final class PuzzleDBContract {
    public PuzzleDBContract(){}

    public static abstract class PuzzleEntry implements BaseColumns {
        public static final String TABLE_NAME = "Puzzle";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_PICTURESET = "PictureSet";
        public static final String COLUMN_NAME_BASE_LAYOUT = "BaseLayout";
        public static final String COLUMN_NAME_LAYOUT = "Layout";
        public static final String COLUMN_NAME_HIGHSCORE = "Highscore";
        public static final String _ID = "_ID";
    }

    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";
    public static final String PUZZLE_INDEX_URL
            = "http://www.simongrey.net/08027/slidingPuzzleAcw/index.json";
    public static final String PUZZLE_URL
            = "http://www.simongrey.net/08027/slidingPuzzleAcw/puzzles/"; // + puzzlexx.json
    public static final String LAYOUT_URL
            = "http://www.simongrey.net/08027/slidingPuzzleAcw/layouts/"; // + layoutxx.json
    public static final String PICTURE_URL
            = "http://www.simongrey.net/08027/slidingPuzzleAcw/images/"; // + <pictureset> eg. lemon

    public static final String SQL_CREATE_PUZZLE_TABLE = "CREATE TABLE " + PuzzleEntry.TABLE_NAME +
            " (" + PuzzleEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
            PuzzleEntry.COLUMN_NAME_NAME + COMMA_SEP + PuzzleEntry.COLUMN_NAME_PICTURESET
            + COMMA_SEP + PuzzleEntry.COLUMN_NAME_BASE_LAYOUT + COMMA_SEP +
            PuzzleEntry.COLUMN_NAME_LAYOUT + COMMA_SEP + PuzzleEntry.COLUMN_NAME_HIGHSCORE + " )";

    public static final String SQL_DELETE_PUZZLE_TABLE = "DROP TABLE IF EXISTS " +
            PuzzleEntry.TABLE_NAME;
}
