package problog.model;

public class DB {

    public EDB edb;
    public IDB idb;
    public EDB edb_temp;

    public DB(){
        edb = new EDB();
        idb = new IDB();
        edb_temp = new EDB();
    }
}
