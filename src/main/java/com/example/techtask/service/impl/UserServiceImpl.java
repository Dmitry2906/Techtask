package com.example.techtask.service.impl;

import com.example.techtask.model.Order;
import com.example.techtask.model.User;
import com.example.techtask.model.enumiration.OrderStatus;
import com.example.techtask.repository.UserRepository;
import com.example.techtask.service.UserService;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findUser() {
        // Возвращаем пользователя с максимальной общей суммой товаров, доставленных в 2003
        return userRepository.findAll().stream()
                .max((u1, u2) -> Double.compare(
                        calculateTotalDeliveredInYear(u1, 2003),
                        calculateTotalDeliveredInYear(u2, 2003)))
                .orElse(null);
    }

    @Override
    public List<User> findUsers() {
        // Возвращаем пользователей, у которых есть оплаченные заказы в 2010
        return userRepository.findAll().stream()
                .filter(user -> user.getOrders().stream()
                        .anyMatch(order -> order.getCreatedAt().getYear() == 2010 && isPaid(order)))
                .collect(Collectors.toList());
    }

    private double calculateTotalDeliveredInYear(User user, int year) {
        return user.getOrders().stream()
                .filter(order -> order.getCreatedAt().getYear() == year)
                .mapToDouble(order -> order.getPrice() * order.getQuantity())
                .sum();
    }

    private boolean isPaid(Order order) {
        // Здесь предполагается, что есть способ определить, что заказ оплачен, например, по статусу заказа
        return order.getOrderStatus().equals(OrderStatus.PAID);
    }
}
