package net.ikishik.RsdnAndroid;

import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.Forums;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.preferences);
        FillForumListinPrefs();
        
    }
 
    private void FillForumListinPrefs()
    {
    	Cursor cursor = managedQuery(Forums.CONTENT_URI, new String[] {Forums._ID, Forums.SHORTFORUMNAME }, null, null,
        		Forums.DEFAULT_SORT_ORDER);
    	
    	
    	CharSequence entries[] = new String[cursor.getCount()];
        CharSequence entryValues[] = new String[cursor.getCount()];
        
        int i = 0;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {         
        	int idColumn = cursor.getColumnIndex(Forums._ID);
	    	int for_id = cursor.getInt(idColumn);
	    	
	    	int nameColumn = cursor.getColumnIndex(Forums.SHORTFORUMNAME);
	    	String for_name = cursor.getString(nameColumn);
	    	
        	entries[i] = for_name;
        	entryValues[i] =  Integer.toString(for_id);
        	i++;     
        } 
        
        try {
        	ListPreferenceMultiSelect forumList = (ListPreferenceMultiSelect)findPreference("ForumList"); 
        		
        
        	forumList.setEntries(entries);
        	forumList.setEntryValues(entryValues);
        	
        }
        catch (Exception e) {
			e.printStackTrace();
		}
        
        //ListPreference forumList = findViewById(R.string.pr_forum_list); 
    }
}

