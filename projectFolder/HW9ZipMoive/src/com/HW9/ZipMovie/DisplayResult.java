package com.HW9.ZipMovie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayResult extends Activity {
	
	private int currentChoice;
	private String[] titles;
	private String[] theaters;
	private String[] showtimes;
	private String[] links;
	
	
	@Override
	protected void onCreate(Bundle instance) {
		// TODO Auto-generated method stub
		super.onCreate(instance);
		this.setContentView(R.layout.searchresult);
		
		LinearLayout myMovieLayout = (LinearLayout) findViewById(R.id.myMovieGroup);
		RadioGroup myRadioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
		
		
		Bundle searchBundle = this.getIntent().getExtras();
		//System.out.println(searchBundle);
		String searchZip = searchBundle.getString("zipCode");
		String accessToken = searchBundle.getString("accessToken");
		
		System.out.println("accessToken pass in =" + accessToken);
		
		String accessToken_secret = searchBundle.getString("accessToken_secret");
		
		System.out.println("accessToken_secret pass in =" + accessToken_secret);
		System.out.println("zipcode = " + searchZip);
		
		final Twitter myTwitter = new TwitterFactory().getInstance();
		myTwitter.setOAuthConsumer(ZipMoiveActivity.CONSUMER_KEY, ZipMoiveActivity.CONSUMER_KEY_SECRET);
		AccessToken myAccessToken = new AccessToken(accessToken,accessToken_secret);
		myTwitter.setOAuthAccessToken(myAccessToken);
		
		try {
			
			URL tomcatURL;
			tomcatURL = new URL("http://cs-server.usc.edu:34714/examples/servlet/ZipMovie?zipCode="+searchZip);
			URLConnection tomcatConnection =  tomcatURL.openConnection();
			tomcatConnection.connect();
			InputStream tomcatInputStream = tomcatConnection.getInputStream();
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(tomcatInputStream));
			String temp = "";
			StringBuffer jsonString = new StringBuffer(""); 							

			while((temp = inputReader.readLine()) != null){
				jsonString.append(temp);
			}
			//System.out.println(jsonString);
			inputReader.close();
			
			
			JSONObject myJSON = new JSONObject(jsonString.toString());
			String type = myJSON.getString("movies");
			
			//System.out.println(myJSON);
			//System.out.println(myJSON.get("movies"));
			//System.out.println(myJSONArray);
			
			
			LinearLayout tempLinear;
			TextView tempText;
			ImageView tempImage;
			RadioButton tempRadio;
			Drawable tempDraw;
			URL tempURL;
			URLConnection tempConnection;
			//System.out.println("string length is " + myJSONArray.length());

	 
			
			if(type.equals("failTypeNoShow")){
				tempText = new TextView(this);
				tempText.setTextSize(20);
				tempText.setText("Sorry your nearest theater doesn't provide the showtime information");
				myMovieLayout.addView(tempText);
				//add back button
			}
			else if(type.equals("failTypeNoZip")){
				tempText = new TextView(this);
				tempText.setTextSize(20);
				tempText.setText("Sorry we can't find the theater information for the zipcode you provided");
				myMovieLayout.addView(tempText);
				//add back button
			}
			else if (type != null){
				JSONArray myJSONArray = myJSON.getJSONObject("movies").getJSONArray("movie");
				titles = new String[myJSONArray.length()];
				theaters = new String[myJSONArray.length()];
				showtimes = new String[myJSONArray.length()];
				links = new String[myJSONArray.length()];
				
				final Button tweetButton = (Button) findViewById(R.id.tweetButton);
				final Button backButton = (Button) findViewById(R.id.backButton);
				
				tweetButton.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {

						String time;
						int index = showtimes[currentChoice].indexOf(',');
						if(index == -1){
							time = showtimes[currentChoice];
						}
						else{
							time = showtimes[currentChoice].substring(0, index);
						}
						try {
							myTwitter.updateStatus("I will see \"" + titles[currentChoice] + "\" at " + theaters[currentChoice] 
									+ " at " + time + "\n Link: http://community.flixster.com" + links[currentChoice]);
							Toast tweetToast = Toast.makeText(getApplicationContext(), "Tweet successful!", Toast.LENGTH_LONG);
							tweetToast.show();
						} catch (TwitterException e) {
							// TODO Auto-generated catch block
							Toast tweetToast = Toast.makeText(getApplicationContext(), "Tweet Fail", Toast.LENGTH_LONG);
							tweetToast.show();
							e.printStackTrace();
						}
							
					}
				});
				
				backButton.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						SharedPreferences tokeSsettings = getPreferences(MODE_PRIVATE);
						SharedPreferences.Editor tokenEditor;
						
						tokenEditor = tokeSsettings.edit();
						tokenEditor.putInt("state", -1);
						tokenEditor.commit();
						Intent goBack = new Intent("com.HW9.ZipMovie.BACK");
						startActivity(goBack);	
					}
				});
				
				for(int i=0;i<myJSONArray.length();i++){
					tempLinear = new LinearLayout(this);
					tempLinear.setOrientation(LinearLayout.HORIZONTAL);
					tempLinear.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));	
					tempLinear.setId(i);
					//tempLinear.setPadding(5, 2, 5, 2);
					
					tempText = new TextView(this);
					tempText.setText(myJSONArray.getJSONObject(i).getString("title"));
					tempText.setWidth(300);
					tempText.setGravity(Gravity.LEFT);
					tempText.setId(i);
					
							
					tempURL = new URL(myJSONArray.getJSONObject(i).getString("cover"));
					tempConnection =  tempURL.openConnection();
					tempConnection.connect();
					InputStream is = tempConnection.getInputStream();
		            tempDraw = Drawable.createFromStream(is, "src");
					tempImage = new ImageView(this);
					tempImage.setImageDrawable(tempDraw);
					//tempImage.set
					tempImage.setLayoutParams(new LayoutParams(80,150));
					tempImage.setId(i);
					
					tempRadio = new RadioButton(this);
					//tempRadio.setGravity(Gravity.RIGHT);
					//System.out.println(tempLinear.getHeight());
					tempRadio.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 150));
					//tempRadio.setHeight(tempLinear.getHeight());
					//tempRadio.setHeight(50);
					tempRadio.setId(i);
					
					
					myRadioGroup.addView(tempRadio);
					tempLinear.addView(tempText);
					tempLinear.addView(tempImage);
					//tempLinear.addView(tempRadio);
					myMovieLayout.addView(tempLinear);
					
					titles[i] = myJSONArray.getJSONObject(i).getString("title");
					theaters[i] = myJSONArray.getJSONObject(i).getString("movieTheatre");
					showtimes[i] = myJSONArray.getJSONObject(i).getString("showTime");
					links[i] = 	myJSONArray.getJSONObject(i).getString("url");

					/*
					System.out.println("showTime = " +myJSONArray.getJSONObject(i).getString("showTime"));
					System.out.println("title = " +myJSONArray.getJSONObject(i).getString("title"));
					System.out.println("cover = " +myJSONArray.getJSONObject(i).getString("cover"));
					System.out.println("movieTheatre = " +myJSONArray.getJSONObject(i).getString("movieTheatre"));
					System.out.println("movieDuration = " +myJSONArray.getJSONObject(i).getString("movieDuration"));
					System.out.println("url = " +myJSONArray.getJSONObject(i).getString("url"));
					*/
				}
				
				
				myRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					
					public void onCheckedChanged(RadioGroup RG, int checkID){
						System.out.println(checkID);
						currentChoice = checkID;
						tweetButton.setVisibility(View.VISIBLE);
						backButton.setVisibility(View.VISIBLE);
					}
					
					
				});
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		}

		
		
		
	}

}
