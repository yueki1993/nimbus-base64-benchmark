# nimbus-base64-benchmark
This repo is to evaluate performance of Base64 Decoder used in [nimbus-jose-jwt](https://bitbucket.org/connect2id/nimbus-jose-jwt/).
This benchmark result implies that we can get better performance if we use java8's base64 decoder instead of nimbus'.
See [issue](https://bitbucket.org/connect2id/nimbus-jose-jwt/issues/380/switch-to-java8s-base64-decoder-for).

[This article](http://java-performance.info/base64-encoding-and-decoding-performance) is really well-written
to get more benchmarks of java's base64 libraries.

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
  - instance type: aws ec2 c5.large
  - os: Ubuntu 18.04.5 LTS

## Result
```
MyBenchmark.java8B64Decoder_small         thrpt    5  1299884.438 ±  5062.251  ops/s
MyBenchmark.guava_small                   thrpt    5  1096650.953 ± 10784.578  ops/s
MyBenchmark.nimbusB64DecoderCodec_small   thrpt    5   254416.835 ±  1665.612  ops/s
MyBenchmark.nimbusB64Decoder_small        thrpt    5   251448.108 ±   820.425  ops/s
MyBenchmark.commons_small                 thrpt    5   216649.714 ±   561.180  ops/s
```

```
MyBenchmark.java8B64Decoder_medium        thrpt    5   129123.483 ±   627.562  ops/s
MyBenchmark.guava_medium                  thrpt    5    99935.088 ±   267.320  ops/s
MyBenchmark.commons_medium                thrpt    5    37540.252 ±   120.918  ops/s
MyBenchmark.nimbusB64Decoder_medium       thrpt    5    25314.663 ±   101.593  ops/s
MyBenchmark.nimbusB64DecoderCodec_medium  thrpt    5    24507.906 ±   103.547  ops/s
```

```
MyBenchmark.java8B64Decoder_large         thrpt    5    14220.810 ±    71.518  ops/s
MyBenchmark.guava_large                   thrpt    5    11014.160 ±    51.124  ops/s
MyBenchmark.commons_large                 thrpt    5     4538.505 ±   119.857  ops/s
MyBenchmark.nimbusB64DecoderCodec_large   thrpt    5     2622.133 ±     8.770  ops/s
MyBenchmark.nimbusB64Decoder_large        thrpt    5     2554.354 ±     8.700  ops/s
```

  

