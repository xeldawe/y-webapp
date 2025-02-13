package hu.davidder.webapp.controllers;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.server.ResponseStatusException;

import hu.davidder.webapp.core.base.order.entity.Order;
import hu.davidder.webapp.core.base.order.filter.OrderFilter;
import hu.davidder.webapp.core.base.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("store")
@Tag(name = "Order", description = "Endpoints for querying Orders")
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Async
	@PostMapping("/order")
	@Operation(summary = "Create a new order", security = { @SecurityRequirement(name = "x-api-key") }, responses = {
			@ApiResponse(responseCode = "201", description = "Order created successfully", content = @Content(schema = @Schema(implementation = Order.class))),
			@ApiResponse(responseCode = "404", description = "Pet not found with id", content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Void.class))),
			@ApiResponse(responseCode = "409", description = "Conflict due to concurrent update", content = @Content(schema = @Schema(implementation = Void.class))) })
	public CompletableFuture<ResponseEntity<Order>> createOrder(@RequestBody Order order) {
			order.setShipDate(order.getShipDate().withZoneSameInstant(ZoneOffset.UTC));
			Order savedOrder = orderService.save(order);
			return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.CREATED).body(savedOrder));
	}


    @GetMapping("/orders")
    @Operation(summary = "Get list of orders", security = { @SecurityRequirement(name = "x-api-key") }, parameters = {
            @Parameter(in = ParameterIn.QUERY, name = "from", description = "From date-time in ISO format", schema = @Schema(type = "string")),
            @Parameter(in = ParameterIn.QUERY, name = "to", description = "To date-time in ISO format", schema = @Schema(type = "string")) }, responses = {
                    @ApiResponse(responseCode = "200", description = "List of orders", content = @Content(schema = @Schema(implementation = Order.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid date-time format", content = @Content(schema = @Schema(implementation = Void.class))) })
    public ResponseEntity<List<Order>> getOrders(@RequestParam(name = "from", required = false) String from,
            @RequestParam(name = "to", required = false) String to, @RequestBody(required = false) OrderFilter filter) {
        ZonedDateTime fromDateTime = (from != null) ? parseZonedDateTime(from)
                : (filter != null ? filter.getFrom() : null);
        ZonedDateTime toDateTime = (to != null) ? parseZonedDateTime(to) : (filter != null ? filter.getTo() : null);

        if (fromDateTime != null && toDateTime != null) {
            long intervalMonths = orderService.parseInterval();
            if (Duration.between(fromDateTime, toDateTime).toDays() > intervalMonths * 30) {
                return ResponseEntity.badRequest().build();
            }
        }

        Iterable<Order> ordersIterable = orderService.getAllOrders();
        List<Order> orders = StreamSupport.stream(ordersIterable.spliterator(), false)
                .filter(order -> isWithinDateRange(order, fromDateTime, toDateTime)).collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

	private boolean isWithinDateRange(Order order, ZonedDateTime from, ZonedDateTime to) {
		if (from != null && order.getShipDate().isBefore(from)) {
			return false;
		}
		if (to != null && order.getShipDate().isAfter(to)) {
			return false;
		}
		return true;
	}

	private ZonedDateTime parseZonedDateTime(String dateTimeString) {
		if (dateTimeString == null || dateTimeString.isEmpty()) {
			return null;
		}
		try {
			return ZonedDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
					.withZoneSameInstant(ZoneId.of("UTC"));
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("Invalid date-time format: " + dateTimeString, e);
		}
	}

	@GetMapping("/orders/{orderId}")
	@Operation(summary = "Get an order by ID", security = { @SecurityRequirement(name = "x-api-key") }, responses = {
			@ApiResponse(responseCode = "200", description = "Order found", content = @Content(schema = @Schema(implementation = Order.class))),
			@ApiResponse(responseCode = "404", description = "Order not found") })
	public ResponseEntity<Order> getOrder(@PathVariable(name = "orderId") Long orderId) {
		Order order = orderService.getOrderByIdCached(orderId).orElse(null);
		if (order == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(order);
	}

	@Async
	@DeleteMapping("/orders/{orderId}")
	@Operation(summary = "Delete an order by ID", security = { @SecurityRequirement(name = "x-api-key") }, responses = {
			@ApiResponse(responseCode = "204", description = "Order deleted"),
			@ApiResponse(responseCode = "404", description = "Order not found") })
	public CompletableFuture<ResponseEntity<Void>> deleteOrder(@PathVariable(name = "orderId") Long orderId) {
		Optional<Order> existingOrderOptional = orderService.getOrderById(orderId);
		if (existingOrderOptional.isPresent()) {
			Order existingOrder = existingOrderOptional.get();
			existingOrder.setDeleted(true);
			existingOrder.setDeleteDate(ZonedDateTime.now());
			orderService.save(existingOrder);
			return CompletableFuture.completedFuture(ResponseEntity.noContent().build());
		} else {
			return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
		}
	}

	@Async
	@PatchMapping(value = "/orders/{orderId}", consumes = "application/json-patch+json")
	@Operation(summary = "Update an order by ID using JSON Patch", security = {
			@SecurityRequirement(name = "x-api-key") }, responses = {
					@ApiResponse(responseCode = "200", description = "Order updated", content = @Content(schema = @Schema(implementation = Order.class))),
					@ApiResponse(responseCode = "404", description = "Order not found") })
	public CompletableFuture<ResponseEntity<Order>> updateOrder(@PathVariable(name = "orderId") Long orderId,
			@RequestBody List<Map<String, Object>> patchOperations) {
		Order order = orderService.getOrderById(orderId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		orderService.applyPatch(order, patchOperations);
		Order updatedOrder = orderService.save(order);
		return CompletableFuture.completedFuture(ResponseEntity.ok(updatedOrder));
	}

}
