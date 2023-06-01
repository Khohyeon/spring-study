## 비동기 메서드 만들기
이 가이드는 GitHub에 대한 비동기 쿼리를 생성하는 과정을 안내합니다. 초점은 서비스를 확장할 때 자주 사용되는 기능인 비동기 부분에 있습니다.

### 무엇을 만들 것인가
GitHub 사용자 정보를 쿼리하고 GitHub의 API를 통해 데이터를 검색하는 조회 서비스를 구축합니다.
서비스 확장에 대한 한 가지 접근 방식은 백그라운드에서 비용이 많이 드는 작업을 실행하고 Java의 CompletableFuture 인터페이스를 사용하여 결과를 기다리는 것입니다.
Java의 CompletableFuture는 일반 Future에서 진화한 것입니다. 여러 비동기 작업을 쉽게 파이프라인하고 단일 비동기 계산으로 병합할 수 있습니다.

### 필요한 것
* 약 15분
* 선호하는 텍스트 편집기 또는 IDE
* 자바 17 이상
* Gradle 7.5+ 또는 Maven 3.5+
* 코드를 IDE로 바로 가져올 수도 있습니다.

### 이 가이드를 완료하는 방법
대부분의 Spring 시작하기 가이드와 마찬가지로 처음부터 시작하여 각 단계를 완료하거나 이미 익숙한 기본 설정 단계를 건너뛸 수 있습니다. 어느 쪽이든 작업 코드로 끝납니다.

처음부터 시작하려면 Spring Initializr로 시작하기로 이동하세요.

기본 사항을 건너뛰려면 다음을 수행하십시오.

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다. git clone https://github.com/spring-guides/gs-async-method.git

* gs-async-method/initial로 cd

* GitHub 사용자의 표현 만들기로 이동하세요.

완료하면 gs-async-method/complete의 코드와 비교하여 결과를 확인할 수 있습니다.

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

https://start.spring.io로 이동합니다. 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

종속성을 클릭하고 Spring 웹을 선택합니다.

생성을 클릭합니다.

선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io로 이동합니다. 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

3. 종속성을 클릭하고 Spring 웹을 선택합니다.

4. 생성을 클릭합니다.

5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### GitHub 사용자의 표현 만들기
GitHub 조회 서비스를 생성하기 전에 GitHub의 API를 통해 검색할 데이터에 대한 표현을 정의해야 합니다.

사용자 표시를 모델링하려면 자원 표시 클래스를 작성하십시오. 
이렇게 하려면 다음 예제(src/main/java/com/example/asyncmethod/User.java에서)에 나와 있는 것처럼 필드, 생성자 및 접근자와 함께 일반 이전 Java 객체를 제공하십시오.

```java
@JsonIgnoreProperties(ignoreUnknown=true)
public class User {

	private String name;
	private String blog;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBlog() {
		return blog;
	}

	public void setBlog(String blog) {
		this.blog = blog;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", blog=" + blog + "]";
	}

}
```

Spring은 Jackson JSON 라이브러리를 사용하여 GitHub의 JSON 응답을 User 객체로 변환합니다. 
@JsonIgnoreProperties 주석은 클래스에 나열되지 않은 속성을 무시하도록 Spring에 지시합니다. 
이렇게 하면 REST 호출을 쉽게 만들고 도메인 개체를 생성할 수 있습니다.

이 가이드에서는 데모 목적으로 이름과 블로그 URL만 가져옵니다.


### GitHub 조회 서비스 만들기
다음으로 GitHub에 쿼리하여 사용자 정보를 찾는 서비스를 만들어야 합니다. 
다음 목록(src/main/java/com/example/asyncmethod/GitHubLookupService.java)은 이를 수행하는 방법을 보여줍니다.

```java
@Service
public class GitHubLookupService {

	private static final Logger logger = LoggerFactory.getLogger(GitHubLookupService.class);

	private final RestTemplate restTemplate;

	public GitHubLookupService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@Async
	public CompletableFuture<User> findUser(String user) throws InterruptedException {
		logger.info("Looking up " + user);
		String url = String.format("https://api.github.com/users/%s", user);
		User results = restTemplate.getForObject(url, User.class);
		// Artificial delay of 1s for demonstration purposes
		Thread.sleep(1000L);
		return CompletableFuture.completedFuture(results);
	}

}
```

itHubLookupService 클래스는 Spring의 RestTemplate을 사용하여 원격 REST 포인트(api.github.com/users/)를 호출한 다음 응답을 User 객체로 변환합니다. 
Spring Boot는 자동 구성 비트(즉, MessageConverter)로 기본값을 사용자 지정하는 RestTemplateBuilder를 자동으로 제공합니다.

findUser 메소드는 별도의 스레드에서 실행되어야 함을 나타내는 Spring의 @Async 주석으로 표시됩니다. 메서드의 반환 유형은 모든 비동기 서비스의 요구 사항인 User 대신 CompletableFuture<User>입니다. 
이 코드는 completedFuture 메서드를 사용하여 GitHub 쿼리의 결과로 이미 완료된 CompletableFuture 인스턴스를 반환합니다.

```
GitHubLookupService 클래스의 로컬 인스턴스를 만들면 findUser 메서드가 비동기적으로 실행되지 않습니다. @Configuration 클래스 내에서 생성되거나 @ComponentScan에 의해 선택되어야 합니다.
```

GitHub API의 타이밍은 다를 수 있습니다. 이 가이드의 뒷부분에서 이점을 보여주기 위해 이 서비스에 1초의 추가 지연이 추가되었습니다.

### 응용 프로그램을 실행 가능하게 만들기
샘플을 실행하려면 실행 가능한 jar를 만들 수 있습니다. Spring의 @Async 어노테이션은 웹 애플리케이션에서 작동하지만 이점을 확인하기 위해 웹 컨테이너를 설정할 필요는 없습니다.
다음 목록(src/main/java/com/example/asyncmethod/AsyncMethodApplication.java)은 이를 수행하는 방법을 보여줍니다.

```java
@SpringBootApplication
@EnableAsync
public class AsyncMethodApplication {

  public static void main(String[] args) {
    // close the application context to shut down the custom ExecutorService
    SpringApplication.run(AsyncMethodApplication.class, args).close();
  }

  @Bean
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(2);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("GithubLookup-");
    executor.initialize();
    return executor;
  }


}
```

```
Spring Initializr는 AsyncMethodApplication 클래스를 생성했습니다. Spring Initializr에서 다운로드한 zip 파일(src/main/java/com/example/asyncmethod/AsyncMethodApplication.java)에서 찾을 수 있습니다. 해당 클래스를 프로젝트에 복사한 다음 수정하거나 이전 목록에서 클래스를 복사할 수 있습니다.
```

@EnableAsync 주석은 백그라운드 스레드 풀에서 @Async 메서드를 실행하는 Spring의 기능을 켭니다. 
이 클래스는 또한 새 bean을 정의하여 Executor를 사용자 정의합니다. 여기에서 이 메서드는 Spring이 검색하는 특정 메서드 이름이므로 taskExecutor로 명명됩니다. 우리의 경우 동시 스레드 수를 2개로 제한하고 대기열 크기를 500개로 제한하려고 합니다. 조정할 수 있는 항목이 더 많이 있습니다. 
Executor 빈을 정의하지 않으면 Spring은 SimpleAsyncTaskExecutor를 생성하여 사용합니다.

GitHubLookupService를 주입하고 해당 서비스를 세 번 호출하여 메서드가 비동기적으로 실행됨을 보여주는 CommandLineRunner도 있습니다.

애플리케이션을 실행하려면 클래스도 필요합니다. src/main/java/com/example/asyncmethod/AppRunner.java에서 찾을 수 있습니다. 
다음 목록은 해당 클래스를 보여줍니다.

```java
@Component
public class AppRunner implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

  private final GitHubLookupService gitHubLookupService;

  public AppRunner(GitHubLookupService gitHubLookupService) {
    this.gitHubLookupService = gitHubLookupService;
  }

  @Override
  public void run(String... args) throws Exception {
    // Start the clock
    long start = System.currentTimeMillis();

    // Kick of multiple, asynchronous lookups
    CompletableFuture<User> page1 = gitHubLookupService.findUser("PivotalSoftware");
    CompletableFuture<User> page2 = gitHubLookupService.findUser("CloudFoundry");
    CompletableFuture<User> page3 = gitHubLookupService.findUser("Spring-Projects");

    // Wait until they are all done
    CompletableFuture.allOf(page1,page2,page3).join();

    // Print results, including elapsed time
    logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
    logger.info("--> " + page1.get());
    logger.info("--> " + page2.get());
    logger.info("--> " + page3.get());

  }

}
```

### 실행 가능한 JAR 빌드
Gradle 또는 Maven을 사용하여 명령줄에서 애플리케이션을 실행할 수 있습니다. 필요한 모든 종속성, 클래스 및 리소스를 포함하는 단일 실행 가능 JAR 파일을 빌드하고 실행할 수도 있습니다. 
실행 가능한 jar을 빌드하면 개발 수명 주기 전체, 다양한 환경 등에 서비스를 애플리케이션으로 쉽게 제공, 버전 지정 및 배포할 수 있습니다.

#### Gradle을 사용하는 경우 다음 명령을 실행하여 애플리케이션을 실행합니다.
./gradlew bootRun ./gradlew build 또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```
java -jar build/libs/async-method-0.1.0.jar
```


애플리케이션은 GitHub에 대한 각 쿼리를 보여주는 로깅 출력을 보여줍니다. 
allOf 팩토리 메소드의 도움으로 CompletableFuture 객체의 배열을 생성합니다. 
조인 메서드를 호출하면 모든 CompletableFuture 개체가 완료될 때까지 기다릴 수 있습니다.

다음 목록은 이 샘플 애플리케이션의 일반적인 출력을 보여줍니다.

```
2023-06-01T21:03:56.399+09:00  INFO 17892 --- [ GithubLookup-1] c.e.asyncmethod.GitHubLookupService      : Looking up PivotalSoftware
2023-06-01T21:03:56.399+09:00  INFO 17892 --- [ GithubLookup-2] c.e.asyncmethod.GitHubLookupService      : Looking up CloudFoundry
2023-06-01T21:03:57.977+09:00  INFO 17892 --- [ GithubLookup-1] c.e.asyncmethod.GitHubLookupService      : Looking up Spring-Projects
2023-06-01T21:03:59.221+09:00  INFO 17892 --- [           main] com.example.asyncmethod.AppRunner        : Elapsed time: 2827
2023-06-01T21:03:59.222+09:00  INFO 17892 --- [           main] com.example.asyncmethod.AppRunner        : --> User [name=Pivotal Software, Inc., blog=http://pivotal.io]
2023-06-01T21:03:59.222+09:00  INFO 17892 --- [           main] com.example.asyncmethod.AppRunner        : --> User [name=Cloud Foundry, blog=https://www.cloudfoundry.org/]
2023-06-01T21:03:59.222+09:00  INFO 17892 --- [           main] com.example.asyncmethod.AppRunner        : --> User [name=Spring, blog=https://spring.io/projects]
2023-06-01T21:03:59.231+09:00  INFO 17892 --- [           main] o.apache.catalina.core.StandardService   : Stopping service [Tomcat]
```