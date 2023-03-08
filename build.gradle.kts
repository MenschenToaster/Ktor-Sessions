plugins {
    kotlin("jvm") version "1.8.0"
    application
    id("maven-publish")
}

group = "at.toastiii"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-server-core:2.2.4")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.mongodb:mongodb-driver-sync:4.9.0")

}

configure<PublishingExtension> {
    repositories {
        maven {
            name = "cafestubeRepository"
            credentials(PasswordCredentials::class)
            url = uri("https://repo.cafestu.be/repository/maven-snapshots/")
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "$group"
            artifactId = "ktor-sessions"
            version = project.version.toString()
            from(components["kotlin"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}