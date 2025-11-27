# 🐻 MediBear (Spring Boot)

## 팀원 구성
| 이름 | 역할 | Github |
|------|------|---------|
| 🧭 김정규 | FullStack | [@gyu0918](https://github.com/gyu0918) |
| 🌟 유신안 | FullStack | [@shinanyu](https://github.com/shinanyu) |
| 🏗️ 변상용 | FullStack | [@Hayden721](https://github.com/Hayden721) |
| 💫 이승권 | FullStack | [@seoungkwon](https://github.com/seoungkwon) |
| 🎯 임예지 | FullStack | [@Bluemoon105](https://github.com/Bluemoon105) |

## 프로젝트 소개

- 헬스케어와 멘탈케어를 통합한 AI 코팅 웹서비스 구현
- 사용자 개인 맞춤 리포트 및 히스토리 시각화 대시보드 제공
- 프로젝트 기간: 2025.11.03 ~ 2025.11.28

## 기술 스택

### 백엔드
![SpringBoot](https://img.shields.io/badge/springboot-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)
![SpringSecurity](https://img.shields.io/badge/springsecurity-%236DB33F.svg?style=for-the-badge&logo=springsecurity&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)

### DB
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)

### 협업 툴
![Jira](https://img.shields.io/badge/jira-%230A0FFF.svg?style=for-the-badge&logo=jira&logoColor=white)
![Slack](https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white)

## 팀원별 구현 기능 상세

### 김정규

### 유신안

### 변상용

### 이상권

### 임예지
### 1. 사용자 관리
- 회원가입 / 사용자 정보 조회 API 개발
- 비밀번호 암호화(BCrypt), 이메일 중복 검사 구현
- 요청 데이터 Validation 적용

### 2. 수면 데이터 관리
- 수면 입력 저장 API 개발
- 일간/주간 수면 리포트 조회 기능 구현
- JPA 기반 Member–SleepData 연관관계 매핑
- FastAPI LLM 분석 서버와 연동하여 분석 결과 반환

### 3. JPA 및 DB 설계
- SleepData 엔티티 설계
- PostgreSQL 스키마 구성 및 테이블 매핑(daily_activities_tb 테이블)
- 생성 시각 자동 기록(Auditing)