package org.mathdictionnaryv2;

/**
 * Created by Larbi on 02/07/2018.
 */

public class Mot {
    private int id;
    private String en;
    private String fr;
    private String ar;

    public Mot(){}

    public Mot(String e, String f, String a){
        en = e;
        fr = f;
        ar = a;
    }

    public int getId() {
        return id;
    }

    public String getEn() {
        return en;
    }

    public String getFr() {
        return fr;
    }

    public String getAr() {
        return ar;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public void setFr(String fr) {
        this.fr = fr;
    }

    public void setAr(String ar) {
        this.ar = ar;
    }

    @Override
    public String toString() {
        return "Mot{" +
                "id=" + id +
                "en=" + en +
                "fr=" + fr +
                "ar=" + ar +
                '}';
    }
}
