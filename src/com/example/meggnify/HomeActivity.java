package com.example.meggnify;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.savagelook.android.UrlJsonAsyncTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	
	private static final String TASKS_URL = "http://192.168.1.30:3000/api/v1/assignments.json";
	
	private SharedPreferences mPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_home);

	    mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
	}

	@Override
	public void onResume() {
	    super.onResume();

	    if (mPreferences.contains("AuthToken")) {
	        loadTasksFromAPI(TASKS_URL);
	    } else {
	        Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
	        startActivityForResult(intent, 0);
	    }
	}

	private void loadTasksFromAPI(String url) {
	    GetTasksTask getTasksTask = new GetTasksTask(HomeActivity.this);
	    getTasksTask.setMessageLoading("Loading tasks...");
	    getTasksTask.execute(url);
	}

	private class GetTasksTask extends UrlJsonAsyncTask {
	    public GetTasksTask(Context context) {
	        super(context);
	    }

	    @Override
	        protected void onPostExecute(JSONObject json) {
	            try {
	                JSONArray jsonTasks = json.getJSONObject("data").getJSONArray("assignments");
	                int length = jsonTasks.length();
	                List<String> tasksTitles = new ArrayList<String>(length);

	                for (int i = 0; i < length; i++) {
	                    tasksTitles.add(jsonTasks.getJSONObject(i).getString("name"));
	                    tasksTitles.add(jsonTasks.getJSONObject(i).getString("type"));
	                }

	                ListView tasksListView = (ListView) findViewById (R.id.tasks_list_view);
	                if (tasksListView != null) {
	                    tasksListView.setAdapter(new ArrayAdapter<String>(HomeActivity.this,
	                      android.R.layout.simple_list_item_1, tasksTitles));
	                }
	            } catch (Exception e) {
	            Toast.makeText(context, e.getMessage(),
	                Toast.LENGTH_LONG).show();
	        } finally {
	            super.onPostExecute(json);
	        }
	    }
	}

}
