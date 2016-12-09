package afengg.oneaday;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        int counter = (int) getIntent().getExtras().get("counter");
        TextView counterDisplay = (TextView) findViewById(R.id.counter_display);
        counterDisplay.setText(Integer.toString(counter));
    }
}
