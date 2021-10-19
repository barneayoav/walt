package com.walt.dao;

import com.walt.model.City;
import com.walt.model.Driver;
import com.walt.model.Delivery;
import com.walt.model.DriverDistance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends CrudRepository<Delivery, Long> {
    List<Delivery> findAllDeliveriesByDriver(Driver driver);

    @Query("select delivery.driver as driver, sum(delivery.distance) as totalDistance " +
            "from Delivery delivery " +
            "group by driver " +
            "order by totalDistance desc")
    List<DriverDistance> findAllDriversDistances();

    @Query("select delivery.driver as driver, sum(delivery.distance) as totalDistance " +
            "from Delivery delivery " +
            "where delivery.customer.city =:city " +
            "group by driver " +
            "order by totalDistance desc")
    List<DriverDistance> findAllDriversDistancesByCity(City city);
}