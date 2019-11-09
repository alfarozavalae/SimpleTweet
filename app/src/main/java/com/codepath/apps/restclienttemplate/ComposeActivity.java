package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.content.Intent;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.github.scribejava.apis.TwitterApi;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final int MAX_TWEET_LENGHT = 140;
    public static final String TAG = "ComposeActivity";

    EditText  etCompose;
    Button btnTweet;
    TextView tweetlenght;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        tweetlenght = findViewById(R.id.tweetlenght);
        client = TwitterApplication.getRestClient(this);


        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tweetlenght.setText(String.valueOf(charSequence.length()));

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                tweetlenght.setText(String.valueOf(charSequence.length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > MAX_TWEET_LENGHT ){
                    tweetlenght.setTextColor(Color.RED);
                    btnTweet.setEnabled(false);
                }

                else{
                    tweetlenght.setTextColor(Color.BLACK);
                    btnTweet.setEnabled(true);
                }
//                tweetLength.setText(String.valueOf(editable.length()));
            }
        });





        // Set click listenner on button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "sorry your tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGHT) {
                    Toast.makeText(ComposeActivity.this, "sorry your tweet is too long", Toast.LENGTH_LONG).show();
                    return;
                }
                //Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_LONG).show();
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet says: " + tweet.body);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            // set result code and bundle data for response
                            setResult(RESULT_OK, intent);
                            // closes the activity, pass data to parent
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                });
                // make an API call to Twitter to publish the tweet
            }
        });
    }
}
