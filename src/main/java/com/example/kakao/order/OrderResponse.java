package com.example.kakao.order;

import java.util.List;
import java.util.stream.Collectors;

import com.example.kakao.cart.Cart;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class OrderResponse {

    // (기능4) 주문상품 정보조회 (유저별)
    @ToString
    @Getter
    @Setter
    public static class FindAllByUserDTO {

        private List<CartDTO> carts;
        private Integer totalPrice;

        public FindAllByUserDTO(List<Cart> carts) {
            this.carts = carts.stream()
                    // .map(CartDTO::new) 메서드참조(Method Reference) 문법
                    // .map(o -> new CartDTO(o)) << 와 같다.
                    .map(CartDTO::new)
                    .collect(Collectors.toList());
            this.totalPrice = 0;

            for (Cart cart : carts) {
                totalPrice += cart.getPrice();
            }
        }

        @Getter
        @Setter
        public class CartDTO {

            private Integer cartId;
            private String cartName;
            private int price;
            private int quantity;

            public CartDTO(Cart cart) {
                this.cartId = cart.getId();
                this.cartName = cart.getOption().getProduct().getProductName() + " - "
                        + cart.getOption().getOptionName();
                this.price = cart.getPrice();
                this.quantity = cart.getQuantity();
            }

        }

    }

    // (기능5) 주문결과 확인
    @ToString
    @Getter
    @Setter
    public static class FindByIdDTO {

    }
}
