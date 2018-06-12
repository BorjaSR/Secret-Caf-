package es.bsalazar.secretcafe.data.entities;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Category {

    private String Id;
    private String Name;
    private int bannerColor;
    private long dateImageUpdate;
    private String toCategory;

    public Category(String id, DocumentSnapshot document) {
        Category category = document.toObject(this.getClass());
        this.Id = id;
        this.Name = category.getName();
        this.bannerColor = category.getBannerColor();
        this.dateImageUpdate = category.getDateImageUpdate();
        this.toCategory = category.getToCategory();
    }

    public Category() {
    }

    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getBannerColor() {
        return bannerColor;
    }

    public void setBannerColor(int bannerColor) {
        this.bannerColor = bannerColor;
    }

    public long getDateImageUpdate() {
        return dateImageUpdate;
    }

    public void setDateImageUpdate(long dateImageUpdate) {
        this.dateImageUpdate = dateImageUpdate;
    }

    public String getToCategory() {
        return toCategory;
    }

    public void setToCategory(String toCategory) {
        this.toCategory = toCategory;
    }

    public HashMap<String, Object> getMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("Name", this.Name);
        map.put("bannerColor", this.bannerColor);
        map.put("dateImageUpdate", this.dateImageUpdate);
        map.put("toCategory", this.toCategory);
        return map;
    }
}
