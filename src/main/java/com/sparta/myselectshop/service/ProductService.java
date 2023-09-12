package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.*;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.repository.FolderRepository;
import com.sparta.myselectshop.repository.ProductFolderRepository;
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
import java.util.Optional;

// 페이징 테스트 데이터 삽입
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final FolderRepository folderRepository;
    private final ProductFolderRepository productFolderRepository;
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

    // 상품에 폴더 추가
    public void addFolder(Long productId, Long folderId, User user) {
        //상품조회
        Product product = productRepository.findById(productId).orElseThrow(
                ()-> new NullPointerException("해당 상품이 존재하지 않습니다.")
        );
        //폴더조회
        Folder folder = folderRepository.findById(folderId).orElseThrow(
                () -> new NullPointerException("해당 폴더가 존재하지 않습니다.")
        );

        //위 조회한 상품과 폴더가 *로그인된 유저*가 등록한것이 맞는지 확인필요!
        if(!product.getUser().getId().equals(user.getId())//로그인유저꺼랑 상품의 유저 아이디랑 맞지 않으면,
        || !folder.getUser().getId().equals(user.getId())//로그인유저꺼랑 폴더의 유저 아이디랑 맞지 않으면, -> 둘중 하나라도 아니라면!
        ){
            throw new IllegalArgumentException("회원님의 관심상품이 아니거나, 회원님의 폴더가 아닙니다.");
        }

        //중복확인하기
        //쿼리메소드를 써서 해보자. -> 미들테이블에 product_id랑 folder_id를 확인해서, 해당 행(row)가 DB에있다면 중복된다는 것이니 그것을 처리
        //ProductFolder Repository를 통해서 확인할 수 있다. 주입go
        Optional<ProductFolder> overlapFolder = productFolderRepository.findByProductAndFolder(product, folder); //이게 존재하면 중복이 확인되는것이고, 찾을수 없으면 중복이 아니니 등록으로 이어지면됨
        if(overlapFolder.isPresent()){ //검증
            throw new IllegalArgumentException("중복된 폴더입니다.");
        }
        //여기까지 통과했다면, 상품이있으며,폴더가있으며,회원의상품과폴더이며, 중복되지도않는 폴더로
        //등록가능

        productFolderRepository.save(new ProductFolder(product, folder)); //Entity객체 하나는 == DB의 Row하나다. -> product, folder로 객체를만들어야 된다(생성자로)
        //Run으로 테스트해보면, 로그인한 상품을 폴더에 추가하는부분을 눌러보면, 유저가생성한 폴더의 갯수만큼 드롭다운이 나타나며,
    }
}
