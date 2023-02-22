import groovy.util.Node
import groovy.util.NodeList

plugins {
    kotlin("jvm") version "1.8.10"
    `maven-publish`
}

group = "me.nicofisi"
version = "0.13.11"

repositories {
    mavenCentral()
    jcenter()

    maven {
        name = "minecraft-libraries-repo"
        setUrl("https://libraries.minecraft.net")
    }

    maven {
        name = "arrow-kt-repo"
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

    maven {
        name = "protocollib-repo"
        setUrl("https://repo.dmulloy2.net/nexus/repository/public/")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.arrow-kt:arrow-core:0.10.4")

    compileOnly("com.destroystokyo.paper:paper-api:1.15.2-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-api:1.15-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.5.0")
//    compileOnly("com.mojang:brigadier:1.0.17")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "17"
    }
    publishToMavenLocal {
        dependsOn(build)
    }
}

publishing {
    repositories {
        maven {
            name = "ShulkerFramework-GitHub-Packages"
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
