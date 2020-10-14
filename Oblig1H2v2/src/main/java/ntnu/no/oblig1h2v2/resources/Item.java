/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntnu.no.oblig1h2v2.resources;

import ntnu.no.oblig1h2v2.resources.MediaObject;
import ntnu.no.oblig1h2v2.resources.MediaObjectAdapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static ntnu.no.oblig1h2v2.resources.Item.FIND_ALL_ITEMS;

/**
 *
 * @author eskil
 */
@Entity
@Data
@AllArgsConstructor
@NamedQuery(name = FIND_ALL_ITEMS, query = "select i from Item i where i.sold = false")
public class Item extends AbstractDomain{
    public static final String FIND_ALL_ITEMS = "Item.findAllUsers";
    
    @Id @GeneratedValue
    private Long id;
    
    String title;
    
    @Column
    boolean sold;
    
    String price;
    
    String description;
    
    @ManyToOne(optional = false,cascade = CascadeType.PERSIST)
    User seller;
    
    @ManyToOne
    User customer;
    
    @JsonbTypeAdapter(MediaObjectAdapter.class)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<MediaObject> photos;
  
    
    protected Item(){}
    
    public Item(String title, String description,User seller,String price){
        this.title = title;
        this.description = description;
        this.seller = seller;
        this.price = price;
        this.sold = false;
    }
    
    public long getId(){
        return id;
    }
    
   public void setSold(boolean sold){
       this.sold = sold;
   }
    
    public void addPhoto(MediaObject photo){
        if(this.photos == null){
            this.photos = new ArrayList<>();
        }
        this.photos.add(photo);
    }
    
    
}
