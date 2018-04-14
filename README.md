# EmerGen

[![Build Status](https://travis-ci.org/qaware/emergen.svg?branch=master)](https://travis-ci.org/qaware/emergen)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=de.qaware.emergen%3Aemergen&metric=alert_status)](https://sonarcloud.io/dashboard?id=de.qaware.emergen%3Aemergen)
[![License](http://img.shields.io/badge/license-MIT-green.svg?style=flat)]()

An emergent design generator framework based on Java APT. The `emergen-core` provides
base classes to work with APT and different templates engines so you can implement
your own custom annotation processors easily. The `emergen-freemarker` and `emergen-velocity`
modules provide service provider implementations for two popular open source template
engines you could use to generate your sources. The `emergen-processors` module contains the 
actual APT implementations. Use these as a reference for your own implementations.

The idea behind emergent design is simple: as soon as your design and architectures evolves you
will eventually discover patterns and probably boiler plate code. It is exactly this boiler plate
code you may want to generated elegantly using APT and EmerGen. All you need is a few annotations
which you usually have anyway because you may have used Spring or CDI, and a small annotation
processor implementation. Use `emergen-core` and a template engine of your liking for that. Done!

## Usage

### Builder Support

This annotation processor generates builder implementations for your ordinary POJOs. First, you need to
add the following dependency to your `build.gradle` file:

```groovy
dependencies {
    compileOnly 'de.qaware.emergen.apt:emergen-apt-builder:1.0.0'
}
```

In case you use Maven as your preferred build tool, add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>de.qaware.emergen.apt</groupId>
    <artifactId>emergen-apt-builder</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

Next, all your POJOs you want EmerGen to generate a builder for need to be annotated with `de.qaware.emergen.apt.builder.BuilderSupport`. Per default, all fields
of the POJO will be included by the builder. You may additionally use the `de.qaware.emergen.apt.builder.BuilderProperty` annotation on each field to modify the
default generator behaviour.

```java
@BuilderSupport
public class ExamplePojo {

    @BuilderProperty(prefix = "say", propertyAccess = BuilderProperty.AccessStrategy.CONSTRUCTOR)
    private final String hello;

    @BuilderProperty(include = false)
    private String ignored;

    @BuilderProperty(defaultValue = "4711", propertyAccess = BuilderProperty.AccessStrategy.DIRECT)
    public int counter;
    
    public ExamplePojo(String hello) {
        this.hello = hello;
    }
    
    // getters and setters omitted
}
```

### Service Loader Support

This annotation processor generates Java SE service loader files for your implementations. First, you need to
add the following dependency to your `build.gradle` file:

```groovy
dependencies {
    compileOnly 'de.qaware.emergen.apt:emergen-apt-loader:1.0.0'
}
```

In case you use Maven as your preferred build tool, add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>de.qaware.emergen.apt</groupId>
    <artifactId>emergen-apt-loader</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

Next, you need to annotate you implementation classes using `de.qaware.emergen.apt.loader.ServiceLoaderSupport`. That's it.
The processor will generate a service loader descriptor file for each implemented interface.

```java
@ServiceLoaderSupport
public class ExampleImplementation implements some.ExampleInterface {
    // body omitted
}
```

This will produce a `META-INF/services/some.ExampleInterface` file. You may now use the Java SE
ServiceLoader to obtain an instance of you service.

```java
ServiceLoader<ExampleInterface> loader = ServiceLoader.load(ExampleInterface.class);
ExampleInterface instance = loader.iterator().next();
```

### Design Enforcer

This annotation processor does not generate anything! Instead it can enforce simple design
rules that exist in the architecture of your system. In case the rules you established are
violated, this processor will break your build and your code won't compile.

First, you need to add the following dependency to your `build.gradle` file:

```groovy
dependencies {
    compileOnly 'de.qaware.emergen.apt:emergen-apt-enforcer:1.0.0'
}
```

In case you use Maven as your preferred build tool, add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>de.qaware.emergen.apt</groupId>
    <artifactId>emergen-apt-enforcer</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

The exact behaviour, meaning the set of annotations to process as well as the rules file
can be set using annotation processor options. Currently the following options are supported:

Option Name  | Description
--- | ---
enforcer.annotations | A comma separated list of fully qualified class names of annotations. To process all annotations in a package use some.pkg.* or even * for all annotations.
enforcer.rules | A JavaScript file that contains the rules to check.

To set these options using Gradle use something like the following:
```groovy
tasks.getByName(sourceSets.main.compileJavaTaskName) {
    options.compilerArgs += ['-Aenforcer.rules=custom.js']
}
```

With Maven you have to configure the `maven-compiler-plugin` accordingly, like
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.7.0</version>
    <configuration>
        <compilerArgs>
          <arg>-Aenforcer.annotations=de.qaware.emergen.apt.enforcer.EnforcerSupport</arg>
          <arg>-Aenforcer.rules=custom.js</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

The rules are expressed in JavaScript. Refer to https://www.n-k.de/riding-the-nashorn/ for information on the
specifics of the Nashorn Engine. The basic structure of the enforcement rules looks like this:

```javascript
/**
 * Design enforcement function. Checks the current element and annotation.
 * 
 * @param {javax.lang.model.element.TypeElement} annotation 
 *   the currently processed annotation
 * 
 * @param {javax.lang.model.element.Element} element 
 *   the annotated element currently being processed
 *   
 * @return true of rules are OK or false if rules are violated
 */
var enforce = function (annotation, element) {
    return true;
};
```


## Other References

- https://projectlombok.org
- http://mapstruct.org
- https://github.com/mapstruct/mapstruct
- https://github.com/google/compile-testing
- https://github.com/MatthewTamlin/Avatar
- https://medium.com/inloopx/weapons-for-boilerplate-destruction-pt-3-testing-the-annotation-processor-f486fb19f5b1
- https://eng.uber.com/rave/
- https://github.com/uber-common/rave
- https://www.logicbig.com/tutorials/core-java-tutorial/java-se-annotation-processing-api/annotation-processor-validation.html

## Maintainer

Mario-Leander Reimer, <mario-leander.reimer@qaware.de>.

## License

This software is provided under the MIT open source license, read the `LICENSE` file for details.
