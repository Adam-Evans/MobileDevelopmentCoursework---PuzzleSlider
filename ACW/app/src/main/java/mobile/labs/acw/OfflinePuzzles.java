package mobile.labs.acw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class OfflinePuzzles extends AppCompatActivity {

    private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_puzzles);

        setTitle("Play Puzzle");

        SQLiteDatabase db = new PuzzleDBHelper(this).getReadableDatabase();

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

        while (c.moveToNext()) {
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
            puzzleList.add(new Puzzle(ID, name, PicSet, baseLayout, layout, highscore));
        }

        c.close();

        puzzleAdapter adapter = new puzzleAdapter(this,
                android.R.layout.simple_list_item_1, puzzleList);
        final ListView puzzleListView = (ListView) findViewById(R.id.indexListView);
        puzzleListView.setAdapter(adapter);
        puzzleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String actualPos = puzzleListView.getItemAtPosition(position).toString();
                String[] split = actualPos.split(" ");
                actualPos = split[0];
                actualPos = actualPos.replaceAll("\\D+", "");
                Log.i("test", "actual pos - loading = " + actualPos);
                Intent data = new Intent(context, PuzzleActivity.class);
                data.putExtra("id",  actualPos);
                data.putExtra("name", "puzzle" + actualPos);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });
    }
}
