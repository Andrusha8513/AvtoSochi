package ru.avtoAra.AvtoSochi.users.Product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import ru.avtoAra.AvtoSochi.users.Images.Image;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer quantity;
    private String category;
    private String brand;
    private Double price;
    private String articleNumber;
    private String description;
    private String manufacturer;
    private String carBrand;
    private Boolean inStock;

     private Long summ;


    public Product(String articleNumber,
                   String brand,
                   String category,
                   String description,
                   Long id,
                   Boolean inStock,
                   String name,
                   String manufacturer,
                   String carBrand,
                   Double price,
                   Integer quantity) {
        this.articleNumber = articleNumber;
        this.brand = brand;
        this.category = category;
        this.description = description;
        this.id = id;
        this.inStock = inStock;
        this.name = name;
        this.manufacturer = manufacturer;
        this.carBrand = carBrand;
        this.price = price;
        this.quantity = quantity;

    }

    public Product() {
    }



    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCarBrand(){
        return carBrand;
    }
    public void setCarBrand(String carBrand){
        this.carBrand = carBrand;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }




    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "product") //mappedBy очень важная хуйня по итогу , чтобы не забыть см. word файл Spting boot
    private List<Image> images = new ArrayList<>();
    private Long previewImageId;
    private LocalDate dateOfCreated;


    @PrePersist
    private void init() {
        dateOfCreated = LocalDate.now();
    }

    @JsonIgnore
    public Image getPreviewImage() {
        if (previewImageId == null) return null;
        return images.stream()
                .filter(img -> img.getId().equals(previewImageId))
                .findFirst()
                .orElse(null);
    }

    public void addImageToProduct(Image image) {
        image.setProduct(this); // хуйня позволяет изображению понять к какому продукту она принадлежит
        images.add(image);// грубо говоря  - image.setProduct(this);  изображение → продукт
                                            //images.add(image);      продукт → изображение
    }

    @JsonIgnore
    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public void setPreviewImageId(Long previewImageId) {
        this.previewImageId = previewImageId;
    }


    public Long getPreviewImageId(){
        return previewImageId;
    }



}