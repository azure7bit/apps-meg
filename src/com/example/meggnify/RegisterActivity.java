package com.example.meggnify;

import java.io.IOException;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.savagelook.android.UrlJsonAsyncTask;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends ActionBarActivity {

	private final static String REGISTER_API_ENDPOINT_URL = "http://192.168.1.30:3000/api/v1/registrations";
	private SharedPreferences mPreferences;
	
	private String mMeggnetEmail;
	private String mMeggnetIdentificationNumber;
	private String mMeggnetPassword;
	private String mMeggnetPasswordConfirmation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_register);

	    mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
	}
	
	private class RegisterTask extends UrlJsonAsyncTask {
	    public RegisterTask(Context context) {
	       super(context);
	    }

	    @Override
	    protected JSONObject doInBackground(String... urls) {
	        DefaultHttpClient client = new DefaultHttpClient();
	        HttpPost post = new HttpPost(urls[0]);
	        JSONObject holder = new JSONObject();
	        JSONObject userObj = new JSONObject();
	        String response = null;
	        JSONObject json = new JSONObject();

	        try {
	            try {
	                json.put("success", false);
	                json.put("info", "Something went wrong. Retry!");
	                
	                userObj.put("email", mMeggnetEmail);
	                userObj.put("identification_number", mMeggnetIdentificationNumber);
	                userObj.put("password", mMeggnetPassword);
	                userObj.put("password_confirmation", mMeggnetPasswordConfirmation);
	                holder.put("meggnet", userObj);
	                StringEntity se = new StringEntity(holder.toString());
	                post.setEntity(se);

	                post.setHeader("Accept", "application/json");
	                post.setHeader("Content-Type", "application/json");

	                ResponseHandler<String> responseHandler = new BasicResponseHandler();
	                response = client.execute(post, responseHandler);
	                json = new JSONObject(response);

	            } catch (HttpResponseException e) {
	                e.printStackTrace();
	                Log.e("ClientProtocol", "" + e);
	            } catch (IOException e) {
	                e.printStackTrace();
	                Log.e("IO", "" + e);
	            }
	        } catch (JSONException e) {
	            e.printStackTrace();
	            Log.e("JSON", "" + e);
	        }

	        return json;
	    }

	    @Override
	    protected void onPostExecute(JSONObject json) {
	        try {
	            if (json.getBoolean("success")) {
	                SharedPreferences.Editor editor = mPreferences.edit();
	                editor.putString("AuthToken", json.getJSONObject("data").getString("auth_token"));
	                editor.commit();

	                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
	                startActivity(intent);
	                finish();
	            }
	            Toast.makeText(context, json.getString("info"), Toast.LENGTH_LONG).show();
	        } catch (Exception e) {
	            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
	        } finally {
	            super.onPostExecute(json);
	        }
	    }
	}

	public void registerNewAccount(View button) {
	    EditText meggnetEmailField = (EditText) findViewById(R.id.meggnetEmail);
	    mMeggnetEmail = meggnetEmailField.getText().toString();
	    
	    EditText meggnetIdentificationNumberField = (EditText) findViewById(R.id.meggnetIdentificationNumbers);
	    mMeggnetIdentificationNumber = meggnetIdentificationNumberField.getText().toString();
	    
	    EditText meggnetPasswordField = (EditText) findViewById(R.id.meggnetPassword);
	    mMeggnetPassword = meggnetPasswordField.getText().toString();
	    
	    EditText userPasswordConfirmationField = (EditText) findViewById(R.id.meggnetPasswordConfirmation);
	    mMeggnetPasswordConfirmation = userPasswordConfirmationField.getText().toString();

	    if (mMeggnetEmail.length() == 0 || mMeggnetIdentificationNumber.length() == 0 || mMeggnetPassword.length() == 0 || mMeggnetPasswordConfirmation.length() == 0) {
	        Toast.makeText(this, "Please complete all the fields",
	            Toast.LENGTH_LONG).show();
	        return;
	    } else {
	        if (!mMeggnetPassword.equals(mMeggnetPasswordConfirmation)) {
	            Toast.makeText(this, "Your password doesn't match confirmation, check again",
	                Toast.LENGTH_LONG).show();
	            return;
	        } else {
	            RegisterTask registerTask = new RegisterTask(RegisterActivity.this);
	            registerTask.setMessageLoading("Registering new account...");
	            registerTask.execute(REGISTER_API_ENDPOINT_URL);
	        }
	    }
	}
}