package ru.avtoAra.AvtoSochi.users.DTO;

import java.util.List;

public class ProductDto {
    // Поля для каталога и админ-панели
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
    private Long previewImageId;

    // Поле для модального окна редактирования
    private List<ImageDto> images;



    public ProductDto() {}

    public ProductDto(String name, String articleNumber) {
        this.name = name;
        this.articleNumber = articleNumber;
    }

  public ProductDto(Long id,
                    String name,
                    Integer quantity,
                    String category,
                    String brand,
                    Double price,
                    String articleNumber,
                    String description,
                    String manufacturer,
                    String carBrand,
                    Boolean inStock,
                    Long previewImageId){
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.carBrand = category;
        this.brand = brand;
        this.price = price;
        this.articleNumber = articleNumber;
        this.description = description;
        this.manufacturer = manufacturer;
        this.carBrand = carBrand;
        this.inStock = inStock;
        this.previewImageId = previewImageId;

  }

//    public ProductDto(String articleNumber,
//                      String brand,
//                      String category,
//                      String description,
//                      Long id,
//                      Boolean inStock,
//                      String name,
//                      String manufacturer,
//                      String carBrand,
//                      Double price,
//                      Integer quantity) {
//    }


    public Long getId() {
        return id; }

    public void setId(Long id) {
        this.id = id; }

    public String getName() {
        return name; }

    public void setName(String name) {
        this.name = name; }

    public Integer getQuantity() {
        return quantity; }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity; }

    public String getCategory() {
        return category; }

    public void setCategory(String category) {
        this.category = category; }

    public String getBrand() {
        return brand; }

    public void setBrand(String brand) {
        this.brand = brand; }

    public Double getPrice() {
        return price; }

    public void setPrice(Double price) {
        this.price = price; }

    public String getArticleNumber() {
        return articleNumber; }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber; }

    public String getDescription() {
        return description; }

    public void setDescription(String description) {
        this.description = description; }

    public String getManufacturer() {
        return manufacturer; }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer; }

    public String getCarBrand(){
        return carBrand;}

    public void setCarBrand(String carBrand){
        this.carBrand = carBrand;}

    public Boolean getInStock() {
        return inStock; }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock; }

    public Long getPreviewImageId() {
        return previewImageId; }

    public void setPreviewImageId(Long previewImageId) {
        this.previewImageId = previewImageId; }

    public List<ImageDto> getImages() {
        return images; }

    public void setImages(List<ImageDto> images) {
        this.images = images; }
}