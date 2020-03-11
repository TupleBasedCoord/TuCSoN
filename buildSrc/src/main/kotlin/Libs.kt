import kotlin.String

/**
 * Generated by https://github.com/jmfayard/buildSrcVersions
 *
 * Update this file with
 *   `$ ./gradlew buildSrcVersions`
 */
object Libs {
    /**
     * https://junit.org/junit5/
     */
    const val junit_jupiter_api: String = "org.junit.jupiter:junit-jupiter-api:" +
            Versions.org_junit_jupiter

    /**
     * https://junit.org/junit5/
     */
    const val junit_jupiter_engine: String = "org.junit.jupiter:junit-jupiter-engine:" +
            Versions.org_junit_jupiter

    /**
     * http://www.slf4j.org
     */
    const val slf4j_api: String = "org.slf4j:slf4j-api:" + Versions.org_slf4j

    /**
     * http://www.slf4j.org
     */
    const val slf4j_jdk14: String = "org.slf4j:slf4j-jdk14:" + Versions.org_slf4j

    const val org_danilopianini_git_sensitive_semantic_versioning_gradle_plugin: String =
            "org.danilopianini.git-sensitive-semantic-versioning:org.danilopianini.git-sensitive-semantic-versioning.gradle.plugin:" +
            Versions.org_danilopianini_git_sensitive_semantic_versioning_gradle_plugin

    const val com_github_breadmoirai_github_release_gradle_plugin: String =
            "com.github.breadmoirai.github-release:com.github.breadmoirai.github-release.gradle.plugin:" +
            Versions.com_github_breadmoirai_github_release_gradle_plugin

    const val com_github_johnrengelman_shadow_gradle_plugin: String =
            "com.github.johnrengelman.shadow:com.github.johnrengelman.shadow.gradle.plugin:" +
            Versions.com_github_johnrengelman_shadow_gradle_plugin

    const val de_fayard_buildsrcversions_gradle_plugin: String =
            "de.fayard.buildSrcVersions:de.fayard.buildSrcVersions.gradle.plugin:" +
            Versions.de_fayard_buildsrcversions_gradle_plugin

    /**
     * http://tuprolog.unibo.it
     */
    const val tuprolog: String = "it.unibo.alice.tuprolog:tuprolog:" + Versions.tuprolog

    /**
     * http://junit.org
     */
    const val junit: String = "junit:junit:" + Versions.junit
}
