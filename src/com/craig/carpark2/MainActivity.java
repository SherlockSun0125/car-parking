package com.craig.carpark2;

//Craig Miller
//S1437151

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.craig.carpark2.Tasks;

import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.craig.carpark2.CarParkModel;

public class MainActivity extends Activity implements SearchView.OnQueryTextListener, OnClickListener {
	
	final private String url = "http://open.glasgow.gov.uk/api/live/parking.php?type=xml";
	private String xml_string;
	private SearchView mSearchView;
    //private TextView mStatusView;
    private Spinner spinner;
    private ImageView spin;
    Timer timer;
    RotateAnimation rAnim;
    AnimationSet animation = new AnimationSet(false);
    List<CarParkModel> CPs = null;
    ProgressBar pb;
	private ViewFlipper viewFlipper;
    private float lastX;
    ListView lv2;
    double occupancyRate;
	
    @Override
    public void onDestroy(){
    	System.out.println("onDestroy");
    	super.onDestroy();
    	timer.cancel();
    	System.gc();
    	finish();
    }
    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().getDecorView().setBackgroundColor(Color.parseColor("#FFFFFF"));
		
		StrictMode.ThreadPolicy permitAll = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(permitAll);
		spinner = (Spinner)findViewById(R.id.spinner1);
		spin = (ImageView) findViewById(R.id.imageView1);
		pb = (ProgressBar) findViewById(R.id.pb);
		viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
		final ListView lv = (ListView)findViewById(R.id.listView1);
		lv2 = (ListView)findViewById(R.id.listView2);
		
		rAnim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
		rAnim.setRepeatCount(Animation.INFINITE);
		rAnim.setInterpolator(new LinearInterpolator());
		rAnim.setDuration(700);
	
		Animation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setInterpolator(new DecelerateInterpolator());
		fadeIn.setStartOffset(1000);
		fadeIn.setDuration(1000);

		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new AccelerateInterpolator()); 
		fadeOut.setStartOffset(2000);
		fadeOut.setDuration(1000);

		animation.addAnimation(fadeIn);
		animation.addAnimation(fadeOut);
		animation.addAnimation(rAnim);
		


    	callAsynchronousTask();

		spinner.setOnItemSelectedListener(new ItemSelectedListener(){
			
			@Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) 
		    {
				
				CarParkModel cp = CPs.get(position);
		        List<String> cp_data = new ArrayList<String>();
		         
		        cp_data.add("Name: " + cp.getName());
		        cp_data.add("Spaces: " + cp.getSpaces());
		        cp_data.add("Status: " + cp.getStatus());
		        cp_data.add("Occupancy: " + cp.getPercentage() + "%");
		        pb.setProgress((int) cp.getPercentage());
		        
				
				
				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_list_item, cp_data);
				lv.setAdapter(arrayAdapter);
				
				

		    }
		});
	
	}
	public void callAsynchronousTask() {
        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {       
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {       
                        try {
                            TestTask test = new TestTask();
                            // PerformBackgroundTask this class is the class that extends AsynchTask 
                            test.execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 65000); //execute in every 65000 ms
    }
	private class TestTask extends AsyncTask<String, Void, String> {
		
		@Override
		protected void onPreExecute(){
			spin.startAnimation(animation);
		}
	    @Override
	    protected String doInBackground(String... urls) {
			
	      String s = null;
		try {
			s = Tasks.getXML(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("Printing the xml");
//		if(s != null){
//			System.out.println(s);	
//		}
		
	    	return s;
	      
	    }

	    @Override
	    protected void onPostExecute(String result) {
	    	xml_string = result;
			List<String> spinnerArray =  new ArrayList<String>();
			CPs = Tasks.parseData(xml_string);
			int totalFree = 0;
			int totalSpaces = 0;
			int totalOccupied = 0;
			for(int i = 0; i < CPs.size(); i++){
				CarParkModel cp = (CarParkModel) CPs.get(i);
				spinnerArray.add(cp.getName());
				totalFree = totalFree + cp.getSpaces();
				totalSpaces = totalSpaces + cp.getTotalSpaces();
				totalOccupied = totalOccupied + cp.getSpacesTaken();
			}
			occupancyRate = Math.floor(((double) totalOccupied / (double)totalSpaces)*100);
			List<String> aggregates = new ArrayList<String>();
			aggregates.add("Total Spaces: " + totalSpaces);
			aggregates.add("Total Occupied: "+ totalOccupied);
			aggregates.add("Total Available: "+ totalFree);
			aggregates.add("Occupancy Rate: "+ occupancyRate + "%");
			
			ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_list_item, aggregates);
			lv2.setAdapter(listAdapter);
        	
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				    MainActivity.this, android.R.layout.simple_spinner_item, spinnerArray);
			
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			spinner.setAdapter(adapter);
	    }
	}

	@Override	
	  public void onClick(View view) {

		  switch(view.getId()){
			case 1:
				System.out.println("test");
				
				break;
			case 2:
				System.out.println("test");
				break;
		  }
	
	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if(null!=searchManager ) {   
         mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        mSearchView.setIconifiedByDefault(false);
        
        setupSearchView(searchItem);
        return true;

	}
    public boolean onTouchEvent(MotionEvent touchevent) {
    	switch (touchevent.getAction()) {
        
        case MotionEvent.ACTION_DOWN: 
        	lastX = touchevent.getX();
            break;
        case MotionEvent.ACTION_UP: 
            float currentX = touchevent.getX();
            
            // Handling left to right screen swap.
            if (lastX < currentX) {
            	
            	// If there aren't any other children, just break.
                if (viewFlipper.getDisplayedChild() == 0)
                	break;
                
                // Next screen comes in from left.
                viewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
                // Current screen goes out from right. 
                viewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);
                
                // Display next screen.
                viewFlipper.showNext();
             }
                                     
            // Handling right to left screen swap.
             if (lastX > currentX) {
            	 
            	 // If there is a child (to the left), kust break.
            	 if (viewFlipper.getDisplayedChild() == 1)
            		 break;
    
            	 // Next screen comes in from right.
            	 viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
            	// Current screen goes out from left. 
            	 viewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);
                 
            	// Display previous screen.
                 viewFlipper.showPrevious();
             }
             break;
    	 }
         return false;
    }
    private void setupSearchView(MenuItem searchItem) {

        if (isAlwaysExpanded()) {
            mSearchView.setIconifiedByDefault(false);
        } else {
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();

            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null
                        && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }
            mSearchView.setSearchableInfo(info);
        }

        mSearchView.setOnQueryTextListener(this);
    }
    
    public boolean onQueryTextChange(String newText) {
        //mStatusView.setText("Search: " + newText);
        return false;
    }

    public boolean onQueryTextSubmit(String query) {
        //mStatusView.setText("Searched for: " + query);
        return false;
    }

    public boolean onClose() {
        //mStatusView.setText("Not open");
        return false;
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_search:

	            return true;
	        case R.id.action_settings:

	        	showListDialog();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void showListDialog()
	{
		final String[] options ={"Orientation","Ambient Mode","Info"};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Options");
		builder.setItems(options,new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichItem) 
	           {   
	        	   switch(whichItem)
	               {
	               		case 0 :
	               			showOrientationOptions();
	               		break;
	               		case 1 :
	               			launchAmbient();
	               		break;
	               		case 2 :
	               			showInfoDialog();
	               		break;
	               }
	           }
		});
		builder.setCancelable(false);
		
		AlertDialog alert = builder.create();	
		alert.show();
	}
	private void launchAmbient(){
			Intent i = new Intent(this, AmbientActivity.class);
			i.putExtra("rate", occupancyRate );
			startActivity(i);
	}
	private void showOrientationOptions()
	{
		final String[] options ={"Lock Landscape","Lock Portrait","None"};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose an option");
		builder.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichItem) 
	        {
	        	   Toast.makeText(getApplicationContext(), options[whichItem], Toast.LENGTH_SHORT).show();
	               switch(whichItem)
	               {
	               		case 0 :
	               			lockOrientationLandscape();
	               			dialog.dismiss();
	               		break;
	               		case 1 :
	               			lockOrientationPortrait();
	               			dialog.dismiss();
	               		break;
	               		case 2 :
	               			unlockOrientation();
	               			dialog.dismiss();
	               		break;
	               }
	        }
		});
		AlertDialog alert = builder.create();	
		alert.show();
	}
	
	private void showInfoDialog()
	{

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("App Information");
		builder.setMessage("The Car Parks app is a small but useful application designed for mobile devices. "
				+ "It will display the details of car parks in Glasgow. The information is updated every 65 seconds. \n"
				+ "S1437151");
		AlertDialog alert = builder.create();	
		alert.show();
	}
	
	public void lockOrientation( int orientation ) {
		setRequestedOrientation(orientation);
	}
	
	public void lockOrientationLandscape() {
		lockOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	public void lockOrientationPortrait() {
		lockOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	public void unlockOrientation() {
		lockOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}
}

