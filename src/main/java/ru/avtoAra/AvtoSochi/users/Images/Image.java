package ru.avtoAra.AvtoSochi.users.Images;

import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import ru.avtoAra.AvtoSochi.users.Product.Product;

@Entity
@Table(name = "image")
public class Image {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   private String name;
   private String originalFileName;
   private Long size;
   private String contentType;
   private boolean isPreviewImage;


   @Lob
   @JsonIgnore
   private byte[] bytes;

   public Image(byte[] bytes,
                String contentType,
                Long id,
                boolean isPreviewImage,
                String name,
                String originalFileName,
                Long size) {
      this.bytes = bytes;
      this.contentType = contentType;
      this.id = id;
      this.isPreviewImage = isPreviewImage;
      this.name = name;
      this.originalFileName = originalFileName;
      this.size = size;
   }
   public Image(){}

   public byte[] getBytes() {
      return bytes;
   }

   public void setBytes(byte[] bytes) {
      this.bytes = bytes;
   }

   public String getContentType() {
      return contentType;
   }

   public void setContentType(String contentType) {
      this.contentType = contentType;
   }

   public Long getId() {
      return id;
   }


   @JsonProperty("isPreviewImage") // Явное указание для поля json
   public boolean isPreviewImage() {
      return isPreviewImage;
   }

   public void setPreviewImage(boolean previewImage) {
      isPreviewImage = previewImage;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getOriginalFileName() {
      return originalFileName;
   }

   public void setOriginalFileName(String originalFileName) {
      this.originalFileName = originalFileName;
   }

   public Long getSize() {
      return size;
   }

   public void setSize(Long size) {
      this.size = size;
   }


   @ManyToOne(cascade = CascadeType.REFRESH , fetch = FetchType.EAGER)
   private Product product;


   public void setProduct(Product product) {
      this.product = product;
   }

   public Product getProduct() {
      return product;
   }
}