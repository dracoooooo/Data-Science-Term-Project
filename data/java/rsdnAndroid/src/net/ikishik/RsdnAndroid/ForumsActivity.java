package net.ikishik.RsdnAndroid;

import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.Forums;
import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ForumsActivity extends ListActivity {
	 private static final String[] PROJECTION = new String[] {
		 Forums._ID, // 0
		 Forums.FORUMNAME, // 1
	 };
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        
        RsdnDbHelper dbOpenHelper = new RsdnDbHelper(ForumsActivity.this);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
       
      
        db.close();
        
        //Intent intent = getIntent();
        //if (intent.getData() == null) {
            //intent.setData(Forums.CONTENT_URI);
        //}
        
        //Bundle bdl = getIntent().getExtras();
        //int grp_id = bdl.getInt("group_id");
        
        Uri mUri = getIntent().getData();
        String grp_id = mUri.getPathSegments().get(1);
        
                
        Cursor cursor = managedQuery(Forums.CONTENT_URI, PROJECTION, "forumGroupId=?", new String[]{grp_id},
        		Forums.DEFAULT_SORT_ORDER);
       
        
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.activity_list_item , cursor,
                new String[] { Forums.FORUMNAME }, new int[] { android.R.id.text1 });
        setListAdapter(adapter);
        
       
        
        
    }

    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		//Uri uri = ContentUris.withAppendedId(ForumGroups.CONTENT_URI, id);
		
		//startActivity(new Intent(Intent.ACTION_VIEW, uri));
		
		// Get the item that was clicked
		//Object o = this.getListAdapter().getItem(position);
		//String keyword = o.toString();
		
	}


}
