package es.bsalazar.secretcafe.data.entities;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;

public class Meal {

    private String Id;
    private String Name;
    private String Description;
    private long dateImageUpdate;
    private double Price;

    public Meal() {
    }

    public Meal(String id, DocumentSnapshot document) {
        Meal meal = document.toObject(this.getClass());
        this.Id = id;
        this.Description = meal.getDescription();
        this.Name = meal.getName();
        this.Price = meal.getPrice();
        this.dateImageUpdate = meal.getDateImageUpdate();
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

    public HashMap<String, Object> getMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("Name", this.Name);
        map.put("Description", this.Description);
        map.put("Price", this.Price);
        map.put("dateImageUpdate", this.dateImageUpdate);
        return map;
    }
}
