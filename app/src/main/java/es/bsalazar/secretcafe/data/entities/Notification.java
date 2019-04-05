package es.bsalazar.secretcafe.data.entities;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;

public class Notification {

    public static final int DRINK = 0;
    public static final int MEAL = 1;
    public static final int OFFER = 2;
    public static final int EVENT = 3;

    private String id;
    private int type = -1;
    private String elementId;
    private long expiredDate;
    private String title;
    private String description;

    public Notification() {
    }

    public Notification(String id, DocumentSnapshot document) {
        Notification notification = document.toObject(this.getClass());
        this.id = id;
        this.type = notification.type;
        this.elementId = notification.elementId;
        this.expiredDate = notification.expiredDate;
        this.title = notification.title;
        this.description = notification.description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public long getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(long expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public HashMap<String, Object> getMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", this.type);
        map.put("elementId", this.elementId);
        map.put("expiredDate", this.expiredDate);
        map.put("title", this.title);
        map.put("description", this.description);
        return map;
    }
}
