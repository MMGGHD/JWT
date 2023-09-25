package com.example.kakao.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductJPARepository extends JpaRepository<Product, Integer> {

    @Query(value = "SELECT * FROM PRODUCT_TB pt left outer join Option_tb ot on pt.id = ot.product_id left outer join cart_tb ct on ot.id = ct.option_id where ct.user_id =:id", nativeQuery = true)
    List<Product> findByUserId(@Param("id") int id);

}
