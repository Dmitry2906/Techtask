package com.example.techtask.service.impl;

import com.example.techtask.model.Order;
import com.example.techtask.model.User;
import com.example.techtask.model.enumiration.UserStatus;
import com.example.techtask.repository.OrderRepository;
import com.example.techtask.repository.UserRepository;
import com.example.techtask.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Order findOrder() {
        // Возвращаем самый новый заказ, в котором больше одного предмета
        return orderRepository.findAll().stream()
                .filter(order -> order.getQuantity() > 1)
                .max((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .orElse(null);
    }

    @Override
    public List<Order> findOrders() {
        // Возвращаем заказы от активных пользователей, отсортированные по дате создания
        return orderRepository.findAll().stream()
                .filter(order -> {
                    User user = userRepository.findById(order.getUserId()).orElse(null);
                    return user != null && user.getUserStatus() == UserStatus.ACTIVE;
                })
                .sorted((o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
