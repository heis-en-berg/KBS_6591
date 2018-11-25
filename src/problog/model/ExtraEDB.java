package problog.model;

import java.util.ArrayList;

public class ExtraEDB {
    public ArrayList<EDB> arrayListOfEDB = new ArrayList<>();
    public EDB lastFactEDB;
    public ExtraEDB(){
        lastFactEDB = new EDB();
    }
}
