package scala.meta.languageserver

import scala.language.reflectiveCalls

import java.io.File
import java.io.PrintStream
import scala.reflect.internal.util.ScalaClassLoader.URLClassLoader

abstract class Formatter {
  def format(code: String, configFile: String, filename: String): String
}
object Formatter {
  def classloadScalafmt(version: String, out: PrintStream): Formatter = {
    val urls = Jars
      .fetch("com.geirsson", "scalafmt-cli_2.12", version, out)
      .iterator
      .map(_.toURI.toURL)
      .toArray
    type Scalafmt210 = {
      def format(code: String, configFile: String, filename: String): String
    }
    val classloader = new URLClassLoader(urls, null)
    val scalafmt210 = classloader
      .loadClass("org.scalafmt.cli.Scalafmt210")
      .newInstance()
      .asInstanceOf[Scalafmt210]
    new Formatter {
      override def format(
          code: String,
          configFile: String,
          filename: String
      ): String =
        scalafmt210.format(code, configFile, filename)
    }
  }
}