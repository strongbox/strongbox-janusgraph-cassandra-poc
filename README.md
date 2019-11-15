# strongbox-janusgraph-cassandra-poc

# Using Elasticsearch

Elasticsearch is a part of this POC, as it is used by Janusgraph to support Mixed Index Queries.  It is configured via
[elasticsearch-maven-plugin](https://github.com/alexcojocaru/elasticsearch-maven-plugin).  The plugin will automatically
start the configured Elasticsearch instance when integration tests are run.  However, you can also use it to run
Elasticsearch when running the application using `maven-sprint-boot-plugin`.

To start both Elasticsearch and the spring-boot app, do the following:

```
$ mvn elasticsearch:runforked spring-boot:run
```
