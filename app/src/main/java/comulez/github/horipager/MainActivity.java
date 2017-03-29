package comulez.github.horipager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private HoriPager horiPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        horiPager = (HoriPager) findViewById(R.id.hp);
        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < 3; i++) {
            View child = inflater.inflate(R.layout.ll, horiPager, false);
            child.setBackgroundColor(Color.rgb(255 / (i + 1), 255 / (i + 1), 0));
            ListView listView = (ListView) child.findViewById(R.id.listview);
            ArrayList<String> datas = new ArrayList();
            for (int j = 0; j < 20; j++) {
                datas.add(i + "--item---" + j);
            }
            listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datas));
            horiPager.addView(child);
        }
    }
}
