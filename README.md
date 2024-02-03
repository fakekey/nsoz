# Ninja School Online

> **Note:** This project coding by **[VSCode](https://code.visualstudio.com/)**. So I recommend using **[VSCode](https://code.visualstudio.com/)** too.

## Environment

> **All Programs** that necessary for building this project.

- **_[Java Development Kit 17](https://www.oracle.com/java/technologies/downloads/#java17/)_** **_(for coding)_**
- **_[Docker](https://www.docker.com/)_** **_(for databases)_**
- **_[Apache Maven](https://maven.apache.org/download.cgi)_** **_(for packaging)_**

## Databases

> **This project** using **MariaDB** for storing **game data** and **MongoDB** for storing **ingame transaction** history.

> **Step 1:** Start your docker engine.

```shell
docker compose up -d
```

> **Step 2:** Create database, go to 127.0.0.1 (localhost). You could modify default port by editing [docker-compose.yaml](docker-compose.yaml). Then import [nsoz.sql](nsoz.sql) file.

## Building

```shell
mvn clean -f pom.xml
```

```shell
mvn package -f pom.xml
```

## Running

```shell
java -server -jar -Dfile.encoding=UTF-8 -Xms1024M -Xmx2048M target/nsoz-jar-with-dependencies.jar
```

> Or simply execute **[run.bat](run.bat)** file with your any shell.

## Debugging

> **Note:** Use **[VSCode](https://code.visualstudio.com/)** for debugging. Simply open **this project** with **[VSCode](https://code.visualstudio.com/)**, install **[Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)** extension.

> **Press F5** to start debugging.

## Cleaning

```shell
mvn clean -f pom.xml
```

```shell
docker compose down
```
