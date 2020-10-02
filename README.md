# nimbus-base64-benchmark
This repo is to evaluate performance of Base64 Decoder used in [nimbus-jose-jwt](https://bitbucket.org/connect2id/nimbus-jose-jwt/).

[This article](http://java-performance.info/base64-encoding-and-decoding-performance) is really well-written
to get more detail of java's base64 libraries.


## How to Build&Run
This benchmark uses [jmh](https://openjdk.java.net/projects/code-tools/jmh/).
```bash
$ mvn clean package
$ java -jar target/benchmark.jar
```

## Target Libraries
- java8 (`java.util.Base64`)
- nimbus Base64 (`com.nimbusds.jose.util.Base64`)
- nimbus Base64Codec (`com.nimbusds.jose.util.Base64Codec`)
- guava (`com.google.common.io.BaseEncoding`)
- commons-codec (`org.apache.commons.codec.binary.Base64`)

## Data Set
- small: randomly generated strings of length 100 
- medium: randomly generated strings of length 1,000
- large: randomly generated strings of length 10,000

Each data set has 10,000 strings.


## Test Environment
- JDK: amazon-corretto-8.jdk
- machine: 
  - instance type: c5.large
  - os: Ubuntu 18.04.5 LTS

## 

  

