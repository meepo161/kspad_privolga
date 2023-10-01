import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.nio.file.Paths

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version ("1.5.1")
}

group = "ru.avem"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")

    flatDir {
        dir("libs")
    }
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("cafe.adriel.voyager:voyager-navigator:1.0.0-rc07")
                implementation("cafe.adriel.voyager:voyager-tab-navigator:1.0.0-rc07")
                implementation("cafe.adriel.voyager:voyager-transitions-desktop:1.0.0-rc07")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")

                implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")

                implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.2.2")

                implementation("org.jetbrains.exposed:exposed:0.17.14")
                implementation("org.xerial:sqlite-jdbc:3.39.3.0")

                implementation("org.apache.poi:poi:5.0.0")
                implementation("org.apache.poi:poi-ooxml:5.0.0")

                implementation("io.github.microutils:kotlin-logging:1.8.3")
                implementation("org.slf4j:slf4j-api:1.7.25")
                implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.9.1")
                implementation("org.apache.logging.log4j:log4j-api:2.9.1")
                implementation("org.apache.logging.log4j:log4j-core:2.9.1")

                implementation("com.fazecast:jSerialComm:2.9.2")
                implementation(":kserialpooler-2.0")
                implementation(":polling-essentials-2.0")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    var version = ""
    val file = Paths.get("version.txt")
    file.toFile().writeText((file.toFile().readText().toInt() + 1).toString())
    version = "1.0." + file.toFile().readText()
    application {
        mainClass = "ru.avem.stand.EntryPointKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "kspad_privolga"
            packageVersion = version
            includeAllModules = true
//            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))
        }
    }
}
