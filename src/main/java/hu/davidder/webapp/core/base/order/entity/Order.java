package hu.davidder.webapp.core.base.order.entity;

import java.time.ZonedDateTime;

import hu.davidder.webapp.core.base.EntityBase;
import hu.davidder.webapp.core.base.order.converter.OrderStatusConverter;
import hu.davidder.webapp.core.base.order.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order extends EntityBase {
    private Long petId;
    private Integer quantity;
    private ZonedDateTime shipDate;

    @Column(nullable = false)
    @Convert(converter = OrderStatusConverter.class)
    private OrderStatus orderStatus;
    
    private Boolean complete;


	public Long getPetId() {
		return petId;
	}

	public void setPetId(Long petId) {
		this.petId = petId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public ZonedDateTime getShipDate() {
		return shipDate;
	}

	public void setShipDate(ZonedDateTime shipDate) {
		this.shipDate = shipDate;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Boolean getComplete() {
		return complete;
	}

	public void setComplete(Boolean complete) {
		this.complete = complete;
	}


}