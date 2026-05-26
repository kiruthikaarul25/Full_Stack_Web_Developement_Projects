package com.example.demo;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StartApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }

    @Bean
    CommandLineRunner seedProducts(ProductRepository repo) {
        return args -> {
            if (repo.count() > 0) return; // Already seeded - skip

            String[][] data = {
                {"SS",        "TON Reserve Edition Cricket Bat",     "cricket",    "Top-tier cricket bat for professionals",          "4999",  "7999",  "https://images.unsplash.com/photo-1531415074968-036ba1b575da?w=400&q=80",  "50",  "4.8", "1240", "hot"},
                {"Adidas",    "Predator Elite Football Boots",        "football",   "Elite football boots with superior grip",          "6499",  "9999",  "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&q=80",     "40",  "4.7",  "890", "sale"},
                {"Yonex",     "Astrox 88D Pro Badminton Racket",      "badminton",  "Pro-level badminton racket for competitive play",  "8999",  "12000", "https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=400&q=80",  "30",  "4.9",  "567", "top"},
                {"Nike",      "Air Zoom Pegasus 41 Running Shoes",    "running",    "Lightweight running shoes for all distances",      "9999",  "14999", "https://images.unsplash.com/photo-1491553895911-0055eca6402d?w=400&q=80",  "60",  "4.6", "2100", "sale"},
                {"Spalding",  "NBA Official Game Basketball",          "basketball", "Official NBA game ball",                           "2999",  "4499",  "https://images.unsplash.com/photo-1546519638-68e109498ffc?w=400&q=80",     "35",  "4.5",  "430", "new"},
                {"Wilson",    "Pro Staff RF97 Tennis Racket",          "tennis",     "Used by Roger Federer, ultimate precision",        "15999", "22000", "https://images.unsplash.com/photo-1622279457486-62dbd47a3b77?w=400&q=80",  "20",  "4.8",  "312", "top"},
                {"Decathlon", "Pro Boxing Gloves 16oz",                "boxing",     "Professional boxing gloves for training",          "1799",  "2999",  "https://images.unsplash.com/photo-1585855822167-b37f61dc900a?w=400&q=80",  "45",  "4.4",  "780", "sale"},
                {"Puma",      "Wired Run Pure Sneakers",               "running",    "Stylish and comfortable running sneakers",         "3499",  "4999",  "https://images.unsplash.com/photo-1511556820780-d912e42b4980?w=400&q=80",  "55",  "4.3",  "660", "new"},
                {"Nike",      "Dri-FIT Yoga Pants",                    "yoga",       "Breathable yoga pants for all-day comfort",        "2299",  "3499",  "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=400&q=80",     "70",  "4.6",  "192", "new"},
                {"MRF",       "Genius Grand Edition Cricket Bat",      "cricket",    "MRF premium bat for serious batters",              "5499",  "7999",  "https://images.unsplash.com/photo-1540747913346-19212a4de6b9?w=400&q=80",  "25",  "4.7",  "388", "new"},
                {"Reebok",    "CrossFit Nano X4 Shoes",                "gym",        "Best cross-training shoes for gym warriors",       "7999",  "11999", "https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=400&q=80",  "30",  "4.5",  "245", "new"},
                {"Adidas",    "Tiro 23 Football Training Kit",         "football",   "Professional football training kit by Adidas",     "1999",  "2999",  "https://images.unsplash.com/photo-1577223625816-7546f13df25d?w=400&q=80",  "80",  "4.4",  "510", "new"},
            };

            for (String[] d : data) {
                Product p = new Product();
                p.setBrand(d[0]);
                p.setName(d[1]);
                p.setCategory(d[2]);
                p.setDescription(d[3]);
                p.setPrice(Double.parseDouble(d[4]));
                p.setOriginalPrice(Double.parseDouble(d[5]));
                p.setImageUrl(d[6]);
                p.setStock(Integer.parseInt(d[7]));
                p.setRating(Double.parseDouble(d[8]));
                p.setReviewCount(Integer.parseInt(d[9]));
                p.setBadge(d[10]);
                repo.save(p);
            }

            System.out.println("✅ " + repo.count() + " products seeded successfully!");
        };
    }
}
