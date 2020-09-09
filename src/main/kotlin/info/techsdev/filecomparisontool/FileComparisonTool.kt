package info.techsdev.filecomparisontool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import info.techsdev.filecomparisontool.extensions.hash
import info.techsdev.filecomparisontool.extensions.iterateFilesRecursively
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.system.exitProcess

class FileComparisonTool: CliktCommand() {
    private val source by option(help = "The path where the original / source files are located")
        .path()
        .required()
    private val target by option(help = "The path containing the files that should be checked for duplicates")
        .path()
        .required()
    private val algorithm by option(help = "The hashing algorithm that should be used to generate file hashes", metavar = "ALGORITHM")
        .default("sha-256", "sha-256")
    private val confirm by option(help = "Skips the confirmation prompt")
        .flag(default = false)

    override fun run() {
        println("Running the file comparison tool with the following settings:")
        println("Source: $source")
        println("Target: $target")

        if (!Files.exists(source)) throw FileNotFoundException("Source folder not found: $source")
        if (!Files.exists(target)) throw FileNotFoundException("Target folder not found: $target")

        if (!Files.isDirectory(source)) throw IllegalArgumentException("Source path is not a folder: $source")
        if (!Files.isDirectory(target)) throw IllegalArgumentException("Target path is not a folder: $target")

        if (!confirm) {
            println("Do you want to compare all files in these folders? [Y/n]")
            val scanner = Scanner(System.`in`)
            val line = scanner.nextLine()
            if (line.isNotEmpty() && line.toLowerCase() != "y") {
                println("Exiting.")
                exitProcess(0)
            }
        }

        // We use the byte arrays converted to strings because [ByteArray] keys don't work in maps.
        println("Building lookup map...")
        val hashes = HashMap<String, Path>()
        source.iterateFilesRecursively { file ->
            hashes[file.hash(algorithm).contentToString()] = file
        }

        target.iterateFilesRecursively { file ->
            val hash = file.hash(algorithm).contentToString()

            val path = hashes[hash] ?: return@iterateFilesRecursively

            println("Found a duplicate!\n Original: $path\n Duplicate: $file\n")
        }
    }
}
