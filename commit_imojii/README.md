# 커밋 단위 컨벤션

하나의 커밋은 적어도 하나 이상의 목적성을 갖도록 생성하고, 비슷 or 동일한 목적의 여러 커밋은 squash 기능을 이용해 하나의 커밋으로 병합하여 커밋의 가독성을 향상시켜 봅시다!

커밋의 목적성이 다양해 지더라도 하나하나의 목적이 명확한 것이 좋을것 같아, 제가 기준으로 삼았던 커밋에 사용한 이모지의 목적 및 예시를 공유해봅니다.
(꼭 지켜질 필요는 없지만, 불필요한 커밋은 없는지, 목적이 중복되는 커밋은 없는지, 하나의 커밋에 너무 많은 목적이 있지는 않은지 생각해보는건 좋을것 같습니다!)

> 기본 템플릿은 https://gitmoji.dev/ 사이트를 참조했으며, 해당 사이트의 내용과 상이한 부분이 있을 수도 있습니다!

> 저도 처음 해봤던거라 지금까지의 커밋중 아래 내용과 상이한 부분이 있을 수도 있습니다! 이런부분은 앞으로 코드리뷰로 말씀해주세요!

> 아래 명세되지 않은 이모지는 혹시 사용하게되면 추가해주세요!

> IntelliJ 사용시, Gitmoji Plus: CommitButton 플러그인을 설치하면 IntelliJ 에서 커밋 작성시 손쉽게 이모지를 검색할 수 있습니다.

:label:(label) - 도메인 모델 추가 및 수정
- 유저 도메인 추가 / 유저 도메인에 유효성 검증 로직 추가

:sparkles:(sparkles) - 새로운 "기능" 추가
- 사용자 조회 기능 구현

:art:(art) - 코드 구조 개선
- 링크를 반환하는 코드 로직 개선

:memo:(memo) - Document 관련 작업 및 코드의 주석 추가 작업
- REST Docs 테스트 코드 추가로 도큐먼트 스니펫 생성

:white_check_mark:(white_check_mark) - 테스트 추가 및 수정
- UserRepository 테스트 코드 추가

:heavy_plus_sign:(heavy_plus_sign) - build.gradle에 의존성 추가
- XXX 에 대한 의존성 추가

:passport_control:(passport_control) - 보안 관련 요구사항 구현
- 로그인 여부에 따른 index 응답 수정

:wrench:(wrench) - 설정파일(.properties, .yml) 수정 및 추가
- 프로필 설정 추가

:green_heart:(green_heart) - CI 관련 에러/버그 수정
- (CI 실패 수정) 데이터 소스 의존성 추가

:construction_worker:(construction_worker) - CI 관련 스크립트 추가/수정
- gradle.yml 생성

:tada:(tada) - 프로젝트 초기화

:recycle:(recycle) - 코드 리팩토링