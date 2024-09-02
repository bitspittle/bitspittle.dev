---
root: .components.layouts.BlogLayout
title: "Kobweb: A Framework Built on Compose HTML"
description: An intro to Kobweb, a Kotlin web framework I wrote and used to build this website.
author: David Herman
date: 2022-02-07
updated: 2024-05-17
tags:
 - compose html
 - webdev
 - kobweb
---

I wrote a thing -- a Kotlin web framework called [Kobweb](https://github.com/varabyte/kobweb).

It is built on top of [Compose HTML](https://github.com/JetBrains/compose-multiplatform#compose-html), an official reactive web UI
framework created by JetBrains (in close collaboration with Google, and in turn built upon core technologies introduced
in Android's Jetpack Compose).

And this whole site, *including this very page you are now perusing*, is Kobweb's first user.

Frontend development in Kotlin is still in its early days, so it's an exciting time to explore the space. In this post,
I'll introduce some Kobweb basics, as well as discuss why you might (or might not!) want to use it.

## Kobweb

Compose HTML is a rich API that does an impressive amount of work wrapping underlying html / css concepts into a
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
    Link("/goodbye", "Say goodbye...")
}
```

With this setup, `https://example.com/hello` will now show a link which, if clicked, will switch your page instantly to
`https://example.com/goodbye`.

If you pass an external address to `Link`, e.g. `Link("https://google.com")`, then it will act like a normal html link
and navigate to that page as you'd expect.

## Silk

Kobweb can be used on its own for its routing capabilities, but it also provides a UI library called Silk, a
color-mode-aware (i.e. light and dark) collection of widgets as well as general theming and CSS styling support via CSS
style blocks.

I believe CSS style blocks are one of those features that once you start using them, you won't want to go back. I
demonstrate it later in its [own subsection▼](#css-style-blocks).

### Color mode

Did you happen to see the color toggling button at the top-right of the site? No need to move your cursor -- I'll create
another copy here: ${.components.widgets.button.ColorModeButton}

This button encapsulates the logic for changing this site's active color mode. Try clicking on it!

It's trivial to query your site's color mode. Silk exposes a `ColorMode.current` property:

```kotlin
@Composable
fun SomeWidget() {
    val colorMode = ColorMode.current
    val widgetColor =
        if (colorMode.isDark) Colors.Pink else Colors.Red
    /* ... code that uses widgetColor ... */
}
```

You can also use `var` instead of `val` with `ColorMode.currentState` in your code, if you want to change the color
mode, not just read it:

```kotlin
@Composable
fun ToggleColorButton() {
    var colorMode by ColorMode.currentState
    Button(onClick = { colorMode = colorMode.opposite })
}
```

### Canvas

So far, most of this post has been text. But honestly -- text is static. How droll.

This is the future! Users of the web3 era demand more.

Using Kotlin, you can create dynamic elements by rendering to a canvas. The following clock is adapted from
[this Mozilla canvas example](https://developer.mozilla.org/en-US/docs/Web/API/Canvas_API/Tutorial/Basic_animations#an_animated_clock)
that was originally written in JavaScript.

{{{ .components.widgets.blog.kotlinsite.DemoWidget }}}

It additionally renders differently depending on the site's color mode. You can click this color mode button
${.components.widgets.button.ColorModeButton} to observe the results yourself.

Here's the [Kotlin source](https://github.com/bitspittle/bitspittle.dev/blob/b5ce2d5a53e2017a6bd89b55dd6e855634587d51/src/jsMain/kotlin/dev/bitspittle/site/components/widgets/blog/kotlinsite/DemoWidget.kt#L39).

Among other things, Silk provides a helpful `Canvas2d` widget which makes it easy to register some code that will
automatically get called for you once per frame.

```kotlin
@Composable
private fun Clock() {
  Canvas2d(300, 300, minDeltaMs = ONE_FRAME_MS_60_FPS) {
    /* This callback handles one frame of canvas rendering. */
  }
}
```

Despite being easy to use, the canvas widget is extremely powerful, and you could use it to create dynamic effects,
full screen backgrounds, or even games.

### Modifier

Anyone who has dabbled with Jetpack Compose is likely familiar with the `Modifier` class. It may seem as fundamental to
Compose as the `@Composable` annotation is.

However, it isn't! Compose HTML actually does not have a `Modifier` class.

Instead, it uses an approach where all HTML tags are converted to `@Composable` function calls that take in something
called an `AttrsScope`.

As a concrete example, this HTML document tag:

```html
<div
   id="example"
   style="width:50px;height:25px;background-color:black"
>
```

would be written with the following Compose HTML code:

```kotlin
Div(attrs = {
    assert(this is AttrsScope)
    id("example")
    style {
        width(50.px)
        height(25.px)
        backgroundColor("black")
    }
})
```

I think this approach is pretty neat, but as `AttrsScope` is a mutable class, that makes it dangerous to store in a
shared variable. Plus, its API doesn't support chaining.

To solve this, Silk provides its own `Modifier` class which is *inspired* by Jetpack Compose's version but isn't exactly
the same one. Still, it should look familiar enough to people who write Jetpack Compose code.

The above Compose HTML `AttrsScope` would be represented by the following `Modifier`:

```kotlin
private val EXAMPLE_MODIFIER = Modifier
    .id("example")
    .width(50.px).height(25.px)
    .backgroundColor(Colors.Black)
```

Silk widgets take modifiers directly:

```kotlin
Button(
    onClick = { /*...*/ },
    modifier = EXAMPLE_MODIFIER
)
```

But for interoperability with Compose HTML elements, it is easy to convert a `Modifier` into an `AttrsScope` on the fly,
using the `toAttrs` method:

```kotlin
Div(attrs = EXAMPLE_MODIFIER.toAttrs())
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
page, specifying their style using a declarative format.*

For example, at one point while working on Kobweb, I used a todo app to learn from, and at least half of the time I
spent was crawling over
[their stylesheet](https://github.com/upstash/redis-examples/blob/master/nextjs-todo/styles/Home.module.css) to
understand the nuances of their approach.

Compose HTML allows you to
[define this stylesheet in code](https://github.com/JetBrains/compose-jb/tree/master/tutorials/HTML/Style_Dsl#stylesheet),
but you can still easily end up with a monolith.

#### CSS style blocks

Kobweb introduces CSS style blocks, which is a fancy way of saying you can define the styles you use in smaller pieces
next to the code that uses them.

It's easy -- just instantiate a `CssStyle` and store the result to a `val`:

```kotlin
val HoverContainerStyle = CssStyle {
    base { Modifier.fontSize(32.px).padding(10.px) }
    hover {
        val highlightColor =
            if (colorMode.isDark) Colors.Pink else Colors.Red
        Modifier.backgroundColor(highlightColor)
    }
}
```

The `base` style, if defined, is special, as it will always be applied first. Any additional declarations are layered on
top of the base if their condition is met.

CSS styles can be converted to `Modifier`s using the `toModifier` method and to `AttrsScope`s using the `toAttrs`
method. This way, you can pass them into either Silk widgets *or* Compose HTML elements:

```kotlin
val HoverContainerStyle = CssStyle { /*...*/ }

// Then later...

// Silk widget:
Box(HoverContainerStyle.toModifier()) { /*...*/ }
    
// Compose HTML element:
Div(attrs = HoverContainerStyle.toAttrs()) { /*...*/ }
```

It is way easier to read your code when your element styles live near where they are used, since you don't have to jump
between the code and a monolithic stylesheet in a different file.

## Markdown

At the beginning of this post, I said this site was written entirely in Kotlin. This may actually be a technicality.

In fact, most of this site is written using markdown. Relevant markdown files are transpiled to Kotlin just before
compilation happens.

Kobweb extends Markdown with some custom support for nesting code inside it which is how I embedded the color buttons
and clock widget above. You can inline code with a Kotlin-y `${...}` syntax or put a larger widget on its own line
with triple curly-brace syntax:

```markdown
# An intro to pathfinding

Here is a demonstration of A-star pathfinding

{{{ .components.widgets.astar.Demo }}}

Play: ${.components.widgets.astar.PlayButton}
Step: ${.components.widgets.astar.StepButton}
```

Code references that start with `.` will automatically be prefixed by your project's base package, so for example all
the code references above would generate final code prefixed with something like `com.example` (but whatever is used by
your project).

You can see [the markdown for this blog post](https://github.com/bitspittle/bitspittle.dev/blob/main/site/src/jsMain/resources/markdown/blog/2022/KotlinSite.md)
for yourself!

Ultimately, Markdown support out-of-the-box means that if you love Kotlin *and* you were thinking of starting a blog,
Kobweb might be a great solution for you.

## Other approaches

Let's finish off by discussing other approaches, to compare and contrast with Kobweb.

If you're already sold on Kobweb, feel free to skip this section and jump straight to the [conclusion▼](#conclusion).

### Compose Multiplatform

Many users in the Kotlin community are excited about the promise of multiplatform, and they want to write an app once
and run it everywhere (Android, iOS, Desktop, *and* Web).

Kobweb is very much *not* that sort of solution. It is designed for developers who want to create a traditional website
but use Kotlin instead of, say, TypeScript.

Kobweb (and Compose HTML) interact with the DOM and let the browser handle rendering it. In contrast, Compose
Multiplatform for Web works by creating an HTML canvas and then rendering your app to it opaquely. If what you really
want to do is write a cross-platform app which just happens to also work in your browser, then Compose Multiplatform is
probably the solution for you.

There's no one-size fits all solution, however, and Kobweb may still be the right choice if you're creating a website.
I wrote about this a bit more in [Kobweb's README](https://github.com/varabyte/kobweb#what-about-compose-for-web-canvas),
in case you wanted to learn more about the different approaches, as well as why you might still choose a traditional
DOM API in a multiplatform world.

### Vanilla Compose HTML

Perhaps you've been burned by frameworks before. "Yeah buddy, Kobweb is nice, but I'm just going to stick with Compose
HTML *classic*."

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
* a ton of CSS properties not found in Compose HTML
* many utility methods and classes for working with the DOM
* Markdown support
* WebSocket support via API streams
* Web worker support via Kobweb Workers
* shape clipping
* composables for all free Font Awesome *and* Material Design icons
* parsing and handling query parameters (e.g. `/posts?userId=...&postId=...`)
* parsing and handling dynamic routes (e.g. `/users/{userId}/posts/{postId}`)
* handling responsive layouts (mobile vs. desktop)
* an experience built from the ground up around live reloading

I mention these not (just) to humblebrag, but because I myself was surprised by what was needed to create an MVP of
Kobweb. I vastly underestimated the scope.

So, sure, I'm biased, but my opinion is that if you're going to use Compose HTML to make a website, you probably want to
at least give Kobweb a try.

### JavaScript / TypeScript

Kotlin/JS may not be for everyone. Most of the webdev community is amassed around JavaScript / TypeScript and libraries
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

If you like what I'm doing with Kobweb but think at this time it makes more sense to use JavaScript / TypeScript for
your project, check out [Next.js](https://nextjs.org/) paired with [Chakra UI](https://chakra-ui.com/), as both of these
solutions were huge inspirations for me.

## Conclusion

I've been very excited about the Kotlin webdev space ever since Compose HTML was announced, and I hope this post has
pushed at least one other person over the fence.

### Trying Kobweb

If Kobweb looks like something you'd want to play with, the easiest way to start is by
[installing the Kobweb binary](https://github.com/varabyte/kobweb#install-the-kobweb-binary).

Once installed, you can run:

```bash
$ kobweb create app
# answer a bunch of questions about your project
$ cd app/site
$ kobweb run
```

Or if this post just made you curious about Compose HTML, you can start with the
[official tutorial](https://github.com/JetBrains/compose-jb/tree/master/tutorials/HTML/Getting_Started) but have Kobweb
set it up for you in a few seconds:

```bash
$ kobweb create examples/jb/counter
$ cd counter/site
$ kobweb run
```

And finally, if after reading this you are thinking about using Kobweb, consider jumping into our
[Discord server](https://discord.gg/5NZ2GKV5Cs), where I'd be happy to answer questions about Kobweb when I'm around.

### The future

I can't predict if Kotlin webdev will ever take off, much less Kobweb itself. But I sincerely want a future where there
are more Kotlin developers owning codebases that cross the full stack. If I can help throw some code over the wall to
help make the experience better, then I'm happy to have tried.

At the very least, Kobweb will always have one user -- this site!
