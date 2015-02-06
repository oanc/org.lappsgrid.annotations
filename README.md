org.lappsgrid.annotations
=========================

### Build Status

[![Master Status](http://grid.anc.org:9080/travis/svg/oanc/org.lappsgrid.annotations?branch=master)](https://travis-ci.org/oanc/org.lappsgrid.annotations)
[![Develop Status](http://grid.anc.org:9080/travis/svg/oanc/org.lappsgrid.annotations?branch=develop)](https://travis-ci.org/oanc/org.lappsgrid.annotations)


Java annotations that can be applied to services to automatically generate JSON metadata files.

<b color='red'>Note:</b> currently these classes reside in the package 
`org.lappsgrid.experimental.annotations`. They will be
moved into the `org.lappsgrid.annotations` package after review.

## Maven

```xml
<groupId>org.lappsgrid.experimental</groupId>
<artifactId>annotations</artifactId>
<version>1.0.0</version>
```

## Annotations

There are three annotation types that can be used to generate JSON metadata.

1. **org.lappsgrid.experimental.annotations.CommonMetadata**<br/>
Use this on super classes to provide common metadata for all sub-classes. This annotation
only works in conjunction with the `@ServiceMetadata` annotation.
1. **org.lappsgrid.experimental.annotations.ServiceMetadata**<br/>
Used to declare metadata for `org.lappsgrid.api.WebService` objects.
1. **org.lappsgrid.experimental.annotations.DataSourceMetadata**<br/>
Used to declare metadata for `org.lappsgrid.api.DataSource` objects.

## Examples

```java

@CommonMetadata(
    vendor="http://www.anc.org",
    license="apache2",
    format="gate",
    encoding="UTF-8"
)
public abstract class ParentClass implements WebService { ... }

@ServiceMetadata(
    requires = "token",
    produces = "sentence"
)
public class ServiceClass extends ParentClass { ... }
```
Note that you can use a discriminator's short name as defined in
http://vocab.lappsgrid.org/discriminators.html any time a discriminator
URI is required.

## Generated Files

To generate the JSON metadata files the Java compiler must be configured to use the
LAPPS annotation processor.

```xml
<plugins>
    <plugin>
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
            <annotationProcessors>
                <annotationProcessor>org.lappsgrid.experimental.annotation.processing.MetadataProcessor</annotationProcessor>
            </annotationProcessors>
        </configuration>
    </plugin>
    ...
</plugins>
<dependencies>
    <dependency>
        <groupId>org.lappsgrid.experimental</groupId>
        <artifactId>annotations</artifactId>
        <version>${lapps.annotation.version}</version>
    </dependency>
</dependencies>
```

The generated JSON files will be created in `src/main/resources/metadata` and will use
the fully qualified class name of the service class. For example, if the above `SeviceClass`
is in the `org.anc.examples` package the name of the generated metadata file will be
`org.anc.examples.ServiceClass.json`.

## Tips and Troubleshooting

### Cleaning up

Since the metadata files are automatically generated during the `compile`
 phase it makes sense to delete them during the `clean` phase. To do this 
 we need to configure the `maven-clean-plugin` to delete the `metadata`
 directory.
 
```xml
    <plugin>
        <artifactId>maven-clean-plugin</artifactId>
        <configuration>
            <filesets>
                <fileset>
                    <directory>src/main/resources/metadata</directory>
                </fileset>
            </filesets>
        </configuration>
    </plugin>
```
