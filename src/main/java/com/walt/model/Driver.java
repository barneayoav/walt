package com.walt.model;

import com.walt.dao.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

@Entity
public class Driver extends NamedEntity {

    @ManyToOne
    City city;

    public Driver(){}

    public Driver(String name, City city){
        super(name);
        this.city = city;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
