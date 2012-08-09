package com.HW9.ZipMovie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ZipMoiveActivity extends Activity {
    /** Called when the activity is first created. */
	public static final String OAUTH_TOKEN = "407596886-fjMWQqcfdBewNZCtPKgd4Tl1YmPa5DE8OdMwdFeS";
	public static final String OAUTH_TOKEN_SECRET = "CNsVTsss2zo9eeu552A8AOmAjBvL4NqDdyk5pJbc";
	public static final String CONSUMER_KEY = "gGVJ14Bal0tJdjveNq0IQ";
	public static final String CONSUMER_KEY_SECRET = "3vitMxByH1TBmthYVen6u65mXdoIlPdREE7hL3vkvx4";
	public static final String CALLBACK_URL = "ZipMovieCallBack:///";
	private Twitter myTwitter;
	static private RequestToken twitterRequestToken;
	private String oauthVerifier;
	static private String access;
	static private String access_secret;
	private SharedPreferences tokeSsettings;
	private SharedPreferences.Editor tokenEditor;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        myTwitter = new TwitterFactory().getInstance();
        myTwitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
        Button loginButton = (Button) findViewById(R.id.bLogin);
       
        loginButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				try {
					twitterRequestToken = myTwitter.getOAuthRequestToken(CALLBACK_URL);
					System.out.println("ORIGINAL ACCESS" + twitterRequestToken);
					//Intent myIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.google.com"));
					tokenEditor = tokeSsettings.edit();
					tokenEditor.putInt("state", 0);
					tokenEditor.commit();
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterRequestToken.getAuthenticationURL()));
					startActivity(browserIntent);
					
				} catch (TwitterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
        
    }


    /*
	@Override
	protected void onNewIntent(Intent startSearchIntent) {
		// TODO Auto-generated method stub
		super.onNewIntent(startSearchIntent);
		
		Bundle searchExtra = startSearchIntent.getExtras();
		String searchZip = searchExtra.getString("zipCode");
		this.setContentView(R.layout.searchresult);
		System.out.println("zipcode = " + searchZip);
		
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
			System.out.println(jsonString);
			inputReader.close();
			
			JSONObject myJSON = new JSONObject(jsonString.toString());
			//System.out.println(myJSON);
			//myJSON.
			


		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
*/

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Intent restartIntent = getIntent();
		System.out.println("+++++++++++++onRestart+++++++");
		
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println(" ACATIVITY DESTROYED");
		tokenEditor = tokeSsettings.edit();
		tokenEditor.putInt("state", -1);
		tokenEditor.commit();		
		
	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println(" ++++++++Paused++++++++");
	}


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		System.out.println(" ++++++++onstop++++++++");
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		final EditText zipEditText;
		final Pattern stringPattern = Pattern.compile("^\\d{5}$");
		Intent resumeIntent = this.getIntent();
		
		System.out.println(this.getIntent().toString());
		tokeSsettings = getPreferences(MODE_PRIVATE);
		
		
		if ((tokeSsettings.getInt("state", -1) == 1)){
			//call from result
			//System.out.println("token recover!!!  " + tokeSsettings.getString("accessToken", "XXXX"));
			//System.out.println("token recover!!!  " + tokeSsettings.getString("accessSecret", "YYYY"));
			System.out.println("token recover!!! state =  " + tokeSsettings.getInt("state", -1));
		}
		else if((tokeSsettings.getInt("state", -1) == 0)){
			
			tokenEditor = tokeSsettings.edit();
			tokenEditor.putInt("state", 1);
			tokenEditor.commit();
			
			System.out.println("+++++++++++++onResume+++++++");
			if(resumeIntent.getData() != null){

				Uri resumeUri = resumeIntent.getData();	
				
				if (resumeUri != null && resumeUri.toString().startsWith(CALLBACK_URL)) { 
					System.out.println("+++++++++++++++++++test+++++++++++");
					System.out.println(resumeUri.toString());
					
					oauthVerifier = resumeUri.getQueryParameter("oauth_verifier");
					System.out.println(oauthVerifier);
					
					try {
						//twitterRequestToken = myTwitter.getOAuthRequestToken(CALLBACK_URL);
						System.out.println("requestToken = " + twitterRequestToken);
						//AccessToken twitterAccessToken = myTwitter.getOAuthAccessToken(twitterRequestToken);
						AccessToken twitterAccessToken = myTwitter.getOAuthAccessToken(twitterRequestToken,oauthVerifier);
						System.out.println("AccessToken = " + twitterAccessToken);
						access = twitterAccessToken.getToken();
						access_secret = twitterAccessToken.getTokenSecret();
						
						System.out.println("access = "+ access);
						System.out.println("access_secret = "+ access_secret);
						tokeSsettings = getPreferences(MODE_PRIVATE);
						tokenEditor = tokeSsettings.edit();
						tokenEditor.putString("accessToken", access);
						tokenEditor.putString("accessSecret", access_secret);
						tokenEditor.commit();
					} catch (TwitterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}
					
					
					this.setContentView(R.layout.searchinput);
					
					zipEditText = (EditText) findViewById(R.id.zipText);
					Button loginButton = (Button) findViewById(R.id.bSearch); 
					
					loginButton.setOnClickListener(new View.OnClickListener() {
						
						public void onClick(View v) {
							// TODO Auto-generated method stub
							
							String zipString = zipEditText.getText().toString();
							Matcher zipMatcher = stringPattern.matcher(zipString);
							
							if (zipMatcher.matches()){
								System.out.println("Find match!!!!");
								
								Intent beginSearch = new Intent("com.HW9.ZipMovie.STARTSEARCH");
								beginSearch.putExtra("zipCode", zipString);
								beginSearch.putExtra("accessToken", access);
								beginSearch.putExtra("accessToken_secret", access_secret);
								startActivity(beginSearch);			
							}
							else{
								System.out.println("Not match!!!!");
								
								//CharSequence text = "Please input valid ZipCode";
								Toast errToast = Toast.makeText(getApplicationContext(), "Zipcode must have 5 digits, Please enter a correct zipCode", Toast.LENGTH_LONG);
								errToast.show();
							}
								
						}
					});
				}
			}	
		}

	}
    
	
	
    
}