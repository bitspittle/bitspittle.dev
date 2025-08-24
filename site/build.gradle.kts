import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import com.varabyte.kobwebx.gradle.markdown.handlers.MarkdownHandlers
import com.varabyte.kobwebx.gradle.markdown.ext.kobwebcall.KobwebCall
import kotlinx.html.script
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kobwebx.markdown)
    alias(libs.plugins.vite.kotlin)
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

class BlogEntry(
    val route: String,
    val author: String,
    val date: String,
    val title: String,
    val desc: String,
    val tags: List<String>
) {
    private fun String.escapeQuotes() = this.replace("\"", "\\\"")
    fun toArticleEntry() = """ArticleEntry("$route", "$author", "$date", "${title.escapeQuotes()}", "${desc.escapeQuotes()}")"""
}

kobweb {
    app {
        cssPrefix.set("bs")
        index {
            description.set("Tech chatter, tutorials, and career advice")

            head.add {
                script {
                    // Needed by components/layouts/BlogLayout.kt
                    src = "/highlight.js/highlight.min.js"
                    type = "module"
                }
            }

            scriptAttributes.put("type", "module")
        }
    }

    markdown {
        handlers {
            val BS_WGT = "dev.bitspittle.site.components.widgets"

            code.set { code ->
                "$BS_WGT.code.CodeBlock(\"\"\"${code.literal.escapeTripleQuotedText()}\"\"\", lang = ${
                    code.info.takeIf { it.isNotBlank() }?.let { "\"$it\"" }
                })"
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
                val id = data.getValue(MarkdownHandlers.DataKeys.HeadingIds).getValue(heading)

                // HoverLink is a widget that will show a link icon (linking back to the header) on hover
                // This is a useful way to let people share a link to a specific header
                heading.appendChild(KobwebCall(".components.widgets.navigation.HoverLink(\"#$id\")"))

                result
            }
        }

        process.set { markdownEntries ->
            val requiredFields = listOf("title", "description", "author", "date")
            val blogEntries = markdownEntries.mapNotNull { markdownEntry ->
                val fm = markdownEntry.frontMatter
                val (title, desc, author, date) = requiredFields
                    .map { key -> fm[key]?.singleOrNull() }
                    .takeIf { values -> values.all { it != null } }
                    ?.requireNoNulls()
                    ?: run {
                        println("Not adding \"${markdownEntry.filePath}\" into the listing file as it is missing required frontmatter fields (one of $requiredFields)")
                        return@mapNotNull null
                    }

                val tags = fm["tags"] ?: emptyList()
                BlogEntry(markdownEntry.route, author, date, title, desc, tags)
            }

            val blogPackage = "dev.bitspittle.site.pages.blog"
            val blogPath = "${blogPackage.replace('.', '/')}/Index.kt"
            generateKotlin(blogPath, buildString {
                appendLine(
                    """
                    // This file is generated. Modify the build script if you need to change it.

                    package $blogPackage

                    import androidx.compose.runtime.*
                    import com.varabyte.kobweb.core.Page
                    import com.varabyte.kobweb.core.PageContext
                    import com.varabyte.kobweb.core.data.*
                    import com.varabyte.kobweb.core.init.InitRoute
                    import com.varabyte.kobweb.core.init.InitRouteContext
                    import com.varabyte.kobweb.core.layout.Layout
                    import dev.bitspittle.site.components.layouts.PageLayout
                    import dev.bitspittle.site.components.layouts.PageLayoutData
                    import dev.bitspittle.site.components.widgets.blog.ArticleEntry
                    import dev.bitspittle.site.components.widgets.blog.ArticleList

                    @InitRoute
                    fun initBlogListingsPage(ctx: InitRouteContext) {
                      ctx.data.add(PageLayoutData("Blog Posts"))
                    }

                    @Page
                    @Composable
                    @Layout(".components.layouts.PageLayout")
                    fun BlogListingsPage(ctx: PageContext) {
                      val entries = listOf(
                    """.trimIndent()
                )

                blogEntries.sortedByDescending { it.date }.forEach { entry ->
                    appendLine("    ${entry.toArticleEntry()},")
                }

                appendLine(
                    """
                      )
                      ArticleList(entries)
                    }
                    """.trimIndent()
                )
            })
            println("Generated blog listing index at \"$blogPath\".")
        }
    }
}

kotlin {
    configAsKobwebApplication("bitspittledev")
    js {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions.target = "es2015"
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.compose.runtime)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(libs.compose.html.core)
                implementation(libs.kobweb.core)
                implementation(libs.kobweb.silk)
                implementation(libs.silk.icons.fa)
                implementation(libs.kobwebx.markdown)
                implementation(libs.firebase.kotlin.bindings)
            }
        }
    }
}

// Vite requires the static files (index.html…) to be in the same directory as the JS files.
// This flattens the 'public' subdirectory when building with Vite.
for (task in listOf(tasks.viteCompileKotlinDev, tasks.viteCompileKotlinProd)) {
    task {
        eachFile {
            relativePath = RelativePath(
                /* endsWithFile = */ relativeSourcePath.isFile,
                *relativeSourcePath.segments
                    .filter { it != "public" }
                    .toTypedArray()
            )
        }
    }
}
