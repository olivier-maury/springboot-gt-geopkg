# Test gt-geopkg with Spring Boot 3 dependencies

Test [`gt-geopkg`](https://docs.geotools.org/stable/userguide/library/data/geopackage.html) from [GeoTools](http://geotools.org/) with Spring Boot 3 dependencies.

The main class `fr.agroclim.test.gtgeopkg.Main` works well without `spring-boot-dependencies` (ignore WARNING from Maven):

```sh
mvn compile exec:java
```

## Spring Boot 3.1.12

```sh
mvn compile exec:java -Pspringboot -Dspringboot.version=3.1.12
```

OK

## Spring Boot 3.2.7

```sh
mvn compile exec:java -Pspringboot -Dspringboot.version=3.2.7
```

```
Caused by: java.sql.SQLFeatureNotSupportedException: not implemented by SQLite JDBC driver
	at org.sqlite.jdbc3.JDBC3PreparedStatement.unsupported(JDBC3PreparedStatement.java:448)
	at org.sqlite.jdbc3.JDBC3Statement.getGeneratedKeys(JDBC3Statement.java:361)
	at org.apache.commons.dbcp.DelegatingStatement.getGeneratedKeys(DelegatingStatement.java:315)
	at org.apache.commons.dbcp.DelegatingStatement.getGeneratedKeys(DelegatingStatement.java:315)
	at org.geotools.jdbc.KeysFetcher$FromDB.postInsert(KeysFetcher.java:287)
	at org.geotools.jdbc.JDBCDataStore.insertPS(JDBCDataStore.java:1955)
	at org.geotools.jdbc.JDBCDataStore.insert(JDBCDataStore.java:1864)
```

## Spring Boot 3.3.1

```sh
mvn compile exec:java -Pspringboot -Dspringboot.version=3.3.1
```

```
Caused by: java.lang.NullPointerException: fid must not be null
	at org.geotools.filter.identity.FeatureIdImpl.setID(FeatureIdImpl.java:55)
	at org.geotools.jdbc.JDBCFeatureReader$ResultSetFeature.setID(JDBCFeatureReader.java:704)
	at org.geotools.jdbc.JDBCInsertFeatureWriter.flush(JDBCInsertFeatureWriter.java:135)
	at org.geotools.jdbc.JDBCInsertFeatureWriter.write(JDBCInsertFeatureWriter.java:102)
	at org.geotools.data.InProcessLockingManager$1.write(InProcessLockingManager.java:308)
	at org.geotools.geopkg.Features$2.write(Features.java:87)
	at org.geotools.geopkg.GeoPackage.add(GeoPackage.java:937)
```

# Fix

To fix this, simply explicitly define version of `sqlite-jdbc` used by [gt-geopkg-31.2](https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc/3.41.2.2):

```xml
    <dependency>
      <groupId>org.xerial</groupId>
      <artifactId>sqlite-jdbc</artifactId>
      <version>3.41.2.2</version>
    </dependency>
```

This issue was sent to GeoTools : <https://osgeo-org.atlassian.net/browse/GEOT-7613>.