package mobile.labs.acw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class PuzzleActivity extends AppCompatActivity {

    private GridView imageGrid;
    private TextView movesText;
    private TextView timerText;
    private ArrayList<Bitmap> puzzlePieces;
    private int moves = 0;
    private int imageWidth = 0;
    private float timeTaken = 0;
    private Integer[] tileIndexes;
    private Integer[] movable = new Integer[4];
    private long timeStarted = 0;
    private Context context = this;
    private Handler timerHandle = new Handler();
    private Runnable timerRun;
    private static Puzzle puzzle = null;
    private static String DB_Name = "Puzzles.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        Intent intent = getIntent();
        Button resetButton = (Button)findViewById(R.id.Retry);
        timerText = (TextView)findViewById(R.id.Time);

        timerRun = new Runnable() {
            @Override
            public void run() {
                long mil = System.currentTimeMillis() - timeStarted;
                int seconds = (int) (mil / 1000);
                int mins = seconds / 60;
                seconds = seconds % 60;
                timeTaken = (int) (mil / 100);
                String text = String.format("Time: %1d:%2$02d" , mins, seconds);
                timerText.setText(text);
                timerHandle.postDelayed(this, 100);
            }
        };

        timeStarted = System.currentTimeMillis();
        timerHandle.postDelayed(timerRun, 0);

        setupPuzzle();
        movesText = (TextView)findViewById(R.id.MovesCounter);
        String tempText = getResources().getString(R.string.moves)
                + ": " + String.valueOf(moves);
        movesText.setText(tempText);
        imageGrid = (GridView) findViewById(R.id.gridView);
        imageGrid.setNumColumns(puzzle.getLayoutWidth());
        resetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                resetPuzzle();
                Toast toast = Toast.makeText(context, "BaddaBing", Toast.LENGTH_SHORT);
                toast.show();

            }
        });
        imageGrid.setAdapter(new ImageAdapter(context, puzzlePieces, imageWidth));
        imageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Arrays.asList(movable).contains(position))
                {
                    final int emptyIndex = Arrays.asList(tileIndexes).indexOf(100);
                    Bitmap temp = puzzlePieces.get(emptyIndex);
                    puzzlePieces.set(emptyIndex, puzzlePieces.get(position));
                    puzzlePieces.set(position, temp);
                    Integer tempIndex = tileIndexes[emptyIndex];
                    tileIndexes[emptyIndex] = tileIndexes[position];
                    tileIndexes[position] = tempIndex;
                    imageGrid.invalidateViews();
                    moves++;
                    String tempText = getResources().getString(R.string.moves)
                            + ": " + String.valueOf(moves);
                    movesText.setText(tempText);
                    updateMovable();
                    if (Arrays.equals(tileIndexes, puzzle.getWinningCondition()))
                    {
                        //Toast toast = Toast.makeText(context, "Winner!", Toast.LENGTH_SHORT);
                        //toast.show();
                        winEvent();
                    }
                }
            }
        });
    }

    private void updateMovable()
    {
        //find empty index in tile list, it's value is 0.
        final int emptyIndex = Arrays.asList(tileIndexes).indexOf(100);
        int width = emptyIndex; // position of empty in row
        final int actualWidth = puzzle.getLayoutWidth(); // width of puzzle
        while (width >= actualWidth)
        {
            width -= actualWidth;
        }
        int col = emptyIndex;
        int vertNeighbours = 2;
        if (col + 1 + actualWidth > actualWidth * puzzle.getLayoutHeight()) // +1 for index (base 0)
            vertNeighbours = 1;
        if (col + 1 - actualWidth < 0)
            vertNeighbours = -1;
        //find left and right neighbours, if they exist, if not set -1
        //For movable, index: 0=up, 1=right, 2=down, 3=left.
        setMovable(emptyIndex, width, vertNeighbours,actualWidth);
    }

    private void setMovable(int index, int rowPos, int vertNeighbours, int puzzleWidth)
    {
        //For movable, index: 0=up, 1=right, 2=down, 3=left.
        movable[1] = index + 1;
        movable[3] = index - 1;
        if (rowPos == 0)
            movable[3] = -1;
        if (rowPos == puzzleWidth)
            movable[1] = -1;
        // for vertNeighbours, 2 = up and down, 1 = up, -1 = down
        if (vertNeighbours == 1)
        {
            movable[0] = index - puzzleWidth;
            movable[2] = -1;
        }
        if (vertNeighbours == -1)
        {
            movable[0] = -1;
            movable[2] = index + puzzleWidth;
        } else
        {
            movable[0] = index - puzzleWidth;
            movable[2] = index + puzzleWidth;
        }
    }



    private void setupPuzzle() {
        Bundle extras = getIntent().getExtras();
        String id = extras.getString("id");
        int intID = extras.getInt("id", 0);
        String name = getIntent().getStringExtra("name");
        puzzle = Puzzle.getPuzzle(context, intID);
        setTitle(puzzle.getName());
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.relativeLayout);
        switch(puzzle.getPictureSet())
        {
            case("apple"):
                rl.setBackgroundColor(ContextCompat.getColor(context, R.color.apple));
                break;
            case("banana"):
                rl.setBackgroundColor(ContextCompat.getColor(context, R.color.banana));
                break;
            case("grapes"):
                rl.setBackgroundColor(ContextCompat.getColor(context, R.color.grapes));
                break;
            case("lemon"):
                rl.setBackgroundColor(ContextCompat.getColor(context, R.color.lemon));
                break;
            case("orange"):
                rl.setBackgroundColor(ContextCompat.getColor(context, R.color.orange));
                break;
            case("pear"):
                rl.setBackgroundColor(ContextCompat.getColor(context, R.color.pear));
                break;
            case("raspberry"):
                rl.setBackgroundColor(ContextCompat.getColor(context, R.color.raspberry));
                break;
            case("strawberry"):
                rl.setBackgroundColor(ContextCompat.getColor(context, R.color.strawberry));
                break;
            default: break;
        }
        tileIndexes = puzzle.layoutToIndexArray(); // tiles are set a value based on winning pos
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Button b = (Button)findViewById(R.id.Retry);
        int height = dm.heightPixels - getStatusBarHeight() - 100 - b.getMeasuredHeight();
        int width = dm.widthPixels;
        if (height / puzzle.getLayoutHeight() > width / puzzle.getLayoutWidth())
            imageWidth = width / puzzle.getLayoutWidth();
        else
            imageWidth = height / puzzle.getLayoutHeight();
        puzzlePieces = new ArrayList<Bitmap>();
        try {
            for (int i = 0; i < puzzle.getLayoutHeight(); ++i)
            {
                for (int j = 0; j < puzzle.getLayoutWidth(); ++j)
                {
                    String path = getApplicationInfo().dataDir + "/"
                            + puzzle.getPictureSet() + "/"
                            + puzzle.getLayoutTile(j, i) + ".jpg";
                    if (puzzle.getLayoutTile(j, i).equals("empty")) {
                        puzzlePieces.add(Bitmap.createBitmap(imageWidth,
                                imageWidth, Bitmap.Config.ARGB_8888));
                    }
                    else
                        puzzlePieces.add(fileImageToBitmap(path));
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        updateMovable();
    }

    public void startTimer()
    {

    }

    public void resetPuzzle()
    {
        puzzle.setLayout(puzzle.getBaseLayout());
        setupPuzzle();
        imageGrid.setAdapter(new ImageAdapter(context, puzzlePieces, imageWidth));
        imageGrid.invalidateViews();
    }

    private void winEvent()
    {

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        int intID = extras.getInt("id", 0);
        Intent data = new Intent();
        data.putExtra("score", calculateScore());
        data.putExtra("highScore", puzzle.getHighscore());
        data.putExtra("id", intID);
        data.putExtra("puzzleName", puzzle.getName());
        setResult(Activity.RESULT_OK, data);
        puzzle.savePuzzleToDB(context);
        finish();
    }

    private int calculateScore()
    {
        int score = 100000;
        score -= (int)(moves * (500 + (125 * moves))) * (timeTaken / (250 + (10 * moves)));
        if (score > puzzle.getHighscore()) {
            Log.i("test", "highscore = " + puzzle.getHighscore());
            puzzle.setHighScore(score);
        }
        return score;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        int navId = getResources().getIdentifier("navigation_bar_height",
                "dimen", "android");
        if (navId > 0)
                result += getResources().getDimensionPixelSize(navId);
        return result;
    }

    private Bitmap urlImageToBitmap(String imageUrl) throws Exception {
        Bitmap result = null;
        URL url = new URL(imageUrl);
        if(url != null) {
            result = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        }
        return result;
    }

    private Bitmap fileImageToBitmap(String filePath) throws Exception {
        File imgFile = new File(filePath);
        Bitmap myBitmap = null;
        if(imgFile.exists()){
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return myBitmap;
    }


}
