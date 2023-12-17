plugins {
    kotlin("jvm") version "1.9.20"
    application
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

application {
    mainClass.set("Day17Kt")
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}
