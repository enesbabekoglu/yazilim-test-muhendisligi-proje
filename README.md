# E-Ticaret Ürün API Simülasyonu ve Rest Assured Otomatik Regresyon Testleri

Yazılım Test Mühendisliği dersi proje ödevi. Spring Boot tabanlı e-ticaret ürün API'si ve Rest Assured ile otomatik regresyon testleri.

## Teknolojiler

- Java 17
- Maven
- Spring Boot 3.2.5
- JUnit 5
- Rest Assured 5.4.0

## API Endpointleri

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| GET | `/products` | Tüm ürünleri listeler |
| GET | `/products/{id}` | ID'ye göre ürün getirir |
| POST | `/products` | Yeni ürün ekler |
| DELETE | `/products/{id}` | Ürün siler |

### Örnek Ürün JSON

```json
{
  "id": 1,
  "name": "Laptop",
  "price": 25000,
  "stock": 15,
  "category": "Electronics"
}
```

### Doğrulama Kuralları

- Ürün adı boş olamaz
- Fiyat 0'dan büyük olmalı
- Stok negatif olamaz
- Aynı isimde ürün eklenemez (409 Conflict)

## Proje Yapısı

```
yazilim-test-muhendisligi-proje
├── pom.xml
├── postman-collection.json
├── .gitignore
├── README.md
├── src
│   ├── main/java/com/example/ecommerce
│   │   ├── EcommerceApplication.java
│   │   ├── Product.java
│   │   └── ProductController.java
│   └── test/java/com/example/ecommerce
│       └── ProductApiRegressionTest.java
```

## Test Senaryoları

| # | Test Adı | Endpoint | Status | Açıklama |
|---|----------|----------|--------|----------|
| 1 | testGetAllProducts | GET /products | 200 | Liste boyutu, fiyat > 0, stok >= 0 |
| 2 | testGetProductById | GET /products/1 | 200 | Laptop detayı (id, name, price, stock, category) |
| 3 | testGetProductNotFound | GET /products/9999 | 404 | Olmayan ürün hatası |
| 4 | testCreateProduct | POST /products | 201 | iPhone 17 Pro ekleme |
| 5 | testCreateDuplicateProduct | POST /products | 409 | Duplicate Laptop kontrolü |
| 6 | testCreateProductWithInvalidPrice | POST /products | 400 | Negatif fiyat doğrulama |
| 7 | testDeleteProduct | DELETE /products/{id} | 200 | Ürün ekleme ve silme |
| 8 | testDeleteProductNotFound | DELETE /products/9999 | 404 | Olmayan ürünü silme hatası |

## Çalıştırma

### Testleri Çalıştırma

```bash
mvn test
```

Başarılı çıktı:

```
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### API'yi Manuel Çalıştırma

```bash
mvn spring-boot:run
```

Sonra başka bir terminalde:

```bash
curl http://localhost:8181/products
curl http://localhost:8181/products/1
curl -X POST http://localhost:8181/products -H "Content-Type: application/json" -d '{"name":"Mouse","price":800,"stock":50,"category":"Electronics"}'
curl -X DELETE http://localhost:8181/products/3
```

### Postman ile Test

Postman'da `postman-collection.json` dosyasını import edin ve istekleri çalıştırın.

## Gereksinimler

- Java 17+
- Maven 3.9+
