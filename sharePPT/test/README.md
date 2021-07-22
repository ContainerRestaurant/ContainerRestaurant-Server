# 테스트 코드

> 본 문서에서는 `용기낸 식당` 사이드 프로젝트 백엔드팀의 테스트 코드 재구성 및 커버리지 향상을 위해 테스트 코드의 목적 작성 방법 등을 전체적으로 훑고, 통일된 형식을 공유하기 위해 작성한 문서입니다.
>
> 기본 구성 환경
> 
> - Junit5
> - AssertJ

테스트 코드는 크게 `단위 테스트` 와 `통합 테스트`로 구분된다. 하지만 본 프로젝트에서는 `통합 테스트`를 대신해 `슬라이스 테스트`를 적용한다.

# 테스트 공통

## 네이밍과 `DisplayName`

테스트 대상, 목적 등을 쉽게 알 수 있도록 통일된 테스트 네이밍과 `DisplayName` 형식을 사용합니다.

### 클래스 네이밍과 `DisplayName`

클래스 네이밍 형식:

```
TargetClassName[OptionalInfo]Test
```

- `TestClassName` : 테스트 대상이 되는 클래스 이름
- `[OptionalInfo]` : 테스트 분류에 대한 정보
  - 단위 테스트는 `[OptionalInfo]`에 아무값도 넣지 않는다.
  - 슬라이스 테스트는 목적과 통합 환경에 따라 `[OptionalInfo]` 값을 지정한다.
    - `DataJpa` 통합하는 경우 `DataJpa` 사용
    - `REST Docs`를 생성하는 경우 `Documentation` 사용
    - `Web` 환경과 통합하는 경우 `Web` 사용
    - 그 외 목적과 통합 환경에 따라 추가될 수 있음
- `Test` : 모든 테스트 클래스는 `Test`를 접미사로 갖는다.

`DisplayName` 형식: 클래스 이름은 영어로, 그 외에는 해석한 형태로 사용한다. 접두사 `Test` 는 `단위 테스트` 혹은 `슬라이스 테스트` 중 적절한 것으로 해석한다.

`TargetClass` 클래스에 대한 테스트 클래스 네이밍 및 `DisplayName` 예시:

- `TargetClassTest` : "TargetClass 단위 테스트"
- `TargetClassDataJpaTest` : "TargetClass Data jpa 슬라이스 테스트"
- `TargetClassWebTest` : "TargetClass 웹 슬라이스 테스트"

### 메서드 네이밍과 `DisplayName`

메서드 네이밍 형식: 

```
targetMethodTest[_optional_info]*
```

- `targetMethod` : 테스트 대상이 되는 메서드 이름
- `[_optional_info]*` : 테스트 조건에 대한 정보 나열
  - 기본 동작 테스트는 `[_optional_info]` 에 아무값도 넣지 않는다.
  - 예외 테스트, Null 테스트 등 기대되지 않는 동작이나 한 메서드에 대해 여러 동작을 테스트하는 경우 상황에 맞게 나열한다.

`DisplayName` 형식 : `-Test` 까지는 메서드 이름을 포함해 해석한 형태로 사용한다. `[_optional_info]` 부분은 `-` 로 나누어 해석한다.

`targetMethod` 메서드에 대한 테스트 메서드 네이밍 및 `DisplayName` 예시

- `targetMethodTest` : "대상 메서드 테스트"
- `targetMethodTest_aIsNull` : "대상 메서드 테스트 - a 가 null 인 경우"
- `targetMethodTest_throwException` : "대상 메서드 테스트 - 예외가 발생하는 경우"
- `targetMethodTest_aIsNull_throwException` : "대상 메서드 테스트 - a 가 null 이고 예외가 발생하는 경우"

## 테스트 형식

### `given-when-then`

테스트를 작성할 때는 기본으로 `given-when-then` 형식을 따르도록 한다.

- `given`: 사전 조건 / 주어진 조건
- `when`: 검증(테스트)하려는 동작 / 행동 / 로직
- `then`: `when`이 완료된 후, 사후 조건 / 불변식 등의 유효성 검사

### `given-expect`

# TODO

# 단위 테스트

가장 작고 단순하며 빠른 테스트로, 의존성 없이 `하나의 클래스`(단위) 동작에 대해 테스트하는데 목적이 있다.

## 의존성 다루기

- 의존성은 가짜(`Mock`, 목) 인스턴스를 이용한다.
- 가짜 인스턴스는 스텁(`Stub`)을 이용해 동작을 가정한다.

## 테스트 대상

- 대상 클래스
  - 도메인 클래스 (Entity)
  - Controller, Service 계층 클래스
  - 그 외 통합되지 않은 환경에서 기대되는 로직이 존재하는 모든 클래스
- 대상 메서드
  - `Getter`, `Setter`, `equals` 등 표준 규약 메서드는 구현에 따라 선택적으로 테스트 대상이 될 수 있다.
  - 표준 규약 메서드 외 모든 `public` 메서드는 테스트 코드가 존재하기를 권장한다.


# 슬라이스 테스트