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


### GraphQL 튜토리얼 따라하기 - DataFetcher from graphql-java
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

### (+)추가: Resolver 사용하여 GraphQL Server 구현하기
#### 의존성 재설정(Gradle)
```groovy
dependencies{
    ...
    implementation 'com.graphql-java-kickstart:graphql-spring-boot-starter:11.0.0' // GraphQL Spring
    implementation 'com.graphql-java-kickstart:playground-spring-boot-starter:11.1.0' // GraphQL Playground
    ...
    testImplementation 'com.graphql-java-kickstart:graphql-spring-boot-starter-test:11.0.0' // GraphQL Test
}
```

#### Schema 수정
- 간단한 쿼리 테스트를 위해 titles, departments 쿼리 추가

- 내부 필드 객체를 리턴하는 employeeByEmpNo 쿼리 추가

```GraphQL
schema {
    query: Query
}

# The Root Query for the application
type Query {
    employees:[Employee],
    employeeByEmpNo(empNo: ID): EmployeeInfo
    titles: [Title],
    departments: [Department]

}

type Employee {
    empNo:ID!
    birthDate: String
    firstName: String
    lastName: String
    gender: String
    hireDate: String
}

type EmployeeInfo {
    empNo:ID!
    birthDate: String
    firstName: String
    lastName: String
    gender: String
    hireDate: String
    title: Title!
    salary: Salary!

}
```
#### GraphQLResolver 작성하기(QueryResolver)
Title, Department, EmployeeInfo 각각 GraphQLQueryResolver를 구현한다.

메소드 명명 규칙에 따라 get이 붙지 않아도 <필드명>으로 우선찾은 뒤, 없는 경우 get을 붙여 get<필드명>이 있는지 확인하여 메서드를 매핑시킨다. 

즉, titles(), getTitles() 둘 다 가능.

`TitleQueryResolver.java`
``` java
  /**
   *  Query 스키마에 해당하는 메서드를 GraphQLQueryResolver를 상속한 클래스에 작성.
   */
   @Component // 반드시 bean으로 등록.
   public class TitleQueryResolver implements GraphQLQueryResolver {
   
        private final TitleMapper titleMapper;

        public TitleQueryResolver(TitleMapper titleMapper){
            this.titleMapper = titleMapper;
        }

        // schema.graphqls의 Title과 Java 타입의 이름이 꼭 일치하지 않아도 됨.
        public List<Title> getTitles(){
            return titleMapper.selectAllTitles();
        }


}
```
`DepartmentQueryResolver.java`
``` java
@Component
@RequiredArgsConstructor
public class DepartmentQueryResolver implements GraphQLQueryResolver {
private final DepartmentMapper departmentMapper;

    public List<Department> departments(){
        System.out.println("DepartmentQueryResolver - getDepartments()");
        return departmentMapper.selectAllDepartments();
    }
}
```
`EmployeeInfoQueryResolver`
``` java
@Component
@RequiredArgsConstructor
public class EmployeeInfoQueryResolver implements GraphQLQueryResolver {

    private final EmployeeMapper employeeMapper;

    public EmployeeInfo employeeByEmpNo(int empNo){
        System.out.println("EmployeeInfoQueryResolver - employeeByEmpNo()");
        Employee employee = employeeMapper.selectEmployeeByEmpNo(empNo);
        return EmployeeInfo.builder()
                .empNo(employee.getEmpNo())
                .birthDate(employee.getBirthDate())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .gender(employee.getGender())
                .hireDate(employee.getHireDate())
                .build();
    }

}
```

#### GraphQLResolver 작성하기(FieldResolver)
EmployeeInfo 내부에는 title, salary 객체 필드가 있다.

이를 가져오기 위한 FieldResolver를 작성하는데, EmployeeInfo 안에서 title을 가져오는 TitleResolver, EmployeeInfo 안에서 salary를 가져오는 SalaryResolver를 작성한다.

`SalaryResolver`
``` java
/**
 * Field Resolver 구현
 * 타입 내에 또 다른 타입이 필드로 설정한 경우, 혹은 DB 쿼리 후 복잡한 계산 값이 추가로 필요한 경우 구현
 */
@Component
@RequiredArgsConstructor
public class SalaryResolver implements GraphQLResolver<EmployeeInfo> {

    private final SalaryMapper salaryMapper;

    public Salary salary(EmployeeInfo emp) {
        System.out.println("Request salary for EmployeeInfo");
        return salaryMapper.selectCurrentSalaryByEmpNo(emp.getEmpNo());
    }

}
```

`TitleResolver`
``` java
@Component
@RequiredArgsConstructor
public class TitleResolver implements GraphQLResolver<EmployeeInfo> {

    private final TitleMapper titleMapper;

    public Title title(EmployeeInfo emp) {
        System.out.println("Request Title for EmployeeInfo");
        return titleMapper.selectCurrentTitleByEmpNo(emp.getEmpNo());
    }

}
```
#### GraphQL Test Code 작성하기
- 테스트용 쿼리 작성을 위해 `test`밑에 `resources`폴더 생성 : `test/resources`
- 테스트용 쿼리 작성 : `employee.graphqls`

    ```graphql
    query {
        employeeByEmpNo(empNo : 10001) {
            empNo
            birthDate
            firstName
            lastName
            gender
            hireDate
            title {
                title
                fromDate
                toDate
            }
            salary {
                salary
                fromDate
                toDate
            }
        }
    }
    ```

- 테스트 돌리기
    - 정의된 테스트 쿼리(`employee.graphqls`)를 읽어와 graphql 요청을 전송하는 테스트 작성
    - GraphQL Java Tools 를 사용한다면 `@SpringBootTest` 어노테이션 사용시 `RANDOM_PORT` 를 의무적으로 설정해야함.

    ```java
    @ExtendWith(SpringExtension.class) // JUnit5
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    public class EmployeeResolverTest {
    
        @Autowired
        private GraphQLTestTemplate graphQLTestTemplate;
    
        @Test
        void employeeInfoTest() throws IOException {
    
            GraphQLResponse response = graphQLTestTemplate.postForResource("employee.graphqls");
    
            JsonNode result = response.readTree().get("data");
            Assertions.assertNotNull(result.asText());
    
        }
    
    }
    ```