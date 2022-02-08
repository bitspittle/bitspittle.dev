---
root: .components.layouts.BlogLayout
title: Kobweb: A Framework Built on Compose for Web
description: An intro to Kobweb, a Kotlin web framework I wrote and used to build this website.
author: David Herman
date: 2022-02-07
tags:
 - kotlin/js
 - compose for web
 - webdev
 - kobweb
---

{{{ .components.widgets.blog.ArticleMetadata }}}

I wrote a thing -- a Kotlin web framework called [Kobweb](https://github.com/varabyte/kobweb).

It is built on top of [Compose for Web](https://compose-web.ui.pages.jetbrains.team/), an official, and fairly recent,
reactive web framework created by JetBrains (in close collaboration with Google, and in turn built on *their* framework,
Jetpack Compose).

And this whole site, *including this very page you are now perusing*, is Kobweb's first user.

Frontend development in Kotlin is still in its early days, so it's an exciting time to explore the space. In this post,
I'll introduce some Kobweb basics, as well as discuss why you might (or might not!) want to use it.

## Kobweb

Compose for Web is a rich API that does an impressive amount of work wrapping underlying html / css concepts into a
reactive API. However, it is ultimately a foundational layer, leaving many choices to the developer on how to
approach the final design.

If instead you wanted to start writing Kotlin code immediately to create webpages, that's where Kobweb comes in.

### Creating a page

Let's say you recently picked up the domain `https://example.com`. One of the very first things you might want to do is
create the page `https://example.com/hello`.

With Kobweb, this couldn't be easier -- just annotate a composable method with the `@Page` annotation, and you're done:

```kotlin
// src/jsMain/kotlin/com/example/pages/Hello.kt
package com.example.pages

@Page
@Composable
fun HelloPage() {
    Text("Hello, World!")
}
```

That's it! Really!

To test it, spin up a Kobweb server (using `kobweb run`), visit `http://localhost:8080/hello` in your browser, and enjoy
your working Kobweb site.

### Linking pages

Once you have at least two pages, you can navigate between them using a `Link`. The page transition will happen
instantly, without needing to fetch additional information from the server.

In other words, if we add this "goodbye" page:

```kotlin
// src/jsMain/kotlin/com/example/pages/Goodbye.kt
package com.example.pages

@Page
@Composable
fun GoodbyePage() {
    Text("Goodbye, Cruel World!")
}
```

we can then modify our "hello" page example to add a link to it:

```kotlin
// src/jsMain/kotlin/com/example/pages/Hello.kt
package com.example.pages

@Page
@Composable
fun HelloPage() {
    Text("Hello, World!")
    Link("/goodbye") {
        Text("Say goodbye...")
    }
}
```

With this setup, `https://example.com/hello` will now show a link which, if clicked, will switch your page instantly to
`https://example.com/goodbye`.

If you pass an external address to `Link`, e.g. `Link("https://google.com")`, then it will act like a normal html link
and navigate to that page as you'd expect.

## Silk

Kobweb can be used on its own for its routing capabilities, but it also provides a UI library called Silk, a
color-mode-aware (i.e. light and dark) collection of widgets as well as general theming and component styling support.

I believe component styling is one of those things that once you start using it you won't want to go back. I demonstrate
it later in its [own subsection ▼](#component-styling).

### Color mode

Did you happen to see the color toggling button at the top-right of the site? No need to move your cursor -- I'll create
another copy here: ${.components.widgets.button.ColorModeButton}

This button encapsulates the logic for changing this site's active color mode. Try clicking on it!

It's trivial to query your site's color mode. Silk exposes a `rememberColorMode` method:

```kotlin
@Composable
fun SomeWidget() {
    val colorMode by rememberColorMode()
    val widgetColor = if (colorMode.isDark()) Colors.Pink else Colors.Red
    /*...*/
}
```

You can also use `var` instead of `val` in your code, if you want to change the color mode, not just read it:

```kotlin
@Composable
fun ToggleColorButton() {
    var colorMode by rememberColorMode()
    Button(onClick = { colorMode = colorMode.opposite() })
}
```

### Canvas

So far, most of this post has been text. But honestly -- text is static. How droll.

This is the future! Users of the web3 era demand more.

Using Kotlin, you can create dynamic elements by rendering to a canvas. The following clock is adapted from
[this Mozilla canvas example](https://developer.mozilla.org/en-US/docs/Web/API/Canvas_API/Tutorial/Basic_animations#an_animated_clock)
that was originally written in JavaScript.

{{{ .components.widgets.blog.kotlinsite.DemoWidget }}}

Here's the [Kotlin source](https://github.com/bitspittle/bitspittle.dev/tree/main/src/jsMain/kotlin/dev/bitspittle/site/components/widgets/blog/kotlinsite/DemoWidget).

Among other things, Silk provides a helpful `Canvas` widget which makes it easy to register some code that will
automatically get called for you once per frame.

Using `Canvas`, it was trivial to make the clock color mode aware as the current color mode was into the callback.
You can click this color mode button ${.components.widgets.button.ColorModeButton} to observe the results yourself.

Despite being easy to use, the canvas widget is extremely powerful, and you could use it to create dynamic effects,
full screen backgrounds, or even games.

### Modifier

Anyone who has dabbled with Jetpack Compose is likely familiar with the `Modifier` class. It may seem as fundamental to
Compose as the `@Composable` annotation is.

However, it isn't! Compose for Web actually does not have a `Modifier` class.

Instead, it uses an approach where all HTML tags are converted to `@Composable` function calls that take in something
called an `AttrsBuilder`.

As a concrete example, this HTML document tag:

```html
<div id="example" style="width:50px;height:25px;background-color:black">
```

would be written with the following Compose for Web code:

```kotlin
Div(attrs = {
    assert(this is AttrsBuilder)
    id("example")
    style {
        width(50.px)
        height(25.px)
        backgroundColor("black")
    }
})
```

I think this approach is pretty neat, but as `AttrsBuilder` is a mutable class, that makes it dangerous to store in a
sharable variable. Plus, its API doesn't support chaining.

To solve this, Silk provides its own `Modifier` class which is *inspired* by Jetpack Compose's version but isn't exactly
the same one. Still, it should look familiar enough to people who write Jetpack Compose code.

Silk widgets take modifiers directly:

```kotlin
Button(
    onClick = { /*...*/ },
    Modifier.fontWeight(FontWeight.Bold)
)
```

But for interoperability with Compose for Web elements, it is easy to convert a `Modifier` into an `AttrsBuilder` on the
fly, using the `asAttributesBuilder` method:

```kotlin
private val EXAMPLE_MODIFIER = Modifier
    .id("example")
    .width(50.px).height(25.px)
    .backgroundColor(Colors.Black)

Div(attrs = EXAMPLE_MODIFIER.asAttributesBuilder())
```

With `Modifier`s, chaining is easy using the `then` method:

```kotlin
private val SIZE_MODIFIER = Modifier.size(50.px)
private val SPACING_MODIFIER = Modifier.margin(10.px).padding(20.px)

private val COMBINED_MODIFIER = SIZE_MODIFIER.then(SPACING_MODIFIER)
```

Modifiers are used heavily throughout Silk, which should help ease the experience for Android and desktop Kotlin
developers just getting started with frontend development.

### Organizing styles

#### Stylesheet shortcomings

Most frontend projects have a single, giant, terrifying stylesheet (or, worse, several giant, terrifying stylesheets)
driving the look and feel of their site.

***Aside:** If you don't know what a stylesheet is, it's a collection of CSS rules that target various elements on your
page using a declarative format.*

For example, at one point while working on Kobweb, I used a todo app to learn from, and at least half of the time I
spent was crawling over
[their stylesheet](https://github.com/upstash/redis-examples/blob/master/nextjs-todo/styles/Home.module.css) to
understand the nuances of their approach.

Compose for Web allows you to
[define this stylesheet in code](https://github.com/JetBrains/compose-jb/tree/master/tutorials/Web/Style_Dsl#stylesheet),
but you can still easily end up with a monolith.

#### Component styling

Kobweb introduces component styling, which is a fancy way of saying you can define the styles you use in smaller pieces
next to the code that uses them.

It's easy -- just instantiate a `ComponentStyle`, pass in a unique name, and store the result to a `val`. Choose a name
that is simple and clear, because it might help you if you need to debug your page using browser tools later:

```kotlin
val SomeWidgetStyle = ComponentStyle("some-widget") {
    base { Modifier.fontSize(32.px).padding(10.px) }
    hover {
        val highlightColor =
            if (colorMode.isDark()) Colors.Pink else Colors.Red
        Modifier.backgroundColor(highlightColor)
    }
}
```

The `base` style, if defined, is special, as it will always be applied first. Any additional declarations are layered on
top of the base if their condition is met.

Component styles, once defined, can be converted to `Modifier`s using the `toModifier` method. This way, you can pass
them into either Silk widgets *or* Compose for Web elements:

```kotlin
val SomeWidgetStyle = ComponentStyle("some-widget") { /*...*/ }

@Composable
fun SomeWidget() {
    val widgetModifier = SomeWidgetStyle.toModifier()
    
    // Silk widget:
    Button(onClick = {}, widgetModifier) { /*...*/ }
    
    // Compose for Web element:
    Div(attrs = widgetModifier.asAttributesBuilder()) { /*...*/ }
}
```

It is way easier to read your code when your element styles live near where they are used, since you don't have to jump
between the code and a monolothic stylesheet in a different file.

## Markdown

At the beginning of this post, I said this site was written entirely in Kotlin. This may actually be a technicality.

In fact, most of this site is actually written using markdown. Relevant markdown files are transpiled to Kotlin just
before compilation happens.

Kobweb extends markdown with some custom support for nesting code inside it which is how I embedded the color buttons
and clock widget above. You can inline code with a Kotlin-y `${'$'}{...}` syntax or put a larger widget on its own line
with triple curly-brace syntax:

```markdown
# An intro to pathfinding

Here is a demonstration of A-star pathfinding

{{{ .components.widgets.astar.Demo }}}

Play: ${'$'}{.components.widgets.astar.PlayButton}
Step: ${'$'}{.components.widgets.astar.StepButton}
```

Code references that start with `.` will automatically be prefixed by your project's base package, so for example the
demo line above may generate a line like `com.example.components.widgets.astar.Demo()`.

Ultimately, Markdown support out of the box means that if you love Kotlin *and* you were thinking of starting a blog,
Kobweb might be a great solution for you.

## Other approaches

Let's finish off by discussing other approaches, to compare and contrast with Kobweb.

If you're already sold on Kobweb, feel free to skip this section and jump straight to the [conclusion ▼](#conclusion).

### Compose for Web - Canvas API

Many users in the Kotlin community are excited about the promise of multiplatform, and they are expecting to write a web
app once and run it everywhere (Android, Desktop, *and* Web).

At the moment of writing this post, Kobweb is very much *not* that sort of solution. It is designed for developers who
want to create a traditional website but use Kotlin instead of, say, TypeScript.

Before committing to Kobweb, you should know that JetBrains is actively working towards enabling the multiplatform
workflow via a new API where you give it an HTML canvas and it renders your app to it opaquely. If what you really want
to do is write a cross-platform app which just happens to also work in your browser, it may be worth waiting for this
feature to land.

There's no one-size fits all solution, however, and Kobweb may still be the right choice if you're creating a website.
I write about this a bit more in [Kobweb's README](https://github.com/varabyte/kobweb#what-about-multiplatform-widgets),
in case you wanted to learn more about the different approaches.

### Custom server

This is planned to change later, but for Kobweb's first release, it owns the full stack.

I didn't go over it in this post, but you can easily implement both your client and server logic in a single project.
This can be a very powerful foundation if you're starting up a new project from scratch.

However, your team may already have existing backend infrastructure, or you know in advance you want to control the
backend entirely yourself because of a workflow you're used to. While this is definitely do-able with Kobweb, it's not
officially supported yet and will require some manual effort.

For people in this situation, it's quite possible that Kobweb is too early to use with their project.

You can read more about Kobweb's approach to defining server API routes in
[the README](https://github.com/varabyte/kobweb#define-api-routes) and/or check
[this issue](https://github.com/varabyte/kobweb/issues/22) in our tracker to see the current state of external server
support in Kobweb.

### Vanilla Compose for Web

Perhaps you've been burned by frameworks before. "Yeah buddy, Kobweb is nice, but I'm just going to stick with Compose
for Web *classic*."

That's fine with me! Just be aware, this post only scratched the surface of what Kobweb can do for you. Here's a fuller
list of features we provide, since if you go it alone, you may need to implement some of them yourself:

* setting up Gradle build files and index.html boilerplate
* site routing
* running and configuring a server
* defining and communicating with server API routes
* site exports, for SEO and/or serving pages of your site statically
* organizing your stylesheets
* light and dark color mode support and theming
* a (growing) collection of color-mode aware widgets
* introduction of the `Modifier` concept, useful for chaining styles
* implementations for `Box`, `Column`, and `Row` on top of html / css
* shape clipping
* markdown support
* composables for all free Font Awesome icons
* parsing and handling query parameters (e.g. `/posts?userId=...&postId=...`)
* parsing and handling dynamic routes (e.g. `/users/{userId}/posts/{postId}`"` )
* handling responsive layouts (mobile vs. desktop)
* an experience built from the ground up around live reloading

I mention these not (just) to humblebrag, but because I myself was surprised by what was needed to create an MVP of
Kobweb. I vastly underestimated the scope.

So, sure, I'm biased, but my opinion is that if you're going to use Compose for Web to make a website (as opposed to a
web app), you probably want to at least give Kobweb a try.

### JavaScript / TypeScript

Kotlin/JS may not be for everyone. Most of webdev community is amassed around JavaScript / TypeScript and frameworks
like React.

There are a lot of advantages to sticking with the crowd in this case. And not just because they have a huge headstart.
Compile times tend to be a lot faster, you can experiment with JavaScript by typing commands directly in your browser,
you'll benefit from a ton of community support and resources, and there's no shortage of interesting projects out there
to learn from.

I have talked to many TypeScript programmers who vouch for it and say they enjoy writing code in the language. Microsoft
has really done a great job adding seatbelts, helmets, and full body cushions to JavaScript (which itself is still
evolving and getting better over time).

While I personally want to encourage more Kotlin developers to explore the frontend world and grow the community, if a
new programmer came up to me today saying they wanted to write a website from scratch, especially with the hopes of
developing skills that will turn into a frontend career, then I would send them to JavaScript / TypeScript tutorials at
this point.

If you like what I'm doing with Kobweb but think it makes more sense to use JavaScript / TypeScript for your project,
check out [Next.js](https://nextjs.org/) paired with [Chakra UI](https://chakra-ui.com/), as both of these solutions
were huge inspirations for me.

## Conclusion

I've been very excited about the Kotlin webdev space ever since Compose for Web was announced, and I hope this post
has pushed at least one other person over the fence.

### Trying Kobweb

If Kobweb looks like something you'd want to play with, the easiest way to start is by
[installing the Kobweb binary]( https://github.com/varabyte/kobweb#install-the-kobweb-binary).

Once installed, you can run:

```bash
$ kobweb create site
# answer a bunch of questions about your project
$ cd site
$ kobweb run
```

If this post made you curious to play around with Compose for Web, you can start with the
[official tutorial](https://github.com/JetBrains/compose-jb/tree/master/tutorials/Web/Getting_Started) but have Kobweb
set it up for you in a few seconds:

```bash
$ kobweb create examples/jb/counter
$ cd counter
$ kobweb run
```

And finally, if you are thinking about using Kobweb or you have decided to start using it(!), consider jumping into our
[Discord server](https://discord.gg/5NZ2GKV5Cs), where I'd be happy to answer questions about Kobweb or even Kotlin
development in general.

### The future

I can't predict if Kotlin webdev will ever take off, much less Kobweb itself. But I sincerely want a future where there
are more Kotlin developers owning codebases that cross the full stack. If I can help throw some code over the wall to
help make the experience better, then I'm happy to have tried.

At the very least, Kobweb will always have one user -- this site!