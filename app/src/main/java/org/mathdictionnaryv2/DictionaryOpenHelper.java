package org.mathdictionnaryv2;

/**
 * Created by Larbi on 30/06/2018.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import static org.mathdictionnaryv2.DictionaryOpenHelper.Constants.DATABASE_NAME;
import static org.mathdictionnaryv2.DictionaryOpenHelper.Constants.DATABASE_VERSION;

/**
 * This creates/opens the database.
 */
public class DictionaryOpenHelper extends SQLiteOpenHelper {
    private final Context mHelperContext;

    /**
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    public DictionaryOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                                int version) {
        super(context, name, factory, version);
        mHelperContext = context;
    }

    public static class Constants implements BaseColumns {
        // The database name
        public static final String DATABASE_NAME = "MathDictionaryDB.db";

        // The database version
        public static final int DATABASE_VERSION = 1;

        // The table Name
        public static final String MY_TABLE = "Translate";

        // Noms de colonnes
        public static final String KEY_COL_ID = "_id";// Mandatory

        public static final String KEY_COL_EN = "en";

        public static final String KEY_COL_FR = "fr";

        public static final String KEY_COL_AR = "ar";

        // Index des colonnes
        public static final int ID_COLUMN = 0;
        public static final int EN_COLUMN = 1;
        public static final int FR_COLUMN = 2;
        public static final int AR_COLUMN = 3;
    }

    // The static string to create the database.
    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + Constants.MY_TABLE + "(" + Constants.KEY_COL_ID
            + " integer primary key autoincrement, " + Constants.KEY_COL_EN + " TEXT, "
            + Constants.KEY_COL_FR + " TEXT, "
            + Constants.KEY_COL_AR + " TEXT) ";

    DictionaryOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mHelperContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the new database using the SQL string Database_create
        db.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("DictionaryOpenHelper", "Mise à jour de la version " + oldVersion
                + " vers la version " + newVersion
                + ", les anciennes données seront détruites ");
        // Drop the old database
        db.execSQL("DROP TABLE IF EXISTS " + Constants.MY_TABLE);
        // Create the new one
        onCreate(db);
        // or do a smartest stuff
    }

}