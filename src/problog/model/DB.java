package problog.model;

public class DB {

    public EDB edb;
    public IDB idb;
    public DB(){
        edb = new EDB();
        idb = new IDB();
    }
}
