import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
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
 * Enquanto a integração roda em **Developer Mode**, a própria Meta orienta a **não** declarar esses
 * meta-data no manifesto. O snippet correspondente está em `docs/adr/0006-dat-version.md` e só entra
 * quando o app sair do modo de desenvolvimento.
 */
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

android {
    namespace = "com.eatcontrolai"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        // Três segmentos por exigência do Wearables Developer Center, que recusa "com.eatcontrolai"
        // com a mensagem de reverse-domain (o exemplo dele é com.example.app).
        // O `namespace` continua com.eatcontrolai: é o pacote do código, não precisa acompanhar.
        applicationId = "com.eatcontrolai.app"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["mwdat_application_id"] =
            localProperties.getProperty("mwdat_application_id") ?: ""
        manifestPlaceholders["mwdat_client_token"] =
            localProperties.getProperty("mwdat_client_token") ?: ""
    }

    buildTypes {
        release {
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
        }
    }
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

    // Câmera do celular como fonte de frame alternativa aos óculos (FR-002).
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Persistência local de perfil, privacidade e histórico (ADR-0002: sem nuvem no caminho crítico).
    implementation(libs.androidx.datastore.preferences)

    // Meta Wearables Device Access Toolkit (ADR-0006). Exige token do GitHub com read:packages em
    // local.properties — sem ele, o Gradle não resolve estes três artefatos.
    implementation(libs.mwdat.core)
    implementation(libs.mwdat.camera)
    // Só em debug: o Mock Device Kit é ferramenta de desenvolvimento e não tem por que
    // ser distribuído no APK de release.
    debugImplementation(libs.mwdat.mockdevice)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
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
