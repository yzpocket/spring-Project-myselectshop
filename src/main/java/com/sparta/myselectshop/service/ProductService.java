package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.entity.UserRoleEnum;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

// 페이징 테스트 데이터 삽입
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    public static final int MIN_MY_PRICE = 100;
    public ProductResponseDto createProduct(ProductRequestDto requestDto, User user) {
        Product product = productRepository.save(new Product(requestDto, user));
        return new ProductResponseDto(product);
    }

    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductMypriceRequestDto requestDto) {
        int myprice = requestDto.getMyprice();
        if (myprice < MIN_MY_PRICE) {
            throw new IllegalArgumentException("유효하지 않은 관심 가격입니다. 최소 " + MIN_MY_PRICE + " 원 이상으로 설정해 주세요.");
        }

        Product product = productRepository.findById(id).orElseThrow(() ->
                new NullPointerException("해당 상품을 찾을 수 없습니다.")
        );

        product.update(requestDto);

        return new ProductResponseDto(product);
    }

    //CMD+E 단축키로 이전파일 살펴볼수있음
    //SHIFTSHIFT 단축키로 프로젝트 전체검색가능
    //ctrl+R 단축키로 Run

    // 지금 여기에 지연로딩에 대한 처리(@Transactional 환경)가 구현되어있지 않다. 그럼 정상적으로 작동하지 않을 것이라는것.
    // 이처럼 product와 productFolder의 레이지로딩을 정상 작동하게 하려면 여기에서 Transactional환경이 필요하다.
    @Transactional(readOnly = true) // 그런데 조회하는 용도기 때문에, READONLY 옵션을 사용해보자.
    public Page<ProductResponseDto> getProducts(User user, int page, int size, String sortBy, boolean isAsc) {
        // 정렬, 페이징 처리위한 페이저블객체
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // 유저 권한 확인
        UserRoleEnum userRoleEnum = user.getRole();

        Page<Product> productList;
        //
        if(userRoleEnum == UserRoleEnum.USER){
            productList = productRepository.findAllByUser(user, pageable);
        } else{
            productList = productRepository.findAll(pageable);
        }

        return productList.map(ProductResponseDto::new);
    }

    @Transactional
    public void updateBySearch(Long id, ItemDto itemDto) {
        Product product = productRepository.findById(id).orElseThrow(()->
                new NullPointerException("해당 상품은 존재하지 않습니다.")
        );
        product.updateByItemDto(itemDto);
    }
    // ADMIN 모든 상품 조회
    public List<ProductResponseDto> getProducts() {
        List<Product> productList = productRepository.findAll();
        List<ProductResponseDto> responseDtoList = new ArrayList<>();
        for (Product product : productList) {
            responseDtoList.add(new ProductResponseDto(product));
        }
        return responseDtoList;
    }
}
