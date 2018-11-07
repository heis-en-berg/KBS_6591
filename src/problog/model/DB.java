package problog.model;

public class DB {

    public EDB edb;
    public EDB edb_temp;
    public IDB idb;
    public DB(){
        edb = new EDB();
        idb = new IDB();
        edb_temp = new EDB();
    }
}
