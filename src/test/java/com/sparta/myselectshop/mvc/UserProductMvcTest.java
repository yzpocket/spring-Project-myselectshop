package com.sparta.myselectshop.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.myselectshop.config.WebSecurityConfig;
import com.sparta.myselectshop.controller.ProductController;
import com.sparta.myselectshop.controller.UserController;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.entity.UserRoleEnum;
import com.sparta.myselectshop.security.UserDetailsImpl;
import com.sparta.myselectshop.service.FolderService;
import com.sparta.myselectshop.service.KakaoService;
import com.sparta.myselectshop.service.ProductService;
import com.sparta.myselectshop.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest( //이것을 통해서 Controller를 테스트 할 수 있음
        controllers = {UserController.class, ProductController.class}, //테스트 할 컨트롤러를 지정할수있다.
        excludeFilters = { //제외할것 지정, WebSecurityConfig 설정을 제외하고자 함. 테스트에 방해되서
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = WebSecurityConfig.class
                )
        }
)
class UserProductMvcTest {
    private MockMvc mvc; //가짜mvc객체

    private Principal mockPrincipal; //가짜 인증도 선언

    @Autowired
    private WebApplicationContext context; //주입

    @Autowired
    private ObjectMapper objectMapper; //주입

    @MockBean //빈을 등록하고 주입이 필요한데, 가짜 빈을통해 그 기능을 대체한다 생각하자.
    UserService userService;

    @MockBean
    KakaoService kakaoService;

    @MockBean
    ProductService productService;

    @MockBean
    FolderService folderService;

    @BeforeEach //mockmvc에 우리가 만든 가짜 필터를 넣어준다. .mvc.MockSpringSecurityFilter 이것이 가짜 인증으로 Security 작동을 우회하게함
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    private void mockUserSetup() {
        // Mock 테스트 유져 생성 - 테스트할 가짜 유저객체를 직접 만든다
        String username = "sollertia4351";
        String password = "robbie1234";
        String email = "sollertia@sparta.com";
        UserRoleEnum role = UserRoleEnum.USER;
        User testUser = new User(username, password, email, role);
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
    }//여기까지 모두 가짜 유저정보를 만들어서 Principal 객체를 만들어준다.

    @Test
    @DisplayName("로그인 Page API 테스트")
    void test1() throws Exception {
        // when - then
        mvc.perform(get("/api/user/login-page")) //API 메서드 방식에 위 가짜 mvc객체를 넣어서 테스트
                .andExpect(status().isOk()) // status코드 무엇인지 예측
                .andExpect(view().name("login")) // 반환되는 페이지 이름이 어딘지 예측
                .andDo(print()); // HttpSevletResponse 통신 결과들 모두 출력(status, error여부, 헤더, 바디 등등 다나옴)
    }

    @Test
    @DisplayName("회원 가입 요청 처리")
    void test2() throws Exception {
        // given - 필요한 정보들 다 넣어주고 (회원가입 입력 폼에 입력한다 생각)
        MultiValueMap<String, String> signupRequestForm = new LinkedMultiValueMap<>();
        signupRequestForm.add("username", "sollertia4351");
        signupRequestForm.add("password", "robbie1234");
        signupRequestForm.add("email", "sollertia@sparta.com");
        signupRequestForm.add("admin", "false");

        // when - then
        mvc.perform(post("/api/user/signup") //API 메서드 방식에 위 가짜 mvc객체를 넣어서 테스트
                        .params(signupRequestForm)
                )
                .andExpect(status().is3xxRedirection())// redirect 무엇인지 예측
                .andExpect(view().name("redirect:/api/user/login-page"))// 반환되는 페이지 이름이 어딘지 예측
                .andDo(print());// HttpSevletResponse 통신 결과들 모두 출력(status, error여부, 헤더, 바디 등등 다나옴)
    }

    @Test
    @DisplayName("신규 관심상품 등록")
    void test3() throws Exception {
        // given - 필요한 정보들 다 넣어주고 (상품 검색 등록 폼 사용한다 생각)
        this.mockUserSetup();
        String title = "Apple <b>아이폰</b> 14 프로 256GB [자급제]";
        String imageUrl = "https://shopping-phinf.pstatic.net/main_3456175/34561756621.20220929142551.jpg";
        String linkUrl = "https://search.shopping.naver.com/gate.nhn?id=34561756621";
        int lPrice = 959000;
        ProductRequestDto requestDto = new ProductRequestDto(
                title,
                imageUrl,
                linkUrl,
                lPrice
        );

        String postInfo = objectMapper.writeValueAsString(requestDto);
        // 네이버 API가 String으로 쏴주는것처럼 동일하게 구성,

        // when - then
        mvc.perform(post("/api/products") //API 메서드 방식에 NAVER가 JSON String을 넘기는 것처럼 실행
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk()) // status코드 무엇인지 예측
                .andDo(print());// HttpSevletResponse 통신 결과들 모두 출력(status, error여부, 헤더, 바디 등등 다나옴)
    }

    //결론
    //위같은 방식으로 테스트한다는 것이지 무조건 정해진 방법이 아니다.
    //테스트 할 부분과 목적에 따라서 위 같이 가짜객체Mock을 어떤것을 만들지, 어떤 로직 부위를 테스트 할지를 생각해야된다.
    //위 방법들은 예시일뿐 골라서 또는 더 필요한 부분이 필요하면 더 공부해서 다른 기능도 사용해본다는 느낌으로 접근하자.
    //필요에 따라서 자연스럽게 추가적인 것들이 무엇인지 찾아내고 공부하는 방법으로 접근해야 한다.
}