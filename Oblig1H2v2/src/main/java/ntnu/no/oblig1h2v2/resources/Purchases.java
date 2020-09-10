/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntnu.no.oblig1h2v2.resources;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
/**
 *
 * @author eskil
 */
@Entity
@Table(name = "purchases")
@AllArgsConstructor
@Data
public class Purchases implements Serializable {

    @Id
    @Generated
    String purchaseID;
    User buyer;
    Item item;

    public Purchases() {
    }
}
