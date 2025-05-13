# picocli-freemarker

Small sample [PicoCLI](https://picocli.info) project that uses [Apache Freemarker](https://freemarker.apache.org) built on Apple Silicon.

When run, it creates a folder with the given project name, adds a Maven `pom.xml` based on the Freemarker template and initialises a git repository.

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