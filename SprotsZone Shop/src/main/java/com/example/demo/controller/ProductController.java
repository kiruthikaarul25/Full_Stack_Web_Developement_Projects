package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepo;

    // ✅ API: All products as JSON (for main.js dynamic load)
    @GetMapping("/api/products")
    @ResponseBody
    public List<Map<String, Object>> getAllProducts() {
        List<Product> all = productRepo.findAll();
        return all.stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id",     p.getId());
            m.put("brand",  p.getBrand());
            m.put("name",   p.getName());
            m.put("img",    p.getImageUrl());
            m.put("price",  p.getPrice().intValue());
            m.put("old",    p.getOriginalPrice().intValue());
            m.put("off",    p.getDiscountPercent());
            m.put("rating", p.getRating());
            m.put("count",  p.getReviewCount());
            m.put("badge",  p.getBadge() != null ? p.getBadge() : "new");
            m.put("cat",    p.getCategory());
            return m;
        }).collect(Collectors.toList());
    }

    // ✅ Product detail page
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Product product = productRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        List<Product> related = productRepo.findByCategory(product.getCategory())
            .stream()
            .filter(p -> !p.getId().equals(id))
            .limit(4)
            .collect(Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", related);
        return "product";
    }
}
