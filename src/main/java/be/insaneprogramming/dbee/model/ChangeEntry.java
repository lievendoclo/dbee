package be.insaneprogramming.dbee.model;

public class ChangeEntry {

    public static final String CHANGELOG_COLLECTION = "dbchangelog"; // ! Don't change due to backward compatibility issue

    public static final String KEY_CHANGEID = "changeId";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_CHANGELOGCLASS = "changeLogClass";
    public static final String KEY_CHANGESETMETHOD = "changeSetMethod";

    private final String author;
    private final long timestamp;
    private final String changeLogClass;
    private final String changeSetMethodName;

    public ChangeEntry(String author, long timestamp, String changeLogClass, String changeSetMethodName) {
        this.author = author;
        this.timestamp = timestamp;
        this.changeLogClass = changeLogClass;
        this.changeSetMethodName = changeSetMethodName;
    }

    @Override
    public String toString() {
        return "ChangeEntry{" +
               "author='" + author + '\'' +
               ", timestamp=" + timestamp +
               ", changeLogClass='" + changeLogClass + '\'' +
               ", changeSetMethodName='" + changeSetMethodName + '\'' +
               '}';
    }

    public String getAuthor() {
        return this.author;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getChangeLogClass() {
        return this.changeLogClass;
    }

    public String getChangeSetMethodName() {
        return this.changeSetMethodName;
    }
}