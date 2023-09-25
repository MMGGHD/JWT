package com.example.kakao.product;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.example.kakao.cart.Cart;
import com.example.kakao.product.option.Option;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class ProductResponse {

    // (기능1) 상품 목록보기
    @ToString
    @Getter
    @Setter
    public static class FindAllDTO {

    }

    // (기능2) 상품 상세보기
    @Setter
    public static class FindByIdDTO {

    }

    // 상품조회 + 옵션조회
    @Getter
    @Setter
    public static class FindByIdV1DTO {
        private Integer productId;
        private String productName;
        private String productImage;
        private Integer productPrice;
        private Integer productStartCount;
        private List<OptionDTO> options;

        public FindByIdV1DTO(Product product, List<Option> options) {
            this.productId = product.getId();
            this.productName = product.getProductName();
            this.productImage = product.getImage();
            this.productPrice = product.getPrice();
            this.productStartCount = 5;
            this.options = options.stream()
                    .map(o -> new OptionDTO(o))
                    .collect(Collectors.toList());
        }

        @Getter
        @Setter
        public class OptionDTO {
            private Integer optionId;
            private String optionName;
            private Integer optionPrice;

            public OptionDTO(Option option) {
                this.optionId = option.getId();
                this.optionName = option.getOptionName();
                this.optionPrice = option.getPrice();
            }
        }
    }

    // 양방향 매핑
    @Getter
    @Setter
    public static class FindByIdV2DTO {
        private Integer productId;
        private String productName;
        private String productImage;
        private Integer productPrice;
        private Integer productStartCount;
        private List<OptionDTO> options;

        public FindByIdV2DTO(Product product) {
            this.productId = product.getId();
            this.productName = product.getProductName();
            this.productImage = product.getImage();
            this.productPrice = product.getPrice();
            this.productStartCount = 5;
            System.out.println("이제 Lazy Loading 한다 =================");
            this.options = product.getOptions().stream()
                    .map(o -> new OptionDTO(o))
                    .collect(Collectors.toList());
        }

        @Getter
        @Setter
        public class OptionDTO {
            private Integer optionId;
            private String optionName;
            private Integer optionPrice;

            public OptionDTO(Option option) {
                System.out.println("이제 Lazy Loading 시작됨 =========");
                this.optionId = option.getId();
                this.optionName = option.getOptionName();
                this.optionPrice = option.getPrice();
            }
        }
    }

    // 옵션만 조회
    @Getter
    @Setter
    public static class FindByIdV3DTO {
        private Integer productId;
        private String productName;
        private String productImage;
        private Integer productPrice;
        private Integer productStartCount;
        private List<OptionDTO> options;

        public FindByIdV3DTO(List<Option> options) {
            System.out.println("이제 Lazy 시작한다???????????????????????");
            this.productId = options.get(0).getProduct().getId();
            this.productName = options.get(0).getProduct().getProductName();
            this.productImage = options.get(0).getProduct().getImage();
            this.productPrice = options.get(0).getProduct().getPrice();
            this.productStartCount = 5;
            this.options = options.stream()
                    .map(o -> new OptionDTO(o))
                    .collect(Collectors.toList());
        }

        @Getter
        @Setter
        public class OptionDTO {
            private Integer optionId;
            private String optionName;
            private Integer optionPrice;

            public OptionDTO(Option option) {
                this.optionId = option.getId();
                this.optionName = option.getOptionName();
                this.optionPrice = option.getPrice();
            }
        }
    }

    @Getter
    @Setter
    // DTO이름을 지을때 사용할 화면이름을 참조하면 좋다.
    public static class FindAllByUserDTO {

        private Integer totalPrice;
        private List<CartDTO> products;

        public FindAllByUserDTO(List<Product> products, List<Cart> carts) {

            this.products = products.stream()
                    .distinct()
                    .map(o -> new CartDTO(o, carts))
                    .collect(Collectors.toList());
            this.totalPrice = carts.stream().mapToInt(cart -> cart.getPrice()).sum();
        }

        @Getter
        @Setter

        // 내부클래스는 public을 뺀다.
        class CartDTO {

            private String productName;
            private List<Category> categorys;

            public CartDTO(Product product, List<Cart> carts) {
                this.categorys = carts.stream()
                        // .filter(carts.get(0).getOption().getProduct().getProductName().equals(product.getProductName()))
                        .filter(t -> t.getOption().getProduct().getId() == product.getId())
                        .map(o -> new Category(o.getOption(), o))
                        .collect(Collectors.toList());
                this.productName = product.getProductName();
            }

        }

        @Getter
        @Setter
        class Category {

            private String optionName;
            private Integer quantity;
            private Integer cartPrice;
            private Integer optionPrice;

            public Category(Option option, Cart cart) {
                this.optionName = cart.getOption().getOptionName();
                this.quantity = cart.getQuantity();
                this.cartPrice = cart.getPrice();
                this.optionPrice = cart.getOption().getPrice();
            }

        }

    }

}