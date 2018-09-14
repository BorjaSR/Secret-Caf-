package es.bsalazar.secretcafe.data.entities;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Offer implements Serializable{

    private String Id;
    private String Name;
    private String Description;
    private long dateImageUpdate;
    private double Price;
    private List<String> offers;

    public Offer() {
    }

    public Offer(String id, DocumentSnapshot document) {
        Offer offer = document.toObject(this.getClass());
        this.Id = id;
        this.Description = offer.getDescription();
        this.Name = offer.getName();
        this.Price = offer.getPrice();
        this.dateImageUpdate = offer.getDateImageUpdate();
        this.offers = offer.getOffers();
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

    public List<String> getOffers() {
        return offers;
    }

    public void setOffers(List<String> offers) {
        this.offers = offers;
    }

    public HashMap<String, Object> getMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("Name", this.Name);
        map.put("Description", this.Description);
        map.put("Price", this.Price);
        map.put("dateImageUpdate", this.dateImageUpdate);
        map.put("offers", this.offers);
        return map;
    }
}
