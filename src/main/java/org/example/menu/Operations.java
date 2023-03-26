package org.example.menu;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Operations {
    Double oper_time;

    Long oper_async_point;
    List<OperProds> oper_products;

    public List<OperProds> getOper_products() {
        return oper_products;
    }

    public Operations(JSONObject stuff) {
        oper_products = new ArrayList<>();
        this.oper_time = Double.valueOf(stuff.get("oper_time").toString());
        this.oper_async_point = (Long) stuff.get("oper_async_point");
        for (Object item : (JSONArray) stuff.get("oper_products")) {
            oper_products.add(new OperProds((JSONObject) item));
        }
    }
}

