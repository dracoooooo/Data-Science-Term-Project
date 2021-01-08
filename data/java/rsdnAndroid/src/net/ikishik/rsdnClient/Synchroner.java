package net.ikishik.rsdnClient;

import java.util.Date;
import java.util.Vector;

import net.ikishik.RsdnAndroid.ListPreferenceMultiSelect;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.ForumGroups;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.Forums;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.Messages;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.Moderates;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.Ratings;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.Users;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.UserRequests;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.DataRequests;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;

public class Synchroner {
	
	private static JanusAT service = new JanusAT();
	
	public static JanusAT getService() {
		return service;
	}
	
	public static void setService(JanusAT service) {
		Synchroner.service = service;
	}
	
	public static Boolean syncForumsAndGrousp(ContentResolver resolver, SharedPreferences prefs)
	{
		JanusAT js = getService();
		
		 try {
			 
			 String userName = prefs.getString("UserName" , "");
		     String UserPassword = prefs.getString("UserPassword" , "");
			 
			 ForumRequest freq = new ForumRequest();
	    	    freq.setuserName(userName);
	    	    freq.setpassword(UserPassword);
	    	    freq.setforumsRowVersion("");
	    	        
	    	    resolver.delete(ForumGroups.CONTENT_URI, null, null);
	    	    resolver.delete(Forums.CONTENT_URI, null, null);
	        	
	        	ForumResponse resp = js.GetForumList(freq);
				
				Vector<JanusForumGroupInfo> forumGroups = resp.getgroupList();
				Vector<JanusForumInfo> forums = resp.getforumList();
				
				for(JanusForumGroupInfo fg : forumGroups)
				{
					 ContentValues values = new ContentValues();
			         
					 values.put(ForumGroups._ID, fg.getforumGroupId());
					 values.put(ForumGroups.FORUMGROUPNAME, fg.getforumGroupName());
					 values.put(ForumGroups.SORTORDER, fg.getsortOrder());
					
			         
					 resolver.insert(ForumGroups.CONTENT_URI, values);
				}
				
				for(JanusForumInfo fi : forums)
				{
					 ContentValues values = new ContentValues();
			         
					 values.put(Forums._ID, fi.getforumId());
					 values.put(Forums.FORUMGROUPID, fi.getforumGroupId());
					 values.put(Forums.FORUMNAME, fi.getforumName());
					 values.put(Forums.INTOP, fi.getinTop());
					 values.put(Forums.RATED, fi.getrated());
					 values.put(Forums.RATELIMIT, fi.getrateLimit());
					 values.put(Forums.SHORTFORUMNAME, fi.getshortForumName());
			         
					 resolver.insert(Forums.CONTENT_URI, values);
				}
	    	    
	        }	
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		return true;
	}
	
	public static Boolean syncNewData(ContentResolver resolver, SharedPreferences prefs)
	{
		JanusAT js = getService();
		
		 try {
			 String userName = prefs.getString("UserName" , "");
		     String UserPassword = prefs.getString("UserPassword" , "");
			 
			 byte[] messageRowVersion = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 }; 
			 byte[] moderateRowVersion = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			 byte[] ratingRowVersion = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };
	    	 
	    	 Cursor cursorRV = resolver.query(DataRequests.CONTENT_URI
	    		, new String[] {DataRequests.REQDATE, DataRequests.MESSAGEROWVERSION, DataRequests.MODERATEROWVERSION, DataRequests.RATINGROWVERSION}
	    	    , null, null, DataRequests.DEFAULT_SORT_ORDER);
			 
	    	 if(cursorRV.moveToFirst())
	    	 {
	    	    	int nameColumn = cursorRV.getColumnIndex(DataRequests.MESSAGEROWVERSION);
	    	    	messageRowVersion = cursorRV.getString(nameColumn).getBytes();
	    	    	
	    	    	nameColumn = cursorRV.getColumnIndex(DataRequests.MODERATEROWVERSION);
	    	    	moderateRowVersion = cursorRV.getString(nameColumn).getBytes();
	    	    	
	    	    	nameColumn = cursorRV.getColumnIndex(DataRequests.RATINGROWVERSION);
	    	    	ratingRowVersion = cursorRV.getString(nameColumn).getBytes();
	    	 }
			 
			 ChangeRequest creq = new ChangeRequest();
	    	    creq.setuserName(userName);
	    	    creq.setpassword(UserPassword);
	    	    creq.setmessageRowVersion(messageRowVersion);
	    	    creq.setmoderateRowVersion(moderateRowVersion);
	    	    creq.setratingRowVersion(ratingRowVersion);
	    	    creq.setmaxOutput(10);
	    	    
	    	    Vector<RequestForumInfo> req_forums = new Vector<RequestForumInfo>();
	    	    
	    	    String rawval = prefs.getString("ForumList", "");
	    	    String[] selected = ListPreferenceMultiSelect.parseStoredValue(rawval);
	    	    
	    	    for(String frm : selected)
	    	    {
	    	    	int frm_id = Integer.parseInt(frm);
	    	    	
	    	    	RequestForumInfo rf = new RequestForumInfo();
	    	    	rf.setforumId(frm_id);
	    	    	rf.setisFirstRequest(true);
	    	    }
	    	    
	    	    
	    	    /*Cursor cursor = resolver.query(Forums.CONTENT_URI, new String[] {Forums._ID }
	    	    , null, null, Forums.DEFAULT_SORT_ORDER);
	    	    
	    	    
	    	    
	    	    for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
				{
	    	    	int nameColumn = cursor.getColumnIndex(Forums._ID);
	    	    	int for_id = cursor.getInt(nameColumn);
	    	    	
	    	    	RequestForumInfo rf = new RequestForumInfo();
	    	    	rf.setforumId(for_id);
	    	    	rf.setisFirstRequest(true);
	    	    	
	    	    	req_forums.add(rf);
				}*/
				
	    	    
	    	    creq.setsubscribedForums(req_forums);
				
	    	    ChangeResponse cresp = js.GetNewData(creq);
	    	    
	    	    Vector<JanusMessageInfo> messages = cresp.getnewMessages();
	    	    for(JanusMessageInfo mi : messages)
				{
					 ContentValues values = new ContentValues();
			         
					 values.put(Messages._ID, mi.getmessageId());
					 values.put(Messages.TOPICID, mi.gettopicId());
					 values.put(Messages.PARENTID, mi.getparentId());
					 values.put(Messages.USERID, mi.getuserId());
					 values.put(Messages.FORUMID, mi.getforumId());
					 values.put(Messages.STATUS, 0);
					 values.put(Messages.SUBJECT, mi.getsubject());
					 values.put(Messages.MESSAGENAME, mi.getmessageName());
					 values.put(Messages.USERNICK, mi.getuserNick());
					 values.put(Messages.MESSAGE, mi.getmessage());
					 values.put(Messages.ARTICLEID, mi.getarticleId());
					 values.put(Messages.MESSAGEDATE, mi.getmessageDate().toLocaleString());
					 values.put(Messages.UPDATEDATE, mi.getupdateDate().toLocaleString());
					 values.put(Messages.USERROLE, mi.getuserRole());
					 values.put(Messages.USERTITLE, mi.getuserTitle());
					 values.put(Messages.USERTITLECOLOR, mi.getuserTitleColor());
					 values.put(Messages.LASTMODERATED, mi.getlastModerated().toLocaleString());
					 values.put(Messages.CLOSED, mi.getclosed());
			         
					 resolver.insert(Messages.CONTENT_URI, values);
				}
	    	    
	    	    Vector<JanusModerateInfo> moderates = cresp.getnewModerate();
	    	    for(JanusModerateInfo modi : moderates)
				{
					 ContentValues values = new ContentValues();
			         
					 values.put(Moderates.MESSAGEID, modi.getmessageId());
					 values.put(Moderates.TOPICID, modi.gettopicId());
					 values.put(Moderates.USERID, modi.getuserId());
					 values.put(Moderates.FORUMID, modi.getforumId());
					 values.put(Moderates.CREATEDATE, modi.getcreate().toLocaleString());
			         
					 resolver.insert(Moderates.CONTENT_URI, values);
				}
	    	    
	    	    Vector<JanusRatingInfo> ratings = cresp.getnewRating();
	    	    for(JanusRatingInfo ri : ratings)
				{
					 ContentValues values = new ContentValues();
			         
					 values.put(Ratings.MESSAGEID, ri.getmessageId());
					 values.put(Ratings.TOPICID, ri.gettopicId());
					 values.put(Ratings.USERID, ri.getuserId());
					 values.put(Ratings.USERRATING, ri.getuserRating());
					 values.put(Ratings.RATE, ri.getrate());
					 values.put(Ratings.RATEDATE, ri.getrateDate().toLocaleString());
			         
					 resolver.insert(Ratings.CONTENT_URI, values);
				}
	    	    
	    	    ContentValues values = new ContentValues();
		    	 
		    	 Date now = new Date();
		    	 
				 values.put(DataRequests.REQDATE, now.toLocaleString());
				 values.put(DataRequests.MESSAGEROWVERSION, cresp.getlastForumRowVersion());
				 values.put(DataRequests.MODERATEROWVERSION, cresp.getlastModerateRowVersion());
				 values.put(DataRequests.RATINGROWVERSION, cresp.getlastRatingRowVersion());
					
				 resolver.insert(DataRequests.CONTENT_URI, values);
				 
	    	    
	        }	
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		return true;
	}
	
	public static Boolean syncNewUsers(ContentResolver resolver, SharedPreferences prefs)
	{
		JanusAT js = getService();
		
		 try {
			 
			 String userName = prefs.getString("UserName" , "");
		     String UserPassword = prefs.getString("UserPassword" , "");
			 
			 byte[] lastRowVersion = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };
			 
	    	 Cursor cursor = resolver.query(UserRequests.CONTENT_URI, new String[] {UserRequests.LASTROWVERSION }
	    	    , null, null, UserRequests.DEFAULT_SORT_ORDER);
			 
	    	 if(cursor.moveToFirst())
	    	 {
	    	    	int nameColumn = cursor.getColumnIndex(UserRequests.LASTROWVERSION);
	    	    	lastRowVersion = cursor.getString(nameColumn).getBytes();
	    	 }
	    	 
	    	 UserRequest ureq = new UserRequest();
	    	 ureq.setuserName(userName);
	    	 ureq.setpassword(UserPassword);
	    	 ureq.setlastRowVersion(lastRowVersion);
	    	 ureq.setmaxOutput(100);
	    	    
	    	 UserResponse uresp = js.GetNewUsers(ureq);
	    	    
	    	 Vector<JanusUserInfo> users = uresp.getusers();
	    	 for(JanusUserInfo ui : users)
	    	 {
				ContentValues values = new ContentValues();
			         
				values.put(Users._ID, ui.getuserId());
				values.put(Users.USERNAME, ui.getuserName());
				values.put(Users.USERNICK, ui.getuserNick());
				values.put(Users.REALNAME, ui.getrealName());
				values.put(Users.PUBLICEMAIL, ui.getpublicEmail());
				values.put(Users.HOMEPAGE, ui.gethomePage());
				values.put(Users.USERNICK, ui.getuserNick());
				values.put(Users.SPECIALIZATION, ui.getspecialization());
				values.put(Users.WHEREFROM, ui.getwhereFrom());
				values.put(Users.ORIGIN, ui.getorigin());
				values.put(Users.USERCLASS, ui.getuserClass());
			         
				resolver.insert(Users.CONTENT_URI, values);
	    	 }
	    	 
	    	 ContentValues values = new ContentValues();
	    	 
	    	 Date now = new Date();
	    	 
			 values.put(UserRequests.REQDATE, now.toLocaleString());
			 values.put(UserRequests.LASTROWVERSION, uresp.getlastRowVersion());
				
			 resolver.insert(UserRequests.CONTENT_URI, values);
	    	    
	        }	
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		return true;
	}

}
