package com.sparta.myselectshop.entity;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.naver.dto.ItemDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity // JPA가 관리할 수 있는 Entity 클래스 지정
@Getter
@Setter
@Table(name = "product") // 매핑할 테이블의 이름을 지정
@NoArgsConstructor
public class Product extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private int lprice;

    @Column(nullable = false)
    private int myprice;

    // 연관관계 설정 - 상품 등록 시, 등록 요청한 회원 정보 추가가 필요하다
    // -> 상품 테이블은 회원 필드가 필요하다 N:1이다 Product->User
    // 회원 한명은 다수의 상품을 가질 수 있다.
    // 따라서 상품 테이블은 회원을 참조해야 한다. == 상품 테이블에는 User user가 필요하다.
    // 회원 User에서는 상품 객체의 정보(필드)를 조회하는 경우가 없다.
    // 따라서 상품:회원을 N:1단방향 연관관계로 설정한다
    // 다대일 단방향이기 때문에 Product에만 User 참조
    // N:1 다대일 단방향
    @ManyToOne(fetch = FetchType.LAZY) // 회원 정보가 필요할때만 조회 할 수 있도록 Lazy로 변경
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Product(ProductRequestDto requestDto, User user) {
        this.title = requestDto.getTitle();
        this.image = requestDto.getImage();
        this.link = requestDto.getLink();
        this.lprice = requestDto.getLprice();
        this.user = user;
    }

    public void update(ProductMypriceRequestDto requestDto) {
        this.myprice = requestDto.getMyprice();
    }

    public void updateByItemDto(ItemDto itemDto) {
        this.lprice = itemDto.getLprice();
    }
}