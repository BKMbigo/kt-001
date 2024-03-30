package io.github.bkmbigo.gallery.gradle.internal.utils

import org.gradle.api.provider.Provider
import java.io.File

internal enum class OS(val id: String) {
    Linux("linux"),
    Windows("windows"),
    MacOS("macos")
}

internal enum class Arch(val id: String) {
    X64("x64"),
    Arm64("arm64")
}

internal data class Target(val os: OS, val arch: Arch) {
    val id: String
        get() = "${os.id}-${arch.id}"
}

internal val currentTarget by lazy {
    Target(currentOS, currentArch)
}

internal val currentArch by lazy {
    val osArch = System.getProperty("os.arch")
    when (osArch) {
        "x86_64", "amd64" -> Arch.X64
        "aarch64" -> Arch.Arm64
        else -> error("Unsupported OS arch: $osArch")
    }
}

internal val currentOS: OS by lazy {
    val os = System.getProperty("os.name")
    when {
        os.equals("Mac OS X", ignoreCase = true) -> OS.MacOS
        os.startsWith("Win", ignoreCase = true) -> OS.Windows
        os.startsWith("Linux", ignoreCase = true) -> OS.Linux
        else -> error("Unknown OS name: $os")
    }
}

internal fun executableName(nameWithoutExtension: String): String =
    if (currentOS == OS.Windows) "$nameWithoutExtension.exe" else nameWithoutExtension

internal fun javaExecutable(javaHome: String): String =
    File(javaHome).resolve("bin/${executableName("java")}").absolutePath

internal fun jvmToolFile(toolName: String, javaHome: Provider<String>): File =
    jvmToolFile(toolName, File(javaHome.get()))

internal fun jvmToolFile(toolName: String, javaHome: File): File {
    val jtool = javaHome.resolve("bin/${executableName(toolName)}")
    check(jtool.isFile) {
        "Invalid JDK: $jtool is not a file! \n" +
                "Ensure JAVA_HOME or buildSettings.javaHome is set to JDK 17 or newer"
    }
    return jtool
}
