package com.example.kakao.cart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.kakao.product.Product;
import com.example.kakao.product.option.Option;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.bytebuddy.dynamic.scaffold.TypeInitializer.Drain;

public class CartResponse {

    // (기능3) 장바구니 조회
    @ToString
    @Getter
    @Setter
    public static class FindAllByUserDTO {

        private Integer totalPrice;
        private List<Category> products;
        // private List<DTO> dtos;

        public FindAllByUserDTO(List<Cart> carts) {

            // this.products = carts.stream()
            // .collect(Collectors.groupingBy(
            // cart -> cart.getOption().getProduct().getId(),
            // Collectors.mapping(Categorys::new, Collectors.toList())))
            // .values()
            // .stream()
            // .map(Categorys::new)
            // .collect(Collectors.toList());

            this.products = carts.stream()
                    .map(o -> new Category(carts))
                    .collect(Collectors.toList());

        }

        @Getter
        @Setter
        public class Category {

            private String productName;
            private List<CartDTO> carts;

            public Category(List<Cart> carts) {
                this.carts = carts.stream()
                        .map(o -> new CartDTO(o))
                        .collect(Collectors.toList());
            }

        }

        // @Getter
        // @Setter
        // public class Carts {

        // private String productName;
        // private List<List<DTO>> dtoList;

        // public Carts(List<Cart> carts) {
        // Map<Integer, List<DTO>> productMap = carts.stream()
        // .collect(Collectors.groupingBy(
        // cart -> cart.getOption().getProduct().getId(),
        // Collectors.mapping(cart -> new DTO(Collections.singletonList(cart)),
        // Collectors.toList())));

        // this.dtoList = new ArrayList<>(productMap.values());
        // // this.dtoList = new ArrayList<>(productMap.values());

        // this.productName = carts.get(0).getOption().getProduct().getProductName();

        // }

        // }

        // @Getter
        // @Setter
        // public class DTO {

        // private List<CartDTO> carts;

        // public DTO(List<Cart> carts) {
        // this.carts = carts.stream()
        // .map(o -> new CartDTO(o))
        // .collect(Collectors.toList());
        // }

        // }

        @Getter
        @Setter
        public class CartDTO {

            private Integer optionPrice;
            private String optionName;
            private Integer quantity;
            private Integer cartPrice;

            public CartDTO(Cart cart) {
                this.optionPrice = cart.getOption().getPrice();
                this.optionName = cart.getOption().getOptionName();
                this.quantity = cart.getQuantity();
                this.cartPrice = cart.getPrice();
            }

        }
    }
}
