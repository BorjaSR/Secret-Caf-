package es.bsalazar.secretcafe.data.entities;

import java.util.HashMap;

/**
 * Created by borja.salazar on 05/09/2018.
 */

public class Discount {

    private String imei;
    private String discountCode;

    public Discount() {
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public HashMap<String, Object> getMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("IMEI", this.imei);
        map.put("DiscountCode", this.discountCode);
        return map;
    }
}
