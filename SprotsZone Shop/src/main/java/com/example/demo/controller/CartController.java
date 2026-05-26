package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class CartController {

    @Autowired CartItemRepository cartRepo;
    @Autowired ProductRepository productRepo;

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "cart";
    }

    @GetMapping("/cart/items")
    @ResponseBody
    public Map<String, Object> getCartItems(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return Map.of("error", "LOGIN_REQUIRED");

        List<CartItem> items = cartRepo.findByUser(user);
        double total = items.stream().mapToDouble(CartItem::getSubtotal).sum();

        List<Map<String, Object>> itemList = new ArrayList<>();
        for (CartItem item : items) {
            Map<String, Object> map = new HashMap<>();
            map.put("cartItemId", item.getId());
            map.put("productId",  item.getProduct().getId());
            map.put("name",       item.getProduct().getName());
            map.put("brand",      item.getProduct().getBrand());
            map.put("price",      item.getProduct().getPrice());
            map.put("imageUrl",   item.getProduct().getImageUrl());
            map.put("quantity",   item.getQuantity());
            map.put("subtotal",   item.getSubtotal());
            itemList.add(map);
        }

        return Map.of("items", itemList, "total", total);
    }

    @PostMapping("/cart/add")
    @ResponseBody
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam(name = "qty", defaultValue = "1") Integer qty,
                            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "LOGIN_REQUIRED";
        Product product = productRepo.findById(productId).orElse(null);
        if (product == null) return "PRODUCT_NOT_FOUND";
        Optional<CartItem> existing = cartRepo.findByUserAndProduct(user, product);
        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + qty);
            cartRepo.save(existing.get());
        } else {
            CartItem item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
            item.setQuantity(qty);
            cartRepo.save(item);
        }
        return "OK";
    }

    @PostMapping("/cart/update")
    @ResponseBody
    public String updateQty(@RequestParam("cartItemId") Long cartItemId,
                            @RequestParam("qty") Integer qty) {
        CartItem item = cartRepo.findById(cartItemId).orElse(null);
        if (item == null) return "NOT_FOUND";
        if (qty <= 0) { cartRepo.delete(item); }
        else { item.setQuantity(qty); cartRepo.save(item); }
        return "OK";
    }

    @PostMapping("/cart/remove")
    @ResponseBody
    public String removeFromCart(@RequestParam("cartItemId") Long cartItemId) {
        cartRepo.deleteById(cartItemId);
        return "OK";
    }
}