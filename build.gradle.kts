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
    mainClass.set("Day20Kt")
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}
