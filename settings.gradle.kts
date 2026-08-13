import java.util.Properties

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

/**
 * Token do GitHub para o repositório Maven do Meta DAT.
 *
 * O SDK não está no Maven Central: está no GitHub Packages, que exige autenticação mesmo para
 * pacotes públicos. Precisa ser um **personal access token (classic)** com escopo `read:packages`.
 *
 * Coloque em `local.properties` (já ignorado pelo git):
 *
 *     github_token=ghp_xxxxxxxxxxxxxxxxxxxx
 *
 * ou exporte `GITHUB_TOKEN` no ambiente. Sem token, o repositório simplesmente não resolve — e como
 * ainda não há dependência `com.meta.wearable:*` declarada, o build continua funcionando normalmente
 * para quem não tem credencial. Ver `docs/adr/0006-dat-version.md`.
 */
val localProperties = Properties().apply {
    val file = File(rootDir, "local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
val githubToken: String = System.getenv("GITHUB_TOKEN")
    ?: localProperties.getProperty("github_token")
    ?: ""

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // Meta Wearables Device Access Toolkit — só é consultado quando há token.
        if (githubToken.isNotBlank()) {
            maven {
                name = "MetaWearablesDat"
                url = uri("https://maven.pkg.github.com/facebook/meta-wearables-dat-android")
                credentials {
                    username = "" // o registro do GitHub ignora o usuário; o que vale é o token
                    password = githubToken
                }
                content {
                    // Limita o escopo: nenhuma outra dependência tenta resolver por aqui.
                    includeGroup("com.meta.wearable")
                }
            }
        }
    }
}

rootProject.name = "EatControlAI"
include(":app")
