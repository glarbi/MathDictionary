package org.mathdictionnaryv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Larbi on 02/07/2018.
 */

public class DictionaryBDD {
    // The database name
    private static final String DATABASE_NAME = "MathDictionaryDB.db";

    // The database version
    private static final int DATABASE_VERSION = 1;

    // The table Name
    private static final String MY_TABLE = "Translate";

    // Noms de colonnes
    private static final String KEY_COL_ID = "_id";// Mandatory

    private static final String KEY_COL_EN = "en";

    private static final String KEY_COL_FR = "fr";

    private static final String KEY_COL_AR = "ar";

    // Index des colonnes
    private static final int ID_COLUMN = 0;
    private static final int EN_COLUMN = 1;
    private static final int FR_COLUMN = 2;
    private static final int AR_COLUMN = 3;

    private SQLiteDatabase bdd;

    private DictionaryOpenHelper dicoOpenHelper;

    public DictionaryBDD(Context context) {
        //On crée la BDD et sa table
        dicoOpenHelper = new DictionaryOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() {
        //on ouvre la BDD en écriture
        bdd = dicoOpenHelper.getWritableDatabase();
    }

    public void close() {
        //on ferme l'accès à la BDD
        bdd.close();
    }

    public SQLiteDatabase getBDD() {
        return bdd;
    }

    public DictionaryOpenHelper getDicoOpenHelper() {
        return dicoOpenHelper;
    }

    public void dropTable() {
        Log.w("DictionaryOpenHelper", "Suppression de la table");
        // Drop the old database
        bdd.execSQL("DROP TABLE IF EXISTS " + MY_TABLE);
    }

    public long insertMot(Mot mot) {
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(KEY_COL_EN, mot.getEn());
        values.put(KEY_COL_FR, mot.getFr());
        values.put(KEY_COL_AR, mot.getAr());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(MY_TABLE, null, values);
    }

    public int updateMot(int id, Mot mot) {
        //La mise à jour d'un mot dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simplement préciser quel mot on doit mettre à jour grâce à l'ID
        ContentValues values = new ContentValues();
        values.put(KEY_COL_EN, mot.getEn());
        values.put(KEY_COL_FR, mot.getFr());
        values.put(KEY_COL_AR, mot.getAr());
        return bdd.update(MY_TABLE, values, KEY_COL_ID + " = " + id, null);
    }

    public int removeMotWithID(int id) {
        //Suppression d'un mot de la BDD grâce à l'ID
        return bdd.delete(MY_TABLE, KEY_COL_ID + " = " + id, null);
    }

    public Mot[] getMotsWithEn(String englishWord) {
        return getMots(KEY_COL_EN + " LIKE \"%" + englishWord + "%\"");
    }

    public Mot[] getMotsWithFr(String frenchWord){
        return getMots(KEY_COL_FR + " LIKE \"%" + frenchWord +"%\"");
    }

    public Mot[] getMotsWithAr(String arabicWord){
        return getMots(KEY_COL_AR + " LIKE \"%" + arabicWord +"%\"");
    }

    public Mot[] getMots(String wordQuery){
        //Récupère dans un Cursor les valeurs correspondant à un mot contenu dans la BDD (ici on sélectionne le mot grâce à sa traduction en anglais)
        Cursor c = bdd.query(MY_TABLE, new String[] {KEY_COL_ID, KEY_COL_EN, KEY_COL_FR, KEY_COL_AR}, wordQuery, null, null, null, null);
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        int retCount = c.getCount();
        if (retCount <= 0) return null;

        Mot[] rets = new Mot[retCount];
        //Sinon on se place sur le premier élément
        int i=0;
        if (c.moveToFirst()){
            while(!c.isAfterLast()){
                Mot mot = new Mot();
                //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
                mot.setId(c.getInt(ID_COLUMN));
                mot.setEn(c.getString(EN_COLUMN));
                mot.setFr(c.getString(FR_COLUMN));
                mot.setAr(c.getString(AR_COLUMN));
                rets[i] = mot;
                i++;

                // do what ever you want here
                c.moveToNext();
            }
        }
        //On ferme le cursor
        c.close();

        return rets;
    }

    //Cette méthode permet de convertir un cursor en un mot
    private Mot cursorToMot(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        Mot mot = new Mot();
        mot.setId(c.getInt(ID_COLUMN));
        mot.setEn(c.getString(EN_COLUMN));
        mot.setFr(c.getString(FR_COLUMN));
        mot.setAr(c.getString(AR_COLUMN));
        //On ferme le cursor
        c.close();

        //On retourne le mot
        return mot;
    }

    public long getRowsCount() {
        Cursor cursor = null;
        close();
        try {
            String countQuery = "SELECT * FROM " + MY_TABLE;
            bdd = dicoOpenHelper.getReadableDatabase();
            cursor = bdd.rawQuery(countQuery, null);
            int cnt = cursor.getCount();
            cursor.close();
            open();
            return cnt;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        if (!bdd.isOpen()) open();
        return 0;
    }
}
