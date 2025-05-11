
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