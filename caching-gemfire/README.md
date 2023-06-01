## 핵심 GemFire로 데이터 캐싱
이 가이드는 Apache Geode의 데이터 관리 시스템을 사용하여 애플리케이션 코드의 특정 호출을 캐시하는 과정을 안내합니다.

```
Apache Geode 개념 및 Apache Geode의 데이터 액세스에 대한 일반적인 지식은 Apache Geode로 데이터 액세스 가이드를 읽어보세요.
```

### 무엇을 만들 것인가
1. CloudFoundry 호스팅 견적 서비스에서 견적을 요청하고 Apache Geode에 캐시하는 서비스를 구축합니다.

2. 그런 다음 동일한 견적을 다시 가져오면 Apache Geode가 지원하는 Spring의 캐시 추상화가 동일한 요청에 대해 결과를 캐시하는 데 사용되므로 Quote 서비스에 대한 비용이 많이 드는 호출이 제거됨을 알 수 있습니다.

3. 견적 서비스는 다음 위치에 있습니다.

```
https://spring-gemfire-quote.cfapps.io/quote?item=ITEM1
```

견적 서비스에는 다음 API가 있습니다…
```
GET /api         - get all quotes
GET /api/random  - get random quote
GET /api/{id}    - get specific quote
```

### 필요한 것
* 약 15분
* 선호하는 텍스트 편집기 또는 IDE
* 자바 1.8 이상
* Gradle 7.5+ 또는 Maven 3.5+
* 코드를 IDE로 바로 가져올 수도 있습니다.

### 이 가이드를 완료하는 방법
대부분의 Spring 시작하기 가이드와 마찬가지로 처음부터 시작하여 각 단계를 완료하거나 이미 익숙한 기본 설정 단계를 건너뛸 수 있습니다. 어느 쪽이든 작업 코드로 끝납니다.

처음부터 시작하려면 Spring Initializr로 시작하기로 이동하세요.

기본 사항을 건너뛰려면 다음을 수행하십시오.

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다. git clone https://github.com/spring-guides/gs-caching-gemfire.git

* gs-caching-gemfire/initial로 cd

* 데이터 가져오기를 위한 바인딩 가능한 개체 만들기로 이동합니다.

완료하면 gs-caching-gemfire/complete의 코드와 비교하여 결과를 확인할 수 있습니다.

### 스프링 이니셜라이저로 시작하기
모든 Spring 애플리케이션의 경우 Spring Initializr로 시작해야 합니다.
Spring Initializr는 애플리케이션에 필요한 모든 종속성을 가져오는 빠른 방법을 제공하고 많은 설정을 수행합니다. 
이 예제에는 "Spring for Apache Geode" 종속성이 필요합니다.

다음 목록은 Gradle을 사용할 때 build.gradle 파일의 예를 보여줍니다.
```
plugins {
    id 'org.springframework.boot' version '2.7.0'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    id 'io.freefair.lombok' version '6.3.0'
    id 'java'
}

apply plugin: 'eclipse'
apply plugin: 'idea'

group = "org.springframework"
version = "0.1.0"
sourceCompatibility = '1.8'

repositories {
    mavenCentral()
}

dependencies {
    implementation "org.springframework.boot:spring-boot-starter"
    implementation "org.springframework.data:spring-data-geode"
    implementation "com.fasterxml.jackson.core:jackson-databind"
    implementation "org.projectlombok:lombok"
    runtimeOnly "javax.cache:cache-api"
    runtimeOnly "org.springframework.shell:spring-shell:1.2.0.RELEASE"
}
```


### 데이터 가져오기를 위한 바인딩 가능한 개체 만들기
이제 프로젝트 및 빌드 시스템을 설정했으므로 Quote 서비스에서 견적(데이터)을 가져오는 데 필요한 비트를 캡처하는 데 필요한 도메인 개체를 정의하는 데 집중할 수 있습니다.

(src/main/java/hello/Quote.java)
```java
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("unused")
public class Quote {

    private Long id;

    private String quote;

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Quote)) {
            return false;
        }

        Quote that = (Quote) obj;

        return ObjectUtils.nullSafeEquals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {

        int hashValue = 17;

        hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getId());

        return hashValue;
    }

    @Override
    public String toString() {
        return getQuote();
    }
}


```

Quote 도메인 클래스에는 id 및 quote 속성이 있습니다. 이 가이드에서 더 자세히 수집하게 될 두 가지 기본 속성입니다. Quote 클래스의 구현은 Project Lombok을 사용하여 단순화되었습니다.

#### 어노테이션 정리
@JsonIgnoreProperties(ignoreUnknown = true)
- JSON 직렬화 또는 역직렬화 과정에서 알 수 없는 속성들을 무시하도록 지정합니다.
SuppressWarnings("unused")
- 자바 컴파일러에 대해 경고 메시지를 억제하도록 지시하는 어노테이션입니다. "unused" 사용하지 않는 코드와 관련된 경고를 억제하는데 사용됩니다.
즉, 해당 코드를 사용하지 않더라도 컴파일러에서 경고를 표시하지 않도록 합니다.


Quote 외에도 QuoteResponse는 Quote 요청에서 보낸 Quote 서비스에서 보낸 응답의 전체 페이로드를 캡처합니다. 여기에는 견적과 함께 요청의 상태(일명 유형)가 포함됩니다. 이 클래스는 구현을 단순화하기 위해 Project Lombok도 사용합니다.
(src/main/java/hello/QuoteResponse.java)

```java
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuoteResponse {

	@JsonProperty("value")
	private Quote quote;

	@JsonProperty("type")
	private String status;

	@Override
	public String toString() {
		return String.format("{ @type = %1$s, quote = '%2$s', status = %3$s }",
			getClass().getName(), getQuote(), getStatus());
	}
}
```

Quote 서비스의 일반적인 응답은 다음과 같습니다.

```json
{
  "type":"success",
  "value": {
    "id":1,
    "quote":"Working with Spring Boot is like pair-programming with the Spring developers."
  }
}
```
### 데이터 조회 견적 서비스
다음 단계는 견적을 쿼리하는 서비스 클래스를 만드는 것입니다.

(src/main/java/hello/QuoteService.java)
```java

@SuppressWarnings("unused")
@Service
public class QuoteService {

	protected static final String ID_BASED_QUOTE_SERVICE_URL = "https://quoters.apps.pcfone.io/api/{id}";
	protected static final String RANDOM_QUOTE_SERVICE_URL = "https://quoters.apps.pcfone.io/api/random";

	private volatile boolean cacheMiss = false;

	private final RestTemplate quoteServiceTemplate = new RestTemplate();

	/**
	 * Determines whether the previous service method invocation resulted in a cache miss.
	 *
	 * @return a boolean value indicating whether the previous service method invocation resulted in a cache miss.
	 */
	public boolean isCacheMiss() {
		boolean cacheMiss = this.cacheMiss;
		this.cacheMiss = false;
		return cacheMiss;
	}

	protected void setCacheMiss() {
		this.cacheMiss = true;
	}

	/**
	 * Requests a quote with the given identifier.
	 *
	 * @param id the identifier of the {@link Quote} to request.
	 * @return a {@link Quote} with the given ID.
	 */
	@Cacheable("Quotes")
	public Quote requestQuote(Long id) {
		setCacheMiss();
		return requestQuote(ID_BASED_QUOTE_SERVICE_URL, Collections.singletonMap("id", id));
	}

	/**
	 * Requests a random quote.
	 *
	 * @return a random {@link Quote}.
	 */
	@CachePut(cacheNames = "Quotes", key = "#result.id")
	public Quote requestRandomQuote() {
		setCacheMiss();
		return requestQuote(RANDOM_QUOTE_SERVICE_URL);
	}

	protected Quote requestQuote(String URL) {
		return requestQuote(URL, Collections.emptyMap());
	}

	protected Quote requestQuote(String URL, Map<String, Object> urlVariables) {

		return Optional.ofNullable(this.quoteServiceTemplate.getForObject(URL, QuoteResponse.class, urlVariables))
			.map(QuoteResponse::getQuote)
			.orElse(null);
	}
}
```


1. QuoteService는 Spring의 RestTemplate을 사용하여 Quote 서비스의 API를 쿼리합니다. 
Quote 서비스는 JSON 개체를 반환하지만 Spring은 Jackson을 사용하여 QuoteResponse 및 궁극적으로 Quote 개체에 데이터를 바인딩합니다.

2. 이 서비스 클래스의 핵심 부분은 requestQuote가 @Cacheable("Quotes")로 주석 처리된 방법입니다. Spring의 캐싱 추상화는 requestQuote에 대한 호출을 가로채 서비스 메서드가 이미 호출되었는지 확인합니다. 
그렇다면 Spring의 캐싱 추상화는 캐시된 복사본을 반환합니다. 그렇지 않으면 Spring은 메서드를 호출하고 응답을 캐시에 저장한 다음 결과를 호출자에게 반환합니다.

3. requestRandomQuote 서비스 메서드에 @CachePut 을 사용했습니다. 이 서비스 메서드 호출에서 반환된 견적은 무작위이므로 어떤 견적을 받을지 알 수 없습니다.
따라서 호출 전에 캐시(예: Quotes)를 참조할 수 없지만 호출 결과를 캐시할 수 있습니다. 
관심 있는 견적이 무작위로 선택되어 캐시되었다고 가정하면 후속 requestQuote(id) 호출에 긍정적인 영향을 미칩니다. 이전에.

4. @CachePut은 SpEL 표현식("#result.id")을 사용하여 서비스 메소드 호출의 결과에 액세스하고 캐시 키로 사용할 견적의 ID를 검색합니다.
여기에서 Spring의 캐시 추상화 SpEL 컨텍스트에 대해 자세히 알아볼 수 있습니다.

```
캐시 이름을 제공해야 합니다. 데모용으로 이름을 "Quotes"로 지정했지만 프로덕션에서는 적절하게 설명하는 이름을 선택하는 것이 좋습니다.
 이것은 또한 다른 방법이 다른 캐시와 연관될 수 있음을 의미합니다. 이는 서로 다른 만료 또는 제거 정책 등과 같이 각 캐시에 대해 서로 다른 구성 설정이 있는 경우에 유용합니다.
```

나중에 코드를 실행할 때 각 호출을 실행하는 데 걸리는 시간을 확인하고 캐싱이 서비스 응답 시간에 미치는 영향을 식별할 수 있습니다. 이것은 특정 호출 캐싱의 가치를 보여줍니다. 
애플리케이션이 지속적으로 동일한 데이터를 조회하는 경우 결과를 캐싱하면 성능이 크게 향상될 수 있습니다.

### 애플리케이션을 실행 가능하게 만들기
Apache Geode 캐싱을 웹 앱 및 WAR 파일에 포함할 수 있지만 아래에 설명된 더 간단한 접근 방식은 독립 실행형 애플리케이션을 만듭니다. 
오래된 Java main() 메서드로 구동되는 실행 가능한 단일 JAR 파일에 모든 것을 패키징합니다.

```
@SpringBootApplication
@ClientCacheApplication(name = "CachingGemFireApplication")
@EnableCachingDefinedRegions(clientRegionShortcut = ClientRegionShortcut.LOCAL)
@EnableGemfireCaching
@SuppressWarnings("unused")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	ApplicationRunner runner(QuoteService quoteService) {

		return args -> {
			Quote quote = requestQuote(quoteService, 12L);
			requestQuote(quoteService, quote.getId());
			requestQuote(quoteService, 10L);
		};
	}

	private Quote requestQuote(QuoteService quoteService, Long id) {

		long startTime = System.currentTimeMillis();

		Quote quote = Optional.ofNullable(id)
			.map(quoteService::requestQuote)
			.orElseGet(quoteService::requestRandomQuote);

		long elapsedTime = System.currentTimeMillis();

		System.out.printf("\"%1$s\"%nCache Miss [%2$s] - Elapsed Time [%3$s ms]%n", quote,
			quoteService.isCacheMiss(), (elapsedTime - startTime));

		return quote;
	}
}
```
#### 어노테이션 정리
@ClientCacheApplication 
- GemFire 또는 Geode 클라이언트 애플리케이션을 선언하는 데 사용되며, name 속성을 통해 애플리케이션의 이름을 지정할 수 있습니다.
@EnableCachingDefinedRegions
- GemFire 또는 Geode 클라이언트 캐시에서 사용할 캐싱 기능을 활성화하는 어노테이션입니다. 매개변수에 clientRegionShortcut 속성은 
클라이언트 캐시 리전을 어떻게 생성을 하는지 정의합니다. 이 예제에서는 로컬 캐시 리전을 사용합니다. (클라이언트 측에서만 캐싱을 수행)
@EnableGemfireCaching
- Spring의 캐싱 지원을 GemFire 또는 Geode와 통합하기 위해 사용되는 어노테이션입니다.
- @Cacheable, @CachePut, @CacheEvict 어노테이션을 사용해서 캐시 동작을 정의 할 수 있습니다.

견적이 처음 요청될 때(requestQuote(id) 사용) 캐시 누락이 발생하고 서비스 메서드가 호출되어 0ms에 가까운 눈에 띄는 지연이 발생합니다. 
이 경우 캐싱은 서비스 메소드인 requestQuote의 입력 매개변수(즉, id)에 의해 연결됩니다.
즉, id 메소드 매개변수는 캐시 키입니다. ID로 식별되는 동일한 견적에 대한 후속 요청은 캐시 적중을 초래하므로 비용이 많이 드는 서비스 호출을 피할 수 있습니다.

#### Gradle을 사용하는 경우 다음 명령을 실행하여 애플리케이션을 실행합니다.
./gradlew bootRun ./gradlew build 또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```
java -jar build/libs/caching-gemfire-0.1.0.jar
```

로깅 출력이 표시됩니다. 서비스가 몇 초 내에 시작되어 실행되어야 합니다.

```
"@springboot with @springframework is pure productivity! Who said in #java one has to write double the code than in other langs? #newFavLib"
Cache Miss [true] - Elapsed Time [776 ms]
"@springboot with @springframework is pure productivity! Who said in #java one has to write double the code than in other langs? #newFavLib"
Cache Miss [false] - Elapsed Time [0 ms]
"Really loving Spring Boot, makes stand alone Spring apps easy."
Cache Miss [true] - Elapsed Time [96 ms]
```


여기에서 견적에 대한 Quote 서비스에 대한 첫 번째 호출에 776ms가 걸리고 결과적으로 캐시 누락이 발생했음을 알 수 있습니다. 
그러나 동일한 견적을 요청하는 두 번째 호출은 0ms가 걸리고 결과적으로 캐시 적중이 발생했습니다. 
이는 두 번째 호출이 캐시되었고 실제로 Quote 서비스에 도달하지 않았음을 분명히 보여줍니다. 
그러나 캐시되지 않은 특정 견적 요청에 대한 최종 서비스 호출이 이루어지면 이 새로운 견적이 호출 전에 이전에 캐시에 없었기 때문에 96ms가 걸리고 캐시 누락이 발생했습니다.