package com.example.farid.submissionlistwithendlessrecyclerview;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class sub_recyclerview extends AppCompatActivity {

    public List<submission_activity> Mix = new ArrayList<>(), Codeforces = new ArrayList<>(), Codechef = new ArrayList<>(), data = new ArrayList<>();
    public int pos = 0, len = 0;
    private BottomSheetBehavior bottomSheetBehavior;
    LinearLayout problem_statement, source_code, bottom_sheet_layout;
    RecyclerView recyclerView;
    private EditText Handle;
    private Button mix, codechef, codeforces;
    private String handle;
    ProgressBar progressBar;
    RecyclerView.LayoutManager layoutManager;
    sub_recycler_adapter myAdapter;
    Context mContext;
    Boolean isScrolling = false;
    int type, currentItems, totalItems, scrollOutItems;

    int ye = 2018, ind = 0; // For Codechef
    int page = 1, index = 1; // For Codeforces


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_recyclerview);

        Handle = findViewById(R.id.handle);
        mix = findViewById(R.id.mix);
        codechef = findViewById(R.id.codechef);
        codeforces = findViewById(R.id.codeforces);
        progressBar = findViewById(R.id.bottom_progress_bar);
        problem_statement = findViewById(R.id.problem_view);
        source_code = findViewById(R.id.code_view);
        recyclerView = findViewById(R.id.sub_recycler_view);
        bottom_sheet_layout = findViewById(R.id.problem_code_bottom_sheet);

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.problem_code_bottom_sheet));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        handle = getIntent().getStringExtra("username");
        type = getIntent().getIntExtra("type", 0);

        progressBar.getIndeterminateDrawable().setColorFilter(R.color.colorPrimaryDark, android.graphics.PorterDuff.Mode.MULTIPLY);
        mContext = sub_recyclerview.this;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = recyclerView.getLayoutManager().getChildCount();
                totalItems = recyclerView.getLayoutManager().getItemCount();
                scrollOutItems = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();;

                if(isScrolling && (currentItems + scrollOutItems == totalItems) && (dy > 0)) { // dy > 0 means scrolling down
                    isScrolling = false;
                    new BackgroundParsing(sub_recyclerview.this, 2, type).execute();
                }
            }
        });

        if(type == 1) { // Mix
            myAdapter = new sub_recycler_adapter(mContext, Handle, Mix, bottomSheetBehavior, problem_statement, source_code, bottom_sheet_layout);
            layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(myAdapter);

            new BackgroundParsing(sub_recyclerview.this, 1, 1).execute();
        } else if(type == 2) { // Codeforces
            myAdapter = new sub_recycler_adapter(mContext, Handle, Codeforces, bottomSheetBehavior, problem_statement, source_code, bottom_sheet_layout);
            layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(myAdapter);

            new BackgroundParsing(sub_recyclerview.this, 1, 2).execute();
        } else if(type == 3) { // Codechef
            myAdapter = new sub_recycler_adapter(mContext, Handle, Codechef, bottomSheetBehavior, problem_statement, source_code, bottom_sheet_layout);
            layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(new LinearLayoutManager(sub_recyclerview.this));
            recyclerView.setAdapter(myAdapter);

            new BackgroundParsing(sub_recyclerview.this, 1, 3).execute();
        }
    }

    public class BackgroundParsing extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;
        Context mContext;
        int progress_type, len, judge_type;

        BackgroundParsing(Context activity, int progress_type, int judge_type) {
            dialog = new ProgressDialog(activity);
            mContext = activity;
            this.progress_type = progress_type;
        }

        @Override
        protected void onPreExecute() {
            if(progress_type == 1) {
                dialog.setTitle("Loading...");
                dialog.setMessage("Please wait.");
                dialog.show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
            progressBar.setVisibility(View.GONE);

            if(judge_type == type && Codechef.size() == 0) Toast.makeText(mContext, "May be Codechef server is down! Try agin Later", Toast.LENGTH_SHORT).show();
            if(judge_type == type && Codeforces.size() == 0) Toast.makeText(mContext, "May be Codeforces server is down! Try agin Later", Toast.LENGTH_SHORT).show();

            if(judge_type == type && len == Codechef.size()) Toast.makeText(mContext, "Seems like all activities are already loaded!", Toast.LENGTH_SHORT).show();
            if(judge_type == type && len == Codeforces.size()) Toast.makeText(mContext, "Seems like all activities are already loaded!", Toast.LENGTH_SHORT).show();

            bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.problem_code_bottom_sheet));
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            myAdapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(type == 1) {
                // Code for Mix
            } else if(type == 2) {
                len = Codeforces.size();
                make_for_codeforces();
            } else if(type == 3) {
                len = Codechef.size();
                make_for_codechef();
            }
            return null;
        }
    }

    public void make_for_codeforces() {
        try {
            int limit = Integer.valueOf(Jsoup.connect("http://codeforces.com/submissions/"+handle+"/page/1").get().getElementsByClass("pagination").select("ul").select("li").get(5).text());
            int val = 0;

            for(int i=1; i<=limit ; i++) {

                String url = "http://codeforces.com/submissions/"+handle+"/page/"+i;
                Document doc = (Document) Jsoup.connect(url).get();
                Elements el = doc.getElementsByClass("status-frame-datatable").select("tbody").select("tr");
                //System.out.println(el.size());

                String solution_id, solution_time,  problem_name, solution_status,  problem_link, solution_link, judge, soulution_language, solution_execution_time, usage_memory, problem_difficulty;

                for(int j= (page == i) ? index : 1; j<el.size(); j++) {
                    page = i;
                    index = j+1;

                    submission_activity store = new submission_activity();
                    Elements tmp = el.get(j).select("td");
                    String s[] = tmp.text().toString().split(" ");

                    store.solution_id = s[0];
                    store.solution_time = s[2]+" "+s[1];
                    store.problem_name = tmp.get(3).select("a").text();
                    store.solution_status = tmp.get(5).text();
                    store.problem_link = tmp.get(3).select("a").attr("abs:href");
                    store.solution_link = store.problem_link.substring(0,store.problem_link.indexOf("problem"))+"submission/"+tmp.get(0).text() ;
                    store.judge = "codeforces";
                    store.soulution_language = tmp.get(4).text();
                    store.solution_execution_time = tmp.get(6).text();
                    store.usage_memory = tmp.get(7).text();
                    store.problem_difficulty = "Entry oise na";

                    if(type == 2) Codeforces.add(store);
                    val++;
                    if(val >= 20) break;
                }
                if(val >= 20) break;
            }
        } catch (Exception e) {}
    }
    public void make_for_codechef() {
        /*new Thread(new Runnable() {
            @Override
            public void run() {*/
                try {
                    int val = 0;
                    for(int y=ye; ; y--) {
                        String year = Integer.toString(y);

                        String url = "https://www.codechef.com/submissions?sort_by=All&sorting_order=asc&language=All&status=All&year="+year+"&handle="+handle+"&pcode=&ccode=&Submit=GO";
                        Document doc = (Document) Jsoup.connect(url).get();

                        Elements el = doc.getElementsByClass("dataTable").select("tbody").select("tr");

                        for(int i = (ye == y) ? ind: 0; i<el.size(); i++) {
                            ye = y;
                            ind = i+1;
                            val++;

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

                            if(type == 3) Codechef.add( new submission_activity(solution_id, solution_time, problem_code, solution_status, problem_link, solution_link, "codechef", solution_language, solution_execution_time, usage_memory, problem_difficulty)) ;

                            if(val >= 20) break;
                        }
                        if(val >= 20) break;
                    }
                } catch(Exception e) {
                    System.out.println("Catch ");

                }
           /* }
        }).start();*/
    }
}
