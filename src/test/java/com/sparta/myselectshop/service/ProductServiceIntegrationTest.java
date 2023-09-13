package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
//통합 테스트 (Integration Test)
//- 두 개 이상의 모듈이 연결된 상태를 테스트할 수 있습니다.
//- 모듈 간의 연결에서 발생하는 에러 검증 가능합니다.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 스프링이 동작되도록 해주는 애너테이션 //서버의 PORT 를 랜덤으로 설정합니다.
//- 테스트 수행 시 스프링이 동작
//    - Spring IoC/DI 기능을 사용 가능
//    - Repository를 사용해 DB CRUD가 가능
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 테스트 인스턴스의 생성 단위를 클래스로 변경합니다.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductServiceIntegrationTest {
    //- 관심상품 통합 테스트 설계
    //    1. 신규 관심상품 등록
    //        - User는 테스트 사용자인 1번 사용자
    //    2. 신규 등록된 관심상품의 희망 최저가 변경
    //        - 1번에서 등록한 관심상품의 희망 최저가를 변경
    //    3. 회원 Id 로 등록된 모든 관심상품 조회
    //        - 조회된 관심상품 중 1번에서 등록한 관심상품이 존재하는지?
    //        - 2번에서 업데이트한 내용이 잘 반영되었는지?

    @Autowired
    ProductService productService; //서비스 주입
    @Autowired
    UserRepository userRepository; //레포지토리 주입

    User user;
    ProductResponseDto createdProduct = null;
    int updatedMyPrice = -1;

    @Test
    @Order(1)
    @DisplayName("신규 관심상품 등록")
    void test1() {
        // given
        String title = "Apple <b>에어팟</b> 2세대 유선충전 모델 (MV7N2KH/A)";
        String imageUrl = "https://shopping-phinf.pstatic.net/main_1862208/18622086330.20200831140839.jpg";
        String linkUrl = "https://search.shopping.naver.com/gate.nhn?id=18622086330";
        int lPrice = 173900;
        ProductRequestDto requestDto = new ProductRequestDto(
                title,
                imageUrl,
                linkUrl,
                lPrice
        );
        user = userRepository.findById(1L).orElse(null);

        // when
        ProductResponseDto product = productService.createProduct(requestDto, user);

        // then // 입력과, Service의createProduct로 전달된 값이 수행된 반환값이 Dto객체인 product에 제대로 저장 및되었는지 결과 확인
        assertNotNull(product.getId());
        assertEquals(title, product.getTitle());
        assertEquals(imageUrl, product.getImage());
        assertEquals(linkUrl, product.getLink());
        assertEquals(lPrice, product.getLprice());
        assertEquals(0, product.getMyprice());
        createdProduct = product;
    }

    @Test
    @Order(2)
    @DisplayName("신규 등록된 관심상품의 희망 최저가 변경")
    void test2() {
        // given //order1에서 생성된 product를 넘겨받음
        Long productId = this.createdProduct.getId();
        int myPrice = 173000;
        ProductMypriceRequestDto requestDto = new ProductMypriceRequestDto();
        requestDto.setMyprice(myPrice);

        // when
        ProductResponseDto product = productService.updateProduct(productId, requestDto);

        // then // 입력과, Service의createProduct로 전달된 값이 수행된 반환값이 Dto객체인 product에 제대로 저장되었는지 결과 확인
        assertNotNull(product.getId());
        assertEquals(this.createdProduct.getTitle(), product.getTitle());
        assertEquals(this.createdProduct.getImage(), product.getImage());
        assertEquals(this.createdProduct.getLink(), product.getLink());
        assertEquals(this.createdProduct.getLprice(), product.getLprice());
        assertEquals(myPrice, product.getMyprice());
        this.updatedMyPrice = myPrice;
    }

    @Test
    @Order(3)
    @DisplayName("회원이 등록한 모든 관심상품 조회")
    void test3() {
        // given
        // when
        Page<ProductResponseDto> productList = productService.getProducts(user,
                0, 10, "id", false);

        // then
        // 1. 전체 상품에서 테스트에 의해 생성된 상품 찾아오기 (상품의 id 로 찾음)
        Long createdProductId = this.createdProduct.getId();
        ProductResponseDto foundProduct = productList.stream()
                .filter(product -> product.getId().equals(createdProductId))
                .findFirst()
                .orElse(null);

        // 2. Order(1) 테스트에 의해 생성된 상품과 일치하는지 검증
        assertNotNull(foundProduct);
        assertEquals(this.createdProduct.getId(), foundProduct.getId());
        assertEquals(this.createdProduct.getTitle(), foundProduct.getTitle());
        assertEquals(this.createdProduct.getImage(), foundProduct.getImage());
        assertEquals(this.createdProduct.getLink(), foundProduct.getLink());
        assertEquals(this.createdProduct.getLprice(), foundProduct.getLprice());

        // 3. Order(2) 테스트에 의해 myPrice 가격이 정상적으로 업데이트되었는지 검증
        assertEquals(this.updatedMyPrice, foundProduct.getMyprice());
    }
}