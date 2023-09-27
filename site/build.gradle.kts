import com.varabyte.kobweb.common.path.toUnixSeparators
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

val generateBlogListingTask = task("bsGenerateBlogListing") {
    group = "bitspittle"
    val BLOG_INPUT_DIR = "src/jsMain/resources/markdown/blog"
    val BLOG_OUTPUT_DIR = "generated/kobweb/src/jsMain/kotlin"
    val BLOG_LISTING_OUTPUT_FILE = "$BLOG_OUTPUT_DIR/dev/bitspittle/site/pages/blog/Index.kt"

    inputs.dir(project.layout.projectDirectory.dir(BLOG_INPUT_DIR))
        .withPropertyName("blogArticles")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    outputs.dir(project.layout.buildDirectory.dir(BLOG_OUTPUT_DIR))
        .withPropertyName("blogListing")

    doLast {
        val parser = kobweb.markdown.features.createParser()
        val blogEntries = mutableListOf<BlogEntry>()
        val root = file(BLOG_INPUT_DIR)
        fileTree(root).forEach { blogArticle ->
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
            blogEntries.add(BlogEntry(blogArticle.relativeTo(root), author, date, title, desc, tags))
        }

        project.layout.buildDirectory.file(BLOG_LISTING_OUTPUT_FILE).map { it.asFile }.get().let { blogList ->
            blogList.parentFile.mkdirs()
            blogList.writeText(buildString {
                appendLine(
                    """
                    package dev.bitspittle.site.pages.blog
                    
                    import androidx.compose.runtime.*
                    import com.varabyte.kobweb.compose.ui.*
                    import com.varabyte.kobweb.core.*
                    import com.varabyte.kobweb.silk.components.navigation.Link
                    import com.varabyte.kobweb.silk.components.text.Text
                    import com.varabyte.kobweb.silk.components.style.*
                    import com.varabyte.kobwebx.markdown.*
                    import dev.bitspittle.site.components.layouts.PageLayout                   
                    import dev.bitspittle.site.components.widgets.blog.*                   
                    import org.jetbrains.compose.web.dom.*
                    
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
                            entry.file.path.substringBeforeLast('.').lowercase().toUnixSeparators()
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

            println("Generated ${blogList.absolutePath}")
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
            kotlin.srcDir(generateBlogListingTask)
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
