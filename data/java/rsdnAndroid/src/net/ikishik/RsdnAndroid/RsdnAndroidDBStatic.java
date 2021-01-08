package net.ikishik.RsdnAndroid;


import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Convenience definitions for NotePadProvider
 */
public final class RsdnAndroidDBStatic 
{
    public static final String AUTHORITY = "net.ikishik.provider.RsdnAndroid";

    // This class cannot be instantiated
    private RsdnAndroidDBStatic() {}
    
    /**
     * ForumGroups table
     */
    public static final class ForumGroups implements BaseColumns 
    {
        // This class cannot be instantiated
        private ForumGroups() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/forumgroups");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/net.ikishik.forumgroup";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/net.ikishik.forumgroup";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "sortOrder DESC";

        /**
         * The name of the forum
         * <P>Type: TEXT</P>
         */
        public static final String FORUMGROUPNAME = "forumGroupName";

        /**
         * The sortOrder of the forum
         * <P>Type: INTEGER</P>
         */
        public static final String SORTORDER = "sortOrder";
    }
    
    /**
     * Forums table
     */
    public static final class Forums implements BaseColumns 
    {
        // This class cannot be instantiated
        private Forums() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/forums");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/net.ikishik.forum";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/net.ikishik.forum";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id DESC";
        
        /**
         * The id of the forum group
         * <P>Type: INTEGER</P>
         */
        public static final String FORUMGROUPID = "forumGroupId";

        /**
         * The short name of the forum
         * <P>Type: VARCHAR(255)</P>
         */
        public static final String SHORTFORUMNAME = "shortForumName";
        
        /**
         * The name of the forum
         * <P>Type: VARCHAR(255)</P>
         */
        public static final String FORUMNAME = "forumName";
        
        /**
         * The rated of the forum
         * <P>Type: INTEGER</P>
         */
        public static final String RATED = "rated";
        
        /**
         * The inTop of the forum
         * <P>Type: INTEGER</P>
         */
        public static final String INTOP = "inTop";
        
        /**
         * The rateLimit of the forum
         * <P>Type: INTEGER</P>
         */
        public static final String RATELIMIT = "rateLimit";
    }
    
    /**
     * Users table
     */
    public static final class Users implements BaseColumns 
    {
        // This class cannot be instantiated
        private Users() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/users");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/net.ikishik.user";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/net.ikishik.user";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id DESC";

        /**
         * The name of the user
         * <P>Type: VARCHAR(255)</P>
         */
        public static final String USERNAME = "userName";

        /**
         * The nick of the user
         * <P>Type: VARCHAR(255)</P>
         */
        public static final String USERNICK = "userNick";
        
        /**
         * The real name of the user
         * <P>Type: VARCHAR(255)</P>
         */
        public static final String REALNAME = "realName";
        
        /**
         * The public email of the user
         * <P>Type: VARCHAR(255)</P>
         */
        public static final String PUBLICEMAIL = "publicEmail";
        
        /**
         * The home page of the user
         * <P>Type: TEXT</P>
         */
        public static final String HOMEPAGE = "homePage";
        
        /**
         * The specialization of the user
         * <P>Type: TEXT</P>
         */
        public static final String SPECIALIZATION = "specialization";
        
        /**
         * The whereFrom the user
         * <P>Type: VARCHAR(255)</P>
         */
        public static final String WHEREFROM = "whereFrom";
        
        /**
         * The origin of the user
         * <P>Type: TEXT</P>
         */
        public static final String ORIGIN = "origin";
        
        /**
         * The userClass of the user
         * <P>Type: INTEGER</P>
         */
        public static final String USERCLASS = "userClass";
        
    }
    
    /**
     * Messages table
     */
    public static final class Messages implements BaseColumns 
    {
        // This class cannot be instantiated
        private Messages() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/messages");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/net.ikishik.message";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/net.ikishik.message";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id DESC";

        /**
         * The id of the topic 
         * <P>Type: INTEGER</P>
         */
        public static final String TOPICID = "topicId";
        
        /**
         * The id of the parent 
         * <P>Type: INTEGER</P>
         */
        public static final String PARENTID = "parentId";
        
        /**
         * The id of the user 
         * <P>Type: INTEGER</P>
         */
        public static final String USERID = "userId";
        
        /**
         * The id of the forum 
         * <P>Type: INTEGER</P>
         */
        public static final String FORUMID = "forumId";
        
        /**
         * The status of the message
         * <P>Type: INTEGER</P>
         */
        public static final String STATUS = "Status";

        /**
         * The subject of the message
         * <P>Type: TEXT</P>
         */
        public static final String SUBJECT = "subject";
        
        /**
         * The messageName of the message
         * <P>Type: VARCHAR(255)</P>
         */
        public static final String MESSAGENAME = "messageName";
        
        /**
         * The userNick in the message
         * <P>Type: VARCHAR(255)</P>
         */
        public static final String USERNICK = "userNick";
        
        /**
         * The message text of the message
         * <P>Type: TEXT</P>
         */
        public static final String MESSAGE = "message";
        
        /**
         * The id of the article
         * <P>Type: INTEGER</P>
         */
        public static final String ARTICLEID = "articleId";
        
        /**
         * The messageDate of the message
         * <P>Type: DATETIME</P>
         */
        public static final String MESSAGEDATE = "messageDate";
        
        /**
         * The updateDate of the message
         * <P>Type: DATETIME</P>
         */
        public static final String UPDATEDATE = "updateDate";
        
        /**
         * The Role of the user
         * <P>Type: VARCHAR(255)</P>
         */
        public static final String USERROLE = "userRole";
        
        /**
         * The Title of the user
         * <P>Type: VARCHAR(255)</P>
         */
        public static final String USERTITLE = "userTitle";
        
        /**
         * The TitleColor of the user
         * <P>Type: VARCHAR(255)</P>
         */
        public static final String USERTITLECOLOR = "userTitleColor";
        
        /**
         * The lastModerated of the message
         * <P>Type: DATETIME</P>
         */
        public static final String LASTMODERATED = "lastModerated";
        
        /**
         * The closed status of the message
         * <P>Type: BOOLEAN</P>
         */
        public static final String CLOSED = "closed";
        
    }

    /**
     * Ratings table
     */
    public static final class Ratings implements BaseColumns 
    {
        // This class cannot be instantiated
        private Ratings() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/ratings");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/net.ikishik.rating";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/net.ikishik.rating";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id DESC";

        /**
         * The id of the message
         * <P>Type: INTEGER</P>
         */
        public static final String MESSAGEID = "messageId";

        /**
         * The id of the topic
         * <P>Type: INTEGER</P>
         */
        public static final String TOPICID = "topicId";
        
        /**
         * The id of the user
         * <P>Type: INTEGER</P>
         */
        public static final String USERID = "userId";
        
        /**
         * The Rating of the user
         * <P>Type: INTEGER</P>
         */
        public static final String USERRATING = "userRating";
        
        /**
         * The rate of the message
         * <P>Type: INTEGER</P>
         */
        public static final String RATE = "rate";
        
        /**
         * The Date of the rate
         * <P>Type: DATETIME</P>
         */
        public static final String RATEDATE = "rateDate";
    }

    /**
     * Moderates table
     */
    public static final class Moderates implements BaseColumns 
    {
        // This class cannot be instantiated
        private Moderates() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/moderates");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/net.ikishik.moderate";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/net.ikishik.moderate";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id DESC";

        /**
         * The id of the message
         * <P>Type: INTEGER</P>
         */
        public static final String MESSAGEID = "messageId";

        /**
         * The id of the topic
         * <P>Type: INTEGER</P>
         */
        public static final String TOPICID = "topicId";
        
        /**
         * The id of the user
         * <P>Type: INTEGER</P>
         */
        public static final String USERID = "userId";
        
        /**
         * The id of the forum
         * <P>Type: INTEGER</P>
         */
        public static final String FORUMID = "forumId";
        
        /**
         * The create Date of the moderate
         * <P>Type: DATETIME</P>
         */
        public static final String CREATEDATE = "createDate";
    }

    /**
     * wMessages table
     */
    public static final class wMessages implements BaseColumns 
    {
        // This class cannot be instantiated
        private wMessages() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/wmessages");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/net.ikishik.wmessage";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/net.ikishik.wmessage";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id DESC";

        /**
         * The parentId of the message
         * <P>Type: INTEGER</P>
         */
        public static final String PARENTID = "parentId";

        /**
         * The forumId of the message
         * <P>Type: INTEGER</P>
         */
        public static final String FORUMID = "forumId";
        
        /**
         * The status of the message
         * <P>Type: INTEGER</P>
         */
        public static final String STATUS = "Status";
        
        /**
         * The subject of the message
         * <P>Type: TEXT</P>
         */
        public static final String SUBJECT = "subject";
        
        /**
         * The message text of the message
         * <P>Type: TEXT</P>
         */
        public static final String MESSAGE = "message";
        
    }

    /**
     * wRates table
     */
    public static final class wRates implements BaseColumns 
    {
        // This class cannot be instantiated
        private wRates() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/wrates");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/net.ikishik.wrate";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/net.ikishik.wrate";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id DESC";

        /**
         * The messageId of the rate
         * <P>Type: INTEGER</P>
         */
        public static final String MESSAGEID = "messageId";
        
        /**
         * The status of the rates
         * <P>Type: INTEGER</P>
         */
        public static final String STATUS = "Status";

        /**
         * The rate of the message
         * <P>Type: INTEGER</P>
         */
        public static final String RATE = "rate";
        
    }

    /**
     * wModerates table
     */
    public static final class wModerates implements BaseColumns 
    {
        // This class cannot be instantiated
        private wModerates() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/wmoderates");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/net.ikishik.wmoderate";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/net.ikishik.wmoderate";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id DESC";

        /**
         * The MessageId of the moderate
         * <P>Type: INTEGER</P>
         */
        public static final String MESSAGEID = "MessageId";
        
        /**
         * The status of the moderate
         * <P>Type: INTEGER</P>
         */
        public static final String STATUS = "Status";

        /**
         * The ModerateAction
         * <P>Type: VARCHAR(255)</P>
         */
        public static final String MODERATEACTION = "ModerateAction";
        
        /**
         * The ModerateToForumId of the moderate
         * <P>Type: INTEGER</P>
         */
        public static final String MODERATETOFORUMID = "ModerateToForumId";
        
        /**
         * The Description of the moderate
         * <P>Type: TEXT</P>
         */
        public static final String DESCRIPTION = "Description";
        
        /**
         * The AsModerator of the moderate
         * <P>Type: BOOLEAN</P>
         */
        public static final String ASMODERATOR = "AsModerator";
        
    }

    /**
     * Exceptions table
     */
    public static final class Exceptions implements BaseColumns 
    {
        // This class cannot be instantiated
        private Exceptions() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/exceptions");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/net.ikishik.exception";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/net.ikishik.exception";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id DESC";

        /**
         * The exception text
         * <P>Type: TEXT</P>
         */
        public static final String EXCEPTION = "exception";

        /**
         * The localMessageId of the exception
         * <P>Type: INTEGER</P>
         */
        public static final String LOCALMESSAGEID = "localMessageId";
        
        /**
         * The info of the exception
         * <P>Type: TEXT</P>
         */
        public static final String INFO = "info";
         
    }
    
    /**
     * RatingExceptions table
     */
    public static final class RatingExceptions implements BaseColumns 
    {
        // This class cannot be instantiated
        private RatingExceptions() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/ratingexceptions");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/net.ikishik.ratingexception";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/net.ikishik.ratingexception";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id DESC";

        /**
         * The exception text
         * <P>Type: TEXT</P>
         */
        public static final String EXCEPTION = "exception";

        /**
         * The localRatingId of the exception
         * <P>Type: INTEGER</P>
         */
        public static final String LOCALRATINGID = "localRatingId";
        
        /**
         * The info of the exception
         * <P>Type: TEXT</P>
         */
        public static final String INFO = "info";
         
    }
    
    /**
     * ModerateExceptions table
     */
    public static final class ModerateExceptions implements BaseColumns 
    {
        // This class cannot be instantiated
        private ModerateExceptions() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/moderateexceptions");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/net.ikishik.moderateexception";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/net.ikishik.moderateexception";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "_id DESC";

        /**
         * The ExceptionMessage text
         * <P>Type: TEXT</P>
         */
        public static final String EXCEPTIONMESSAGE = "ExceptionMessage";

        /**
         * The localRatingId of the exception
         * <P>Type: INTEGER</P>
         */
        public static final String LOCALMODERATEID = "LocalModerateId";
        
        /**
         * The info of the exception
         * <P>Type: TEXT</P>
         */
        public static final String INFO = "Info";
         
    }

    /**
     * UserRequests table
     */
    public static final class UserRequests implements BaseColumns 
    {
        // This class cannot be instantiated
        private UserRequests() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/userrequests");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/net.ikishik.userrequest";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/net.ikishik.userrequest";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "ReqDate DESC";

        /**
         * The ReqDate of the UserRequests
         * <P>Type: DATETIME</P>
         */
        public static final String REQDATE = "ReqDate";

        /**
         * The lastRowVersion of the UserRequests
         * <P>Type: BLOB</P>
         */
        public static final String LASTROWVERSION = "lastRowVersion";
         
    }
    
    /**
     * DataRequests table
     */
    public static final class DataRequests implements BaseColumns 
    {
        // This class cannot be instantiated
        private DataRequests() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/datarequests");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/net.ikishik.datarequest";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/net.ikishik.datarequest";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "ReqDate DESC";

        /**
         * The ReqDate of the DataRequests
         * <P>Type: DATETIME</P>
         */
        public static final String REQDATE = "ReqDate";

        /**
         * The messageRowVersion of the DataRequests
         * <P>Type: BLOB</P>
         */
        public static final String MESSAGEROWVERSION = "messageRowVersion";
        
        /**
         * The moderateRowVersion of the DataRequests
         * <P>Type: BLOB</P>
         */
        public static final String MODERATEROWVERSION = "moderateRowVersion";
        
        /**
         * The ratingRowVersion of the DataRequests
         * <P>Type: BLOB</P>
         */
        public static final String RATINGROWVERSION = "ratingRowVersion";
         
    }
}

