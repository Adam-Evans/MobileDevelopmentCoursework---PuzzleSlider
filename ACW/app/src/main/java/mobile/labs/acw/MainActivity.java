package mobile.labs.acw;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    PuzzleDBHelper mDbHelper;
    Context context = this;
    static final int PUZZLE_SELECT_ACTIVITY = 1;
    static final int PUZZLE_SELECT_PLAY = 2;
    static final int PUZZLE_PLAY = 3;
    static final int PUZZLE_HIGHSCORES = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbHelper = new PuzzleDBHelper(context);
        setTitle("Main Menu");
    }



    public void onClickPuzzleSelect(View pView)
    {
        Intent intent = new Intent(MainActivity.this, listAllPuzzlesActivity.class);
        startActivityForResult(intent, PUZZLE_SELECT_ACTIVITY);
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case(PUZZLE_SELECT_ACTIVITY) :
                if (resultCode == Activity.RESULT_OK)
                {
                    context = this;
                    String puzzleName = "puzzle";
                    puzzleName += data.getIntExtra("index",0) + ".json";
                    Log.i("test", "downloading puzzle: " + puzzleName);
                    PuzzleDBContract contract = new PuzzleDBContract();
                    String url = contract.PUZZLE_URL + puzzleName;
                    Log.i("test", "url = " + url);
                    new downloadJSON().execute(url,
                            String.valueOf(data.getIntExtra("index", 0)));

                }
                else if (resultCode == Activity.RESULT_CANCELED)
                {

                }
                break;
            case(PUZZLE_SELECT_PLAY) :
                 if (resultCode == Activity.RESULT_OK)
                 {
                     String id = data.getStringExtra("id");
                     int intID = Integer.parseInt(id);
                     checkPuzzleIndex(intID);
                 }
                 break;
            case(PUZZLE_PLAY):
                if (resultCode == Activity.RESULT_OK)
                {
                    Intent intent = new Intent(this, PuzzleFinish.class);
                    intent.putExtra("puzzleName", data.getStringExtra("puzzleName"));
                    intent.putExtra("score", data.getIntExtra("score", 0));
                    intent.putExtra("highScore",
                            data.getIntExtra("highScore", 0));
                    intent.putExtra("id", data.getIntExtra("id", 0));
                    startActivityForResult(intent, PUZZLE_HIGHSCORES);
                }
                break;
            case(PUZZLE_HIGHSCORES):
                if (resultCode == Activity.RESULT_OK)
                {
                    if (data.getBooleanExtra("restart", false))
                    {
                        int id = data.getIntExtra("id", 0);
                        checkPuzzleIndex(id);
                    }
                    else
                    {

                    }
                }
            default: break;
        }
    }

    public void onButtonPressOfflinePuzzle(View pView)
    {
        Intent intent = new Intent(this, OfflinePuzzles.class);
        startActivityForResult(intent, PUZZLE_SELECT_PLAY);
    }

    public void checkPuzzleIndex(int index)
    {
        Intent intent = new Intent(MainActivity.this, PuzzleActivity.class);
        intent.putExtra("id", index);
        startActivityForResult(intent, PUZZLE_PLAY);
    }

    private class downloadJSON extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String...args) {
            String result = "";
            PuzzleDBContract contract = new PuzzleDBContract();
            try {
                InputStream stream = (InputStream) new URL(args[0]).getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                String line = "";

                ContentValues values = new ContentValues();

                while (line != null) {
                    line = reader.readLine();
                    result += line;
                }

                JSONObject json = new JSONObject(result);
                final String PicSet = json.getString("PictureSet");
                String layout = json.getString("layout");

                File file = new File(getApplicationContext().getFilesDir(), "layouts/"
                        + layout);

                int tempIndex = Integer.parseInt(args[1]);
                Puzzle tempPuzzle = Puzzle.getPuzzle(context, tempIndex);
                if (tempPuzzle != null)
                {
                    Log.i("test", "puzzle exists, skip download");
                }
                else
                {

                    try {
                        String url = contract.LAYOUT_URL + layout;
                        stream.close();
                        stream = (InputStream) new URL(url).getContent();
                        reader = new BufferedReader(new InputStreamReader(stream));
                        line = "";
                        result = "";
                        while (line != null) {
                            line = reader.readLine();
                            result += line;
                        }
                        stream.close();
                        String name = args[0].substring(args[0].lastIndexOf('/') + 1);
                        name = name.replaceAll(".json", "");
                        final int id = Integer.parseInt(name.replaceAll("\\D+",
                                ""));
                        final String idString = name.replaceAll("\\D+", "");
                        json = new JSONObject(result);
                        //Next step; download PicSet and baseLayout if they don't exist.
                        //check file for layout, if not download it
                        url = contract.LAYOUT_URL + layout;
                        stream = (InputStream) new URL(url).getContent();
                        reader = new BufferedReader(new InputStreamReader(stream));
                        line = "";
                        result = "";
                        while (line != null) {
                            line = reader.readLine();
                            result += line;
                        }
                        //TODO look into this section, some puzzles missing last column after formatting
                        Pattern p = Pattern.compile("\"([^\"]*)\"");
                        Matcher m = p.matcher(result);
                        result = result.substring(12);
                        String[] layoutSplit = result.split(Pattern.quote("],["));
                        layoutSplit[layoutSplit.length - 1] =
                                layoutSplit[layoutSplit.length - 1].substring(0,
                                        layoutSplit[layoutSplit.length - 1].length() - 7);
                        layout = "";
                        for (int i = 0; i < layoutSplit.length; ++i) {
                            layout += layoutSplit[i];
                            if (i < layoutSplit.length)
                                layout += "|";
                        }
                        //layout downloaded and parsed, we can use this to find picSets
                        String picPath = getApplicationInfo().dataDir + "/" + PicSet;
                        file = new File(picPath);
                        if (file.exists()) {
                            //load file if it exists otherwise download...
                            Log.i("test", "picset " + PicSet + " already exists");
                        } else {
                            Log.i("test", "picset " + PicSet
                                    + " doesn't exist, downloading");
                            int s = layoutSplit[0].length(); // split length
                            for (int i = 0; i < layoutSplit.length; ++i) {
                                String[] splitSplit = layoutSplit[i].split(",");
                                String path = "";
                                for (int j = 0; j < splitSplit.length; ++j) {
                                    if (!splitSplit[j].equals("empty")) {
                                        url = PuzzleDBContract.PICTURE_URL + PicSet + "/"
                                                + splitSplit[j];
                                        url = url.replaceAll("\"", "");
                                        path = PicSet + "/" + splitSplit[j].replaceAll("\"",
                                                "") + ".jpg";
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                            }
                                        });
                                        downloadImage(url, path);
                                    }
                                }
                            }
                        }
                        layoutSplit = layout.split(Pattern.quote("|"));

                        values.put(PuzzleDBContract.PuzzleEntry._ID, id);
                        values.put(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_NAME, name);
                        values.put(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_PICTURESET, PicSet);
                        values.put(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_BASE_LAYOUT, layout);
                        values.put(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_LAYOUT, layout);
                        values.put(PuzzleDBContract.PuzzleEntry.COLUMN_NAME_HIGHSCORE, 0);
                        db.insertWithOnConflict(PuzzleDBContract.PuzzleEntry.TABLE_NAME,
                                null, values, SQLiteDatabase.CONFLICT_REPLACE);
                        stream.close();
                        db.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("test", e.getMessage());
                    }
                }


                //String[] rows = result.split("|"); Do this later on launch. easier to handle.


            }catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }

        private void downloadImage(String url, String path)
        {
            Bitmap bitmap = null;
            try {
                FileInputStream reader = new FileInputStream(new File(path));
                bitmap = BitmapFactory.decodeStream(reader);
            } catch (FileNotFoundException fileNotFound) {
            try{

                bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                //FileOutputStream writer = null;
                try {
                    /*
                    String[] splitPath = path.split("/");
                    File dir = new File(splitPath[0]);
                    dir.mkdirs();


                    FileOutputStream fos = new FileOutputStream(new File(path), true);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    */

                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                    FileOutputStream writer = null;
                    try
                    {
                        String[] splitPath = path.split("/");

                        //writer = getApplicationContext().openFileOutput(path, Context.MODE_PRIVATE);
                        String pPath = getApplicationInfo().dataDir;
                        File file = new File(pPath + "/" + splitPath[0]);
                        file.mkdirs();
                        file = new File(pPath + "/" + path);
                        writer = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, writer);
                        writer.flush();
                        writer.close();
                    }

                    catch (Exception e)
                    {
                        Log.i("My Error", e.getMessage());
                    }
                    finally {
                        writer.close();
                    }
                } catch (Exception e){
                    Log.i("test", e.toString());
                } finally {
                }
            } catch (Exception e) {

            }
        }

        }
    }



}
