package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class OrderController {

    @Autowired OrderRepository orderRepo;
    @Autowired CartItemRepository cartRepo;

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        List<CartItem> items = cartRepo.findByUser(user);
        if (items.isEmpty()) return "redirect:/cart";

        double subtotal = items.stream().mapToDouble(CartItem::getSubtotal).sum();
        double delivery = subtotal >= 999 ? 0 : 99;
        double total = subtotal + delivery;

        model.addAttribute("user", user);
        model.addAttribute("cartItems", items);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("delivery", delivery);
        model.addAttribute("total", total);
        return "checkout";
    }

    @PostMapping("/order/place")
    @Transactional
    public String placeOrder(@RequestParam("fullName") String fullName,
                             @RequestParam("phone") String phone,
                             @RequestParam("address") String address,
                             @RequestParam("city") String city,
                             @RequestParam("pincode") String pincode,
                             @RequestParam("paymentMethod") String paymentMethod,
                             HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        List<CartItem> cartItems = cartRepo.findByUser(user);
        if (cartItems.isEmpty()) return "redirect:/cart";

        Order order = new Order();
        order.setUser(user);
        order.setFullName(fullName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setCity(city);
        order.setPincode(pincode);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("CONFIRMED");
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = cartItems.stream().map(ci -> {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(ci.getProduct().getPrice());
            return oi;
        }).collect(Collectors.toList());

        double total = orderItems.stream().mapToDouble(OrderItem::getSubtotal).sum();
        double delivery = total >= 999 ? 0 : 99;
        order.setTotalAmount(total + delivery);
        order.setItems(orderItems);
        orderRepo.save(order);

        cartRepo.deleteByUser(user);

        return "redirect:/order/confirmation/" + order.getId();
    }

    @GetMapping("/order/confirmation/{id}")
    public String confirmation(@PathVariable("id") Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        Order order = orderRepo.findById(id).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("order", order);
        return "order-confirmation";
    }

    @GetMapping("/order/{id}")
    public String orderDetail(@PathVariable("id") Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        Order order = orderRepo.findById(id).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("order", order);
        return "order-confirmation";
    }

    @GetMapping("/my-orders")
    public String myOrders(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        model.addAttribute("orders", orderRepo.findByUserOrderByCreatedAtDesc(user));
        return "my-orders";
    }
}