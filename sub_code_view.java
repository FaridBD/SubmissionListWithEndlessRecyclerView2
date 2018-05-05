package com.example.farid.submissionlistwithendlessrecyclerview;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import thereisnospon.codeview.CodeView;
import thereisnospon.codeview.CodeViewTheme;

public class sub_code_view extends AppCompatActivity {

    CodeView codeView;
    TextView problem_name_on_codeview, author;
    String CODE = null, link = null, judge = null;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_code_view);

        codeView= findViewById(R.id.code_view);
        problem_name_on_codeview = findViewById(R.id.problem_name_on_codeview);
        author = findViewById(R.id.author);

        codeView.setTheme(CodeViewTheme.ANDROIDSTUDIO).fillColor();
        link = getIntent().getStringExtra("link");
        judge = getIntent().getStringExtra("judge");
        problem_name_on_codeview.setText(getIntent().getStringExtra("problem_name"));
        author.setText(getIntent().getStringExtra("handle"));

        new BackgroundCodeParsing(this).execute();
    }

    public class BackgroundCodeParsing extends AsyncTask<Void, Void, Void>{

        ProgressDialog dialog;
        Context mContext;

        BackgroundCodeParsing(Context activity) {
            dialog = new ProgressDialog(activity);
            mContext = activity;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Loading...");
            dialog.setMessage("Please wait.");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            if(CODE == null) {
                Toast.makeText(mContext,"Something went wrong", Toast.LENGTH_SHORT).show();
            } else codeView.showCode(CODE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            CODE = give_me_code(link, judge);
            return null;
        }
    };

    public String give_me_code(String link, String judge) {
        if(judge == "codeforces") {
            try {
                Document doc = Jsoup.connect(link).get();
                String code = doc.getElementsByClass("prettyprint lang-cpp program-source").text();
                return code;

            } catch(Exception e){
                return null;
            }
        } else {
            try {
                System.out.println(link);
                Document doc = Jsoup.connect(link).get();
                String code = doc.text();
                return code;

            } catch(Exception e) {
                return null;
            }
        }
    }
}
