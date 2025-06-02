// The code in this file is a convention plugin - a Gradle mechanism for sharing reusable build logic.
// `buildSrc` is a Gradle-recognized directory and every plugin there will be easily available in the rest of the build.

import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin in JVM projects.
    kotlin("jvm")
    id("com.vanniktech.maven.publish")
}

tasks.withType<Test>().configureEach {
    // Configure all test Gradle tasks to use JUnitPlatform.
    useJUnitPlatform()

    // Log information about all test results, not only the failed ones.
    testLogging {
        events(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED
        )
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(MavenPublishConfig.GROUP_ID, project.name, MavenPublishConfig.VERSION)

    configure(KotlinJvm(
        // configures the -javadoc artifact, possible values:
        // - `JavadocJar.None()` don't publish this artifact
        // - `JavadocJar.Empty()` publish an emprt jar
        // - `JavadocJar.Dokka("dokkaHtml")` when using Kotlin with Dokka, where `dokkaHtml` is the name of the Dokka task that should be used as input
//        JavadocJar.None(),
//        javadocJar = JavadocJar.Dokka("dokkaHtml"),
        // whether to publish a sources jar
        sourcesJar = true,
    ))

    pom {
        name = "morecup"
        description = "更好的ddd框架"
        url = "https://github.com/morecup/better-ddd"
        licenses {
            license {
                name = "Apache-2.0"
                url = "https://spdx.org/licenses/Apache-2.0.html"
            }
        }

        developers {
            developer {
                id = "morecup/jimmer"
                name = "morecup"
                url = "https://github.com/morecup"
            }
        }
        scm {
            url = "https://github.com/morecup/jimmer"
            connection = "scm:git:git@github.com:morecup/jimmer.git"
            developerConnection = "scm:git:ssh://git@github.com:morecup/jimmer.git"
        }
    }
}