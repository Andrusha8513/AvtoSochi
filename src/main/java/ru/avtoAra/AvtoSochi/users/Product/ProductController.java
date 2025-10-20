package ru.avtoAra.AvtoSochi.users.Product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.avtoAra.AvtoSochi.users.DTO.ProductDto;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/AllProducts")
    public ResponseEntity<List<ProductDto>> getAllProduct(@RequestParam(name = "name", required = false) String name) {
        List<ProductDto> products = productService.getAllProductsAsDto(name);
        return ResponseEntity.ok(products);
    }


    @PostMapping(value = "/createProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> createProduct(@RequestPart("product") String productJson,
                                                 @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Product product = objectMapper.readValue(productJson, Product.class);

        try {
            Product createProduct = productService.createProduct(product, files);
            return ResponseEntity.status(HttpStatus.CREATED).body(createProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        try {
            ProductDto product = productService.findByIdAsDto(id);
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,
                                                 @RequestPart("product") String productJson,
                                                 @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Product productDetails = objectMapper.readValue(productJson, Product.class);

        try {
            Product updatedProduct = productService.updateProduct(id, productDetails, files);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{productId}/images/{imageId}/set-preview")
    public ResponseEntity<Void> setProductFromPreview(@PathVariable Long productId,
                                                      @PathVariable Long imageId) {
        try {
            productService.setProductPreviewImage(productId, imageId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<Void> deleteImageFromProduct(@PathVariable Long productId,
                                                       @PathVariable Long imageId){
        try {
            productService.deleteImageFromProduct(productId, imageId);
            return ResponseEntity.ok().build();
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }
}

//    @PostMapping(value = "/createProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<Product> createProduct(
//            @RequestPart("product") String productJson,
//            @RequestPart(value = "file1", required = false) MultipartFile file1,
//            @RequestPart(value = "file2", required = false) MultipartFile file2,
//            @RequestPart(value = "file3", required = false) MultipartFile file3) throws IOException {
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        Product product = objectMapper.readValue(productJson, Product.class);
//
//        try {
//            Product createdProduct = productService.createProduct(product, file1, file2, file3);
//            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }