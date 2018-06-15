package mobile.labs.acw;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;


public class puzzleAdapter extends ArrayAdapter<Puzzle>{
    public puzzleAdapter (Context pContext, int pTextViewResourceId, ArrayList<Puzzle> pItems){
        super(pContext, pTextViewResourceId, pItems);
    }


}
