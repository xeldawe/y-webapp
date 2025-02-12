package hu.davidder.webapp.core.base.order.repository;

import java.util.List;
import java.util.Optional;

import hu.davidder.webapp.core.base.order.entity.Order;

public interface CustomOrderRepository {
    List<Order> customFindAll();
    Optional<Order> customFindById(Long id);
}
