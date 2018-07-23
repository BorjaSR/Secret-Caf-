package es.bsalazar.secretcafe.data.entities;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.HashMap;

public class Event implements Serializable{


    private String Id;
    private String Name;
    private String Description;
    private long dateImageUpdate;
    private double Price;
    private String date;
    private String startTime;
    private String endTime;

    public Event() {
    }

    public Event(String id, DocumentSnapshot document) {
        Event event = document.toObject(this.getClass());
        this.Id = id;
        this.Description = event.getDescription();
        this.Name = event.getName();
        this.Price = event.getPrice();
        this.dateImageUpdate = event.getDateImageUpdate();
        this.date = event.getDate();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public long getDateImageUpdate() {
        return dateImageUpdate;
    }

    public void setDateImageUpdate(long dateImageUpdate) {
        this.dateImageUpdate = dateImageUpdate;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public HashMap<String, Object> getMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("Name", this.Name);
        map.put("Description", this.Description);
        map.put("Price", this.Price);
        map.put("dateImageUpdate", this.dateImageUpdate);
        map.put("date", this.date);
        map.put("startTime", this.startTime);
        map.put("endTime", this.endTime);
        return map;
    }
}
