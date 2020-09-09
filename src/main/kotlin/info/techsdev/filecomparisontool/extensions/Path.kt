package info.techsdev.filecomparisontool.extensions

import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest

fun Path.hash(algorithm: String): ByteArray {
    val digest = MessageDigest.getInstance(algorithm)
    Files.newInputStream(this).use { stream ->
        var read: Int
        val buffer = ByteArray(1024)
        do {
            read = stream.read(buffer)
            if (read > 0)
                digest.update(buffer, 0, read)
        } while (read != -1)
    }
    return digest.digest()
}

fun Path.iterateFilesRecursively(callback: (Path) -> Unit) {
    Files.list(this).forEach { file ->
        when {
            Files.isSymbolicLink(file) -> return@forEach
            Files.isDirectory(file) -> file.iterateFilesRecursively(callback)
            Files.isRegularFile(file) -> callback(file)
        }
    }
}