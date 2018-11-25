package problog.model;

import java.util.ArrayList;

public class DB {

    public EDB edb;
    public IDB idb;
    public EDB edb_temp;
    public EDB last_derived_facts;
    public ArrayList<EDB> ruleFacts;

    public DB(){
        edb = new EDB();
        idb = new IDB();
        edb_temp = new EDB();
        last_derived_facts = new EDB();
        ruleFacts = new ArrayList<>();
    }
    
}
