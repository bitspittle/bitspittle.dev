import com.varabyte.kobweb.common.path.invariantSeparatorsPath
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import com.varabyte.kobwebx.gradle.markdown.MarkdownHandlers.Companion.HeadingIdsKey
import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCall
import com.varabyte.kobwebx.gradle.markdown.yamlStringToKotlinString
import kotlinx.html.script
import org.commonmark.ext.front.matter.YamlFrontMatterBlock
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.CustomBlock

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kobwebx.markdown)
}

repositories {
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    mavenLocal {
        content {
            includeGroup("dev.bitspittle")
        }
    }
}

group = "dev.bitspittle.site"
version = "1.0-SNAPSHOT"

kobweb {
    app {
        index {
            description.set("Tech chatter, tutorials, and career advice")

            head.add {
                script {
                    // Needed by components/layouts/BlogLayout.kt
                    src = "/highlight.js/highlight.min.js"
                }
            }
        }
    }

    markdown {
        handlers {
            val BS_WGT = "dev.bitspittle.site.components.widgets"

            code.set { code ->
                "$BS_WGT.code.CodeBlock(\"\"\"${code.literal.escapeTripleQuotedText()}\"\"\", lang = ${code.info.takeIf { it.isNotBlank() }?.let { "\"$it\"" } })"
            }

            inlineCode.set { code ->
                "$BS_WGT.code.InlineCode(\"\"\"${code.literal.escapeTripleQuotedText()}\"\"\")"
            }

            val baseHeadingHandler = heading.get()
            heading.set { heading ->
                // Convert a heading to include its ID
                // e.g. <h2>My Heading</h2> becomes <h2 id="my-heading">My Heading</h2>
                val result = baseHeadingHandler.invoke(this, heading)
                // ID guaranteed to be created as side effect of base handler
                val id = data.getValue(HeadingIdsKey).getValue(heading)

                // HoverLink is a widget that will show a link icon (linking back to the header) on hover
                // This is a useful way to let people share a link to a specific header
                heading.appendChild(KobwebCall(".components.widgets.navigation.HoverLink(\"#$id\")"))

                result
            }
        }
    }
}

class MarkdownVisitor : AbstractVisitor() {
    private val _frontMatter = mutableMapOf<String, List<String>>()
    val frontMatter: Map<String, List<String>> = _frontMatter

    override fun visit(customBlock: CustomBlock) {
        if (customBlock is YamlFrontMatterBlock) {
            val yamlVisitor = YamlFrontMatterVisitor()
            customBlock.accept(yamlVisitor)
            _frontMatter.putAll(
                yamlVisitor.data
                    .mapValues { (_, values) ->
                        values.map { it.yamlStringToKotlinString() }
                    }
            )
        }
    }
}

data class BlogEntry(
    val file: File,
    val author: String,
    val date: String,
    val title: String,
    val desc: String,
    val tags: List<String>
)

fun String.escapeQuotes() = this.replace("\"", "\\\"")

val generateBlogSourceTask = task("generateBlogSource") {
    group = "bitspittledev"
    val blogInputDir = layout.projectDirectory.dir("src/jsMain/resources/markdown/blog")
    val blogGenDir = layout.buildDirectory.dir("generated/$group/src/jsMain/kotlin").get()

    inputs.dir(blogInputDir)
        .withPropertyName("blogArticles")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    outputs.dir(blogGenDir)
        .withPropertyName("blogGeneratedSource")

    doLast {
        val parser = kobweb.markdown.features.createParser()
        val blogEntries = mutableListOf<BlogEntry>()

        blogInputDir.asFileTree.forEach { blogArticle ->
            val rootNode = parser.parse(blogArticle.readText())
            val visitor = MarkdownVisitor()

            rootNode.accept(visitor)

            val fm = visitor.frontMatter
            val requiredFields = listOf("title", "description", "author", "date")
            val (title, desc, author, date) = requiredFields
                .map { key -> fm[key]?.singleOrNull() }
                .takeIf { values -> values.all { it != null } }
                ?.requireNoNulls()
                ?: run {
                    println("Skipping $blogArticle in the listing as it is missing required frontmatter fields (one of $requiredFields)")
                    return@forEach
                }

            val tags = fm["tags"] ?: emptyList()
            blogEntries.add(BlogEntry(blogArticle.relativeTo(blogInputDir.asFile), author, date, title, desc, tags))
        }

        blogGenDir.file("dev/bitspittle/site/pages/blog/Index.kt").asFile.apply {
            parentFile.mkdirs()
            writeText(buildString {
                appendLine(
                    """
                    // This file is generated. Modify the build script if you need to change it.

                    package dev.bitspittle.site.pages.blog

                    import androidx.compose.runtime.*
                    import com.varabyte.kobweb.core.Page
                    import dev.bitspittle.site.components.layouts.PageLayout
                    import dev.bitspittle.site.components.widgets.blog.ArticleEntry
                    import dev.bitspittle.site.components.widgets.blog.ArticleList

                    @Page
                    @Composable
                    fun BlogListingsPage() {
                      PageLayout("Blog Posts") {
                        val entries = listOf(
                    """.trimIndent()
                )

                blogEntries.sortedByDescending { it.date }.forEach { entry ->
                    appendLine(
                        """      ArticleEntry("/blog/${
                            entry.file.path.substringBeforeLast('.').lowercase().invariantSeparatorsPath
                        }", "${entry.author}", "${entry.date}", "${entry.title.escapeQuotes()}", "${entry.desc.escapeQuotes()}"),"""
                    )
                }

                appendLine(
                    """
                        )
                        ArticleList(entries)
                      }
                    }
                    """.trimIndent()
                )
            })

            println("Generated $absolutePath")
        }
    }
}

kotlin {
    configAsKobwebApplication("bitspittledev")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
            }
        }

        val jsMain by getting {
            kotlin.srcDir(generateBlogSourceTask)
            dependencies {
                implementation(compose.html.core)
                implementation(libs.kobweb.core)
                implementation(libs.kobweb.silk)
                implementation(libs.silk.icons.fa)
                implementation(libs.kobwebx.markdown)
                implementation(libs.firebase.kotlin.bindings)
            }
        }
    }
}
