package org.example.storage;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.json.simple.JSONArray;

import org.json.simple.JSONObject;

import org.json.simple.parser.*;
import org.w3c.dom.ls.LSOutput;


public class Storage {
    String jarPath = Storage.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    JSONArray o =(JSONArray) new JSONParser().parse(new FileReader("classes\\input\\products.txt"));

     HashMap<Integer, Product> storage;
    public Storage() throws IOException, ParseException {
        System.out.println(jarPath);
         for (Object item : o) {
             int id = (int) ((JSONObject) item).get("prod_item_id");
             storage.put(id, new Product((JSONObject)item));
             System.out.println("get");
         }
    }


}
