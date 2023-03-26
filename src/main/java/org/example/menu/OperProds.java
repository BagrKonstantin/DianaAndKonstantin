package org.example.menu;

import org.json.simple.JSONObject;

public class OperProds {
    Long prod_type;
    Double prod_quantity;

    public Double getProd_q() {
        return prod_quantity;
    }

    public Long getProd_type() {
        return prod_type;
    }

    public OperProds(JSONObject stuff) {
        this.prod_type = (Long) stuff.get("prod_type");
        this.prod_quantity = Double.valueOf(stuff.get("prod_quantity").toString());
    }
}
