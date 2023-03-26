package org.example.customer;

import org.json.simple.JSONObject;

public class CusOrdDish {
    Long ord_dish_id;
    Long menu_dish;

    public CusOrdDish(JSONObject stuff) {
        this.ord_dish_id = (Long) stuff.get("ord_dish_id");
        this.menu_dish = (Long) stuff.get("menu_dish");
    }
}
