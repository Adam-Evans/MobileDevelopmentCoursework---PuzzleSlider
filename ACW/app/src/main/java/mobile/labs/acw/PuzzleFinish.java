package mobile.labs.acw;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class PuzzleFinish extends AppCompatActivity {

    private String pName;
    private int id;
    private int score;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_finish);
       setup();
    }

    private  void setup()
    {
        //set text views
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        id = extras.getInt("id", 0);
        score = extras.getInt("score", 0);
        pName = extras.getString("puzzleName");
        int Highscore = extras.getInt("highScore", 0);
        String text;
        TextView tv = (TextView)findViewById(R.id.win_Puzzle_Name_Text);
        tv.setText(pName);
        tv = (TextView)findViewById(R.id.win_YourScore_Text);
        text = getResources().getString(R.string.your_score)  + ": " + String.valueOf(score);
        tv.setText(text);
        tv = (TextView)findViewById(R.id.win_HighScore_Text);
        text = getResources().getString(R.string.highscore)  + ": " + String.valueOf(Highscore);
        tv.setText(text);

        if (score >= Highscore)
        {
            //update highscore for the puzzle
            Toast toast = Toast.makeText(this,
                    "New High Score, Congratulations!", Toast.LENGTH_SHORT);
            toast.show();
            Puzzle puzzle = Puzzle.getPuzzle(this, id);
            puzzle.setHighScore(score);
        }

        //set buttons
        Button b = (Button)findViewById(R.id.win_RetryButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent data = new Intent();
                    data.putExtra("restart", true);
                    data.putExtra("id", id);
                    setResult(RESULT_OK, data);
                    finish();
            }
        });
        b = (Button)findViewById(R.id.Win_MainMenu_Button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);

                finish();
            }
        });
    }
}
