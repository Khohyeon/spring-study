## Spring Boot JAR 애플리케이션을 WAR로 변환
Spring Boot는 두 가지 강력한 플러그인과 함께 제공됩니다.

* spring-boot-gradle-plugin

* spring-boot-maven-plugin

둘 다 기본적으로 기능 패리티가 있으며 명령줄에서 Spring Boot 앱을 실행하고 실행 가능한 JAR을 번들로 묶는 기능을 제공합니다. 이 주제는 끝까지 실행 단계에서 거의 모든 가이드에서 언급됩니다.

인기 있는 주제는 많은 사람들이 여전히 컨테이너 내부에 배포할 WAR 파일을 생성하기를 원한다는 것입니다. 이 두 플러그인 모두 이를 지원합니다. 기본적으로 WAR 파일을 생성하고 포함된 컨테이너 종속성을 "제공됨"으로 선언하도록 프로젝트를 재구성해야 합니다. 이렇게 하면 관련 포함된 컨테이너 종속성이 WAR 파일에 포함되지 않습니다.

컨테이너용 WAR 파일을 생성하도록 애플리케이션을 구성하는 방법에 대한 자세한 단계는 다음을 참조하세요.

* <a href="https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#build-tool-plugins-maven-packaging">Maven으로 실행 가능한 jar 및 war 파일 패키징 </a>

* <a href="https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#build-tool-plugins-gradle-plugin">스프링 부트 Gradle 플러그인</a>

* <a href="https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#packaging-executable.wars">Gradle 플러그인 참조: 실행 가능한 war 패키징</a>

```
Spring Boot는 서블릿 5.0 사양 컨테이너에서 작동합니다.
```

### 코드받기
<a href="https://github.com/spring-guides/gs-convert-jar-to-war"> git 저장소로 이동 </a>
