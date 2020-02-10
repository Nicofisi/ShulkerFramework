import groovy.util.NodeList
import groovy.util.Node

plugins {
    kotlin("jvm") version "1.3.70-eap-42"
    `maven-publish`
}

group = "me.nicofisi"
version = "0.7"

repositories {
    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
    mavenCentral()
    jcenter()

    maven {
        setUrl("https://dl.bintray.com/arrow-kt/arrow-kt/")
    }

    maven {
        name = "paperspigot-repo"
        setUrl("https://repo.destroystokyo.com/repository/maven-public/")
    }

    maven {
        name = "bungeecord-chat-repo"
        setUrl("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.destroystokyo.paper:paper-api:1.15.2-R0.1-SNAPSHOT")
    implementation("net.md-5:bungeecord-api:1.15-SNAPSHOT")
    implementation("io.arrow-kt:arrow-core:0.10.4")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    publishToMavenLocal {
        dependsOn(build)
    }
}

publishing {
    repositories {
        maven {
            name = "ShulkerFramework GitHub Packages"
            url = uri("https://maven.pkg.github.com/nicofisi/shulkerframework")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

//            pom.withXml {
//                project.configurations.compileOnly.get().allDependencies.forEach { dep ->
//                    ((asNode()["dependencies"] as NodeList)[0] as Node).appendNode("dependency").let {
//                        it.appendNode("groupId", dep.group)
//                        it.appendNode("artifactId", dep.name)
//                        it.appendNode("version", dep.version)
//                        it.appendNode("scope", "provided")
//                    }
//            }

            pom.withXml {
                ((asNode()["dependencies"] as NodeList)[0] as Node).children().forEach {
                    if (it is Node) {
                        ((it["scope"] as NodeList?)?.get(0) as Node).setValue("compile")
                    }
                }
            }
        }
    }
}
