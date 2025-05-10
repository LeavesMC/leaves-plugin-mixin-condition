
plugins {
    java
    `maven-publish`
}

group = "org.leavesmc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://modmaven.dev")
}

dependencies {
    compileOnly("org.jetbrains:annotations:26.0.2")
    compileOnly("net.fabricmc:sponge-mixin:0.15.5+mixin.0.8.7")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}