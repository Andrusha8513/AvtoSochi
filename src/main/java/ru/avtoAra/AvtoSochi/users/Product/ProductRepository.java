package ru.avtoAra.AvtoSochi.users.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product , Long> {

   Optional<Product> findByArticleNumber(String article);
   List<Product> findByName(String name);

   @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images")
   List<Product> findAllWithImages();
}
