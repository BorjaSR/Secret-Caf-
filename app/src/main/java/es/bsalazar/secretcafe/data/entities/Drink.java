package es.bsalazar.secretcafe.data.entities;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by borja.salazar on 16/03/2018.
 */

public class Drink implements Serializable {

    private String Id;
    private String Name;
    private String Description;
    private long dateImageUpdate;
    private double Price;

    public Drink() {
    }

    public Drink(String id, DocumentSnapshot document) {
        Drink drink = document.toObject(this.getClass());
        this.Id = id;
        this.Description = drink.getDescription();
        this.Name = drink.getName();
        this.Price = drink.getPrice();
        this.dateImageUpdate = drink.getDateImageUpdate();
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public String getId() {
        return Id;
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

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public long getDateImageUpdate() {
        return dateImageUpdate;
    }

    public void setDateImageUpdate(long dateImageUpdate) {
        this.dateImageUpdate = dateImageUpdate;
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
