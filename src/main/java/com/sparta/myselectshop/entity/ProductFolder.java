package com.sparta.myselectshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
// [연관관계 정리]
// 상품은 추가될 폴더의 정보가 필요합니다.
// -> 관심상품에 폴더를 등록합니다
// -> 상품은 여러개의 폴더를 등록 할 수 있습니다.
// => 상품:폴더 N:1관계
// 폴더에 관심 상품을 등록합니다.
// -> 하나의 폴더는 여러개 상품을 포함 할 수 있습니다.
// -> 폴더:상품 N:1관계
// ====> 따라서 상품:폴더 = N:M 다대다 관계입니다.
// @ManyToMany로 풀 수도 있지만, **중간 테이블 product_folder **을 만들어서 풀어보고자 한다.
// -> product ManyToOne -> Middle table <- ManyToOne folder

// [조회 여부에 따른 연관관계 방향정리]
// 관심 상품을 조회 할 때 포함된 폴더들의 정보가 필요하다
// -> 상품-폴더 양방향 관계
// 폴더에서는 상품 정보를 필요로하지 않는다.
// -> 폴더-상품은 관계 X

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "product_folder")
public class ProductFolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 중간테이블:상품테이블 = N:1 다대일
    // 필요할때 조회하도록 FetchType.LAZY
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // <- 양방향 설정으로 Product에서 해당 변수를 참조하고있음 -> 중간테이블에서 product의 필드가 나타남 => folder에서 product의 필드가 나타 = folder는 product의 정보를 조회 할 수 있음

    // 중간테이블:폴더테이블 = N:1 다대
    // 필요할때 조회하도록 FetchType.LAZY
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;

    public ProductFolder(Product product, Folder folder) {
        this.product = product;
        this.folder = folder;
    }
}