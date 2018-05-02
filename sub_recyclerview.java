package com.example.farid.submissionlistwithendlessrecyclerview;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class sub_recyclerview extends AppCompatActivity {

    public List<submission_activity> MIx = new ArrayList<>(), Codeforces = new ArrayList<>(), Codechef = new ArrayList<>(), data = new ArrayList<>();
    public int pos = 0, len = 0;
    private BottomSheetBehavior bottomSheetBehavior;
    LinearLayout problem_statement, source_code;
    RecyclerView recyclerView;
    private EditText Handle;
    private Button mix, codechef, codeforces;
    private String handle;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_recyclerview);

        Handle = findViewById(R.id.handle);
        mix = findViewById(R.id.mix);
        codechef = findViewById(R.id.codechef);
        codeforces = findViewById(R.id.codeforces);

        handle = getIntent().getStringExtra("username");

        problem_statement = findViewById(R.id.problem_view);
        source_code = findViewById(R.id.code_view);

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.problem_code_bottom_sheet));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        type = getIntent().getIntExtra("type", 0);
        if(type == 3) {
            BackgroundParsing codechefParsing = new BackgroundParsing(sub_recyclerview.this);
            codechefParsing.execute();
        }
    }
    public class BackgroundParsing extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;
        Context mContext;

        BackgroundParsing(Context activity) {
            dialog = new ProgressDialog(activity);
            mContext = activity;
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle("Loading...");
            dialog.setMessage("Please wait.");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            int val = 0;
            int len = data.size();

            Log.v("app",len + " **********************");
            if(pos >= len) Toast.makeText(mContext, "No more submission's are available", Toast.LENGTH_SHORT).show();
            for(int i=pos; val <= 20 && pos < len; i++){
                Codechef.add(data.get(i));
                pos++; val++;
            }
            dialog.dismiss();
            recyclerView = findViewById(R.id.sub_recycler_view);
            bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.problem_code_bottom_sheet));
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            sub_recycler_adapter myAdapter = new sub_recycler_adapter(mContext, Handle, Codechef, bottomSheetBehavior, problem_statement, source_code);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.setAdapter(myAdapter);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            make_for_codechef();
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void make_for_codechef() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for(int y=2018; ; y--) {
                        String year = Integer.toString(y);
                        String url = "https://www.codechef.com/submissions?sort_by=All&sorting_order=asc&language=All&status=All&year="+year+"&handle="+handle+"&pcode=&ccode=&Submit=GO";
                        Document doc = (Document) Jsoup.connect(url).get();

                        Elements el = doc.getElementsByClass("dataTable").select("tbody").select("tr");

                        for(int i=0; i<el.size(); i++) {

                            String solution_id = el.get(i).select("td").get(0).text();
                            String solution_time = el.get(i).select("td").get(1).text();
                            String problem_link = el.get(i).select("td").get(4).select("a").attr("abs:href").toString();
                            String problem_code = el.get(i).select("td").get(3).select("a").text();
                            String problem_difficulty = el.get(i).select("td").get(4).select("a").text();
                            String solution_status = el.get(i).select("td").get(5).select("div").select("span").first().attr("title").toString();
                            String solution_execution_time = el.get(i).select("td").get(6).text();
                            String usage_memory = el.get(i).select("td").get(7).text();
                            String solution_language = el.get(i).select("td").get(8).text();
                            String solution_link = el.get(i).select("td").get(9).select("a").attr("abs:href").toString();

                            if(solution_status.length() == 0) {
                                String point =  el.get(i).select("td").get(5).select("div").select("span").first().text();
                                StringTokenizer st = new StringTokenizer(point);
                                point = st.nextToken();
                                if(Integer.valueOf(point) == 100) {
                                    solution_status = "accepted";
                                }
                                else solution_status = "partially accepted("+point+"pts)";
                            }
                            if(!solution_status.equals("accepted")) {

                                solution_execution_time = "-";
                                usage_memory = "-";
                            }
                            String tmp = solution_link;
                            int len = solution_link.length();

                            tmp = tmp.replaceFirst("viewsolution", "viewplaintext");

                            //Document doc2 = Jsoup.connect(tmp).get();

                            data.add( new submission_activity(solution_id, solution_time, problem_code, solution_status, problem_link, solution_link, "codechef", solution_language, solution_execution_time, usage_memory, problem_difficulty)) ;
                        }
                    }
                } catch(IndexOutOfBoundsException e) {

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
