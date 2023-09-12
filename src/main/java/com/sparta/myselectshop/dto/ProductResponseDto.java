package com.sparta.myselectshop.dto;

import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.ProductFolder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String title;
    private String link;
    private String image;
    private int lprice;
    private int myprice;
    //양방향으로 걸어뒀기 때문에
    // 여기서 product의 정보만이 아니라 추가적으로 folder의 정보도 추가해서 보내줘야한다.
    //ex)관심상품 하나에 폴더가 여러개 등록 될 수 있다. 해쉬태그걸린것처럼. 그래서 그 정보를 보내줘야 한다.
    private List<FolderResponseDto> productFolderList = new ArrayList<>();


    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.link = product.getLink();
        this.image = product.getImage();
        this.lprice = product.getLprice();
        this.myprice = product.getMyprice();

        for (ProductFolder productFolder : product.getProductFolderList()) {//product폴더에 들어있는 폴더 정보를 위 변수에 넣어줘야한다.
            productFolderList.add(new FolderResponseDto(productFolder.getFolder())); //product에 우리가 연결한 productFolderList 를 가지고 온다. 거기에 folder정보가 있다.(양방향해둬서 가져올수있지) 그것을 FolderResponseDto로 변환하면서, 위 List에 그 정보를 넣는다.
        }
    }
    //그런데 위 ProductFolder타입의 productFolder로 정보를 가져오는 것을 추가하면서 생각할 것이 있다.
    //ProductFolder타입(중간테이블)에서 product와 folder를 관계 설정에서
    //@ManyToOne(fetch = FetchType.LAZY)으로 설정했다. 이것은 @Transactional 환경을 요구한다.
    //그런데 ProductResponseDto를 사용하는 ProductService 레이어의 getProducts구현부로 가보면,

}