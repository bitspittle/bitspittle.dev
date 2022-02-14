import com.varabyte.kobwebx.gradle.markdown.children
import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCall
import kotlinx.html.script
import org.commonmark.ext.front.matter.YamlFrontMatterBlock
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.CustomBlock
import org.commonmark.node.Text

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kobwebx.markdown)
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
    maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
}

group = "dev.bitspittle.site"
version = "1.0-SNAPSHOT"

kobweb {
    index {
        description.set("A site about programming and advice extracted from a 20-year career")

        head.add {
            script {
                // Needed by components/layouts/BlogLayout.kt
                src = "/highlight.js/highlight.min.js"
            }
        }
    }
}

kobwebx {
    markdown {
        components {
            val BS_WGT = "dev.bitspittle.site.components.widgets"

            hr.set { hr ->
                "$BS_WGT.dom.Hr"
            }

            code.set { code ->
                "$BS_WGT.code.CodeBlock(\"\"\"${code.literal}\"\"\", lang = ${code.info.takeIf { it.isNotBlank() }?.let { "\"$it\"" } })"
            }

            inlineCode.set { code ->
                "$BS_WGT.code.InlineCode(\"\"\"${code.literal}\"\"\")"
            }

            val baseHeadingHandler = heading.get()
            heading.set { heading ->
                val result = baseHeadingHandler.invoke(this, heading)
                val id = idGenerator.get().invoke(
                    heading.children().filterIsInstance<Text>().map { it.literal }.joinToString("")
                )
                heading.appendChild(KobwebCall(".components.widgets.navigation.HoverLink(\"#$id\")"))

                result
            }
        }
    }
}

kotlin {
    jvm {
        tasks.named("jvmJar", Jar::class.java).configure {
            archiveFileName.set("bitspittledev.jar")
        }
    }
    js(IR) {
        moduleName = "bitspittledev"
        browser {
            commonWebpackConfig {
                outputFileName = "bitspittledev.js"
            }
        }
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(libs.kobweb.core)
                implementation(libs.kobweb.silk.core)
                implementation(libs.kobweb.silk.icons.fa)
                implementation(libs.kobwebx.markdown)
             }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.kobweb.api)
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
            _frontMatter.putAll(yamlVisitor.data)
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
    val BLOG_LIST_OUTPUT_FILE = "generated/kobweb/src/jsMain/kotlin/dev/bitspittle/site/pages/blog/Index.kt"

    inputs.files(fileTree(BLOG_INPUT_DIR))
        .withPropertyName("blogArticles")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    outputs.file(layout.buildDirectory.file(BLOG_LIST_OUTPUT_FILE))
        .withPropertyName("blogListing")

    doLast {
        val parser = kobwebx.markdown.features.createParser()
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

        project.layout.buildDirectory.file(BLOG_LIST_OUTPUT_FILE).map { it.asFile }.get().let { blogList ->
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
                    appendLine("""      ArticleEntry("/blog/${entry.file.path.substringBeforeLast('.').toLowerCase()}", "${entry.author}", "${entry.date}", "${entry.title.escapeQuotes()}", "${entry.desc.escapeQuotes()}"),""")
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
tasks.named("kobwebGenSite") { dependsOn(generateBlogListingTask) }
