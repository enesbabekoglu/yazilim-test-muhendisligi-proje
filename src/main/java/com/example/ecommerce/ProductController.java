package com.example.ecommerce;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

// REST API Controller - /products endpoint'lerini yönetir
@RestController
@RequestMapping("/products")
public class ProductController {

    // Ürünleri geçici olarak bellekte tutuyoruz (veritabanı yok)
    private final List<Product> products = new ArrayList<>();
    // Yeni ürün eklenirken otomatik ID üretmek için sayaç
    private final AtomicInteger idCounter = new AtomicInteger(3);

    // Başlangıçta 3 örnek ürün ekliyoruz (farklı kategoriler ve stok adetleriyle)
    public ProductController() {
        products.add(new Product(1, "Laptop", 25000, 15, "Electronics"));
        products.add(new Product(2, "Telefon", 15000, 30, "Electronics"));
        products.add(new Product(3, "Kitap", 250, 100, "Education"));
    }

    // GET /products - Tüm ürünleri listeler
    @GetMapping
    public List<Product> getAllProducts() {
        return products;
    }

    // GET /products/{id} - ID'ye göre ürün getirir, yoksa 404 döner
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable int id) {
        var found = products.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
        if (found.isPresent()) {
            return ResponseEntity.ok(found.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Ürün bulunamadı", "id", id));
    }

    // POST /products - Yeni ürün ekler
    // Aynı isimde ürün varsa 409 Conflict döner
    // Fiyat 0 veya negatifse 400 Bad Request döner
    // Stok negatifse 400 Bad Request döner
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Ürün adı boş olamaz"));
        }

        if (product.getPrice() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Ürün fiyatı 0'dan büyük olmalıdır"));
        }

        if (product.getStock() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Stok negatif olamaz"));
        }

        boolean exists = products.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(product.getName()));
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Bu isimde bir ürün zaten mevcut", "name", product.getName()));
        }

        product.setId(idCounter.getAndIncrement());
        products.add(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    // DELETE /products/{id} - ID'ye göre ürün siler, yoksa 404 döner
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) {
        var found = products.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
        if (found.isPresent()) {
            products.remove(found.get());
            return ResponseEntity.ok(Map.of("message", "Ürün silindi", "id", id));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Ürün bulunamadı", "id", id));
    }
}
