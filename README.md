# graphql-demo
Spring Boot + GraphQL + Mybatis(MariaDB)

## 실습 환경
0. Mariadb 테스트 데이터 준비하기
    
   git clone https://github.com/datacharmer/test_db.git

1. Docker로 MariaDB 설치하기

   ```docker pull mariadb```

2. Docker로 MariaDB 실행 (테스트 데이터 다운 경로와 마운팅)
   ```docker run \
   --name mariadb \
   -d \
   -v [테스트데이터 다운 로컬 경로]:/test_db \
   -p 3306:3306 \
   --restart=always \
   -e MYSQL_ROOT_PASSWORD=root \
   mariadb
   ```
3. dump 데이터 생성

   1) `cd test_db` : 실행할 sql 데이터 디렉토리로 이동
   
   2) `source` 명령어로 데이터 생성 : `source employees.sql`

   3) 데이터 조회해보기

## Spring Boot 시작하기(with Mybatis, GraphQL)
### 프로젝트 생성하기
#### 개발 환경
- Spring Boot : `2.7.0`
- Gradle: `7.2`
- Java : `jdk17`
- MariaDB : `10.8` (latest)
#### Spring Boot 프로젝트 생성
[start.spring.io](https://start.spring.io/) initializr 에서 의존성 추가하여 프로젝트 생성

- [ ]  Lombok
- [ ]  MyBatis Framework
- [ ]  MariaDB Driver

#### MyBatis 연동하기
```
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.url=jdbc:mariadb://localhost:3306/employees?useUnicode=yes&characterEncoding=UTF-8&allowMultiQueries=true&serverTimezone=Asia/Seoul
spring.datasource.username=root
spring.datasource.password=root
mybatis.configuration.map-underscore-to-camel-case=true
mybatis.type-aliases-package=com.example.graphqldemo.entity
mybatis.mapper-locations=/mapper/*.xml
```

#### MyBatis 테스트하기
MyBatis 단위 테스트를 위한 의존성 추가 : `mybatis-spring-boot-starter-test`

mybatis-spring-boot-starter-test 더보기 -> [Introduction](http://mybatis.org/spring-boot-starter/mybatis-spring-boot-test-autoconfigure/#Using_MybatisTest)


### 1) GraphQL 튜토리얼 따라하기 - DataFetcher from graphql-java
튜토리얼 [Tutorial with Spring Boot | GraphQL Java](https://www.graphql-java.com/tutorials/getting-started-with-spring-boot/)

#### 의존성 추가(Gradle)
```groovy
dependencies{
    implementation 'org.springframework.boot:spring-boot-starter-web' // Spring Web
    implementation 'com.graphql-java:graphql-java:18.0' // GraphQL Java
    implementation 'com.graphql-java:graphql-java-spring-boot-starter-webmvc:2.0' // GraphQL spring
    implementation 'com.google.guava:guava:31.1-android' // Optional
    implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:2.2.2' // MyBatis
    compileOnly 'org.projectlombok:lombok' // Lombok
    runtimeOnly 'org.mariadb.jdbc:mariadb-java-client' // MariaDB JDBC
    annotationProcessor 'org.projectlombok:lombok' // Lombok
    testImplementation 'org.springframework.boot:spring-boot-starter-test' // Spring Boot Test
    testImplementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter-test:2.2.2' // MyBatis Test
}
```
#### SDL 작성 (`src/main/resources/schema.graphqls`)
> - REST는 엔드포인트의 집합, GraphQL은 *타입 집합*.
> - 데이터 타입의 집합 : 스키마
> - 스키마 정의 언어 : SDL(Schema Definition Language)
>
```graphql
type Query {
	employeeByEmpNo(empNo: ID): EmployeeDetail,
	departments: [Department],
	titles: [Title],
}

type EmployeeDetail {
	empNo:ID!
	birthDate: String
	firstName: String
	lastName: String
	gender: Gender
	hireDate: String
	dept: DepartmentDetail
	salary: Salary
	title: Title
}

type Department {
	deptNo: ID!
	deptName: String!
}

# dept_emp의 정보 + dept의 부서이름
type DepartmentDetail {
	deptName: String!
	fromDate: String
	toDate: String
}

type Title {
	empNo: Int!
	title: String!
	fromDate: String!
	toDate: String
}

type DeptManager {
	deptNo: String!
	empNo: Int!
	fromDate: String
	toDate: String
}

type Salary {
	empNo:Int!
	salary: Int
	fromDate: String!
	toDate: String
}

enum Gender {
	M
	F
}

# scalar Date # Allowed values are yyyy-mm-dd
```
#### `GraphQLProvider` : GraphQL 사용을 위한 환경 구성

`GraphQLProvider` 에서는 `GraphQL`을 빈으로 등록하고 스키마 파일(SDL)로부터 스키마 객체를 생성한다.
```java
@PostConstruct
public void init() throws IOException {
    // Guava Resource로부터 정의서(SDL) 가져오기
    URL url = Resources.getResource("schema.graphqls");
    String sdl = Resources.toString(url, Charsets.UTF_8);
		// Guava 사용하지 않을 경우
    //String sdl = StreamUtils.copyToString(schemaResource.getInputStream(), StandardCharsets.UTF_8);

    // SDL 로부터 GraphQLSchema, GraphQL 인스턴스 생성
    GraphQLSchema graphQLSchema = buildSchema(sdl);
    this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
}
```
- 의존관계 주입이 이루어진 후에 초기화를 수행하기 위해 `@PostContruct` 로 init()메소드를 작성한다.
   - SDL 파일(`schema.graphqls`)을 읽고,
   - SDL 로부터 `GraphQLSchema`, `GraphQL` 인스턴스를 생성한다.

     (`GraphQL`인스턴스는 메소드주입 방식으로 `bean`으로 등록된다)
      ```java
      @Bean
      public GraphQL graphQL() {
         return graphQL;
      }
     ```
   - GraphQL : GraphQL Java Spring Adaptor가 `/graphql`(디폴트 경로)로 HTTP를 통한 스키마를 이용할 수 있게 하는 객체
     - DataFetcher를 등록한다.
      
       DataFetcher는 SDL에 작성된 쿼리(메소드)마다 1:1로 작성되어야 한다.
       ```java
           private RuntimeWiring buildWiring() {
             return RuntimeWiring.newRuntimeWiring()
              .type(newTypeWiring("Query")
              .dataFetcher("employeeByEmpNo", graphQLDataFetchers.getEmployeeByEmpNo()))
              .type(newTypeWiring("Query")
              .dataFetcher("departments", graphQLDataFetchers.getDepartments()))
              .type(newTypeWiring("Query")
              .dataFetcher("titles", graphQLDataFetchers.getTitles()))
              .build();
           }
       ```

#### `DataFetchers`
- GraphQL Java Server 구현 시의 핵심 개념.
- 하나의 Query 가 실행되는 동안 Data와 Field를 fetch하는 작업 담당.
- Query 가 실행되면 각 field에 따라 적절한  `DataFetcher`가 호출된다.

<aside>
⭐ 스키마의 **모든 필드**는 각각 연관된 `DataFetcher`를 갖고 있다.
만약 한 필드라도 `DataFetcher`에 명시되어 있지 않을 경우 디폴트로 `PropertyDataFetcher`가 사용된다.

</aside>

#### `GraphQLDataFetchers` 생성하기

- literally, 말 그대로 GraphQL 스키마와 Data를 Fetching 하는 클래스
- 필요한 의존성 주입을 받는다.

    ```java
    // 의존성 주입
    public GraphQLDataFetchers(EmployeeMapper employeeMapper
            , TitleMapper titleMapper
            , DepartmentMapper departmentMapper
            , SalaryMapper salaryMapper) {
        this.employeeMapper = employeeMapper;
        this.departmentMapper = departmentMapper;
        this.titleMapper = titleMapper;
        this.salaryMapper = salaryMapper;
    }
    ```

- DataFetching 예시 1 : `EmployeeDetail` (직원과 관련된 모든 테이블의 정보를 Join해서 가져오는 예제)

    ```java
    public DataFetcher getEmployeeByEmpNo() {
    
            return dataFetchingEnvironment -> {
                // DataFetching Environment로부터 스키마에 작성된 파라미터를 불러올 수 있다.
                int empNo = Integer.parseInt(dataFetchingEnvironment.getArgument("empNo"));
    
                EmployeeInfo employeeInfo = employeeMapper.selectEmployeeInfoByEmpNo(empNo); // Join 쿼리
                Title title = titleMapper.selectTitleByEmpNo(empNo);
                Salary salary = salaryMapper.selectLastSalaryByEmpNo(empNo);
    
                // DepartmentDetail 스키마 객체를 생성
                DepartmentDetail departmentDetail = DepartmentDetail.builder()
                        .deptName(employeeInfo.getDeptName())
                        .fromDate(employeeInfo.getDeptFromDate())
                        .toDate(employeeInfo.getDeptToDate())
                        .build();
    
                // 최종 리턴인 EmployeeDetail 스키마 객체를 생성
                return EmployeeDetail.builder()
                        .empNo(empNo)
                        .firstName(employeeInfo.getFirstName())
                        .lastName(employeeInfo.getLastName())
                        .gender(employeeInfo.getGender())
                        .birthDate(employeeInfo.getBirthDate())
                        .hireDate(employeeInfo.getHireDate())
                        .dept(departmentDetail)
                        .salary(salary)
                        .title(title)
                        .build();
            };
        }
    ```

- DataFetching 예시 2 : Titles (직원의 역할 타이틀 정보를 원 테이블에서 조회)

    ```java
    public DataFetcher getDepartments() {
        return dataFetchingEnvironment -> {
            return departmentMapper.selectAllDepartments();
        };
    }
    ```

### 2) QueryResolver from graphql-java-tools
#### 의존성 추가(Gradle)
```groovy
dependencies{
    ...
    implementation 'com.graphql-java:graphql-java-tools:5.2.4' // GraphQL Java
    implementation 'com.graphql-java:graphql-spring-boot-starter:5.0.2' // GraphQL spring
    ...
}
```
#### GraphQLResolver 작성하기 (진행중)


  
### 테스트하기 : Graphql Playground
<aside>
💡 Graphql Playground는 graphql 쿼리 테스트 툴인데,
1. 데스크탑에 응용 프로그램으로 설치하거나
2. 프로젝트 내부에 모듈로 생성할 수 있음.

</aside>

1. 프로젝트 모듈로 포함시키기 - 디펜던시 추가
```groovy
implementation 'com.graphql-java-kickstart:playground-spring-boot-starter:11.1.0' // Graphql Playground
```
2. application.properties
```
# graphql playground
graphql.playground.mapping=/playground
graphql.playground.enabled=true
graphql.playground.page-title=graphql playground
graphql.playground.settings.editor.font-size=13
```
3. 실행하기 : 프로젝트 실행시키고 http://localhost:8080/playground
4. 테스트 : 쿼리 작성해서 테스트하기
   ```   # Write your query or mutation here
    query {
      departments {
        deptNo
        deptName
      }
    }
   ```
   ```   # Write your query or mutation here
    query {
       employeeByEmpNo(empNo:"10001"){
          empNo
          firstName
          lastName
          salary{
              salary
              fromDate
              toDate
          }
          dept {
             deptName
             fromDate
             toDate
         }
         title {
            title
         }
    }
}