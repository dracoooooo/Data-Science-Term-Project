package net.ikishik.RsdnAndroid;

import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.ForumGroups;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.Forums;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.Users;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.Messages;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.Ratings;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.Moderates;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.wMessages;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.wRates;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.wModerates;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.Exceptions;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.RatingExceptions;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.ModerateExceptions;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.UserRequests;
import net.ikishik.RsdnAndroid.RsdnAndroidDBStatic.DataRequests;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Date;
import java.util.HashMap;

/**
 * Provides access to a database of notes. Each note has a title, the note
 * itself, a creation date and a modified data.
 */
public class RsdnAndroidProvider extends ContentProvider {

    //private static final String TAG = "NotePadProvider";

    //private static final String DATABASE_NAME = "rsdn.db";
    //private static final int DATABASE_VERSION = 1;
    private static final String FORUMGROUPS_TABLE_NAME = "ForumGroups";
    private static final String FORUMS_TABLE_NAME = "Forums";
    private static final String USERS_TABLE_NAME = "Users";
    private static final String MESSAGES_TABLE_NAME = "Messages";
    private static final String RATINGS_TABLE_NAME = "Ratings";
    private static final String MODERATES_TABLE_NAME = "Moderates";
    private static final String WMESSAGES_TABLE_NAME = "wMessages";
    private static final String WRATES_TABLE_NAME = "wRates";
    private static final String WMODERATES_TABLE_NAME = "wModerates";
    private static final String EXCEPTIONS_TABLE_NAME = "Exceptions";
    private static final String RATINGEXCEPTIONS_TABLE_NAME = "RatingExceptions";
    private static final String MODERATEEXCEPTIONS_TABLE_NAME = "ModerateExceptions";
    private static final String USERREQUESTS_TABLE_NAME = "UserRequests";
    private static final String DATAREQUESTS_TABLE_NAME = "DataRequests";

    private static HashMap<String, String> sForumGroupsProjectionMap;
    private static HashMap<String, String> sForumsProjectionMap;
    private static HashMap<String, String> sUsersProjectionMap;
    private static HashMap<String, String> sMessagesProjectionMap;
    private static HashMap<String, String> sRatingsProjectionMap;
    private static HashMap<String, String> sModeratesProjectionMap;
    private static HashMap<String, String> swMessagesProjectionMap;
    private static HashMap<String, String> swRatesProjectionMap;
    private static HashMap<String, String> swModeratesProjectionMap;
    private static HashMap<String, String> sExceptionsProjectionMap;
    private static HashMap<String, String> sRatingExceptionsProjectionMap;
    private static HashMap<String, String> sModerateExceptionsProjectionMap;
    private static HashMap<String, String> sUserRequestsProjectionMap;
    private static HashMap<String, String> sDataRequestsProjectionMap;

    private static final int FORUMGROUPS = 1;
    private static final int FORUMGROUP_ID = 2;
    
    private static final int FORUMS = 3;
    private static final int FORUM_ID = 4;
    
    private static final int USERS = 5;
    private static final int USER_ID = 6;
    
    private static final int MESSAGES = 7;
    private static final int MESSAGE_ID = 8;
    
    private static final int RATINGS = 9;
    private static final int RATING_ID = 10;
    
    private static final int MODERATES = 11;
    private static final int MODERATE_ID = 12;
    
    private static final int WMESSAGES = 13;
    private static final int WMESSAGE_ID = 14;
    
    private static final int WRATES = 15;
    private static final int WRATE_ID = 16;
    
    private static final int WMODERATES = 17;
    private static final int WMODERATE_ID = 18;
    
    private static final int EXCEPTIONS = 19;
    private static final int EXCEPTION_ID = 20;
    
    private static final int RATINGEXCEPTIONS = 21;
    private static final int RATINGEXCEPTION_ID = 22;
    
    private static final int MODERATEEXCEPTIONS = 23;
    private static final int MODERATEEXCEPTION_ID = 24;
    
    private static final int USERREQUESTS = 25;
    private static final int USERREQUEST_ID = 26;
    
    private static final int DATAREQUESTS = 27;
    private static final int DATAREQUEST_ID = 28;

    private static final UriMatcher sUriMatcher;

    /**
     * This class helps open, create, and upgrade the database file.
     */
    private RsdnDbHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new RsdnDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String orderBy;
        
        switch (sUriMatcher.match(uri)) {
        case FORUMGROUPS:
            qb.setTables(FORUMGROUPS_TABLE_NAME);
            qb.setProjectionMap(sForumGroupsProjectionMap);
            
            orderBy = ForumGroups.DEFAULT_SORT_ORDER;
            break;
        case FORUMGROUP_ID:
            qb.setTables(FORUMGROUPS_TABLE_NAME);
            qb.setProjectionMap(sForumGroupsProjectionMap);
            qb.appendWhere(ForumGroups._ID + "=" + uri.getPathSegments().get(1));
            
            orderBy = ForumGroups.DEFAULT_SORT_ORDER;
            break;
        case FORUMS:
            qb.setTables(FORUMS_TABLE_NAME);
            qb.setProjectionMap(sForumsProjectionMap);
            
            orderBy = Forums.DEFAULT_SORT_ORDER;
            
            break;
        case FORUM_ID:
            qb.setTables(FORUMS_TABLE_NAME);
            qb.setProjectionMap(sForumsProjectionMap);
            qb.appendWhere(Forums._ID + "=" + uri.getPathSegments().get(1));
            
            orderBy = Forums.DEFAULT_SORT_ORDER;
            
            break;
        case USERS:
            qb.setTables(USERS_TABLE_NAME);
            qb.setProjectionMap(sUsersProjectionMap);
            
            orderBy = Users.DEFAULT_SORT_ORDER;
            
            break;
        case USER_ID:
            qb.setTables(USERS_TABLE_NAME);
            qb.setProjectionMap(sUsersProjectionMap);
            qb.appendWhere(Users._ID + "=" + uri.getPathSegments().get(1));
            
            orderBy = Users.DEFAULT_SORT_ORDER;
            
            break;
        case MESSAGES:
            qb.setTables(MESSAGES_TABLE_NAME);
            qb.setProjectionMap(sMessagesProjectionMap);
            
            orderBy = Messages.DEFAULT_SORT_ORDER;
            
            break;
        case MESSAGE_ID:
            qb.setTables(MESSAGES_TABLE_NAME);
            qb.setProjectionMap(sMessagesProjectionMap);
            qb.appendWhere(Messages._ID + "=" + uri.getPathSegments().get(1));
            
            orderBy = Messages.DEFAULT_SORT_ORDER;
            
            break;
        case RATINGS:
            qb.setTables(RATINGS_TABLE_NAME);
            qb.setProjectionMap(sRatingsProjectionMap);
            
            orderBy = Ratings.DEFAULT_SORT_ORDER;
            
            break;
        case RATING_ID:
            qb.setTables(RATINGS_TABLE_NAME);
            qb.setProjectionMap(sRatingsProjectionMap);
            qb.appendWhere(Ratings._ID + "=" + uri.getPathSegments().get(1));
            
            orderBy = Ratings.DEFAULT_SORT_ORDER;
            
            break;
        case MODERATES:
            qb.setTables(MODERATES_TABLE_NAME);
            qb.setProjectionMap(sModeratesProjectionMap);
            
            orderBy = Moderates.DEFAULT_SORT_ORDER;
            
            break;
        case MODERATE_ID:
            qb.setTables(MODERATES_TABLE_NAME);
            qb.setProjectionMap(sModeratesProjectionMap);
            qb.appendWhere(Moderates._ID + "=" + uri.getPathSegments().get(1));
            
            orderBy = Moderates.DEFAULT_SORT_ORDER;
            
            break;
        case WMESSAGES:
            qb.setTables(WMESSAGES_TABLE_NAME);
            qb.setProjectionMap(swMessagesProjectionMap);
            
            orderBy = wMessages.DEFAULT_SORT_ORDER;
            
            break;
        case WMESSAGE_ID:
            qb.setTables(WMESSAGES_TABLE_NAME);
            qb.setProjectionMap(swMessagesProjectionMap);
            qb.appendWhere(wMessages._ID + "=" + uri.getPathSegments().get(1));
            
            orderBy = wMessages.DEFAULT_SORT_ORDER;
            
            break;
        case WRATES:
            qb.setTables(WRATES_TABLE_NAME);
            qb.setProjectionMap(swRatesProjectionMap);
            
            orderBy = wRates.DEFAULT_SORT_ORDER;
            
            break;
        case WRATE_ID:
            qb.setTables(WRATES_TABLE_NAME);
            qb.setProjectionMap(swRatesProjectionMap);
            qb.appendWhere(wRates._ID + "=" + uri.getPathSegments().get(1));
            
            orderBy = wRates.DEFAULT_SORT_ORDER;
            
            break;
        case WMODERATES:
            qb.setTables(WMODERATES_TABLE_NAME);
            qb.setProjectionMap(swModeratesProjectionMap);
            
            orderBy = wModerates.DEFAULT_SORT_ORDER;
            
            break;
        case WMODERATE_ID:
            qb.setTables(WMODERATES_TABLE_NAME);
            qb.setProjectionMap(swModeratesProjectionMap);
            qb.appendWhere(wModerates._ID + "=" + uri.getPathSegments().get(1));
            
            orderBy = wModerates.DEFAULT_SORT_ORDER;
            
            break;
        case EXCEPTIONS:
            qb.setTables(EXCEPTIONS_TABLE_NAME);
            qb.setProjectionMap(sExceptionsProjectionMap);
            
            orderBy = Exceptions.DEFAULT_SORT_ORDER;
            
            break;
        case EXCEPTION_ID:
            qb.setTables(EXCEPTIONS_TABLE_NAME);
            qb.setProjectionMap(sExceptionsProjectionMap);
            qb.appendWhere(Exceptions._ID + "=" + uri.getPathSegments().get(1));
            
            orderBy = Exceptions.DEFAULT_SORT_ORDER;
            
            break;
        case RATINGEXCEPTIONS:
            qb.setTables(RATINGEXCEPTIONS_TABLE_NAME);
            qb.setProjectionMap(sRatingExceptionsProjectionMap);
            
            orderBy = RatingExceptions.DEFAULT_SORT_ORDER;
            
            break;
        case RATINGEXCEPTION_ID:
            qb.setTables(RATINGEXCEPTIONS_TABLE_NAME);
            qb.setProjectionMap(sRatingExceptionsProjectionMap);
            qb.appendWhere(RatingExceptions._ID + "=" + uri.getPathSegments().get(1));
            
            orderBy = RatingExceptions.DEFAULT_SORT_ORDER;
            
            break;
        case MODERATEEXCEPTIONS:
            qb.setTables(MODERATEEXCEPTIONS_TABLE_NAME);
            qb.setProjectionMap(sModerateExceptionsProjectionMap);
            
            orderBy = ModerateExceptions.DEFAULT_SORT_ORDER;
            
            break;
        case MODERATEEXCEPTION_ID:
            qb.setTables(MODERATEEXCEPTIONS_TABLE_NAME);
            qb.setProjectionMap(sModerateExceptionsProjectionMap);
            qb.appendWhere(ModerateExceptions._ID + "=" + uri.getPathSegments().get(1));
            
            orderBy = ModerateExceptions.DEFAULT_SORT_ORDER;
            
            break;
        case USERREQUESTS:
            qb.setTables(USERREQUESTS_TABLE_NAME);
            qb.setProjectionMap(sUserRequestsProjectionMap);
            
            orderBy = UserRequests.DEFAULT_SORT_ORDER;
            
            break;
        case USERREQUEST_ID:
            qb.setTables(USERREQUESTS_TABLE_NAME);
            qb.setProjectionMap(sUserRequestsProjectionMap);
            qb.appendWhere(UserRequests._ID + "=" + uri.getPathSegments().get(1));
            
            orderBy = UserRequests.DEFAULT_SORT_ORDER;
            
            break;
        case DATAREQUESTS:
            qb.setTables(DATAREQUESTS_TABLE_NAME);
            qb.setProjectionMap(sDataRequestsProjectionMap);
            
            orderBy = DataRequests.DEFAULT_SORT_ORDER;
            
            break;
        case DATAREQUEST_ID:
            qb.setTables(DATAREQUESTS_TABLE_NAME);
            qb.setProjectionMap(sDataRequestsProjectionMap);
            qb.appendWhere(DataRequests._ID + "=" + uri.getPathSegments().get(1));
            
            orderBy = DataRequests.DEFAULT_SORT_ORDER;
            
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }   

        // If no sort order is specified use the default
        
        if (!TextUtils.isEmpty(sortOrder)) {
            orderBy = sortOrder;
        } 

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case FORUMGROUPS:
            return ForumGroups.CONTENT_TYPE;
        case FORUMGROUP_ID:
            return ForumGroups.CONTENT_ITEM_TYPE;
        case FORUMS:
            return Forums.CONTENT_TYPE;
        case FORUM_ID:
            return Forums.CONTENT_ITEM_TYPE;
        case USERS:
            return Users.CONTENT_TYPE;
        case USER_ID:
            return Users.CONTENT_ITEM_TYPE;
        case MESSAGES:
            return Messages.CONTENT_TYPE;
        case MESSAGE_ID:
            return Messages.CONTENT_ITEM_TYPE;
        case RATINGS:
            return Ratings.CONTENT_TYPE;
        case RATING_ID:
            return Ratings.CONTENT_ITEM_TYPE;
        case MODERATES:
            return Moderates.CONTENT_TYPE;
        case MODERATE_ID:
            return Moderates.CONTENT_ITEM_TYPE;
        case WMESSAGES:
            return wMessages.CONTENT_TYPE;
        case WMESSAGE_ID:
            return wMessages.CONTENT_ITEM_TYPE;
        case WRATES:
            return wRates.CONTENT_TYPE;
        case WRATE_ID:
            return wRates.CONTENT_ITEM_TYPE;
        case WMODERATES:
            return wModerates.CONTENT_TYPE;
        case WMODERATE_ID:
            return wModerates.CONTENT_ITEM_TYPE;
        case EXCEPTIONS:
            return Exceptions.CONTENT_TYPE;
        case EXCEPTION_ID:
            return Exceptions.CONTENT_ITEM_TYPE;
        case RATINGEXCEPTIONS:
            return RatingExceptions.CONTENT_TYPE;
        case RATINGEXCEPTION_ID:
            return RatingExceptions.CONTENT_ITEM_TYPE;
        case MODERATEEXCEPTIONS:
            return ModerateExceptions.CONTENT_TYPE;
        case MODERATEEXCEPTION_ID:
            return ModerateExceptions.CONTENT_ITEM_TYPE;
        case USERREQUESTS:
            return UserRequests.CONTENT_TYPE;
        case USERREQUEST_ID:
            return UserRequests.CONTENT_ITEM_TYPE;
        case DATAREQUESTS:
            return DataRequests.CONTENT_TYPE;
        case DATAREQUEST_ID:
            return DataRequests.CONTENT_ITEM_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
    	 
    	ContentValues values;
         if (initialValues != null) {
             values = new ContentValues(initialValues);
         } else {
             values = new ContentValues();
         } 
         String tableName = "";
         Uri contentURI = null;
         
    	
    	switch (sUriMatcher.match(uri)) {
         case FORUMGROUPS:
        	 if (values.containsKey(ForumGroups._ID) == false) {
                 values.put(ForumGroups._ID, 0);
             }
        	 if (values.containsKey(ForumGroups.FORUMGROUPNAME) == false) {
                 values.put(ForumGroups.FORUMGROUPNAME, "");
             }
        	 if (values.containsKey(ForumGroups.SORTORDER) == false) {
                 values.put(ForumGroups.SORTORDER, 0);
             }
             
             tableName = FORUMGROUPS_TABLE_NAME;
             contentURI = ForumGroups.CONTENT_URI;
             
             break;

         case FORUMS:
        	 if (values.containsKey(Forums._ID) == false) {
                 values.put(Forums._ID, 0);
             }
        	 if (values.containsKey(Forums.FORUMGROUPID) == false) {
                 values.put(Forums.FORUMGROUPID, 0);
             }
        	 if (values.containsKey(Forums.SHORTFORUMNAME) == false) {
                 values.put(Forums.SHORTFORUMNAME, "");
             }
             if (values.containsKey(Forums.FORUMNAME) == false) {
                 values.put(Forums.FORUMNAME, "");
             }
             if (values.containsKey(Forums.RATED) == false) {
                 values.put(Forums.RATED, 0);
             }
             if (values.containsKey(Forums.INTOP) == false) {
                 values.put(Forums.INTOP, 0);
             }
             if (values.containsKey(Forums.RATELIMIT) == false) {
                 values.put(Forums.RATELIMIT, 0);
             }
             
             tableName = FORUMS_TABLE_NAME;
             contentURI = Forums.CONTENT_URI;
             
             break;
             
         case USERS:
        	 if (values.containsKey(Users.USERNAME) == false) {
                 values.put(Users.USERNAME, "");
             }
        	 if (values.containsKey(Users.USERNICK) == false) {
                 values.put(Users.USERNICK, "");
             }
             if (values.containsKey(Users.REALNAME) == false) {
                 values.put(Users.REALNAME, "");
             }
             if (values.containsKey(Users.PUBLICEMAIL) == false) {
                 values.put(Users.PUBLICEMAIL, "");
             }
             if (values.containsKey(Users.HOMEPAGE) == false) {
                 values.put(Users.HOMEPAGE, "");
             }
             if (values.containsKey(Users.SPECIALIZATION) == false) {
                 values.put(Users.SPECIALIZATION, "");
             }
             if (values.containsKey(Users.WHEREFROM) == false) {
                 values.put(Users.WHEREFROM, "");
             }
             if (values.containsKey(Users.ORIGIN) == false) {
                 values.put(Users.ORIGIN, "");
             }
             if (values.containsKey(Users.USERCLASS) == false) {
                 values.put(Users.USERCLASS, 0);
             }
             
             tableName = USERS_TABLE_NAME;
             contentURI = Users.CONTENT_URI;
             
             break;
             
         case MESSAGES:
        	 if (values.containsKey(Messages.TOPICID) == false) {
                 values.put(Messages.TOPICID, 0);
             }
        	 if (values.containsKey(Messages.PARENTID) == false) {
                 values.put(Messages.PARENTID, 0);
             }
             if (values.containsKey(Messages.USERID) == false) {
                 values.put(Messages.USERID, 0);
             }
             if (values.containsKey(Messages.FORUMID) == false) {
                 values.put(Messages.FORUMID, 0);
             }
             if (values.containsKey(Messages.STATUS) == false) {
                 values.put(Messages.STATUS, 0);
             }
             if (values.containsKey(Messages.SUBJECT) == false) {
                 values.put(Messages.SUBJECT, "");
             }
             if (values.containsKey(Messages.MESSAGENAME) == false) {
                 values.put(Messages.MESSAGENAME, "");
             }
             if (values.containsKey(Messages.USERNICK) == false) {
                 values.put(Messages.USERNICK, "");
             }
             if (values.containsKey(Messages.MESSAGE) == false) {
                 values.put(Messages.MESSAGE, "");
             }
             if (values.containsKey(Messages.ARTICLEID) == false) {
                 values.put(Messages.ARTICLEID, 0);
             }
             if (values.containsKey(Messages.MESSAGEDATE) == false) {
                 values.put(Messages.MESSAGEDATE, new Date().toString());
             }
             if (values.containsKey(Messages.UPDATEDATE) == false) {
                 values.put(Messages.UPDATEDATE, new Date().toString());
             }
             if (values.containsKey(Messages.USERROLE) == false) {
                 values.put(Messages.USERROLE, "");
             }
             if (values.containsKey(Messages.USERTITLE) == false) {
                 values.put(Messages.USERTITLE, "");
             }
             if (values.containsKey(Messages.USERTITLECOLOR) == false) {
                 values.put(Messages.USERTITLECOLOR, "");
             }
             if (values.containsKey(Messages.LASTMODERATED) == false) {
                 values.put(Messages.LASTMODERATED, new Date().toString());
             }
             if (values.containsKey(Messages.CLOSED) == false) {
                 values.put(Messages.CLOSED, false);
             }
             
             
             tableName = MESSAGES_TABLE_NAME;
             contentURI = Messages.CONTENT_URI;
             
             break;
         case RATINGS:
        	 if (values.containsKey(Ratings.MESSAGEID) == false) {
                 values.put(Ratings.MESSAGEID, 0);
             }
        	 if (values.containsKey(Ratings.TOPICID) == false) {
                 values.put(Ratings.TOPICID, 0);
             }
             if (values.containsKey(Ratings.USERID) == false) {
                 values.put(Ratings.USERID, 0);
             }
             if (values.containsKey(Ratings.USERRATING) == false) {
                 values.put(Ratings.USERRATING, 0);
             }
             if (values.containsKey(Ratings.RATE) == false) {
                 values.put(Ratings.RATE, 0);
             }
             if (values.containsKey(Ratings.RATEDATE) == false) {
                 values.put(Ratings.RATEDATE, new Date().toString());
             }
             
             tableName = RATINGS_TABLE_NAME;
             contentURI = Ratings.CONTENT_URI;
             
             break;
         case MODERATES:
        	 if (values.containsKey(Moderates.MESSAGEID) == false) {
                 values.put(Moderates.MESSAGEID, 0);
             }
        	 if (values.containsKey(Moderates.TOPICID) == false) {
                 values.put(Moderates.TOPICID, 0);
             }
             if (values.containsKey(Moderates.USERID) == false) {
                 values.put(Moderates.USERID, 0);
             }
             if (values.containsKey(Moderates.FORUMID) == false) {
                 values.put(Moderates.FORUMID, 0);
             }
             if (values.containsKey(Moderates.CREATEDATE) == false) {
                 values.put(Moderates.CREATEDATE, new Date().toString());
             }
             
             tableName = MODERATES_TABLE_NAME;
             contentURI = Moderates.CONTENT_URI;
             
             break;
         case WMESSAGES:
        	 if (values.containsKey(wMessages.PARENTID) == false) {
                 values.put(wMessages.PARENTID, 0);
             }
             if (values.containsKey(wMessages.FORUMID) == false) {
                 values.put(wMessages.FORUMID, 0);
             }
             if (values.containsKey(wMessages.STATUS) == false) {
                 values.put(wMessages.STATUS, 0);
             }
             if (values.containsKey(wMessages.SUBJECT) == false) {
                 values.put(wMessages.SUBJECT, "");
             }
             if (values.containsKey(wMessages.MESSAGE) == false) {
                 values.put(wMessages.MESSAGE, "");
             }
             
             tableName = WMESSAGES_TABLE_NAME;
             contentURI = wMessages.CONTENT_URI;
             
             break;
         case WRATES:
        	 if (values.containsKey(wRates.MESSAGEID) == false) {
                 values.put(wRates.MESSAGEID, 0);
             }
        	 if (values.containsKey(wRates.STATUS) == false) {
                 values.put(wRates.STATUS, 0);
             }
             if (values.containsKey(wRates.RATE) == false) {
                 values.put(wRates.RATE, 0);
             }
             
             tableName = WRATES_TABLE_NAME;
             contentURI = wRates.CONTENT_URI;
             
             break;
         case WMODERATES:
        	 if (values.containsKey(wModerates.MESSAGEID) == false) {
                 values.put(wModerates.MESSAGEID, 0);
             }
        	 if (values.containsKey(wModerates.STATUS) == false) {
                 values.put(wModerates.STATUS, 0);
             }
        	 if (values.containsKey(wModerates.MODERATEACTION) == false) {
                 values.put(wModerates.MODERATEACTION, "");
             }
             if (values.containsKey(wModerates.MODERATETOFORUMID) == false) {
                 values.put(wModerates.MODERATETOFORUMID, 0);
             }
             if (values.containsKey(wModerates.DESCRIPTION) == false) {
                 values.put(wModerates.DESCRIPTION, "");
             }
             if (values.containsKey(wModerates.ASMODERATOR) == false) {
                 values.put(wModerates.ASMODERATOR, false);
             }
             
             tableName = WMODERATES_TABLE_NAME;
             contentURI = wModerates.CONTENT_URI;
             
             break;
         case EXCEPTIONS:
        	 if (values.containsKey(Exceptions.EXCEPTION) == false) {
                 values.put(Exceptions.EXCEPTION, "");
             }
        	 if (values.containsKey(Exceptions.LOCALMESSAGEID) == false) {
                 values.put(Exceptions.LOCALMESSAGEID, 0);
             }
             if (values.containsKey(Exceptions.INFO) == false) {
                 values.put(Exceptions.INFO, "");
             }
             
             tableName = EXCEPTIONS_TABLE_NAME;
             contentURI = Exceptions.CONTENT_URI;
             
             break;
         case RATINGEXCEPTIONS:
        	 if (values.containsKey(RatingExceptions.EXCEPTION) == false) {
                 values.put(RatingExceptions.EXCEPTION, "");
             }
        	 if (values.containsKey(RatingExceptions.LOCALRATINGID) == false) {
                 values.put(RatingExceptions.LOCALRATINGID, 0);
             }
             if (values.containsKey(RatingExceptions.INFO) == false) {
                 values.put(RatingExceptions.INFO, "");
             }
             
             tableName = RATINGEXCEPTIONS_TABLE_NAME;
             contentURI = RatingExceptions.CONTENT_URI;
             
             break;
         case MODERATEEXCEPTIONS:
        	 if (values.containsKey(ModerateExceptions.EXCEPTIONMESSAGE) == false) {
                 values.put(ModerateExceptions.EXCEPTIONMESSAGE, "");
             }
        	 if (values.containsKey(ModerateExceptions.LOCALMODERATEID) == false) {
                 values.put(ModerateExceptions.LOCALMODERATEID, 0);
             }
             if (values.containsKey(ModerateExceptions.INFO) == false) {
                 values.put(ModerateExceptions.INFO, "");
             }
             
             tableName = MODERATEEXCEPTIONS_TABLE_NAME;
             contentURI = ModerateExceptions.CONTENT_URI;
             
             break;
         case USERREQUESTS:
        	 if (values.containsKey(UserRequests.REQDATE) == false) {
                 values.put(UserRequests.REQDATE, new Date().toString());
             }
        	 if (values.containsKey(UserRequests.LASTROWVERSION) == false) {
                 values.put(UserRequests.LASTROWVERSION, 0);
             }
            
             tableName = USERREQUESTS_TABLE_NAME;
             contentURI = UserRequests.CONTENT_URI;
             
             break;
         case DATAREQUESTS:
        	 if (values.containsKey(DataRequests.REQDATE) == false) {
                 values.put(DataRequests.REQDATE, new Date().toString());
             }
        	 if (values.containsKey(DataRequests.MESSAGEROWVERSION) == false) {
                 values.put(DataRequests.MESSAGEROWVERSION, 0);
             }
        	 if (values.containsKey(DataRequests.MODERATEROWVERSION) == false) {
                 values.put(DataRequests.MODERATEROWVERSION, 0);
             }
        	 if (values.containsKey(DataRequests.RATINGROWVERSION) == false) {
                 values.put(DataRequests.RATINGROWVERSION, 0);
             }
            
             tableName = DATAREQUESTS_TABLE_NAME;
             contentURI = DataRequests.CONTENT_URI;
             
             break;
         default:
             throw new IllegalArgumentException("Unknown URI " + uri);
         }   
    	
    	SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(tableName, null, values);
        if (rowId > 0) {
            Uri fgUri = ContentUris.withAppendedId(contentURI, rowId);
            getContext().getContentResolver().notifyChange(fgUri, null);
            return fgUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case FORUMGROUPS:
            count = db.delete(FORUMGROUPS_TABLE_NAME, where, whereArgs);
            break;
        case FORUMGROUP_ID:
            String fgId = uri.getPathSegments().get(1);
            count = db.delete(FORUMGROUPS_TABLE_NAME, ForumGroups._ID + "=" + fgId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case FORUMS:
            count = db.delete(FORUMS_TABLE_NAME, where, whereArgs);
            break;
        case FORUM_ID:
            String fId = uri.getPathSegments().get(1);
            count = db.delete(FORUMS_TABLE_NAME, Forums._ID + "=" + fId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case USERS:
            count = db.delete(USERS_TABLE_NAME, where, whereArgs);
            break;
        case USER_ID:
            String uId = uri.getPathSegments().get(1);
            count = db.delete(USERS_TABLE_NAME, Users._ID + "=" + uId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case MESSAGES:
            count = db.delete(MESSAGES_TABLE_NAME, where, whereArgs);
            break;
        case MESSAGE_ID:
            String mId = uri.getPathSegments().get(1);
            count = db.delete(MESSAGES_TABLE_NAME, Messages._ID + "=" + mId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case RATINGS:
            count = db.delete(RATINGS_TABLE_NAME, where, whereArgs);
            break;
        case RATING_ID:
            String rId = uri.getPathSegments().get(1);
            count = db.delete(RATINGS_TABLE_NAME, Ratings._ID + "=" + rId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case MODERATES:
            count = db.delete(MODERATES_TABLE_NAME, where, whereArgs);
            break;
        case MODERATE_ID:
            String modId = uri.getPathSegments().get(1);
            count = db.delete(MODERATES_TABLE_NAME, Moderates._ID + "=" + modId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case WMESSAGES:
            count = db.delete(WMESSAGES_TABLE_NAME, where, whereArgs);
            break;
        case WMESSAGE_ID:
            String wmId = uri.getPathSegments().get(1);
            count = db.delete(WMESSAGES_TABLE_NAME, wMessages._ID + "=" + wmId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case WRATES:
            count = db.delete(WRATES_TABLE_NAME, where, whereArgs);
            break;
        case WRATE_ID:
            String wrId = uri.getPathSegments().get(1);
            count = db.delete(WRATES_TABLE_NAME, wRates._ID + "=" + wrId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case WMODERATES:
            count = db.delete(WMODERATES_TABLE_NAME, where, whereArgs);
            break;
        case WMODERATE_ID:
            String modWId = uri.getPathSegments().get(1);
            count = db.delete(WMODERATES_TABLE_NAME, wModerates._ID + "=" + modWId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case EXCEPTIONS:
            count = db.delete(EXCEPTIONS_TABLE_NAME, where, whereArgs);
            break;
        case EXCEPTION_ID:
            String excId = uri.getPathSegments().get(1);
            count = db.delete(EXCEPTIONS_TABLE_NAME, Exceptions._ID + "=" + excId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case RATINGEXCEPTIONS:
            count = db.delete(RATINGEXCEPTIONS_TABLE_NAME, where, whereArgs);
            break;
        case RATINGEXCEPTION_ID:
            String rexcId = uri.getPathSegments().get(1);
            count = db.delete(RATINGEXCEPTIONS_TABLE_NAME, RatingExceptions._ID + "=" + rexcId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case MODERATEEXCEPTIONS:
            count = db.delete(MODERATEEXCEPTIONS_TABLE_NAME, where, whereArgs);
            break;
        case MODERATEEXCEPTION_ID:
            String mexcId = uri.getPathSegments().get(1);
            count = db.delete(MODERATEEXCEPTIONS_TABLE_NAME, ModerateExceptions._ID + "=" + mexcId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case USERREQUESTS:
            count = db.delete(USERREQUESTS_TABLE_NAME, where, whereArgs);
            break;
        case USERREQUEST_ID:
            String urId = uri.getPathSegments().get(1);
            count = db.delete(USERREQUESTS_TABLE_NAME, UserRequests._ID + "=" + urId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case DATAREQUESTS:
            count = db.delete(DATAREQUESTS_TABLE_NAME, where, whereArgs);
            break;
        case DATAREQUEST_ID:
            String drId = uri.getPathSegments().get(1);
            count = db.delete(DATAREQUESTS_TABLE_NAME, DataRequests._ID + "=" + drId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case FORUMGROUPS:
            count = db.update(FORUMGROUPS_TABLE_NAME, values, where, whereArgs);
            break;
        case FORUMGROUP_ID:
            String noteFGId = uri.getPathSegments().get(1);
            count = db.update(FORUMGROUPS_TABLE_NAME, values, Forums._ID + "=" + noteFGId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case FORUMS:
            count = db.update(FORUMS_TABLE_NAME, values, where, whereArgs);
            break;
        case FORUM_ID:
            String noteFId = uri.getPathSegments().get(1);
            count = db.update(FORUMS_TABLE_NAME, values, Forums._ID + "=" + noteFId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case USERS:
            count = db.update(USERS_TABLE_NAME, values, where, whereArgs);
            break;
        case USER_ID:
            String noteUId = uri.getPathSegments().get(1);
            count = db.update(USERS_TABLE_NAME, values, Users._ID + "=" + noteUId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case MESSAGES:
            count = db.update(MESSAGES_TABLE_NAME, values, where, whereArgs);
            break;
        case MESSAGE_ID:
            String noteMId = uri.getPathSegments().get(1);
            count = db.update(MESSAGES_TABLE_NAME, values, Messages._ID + "=" + noteMId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case RATINGS:
            count = db.update(RATINGS_TABLE_NAME, values, where, whereArgs);
            break;
        case RATING_ID:
            String noteRId = uri.getPathSegments().get(1);
            count = db.update(RATINGS_TABLE_NAME, values, Ratings._ID + "=" + noteRId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case MODERATES:
            count = db.update(MODERATES_TABLE_NAME, values, where, whereArgs);
            break;
        case MODERATE_ID:
            String noteModId = uri.getPathSegments().get(1);
            count = db.update(MODERATES_TABLE_NAME, values, Moderates._ID + "=" + noteModId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case WMESSAGES:
            count = db.update(WMESSAGES_TABLE_NAME, values, where, whereArgs);
            break;
        case WMESSAGE_ID:
            String noteWMId = uri.getPathSegments().get(1);
            count = db.update(WMESSAGES_TABLE_NAME, values, wMessages._ID + "=" + noteWMId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case WRATES:
            count = db.update(WRATES_TABLE_NAME, values, where, whereArgs);
            break;
        case WRATE_ID:
            String noteWRId = uri.getPathSegments().get(1);
            count = db.update(WRATES_TABLE_NAME, values, wRates._ID + "=" + noteWRId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case WMODERATES:
            count = db.update(WMODERATES_TABLE_NAME, values, where, whereArgs);
            break;
        case WMODERATE_ID:
            String noteModWId = uri.getPathSegments().get(1);
            count = db.update(WMODERATES_TABLE_NAME, values, wModerates._ID + "=" + noteModWId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case EXCEPTIONS:
            count = db.update(EXCEPTIONS_TABLE_NAME, values, where, whereArgs);
            break;
        case EXCEPTION_ID:
            String noteExcId = uri.getPathSegments().get(1);
            count = db.update(EXCEPTIONS_TABLE_NAME, values, Exceptions._ID + "=" + noteExcId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case RATINGEXCEPTIONS:
            count = db.update(RATINGEXCEPTIONS_TABLE_NAME, values, where, whereArgs);
            break;
        case RATINGEXCEPTION_ID:
            String noteRExcId = uri.getPathSegments().get(1);
            count = db.update(RATINGEXCEPTIONS_TABLE_NAME, values, RatingExceptions._ID + "=" + noteRExcId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case MODERATEEXCEPTIONS:
            count = db.update(MODERATEEXCEPTIONS_TABLE_NAME, values, where, whereArgs);
            break;
        case MODERATEEXCEPTION_ID:
            String noteMExcId = uri.getPathSegments().get(1);
            count = db.update(MODERATEEXCEPTIONS_TABLE_NAME, values, ModerateExceptions._ID + "=" + noteMExcId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case USERREQUESTS:
            count = db.update(USERREQUESTS_TABLE_NAME, values, where, whereArgs);
            break;
        case USERREQUEST_ID:
            String noteURId = uri.getPathSegments().get(1);
            count = db.update(USERREQUESTS_TABLE_NAME, values, UserRequests._ID + "=" + noteURId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        case DATAREQUESTS:
            count = db.update(DATAREQUESTS_TABLE_NAME, values, where, whereArgs);
            break;
        case DATAREQUEST_ID:
            String noteDRId = uri.getPathSegments().get(1);
            count = db.update(DATAREQUESTS_TABLE_NAME, values, DataRequests._ID + "=" + noteDRId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "forumgroups", FORUMGROUPS);
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "forumgroups/#", FORUMGROUP_ID);
        
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "forums", FORUMS);
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "forums/#", FORUM_ID);
        
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "users", USERS);
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "users/#", USER_ID);
        
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "messages", MESSAGES);
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "messages/#", MESSAGE_ID);
        
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "ratings", RATINGS);
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "ratings/#", RATING_ID);
        
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "moderates", MODERATES);
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "moderates/#", MODERATE_ID);
        
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "wmessages", WMESSAGES);
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "wmessages/#", WMESSAGE_ID);
        
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "wrates", WRATES);
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "wrates/#", WRATE_ID);
        
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "wmoderates", WMODERATES);
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "wmoderates/#", WMODERATE_ID);
        
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "exceptions", EXCEPTIONS);
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "exceptions/#", EXCEPTION_ID);
        
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "ratingexceptions", RATINGEXCEPTIONS);
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "ratingexceptions/#", RATINGEXCEPTION_ID);
        
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "moderateexceptions", MODERATEEXCEPTIONS);
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "moderateexceptions/#", MODERATEEXCEPTION_ID);
        
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "userrequests", USERREQUESTS);
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "userrequests/#", USERREQUEST_ID);
        
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "datarequests", DATAREQUESTS);
        sUriMatcher.addURI(RsdnAndroidDBStatic.AUTHORITY, "datarequests/#", DATAREQUEST_ID);

        sForumGroupsProjectionMap = new HashMap<String, String>();
        sForumGroupsProjectionMap.put(ForumGroups._ID, ForumGroups._ID);
        sForumGroupsProjectionMap.put(ForumGroups.FORUMGROUPNAME, ForumGroups.FORUMGROUPNAME);
        sForumGroupsProjectionMap.put(ForumGroups.SORTORDER, ForumGroups.SORTORDER);
        
        sForumsProjectionMap = new HashMap<String, String>();
        sForumsProjectionMap.put(Forums._ID, Forums._ID);
        sForumsProjectionMap.put(Forums.FORUMGROUPID, Forums.FORUMGROUPID);
        sForumsProjectionMap.put(Forums.SHORTFORUMNAME, Forums.SHORTFORUMNAME);
        sForumsProjectionMap.put(Forums.FORUMNAME, Forums.FORUMNAME);
        sForumsProjectionMap.put(Forums.RATED, Forums.RATED);
        sForumsProjectionMap.put(Forums.INTOP, Forums.INTOP);
        sForumsProjectionMap.put(Forums.RATELIMIT, Forums.RATELIMIT);
        
        sUsersProjectionMap = new HashMap<String, String>();
        sUsersProjectionMap.put(Users._ID, Users._ID);
        sUsersProjectionMap.put(Users.USERNAME, Users.USERNAME);
        sUsersProjectionMap.put(Users.USERNICK, Users.USERNICK);
        sUsersProjectionMap.put(Users.REALNAME, Users.REALNAME);
        sUsersProjectionMap.put(Users.PUBLICEMAIL, Users.PUBLICEMAIL);
        sUsersProjectionMap.put(Users.HOMEPAGE, Users.HOMEPAGE);
        sUsersProjectionMap.put(Users.SPECIALIZATION, Users.SPECIALIZATION);
        sUsersProjectionMap.put(Users.WHEREFROM, Users.WHEREFROM);
        sUsersProjectionMap.put(Users.ORIGIN, Users.ORIGIN);
        sUsersProjectionMap.put(Users.USERCLASS, Users.USERCLASS);
        
        sMessagesProjectionMap = new HashMap<String, String>();
        sMessagesProjectionMap.put(Messages._ID, Messages._ID);
        sMessagesProjectionMap.put(Messages.TOPICID, Messages.TOPICID);
        sMessagesProjectionMap.put(Messages.USERNICK, Messages.USERNICK);
        sMessagesProjectionMap.put(Messages.PARENTID, Messages.PARENTID);
        sMessagesProjectionMap.put(Messages.USERID, Messages.USERID);
        sMessagesProjectionMap.put(Messages.FORUMID, Messages.FORUMID);
        sMessagesProjectionMap.put(Messages.STATUS, wMessages.STATUS);
        sMessagesProjectionMap.put(Messages.SUBJECT, Messages.SUBJECT);
        sMessagesProjectionMap.put(Messages.MESSAGENAME, Messages.MESSAGENAME);
        sMessagesProjectionMap.put(Messages.USERNICK, Messages.USERNICK);
        sMessagesProjectionMap.put(Messages.MESSAGE, Messages.MESSAGE);
        sMessagesProjectionMap.put(Messages.ARTICLEID, Messages.ARTICLEID);
        sMessagesProjectionMap.put(Messages.MESSAGEDATE, Messages.MESSAGEDATE);
        sMessagesProjectionMap.put(Messages.UPDATEDATE, Messages.UPDATEDATE);
        sMessagesProjectionMap.put(Messages.USERROLE, Messages.USERROLE);
        sMessagesProjectionMap.put(Messages.USERTITLE, Messages.USERTITLE);
        sMessagesProjectionMap.put(Messages.USERTITLECOLOR, Messages.USERTITLECOLOR);
        sMessagesProjectionMap.put(Messages.LASTMODERATED, Messages.LASTMODERATED);
        sMessagesProjectionMap.put(Messages.CLOSED, Messages.CLOSED);
        
        sRatingsProjectionMap = new HashMap<String, String>();
        sRatingsProjectionMap.put(Ratings._ID, Ratings._ID);
        sRatingsProjectionMap.put(Ratings.MESSAGEID, Ratings.MESSAGEID);
        sRatingsProjectionMap.put(Ratings.TOPICID, Ratings.TOPICID);
        sRatingsProjectionMap.put(Ratings.USERID, Ratings.USERID);
        sRatingsProjectionMap.put(Ratings.USERRATING, Ratings.USERRATING);
        sRatingsProjectionMap.put(Ratings.RATE, Ratings.RATE);
        sRatingsProjectionMap.put(Ratings.RATEDATE, Ratings.RATEDATE);
        
        sModeratesProjectionMap = new HashMap<String, String>();
        sModeratesProjectionMap.put(Moderates._ID, Moderates._ID);
        sModeratesProjectionMap.put(Moderates.MESSAGEID, Moderates.MESSAGEID);
        sModeratesProjectionMap.put(Moderates.TOPICID, Moderates.TOPICID);
        sModeratesProjectionMap.put(Moderates.USERID, Moderates.USERID);
        sModeratesProjectionMap.put(Moderates.FORUMID, Moderates.FORUMID);
        sModeratesProjectionMap.put(Moderates.CREATEDATE, Moderates.CREATEDATE);
        
        swMessagesProjectionMap = new HashMap<String, String>();
        swMessagesProjectionMap.put(wMessages._ID, wMessages._ID);
        swMessagesProjectionMap.put(wMessages.PARENTID, wMessages.PARENTID);
        swMessagesProjectionMap.put(wMessages.FORUMID, wMessages.FORUMID);
        swMessagesProjectionMap.put(wMessages.STATUS, wMessages.STATUS);
        swMessagesProjectionMap.put(wMessages.SUBJECT, wMessages.SUBJECT);
        swMessagesProjectionMap.put(wMessages.MESSAGE, wMessages.MESSAGE);
        
        swRatesProjectionMap = new HashMap<String, String>();
        swRatesProjectionMap.put(wRates._ID, wRates._ID);
        swRatesProjectionMap.put(wRates.MESSAGEID, wRates.MESSAGEID);
        swRatesProjectionMap.put(wRates.STATUS, wRates.STATUS);
        swRatesProjectionMap.put(wRates.RATE, wRates.RATE);
        
        swModeratesProjectionMap = new HashMap<String, String>();
        swModeratesProjectionMap.put(wModerates._ID, Moderates._ID);
        swModeratesProjectionMap.put(wModerates.MESSAGEID, wModerates.MESSAGEID);
        swModeratesProjectionMap.put(wModerates.STATUS, wModerates.STATUS);
        swModeratesProjectionMap.put(wModerates.MODERATEACTION, wModerates.MODERATEACTION);
        swModeratesProjectionMap.put(wModerates.MODERATETOFORUMID, wModerates.MODERATETOFORUMID);
        swModeratesProjectionMap.put(wModerates.DESCRIPTION, wModerates.DESCRIPTION);
        swModeratesProjectionMap.put(wModerates.ASMODERATOR, wModerates.ASMODERATOR);
        
        sExceptionsProjectionMap = new HashMap<String, String>();
        sExceptionsProjectionMap.put(Exceptions._ID, Exceptions._ID);
        sExceptionsProjectionMap.put(Exceptions.EXCEPTION, Exceptions.EXCEPTION);
        sExceptionsProjectionMap.put(Exceptions.LOCALMESSAGEID, Exceptions.LOCALMESSAGEID);
        sExceptionsProjectionMap.put(Exceptions.INFO, Exceptions.INFO);
        
        sRatingExceptionsProjectionMap = new HashMap<String, String>();
        sRatingExceptionsProjectionMap.put(RatingExceptions._ID, Exceptions._ID);
        sRatingExceptionsProjectionMap.put(RatingExceptions.EXCEPTION, RatingExceptions.EXCEPTION);
        sRatingExceptionsProjectionMap.put(RatingExceptions.LOCALRATINGID, RatingExceptions.LOCALRATINGID);
        sRatingExceptionsProjectionMap.put(RatingExceptions.INFO, RatingExceptions.INFO);
        
        sModerateExceptionsProjectionMap = new HashMap<String, String>();
        sModerateExceptionsProjectionMap.put(ModerateExceptions._ID, ModerateExceptions._ID);
        sModerateExceptionsProjectionMap.put(ModerateExceptions.EXCEPTIONMESSAGE, ModerateExceptions.EXCEPTIONMESSAGE);
        sModerateExceptionsProjectionMap.put(ModerateExceptions.LOCALMODERATEID, ModerateExceptions.LOCALMODERATEID);
        sModerateExceptionsProjectionMap.put(ModerateExceptions.INFO, ModerateExceptions.INFO);
        
        sUserRequestsProjectionMap = new HashMap<String, String>();
        sUserRequestsProjectionMap.put(UserRequests._ID, UserRequests._ID);
        sUserRequestsProjectionMap.put(UserRequests.REQDATE, UserRequests.REQDATE);
        sUserRequestsProjectionMap.put(UserRequests.LASTROWVERSION, UserRequests.LASTROWVERSION);
        
        sDataRequestsProjectionMap = new HashMap<String, String>();
        sDataRequestsProjectionMap.put(DataRequests._ID, DataRequests._ID);
        sDataRequestsProjectionMap.put(DataRequests.REQDATE, UserRequests.REQDATE);
        sDataRequestsProjectionMap.put(DataRequests.MESSAGEROWVERSION, DataRequests.MESSAGEROWVERSION);
        sDataRequestsProjectionMap.put(DataRequests.MODERATEROWVERSION, DataRequests.MODERATEROWVERSION);
        sDataRequestsProjectionMap.put(DataRequests.RATINGROWVERSION, DataRequests.RATINGROWVERSION);
    }
}
