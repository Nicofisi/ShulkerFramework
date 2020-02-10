### Artifacts

##### Gradle Groovy DSL

```gradle
dependencies {
    implementation 'me.nicofisi:shulker-framework:VERSION'
}

repositories {
    maven {
        name 'ShulkerFramework GitHub Packages'
        url 'https://maven.pkg.github.com/nicofisi/shulkerframework'
    }
}
```
##### Gradle Kotlin DSL

```gradle
dependencies {
    implementation("me.nicofisi:shulker-framework:VERSION")
}

repositories {
    maven {
        name = "ShulkerFramework GitHub Packages"
        url = uri("https://maven.pkg.github.com/nicofisi/shulkerframework")
    }
}
```

##### Maven

```maven
<dependency>
    <groupId>me.nicofisi</groupId>
    <artifactId>shulker-framework</artifactId>
    <version>VERSION</version>
</dependency>

<repository>
    <id>shulkerframework-github-packages</id>
    <name>ShulkerFramework GitHub Packages</name>
    <url>https://maven.pkg.github.com/nicofisi/shulkerframework</url>
</repository>
```
