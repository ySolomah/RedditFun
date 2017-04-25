package com.fun.reddit.redditfun;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public class stringClass{
        public String ourString;
        public stringClass()
        {
            ourString = "";
        }
    }

    public class flagClass{
        public boolean flag;
        public flagClass()
        {
            this.flag = false;
        }
    }

    public class myJavaScriptInterface
    {
        @JavascriptInterface
        public void processHTML(String html)
        {
            loadedPage = html;
        }
    }

    String loadedPage;
     stringClass ourString;

    public class DBHelperSaveReddits extends SQLiteOpenHelper{
        public static final String SUBREDDITNAME = "subreddit";
        public static final String DATABASE_NAME = "SubredditsSaved.db";

        public DBHelperSaveReddits(Context context) { super(context, DATABASE_NAME, null, 1); }
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL("create table subreddits " + "(subreddit text)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS subbreddits");
        }

        public boolean insertPost(String subreddit)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("subreddit", subreddit);
            db.insert("subreddits", null, contentValues);
            return (true);
        }

        public boolean deletePost(String id)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            int returned = db.delete("subreddits", "subreddit = ?", new String[] { id });
            if(returned > 0) { return(true); }
            return (false);
        }

        public ArrayList<String> getAllSubreddits()
        {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from subreddits", null);
            res.moveToFirst();
            ArrayList<String> returnList = new ArrayList<>();
            while(res.isAfterLast() == false)
            {
                String add = res.getString(res.getColumnIndex(SUBREDDITNAME));
                returnList.add(add);
            }
            return (returnList);
        }

    }

    public class DBHelper extends SQLiteOpenHelper{
        public static final String DATABASE_NAME = "RedditFun.db";
        public static final String ID = "id";
        public static final String AUTHOR = "author";
        public static final String DATAURL = "dataUrl";
        public static final String BITMAPURL = "bitmapUrl";
        public static final String TIMESTAMP = "timeStamp";
        public static final String SUBREDDIT = "subreddit";
        public static final String COMMENTSURL = "commentsUrl";
        public static final String NUMCOMMENTS = "numComments";
        public static final String SCORE = "score";
        public static final String CONDENSEDSCORE = "condensedScore";
        public static final String TITLE = "title";

        public DBHelper(Context context)
        {
            super(context, DATABASE_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL("create table posts " + "(id text, author text, dataUrl text, bitmapUrl text, timeStamp text, subreddit text, commentsUrl text, " +
                    "numComments text, score text, condensedScore text, title text)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS posts");
        }

        public boolean insertPost(postData toInsert)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", toInsert.id);
            contentValues.put("author", toInsert.author);
            contentValues.put("dataUrl", toInsert.dataUrl);
            contentValues.put("bitmapUrl", toInsert.bitmapUrl);
            contentValues.put("timeStamp", toInsert.timeStamp);
            contentValues.put("subreddit", toInsert.subreddit);
            contentValues.put("commentsUrl", toInsert.commentsUrl);
            contentValues.put("numComments", toInsert.numComments);
            contentValues.put("score", toInsert.score);
            contentValues.put("condensedScore", toInsert.condensedScore);
            contentValues.put("title", toInsert.title);
            db.insert("posts", null, contentValues);
            return (true);
        }

        public boolean deletePost(String id)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            int returned = db.delete("posts", "id = ?", new String[] { id });
            if(returned > 0) { return(true); }
            return (false);
        }

        public ArrayList<postData> getAllPosts()
        {
            ArrayList<postData> returnList = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from posts", null);
            res.moveToFirst();
            while(res.isAfterLast() == false)
            {
                String id = res.getString(res.getColumnIndex(ID));
                String author = res.getString(res.getColumnIndex(AUTHOR));
                String dataURL = res.getString(res.getColumnIndex(DATAURL));
                String bitmapURL = res.getString(res.getColumnIndex(BITMAPURL));
                String timeStamp = res.getString(res.getColumnIndex(TIMESTAMP));
                String subreddit = res.getString(res.getColumnIndex(SUBREDDIT));
                String commentsUrl = res.getString(res.getColumnIndex(COMMENTSURL));
                String numComments = res.getString(res.getColumnIndex(NUMCOMMENTS));
                String score = res.getString(res.getColumnIndex(SCORE));
                String condensedScore = res.getString(res.getColumnIndex(CONDENSEDSCORE));
                String title = res.getString(res.getColumnIndex(TITLE));

                returnList.add(new postData(id, author, dataURL, bitmapURL, timeStamp, subreddit, commentsUrl, numComments, score, condensedScore, title));
            }
            return(returnList);
        }
    }


    public class postData
    {
        public String id;
        public String author;
        public String dataUrl;
        public String bitmapUrl;
        public String timeStamp;
        public String subreddit;
        public String commentsUrl;
        public String numComments;
        public String score;
        public String condensedScore;
        public String title;
        public postData(String id, String author, String dataUrl, String bitmapUrl, String timeStamp, String subreddit, String commentsUrl, String numComments, String score, String condensedScore, String title)
        {
            this.id = id;
            this.author = author;
            this.dataUrl = dataUrl;
            this.bitmapUrl = bitmapUrl;
            this.timeStamp = timeStamp;
            this.subreddit = subreddit;
            this.commentsUrl = commentsUrl;
            this.numComments = numComments;
            this.score = score;
            this.condensedScore = condensedScore;
            this.title = title;
        }
    }
    private DBHelper mDB;
    protected ArrayList<postData> savedPosts;
    LinearLayout mainHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDB = new DBHelper(this);

        savedPosts = mDB.getAllPosts();

        mainHolder = (LinearLayout) findViewById(R.id.mainHolder);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                LinearLayout redditListing = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.reddit_listing, null);
                mainHolder.addView(redditListing);
            }
        });

        final ArrayList<postData> ourDataArray = new ArrayList<>();
        final ScrollView ourScrollView = (ScrollView) findViewById(R.id.scrollViewMain);
        final LinearLayout dummyLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dummy_web, null);
        //mainHolder.addView(dummyLayout);
        final WebView myWeb = (WebView) dummyLayout.findViewById(R.id.dummyWeb);
        final myJavaScriptInterface ourInterface = new myJavaScriptInterface();
        //myWeb.getSettings().getUserAgentString().replace("Mobile", "eliboM").replace("Android", "diordnA");
        myWeb.getSettings().setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
        myWeb.getSettings().setJavaScriptEnabled(true);
        myWeb.addJavascriptInterface(ourInterface, "HTMLOUT");
        myWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                myWeb.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByTagName('html')[0].innerHTML);");
            }
        });

        EditText subredditSearch = (EditText) findViewById(R.id.subredditEditText);
        ourString = new stringClass();
        ourString.ourString = "https://reddit.com";
        Thread startThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //String document = getDoc("https://www.reddit.com");
                loadedPage = "";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myWeb.loadUrl(ourString.ourString);
                    }
                });
                while(loadedPage.isEmpty()) { }
                String document = loadedPage;


                for(int i = 0; i < (document.length()/500)-1; i++) {
                    Log.d("doc", document.substring(i*500, (i+1)*500));
                }
                Element doc = Jsoup.parse(document);
                if(doc != null) {
                    Element posts = null;
                    posts = ParseDoc(posts, doc);
                    Log.d("MyNumOfChildren4", "Total : " + posts.children().size());
                    for (Element nextPost : posts.children()) {
                        if (nextPost != null && nextPost.hasClass("thing")) {
                            ourDataArray.add(getPostData(nextPost));
                            final int ourSize = ourDataArray.size()-1;
                            AddView(mainHolder, ourSize, ourDataArray);
                        }
                        if(nextPost != null && nextPost.hasClass("nav-buttons"))
                        {
                            Elements nextButtons = nextPost.getElementsByClass("next-button");
                            if(nextButtons != null && nextButtons.size() > 0)
                            {
                                Elements link = nextButtons.first().children();
                                if(link != null)
                                {
                                    Element nextPageLink = link.first();
                                    if(nextPageLink != null)
                                    {
                                        ourString.ourString = nextPageLink.attr("href");
                                        Log.d("OurString", ourString.ourString);
                                    }
                                }
                            }
                        }
                    }
                    final LinearLayout addMore = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.load_more, null);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainHolder.addView(addMore);

                        }
                    });
                    loadMoreListener(mainHolder, addMore, ourScrollView, myWeb, ourDataArray);
                }

            }
        });
        startThread.start();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void AddView (final LinearLayout mainHolder, final int ourSize, final ArrayList<postData> ourDataArray)
    {

        Log.d("COURSE ADDED", "Number: " + ourDataArray.size());
                            /*
                            public String id;
                            public String author;
                            public String dataUrl;
                            public String bitmapUrl;
                            public String timeStamp;
                            public String subreddit;
                            public String commentsUrl;
                            public String numComments;
                            public String score;
                            public String condensedScore;*/
        Log.d("title", ourDataArray.get(ourDataArray.size()-1).title);
        Log.d("id", ourDataArray.get(ourDataArray.size()-1).id);
        Log.d("author", ourDataArray.get(ourDataArray.size()-1).author);
        Log.d("dataUrl", ourDataArray.get(ourDataArray.size()-1).dataUrl);
        Log.d("bitmapUrl", ourDataArray.get(ourDataArray.size()-1).bitmapUrl);
        Log.d("timeStamp", ourDataArray.get(ourDataArray.size()-1).timeStamp);
        Log.d("subreddit", ourDataArray.get(ourDataArray.size()-1).subreddit);
        Log.d("numComments", ourDataArray.get(ourDataArray.size()-1).numComments);
        Log.d("score", ourDataArray.get(ourDataArray.size()-1).score);
        Log.d("condensedScore", ourDataArray.get(ourDataArray.size()-1).condensedScore);


        Thread loadBitMapThread = new Thread(new Runnable() {
            final LinearLayout redditListing = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.reddit_listing, null);
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainHolder.addView(redditListing);
                    }
                });
                final ProgressBar mProgress = (ProgressBar) redditListing.findViewById(R.id.imageLoad);
                final ImageView thumbnail = (ImageView) redditListing.findViewById(R.id.listingBitMap);
                final TextView title = (TextView) redditListing.findViewById(R.id.listingTitle);
                final TextView info = (TextView) redditListing.findViewById(R.id.listingInfo);
                final TextView score = (TextView) redditListing.findViewById(R.id.listingScore);
                final TextView upvote = (TextView) redditListing.findViewById(R.id.listingUpVote);
                final TextView downvote = (TextView) redditListing.findViewById(R.id.listingDownVote);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(ourDataArray.get(ourSize).title);
                        info.setText("Dude: " + ourDataArray.get(ourSize).author + "\nTime: " + ourDataArray.get(ourDataArray.size()-1).timeStamp + "\nSubreddit: " + ourDataArray.get(ourDataArray.size()-1).subreddit + "\nNumber of Comments: "  + ourDataArray.get(ourDataArray.size()-1).numComments);
                        score.setText(ourDataArray.get(ourSize).condensedScore);
                    }
                });
                redditListing.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setPositiveButton("Add To Saved Posts", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                savedPosts.add(ourDataArray.get(ourSize));
                                mDB.insertPost(ourDataArray.get(ourSize));
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        builder.show();
                        return(true);
                    }
                });
                Thread innerBitMapThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(ourDataArray.get(ourSize).bitmapUrl);
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();
                            con.setDoInput(true);
                            con.connect();
                            InputStream input = con.getInputStream();
                            final Bitmap myBitMap = BitmapFactory.decodeStream(input);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgress.setVisibility(View.GONE);
                                    thumbnail.setImageBitmap(null);
                                    thumbnail.setImageBitmap(myBitMap);

                                }
                            });
                        }
                        catch (IOException e1)
                        {
                            Log.d("IOE", "IOE, URL is: " + ourDataArray.get(ourSize).bitmapUrl);
                            mProgress.setVisibility(View.GONE);


                        }

                        thumbnail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder Builder = new AlertDialog.Builder(MainActivity.this);
                                LinearLayout mainLayout = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.dummy_web, null);
                                final WebView dialogWeb = (WebView) mainLayout.findViewById(R.id.dummyWeb);
                                dialogWeb.loadUrl(ourDataArray.get(ourSize).dataUrl);
                                dialogWeb.getSettings().setJavaScriptEnabled(true);
                                dialogWeb.getSettings().setBuiltInZoomControls(true);
                                final flagClass zoom = new flagClass();
                                                    /*dialogWeb.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            if(!zoom.flag) {
                                                                dialogWeb.zoomOut();
                                                                zoom.flag = true;
                                                            }
                                                            else{
                                                                dialogWeb.zoomIn();
                                                                zoom.flag = false;
                                                            }
                                                        }
                                                    });*/
                                Builder.setView(mainLayout);
                                Builder.setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                Builder.show();
                            }
                        });
                    }
                });
                innerBitMapThread.start();
            }
        });
        loadBitMapThread.start();
    }

    protected void loadMoreListener(final LinearLayout mainHolder, final LinearLayout addMore, final ScrollView ourScrollView, final WebView myWeb, final ArrayList<postData> ourDataArray)
    {
        Thread loadMoreThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean endFlag = false;
                while(!endFlag) {
                    Rect scrollBounds = new Rect();
                    ourScrollView.getHitRect(scrollBounds);
                    if(addMore.getLocalVisibleRect(scrollBounds)) {
                        loadedPage = "";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myWeb.loadUrl(ourString.ourString);
                            }
                        });


                        while (loadedPage.isEmpty()) {
                        }
                        String document = loadedPage;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mainHolder.removeView(addMore);
                            }
                        });
                        Element doc = Jsoup.parse(document);
                        if (doc != null) {
                            Element posts = null;
                            posts = ParseDoc(posts, doc);
                            Log.d("MyNumOfChildren4", "Total : " + posts.children().size());
                            for (Element nextPost : posts.children()) {
                                if (nextPost != null && nextPost.hasClass("thing")) {
                                    ourDataArray.add(getPostData(nextPost));
                                    final int ourSize = ourDataArray.size() - 1;
                                    AddView(mainHolder, ourSize, ourDataArray);
                                }
                                if (nextPost != null && nextPost.hasClass("nav-buttons")) {
                                    Elements nextButtons = nextPost.getElementsByClass("next-button");
                                    if (nextButtons != null && nextButtons.size() > 0) {
                                        Elements link = nextButtons.first().children();
                                        if (link != null) {
                                            Element nextPageLink = link.first();
                                            if (nextPageLink != null) {
                                                ourString.ourString = nextPageLink.attr("href");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        final LinearLayout nextLoadMore = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.load_more, null);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mainHolder.addView(nextLoadMore);
                            }
                        });
                        loadMoreListener(mainHolder, nextLoadMore, ourScrollView, myWeb, ourDataArray);
                        endFlag = true;
                    }
                    else
                    {}
                }
            }
        });
        loadMoreThread.start();
    }

    protected Element ParseDoc(Element posts, Element doc)
    {
        Log.d("Main", "Tag: " + doc.tagName() + " Class: " + doc.className() + " &c: " + doc.nodeName());
        posts = doc.getElementsByClass("listing-page").first();
        Log.d("MyNumOfChildren1", "Total : " + posts.children().size());
        for(Element nextEle : posts.children())
        {
            Log.d("Element", nextEle.ownText());
        }
        //posts = posts.getElementsByClass("content").get(1);
        posts = posts.getElementsByAttributeValue("role", "main").first();
        Log.d("MyNumOfChildren2", "Total : " + posts.children().size());

        for(Element nextEle : posts.children())
        {
            Log.d("Element", nextEle.ownText());
        }
        if(posts.getElementsByClass("spacer").size() == 2)
        {
            posts = posts.getElementsByClass("spacer").get(1);
        }
        else {
            posts = posts.getElementsByClass("spacer").get(2);
        }
        Log.d("MyNumOfChildren3", "Total : " + posts.children().size());
        posts = posts.getElementById("siteTable");
        return(posts);
    }

    protected String getDoc(String url)
    {
        try
        {
            /*Document myDoc = Jsoup.connect(URL).get();
            String document = myDoc.html();
            return(Jsoup.connect(URL).get().html());*/
            java.net.URL con = null;
            con = new URL(url);
            InputStream is = con.openStream();
            int ptr = 0;
            StringBuilder builder = new StringBuilder();
            while((ptr = is.read()) != -1)
            {
                builder.append((char) ptr);
            }
            return (builder.toString());
        }
        catch (IOException e)
        {
            Log.d("Failed", "Could not get document from URL: " + url);
            return (null);
        }
    }
    protected postData getPostData(Element post)
    {
        /*
        public String id;
        public String author;
        public String dataUrl;
        public String bitmapUrl;
        public String timeStamp;
        public String subreddit;
        public String commentsUrl;
        public String numComments;
        public String score;
        */

        String id = post.attr("data-fullname");
        String author = post.attr("data-author");
        String dataUrl = post.attr("data-url");
        String bitmapURL = "";
        Elements bitmap = post.getElementsByTag("a");
        if(bitmap != null && bitmap.size() != 0) {
            Element aElement = bitmap.get(0);
            Elements imgSource = aElement.getElementsByTag("img");
            if(imgSource != null && imgSource.size() != 0)
            {
                bitmapURL = "https:" + imgSource.get(0).attr("src");
            }
        }
        String timeStamp = "";
        String numComments = "";
        String commentsUrl = "";
        Elements timeStampDiv = post.getElementsByClass("entry");
        Element timeStampInner = null;
        String title = "";

        if(timeStampDiv != null)
        {
            for(Element nextClassElement : timeStampDiv)
            {
                if(nextClassElement.hasClass("unvoted"))
                {
                    Element tagLine = null;
                    for(Element nextPElement : nextClassElement.getElementsByTag("p"))
                    {
                        if(nextPElement.hasClass("tagline"))
                        {
                            Element time = null;
                            Elements timeElements = nextPElement.getElementsByTag("time");
                            if(timeElements != null && timeElements.size() != 0)
                            {
                                timeStamp = timeElements.first().ownText();
                            }
                        }
                        if(nextPElement.hasClass("title"))
                        {
                            Elements titleElements = nextPElement.getElementsByTag("a");
                            if(titleElements != null && titleElements.size() != 0)
                            {
                                title = titleElements.first().ownText();
                            }
                        }
                    }
                    for(Element ulElement : nextClassElement.getElementsByTag("ul"))
                    {
                        for(Element liElement : ulElement.getElementsByTag("li"))
                        {
                            if(liElement.hasClass("first"))
                            {
                                for(Element aElement : liElement.getElementsByTag("a"))
                                {
                                    numComments = aElement.ownText();
                                    commentsUrl = aElement.attr("href");
                                }
                            }
                        }
                    }
                }
            }
        }
        String subreddit = post.attr("data-subreddit");
        String score = "";
        String condensedScore = "";
        for(Element midcolElement : post.getElementsByClass("midcol"))
        {
            if(midcolElement.hasClass("unvoted"))
            {
                for(Element divElement : midcolElement.getElementsByTag("div"))
                {
                    if(divElement.hasClass("score") && divElement.hasClass("unvoted"))
                    {
                        score = divElement.attr("title");
                        condensedScore = divElement.ownText();
                    }
                }
            }
        }

        return(new postData(id, author, dataUrl, bitmapURL, timeStamp, subreddit, commentsUrl, numComments, score, condensedScore, title));
    }
}
