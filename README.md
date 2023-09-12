# Spring Boot Mini Project!
관심 상품을 검색, 등록하고 가격을 추적하는 나의 관심 상품 관리 웹 서비스

## 🖥️ 스터디 저장소 소개
`Spring Boot`를 활용한 회원가입 및 로그인(카카오), 상품 검색(네이버)과 등록 및 조회 등 기능을 연습하기 위한 미니 프로젝트입니다.

## 👋🏻 Contact
- **Email** : citefred@yzpocket.com
- **Blog** : https://www.citefred.com

## 🕰️ 학습 기간
* 23.09.10 ~ 23.09.12

## ⚙️ 개발 환경
- **MainLanguage** : `Java - JDK 17`
- **IDE** : `IntelliJ IDEA Ultimate`
- **Framework** : `SpringBoot`
- **Database** : `MySQL`
- **SERVER** : `Spring Inner Server(TOMCAT)`
- **TEST** : `POSTMAN API Request`

## 📌 주요 기능
### 학습한 기능
* 회원 부분 `Spring Security`, `JWT` 방식
    - 회원가입
    - 카카오 로그인을 통한 회원가입
    - 로그인
    - 카카오 로그인

* 상품 관련 기능 `JPA`를 통한 `MySQL DB`관리, `Naver Open API` 검색 기능
    - 관심 상품 등록
    - 관심 상품의 희망 최저가 업데이트
    - 관심 상품 조회하기
    - 사용자 카테고리인 폴더 추가
        - 상품을 폴더에 등록하기
        - 폴더별 폴더에 등록된 상품 조회
    - 조회 기능 전체 페이징처리
    - 조회 기능 전체 정렬 기능(가격순,이름순,최저가순) 오름, 내림차순

## ⚠️ 주의
#### 추적 예외
* DB 접속 정보 및 Naver Developers의 Client ID 및 Secret Key는 추적이 제외되어 있습니다.
* `application.properties`와 `application-API-KEY.properties` src/main/resources/ 경로에 생성해야 합니다.
```
# application.properties의 내용
spring.datasource.url=jdbc:mysql://localhost:3306/orderapp
spring.datasource.username={MySQL 계정}
spring.datasource.password={비밀번호}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update

spring.jpa.properties.hibernate.show_sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true

# API-KEY 포함
spring.profiles.include=API-KEY
```
```
# application-API-KEY.properties의 내용
naver.client.id={CLIENT_ID}
naver.client.secret={SECRET_KEY}
```
