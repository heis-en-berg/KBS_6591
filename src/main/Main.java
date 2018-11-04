package main;

import problog.model.DB;
import problog.parser.Parser;

public class Main {
    public  static  void main(String[] args){
        Parser parser = new Parser();
        DB db = parser.db;
    }
}
