package hu.davidder.webapp.core.base.order.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hu.davidder.webapp.core.base.order.entity.Order;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long>, CustomOrderRepository {

}
