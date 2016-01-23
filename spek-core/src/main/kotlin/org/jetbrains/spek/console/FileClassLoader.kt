package org.jetbrains.spek.console

import org.jetbrains.spek.api.*
import java.io.*
import java.lang.reflect.*
import java.net.*
import java.util.jar.*


public fun getUrlsForPaths(paths: List<String>): List<URL> {
    val urls = arrayListOf<URL>()
    paths.forEach {
        val file = File(it)
        if (file.exists()) {
            val url = file.toURI().toURL()
            urls.add(url)
        }
    }
    return urls
}


public fun findClassesInClassPath(packageName: String): List<String> {
    val classLoader = Thread.currentThread().getContextClassLoader()
    val packageUrl = classLoader?.getResource(packageName.replace('.', '/'))
    // todo js broken -> finding class in class path doesn't work! (glues package to classpath - thus creating non-existing paths)
    return findClassesInUrls(listOf(packageUrl!!), packageName)
}

public fun findClassesInUrls(urls: List<URL>, packageName: String): List<String> {
    val names = arrayListOf<String>()
    for (url in urls) {
        if (url.toString().endsWith(".jar")) {
            var jarFilename = URLDecoder.decode(url.getFile()!!, "UTF-8")
            val jarFile = JarFile(jarFilename)
            var jarEntries = jarFile.entries()
            while (jarEntries.hasMoreElements())  {
                var entryName = jarEntries.nextElement().getName()
                if (entryName.startsWith(packageName) && entryName.length > packageName.length +5) {
                    entryName = entryName.substring(packageName.length,entryName.lastIndexOf('.'))
                    names.add(entryName)
                }
            }
        } else {
            val uri = URI(url.toString() + "/" + packageName.replace('.', '/'))
            val folder = File(uri.getPath()!!)
            val contents = folder.listFiles()
            contents?.forEach {
                var entry = it.getName()
                val classIndex = entry.lastIndexOf('.')
                if (classIndex > 0) {
                    entry = entry.substring(0, classIndex)
                    names.add(entry)
                }
            }
        }
    }
    return names
}

public fun findSpecs(paths: List<String>, packageName: String): MutableList<TestSpekAction> {
    val result = arrayListOf<TestSpekAction>()
    val urls = getUrlsForPaths(paths)
    val classloader = URLClassLoader.newInstance(urls.toTypedArray())!!
    urls.forEach {
        val classes = findClassesInUrls(urls, packageName)
        classes.forEach {
            val loadedClass = classloader.loadClass(packageName + "." + it)
            if (Spek::class.java.isAssignableFrom(loadedClass!!)) {
                @Suppress("UNCHECKED_CAST")
                result.add(ClassSpek(loadedClass as Class<Spek>))
            }
        }
    }
    return result
}


private fun AnnotatedElement.checkSkipped() {
    val skip = getAnnotation(ignored::class.java)
    if (skip != null) throw SkippedException(skip.why)
}

public data class ExtensionFunctionSpek(val method: Method) : TestSpekAction {
    override fun description(): String = method.toString()

    override fun iterateGiven(it: (TestGivenAction) -> Unit) {
        val builder = object : Spek() {
        }
        //TODO: assert method signature

        method.checkSkipped()
        method.invoke(null, builder)

        builder.iterateGiven(it)
    }
}

public data class ClassSpek<T : Spek>(val specificationClass: Class<out T>) : TestSpekAction {
    override fun description(): String = specificationClass.getName()

    override fun iterateGiven(it: (TestGivenAction) -> Unit) {
        specificationClass.checkSkipped()
        specificationClass.newInstance().iterateGiven(it)
    }
}
