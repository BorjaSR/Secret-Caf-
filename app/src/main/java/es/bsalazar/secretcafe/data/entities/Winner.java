package es.bsalazar.secretcafe.data.entities;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by borja.salazar on 05/09/2018.
 */

public class Winner implements Serializable {

    public static final int DISCOUNT_PENDING = 0;
    public static final int DISCOUNT_SPENT = 1;

    private String id;
    private String imei;
    private String discountCode;
    private long expiredDate;
    private int status;

    public Winner() {
    }

    public Winner(String imei, String discountCode, long timestamp) {
        this.imei = imei;
        this.discountCode = discountCode;
        this.expiredDate = timestamp;
        this.status = DISCOUNT_PENDING;
    }

    public Winner(String id, DocumentSnapshot document){
        Winner winner = document.toObject(this.getClass());
        this.id = id;
        this.imei = winner.getImei();
        this.discountCode = winner.getDiscountCode();
        this.expiredDate = winner.getExpiredDate();
        this.status = winner.getStatus();
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
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

    public long getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(long expiredDate) {
        this.expiredDate = expiredDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Exclude
    public HashMap<String, Object> getMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("imei", this.imei);
        map.put("discountCode", this.discountCode);
        map.put("expiredDate", this.expiredDate);
        map.put("status", this.status);
        return map;
    }
}
