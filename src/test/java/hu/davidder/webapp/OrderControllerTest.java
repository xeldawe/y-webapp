package hu.davidder.webapp;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import hu.davidder.webapp.conf.NoRedisTestConfig;
import hu.davidder.webapp.conf.VertxTestConfig;
import hu.davidder.webapp.controllers.OrderController;
import hu.davidder.webapp.core.base.order.entity.Order;
import hu.davidder.webapp.core.base.order.enums.OrderStatus;
import hu.davidder.webapp.core.base.order.service.OrderService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OrderController.class)
@Import({ NoRedisTestConfig.class, VertxTestConfig.class })
public class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OrderService orderService;

	@MockitoBean
	private RedisTemplate<String, Object> redisTemplate;
	
	@Value("${API_KEY:abc123}")
	private String apiKey;

	private static final String API_KEY_HEADER = "x-api-key";

	private ObjectMapper mapper;

	public OrderControllerTest() {
		this.mapper = new ObjectMapper();
		this.mapper.registerModule(new JavaTimeModule());
		this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		this.mapper.registerModule(new Jdk8Module());
	}

	private String asJsonString(final Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testCreateOrder() throws Exception {
		Order order = new Order();
		order.setPetId(1L);
		order.setQuantity(2);
		order.setShipDate(ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC));
		order.setOrderStatus(OrderStatus.PLACED);
		order.setComplete(false);
		String orderJson = asJsonString(order);

		when(orderService.save(any(Order.class))).thenReturn(order);

		MvcResult result = mockMvc.perform(post("/store/order").header(API_KEY_HEADER, apiKey)
				.contentType(MediaType.APPLICATION_JSON).content(orderJson)).andExpect(request().asyncStarted())
				.andReturn();

		result.getRequest().getAsyncContext().setTimeout(5000);

		mockMvc.perform(asyncDispatch(result)).andExpect(status().isCreated())
				.andExpect(jsonPath("$.petId").value(order.getPetId()))
				.andExpect(jsonPath("$.quantity").value(order.getQuantity()))
				.andExpect(jsonPath("$.orderStatus").value(order.getOrderStatus().toString()))
				.andExpect(jsonPath("$.complete").value(order.getComplete()));
	}

	@Test
	public void testGetOrders() throws Exception {
		List<Order> orders = Collections.singletonList(new Order());
		when(orderService.getAllOrders(null, null, null)).thenReturn(ResponseEntity.ok(orders));

		mockMvc.perform(get("/store/orders").header(API_KEY_HEADER, apiKey)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

	@Test
	public void testGetOrderById() throws Exception {
		Order order = new Order();
		order.setId(1L);
		when(orderService.getOrderByIdCached(1L)).thenReturn(Optional.of(order));

		mockMvc.perform(get("/store/orders/1").header(API_KEY_HEADER, apiKey)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(order.getId()));
	}

	@Test
	public void testDeleteOrder() throws Exception {
		Order order = new Order();
		order.setId(1L);
		when(orderService.getOrderById(1L)).thenReturn(Optional.of(order));
		when(orderService.save(any(Order.class))).thenReturn(order);

		MvcResult result = mockMvc.perform(delete("/store/orders/1").header(API_KEY_HEADER, apiKey))
				.andExpect(request().asyncStarted()).andReturn();

		result.getRequest().getAsyncContext().setTimeout(5000);

		mockMvc.perform(asyncDispatch(result)).andExpect(status().isNoContent());
	}

	@Test
	public void testUpdateOrder() throws Exception {
		Order order = new Order();
		order.setId(1L);
		when(orderService.getOrderById(1L)).thenReturn(Optional.of(order));
		when(orderService.save(any(Order.class))).thenReturn(order);

		MvcResult result = mockMvc
				.perform(patch("/store/orders/1").header(API_KEY_HEADER, apiKey)
						.contentType("application/json-patch+json")
						.content("[{ \"op\": \"replace\", \"path\": \"/orderStatus\", \"value\": \"shipped\" }]"))
				.andExpect(request().asyncStarted()).andReturn();

		result.getRequest().getAsyncContext().setTimeout(5000);

		mockMvc.perform(asyncDispatch(result)).andExpect(status().isOk());
	}

}