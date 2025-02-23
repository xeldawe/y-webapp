package hu.davidder.webapp.core.base.order.service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import hu.davidder.webapp.core.base.order.entity.Order;
import hu.davidder.webapp.core.base.order.filter.OrderFilter;
import hu.davidder.webapp.core.base.order.repository.OrderRepository;
import hu.davidder.webapp.core.base.pet.entity.Pet;
import hu.davidder.webapp.core.base.pet.repository.PetRepository;
import hu.davidder.webapp.core.util.DateUtil;
import hu.davidder.webapp.core.util.RedisUtil;

@Service
public class OrderService {

	@Lazy
	@Autowired
	private OrderRepository orderRepository;

	@Lazy
	@Autowired
	private PetRepository petRepository;

	@Lazy
	@Autowired
	private RedisUtil redisUtil;

	@Lazy
	@Autowired
	private DateUtil dateUtil;

	public CompletableFuture<Order> createOrder(Order order) {
		return CompletableFuture.completedFuture(orderRepository.save(order));
	}

	@Cacheable(value = "orders", unless = "#result == null or #result.size()==0")
	public Iterable<Order> getAllOrders() {
		return orderRepository.customFindAll();
	}

	public Optional<Order> getOrderById(Long orderId) {
		return orderRepository.customFindById(orderId);
	}

	@Cacheable(value = "order", key = "{#id}", unless = "#result == null")
	public Optional<Order> getOrderByIdCached(Long orderId) {
		return orderRepository.customFindById(orderId);
	}

	public Order save(Order order) {
		Optional<Pet> pet = petRepository.findById(order.getPetId());
		if (!pet.isPresent()) {
			throw new ResourceNotFoundException("Pet not found with id " + order.getPetId());
		}
		Order res = orderRepository.save(order);
		redisUtil.resetCache();
		return res;
	}

	public CompletableFuture<Void> deleteOrder(Long orderId) {
		Optional<Order> existingOrderOptional = orderRepository.customFindById(orderId);
		if (existingOrderOptional.isPresent()) {
			Order existingOrder = existingOrderOptional.get();
			existingOrder.setDeleted(true);
			existingOrder.setDeleteDate(ZonedDateTime.now(ZoneId.of("UTC")));
			orderRepository.save(existingOrder);
			redisUtil.resetCache();
			return CompletableFuture.completedFuture(null);
		} else {
			return CompletableFuture.completedFuture(null);
		}
	}

	public CompletableFuture<Order> updateOrder(Order order) {
		Optional<Order> existingOrderOptional = orderRepository.customFindById(order.getId());
		if (existingOrderOptional.isPresent()) {
			Order existingOrder = existingOrderOptional.get();
			updateOrderFields(existingOrder, order);
			existingOrder.setModifyDate(ZonedDateTime.now(ZoneId.of("UTC")));
			CompletableFuture<Order> res = CompletableFuture.completedFuture(orderRepository.save(existingOrder));
			redisUtil.resetCache();
			return res;
		} else {
			return CompletableFuture.completedFuture(null);
		}
	}

	private void updateOrderFields(Order existingOrder, Order newOrder) {
		existingOrder.setPetId(newOrder.getPetId());
		existingOrder.setQuantity(newOrder.getQuantity());
		existingOrder.setShipDate(newOrder.getShipDate());
		existingOrder.setOrderStatus(newOrder.getOrderStatus());
		existingOrder.setComplete(newOrder.getComplete());
		existingOrder.setStatus(newOrder.isStatus());
		existingOrder.setModifyDate(ZonedDateTime.now(ZoneId.of("UTC")));
	}

	public void applyPatch(Order order, List<Map<String, Object>> patchOperations) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule

		String orderJson = null;
		try {
			orderJson = objectMapper.writeValueAsString(order);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (orderJson == null || orderJson.isEmpty()) {
			throw new IllegalArgumentException("JSON string can not be null or empty");
		}
		DocumentContext documentContext = JsonPath.parse(orderJson);

		for (Map<String, Object> patchOperation : patchOperations) {
			String op = (String) patchOperation.get("op");
			String path = (String) patchOperation.get("path");
			Object value = patchOperation.get("value");
			if (path.startsWith("/")) {
				path = path.substring(1);
			}
			switch (op) {
			case "add":
				documentContext.put(path, value.toString(), value);
				break;
			case "remove":
				try {
					documentContext.delete(path);
				} catch (PathNotFoundException e) {
					// Handle path not found exception
				}
				break;
			case "replace":
				documentContext.set(path, value);
				break;
			default:
				throw new UnsupportedOperationException("Unsupported patch operation: " + op);
			}
		}

		// Convert patched JSON back to Order object
		Order patchedOrder = null;
		try {
			patchedOrder = objectMapper.readValue(documentContext.jsonString(), Order.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		copyProperties(patchedOrder, order);
	}

	private void copyProperties(Order source, Order target) {
		target.setPetId(source.getPetId());
		target.setQuantity(source.getQuantity());
		target.setShipDate(source.getShipDate());
		target.setOrderStatus(source.getOrderStatus());
		target.setModifyDate(ZonedDateTime.now(ZoneId.of("UTC")));
		target.setComplete(source.getComplete());
	}

	public ResponseEntity<List<Order>> getAllOrders(String from, String to, OrderFilter filter) {
		try {
			ZonedDateTime fromDateTime = (from != null) ? dateUtil.parseZonedDateTime(from)
					: (filter != null ? filter.getFrom() : null);
			ZonedDateTime toDateTime = (to != null) ? dateUtil.parseZonedDateTime(to)
					: (filter != null ? filter.getTo() : null);

			if (fromDateTime != null && toDateTime != null) {
				long interval = dateUtil.parseInterval();
				if (Duration.between(fromDateTime, toDateTime).toDays() > interval) {
					return ResponseEntity.badRequest().build();
				}
			}
			Iterable<Order> ordersIterable = getAllOrders();
			List<Order> orders = StreamSupport.stream(ordersIterable.spliterator(), false)
					.filter(order -> dateUtil.isWithinDateRange(order, fromDateTime, toDateTime))
					.collect(Collectors.toList());
			return ResponseEntity.ok(orders);
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

}
