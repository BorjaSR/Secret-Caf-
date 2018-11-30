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
    private String instanceID;
    private String discountCode;
    private long expiredDate;
    private int status;

    public Winner() {
    }

    public Winner(String instanceID, String discountCode, long timestamp) {
        this.instanceID = instanceID;
        this.discountCode = discountCode;
        this.expiredDate = timestamp;
        this.status = DISCOUNT_PENDING;
    }

    public Winner(String id, DocumentSnapshot document){
        Winner winner = document.toObject(this.getClass());
        this.id = id;
        this.instanceID = winner.getInstanceID();
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

    public String getInstanceID() {
        return instanceID;
    }

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
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
        map.put("instanceID", this.instanceID);
        map.put("discountCode", this.discountCode);
        map.put("expiredDate", this.expiredDate);
        map.put("status", this.status);
        return map;
    }
}
