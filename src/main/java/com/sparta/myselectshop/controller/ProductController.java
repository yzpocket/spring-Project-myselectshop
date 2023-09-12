package com.sparta.myselectshop.controller;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.security.UserDetailsImpl;
import com.sparta.myselectshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;
    // CREATE
    @PostMapping("/products")
    public ProductResponseDto createProduct(@RequestBody ProductRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return productService.createProduct(requestDto, userDetails.getUser());
    }

    // UPDATE
    @PutMapping("/products/{id}")
    public ProductResponseDto updateProduct(@PathVariable Long id, @RequestBody ProductMypriceRequestDto requestDto){
        return productService.updateProduct(id, requestDto);
    }

    // READ
    @GetMapping("/products")
    public Page<ProductResponseDto> getProducts(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("isAsc") boolean isAsc,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    )
    {
        return productService.getProducts(
                userDetails.getUser(),
                page-1,
                size,
                sortBy,
                isAsc
        );
    }

    // ADMIN - READ
    @GetMapping("/admin/products")
    public List<ProductResponseDto> getAllProducts(){
        return productService.getProducts();
    }

    @PostMapping("/products/{productId}/folder")
    public void addFolder(
            @PathVariable Long productId,        //ProductId 패스로 받아오고,
            @RequestParam Long folderId,         //폼형식으로 folder의 id가 넘어오니 RequestParam,
            @AuthenticationPrincipal UserDetailsImpl userDetails    // 상품과 그 폴더가 그 로그인한 유저의 것인지 확인이 필요하다. 로그인정보도 받아야 함.
    ){
        productService.addFolder(productId, folderId, userDetails.getUser());
    }
}
