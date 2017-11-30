import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.junit.platform.gradle.plugin.EnginesExtension
import org.junit.platform.gradle.plugin.FiltersExtension
import org.junit.platform.gradle.plugin.JUnitPlatformExtension

version = "1.0-SNAPSHOT"

buildscript {
	var kotlin_version: String by extra
	kotlin_version = "1.2.0"

	repositories {
		mavenCentral()
	}

	dependencies {
		classpath(kotlinModule("gradle-plugin", kotlin_version))
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.2")
	}

}

apply {
    plugin("org.junit.platform.gradle.plugin")
	plugin("kotlin")
}

val kotlin_version: String by extra

repositories {
	mavenCentral()
    jcenter()
}

configure<JUnitPlatformExtension> {
    filters {
        engines {
            include("spek")
        }
    }
}

dependencies {
	compile(kotlinModule("stdlib-jdk8", kotlin_version))
    testRuntime(kotlinModule("runtime", kotlin_version))
	testCompile(kotlinModule("test", kotlin_version))
    testCompile(kotlinModule("reflect", kotlin_version))

    testRuntime("org.junit.platform:junit-platform-launcher:1.0.2")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.0.2")

    testCompile ("org.jetbrains.spek:spek-api:1.1.5") {
        exclude(group="org.jetbrains.kotlin")
    }
    testRuntime ("org.jetbrains.spek:spek-junit-platform-engine:1.1.5") {
        exclude(group="org.junit.platform")
        exclude(group="org.jetbrains.kotlin")
    }

    // kluent things
    testCompile("junit", "junit", "4.12")
    testCompile("com.nhaarman", "mockito-kotlin-kt1.1", "1.5.+")
    testCompile("org.amshove.kluent:kluent:1.31")
}

configure<JavaPluginConvention> {
	sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "1.8"
}

// extension for configuration
fun JUnitPlatformExtension.filters(setup: FiltersExtension.() -> Unit) {
    when (this) {
        is ExtensionAware -> extensions.getByType(FiltersExtension::class.java).setup()
        else -> throw Exception("${this::class} must be an instance of ExtensionAware")
    }
}
fun FiltersExtension.engines(setup: EnginesExtension.() -> Unit) {
    when (this) {
        is ExtensionAware -> extensions.getByType(EnginesExtension::class.java).setup()
        else -> throw Exception("${this::class} must be an instance of ExtensionAware")
    }
}
