package org.mathdictionnaryv2;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    Bundle newBundy = new Bundle();
    private static final String FR_NOT_FOUND = "<h1><i>Aucune traduction trouvée</i></h1>";
    private static final String AR_NOT_FOUND = "<h1><i>لم يتم العثور على أي ترجمة</i></h1>";
    private static final String EN_NOT_FOUND = "<h1><i>No traduction found</i></h1>";
    int hi;
    SearchView searchView;
    private TextView mtexView;
    private EditText mEdTex;
    // The database
    private DictionaryBDD dicoBdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dicoBdd = new DictionaryBDD(this);

        // open the database
        dicoBdd.open();
        //dicoBdd.dropTable();

        if (!isTableExists(DictionaryOpenHelper.Constants.MY_TABLE)) { //Table inexistante
            creatDB_loadDico();
        }
        else if (dicoBdd.getRowsCount() < 1949) { //Dictionnaire incomplet
            dicoBdd.dropTable();
            creatDB_loadDico();
        }

        mtexView = (TextView) findViewById(R.id.tvDesc);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        hi = dm.heightPixels;
        //Restaurer les données
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        int dl = sharedPref.getInt(getString(R.string.disp_lang), 2);
        String d = "", entra = "", frtra = "", artra = "", query = "";
        int length = 0;
        switch (dl) {
            case 1:
                toAr();
                //Restauration du contenu
                length = sharedPref.getInt("length", 0);
                for (int i = 0; i < length; i++) {
                    entra = sharedPref.getString("entra"+i, "");
                    frtra = sharedPref.getString("frtra"+i, "");
                    artra = sharedPref.getString("artra"+i, "");
                    d += formatAR(entra, frtra, artra);
                }
                query = sharedPref.getString("query", "Word to find");
                mEdTex = (EditText) findViewById(R.id.editText);
                mEdTex.setText(Html.fromHtml("<h2>"+query+"</h2>", new ImageGetter(),null));
                mtexView = (TextView) findViewById(R.id.tvDesc);
                mtexView.setMovementMethod(new ScrollingMovementMethod());
                mtexView.setText((Html.fromHtml(d, new ImageGetter(),null)));
                break;
            case 2:
                toEn();
                //Restauration du contenu
                length = sharedPref.getInt("length", 0);
                for (int i = 0; i < length; i++) {
                    entra = sharedPref.getString("entra"+i, "");
                    frtra = sharedPref.getString("frtra"+i, "");
                    artra = sharedPref.getString("artra"+i, "");
                    d += formatEN(entra, frtra, artra);
                }
                query = sharedPref.getString("query", "Word to find");
                mEdTex = (EditText) findViewById(R.id.editText);
                mEdTex.setText(Html.fromHtml("<h2>"+query+"</h2>", new ImageGetter(),null));
                mtexView = (TextView) findViewById(R.id.tvDesc);
                mtexView.setMovementMethod(new ScrollingMovementMethod());
                mtexView.setText((Html.fromHtml(d, new ImageGetter(),null)));
                break;
            case 3:
                toFr();
                //Restauration du contenu
                length = sharedPref.getInt("length", 0);
                for (int i = 0; i < length; i++) {
                    entra = sharedPref.getString("entra"+i, "");
                    frtra = sharedPref.getString("frtra"+i, "");
                    artra = sharedPref.getString("artra"+i, "");
                    d += formatFR(entra, frtra, artra);
                }
                query = sharedPref.getString("query", "Word to find");
                mEdTex = (EditText) findViewById(R.id.editText);
                mEdTex.setText(Html.fromHtml("<h2>"+query+"</h2>", new ImageGetter(),null));
                mtexView = (TextView) findViewById(R.id.tvDesc);
                mtexView.setMovementMethod(new ScrollingMovementMethod());
                mtexView.setText((Html.fromHtml(d, new ImageGetter(),null)));
                break;
            default:
                toEn();
                //Restauration du contenu
                length = sharedPref.getInt("length", 0);
                for (int i = 0; i < length; i++) {
                    entra = sharedPref.getString("entra"+i, "");
                    frtra = sharedPref.getString("frtra"+i, "");
                    artra = sharedPref.getString("artra"+i, "");
                    d += formatEN(entra, frtra, artra);
                }
                query = sharedPref.getString("query", "Word to find");
                mEdTex = (EditText) findViewById(R.id.editText);
                mEdTex.setText(Html.fromHtml("<h2>"+query+"</h2>", new ImageGetter(),null));
                mtexView = (TextView) findViewById(R.id.tvDesc);
                mtexView.setMovementMethod(new ScrollingMovementMethod());
                mtexView.setText((Html.fromHtml(d, new ImageGetter(),null)));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //final MenuItem searchItem = menu.findItem(R.id.action_search);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchView != null) searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        int dl = sharedPref.getInt(getString(R.string.disp_lang), 2);
        String d = "", entra = "", frtra = "", artra = "", query = "";
        int length = 0;
        switch(dl) {
            case 1 :
                toAr();
                //Restauration du contenu
                length = sharedPref.getInt("length", 0);
                for (int i = 0; i < length; i++) {
                    entra = sharedPref.getString("entra"+i, "");
                    frtra = sharedPref.getString("frtra"+i, "");
                    artra = sharedPref.getString("artra"+i, "");
                    d += formatAR(entra, frtra, artra);
                }
                query = sharedPref.getString("query", "Word to find");
                mEdTex = (EditText) findViewById(R.id.editText);
                mEdTex.setText(Html.fromHtml("<h2>"+query+"</h2>", new ImageGetter(),null));
                mtexView = (TextView) findViewById(R.id.tvDesc);
                mtexView.setMovementMethod(new ScrollingMovementMethod());
                mtexView.setText((Html.fromHtml(d, new ImageGetter(),null)));
                break;
            case 2 :
                toEn();
                //Restauration du contenu
                length = sharedPref.getInt("length", 0);
                for (int i = 0; i < length; i++) {
                    entra = sharedPref.getString("entra"+i, "");
                    frtra = sharedPref.getString("frtra"+i, "");
                    artra = sharedPref.getString("artra"+i, "");
                    d += formatEN(entra, frtra, artra);
                }
                query = sharedPref.getString("query", "Word to find");
                mEdTex = (EditText) findViewById(R.id.editText);
                mEdTex.setText(Html.fromHtml("<h2>"+query+"</h2>", new ImageGetter(),null));
                mtexView = (TextView) findViewById(R.id.tvDesc);
                mtexView.setMovementMethod(new ScrollingMovementMethod());
                mtexView.setText((Html.fromHtml(d, new ImageGetter(),null)));
                break;
            case 3 :
                toFr();
                //Restauration du contenu
                length = sharedPref.getInt("length", 0);
                for (int i = 0; i < length; i++) {
                    entra = sharedPref.getString("entra"+i, "");
                    frtra = sharedPref.getString("frtra"+i, "");
                    artra = sharedPref.getString("artra"+i, "");
                    d += formatFR(entra, frtra, artra);
                }
                query = sharedPref.getString("query", "Word to find");
                mEdTex = (EditText) findViewById(R.id.editText);
                mEdTex.setText(Html.fromHtml("<h2>"+query+"</h2>", new ImageGetter(),null));
                mtexView = (TextView) findViewById(R.id.tvDesc);
                mtexView.setMovementMethod(new ScrollingMovementMethod());
                mtexView.setText((Html.fromHtml(d, new ImageGetter(),null)));
                break;
            default :
                toEn();
                //Restauration du contenu
                length = sharedPref.getInt("length", 0);
                for (int i = 0; i < length; i++) {
                    entra = sharedPref.getString("entra"+i, "");
                    frtra = sharedPref.getString("frtra"+i, "");
                    artra = sharedPref.getString("artra"+i, "");
                    d += formatEN(entra, frtra, artra);
                }
                query = sharedPref.getString("query", "Word to find");
                mEdTex = (EditText) findViewById(R.id.editText);
                mEdTex.setText(Html.fromHtml("<h2>"+query+"</h2>", new ImageGetter(),null));
                mtexView = (TextView) findViewById(R.id.tvDesc);
                mtexView.setMovementMethod(new ScrollingMovementMethod());
                mtexView.setText((Html.fromHtml(d, new ImageGetter(),null)));
                break;
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                newSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    void creatDB_loadDico() {
        dicoBdd.getDicoOpenHelper().onCreate(dicoBdd.getBDD());
        try {
            loadDictionarytoDB();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String formatEN(String entra, String frtra, String artra) {
        String imgen = String.format("<img src=\"%s\" />", R.drawable.ic_en);
        String htmlen = String.format("<h1> %s English : </h1> <h3>%s</h3><br>",imgen,entra);
        String imgfr = String.format("<img src=\"%s\" />", R.drawable.ic_fr);
        String htmlfr = String.format("<h1> %s Français : </h1> "+
                "<h3>%s</h3><br>",imgfr,frtra);
        String imgar = String.format("<img src=\"%s\" />", R.drawable.ic_ar);
        String htmlar = String.format("<p align=\"right\"><h1>%s العربية : </h1> "+
                "<h3>%s</h3></p><p>===============================================</p>",imgar,artra);
        return htmlen+htmlfr+htmlar;
    }

    public String formatFR(String entra, String frtra, String artra) {
        String imgfr = String.format("<img src=\"%s\" />", R.drawable.ic_fr);
        String htmlfr = String.format("<h1> %s Français : </h1> <h3>%s</h3><br>",imgfr,frtra);
        String imgen = String.format("<img src=\"%s\" />", R.drawable.ic_en);
        String htmlen = String.format("<h1>%s English : </h1> "+
                "<h3>%s</h3><br>", imgen, entra);
        String imgar = String.format("<img src=\"%s\" />", R.drawable.ic_ar);
        String htmlar = String.format("<p align=\"right\"><h1>%s العربية : </h1> "+
                "<h3>%s</h3></p><p>===============================================</p>", imgar, artra);
        return htmlfr+htmlen+htmlar;
    }

    public String formatAR(String entra, String frtra, String artra) {
        String imgar = String.format("<img src=\"%s\" />", R.drawable.ic_ar);
        String htmlar = String.format("<p align=\"right\"><h1> %s العربية : </h1> <h3>%s</h3></p><br>",imgar,artra);
        String imgfr = String.format("<img src=\"%s\" />", R.drawable.ic_fr);
        String htmlfr = String.format("<h1>%s Français : </h1> "+
                "<h3>%s</h3><br>",imgfr,frtra);
        String imgen = String.format("<img src=\"%s\" />", R.drawable.ic_en);
        String htmlen = String.format("<h1>%s English : </h1> "+
                "<h3>%s</h3><p>===============================================</p>",imgen,entra);
        return htmlar+htmlfr+htmlen;
    }

    public int newSearch(String q) {
        //Vider les SharedPreferences sans perdre la langue
        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        int dl = sharedPref.getInt(getString(R.string.disp_lang), 2);
        emptySharedPref();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.disp_lang),dl);
        editor.commit();

        if (q.equals("")) return 0;

        String d = "";
        Mot[] mots = null;
        switch(dl) {
            case 1:
                mots = dicoBdd.getMotsWithAr(q);
                if (mots != null && mots.length > 0) {
                    //Sauvegarde du contenu
                    editor = sharedPref.edit();
                    editor.putInt("length", mots.length);
                    for (int i = 0; i < mots.length; i++) {
                        editor.putString("entra" + i, mots[i].getEn());
                        editor.putString("frtra" + i, mots[i].getFr());
                        editor.putString("artra" + i, mots[i].getAr());
                        d += formatAR(mots[i].getEn(), mots[i].getFr(), mots[i].getAr());
                    }
                    editor.putString("query", q);
                    editor.commit();
                } else {
                    //Sauvegarde du contenu
                    editor = sharedPref.edit();
                    editor.putInt("length", 1);
                    editor.putString("frtra0", FR_NOT_FOUND);
                    editor.putString("artra0", AR_NOT_FOUND);
                    editor.putString("entra0", EN_NOT_FOUND);
                    editor.putString("query", q);
                    editor.commit();
                    d += formatAR(EN_NOT_FOUND, FR_NOT_FOUND, AR_NOT_FOUND);
                }
                break;
            case 2:
                mots = dicoBdd.getMotsWithEn(q);
                if (mots != null && mots.length > 0) {
                    //Sauvegarde du contenu
                    editor = sharedPref.edit();
                    editor.putInt("length", mots.length);
                    for (int i = 0; i < mots.length; i++) {
                        editor.putString("entra" + i, mots[i].getEn());
                        editor.putString("frtra" + i, mots[i].getFr());
                        editor.putString("artra" + i, mots[i].getAr());
                        d += formatEN(mots[i].getEn(), mots[i].getFr(), mots[i].getAr());
                    }
                    editor.putString("query", q);
                    editor.commit();
                } else {
                    //Sauvegarde du contenu
                    editor = sharedPref.edit();
                    editor.putInt("length", 1);
                    editor.putString("frtra0", FR_NOT_FOUND);
                    editor.putString("artra0", AR_NOT_FOUND);
                    editor.putString("entra0", EN_NOT_FOUND);
                    editor.putString("query", q);
                    editor.commit();
                    d += formatEN(EN_NOT_FOUND, FR_NOT_FOUND, AR_NOT_FOUND);
                }
                break;
            case 3:
                mots = dicoBdd.getMotsWithFr(q);
                if (mots != null && mots.length > 0) {
                    //Sauvegarde du contenu
                    editor = sharedPref.edit();
                    editor.putInt("length", mots.length);
                    for (int i = 0; i < mots.length; i++) {
                        editor.putString("entra" + i, mots[i].getEn());
                        editor.putString("frtra" + i, mots[i].getFr());
                        editor.putString("artra" + i, mots[i].getAr());
                        d += formatFR(mots[i].getEn(), mots[i].getFr(), mots[i].getAr());
                    }
                    editor.putString("query", q);
                    editor.commit();
                } else {
                    //Sauvegarde du contenu
                    editor = sharedPref.edit();
                    editor.putInt("length", 1);
                    editor.putString("frtra0", FR_NOT_FOUND);
                    editor.putString("artra0", AR_NOT_FOUND);
                    editor.putString("entra0", EN_NOT_FOUND);
                    editor.putString("query", q);
                    editor.commit();
                    d += formatFR(EN_NOT_FOUND, FR_NOT_FOUND, AR_NOT_FOUND);
                }
                break;
            default:
                mots = dicoBdd.getMotsWithEn(q);
                if (mots != null && mots.length > 0) {
                    //Sauvegarde du contenu
                    editor = sharedPref.edit();
                    editor.putInt("length", mots.length);
                    for (int i = 0; i < mots.length; i++) {
                        editor.putString("entra" + i, mots[i].getEn());
                        editor.putString("frtra" + i, mots[i].getFr());
                        editor.putString("artra" + i, mots[i].getAr());
                        d += formatEN(mots[i].getEn(), mots[i].getFr(), mots[i].getAr());
                    }
                    editor.putString("query", q);
                    editor.commit();
                } else {
                    //Sauvegarde du contenu
                    editor = sharedPref.edit();
                    editor.putInt("length", 1);
                    editor.putString("frtra0", FR_NOT_FOUND);
                    editor.putString("artra0", AR_NOT_FOUND);
                    editor.putString("entra0", EN_NOT_FOUND);
                    editor.putString("query", q);
                    editor.commit();
                    d += formatEN(EN_NOT_FOUND, FR_NOT_FOUND, AR_NOT_FOUND);
                }
                break;
        }
        mEdTex = (EditText) findViewById(R.id.editText);
        mEdTex.setText(Html.fromHtml("<h2>"+q+"</h2>", new ImageGetter(),null));
        mtexView = (TextView) findViewById(R.id.tvDesc);
        mtexView.setMovementMethod(new ScrollingMovementMethod());
        mtexView.setText((Html.fromHtml(d, new ImageGetter(),null)));

        return 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.ar:
                setAr();
                return true;
            case R.id.en:
                setEn();
                return true;
            case R.id.fr:
                setFr();
                return true;
            case R.id.menu_exit:
                //MainActivity.this.finish();
                finish();
                moveTaskToBack(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setAr(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.disp_lang),1);
        editor.commit();
        toAr();
    }

    private void setEn(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.disp_lang),2);
        editor.commit();
        toEn();
    }

    private void setFr(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.disp_lang),3);
        editor.commit();
        toFr();
    }

    private void toAr(){
        setContentView(R.layout.activity_main);
        if(searchView!=null) searchView.setQueryHint("البحث في القاموس");
    }

    private void toEn(){
        setContentView(R.layout.activity_main);
        if(searchView!=null) searchView.setQueryHint("Search Math Dictionnary");
    }

    private void toFr(){
        setContentView(R.layout.activity_main);
        if(searchView!=null) searchView.setQueryHint("Rechercher Math Dictionnary");
    }

    private class ImageGetter implements Html.ImageGetter {

        public Drawable getDrawable(String source) {
            Drawable d = null;
            try {
                d = getResources().getDrawable(Integer.parseInt(source));
                d.setBounds(0, 0, hi/10, hi/10);
                //d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            } catch (Resources.NotFoundException e) {
                Log.e("log_tag", "Image not found. Check the ID.", e);
            } catch (NumberFormatException e) {
                Log.e("log_tag", "Source string not a valid resource ID.", e);
            }
            return d;
        }
    }

    @Override
    protected void onDestroy() {
        emptySharedPref();

        // close the database
        dicoBdd.close();

        super.onDestroy();
        Toast.makeText(getApplicationContext(), "onDestroy called", Toast.LENGTH_LONG).show();
    }

    void emptySharedPref() {
        //Vider le contenu sauvegardé
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        sharedPref.edit().clear().commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            onSaveInstanceState(newBundy);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            onSaveInstanceState(newBundy);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("newBundy", newBundy);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.getBundle("newBundy");
    }

    /*********************************************************************************/
    /** Managing LifeCycle and database open/close operations *********************************/
    /*********************************************************************************/
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void loadDictionarytoDB() throws IOException {
        Toast.makeText(this, "Loading words...", Toast.LENGTH_LONG).show();
        final Resources resources = getResources();
        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myparser = xmlFactoryObject.newPullParser();
            InputStream ins = resources.openRawResource(R.raw.dict);
            myparser.setInput(ins,null);
            int event = myparser.getEventType();
            Mot mot = new Mot();
            while (event != XmlPullParser.END_DOCUMENT){
                String name = myparser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.END_TAG:
                        if(name.equals("dict")){
                            //On insère le livre que l'on vient de créer
                            mot.setEn(myparser.getAttributeValue(null,"en").trim());
                            mot.setFr(myparser.getAttributeValue(null,"fr").trim());
                            mot.setAr(myparser.getAttributeValue(null,"ar").trim());
                            long id = dicoBdd.insertMot(mot);
                            if (id < 0) {
                                Toast.makeText(this, "unable to add word: ", Toast.LENGTH_LONG).show();
                            }
                        }
                        break;
                }
                event = myparser.next();
            }
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    public boolean isTableExists(String tableName) {
        boolean isExist = false;
        Cursor cursor = dicoBdd.getBDD().rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                isExist = true;
            }
            cursor.close();
        }
        return isExist;
    }

}
