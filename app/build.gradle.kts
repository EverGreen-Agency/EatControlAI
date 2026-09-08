import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    jacoco
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

/**
 * Credenciais do Wearables Developer Center.
 *
 * Ficam em `local.properties` (ignorado pelo git) e entram no manifesto por placeholder. O
 * `CLIENT_TOKEN` acaba dentro do APK de qualquer jeito — é assim que a atestação funciona — mas não
 * há motivo para ele também morar no histórico do repositório.
 *
 *     mwdat_application_id=...
 *     mwdat_client_token=AR|...|...
 *
 * O Manifest usa placeholders nas variantes debug e release. O valor sentinela `0` mantém builds
 * locais compiláveis quando a integração não está configurada; autorização DAT e release channels
 * exigem os valores reais somente em `local.properties`, nunca no histórico do repositório.
 */
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

/**
 * Assinatura de release opcional e inteiramente local.
 *
 * Para habilitar, declare em `local.properties` (nunca no Git):
 *
 *     release_store_file=keystores/eatcontrol-release.jks
 *     release_store_password=...
 *     release_key_alias=...
 *     release_key_password=...
 *
 * Enquanto essas quatro chaves não existirem, builds debug continuam usando o debug keystore e o
 * APK release permanece sem uma identidade de produção configurada.
 */
val releaseStorePath = localProperties.getProperty("release_store_file")
val releaseStorePassword = localProperties.getProperty("release_store_password")
val releaseKeyAlias = localProperties.getProperty("release_key_alias")
val releaseKeyPassword = localProperties.getProperty("release_key_password")
val hasReleaseSigning = listOf(
    releaseStorePath,
    releaseStorePassword,
    releaseKeyAlias,
    releaseKeyPassword
).all { !it.isNullOrBlank() }

android {
    namespace = "com.eatcontrolai"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = rootProject.file(requireNotNull(releaseStorePath))
                storePassword = requireNotNull(releaseStorePassword)
                keyAlias = requireNotNull(releaseKeyAlias)
                keyPassword = requireNotNull(releaseKeyPassword)
            }
        }
    }

    defaultConfig {
        // Três segmentos por exigência do Wearables Developer Center, que recusa "com.eatcontrolai"
        // com a mensagem de reverse-domain (o exemplo dele é com.example.app).
        // O `namespace` continua com.eatcontrolai: é o pacote do código, não precisa acompanhar.
        applicationId = "ai.eatcontrol.app"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Em Developer Mode, a documentação oficial aceita o valor sentinela `0`.
        // Para release channels, os dois valores reais precisam existir em `local.properties`.
        manifestPlaceholders["mwdat_application_id"] =
            localProperties.getProperty("mwdat_application_id") ?: "0"
        manifestPlaceholders["mwdat_client_token"] =
            localProperties.getProperty("mwdat_client_token") ?: "0"
    }

    buildTypes {
        debug {
            // Habilita a instrumentação JaCoCo nos testes JVM. É o caminho oficial do AGP e evita
            // reimplementar o wiring de cobertura à mão.
            enableUnitTestCoverage = true
        }
        release {
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    /**
     * As bibliotecas nativas do ML Kit em quatro ABIs respondem por ~90% do APK. Separar por ABI
     * derruba o pacote instalado de ~68 MB para ~22 MB no aparelho-alvo, o que conta no checkpoint
     * de eficiência do edital e em `docs/METRICS.md` (package size).
     *
     * Só liga em build de release: em debug, um APK universal mantém o ciclo editar-rodar rápido e
     * funciona tanto no celular quanto no emulador x86.
     */
    splits {
        abi {
            isEnable = gradle.startParameter.taskNames.any { it.contains("Release") }
            reset()
            include("arm64-v8a", "armeabi-v7a", "x86_64")
            isUniversalApk = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        // A tela de Laboratório só aparece em build de debug (BuildConfig.DEBUG).
        buildConfig = true
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
            // Necessário para Robolectric enxergar recursos e manifesto ao testar ViewModel e UI.
            isIncludeAndroidResources = true
        }
    }
}

/**
 * Cobertura de testes.
 *
 * A régua alta é aplicada onde ela representa comportamento determinístico reproduzível: parsing,
 * evidência, decisão, roteamento, voz e os fluxos assistidos de MENU/PLATE. Adaptadores de hardware
 * (DAT, CameraX, ML Kit, TTS/STT) ficam fora porque exigem aparelho real; cobri-los apenas com dublês
 * produziria um número alto sem validar a integração física.
 *
 * A lista de exclusões é explícita de propósito: qualquer arquivo novo entra na conta até alguém
 * decidir o contrário e justificar aqui.
 */
val coverageExclusions = listOf(
    // Gerado pelo compilador/AGP.
    "**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*",
    "**/*_Factory*.*", "**/*Companion*.*", "**/*\$\$serializer*.*",
    // Camada de UI Compose: coberta por teste de interface, não por cobertura de linha.
    "**/ui/**",
    // Adaptadores de hardware e SDK externo: exigem dispositivo real.
    "**/glasses/**", "**/inference/**",
    // Harness de medição, não regra de negócio.
    "**/benchmark/**"
)

/** Pacotes determinísticos monitorados pelo gate de regressão. */
val fullyCoveredPackages = listOf(
    "com/eatcontrolai/domain/nutrition/**",
    "com/eatcontrolai/domain/label/**",
    "com/eatcontrolai/domain/decision/**",
    "com/eatcontrolai/domain/routing/**",
    "com/eatcontrolai/domain/voice/**",
    "com/eatcontrolai/domain/barcode/**",
    "com/eatcontrolai/domain/evidence/**",
    "com/eatcontrolai/domain/menu/**",
    "com/eatcontrolai/domain/plate/**",
    "com/eatcontrolai/domain/glp1/**"
)

private val unitTestExecData = layout.buildDirectory
    .file("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")

/**
 * Onde ficam os `.class` compilados.
 *
 * O AGP 9 usa a compilação Kotlin embutida (`built_in_kotlinc`); versões anteriores usavam
 * `tmp/kotlin-classes`. Os quatro caminhos ficam declarados porque diretório ausente é ignorado, e
 * assim a cobertura não silencia ao trocar de versão do plugin — silenciar seria pior que falhar.
 */
private val compiledClassDirs = files(
    layout.buildDirectory.dir("intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes"),
    layout.buildDirectory.dir("tmp/kotlin-classes/debug"),
    layout.buildDirectory.dir("intermediates/javac/debug/classes"),
    layout.buildDirectory.dir("intermediates/javac/debug/compileDebugJavaWithJavac/classes")
)

private val coveredClassDirs = compiledClassDirs.asFileTree.matching { exclude(coverageExclusions) }

tasks.register<JacocoReport>("coverageReport") {
    group = "verification"
    description = "Relatório de cobertura dos testes JVM (HTML e XML)."
    dependsOn("testDebugUnitTest")

    executionData.setFrom(unitTestExecData)
    classDirectories.setFrom(coveredClassDirs)
    sourceDirectories.setFrom(files("src/main/java"))

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }
}

tasks.register<JacocoCoverageVerification>("coverageVerify") {
    group = "verification"
    description = "Aplica os pisos de cobertura das camadas determinísticas de domínio."
    dependsOn("testDebugUnitTest")

    executionData.setFrom(unitTestExecData)
    sourceDirectories.setFrom(files("src/main/java"))
    classDirectories.setFrom(compiledClassDirs.asFileTree.matching { include(fullyCoveredPackages) })

    /**
     * Piso medido, não aspiracional.
     *
     * Medição de 18/08/2026 nas camadas de domínio, já com o rule pack GLP-1: **linha 99,3%**
     * (996/1003) e **ramo 82,7%** (425/514). O gate preserva 99% de linhas e 82% de ramos como
     * catraca de regressão. Ao subir cobertura, **suba o piso no mesmo commit**.
     */
    violationRules {
        rule {
            element = "BUNDLE"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.99".toBigDecimal()
            }
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.82".toBigDecimal()
            }
        }
    }
}

/** `check` passa a reprovar queda de cobertura no domínio, não só teste vermelho. */
tasks.named("check") {
    dependsOn("coverageVerify")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.kotlinx.coroutines.android)

    // Perception — ADR-0004: OCR e Barcode on-device pronto antes de pipeline customizado.
    implementation(libs.mlkit.text.recognition)
    implementation(libs.mlkit.barcode.scanning)
    // Modelo bundled: candidatos de prato disponíveis offline, sem download em primeiro uso.
    implementation(libs.mlkit.image.labeling)

    // Câmera do celular como fonte de frame alternativa aos óculos (FR-002).
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Persistência local de perfil, privacidade e histórico (ADR-0002: sem nuvem no caminho crítico).
    implementation(libs.androidx.datastore.preferences)

    // Telemetria & Observabilidade de Produto (Clarity para gravações/mapas de calor; PostHog para funis/métricas técnicas)
    implementation(libs.clarity)
    implementation(libs.posthog)

    // Meta Wearables Device Access Toolkit (ADR-0006). Exige token do GitHub com read:packages em
    // local.properties — sem ele, o Gradle não resolve estes três artefatos.
    implementation(libs.mwdat.core)
    implementation(libs.mwdat.camera)
    // Só em debug: o Mock Device Kit é ferramenta de desenvolvimento e não tem por que
    // ser distribuído no APK de release.
    debugImplementation(libs.mwdat.mockdevice)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    // Permite testar ViewModel, DataStore e Compose no JVM, sem emulador.
    testImplementation(libs.robolectric)
    // Asserções sobre StateFlow/Flow sem sleep nem race condition.
    testImplementation(libs.turbine)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    // O `org.json` do android.jar é um stub sem implementação: em teste JVM devolve null em tudo.
    // Esta dependência traz a implementação real para os testes de serialização.
    testImplementation(libs.org.json)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
