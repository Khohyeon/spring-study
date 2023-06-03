## Spring Boot로 애플리케이션 구축
이 가이드는 Spring Boot가 애플리케이션 개발을 가속화하는 데 어떻게 도움이 되는지 샘플을 제공합니다.
더 많은 Spring 시작하기 가이드를 읽으면서 Spring Boot에 대한 더 많은 사용 사례를 보게 될 것입니다. 
이 가이드는 Spring Boot의 빠른 맛보기를 제공하기 위한 것입니다. 자신만의 Spring Boot 기반 프로젝트를 만들고 싶다면 Spring Initializr를 방문하여 프로젝트 세부 정보를 입력하고 옵션을 선택한 다음 번들 프로젝트를 zip 파일로 다운로드하십시오.

### 무엇을 만들 것인가
Spring Boot로 간단한 웹 애플리케이션을 빌드하고 여기에 몇 가지 유용한 서비스를 추가합니다.

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

* 이 가이드의 소스 리포지토리를 다운로드하고 압축을 풀거나 Git을 사용하여 복제합니다. git clone https://github.com/spring-guides/gs-spring-boot.git

* gs-spring-boot/initial로 cd

* 간단한 웹 응용 프로그램 만들기로 이동하십시오.

완료하면 gs-spring-boot/complete의 코드와 비교하여 결과를 확인할 수 있습니다.


### Spring Boot로 할 수 있는 작업 알아보기
Spring Boot는 애플리케이션을 빌드하는 빠른 방법을 제공합니다. 클래스 경로와 구성한 빈을 살펴보고 누락된 항목에 대해 합당한 가정을 하고 해당 항목을 추가합니다. Spring Boot를 사용하면 인프라보다는 비즈니스 기능에 더 집중할 수 있습니다.

다음 예제는 Spring Boot가 무엇을 할 수 있는지 보여줍니다.

* 클래스 경로에 Spring MVC가 있습니까? 거의 항상 필요한 몇 가지 특정 빈이 있으며 Spring Boot는 자동으로 추가합니다. Spring MVC 애플리케이션에는 서블릿 컨테이너도 필요하므로 Spring Boot는 임베디드 Tomcat을 자동으로 구성합니다.

* Jetty가 클래스 경로에 있습니까? 그렇다면 아마도 Tomcat이 아니라 임베디드 Jetty가 필요할 것입니다. Spring Boot가 이를 처리합니다.

* Thymeleaf가 클래스 경로에 있습니까? 그렇다면 애플리케이션 컨텍스트에 항상 추가해야 하는 몇 가지 빈이 있습니다. Spring Boot는 그것들을 추가합니다.

이들은 Spring Boot가 제공하는 자동 구성의 몇 가지 예일 뿐입니다. 동시에 Spring Boot는 방해가 되지 않습니다. 예를 들어 Thymeleaf가 경로에 있는 경우 Spring Boot는 애플리케이션 컨텍스트에 SpringTemplateEngine을 자동으로 추가합니다. 그러나 자체 설정으로 자체 SpringTemplateEngine을 정의하면 Spring Boot는 추가하지 않습니다. 이렇게 하면 약간의 노력으로 제어할 수 있습니다.

### 스프링 이니셜라이저로 시작하기
이 사전 초기화된 프로젝트를 사용하고 생성을 클릭하여 ZIP 파일을 다운로드할 수 있습니다. 이 프로젝트는 이 자습서의 예제에 맞게 구성됩니다.

프로젝트를 수동으로 초기화하려면:

1. https://start.spring.io로 이동합니다. 이 서비스는 애플리케이션에 필요한 모든 종속성을 가져오고 대부분의 설정을 수행합니다.

2. Gradle 또는 Maven과 사용하려는 언어를 선택합니다. 이 가이드에서는 Java를 선택했다고 가정합니다.

3. 종속성을 클릭하고 Spring 웹을 선택합니다.

4. 생성을 클릭합니다.

5. 선택 사항으로 구성된 웹 애플리케이션의 아카이브인 결과 ZIP 파일을 다운로드합니다.

### 간단한 웹 애플리케이션 만들기
이제 다음 목록(src/main/java/com/example/springboot/HelloController.java에서)과 같이 간단한 웹 애플리케이션용 웹 컨트롤러를 만들 수 있습니다.

```java
@RestController
public class HelloController {

	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

}
```

@RestController로 표시되며 이는 웹 요청을 처리하기 위해 Spring MVC에서 사용할 준비가 되었음을 의미합니다. 
@GetMapping은 /를 index() 메서드에 매핑합니다. 브라우저에서 호출하거나 명령줄에서 curl을 사용하여 호출하면 이 메서드는 순수 텍스트를 반환합니다.
이는 @RestController가 @Controller와 @ResponseBody를 결합하기 때문입니다. 두 개의 주석은 웹 요청이 뷰가 아닌 데이터를 반환하도록 합니다.

### 
애플리케이션 클래스 생성
Spring Initializr는 간단한 애플리케이션 클래스를 생성합니다. 그러나 이 경우에는 너무 간단하다. 
다음 목록과 일치하도록 애플리케이션 클래스를 수정해야 합니다(src/main/java/com/example/springboot/Application.java에서).

```java
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			System.out.println("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}

		};
	}

}
```

@SpringBootApplication은 다음을 모두 추가하는 편리한 주석입니다.

* @Configuration: 애플리케이션 컨텍스트에 대한 빈 정의의 소스로 클래스에 태그를 지정합니다.

* @EnableAutoConfiguration: 클래스 경로 설정, 기타 빈 및 다양한 속성 설정을 기반으로 빈 추가를 시작하도록 Spring Boot에 지시합니다. 예를 들어 spring-webmvc가 클래스 경로에 있는 경우 이 주석은 애플리케이션을 웹 애플리케이션으로 플래그 지정하고 DispatcherServlet 설정과 같은 주요 동작을 활성화합니다.

* @ComponentScan: com/example 패키지에서 다른 구성 요소, 구성 및 서비스를 찾도록 Spring에 지시하여 컨트롤러를 찾도록 합니다.

main() 메서드는 Spring Boot의 SpringApplication.run() 메서드를 사용하여 애플리케이션을 시작합니다. XML이 한 줄도 없다는 사실을 눈치채셨나요? web.xml 파일도 없습니다. 이 웹 애플리케이션은 100% 순수 Java이며 배관이나 인프라 구성을 처리할 필요가 없습니다.

@Bean으로 표시된 CommandLineRunner 메소드도 있으며 시작 시 실행됩니다. 애플리케이션에서 생성되었거나 Spring Boot에서 자동으로 추가된 모든 빈을 검색합니다. 분류하여 출력합니다.

### 애플리케이션 실행
애플리케이션을 실행하려면 터미널 창(전체) 디렉토리에서 다음 명령을 실행합니다.
```
./gradlew bootRun
```

Maven을 사용하는 경우 터미널 창(전체) 디렉터리에서 다음 명령을 실행합니다.
```
./mvnw spring-boot:run
```

다음과 유사한 출력이 표시되어야 합니다.

#### Gradle을 사용하는 경우 다음 명령을 실행하여 애플리케이션을 실행합니다.
./gradlew bootRun ./gradlew build 또는 다음을 사용하여 JAR 파일을 빌드한 후 JAR 파일을 실행할 수 있습니다 .
```
java -jar build/libs/spring-boot-0.1.0.jar
```

```
Let's inspect the beans provided by Spring Boot:
application
applicationAvailability
applicationTaskExecutor
basicErrorController
beanNameHandlerMapping
beanNameViewResolver
characterEncodingFilter
classLoaderMetrics
commandLineRunner
controllerEndpointDiscoverer
controllerEndpointHandlerMapping
controllerExposeExcludePropertyEndpointFilter
conventionErrorViewResolver
defaultMeterObservationHandler
defaultServletHandlerMapping
defaultViewResolver
diskSpaceHealthIndicator
diskSpaceMetrics
dispatcherServlet
dispatcherServletRegistration
endpointCachingOperationInvokerAdvisor
endpointMediaTypes
endpointObjectMapper
endpointObjectMapperWebMvcConfigurer
endpointOperationParameterMapper
error
errorAttributes
errorPageCustomizer
errorPageRegistrarBeanPostProcessor
fileDescriptorMetrics
flashMapManager
forceAutoProxyCreatorToUseClassProxying
formContentFilter
handlerExceptionResolver
handlerFunctionAdapter
healthContributorRegistry
healthEndpoint
healthEndpointGroups
healthEndpointGroupsBeanPostProcessor
healthEndpointWebExtension
healthEndpointWebMvcHandlerMapping
healthHttpCodeStatusMapper
healthStatusAggregator
helloController
httpRequestHandlerAdapter
jacksonObjectMapper
jacksonObjectMapperBuilder
jsonComponentModule
jsonMixinModule
jsonMixinModuleEntries
jvmCompilationMetrics
jvmGcMetrics
jvmHeapPressureMetrics
jvmInfoMetrics
jvmMemoryMetrics
jvmThreadMetrics
lifecycleProcessor
localeCharsetMappingsCustomizer
localeResolver
logbackMetrics
management.endpoint.health-org.springframework.boot.actuate.autoconfigure.health.HealthEndpointProperties
management.endpoints.web-org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties
management.endpoints.web.cors-org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties
management.health.diskspace-org.springframework.boot.actuate.autoconfigure.system.DiskSpaceHealthIndicatorProperties
management.info-org.springframework.boot.actuate.autoconfigure.info.InfoContributorProperties
management.metrics-org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties
management.observations-org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties
management.server-org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties
management.simple.metrics.export-org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleProperties
managementServletContext
mappingJackson2HttpMessageConverter
messageConverters
meterRegistryPostProcessor
metricsHttpClientUriTagFilter
metricsHttpServerUriTagFilter
metricsObservationHandlerGrouping
micrometerClock
multipartConfigElement
multipartResolver
mvcContentNegotiationManager
mvcConversionService
mvcHandlerMappingIntrospector
mvcPathMatcher
mvcPatternParser
mvcResourceUrlProvider
mvcUriComponentsContributor
mvcUrlPathHelper
mvcValidator
mvcViewResolver
observationRegistry
observationRegistryPostProcessor
observationRestTemplateCustomizer
org.springframework.aop.config.internalAutoProxyCreator
org.springframework.boot.actuate.autoconfigure.availability.AvailabilityHealthContributorAutoConfiguration
org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration
org.springframework.boot.actuate.autoconfigure.endpoint.jackson.JacksonEndpointAutoConfiguration
org.springframework.boot.actuate.autoconfigure.endpoint.web.ServletEndpointManagementContextConfiguration
org.springframework.boot.actuate.autoconfigure.endpoint.web.ServletEndpointManagementContextConfiguration$WebMvcServletEndpointManagementContextConfiguration
org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration
org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration$WebEndpointServletConfiguration
org.springframework.boot.actuate.autoconfigure.endpoint.web.servlet.WebMvcEndpointManagementContextConfiguration
org.springframework.boot.actuate.autoconfigure.health.HealthContributorAutoConfiguration
org.springframework.boot.actuate.autoconfigure.health.HealthEndpointAutoConfiguration
org.springframework.boot.actuate.autoconfigure.health.HealthEndpointConfiguration
org.springframework.boot.actuate.autoconfigure.health.HealthEndpointWebExtensionConfiguration
org.springframework.boot.actuate.autoconfigure.health.HealthEndpointWebExtensionConfiguration$MvcAdditionalHealthEndpointPathsConfiguration
org.springframework.boot.actuate.autoconfigure.info.InfoContributorAutoConfiguration
org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration
org.springframework.boot.actuate.autoconfigure.metrics.JvmMetricsAutoConfiguration
org.springframework.boot.actuate.autoconfigure.metrics.LogbackMetricsAutoConfiguration
org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration
org.springframework.boot.actuate.autoconfigure.metrics.SystemMetricsAutoConfiguration
org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration
org.springframework.boot.actuate.autoconfigure.metrics.integration.IntegrationMetricsAutoConfiguration
org.springframework.boot.actuate.autoconfigure.metrics.startup.StartupTimeMetricsListenerAutoConfiguration
org.springframework.boot.actuate.autoconfigure.metrics.task.TaskExecutorMetricsAutoConfiguration
org.springframework.boot.actuate.autoconfigure.metrics.web.tomcat.TomcatMetricsAutoConfiguration
org.springframework.boot.actuate.autoconfigure.observation.ObservationAutoConfiguration
org.springframework.boot.actuate.autoconfigure.observation.ObservationAutoConfiguration$MeterObservationHandlerConfiguration
org.springframework.boot.actuate.autoconfigure.observation.ObservationAutoConfiguration$MeterObservationHandlerConfiguration$OnlyMetricsMeterObservationHandlerConfiguration        
org.springframework.boot.actuate.autoconfigure.observation.ObservationAutoConfiguration$OnlyMetricsConfiguration
org.springframework.boot.actuate.autoconfigure.observation.web.client.HttpClientObservationsAutoConfiguration
org.springframework.boot.actuate.autoconfigure.observation.web.client.HttpClientObservationsAutoConfiguration$MeterFilterConfiguration
org.springframework.boot.actuate.autoconfigure.observation.web.client.RestTemplateObservationConfiguration
org.springframework.boot.actuate.autoconfigure.observation.web.servlet.WebMvcObservationAutoConfiguration
org.springframework.boot.actuate.autoconfigure.observation.web.servlet.WebMvcObservationAutoConfiguration$MeterFilterConfiguration
org.springframework.boot.actuate.autoconfigure.system.DiskSpaceHealthContributorAutoConfiguration
org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration
org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration$SameManagementContextConfiguration
org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration$SameManagementContextConfiguration$EnableSameManagementContextConfiguration
org.springframework.boot.actuate.autoconfigure.web.servlet.ServletManagementContextAutoConfiguration
org.springframework.boot.autoconfigure.AutoConfigurationPackages
org.springframework.boot.autoconfigure.aop.AopAutoConfiguration
org.springframework.boot.autoconfigure.aop.AopAutoConfiguration$ClassProxyingConfiguration
org.springframework.boot.autoconfigure.availability.ApplicationAvailabilityAutoConfiguration
org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration
org.springframework.boot.autoconfigure.context.LifecycleAutoConfiguration
org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration
org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration
org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration$StringHttpMessageConverterConfiguration
org.springframework.boot.autoconfigure.http.JacksonHttpMessageConvertersConfiguration
org.springframework.boot.autoconfigure.http.JacksonHttpMessageConvertersConfiguration$MappingJackson2HttpMessageConverterConfiguration
org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration
org.springframework.boot.autoconfigure.internalCachingMetadataReaderFactory
org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$Jackson2ObjectMapperBuilderCustomizerConfiguration
org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$JacksonMixinConfiguration
org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$JacksonObjectMapperBuilderConfiguration
org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$JacksonObjectMapperConfiguration
org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$ParameterNamesModuleConfiguration
org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration
org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration
org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration
org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration
org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration
org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration$TomcatWebServerFactoryCustomizerConfiguration
org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration$DispatcherServletConfiguration
org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration$DispatcherServletRegistrationConfiguration
org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryConfiguration$EmbeddedTomcat
org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration$EnableWebMvcConfiguration
org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration$WebMvcAutoConfigurationAdapter
org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration$DefaultErrorViewResolverConfiguration
org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration$WhitelabelErrorViewConfiguration
org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration
org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration$TomcatWebSocketConfiguration
org.springframework.boot.context.internalConfigurationPropertiesBinder
org.springframework.boot.context.internalConfigurationPropertiesBinderFactory
org.springframework.boot.context.properties.BoundConfigurationProperties
org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor
org.springframework.boot.context.properties.EnableConfigurationPropertiesRegistrar.methodValidationExcludeFilter
org.springframework.boot.sql.init.dependency.DatabaseInitializationDependencyConfigurer$DependsOnDatabaseInitializationPostProcessor
org.springframework.context.annotation.internalAutowiredAnnotationProcessor
org.springframework.context.annotation.internalCommonAnnotationProcessor
org.springframework.context.annotation.internalConfigurationAnnotationProcessor
org.springframework.context.event.internalEventListenerFactory
org.springframework.context.event.internalEventListenerProcessor
parameterNamesModule
pathMappedEndpoints
pingHealthContributor
preserveErrorControllerTargetClassPostProcessor
processorMetrics
propertiesMeterFilter
propertySourcesPlaceholderConfigurer
requestContextFilter
requestMappingHandlerAdapter
requestMappingHandlerMapping
resourceHandlerMapping
restTemplateBuilder
restTemplateBuilderConfigurer
routerFunctionMapping
server-org.springframework.boot.autoconfigure.web.ServerProperties
servletEndpointDiscoverer
servletEndpointRegistrar
servletExposeExcludePropertyEndpointFilter
servletWebChildContextFactory
servletWebServerFactoryCustomizer
simpleConfig
simpleControllerHandlerAdapter
simpleMeterRegistry
spring.info-org.springframework.boot.autoconfigure.info.ProjectInfoProperties
spring.jackson-org.springframework.boot.autoconfigure.jackson.JacksonProperties
spring.lifecycle-org.springframework.boot.autoconfigure.context.LifecycleProperties
spring.mvc-org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties
webServerFactoryCustomizerBeanPostProcessor
websocketServletWebServerCustomizer
welcomePageHandlerMapping
```

org.springframework.boot.autoconfigure 빈을 명확하게 볼 수 있습니다. tomcatEmbeddedServletContainerFactory도 있습니다.

이제 다음 명령(해당 출력과 함께 표시됨)을 실행하여 curl(별도의 터미널 창에서)로 서비스를 실행합니다.

```
$ curl localhost:8080
Greetings from Spring Boot!
```

단위 테스트 추가
추가한 끝점에 대한 테스트를 추가하고 싶을 것이며 Spring Test는 이를 위한 몇 가지 장치를 제공합니다.

Gradle을 사용하는 경우 build.gradle 파일에 다음 종속성을 추가합니다.
```
testImplementation('org.springframework.boot:spring-boot-starter-test')
```


이제 다음 목록(src/test/java/com/example/springboot/HelloControllerTest.java에서)이 보여주는 것처럼 엔드포인트를 통해 서블릿 요청 및 응답을 조롱하는 간단한 단위 테스트를 작성합니다.

```java
@SpringBootTest
@AutoConfigureMockMvc
public class HelloControllerTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void getHello() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo("Greetings from Spring Boot!")));
	}
}
```
HTTP 요청 주기를 모방할 뿐만 아니라 Spring Boot를 사용하여 간단한 전체 스택 통합 테스트를 작성할 수도 있습니다. 예를 들어 앞에서 본 모의 테스트 대신(또는 뿐만 아니라) 다음 테스트를 만들 수 있습니다(src/test/java/com/example/springboot/HelloControllerIT.java에서).

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloControllerIT {

	@Autowired
	private TestRestTemplate template;

    @Test
    public void getHello() throws Exception {
        ResponseEntity<String> response = template.getForEntity("/", String.class);
        assertThat(response.getBody()).isEqualTo("Greetings from Spring Boot!");
    }
}
```

임베디드 서버는 webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT 때문에 무작위 포트에서 시작하고 실제 포트는 TestRestTemplate의 기본 URL에서 자동으로 구성됩니다.

### 프로덕션급 서비스 추가
비즈니스용 웹 사이트를 구축하는 경우 일부 관리 서비스를 추가해야 할 수 있습니다. Spring Boot는 액추에이터 모듈과 함께 여러 가지 서비스(예: 상태, 감사, 빈 등)를 제공합니다.

Gradle을 사용하는 경우 build.gradle 파일에 다음 종속성을 추가합니다.

```
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```

그런 다음 응용 프로그램을 다시 시작하십시오. Gradle을 사용하는 경우 터미널 창(전체 디렉터리)에서 다음 명령을 실행합니다.
```
./gradlew bootRun
```


새로운 RESTful 엔드포인트 세트가 애플리케이션에 추가되었음을 확인할 수 있습니다. Spring Boot에서 제공하는 관리 서비스입니다. 다음 목록은 일반적인 출력을 보여줍니다.

```
management.endpoint.health-org.springframework.boot.actuate.autoconfigure.health.HealthEndpointProperties
management.endpoints.web-org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties
management.endpoints.web.cors-org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties
management.health.diskspace-org.springframework.boot.actuate.autoconfigure.system.DiskSpaceHealthIndicatorProperties
management.info-org.springframework.boot.actuate.autoconfigure.info.InfoContributorProperties
management.metrics-org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties
management.observations-org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties
management.server-org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties
management.simple.metrics.export-org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleProperties
managementServletContext

```

```
/actuator/shutdown 엔드포인트도 있지만 기본적으로 JMX를 통해서만 볼 수 있습니다. 이를 HTTP 끝점으로 활성화하려면 application.properties 파일에 management.endpoint.shutdown.enabled=true를 추가하고 management.endpoints.web.exposure.include=health,info,shutdown으로 노출합니다. 그러나 공개적으로 사용 가능한 애플리케이션에 대해 종료 엔드포인트를 활성화해서는 안 됩니다.
```

properties 파일에 다음을 추가합니다.
```properties
management.endpoint.shutdown.enabled=true
application.propertiesmanagement.endpoints.web.exposure.include=health,info,shutdown
```

다음 명령을 실행하여 애플리케이션의 상태를 확인할 수 있습니다.
```
입력 : $ curl localhost:8080/actuator/health
출력 : {"status":"UP"}
```
curl을 통해 종료를 호출하여 application.properties에 필요한 줄(이전 참고에 표시됨)을 추가하지 않은 경우 어떤 일이 발생하는지 확인할 수 있습니다.

```
입력 : $ curl -X POST localhost:8080/actuator/shutdown
출력 : {"timestamp":1401820343710,"error":"Not Found","status":404,"1message":"","path":"/actuator/shutdown"}
```
활성화하지 않았기 때문에 요청된 엔드포인트를 사용할 수 없습니다. <br> 
-> 엔드포인트가 존재하지 않기 때문

### Spring Boot의 스타터 보기
Spring Boot의 "스타터" 중 일부를 보았습니다. 여기 소스 코드에서 모두 볼 수 있습니다.

### JAR 지원 및 Groovy 지원
마지막 예제는 Spring Boot가 필요한지 모를 수도 있는 빈을 연결하는 방법을 보여주었습니다. 편리한 관리 서비스를 켜는 방법도 보여줬다.


