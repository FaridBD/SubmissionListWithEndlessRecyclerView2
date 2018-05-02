package com.example.farid.submissionlistwithendlessrecyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    private EditText Handle;
    private Button mix, codechef, codeforces;
    private String handle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        codechef = findViewById(R.id.codechef);
        codeforces = findViewById(R.id.codeforces);
        mix = findViewById(R.id.mix);
        final EditText handle = findViewById(R.id.handle);

        codechef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, sub_recyclerview.class);
                intent.putExtra("type", 3);
                intent.putExtra("username", handle.getText().toString());
                startActivity(intent);
            }
        });
        codeforces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, sub_recyclerview.class);
                intent.putExtra("type", 2);
                intent.putExtra("username", handle.getText().toString());
                startActivity(intent);
            }
        });
        mix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, sub_recyclerview.class);
                intent.putExtra("type", 1);
                intent.putExtra("username", handle.getText().toString());
                startActivity(intent);
            }
        });
    }

}
