plugins {
    java
    `maven-publish`
}

group = "org.leavesmc"
version = "1.0-SNAPSHOT"

val asmVersion = "9.8"
val jbAnnotationVersion = "26.0.2"
val mixinVersion = "0.15.5+mixin.0.8.7"
val junitVersion = "5.10.0"

repositories {
    mavenCentral()
    maven("https://modmaven.dev")
}

dependencies {
    compileOnly("net.fabricmc:sponge-mixin:$mixinVersion") {
        exclude(group = "org.ow2.asm")
    }
    compileOnly("org.ow2.asm:asm-tree:$asmVersion")
    compileOnly("org.jetbrains:annotations:$jbAnnotationVersion")
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("net.fabricmc:sponge-mixin:$mixinVersion") {
        exclude(group = "org.ow2.asm")
    }
    testImplementation("org.ow2.asm:asm-tree:$asmVersion")
    testImplementation("org.jetbrains:annotations:$jbAnnotationVersion")
}

val sourceJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val isSnapshot = project.version.toString().endsWith("-SNAPSHOT")

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
            artifact(sourceJar)
            withoutBuildIdentifier()

            pom {
                val repoPath = "LeavesMC/leaves-plugin-mixin-condition"
                val repoUrl = "https://github.com/$repoPath"

                name.set("leaves-plugin-mixin-condition")
                description.set(project.description)
                url.set(repoUrl)
                packaging = "jar"

                licenses {
                    license {
                        name.set("MIT")
                        url.set("$repoUrl/blob/master/LICENSE")
                        distribution.set("repo")
                    }
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("$repoUrl/issues")
                }

                developers {
                    developer {
                        id.set("MC_XiaoHei")
                        name.set("MC_XiaoHei")
                        email.set("xor7xiaohei@gmail.com")
                        url.set("https://github.com/MC_XiaoHei")
                    }
                }

                scm {
                    url.set(repoUrl)
                    connection.set("scm:git:$repoUrl.git")
                    developerConnection.set("scm:git:git@github.com:$repoPath.git")
                }
            }
        }

        repositories {
            val url = if (isSnapshot) {
                "https://repo.leavesmc.org/snapshots"
            } else {
                "https://repo.leavesmc.org/releases"
            }

            maven(url) {
                name = "leaves"
                credentials(PasswordCredentials::class) {
                    username = System.getenv("LEAVES_USERNAME")
                    password = System.getenv("LEAVES_PASSWORD")
                }
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}