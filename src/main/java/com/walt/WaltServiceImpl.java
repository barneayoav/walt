package com.walt;

import com.walt.dao.DeliveryRepository;
import com.walt.dao.DriverRepository;
import com.walt.model.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class WaltServiceImpl implements WaltService {

    protected final static String ERR_MISMATCH_CITIES = "Order failed: Cannot perform delivery to a different city.";
    protected final static String ERR_NO_AVAILABLE_DRIVERS = "Order failed: No available drivers to fulfill delivery.";

    @Resource
    private DeliveryRepository deliveryRepository;
    @Resource
    private DriverRepository driverRepository;

    @Override
    public Delivery createOrderAndAssignDriver(Customer customer, Restaurant restaurant, Date deliveryTime) {
        validateOrderLocation(customer, restaurant);

        List<Driver> potentialDrivers = findAllPotentialDrivers(customer.getCity(), deliveryTime);
        Driver driver = getLeastBusyDriver(potentialDrivers);
        Delivery delivery = new Delivery(driver, restaurant, customer, deliveryTime);

        return deliveryRepository.save(delivery);
    }

    /**
     * The method checks whether the customer's city matches the restaurant's
     * if not - throws a Runtime exception
     */
    private void validateOrderLocation(Customer customer, Restaurant restaurant) {
        if (!customer.getCity().getId().equals(restaurant.getCity().getId()))
            throw new RuntimeException(ERR_MISMATCH_CITIES);
    }

    /**
     * The method filters out all drivers who can't deliver in the delivery city,
     * and all drivers with deliveries set up already at the same time
     */
    private List<Driver> findAllPotentialDrivers(City deliveryCity, Date deliveryTime) {
        List<Driver> potentialDrivers = new ArrayList<>();
        List<Delivery> driverDelivers;
        boolean driverAvailability;

        for (Driver driver : driverRepository.findAllDriversByCity(deliveryCity)) {
            driverDelivers = deliveryRepository.findAllDeliveriesByDriver(driver);
            driverAvailability = true;

            for (Delivery delivery : driverDelivers) {
                if (deliveryTime.compareTo(delivery.getDeliveryTime()) == 0)
                    driverAvailability = !driverAvailability;
            }

            if (driverAvailability)
                potentialDrivers.add(driver);
        }

        return potentialDrivers;
    }

    /**
     * The method returns the driver with the least accumulated distance from the available drivers list
     */
    private Driver getLeastBusyDriver(List<Driver> availableDrivers) {
        if (availableDrivers.isEmpty())
            throw new RuntimeException(ERR_NO_AVAILABLE_DRIVERS);

        availableDrivers.sort(Comparator.comparingDouble(this::getDriverTotalDistance));

        return availableDrivers.get(0);
    }

    /**
     * The method calculates and returns the total distance a driver has accumulated through his deliveries,
     * used as Comparator to sort drivers by distance
     */
    public double getDriverTotalDistance(Driver driver) {
        double sum = 0;
        List<Delivery> driverDeliveries = deliveryRepository.findAllDeliveriesByDriver(driver);

        for (Delivery delivery : driverDeliveries) {
            sum += delivery.getDistance();
        }

        System.out.println(sum);
        return sum;
    }

    @Override
    public List<DriverDistance> getDriverRankReport() {
        return deliveryRepository.findAllDriversDistances();
    }

    @Override
    public List<DriverDistance> getDriverRankReportByCity(City city) {
        return deliveryRepository.findAllDriversDistancesByCity(city);
    }
}
