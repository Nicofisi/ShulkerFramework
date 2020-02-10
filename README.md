# ShulkerFramework ![Travis Shield](https://img.shields.io/travis/Nicofisi/ShulkerFramework) ![License Badge](https://img.shields.io/github/license/Nicofisi/ShulkerFramework) ![Code Size Badge](https://img.shields.io/github/languages/code-size/Nicofisi/ShulkerFramework)
The project is not yet ready to be used in production,
although I plan to keep updating and expanding it,
since I need it for my server.

There's a possibility I will eventually
write some plugins for the public using this framework - 
I keep this option in mind while developing. Due to this reason
I'm working on adding features such as support for multiple
languages, which I don't need myself.

Have a great day, lost reader, and maybe come back later,
when there's actually something to see here!

## Plugins using this framework
- Pickaxe - a closed source plugin for my server.
- *Undisclosed* - an unreleased, unannounced plugin that I'm working on,
intended for my server and for the public.
- [FallSuicidePreventer](https://www.spigotmc.org/resources/fallsuicidepreventer.61230/) -
a random single-day project created to test an early
version of the framework in action.

## Artifacts

##### Gradle Groovy DSL

```gradle
dependencies {
    implementation 'me.nicofisi:shulker-framework:VERSION'
}

repositories {
    maven {
        name 'ShulkerFramework-GitHub-Packages'
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
        name = "ShulkerFramework-GitHub-Packages"
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
