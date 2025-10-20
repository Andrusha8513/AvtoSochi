package ru.avtoAra.AvtoSochi.users.Product;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.avtoAra.AvtoSochi.users.DTO.ImageDto;
import ru.avtoAra.AvtoSochi.users.DTO.ProductDto;
import ru.avtoAra.AvtoSochi.users.Images.Image;
import ru.avtoAra.AvtoSochi.users.Images.ImageRepository;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    public ProductService(ProductRepository productRepository, ImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
    }

    public List<Product> getAllProduct(String name) {
        // List<Product> products = productRepository.findAll();
        if (name != null)
            return productRepository.findByName(name);
        return productRepository.findAll();
    }


    @Transactional
    public Product createProduct(Product product, List<MultipartFile> files) throws IOException {
        if (files != null && !files.isEmpty()) {
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                if (file.isEmpty()) {
                    continue;
                }
                Image image = toImageEntity(file);
                if (i == 0) {
                    image.setPreviewImage(true);
                }

                product.addImageToProduct(image);
            }
        }
        if (productRepository.findByArticleNumber(product.getArticleNumber()).isPresent()) {
            throw new IllegalArgumentException("Продукт с таким артикулом уже существует");
        }
        Product saveProduct = productRepository.save(product);
        if (!saveProduct.getImages().isEmpty()) {
            saveProduct.setPreviewImageId(saveProduct.getImages().get(0).getId());
        }
        return productRepository.save(saveProduct);

    }


    private Image toImageEntity(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        Image image = new Image();
        image.setName(UUID.randomUUID().toString());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());

        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Файл должен быть изображением");
        }

        if (file.getSize() > 50_000_000) {
            throw new IllegalArgumentException("Размер файла не должен превышать 50MB");
        }

        image.setBytes(file.getBytes());
        return image;
    }



    @Transactional
    public Product updateProduct(Long id,
                                 Product productDetails,
                                 List<MultipartFile> files) throws IOException {
        Product product = findById(id);

        product.setName(productDetails.getName());
        product.setQuantity(productDetails.getQuantity());
        product.setCategory(productDetails.getCategory());
        product.setBrand(productDetails.getBrand());
        product.setPrice(productDetails.getPrice());
        product.setDescription(productDetails.getDescription());
        product.setCarBrand(productDetails.getCarBrand());
        product.setInStock(productDetails.getInStock());
        product.setManufacturer(productDetails.getManufacturer());

        if (!product.getArticleNumber().equals(productDetails.getArticleNumber())) {
            if (productRepository.findByArticleNumber(productDetails.getArticleNumber()).isPresent()) {
                throw new IllegalArgumentException("Продукт с таким артикулом существует");
            }
            product.setArticleNumber(productDetails.getArticleNumber());
        }

        if (files != null && !files.isEmpty()) {
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                Image image = toImageEntity(file);
                product.addImageToProduct(image);
            }
        }

        if (product.getPreviewImage() == null && !product.getImages().isEmpty()) {
            product.setPreviewImageId(product.getImages().get(0).getId());
            product.getImages().get(0).setPreviewImage(true);
        }
        return productRepository.save(product);
    }






    @Transactional
    public void setProductPreviewImage(Long productId, Long imageId) {
        Product product = findById(productId);
        Image newPreview = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("изображения с id= " + imageId + "не найдено"));
        if (!newPreview.getProduct().getId().equals(productId)) {
            throw new IllegalArgumentException("Изображение с id= " + imageId + "не принадлежит продукту");
        }
        product.getImages().forEach(img -> img.setPreviewImage(false));

        newPreview.setPreviewImage(true);
        product.setPreviewImageId(newPreview.getId());

        imageRepository.saveAll(product.getImages());
        productRepository.save(product);
    }

    @Transactional
    public void deleteImageFromProduct(Long productId, Long imageId) {
        Product product = findById(productId);
        Image imageToDelete = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Изображения с таким id= " + imageId + "нет"));
        if (!imageToDelete.getProduct().getId().equals(productId)) {
            throw new IllegalArgumentException("Изображение с id=" + imageId + " не принадлежит продукту с id=" + productId + ".");
        }

        product.getImages().remove(imageToDelete);

        if (imageToDelete.getId().equals(product.getPreviewImageId())) {
            if (!product.getImages().isEmpty()) {
                Image newPreview = product.getImages().get(0);
                newPreview.setPreviewImage(true);
                product.setPreviewImageId(newPreview.getId());


            } else {
                product.setPreviewImageId(null);
            }
        }
        imageRepository.delete(imageToDelete);
        productRepository.save(product);

    }

//Отсюда и вниз разобраться
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Продукт с id=" + id + " не найден.");
        }
        productRepository.deleteById(id);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Продукта с таким id" + id + " нет"));
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProductsAsDto(String name) {
        List<Product> products;
        if (name != null && !name.isEmpty()) {
            products = productRepository.findByName(name);
        } else {
            products = productRepository.findAllWithImages();
        }
        return products.stream()
                .map(this::toProductDto) // Используем маппер
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDto findByIdAsDto(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Продукта с таким id " + id + " нет"));
        return toProductDto(product); // Используем маппер
    }
    private ProductDto toProductDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setQuantity(product.getQuantity());
        dto.setCategory(product.getCategory());
        dto.setBrand(product.getBrand());
        dto.setPrice(product.getPrice());
        dto.setArticleNumber(product.getArticleNumber());
        dto.setDescription(product.getDescription());
        dto.setManufacturer(product.getManufacturer());
        dto.setCarBrand(product.getCarBrand());
        dto.setInStock(product.getInStock());
        dto.setPreviewImageId(product.getPreviewImageId());

        // ВАЖНО:  безопасно обращаюсь к ленивой коллекции ВНУТРИ транзакции
        List<ImageDto> imageDto = product.getImages().stream()
                .map(img -> new ImageDto(img.getId(), img.isPreviewImage(), img.getOriginalFileName()))
                .collect(Collectors.toList());
        dto.setImages(imageDto);

        return dto;
    }


}

//    @Transactional
//    public Product updateProduct(Long id, Product productDetails, List<MultipartFile> files) throws IOException {
//        Product product = findById(id);
//
//
//        product.setName(productDetails.getName());
//        product.setQuantity(productDetails.getQuantity());
//        product.setCategory(productDetails.getCategory());
//        product.setBrand(productDetails.getBrand());
//        product.setPrice(productDetails.getPrice());
//        product.setDescription(productDetails.getDescription());
//        product.setInStock(productDetails.getInStock());
//
//
//        if (!product.getArticleNumber().equals(productDetails.getArticleNumber())) {
//            if (productRepository.findByArticleNumber(productDetails.getArticleNumber()).isPresent()) {
//                throw new IllegalArgumentException("Продукт с таким артикулом уже существует");
//            }
//            product.setArticleNumber(productDetails.getArticleNumber());
//        }
//
//
//        if (files != null && !files.isEmpty()) {
//
//            product.getImages().clear();
//
//
//            for (int i = 0; i < files.size(); i++) {
//                MultipartFile file = files.get(i);
//                if (file.isEmpty()) continue;
//
//                Image image = toImageEntity(file);
//                if (i == 0) {
//                    image.setPreviewImage(true);
//                }
//                product.addImageToProduct(image);
//            }
//        }
//
//
//        Product updatedProduct = productRepository.save(product);
//
//
//        if (!updatedProduct.getImages().isEmpty()) {
//            Long newPreviewId = updatedProduct.getImages().stream()
//                    .filter(Image::isPreviewImage)
//                    .findFirst()
//                    .map(Image::getId)
//                    .orElse(updatedProduct.getImages().get(0).getId());
//            updatedProduct.setPreviewImageId(newPreviewId);
//        } else {
//            updatedProduct.setPreviewImageId(null);
//        }
//
//        return productRepository.save(updatedProduct);
//    }

