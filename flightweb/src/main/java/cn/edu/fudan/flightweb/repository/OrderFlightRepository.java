package cn.edu.fudan.flightweb.repository;

import cn.edu.fudan.flightweb.domain.OrderFlight;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;

/**
 * Created by junfeng on 1/5/16.
 */
public interface OrderFlightRepository extends CrudRepository<OrderFlight, Long> {

    List<OrderFlight> findByOrderUserAndFlightDateBetween(String orderUser, Date startDate, Date endDate);
}
