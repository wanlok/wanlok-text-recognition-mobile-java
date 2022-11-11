package wanlok.android.wanlok_text_recognition_mobile_java;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Text Recognition Mobile Server");
        textView = findViewById(R.id.textView);
        try {
            presenter = new MainPresenter(this);
            textView.setText(Utils.getHostAddress().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}