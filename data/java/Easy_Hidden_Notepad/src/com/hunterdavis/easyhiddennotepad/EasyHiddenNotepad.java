package com.hunterdavis.easyhiddennotepad;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class EasyHiddenNotepad extends Activity {
	
	 // setup our hidden sql text
    InventorySQLHelper NotesData = new InventorySQLHelper(this);
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
       
		// grab a view to the image and load blank png 
		ImageView imgView = (ImageView) findViewById(R.id.ImageView01);
		imgView.setImageResource(R.drawable.blankscreen);
		
		// photo on click listener
		imgView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Select Picture"), 1);

			}

		}); 
		 
		// photo long click listener
		imgView.setOnLongClickListener(new OnLongClickListener() { 
	        @Override
	        public boolean onLongClick(View v) {
	            // TODO Auto-generated method stub
	        	switchViews(v.getContext(),1);
	            return true;
	        }
	    });
		
		// hidden button listener
		Button hiddenButton = (Button) findViewById(R.id.hideButton);
		hiddenButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switchViews(v.getContext(),0);
			}

		});
		
		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView) this.findViewById(R.id.adView);
		adView.loadAd(new AdRequest());
    }
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				Uri selectedImageUri1 = data.getData();

				// grab a handle to the image
				ImageView imgPreView = (ImageView) findViewById(R.id.ImageView01);
				imgPreView.setImageURI(selectedImageUri1);

			} 
		}
	}
	
	public void switchViews(Context context,int view)
	{
		ImageView imgView = (ImageView) findViewById(R.id.ImageView01);
		EditText hiddenText = (EditText) findViewById(R.id.hiddentext);
		Button hiddenButton = (Button) findViewById(R.id.hideButton);
		
		if(view == 1)
		{
			Cursor noteCursor = getNotesCursor();
			if(noteCursor.getCount() > 0)
			{
				noteCursor.moveToFirst();
				// retrieve our values for this row
				String Notes = noteCursor.getString(1);
				hiddenText.setText(Notes);
			}
			imgView.setVisibility(View.GONE);
			hiddenText.setVisibility(View.VISIBLE);
			hiddenButton.setVisibility(View.VISIBLE);
		}
		else
		{
			// here we save our hidden text to the database
			String name = "Unnamed Item";
			// now that we have a picture uri, create a new table entry for
			// this inventory item
			SQLiteDatabase db = NotesData.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(InventorySQLHelper.NOTES, hiddenText.getText().toString());
			long latestRowId = db.insert(InventorySQLHelper.TABLE, null,
					values);
			db.close();
			
			imgView.setVisibility(View.VISIBLE);
			hiddenText.setVisibility(View.GONE);
			hiddenButton.setVisibility(View.GONE);
		}
	
	}
	
	private Cursor getNotesCursor() {
		SQLiteDatabase db = NotesData.getReadableDatabase();
		Cursor cursor = db.query(InventorySQLHelper.TABLE, null, null, null,
				null, null, null);
		startManagingCursor(cursor);
		return cursor;
	}
    
}