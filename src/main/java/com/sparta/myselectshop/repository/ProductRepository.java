package com.sparta.myselectshop.repository;

import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByUser(User user, Pageable pageable);

    Page<Product> findAllByUserAndProductFolderList_FolderId(User user, Long folderId, Pageable pageable);
    //우리가 원하는것은 로그인한 유저의 상품의 리스트인데 어떤것? 특정 폴더에 소속된 것을 원한다.
    // ->
    // DB Console에서 테스트한것
    /*# Query Method 한번 SQL로 해보기
        select
            *
        from
            product p
        where
            p.user_id = 1;

        select
            p.id,
            p.title,
            pf.product_id as product_id,
            pf.folder_id as folder_id
        from
            product p
                left join product_folder pf
                    on p.id = pf.product_id
        where
            p.user_id = 1
        and
            pf.folder_id = 3
        order by
            p.id desc
        # limit 0, 10 # 페이징처리 부분 추가 -> 0부터 10개까지,
        limit 10,10 # 페이징처리 부분 -> 10부터 10개 (이는 곧 10~20번까지라는것)
        ;
        # 이처럼 페이징 처리는 limit을 조절해주고있는것이고, Spring JPA가 이것을 구현해준다는것!*/
}
