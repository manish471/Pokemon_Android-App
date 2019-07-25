package com.example.manishchougule.pokemon;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    ArrayList<String> pokeNames = new ArrayList<>();
    ArrayList<String> pokeURLs = new ArrayList<>();
    ArrayList<String> newPokeNames;

    SharedPreferences sharedPreferences;

    int chosenPoke = 0;
    int locationOfCorrectAnswers = 0;
    String[] answers = new String[4];
    int score = 0;
    int highScore = 0;
    int noOfQuestions = 500;
    Boolean isDataFetched = false;
    Boolean correct = false;
    long timerTick=100000;
    CountDownTimer newTimer;

    Button button0;
    Button button1;
    Button button2;
    Button button3;
    Button startButton;


    String pokeName = "";
    String pokeUrl = "";
    Bitmap bitmapImage;

    ImageView imageView;
    TextView timerTextView;
    TextView scoreTextView;
    TextView displayScoreView;
    RelativeLayout gameRalativeLayout;
    ImageView startImageView;
    TextView highScoreView;

    public void play(){
        ImageDownloader imageTask = new ImageDownloader();
        Bitmap myBitmap;

        Random random = new Random();
        chosenPoke = random.nextInt(newPokeNames.size());
//        while(chosenPoke == 0){
//            chosenPoke = random.nextInt(pokeNames.size());
//        }
        Log.i("chosenPoke",Integer.toString(chosenPoke));

        try {
            myBitmap = imageTask.execute("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/" + (chosenPoke+1) + ".png").get();
            imageView = (ImageView)findViewById(R.id.imageView);
            imageView.setImageBitmap(myBitmap);

            locationOfCorrectAnswers = random.nextInt(4);

            int incorrectAnswerLocation;
            for(int i=0;i<4;i++){
                if(i == locationOfCorrectAnswers){
                    answers[i] = newPokeNames.get(chosenPoke);
                }else{
                    incorrectAnswerLocation = random.nextInt(newPokeNames.size());
                    while(incorrectAnswerLocation == chosenPoke){
                        incorrectAnswerLocation = random.nextInt(newPokeNames.size());
                    }

                    answers[i] = newPokeNames.get(incorrectAnswerLocation);
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }



        button0.setText(answers[0]);
        button1.setText(answers[1]);
        button2.setText(answers[2]);
        button3.setText(answers[3]);



    }

    public class ImageDownloader extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.connect();

                InputStream in = urlConnection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);

                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

    }


    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1){
                    char current = (char) data;

                    result+=current;

                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("data","fetched!!!");
            isDataFetched = true;


            try{



//                JSONObject imgUrl = new JSONObject(jsonObject.getString("sprites"));
//                Log.i("imgUrl : ",imgUrl.getString("back_default"));
//                pokeUrl = imgUrl.getString("back_default");
//                pokeURLs.add(pokeUrl);

            }catch(Exception e){

            }

        }
    }

    public void onChoosePokemon(View view){

        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswers))){
            score+=5;
            correct = true;

            Toast toast= Toast.makeText(getApplicationContext(),
            Html.fromHtml("<font color='#4BB543' ><b>" + "Correct!!! Bonus: +5" + "</b></font>"), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 1100);
            View viewToast=toast.getView();
            toast.show();

        }else{
            score--;
            Toast toast= Toast.makeText(getApplicationContext(),
            Html.fromHtml("<font color='#F32013' ><b>" + "Wrong!!!It was " + newPokeNames.get(chosenPoke) + " penalty: -1" + "</b></font>"), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 1100);
            toast.show();

        }


//        noOfQuestions++;
        displayScoreView.setText("Score : "+score);
        play();

    }

    public void createTimer(){

        startButton.setVisibility(View.INVISIBLE);
        scoreTextView.setText("");
        gameRalativeLayout.setVisibility(RelativeLayout.VISIBLE);
        startImageView.setVisibility(ImageView.INVISIBLE);
        highScoreView.setVisibility(TextView.INVISIBLE);

            new CountDownTimer(timerTick,1000){
            @Override
            public void onTick(long l) {
                timerTextView.setText(String.valueOf(l/1000)+"s");
            }

            @Override
            public void onFinish() {
                Log.i("message","Done!!!");

                startButton.setText("Play Again");
                startButton.setVisibility(View.VISIBLE);
                startImageView.setVisibility(ImageView.VISIBLE);
                gameRalativeLayout.setVisibility(RelativeLayout.INVISIBLE);
                scoreTextView.setText("Your Score is "+score);
                noOfQuestions=500;
                if(highScore <= score){
                    sharedPreferences.edit().putString("highScore", Integer.toString(score)).apply();
                    highScore = score;
                }

                highScoreView.setText("High Score : "+highScore);
                highScoreView.setVisibility(TextView.VISIBLE);

                score=0;
                displayScoreView.setText("Score : 0");
                timerTextView.setText("100s");


            }
        }.start();

    }

    public void onHandleStart(View view){



        startButton.setVisibility(View.INVISIBLE);
        scoreTextView.setText("");
        gameRalativeLayout.setVisibility(RelativeLayout.VISIBLE);
        startImageView.setVisibility(ImageView.INVISIBLE);
        highScoreView.setVisibility(TextView.INVISIBLE);
        play();

        createTimer();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("com.example.manishchougule.pokemon", Context.MODE_PRIVATE);


        button0 = (Button)findViewById(R.id.button1);
        button1 = (Button)findViewById(R.id.button2);
        button2 = (Button)findViewById(R.id.button3);
        button3 = (Button)findViewById(R.id.button4);

        highScoreView = (TextView) findViewById(R.id.highScore);

        startButton = (Button)findViewById(R.id.startButton);
        gameRalativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout2);
        timerTextView = (TextView)findViewById(R.id.textView1);
        displayScoreView = (TextView)findViewById(R.id.textView2);
        scoreTextView = (TextView)findViewById(R.id.scoreText);
        startImageView = (ImageView)findViewById(R.id.imageView2);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.startimage);
        startImageView.setImageBitmap(bitmap);

        String highScoreText = sharedPreferences.getString("highScore","");

        if(highScoreText.length() > 0) {
            highScoreView.setText("High Score : "+highScoreText);
        }else{
            highScoreView.setText("High Score : 0");
        }


        try{
            newPokeNames = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("pokeNames",ObjectSerializer.serialize(new ArrayList<String>())));
        }catch(Exception e){
            e.printStackTrace();
        }

        Log.i("newPokeNames",newPokeNames.toString());

        if(!isDataFetched && newPokeNames.size() == 0){
            startButton.setVisibility(View.INVISIBLE);
            scoreTextView.setText("Loading...");
        }



        if(newPokeNames.size() == 0) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    DownloadTask task = new DownloadTask();
                    String s = "";
                    try {
                        s = task.execute("https://pokeapi.co/api/v2/pokemon/?limit=500").get();

                        JSONObject jsonObject = new JSONObject(s);
                        String result = jsonObject.getString("results");

                        JSONArray arr = new JSONArray(result);

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject jsonPart = arr.getJSONObject(i);

                            Log.i("name : ", jsonPart.getString("name"));
                            pokeName = jsonPart.getString("name");
                            pokeNames.add(pokeName);

                        }


                        sharedPreferences.edit().putString("pokeNames", ObjectSerializer.serialize(pokeNames)).apply();

                        newPokeNames = new ArrayList<>();

                        newPokeNames = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("pokeNames", ObjectSerializer.serialize(new ArrayList<String>())));


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }, 1000);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startButton.setVisibility(View.VISIBLE);
                    scoreTextView.setText("catch the pokemons..!!!");
                }
            },3000);

        }else {
            startButton.setVisibility(View.VISIBLE);
            scoreTextView.setText("catch the pokemons..!!!");
        }









    }
}
