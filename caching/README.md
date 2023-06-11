## Spring으로 데이터 캐싱
이 가이드는 Spring 관리 빈에서 캐싱을 활성화하는 과정을 안내합니다.

### 무엇을 만들 것인가
간단한 BookRepository 에서 캐싱을 활성화하는 애플리케이션을 빌드합니다.

### 필요한 것
* 약 15분

* 선호하는 텍스트 편집기 또는 IDE

* 자바 17 이상

* Gradle 7.5+ 또는 Maven 3.5+

* 코드를 IDE로 바로 가져올 수도 있습니다.

### 이 가이드를 완료하는 방법
대부분의 Spring 시작하기 가이드 와 마찬가지로 처음부터 시작하여 각 단계를 완료하거나 이미 익숙한 기본 설정 단계를 건너뛸 수 있습니다. 어느 쪽이든 작업 코드로 끝납니다.

처음부터 시작 하려면 Spring Initializr로 시작하기 로 이동하십시오 .

기본 사항을 건너뛰 려면 다음을 수행하십시오.

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다. git clone https://github.com/spring-guides/gs-caching.git

* gs-caching/initial로 cd

* 책 모델 만들기로 이동하십시오.

완료하면 gs-caching/complete의 코드와 비교하여 결과를 확인할 수 있습니다.

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io로 이동합니다. 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

3. 종속성을 클릭하고 Spring 캐시 추상화를 선택하십시오.

4. 생성을 클릭합니다.

5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### Book 모델 만들기

```java
public class Book {

  private String isbn;
  private String title;

  public Book(String isbn, String title) {
    this.isbn = isbn;
    this.title = title;
  }

  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return "Book{" + "isbn='" + isbn + '\'' + ", title='" + title + '\'' + '}';
  }

}
```

### BookRepository 만들기

```java
public interface BookRepository {

  Book getByIsbn(String isbn);

}
```

Spring Data를 사용하여 광범위한 SQL 또는 NoSQL 저장소에 대한 리포지토리 구현을 제공할 수 있습니다. 그러나 이 가이드의 목적상 약간의 대기 시간(네트워크 서비스, 느린 지연 또는 기타 문제)을 시뮬레이트하는 순진한 구현을 사용합니다.

### SimpleBookRepository 만들기

```java
@Component
public class SimpleBookRepository implements BookRepository {

  @Override
  public Book getByIsbn(String isbn) {
    simulateSlowService();
    return new Book(isbn, "Some book");
  }

  // Don't do this at home
  private void simulateSlowService() {
    try {
      long time = 3000L;
      Thread.sleep(time);
    } catch (InterruptedException e) {
      throw new IllegalStateException(e);
    }
  }

}
```

SimulateSlowService는 의도적으로 각 getByIsbn 호출에 3초 지연을 삽입합니다. 나중에 캐싱을 사용하여 이 예제의 속도를 높일 것입니다.

BookRepository를 주입하고 다른 인수로 여러 번 호출하는 CommandLineRunner가 필요합니다.

```java
@Component
public class AppRunner implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

  private final BookRepository bookRepository;

  public AppRunner(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    logger.info(".... Fetching books");
    logger.info("isbn-1234 -->" + bookRepository.getByIsbn("isbn-1234"));
    logger.info("isbn-4567 -->" + bookRepository.getByIsbn("isbn-4567"));
    logger.info("isbn-1234 -->" + bookRepository.getByIsbn("isbn-1234"));
    logger.info("isbn-4567 -->" + bookRepository.getByIsbn("isbn-4567"));
    logger.info("isbn-1234 -->" + bookRepository.getByIsbn("isbn-1234"));
    logger.info("isbn-1234 -->" + bookRepository.getByIsbn("isbn-1234"));
  }

}
```
이 시점에서 응용 프로그램을 실행하려고 하면 정확히 동일한 책을 여러 번 검색하더라도 매우 느리다는 것을 알 수 있습니다. 다음 샘플 출력은 (의도적으로 끔찍한) 코드가 생성한 3초 지연을 보여줍니다.

```
.... Fetching books
isbn-1234 -->Book{isbn='isbn-1234', title='Some book'}
isbn-1234 -->Book{isbn='isbn-1234', title='Some book'}
isbn-1234 -->Book{isbn='isbn-1234', title='Some book'}
```
캐싱을 활성화하여 상황을 개선할 수 있습니다.

### 캐싱 활성화
이제 책이 책 캐시 내에 캐시되도록 SimpleBookRepository에서 캐싱을 활성화할 수 있습니다.

@Cachable("books) 를 추가하면서 캐싱을 활성화 할 수 있습니다. 이렇게 하면 getByIsbn 메소드가 호출될 때마다 캐시가 검사됩니다. 캐시에 책이 없으면 메소드가 호출되고 결과가 캐시에 저장됩니다. 그런 다음 캐시에 책이 있으면 메소드가 호출되지 않고 캐시에서 결과가 반환됩니다.

#### 실행 가능한 JAR 빌드
Gradle 또는 Maven을 사용하여 명령줄에서 애플리케이션을 실행할 수 있습니다. 필요한 모든 종속성, 클래스 및 리소스를 포함하는 단일 실행 가능 JAR 파일을 빌드하고 실행할 수도 있습니다. 실행 가능한 jar을 빌드하면 개발 수명 주기 전체, 다양한 환경 등에 서비스를 애플리케이션으로 쉽게 제공, 버전 지정 및 배포할 수 있습니다.

Gradle을 사용하는 경우 ./gradlew bootRun을 사용하여 애플리케이션을 실행할 수 있습니다. 또는 다음과 같이 ./gradlew build를 사용하여 JAR 파일을 빌드한 다음 JAR 파일을 실행할 수 있습니다.
```
java -jar build/libs/caching-0.0.1-SNAPSHOT.jar
```

Maven을 사용하는 경우 ./mvnw spring-boot:run을 사용하여 애플리케이션을 실행할 수 있습니다. 또는 다음과 같이 ./mvnw clean 패키지로 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다.
```
java -jar target/caching-0.0.1-SNAPSHOT.jar
```

### 애플리케이션 테스트
이제 캐싱이 활성화되었으므로 애플리케이션을 다시 실행하고 동일한 ISBN을 사용하거나 사용하지 않고 추가 호출을 추가하여 차이를 확인할 수 있습니다. 큰 차이를 만들어야 합니다. 다음 목록은 캐싱이 활성화된 출력을 보여줍니다.

```
.... Fetching books
isbn-1234 -->Book{isbn='isbn-1234', title='Some book'}
isbn-4567 -->Book{isbn='isbn-4567', title='Some book'}
isbn-1234 -->Book{isbn='isbn-1234', title='Some book'}
isbn-4567 -->Book{isbn='isbn-4567', title='Some book'}
```

이전 샘플 출력에서 책의 첫 번째 검색에는 여전히 3초가 걸립니다. 그러나 동일한 책에 대한 두 번째 및 후속 시간은 캐시가 제 역할을 하고 있음을 보여주면서 훨씬 더 빠릅니다.
