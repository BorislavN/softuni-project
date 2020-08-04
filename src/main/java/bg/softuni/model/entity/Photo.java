package bg.softuni.model.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "photos")
public class Photo extends BaseEntity {
    private String location;
    private Item item;

    public Photo() {
    }

    public Photo(String location, Item item) {
        this.location = location;
        this.item = item;
    }

    @Column(nullable = false)
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @ManyToOne(optional = false ,fetch = FetchType.LAZY)
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
