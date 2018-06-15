package mobile.labs.acw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class listAllPuzzlesActivity extends AppCompatActivity {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Download Puzzles");
        setContentView(R.layout.activity_list_all_puzzles);
        new downloadJSONIndex()
                .execute("http://www.simongrey.net/08027/slidingPuzzleAcw/index.json");

    }

    private class downloadJSONIndex extends AsyncTask<String, String, String> {

        @Override
        protected  String doInBackground(String...args) {
            String result = "";
            String[] indices = new String[1];
            try {
                InputStream stream = (InputStream)new URL(args[0]).getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line = "";
                while(line != null){
                    line = reader.readLine();
                    result += line;
                }
                JSONObject json = new JSONObject(result);
                final JSONArray puzzleIndexes = json.getJSONArray("PuzzleIndex");
                //ContentValues values = new ContentValues();
                indices = new String[puzzleIndexes.length()];
                for (int i = 0; i < puzzleIndexes.length(); ++i)
                {
                    indices[i] = puzzleIndexes.getString(i);
                }
                final String[] fIndices = indices;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                                android.R.layout.simple_list_item_1, fIndices);
                        ListView l = (ListView)findViewById(R.id.indexListView);
                        l.setAdapter(adapter);
                        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent data = new Intent(context, PuzzleActivity.class);
                                data.putExtra("index", position + 1);
                                setResult(Activity.RESULT_OK, data);
                                finish();
                            }
                        });
                    }
                });



            }catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }
    }
}
