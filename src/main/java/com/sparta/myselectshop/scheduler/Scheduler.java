package com.sparta.myselectshop.scheduler;

import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.naver.service.NaverApiService;
import com.sparta.myselectshop.repository.ProductRepository;
import com.sparta.myselectshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "Scheduler")
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final NaverApiService naverApiService;
    private final ProductService productService;
    private final ProductRepository productRepository;


    // cron : 자동수행과 관련된 특정 시간들과 관련된 명령어 중 하나
    // 공식문서에서 정확한 표현들 알 수 있음.
    // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/support/CronExpression.html
    // 간략하게는 초, 분, 시, 일, 월, 주 순서

    //@Scheduled(cron = "0 0 1 * * *") // 매일 새벽 1시로 설정할 경우.
    @Scheduled(cron = "*/10 * * * * *") // 테스트용으로 짧게 우선 작동하는지 보고 실제 프로젝트는 위처럼, -> 실행시켜보면 자동 업데이트가 주기적으로 작동함.
    public void updatePrice() throws InterruptedException {
        log.info("가격 업데이트 실행");
        List<Product> productList = productRepository.findAll(); // 스케줄러에 따라 자동 검색해야 할 대상 모두 검색
        for (Product product : productList) { // 하나씩 검색을 함
            // 1초에 한 상품 씩 조회합니다 (NAVER 제한)
            TimeUnit.SECONDS.sleep(1);

            // i 번째 관심 상품의 제목으로 검색을 실행합니다.
            String title = product.getTitle(); // 우선, 제목 가져오기
            List<ItemDto> itemDtoList = naverApiService.searchItems(title); // 네이버 검색 구현해둔곳으로 제목을 전달해서 검색시킴

            if (itemDtoList.size() > 0) {
                ItemDto itemDto = itemDtoList.get(0);
                // i 번째 관심 상품 정보를 업데이트합니다.
                Long id = product.getId();
                try {
                    productService.updateBySearch(id, itemDto); // 자동 검색 시킨것을 통해 업데이트문을 실행시킴
                } catch (Exception e) {
                    log.error(id + " : " + e.getMessage());
                }
            }
        }
    }

}