# picocli-freemarker

Small sample [PicoCLI](https://picocli.info) project that uses [Apache Freemarker](https://freemarker.apache.org).
## Compile

```shell
mvn -Pnative clean package -DskipTests  
```

## Run

```shell
target/picocli-freemarker scaffold newproj
```

## Cleanup

```shell
rm -rf newproj
```