package com.kira.exequtassesment;

import com.kira.exequtassesment.dto.request.CartItemInfo;
import com.kira.exequtassesment.dto.response.CartResponse;
import com.kira.exequtassesment.enums.OrderStatus;
import com.kira.exequtassesment.enums.PaymentStatus;
import com.kira.exequtassesment.repository.CartRepository;
import com.kira.exequtassesment.repository.OrderRepository;
import com.kira.exequtassesment.repository.PaymentRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(TestConfig.class)
@Sql({"/import.sql"})
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderCycleTests {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @Order(1)
    public void testCartLifecycle() {
        // This test would ideally simulate the entire order lifecycle:

        var createCartResponse = restTemplate.postForEntity("http://localhost:8080/carts", null, CartResponse.class);
        assertThat(createCartResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(createCartResponse.getBody()).isNotNull();
        assertThat(createCartResponse.getBody().getId()).isNotNull();
        Long cartId = createCartResponse.getBody().getId();
        assertThat(cartRepository.findById(cartId)).isNotNull();

        CartItemInfo item1 = new CartItemInfo();
        item1.setProductId(1L);
        item1.setPrice(BigDecimal.valueOf(100));
        item1.setQuantity(2);

        CartItemInfo item2 = new CartItemInfo();
        item2.setProductId(2L);
        item2.setPrice(BigDecimal.valueOf(200));
        item2.setQuantity(1);

        var addItemsResponse = restTemplate.postForEntity("http://localhost:8080/carts/"+cartId+"/items", List.of(item1, item2), String.class);
        assertThat(addItemsResponse.getStatusCode().is2xxSuccessful()).isTrue();
        var cart = cartRepository.findByIdWithItems(cartId);
        assertThat(cart).isNotNull();
        assertThat(cart.getItems().size()).isEqualTo(2);

        Long itemIdToRemove = cart.getItems().get(1).getId();
        restTemplate.delete("http://localhost:8080/carts/"+cartId+"/"+itemIdToRemove);
        cart = cartRepository.findByIdWithItems(cartId);
        assertThat(cart).isNotNull();
        assertThat(cart.getItems().size()).isEqualTo(1);

        var checkoutResponse = restTemplate.postForEntity("http://localhost:8080/orders/"+cartId+"/checkout", null, String.class);
        assertThat(checkoutResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(orderRepository.findById(1L)).isNotNull();

    }

    @Test
    @Order(2)
    public void testOrderPaymentSuccess() {
        Long orderId = 1L; // Assuming order with ID 1 exists from previous test

        var paymentStartResponse = restTemplate.postForEntity("http://localhost:8080/orders/"+orderId+"/payment/start", null, String.class);
        assertThat(paymentStartResponse.getStatusCode().is2xxSuccessful()).isTrue();

        var payment = paymentRepository.findById(1L);
        assertThat(payment.get().getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);

        var order = orderRepository.findById(orderId).get();
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);

        Long paymentId = payment.get().getId();

        Boolean requestBody = true; // Simulate successful payment
        var paymentWebhookResponse = restTemplate.postForEntity("http://localhost:8080/payments/webhook/"+paymentId, requestBody, String.class);
        assertThat(paymentWebhookResponse.getStatusCode().is2xxSuccessful()).isTrue();

        payment = paymentRepository.findById(1L);
        assertThat(payment.get().getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);

        order = orderRepository.findById(orderId).get();
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }
}
