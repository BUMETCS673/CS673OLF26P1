This folder contains all source code and test code.

When actual springboot project checks in, we can run

# To Start Up

# docker-compose up --build



[user@localhost code]$ docker-compose up --build
[+] up 10/12
 ⠙ Image mysql:8.0 [⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿] 233.6MB / 233.6MB Pulling                                                               12.2s
WARN[0012] buildx Docker CLI plugin not found: falling back to the classic builder. BuildKit-only build features (multi-arch, secrets, ssh, additional contexts, ...) will not be available 
Sending build context to Docker daemon  48.54MB
Step 1/6 : FROM eclipse-temurin:17-jdk-alpine
17-jdk-alpine: Pulling from library/eclipse-temurin
55afa1ecc21d: Pull complete 
a26cd5aab71d: Pull complete 
ebb3df790276: Pull complete 
05721dd3d30a: Pull complete 
768e6e1dbe85: Pull complete 
Digest: sha256:0bd5d65efad5c8d9f8d8e6573aa5c8851237550605ff18ff78fee5810c2ebe25
Status: Downloaded newer image for eclipse-temurin:17-jdk-alpine
 ---> 6e94d7e422ff
Step 2/6 : WORKDIR /app
 ---> Running in 48c1eb2bc6d2
 ---> Removed intermediate container 48c1eb2bc6d2
 ---> b7c8b45c88d9
Step 3/6 : COPY target/*.jar app.jar
 ---> 8f4f800be70c
Step 4/6 : EXPOSE 8080
 ---> Running in 4c5a20b2964f
 ---> Removed intermediate container 4c5a20b2964f
 ---> 795ccb5eace1
Step 5/6 : ENTRYPOINT ["java", "-jar", "app.jar"]
 ---> Running in dd42fddbc2c8
 ---> Removed intermediate container dd42fddbc2c8
 ---> f512a6c9ac40
Step 6/6 : LABEL com.docker.compose.image.builder=classic
 ---> Running in f2d7f9b31a29
 ---> Removed intermediate container f2d7f9b31a29
 ---> 49a7a6883382
[+] up 17/17 built 49a7a6883382
 ✔ Image mysql:8.0                  Pulled                                                                               12.3s
 ✔ Image code-backend-app           Built                                                                                11.3s
 ✔ Volume code_mysql_data           Created                                                                               0.0s
 ✔ Network code_default             Created                                                                               0.0s
 ✔ Container app-mysql-db           Created                                                                               0.1s
 ✔ Container app-springboot-backend Created                                                                               0.0s
Attaching to app-mysql-db, app-springboot-backend
app-mysql-db  | 2026-09-13 18:35:04+00:00 [Note] [Entrypoint]: Entrypoint script for MySQL Server 8.0.46-1.el9 started.
Container app-mysql-db Waiting 
app-mysql-db  | 2026-09-13 18:35:04+00:00 [Note] [Entrypoint]: Switching to dedicated user 'mysql'
app-mysql-db  | 2026-09-13 18:35:04+00:00 [Note] [Entrypoint]: Entrypoint script for MySQL Server 8.0.46-1.el9 started.
app-mysql-db  | 2026-09-13 18:35:04+00:00 [Note] [Entrypoint]: Initializing database files
app-mysql-db  | 2026-09-13T18:35:04.597341Z 0 [Warning] [MY-011068] [Server] The syntax '--skip-host-cache' is deprecated and will be removed in a future release. Please use SET GLOBAL host_cache_size=0 instead.
app-mysql-db  | 2026-09-13T18:35:04.597424Z 0 [System] [MY-013169] [Server] /usr/sbin/mysqld (mysqld 8.0.46) initializing of server in progress as process 80
app-mysql-db  | 2026-09-13T18:35:04.607235Z 1 [System] [MY-013576] [InnoDB] InnoDB initialization has started.
app-mysql-db  | 2026-09-13T18:35:05.469381Z 1 [System] [MY-013577] [InnoDB] InnoDB initialization has ended.
app-mysql-db  | 2026-09-13T18:35:07.511555Z 6 [Warning] [MY-010453] [Server] root@localhost is created with an empty password ! Please consider switching off the --initialize-insecure option.
app-mysql-db  | 2026-09-13 18:35:12+00:00 [Note] [Entrypoint]: Database files initialized
app-mysql-db  | 2026-09-13 18:35:12+00:00 [Note] [Entrypoint]: Starting temporary server
app-mysql-db  | 2026-09-13T18:35:12.669120Z 0 [Warning] [MY-011068] [Server] The syntax '--skip-host-cache' is deprecated and will be removed in a future release. Please use SET GLOBAL host_cache_size=0 instead.
app-mysql-db  | 2026-09-13T18:35:12.669585Z 0 [System] [MY-010116] [Server] /usr/sbin/mysqld (mysqld 8.0.46) starting as process 130
app-mysql-db  | 2026-09-13T18:35:12.682081Z 1 [System] [MY-013576] [InnoDB] InnoDB initialization has started.
app-mysql-db  | 2026-09-13T18:35:12.888910Z 1 [System] [MY-013577] [InnoDB] InnoDB initialization has ended.
app-mysql-db  | 2026-09-13T18:35:13.188008Z 0 [Warning] [MY-010068] [Server] CA certificate ca.pem is self signed.
app-mysql-db  | 2026-09-13T18:35:13.188039Z 0 [System] [MY-013602] [Server] Channel mysql_main configured to support TLS. Encrypted connections are now supported for this channel.
app-mysql-db  | 2026-09-13T18:35:13.192991Z 0 [Warning] [MY-011810] [Server] Insecure configuration for --pid-file: Location '/var/run/mysqld' in the path is accessible to all OS users. Consider choosing a different directory.
app-mysql-db  | 2026-09-13T18:35:13.204789Z 0 [System] [MY-011323] [Server] X Plugin ready for connections. Socket: /var/run/mysqld/mysqlx.sock
app-mysql-db  | 2026-09-13T18:35:13.204818Z 0 [System] [MY-010931] [Server] /usr/sbin/mysqld: ready for connections. Version: '8.0.46'  socket: '/var/run/mysqld/mysqld.sock'  port: 0  MySQL Community Server - GPL.
app-mysql-db  | 2026-09-13 18:35:13+00:00 [Note] [Entrypoint]: Temporary server started.
app-mysql-db  | '/var/lib/mysql/mysql.sock' -> '/var/run/mysqld/mysqld.sock'
app-mysql-db  | Warning: Unable to load '/usr/share/zoneinfo/iso3166.tab' as time zone. Skipping it.
app-mysql-db  | Warning: Unable to load '/usr/share/zoneinfo/leap-seconds.list' as time zone. Skipping it.
app-mysql-db  | Warning: Unable to load '/usr/share/zoneinfo/leapseconds' as time zone. Skipping it.
app-mysql-db  | Warning: Unable to load '/usr/share/zoneinfo/tzdata.zi' as time zone. Skipping it.
app-mysql-db  | Warning: Unable to load '/usr/share/zoneinfo/zone.tab' as time zone. Skipping it.
app-mysql-db  | Warning: Unable to load '/usr/share/zoneinfo/zone1970.tab' as time zone. Skipping it.
app-mysql-db  | 2026-09-13 18:35:14+00:00 [Note] [Entrypoint]: Creating database my_database
app-mysql-db  | 2026-09-13 18:35:14+00:00 [Note] [Entrypoint]: Creating user springuser
app-mysql-db  | 2026-09-13 18:35:14+00:00 [Note] [Entrypoint]: Giving user springuser access to schema my_database
app-mysql-db  | 
app-mysql-db  | 2026-09-13 18:35:14+00:00 [Note] [Entrypoint]: Stopping temporary server
app-mysql-db  | 2026-09-13T18:35:14.345847Z 13 [System] [MY-013172] [Server] Received SHUTDOWN from user root. Shutting down mysqld (Version: 8.0.46).
app-mysql-db  | 2026-09-13T18:35:16.618679Z 0 [System] [MY-010910] [Server] /usr/sbin/mysqld: Shutdown complete (mysqld 8.0.46)  MySQL Community Server - GPL.
app-mysql-db  | 2026-09-13 18:35:17+00:00 [Note] [Entrypoint]: Temporary server stopped
app-mysql-db  | 
app-mysql-db  | 2026-09-13 18:35:17+00:00 [Note] [Entrypoint]: MySQL init process done. Ready for start up.
app-mysql-db  | 
app-mysql-db  | 2026-09-13T18:35:17.509884Z 0 [Warning] [MY-011068] [Server] The syntax '--skip-host-cache' is deprecated and will be removed in a future release. Please use SET GLOBAL host_cache_size=0 instead.
app-mysql-db  | 2026-09-13T18:35:17.510626Z 0 [System] [MY-010116] [Server] /usr/sbin/mysqld (mysqld 8.0.46) starting as process 1
app-mysql-db  | 2026-09-13T18:35:17.517441Z 1 [System] [MY-013576] [InnoDB] InnoDB initialization has started.
app-mysql-db  | 2026-09-13T18:35:17.727058Z 1 [System] [MY-013577] [InnoDB] InnoDB initialization has ended.
app-mysql-db  | 2026-09-13T18:35:17.977346Z 0 [Warning] [MY-010068] [Server] CA certificate ca.pem is self signed.
app-mysql-db  | 2026-09-13T18:35:17.977386Z 0 [System] [MY-013602] [Server] Channel mysql_main configured to support TLS. Encrypted connections are now supported for this channel.
app-mysql-db  | 2026-09-13T18:35:17.983681Z 0 [Warning] [MY-011810] [Server] Insecure configuration for --pid-file: Location '/var/run/mysqld' in the path is accessible to all OS users. Consider choosing a different directory.
app-mysql-db  | 2026-09-13T18:35:18.000384Z 0 [System] [MY-011323] [Server] X Plugin ready for connections. Bind-address: '::' port: 33060, socket: /var/run/mysqld/mysqlx.sock
app-mysql-db  | 2026-09-13T18:35:18.000452Z 0 [System] [MY-010931] [Server] /usr/sbin/mysqld: ready for connections. Version: '8.0.46'  socket: '/var/run/mysqld/mysqld.sock'  port: 3306  MySQL Community Server - GPL.
Container app-mysql-db Healthy 
app-springboot-backend  | 
app-springboot-backend  |   .   ____          _            __ _ _
app-springboot-backend  |  /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
app-springboot-backend  | ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
app-springboot-backend  |  \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
app-springboot-backend  |   '  |____| .__|_| |_|_| |_\__, | / / / /
app-springboot-backend  |  =========|_|==============|___/=/_/_/_/
app-springboot-backend  | 
app-springboot-backend  |  :: Spring Boot ::                (v4.1.1)
app-springboot-backend  | 
app-springboot-backend  | 2026-09-13T18:35:20.772Z  INFO 1 --- [demo] [           main] com.example.demo.DemoApplication         : Starting DemoApplication v0.0.1-SNAPSHOT using Java 17.0.20 with PID 1 (/app/app.jar started by root in /app)
app-springboot-backend  | 2026-09-13T18:35:20.776Z  INFO 1 --- [demo] [           main] com.example.demo.DemoApplication         : No active profile set, falling back to 1 default profile: "default"
app-springboot-backend  | 2026-09-13T18:35:21.243Z  INFO 1 --- [demo] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data JPA repositories in DEFAULT mode.
app-springboot-backend  | 2026-09-13T18:35:21.259Z  INFO 1 --- [demo] [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 10 ms. Found 0 JPA repository interfaces.
app-springboot-backend  | 2026-09-13T18:35:21.597Z  INFO 1 --- [demo] [           main] o.s.boot.tomcat.TomcatWebServer          : Tomcat initialized with port 8080 (http)
app-springboot-backend  | 2026-09-13T18:35:21.615Z  INFO 1 --- [demo] [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
app-springboot-backend  | 2026-09-13T18:35:21.616Z  INFO 1 --- [demo] [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/11.0.24]
app-springboot-backend  | 2026-09-13T18:35:21.652Z  INFO 1 --- [demo] [           main] b.w.c.s.WebApplicationContextInitializer : Root WebApplicationContext: initialization completed in 820 ms
app-springboot-backend  | 2026-09-13T18:35:21.855Z  INFO 1 --- [demo] [           main] org.hibernate.orm.jpa                    : HHH008540: Processing PersistenceUnitInfo [name: default]
app-springboot-backend  | 2026-09-13T18:35:21.915Z  INFO 1 --- [demo] [           main] org.hibernate.orm.core                   : HHH000001: Hibernate ORM core version 7.4.5.Final
app-springboot-backend  | 2026-09-13T18:35:22.291Z  INFO 1 --- [demo] [           main] o.s.o.j.p.SpringPersistenceUnitInfo      : No LoadTimeWeaver setup: ignoring JPA class transformer
app-springboot-backend  | 2026-09-13T18:35:22.323Z  INFO 1 --- [demo] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
app-springboot-backend  | 2026-09-13T18:35:22.478Z  INFO 1 --- [demo] [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection com.mysql.cj.jdbc.ConnectionImpl@503556cb
app-springboot-backend  | 2026-09-13T18:35:22.480Z  INFO 1 --- [demo] [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
app-springboot-backend  | 2026-09-13T18:35:22.564Z  INFO 1 --- [demo] [           main] org.hibernate.orm.connections.pooling    : HHH10001005: Database info:
app-springboot-backend  | 	Database JDBC URL [jdbc:mysql://database:3306/my_database?allowPublicKeyRetrieval=true&useSSL=false]
app-springboot-backend  | 	Database driver: MySQL Connector/J
app-springboot-backend  | 	Database dialect: MySQLDialect
app-springboot-backend  | 	Database version: 8.0.46
app-springboot-backend  | 	Default catalog/schema: my_database/undefined
app-springboot-backend  | 	Autocommit mode: undefined/unknown
app-springboot-backend  | 	Isolation level: REPEATABLE_READ [default REPEATABLE_READ]
app-springboot-backend  | 	JDBC fetch size: none
app-springboot-backend  | 	Pool: DataSourceConnectionProvider
app-springboot-backend  | 	Minimum pool size: undefined/unknown
app-springboot-backend  | 	Maximum pool size: undefined/unknown
app-springboot-backend  | 2026-09-13T18:35:22.775Z  INFO 1 --- [demo] [           main] org.hibernate.orm.core                   : HHH000489: No JTA platform available (set 'hibernate.transaction.jta.platform' to enable JTA platform integration)
app-springboot-backend  | 2026-09-13T18:35:22.806Z  INFO 1 --- [demo] [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'
app-springboot-backend  | 2026-09-13T18:35:22.840Z  WARN 1 --- [demo] [           main] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
app-springboot-backend  | 2026-09-13T18:35:23.080Z  INFO 1 --- [demo] [           main] o.s.boot.tomcat.TomcatWebServer          : Tomcat started on port 8080 (http) with context path '/'
app-springboot-backend  | 2026-09-13T18:35:23.091Z  INFO 1 --- [demo] [           main] com.example.demo.DemoApplication         : Started DemoApplication in 2.722 seconds (process running for 3.035)
app-springboot-backend  | 2026-09-13T18:35:40.715Z  INFO 1 --- [demo] [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
app-springboot-backend  | 2026-09-13T18:35:40.715Z  INFO 1 --- [demo] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
app-springboot-backend  | 2026-09-13T18:35:40.716Z  INFO 1 --- [demo] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms

# Shutdown

[user@localhost code]$ docker-compose down
[+] down 3/3
 ✔ Container app-springboot-backend Removed                                                                                                                                   0.2s
 ✔ Container app-mysql-db           Removed                                                                                                                                   1.1s
 ✔ Network code_default             Removed     
