
package net.ikishik.RsdnAndroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class RsdnDbHelper extends SQLiteOpenHelper {

	 private static final int DB_VERSION = 6;
	 private static final String DB_NAME = "rsdn.db";
	 
	 public static final String tbl_ForumGroups = "ForumGroups";
	 public static final String tbl_Forums = "Forums";
	 
	 public static final String tbl_Users = "Users";
	 public static final String tbl_Messages = "Messages";
	 public static final String tbl_Ratings = "Ratings";
	 public static final String tbl_Moderates = "Moderates";
	 
	 public static final String tbl_wMessages = "wMessages";
	 public static final String tbl_wRates = "wRates";
	 public static final String tbl_wModerates = "wModerates";
	 
	 public static final String tbl_Exceptions = "Exceptions";
	 public static final String tbl_RatingExceptions = "RatingExceptions";
	 public static final String tbl_ModerateExceptions = "ModerateExceptions";
	 
	 public static final String tbl_UserRequests = "UserRequests";
	 public static final String tbl_DataRequests = "DataRequests";

	 	
	public RsdnDbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);	
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		DropDB(db);
		
		String create_tbl_ForumGroups = "create table " + tbl_ForumGroups + " ( _id integer primary key, "
				+ "forumGroupName TEXT, " 
				+ "sortOrder INTEGER)";

		db.execSQL(create_tbl_ForumGroups);
		
		
		String create_tbl_Forums = "create table " + tbl_Forums + " ( _id integer primary key, "
				+ "forumGroupId INTEGER, "
				+ "shortForumName VARCHAR(255), "
				+ "forumName VARCHAR(255), "
				+ "rated INTEGER, "
				+ "inTop INTEGER, "
				+ "rateLimit INTEGER)";

		db.execSQL(create_tbl_Forums);
		
		String create_tbl_Users = "create table " + tbl_Users + " ( _id integer primary key, "
				+ "userName VARCHAR(255), "
				+ "userNick VARCHAR(255), "
				+ "realName VARCHAR(255), "
				+ "publicEmail VARCHAR(255), "
				+ "homePage TEXT, "
				+ "specialization TEXT, "
				+ "whereFrom VARCHAR(255), "
				+ "origin TEXT, "
				+ "userClass INTEGER)";

		db.execSQL(create_tbl_Users);
		
		String create_tbl_Messages = "create table " + tbl_Messages + " ( _id integer primary key, "
				+ "topicId INTEGER, "
				+ "parentId INTEGER, "
				+ "userId INTEGER, "
				+ "forumId INTEGER, "
				+ "Status INTEGER, "
				+ "subject TEXT, "
				+ "messageName VARCHAR(255), "
				+ "userNick VARCHAR(255), "
				+ "message TEXT, "
				+ "articleId INTEGER, "
				+ "messageDate DATETIME, "
				+ "updateDate DATETIME, "
				+ "userRole VARCHAR(255), "
				+ "userTitle VARCHAR(255), "
				+ "userTitleColor VARCHAR(255), "
				+ "lastModerated DATETIME, "
				+ "closed BOOLEAN)";

		db.execSQL(create_tbl_Messages);
		
		String create_tbl_Ratings = "create table " + tbl_Ratings + " ( _id integer primary key autoincrement, "
				+ "messageId INTEGER, "
				+ "topicId INTEGER, "
				+ "userId INTEGER, "
				+ "userRating INTEGER, "
				+ "rate INTEGER, "
				+ "rateDate DATETIME)";

		db.execSQL(create_tbl_Ratings);
		
		String create_tbl_Moderates = "create table " + tbl_Moderates + " ( _id integer primary key autoincrement, "
				+ "messageId INTEGER, "
				+ "topicId INTEGER, "
				+ "userId INTEGER, "
				+ "forumId INTEGER, "
				+ "createDate DATETIME)";

		db.execSQL(create_tbl_Moderates);
		
		String create_tbl_wMessages = "create table " + tbl_wMessages + " ( _id integer primary key autoincrement, "
				+ "parentId INTEGER, "
				+ "forumId INTEGER, "
				+ "status INTEGER, "
				+ "subject TEXT, "
				+ "message TEXT)";

		db.execSQL(create_tbl_wMessages);
		
		String create_tbl_wRates = "create table " + tbl_wRates + " ( _id integer primary key autoincrement, "
				+ "messageId INTEGER, "
				+ "status INTEGER, "
				+ "rate INTEGER)";

		db.execSQL(create_tbl_wRates);
		
		String create_tbl_wModerates = "create table " + tbl_wModerates + " ( _id integer primary key autoincrement, "
				+ "MessageId INTEGER, "
				+ "status INTEGER, "
				+ "ModerateAction VARCHAR(255), "
				+ "ModerateToForumId INTEGER, "
				+ "Description TEXT, "
				+ "AsModerator BOOLEAN)";

		db.execSQL(create_tbl_wModerates);
		
		String create_tbl_Exceptions = "create table " + tbl_Exceptions + " ( _id integer primary key autoincrement, "
				+ "exception TEXT, "
				+ "localMessageId INTEGER, "
				+ "info TEXT)";

		db.execSQL(create_tbl_Exceptions);
		
		String create_tbl_RatingExceptions = "create table " + tbl_RatingExceptions + " ( _id integer primary key autoincrement, "
				+ "exception TEXT, "
				+ "localRatingId INTEGER, "
				+ "info TEXT)";

		db.execSQL(create_tbl_RatingExceptions);
		
		String create_tbl_ModerateExceptions = "create table " + tbl_ModerateExceptions + " ( _id integer primary key autoincrement, "
				+ "ExceptionMessage TEXT, "
				+ "LocalModerateId INTEGER, "
				+ "Info TEXT)";

		db.execSQL(create_tbl_ModerateExceptions);
		
		String create_tbl_UserRequests = "create table " + tbl_UserRequests + " ( _id integer primary key autoincrement, "
				+ "ReqDate DATETIME, "
				+ "lastRowVersion BLOB)";

		db.execSQL(create_tbl_UserRequests);
		
		String create_tbl_DataRequests = "create table " + tbl_DataRequests + " ( _id integer primary key autoincrement, "
				+ "ReqDate DATETIME, "
				+ "messageRowVersion BLOB, "
				+ "moderateRowVersion BLOB, "
				+ "ratingRowVersion BLOB)";

		db.execSQL(create_tbl_DataRequests);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//if(newVersion != oldVersion)
		//{
			
			//DropDB(db);
			
			onCreate(db);
		//}
	}
	
	private void DropDB(SQLiteDatabase db)
	{
		db.execSQL("DROP TABLE IF EXISTS " + tbl_ForumGroups);
		
		db.execSQL("DROP TABLE IF EXISTS " + tbl_Forums);
		db.execSQL("DROP TABLE IF EXISTS " + tbl_Users);
		db.execSQL("DROP TABLE IF EXISTS " + tbl_Messages);
		db.execSQL("DROP TABLE IF EXISTS " + tbl_Ratings);
		db.execSQL("DROP TABLE IF EXISTS " + tbl_Moderates);
		
		db.execSQL("DROP TABLE IF EXISTS " + tbl_wMessages);
		db.execSQL("DROP TABLE IF EXISTS " + tbl_wRates);
		db.execSQL("DROP TABLE IF EXISTS " + tbl_wModerates);
		
		db.execSQL("DROP TABLE IF EXISTS " + tbl_Exceptions);
		db.execSQL("DROP TABLE IF EXISTS " + tbl_RatingExceptions);
		db.execSQL("DROP TABLE IF EXISTS " + tbl_ModerateExceptions);
		
		db.execSQL("DROP TABLE IF EXISTS " + tbl_UserRequests);
		db.execSQL("DROP TABLE IF EXISTS " + tbl_DataRequests);
	}

}
