---
root: .components.layouts.BlogLayout
title: This site is written in Kotlin
description: A bit about Kobweb and writing a blog site in Kotlin
date: 2022-02-02
tags:
 - kotlin/js
 - compose for web
 - webdev
 - kobweb
---

I wrote a thing -- a Kotlin web framework called [Kobweb](https://github.com/varabyte/kobweb).

It is built on top of [Compose for Web](https://compose-web.ui.pages.jetbrains.team/), an official, and fairly recent,
reactive web framework created by JetBrains (in close collaboration with Google, and based on *their* Jetpack Compose
API).

And this whole site, *including this very page you are now perusing*, is built using it.

In this post, I'll introduce some Kobweb basics, and why you might (or might not!) want to use it.

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

***Note:** The URL comes from the file name, not the function name. You can name the function anything you want, but
keeping it the same as the file name (with the suffix -Page) is the recommended convention.*

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

Kobweb can be used on its own for its routing capabilities, but it also provides a library called Silk, a
color-mode-aware (i.e. light and dark) collection of widgets as well as general theming and component styling support.

I believe component styling could forever change how you will want to create widgets in Kotlin web, and I will
demonstrate it later in its own subsection.

### Modifier

Anyone who has dabbled with Jetpack Compose is likely very familiar with the `Modifier` class. It may seem as
fundamental to Compose as the `@Composable` annotation is.

However, it isn't! Compose for Web actually does not have a `Modifier` class.

Instead, it uses an approach where all HTML tags are converted to `@Composable` function calls that take in something
called an `AttrsBuilder`.

As a concrete example, the HTML document tag:

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
the same one. Still, it should look familiar enough to people who write Jetpack Compose code, and may help ease Kotlin
developers just getting started with frontend development into this strange, new world.

Silk widgets take modifiers directly, but for interoperability with Compose for Web widgests, it is easy to convert a
`Modifier` into an `AttrsBuilder` on the fly, using the `asAttributeBuilder` method:

```kotlin
// The above example, but using a Modifier instead
private val EXAMPLE_MODIFIER = Modifier
    .id("example")
    .width(50.px).height(25.px)
    .backgroundColor(Colors.Black)

Div(attrs = EXAMPLE_MODIFIER.asAttributeBuilder())
```

With `Modifier`s, chaining is easy using the `then` method:

```kotlin
private val SIZE_MODIFIER = Modifier.size(50.px)
private val SPACING_MODIFIER = Modifier.margin(10.px).padding(20.px)

Div(attrs = SIZE_MODIFIER.then(SPACING_MODIFIER).asAttributeBuilder())
```

If you ever encounter a situation where Kobweb hasn't yet added support for a property or attribute you need, you can
use the fallback `attrModifer` and `styleModifier` methods:

```kotlin
private val ATTR_AND_STYLES_MODIFIER = Modifier.attrModifier {
    id("example")
    style {
        width(50.px)
        height(50.px)
        backgroundColor("black")
    }
}

private val JUST_STYLES_MODIFIER = Modifier.styleModifier {
    width(50.px)
    height(50.px)
    backgroundColor("black")
}
```

***Note:** The `attrModifier` method is a superset of `styleModifier`. However, in practice, you usually won't be
defining attributes, just style properties, so fewer lines and reduced indentation via `styleModifier` is better in
that case.*

### Color mode

Did you happen to see the color toggling button at the top-right of the site? No need to move your cursor -- I'll create
another copy here: {.components.widgets.button.ColorModeButton}

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

### Component Styling

Most frontend projects have a single, giant, terrifying stylesheet (or, worse, several giant, terrifying stylesheets)
driving the look and feel of their site.

***Aside:** If you don't know what a stylesheet is, it's a collection of CSS rules that target various elements on your
page using a declarative format.*

For example, at one point while working on Kobweb, I used a todo app to learn from, and at least half of the time I
spent was crawling over
[their stylesheet](https://github.com/upstash/redis-examples/blob/master/nextjs-todo/styles/Home.module.css) to
understand the nuances of their approach.

Compose for Web allows you to define this stylesheet in code, but you can still easily end up with a monolith.

Kobweb introduces **component styling**, which is a fancy way of saying you can define the styles you use *next to your
code*.

At compile time, Kobweb stitches all your component styles together into one giant stylesheet behind your back, but you
get the benefit of seeing them as local styles when you read the code.

It's easy enough to declare one. You just have to create a `ComponentStyle` and pass in a unique name. Choose a name
that is simple and clear, because it might help you if you need to debug your page using browser tools later:

```kotlin
val SomeWidgetStyle = ComponentStyle("some-widget") {
    base { Modifier.fontSize(32.px).padding(10.px) }
    hover {
        val highlight =
            if (colorMode.isDark()) Colors.Pink else Colors.Red
        Modifier.backgroundColor(highlight)
    }
}
```

The `base` style, if defined, is special, as it will always be applied first. Any additional declarations are layered on
top of the base if their condition is met.

Note above that the `colorMode` value is available within the scope of a `ComponentStyle` block, which you can use to
easily toggle theming behavior based on what color mode your site is in.

Finally, component styles, once defined, can be converted to `Modifier`s easily, using the `toModifier` method. After
that, it's
pretty standard to pass them into Silk widgets or Compose for Web elements as normal:

```kotlin
val SomeWidgetStyle = ComponentStyle("some-widget") { /*...*/ }

@Composable
fun SomeWidget() {
    val widgetModifier = SomeWidgetStyle.toModifier()
    Div(attrs = widgetModifier.asAttributeBuilder()) {
       /*...*/
    }
}
```

It is way easier to read your code when it all relevant pieces live near each other, and you don't have to jump between
the code and a monolothic stylesheet in a different file.

### Canvas

So far, most of this post has been text. But honestly -- text is static. How droll.

This is the future! Users of the web3 era demand more.

Of course, using Kotlin, you can create dynamic elements by rendering to a canvas. The following clock is adapted from
[this Mozilla canvas example](https://developer.mozilla.org/en-US/docs/Web/API/Canvas_API/Tutorial/Basic_animations#an_animated_clock)
that was originally written in JavaScript.

{{{ .components.widgets.blog.kotlinsite.DemoWidget }}}

Here's the [Kotlin source](https://github.com/bitspittle/bitspittle.dev/tree/main/src/jsMain/kotlin/dev/bitspittle/site/components/widgets/blog/kotlinsite/DemoWidget).
Among other things, Silk provides a helpful `Canvas` widget which makes it easy to register some code that will
automatically get called for you once per frame.

Plus, thanks to Silk, it was trivial to make the clock color mode aware. You can click this color mode button
{.components.widgets.button.ColorModeButton} to observe the changes yourself.

The canvas widget is extremely powerful, and you could use it to create extremely fancy effects, backgrounds, or even
games.

## Markdown

At the beginning of this post, I said this site was written entirely in Kotlin. This may only not be a lie based on a
technicality.

In fact, most of this site is actually written using markdown. Relevant markdown files are transpiled to Kotlin at
compile time. You can even find them beneath your project's `build/generated` folder (but I wouldn't recommend it...)

Kobweb extends markdown with some custom support for nesting code inside it, which is how I embedded the color buttons
and clock widget above.

```markdown
<!-- Markdown extended with Kobweb syntax -->

# This site is written in Kotlin

Blah blah here's a color button:

{{{ .components.widgets.ColorButton }}}

Wow this is some engaging writing.
```

Ultimately, what this means is if you love Kotlin *and* you were thinking of starting a blog, Kobweb might be a great
solution for you out of the box!

## Other approaches

Let's finish off by discussing other approaches, to compare and contrast with Kobweb.

### Compose for Web on Canvas

It's very important to emphasize that, at the moment of writing this post, Kobweb is very much *not* a multiplatform
solution. It is designed to enable developers who are intentionally setting out to create a traditional website and want
to write it in Kotlin instead of, say, typescript (e.g. to share common logic between the website and the backend).

However, most users in the community want to write a web app once and run it everywhere (Android, Desktop, *and* Web).
And you should know, JetBrains is actively working towards enabling this workflow.

The way it will work is, you allocate a large web canvas in your html page and pass it to their API. This allows them to
render opaquely to it. Once they have full control of the rendering pipeline, they can then use the original Jetpack
Compose APIs without worrying about anything like html or css *at all*.

If all you're doing is creating a cross-platform app, it may be worth waiting for this approach.

I write about this a bit more in [Kobweb's README](https://github.com/varabyte/kobweb#what-about-multiplatform-widgets),
in case you wanted to learn more about this.

### Vanilla Compose for Web

Perhaps you've been burned by frameworks before. "Yeah buddy, Kobweb is nice, but I'm just going to stick with Compose
for Web *classic*".

That's fine with me! Just be aware, this post only scratched the surface of what Kobweb can do for you. Here's a fuller
list of features to consider, since without Kobweb, you may need to implement some of them yourself:

* creating Gradle and index.html boilerplate
* site routing
* running and configuring a server
* defining and connecting to server API routes
* organizing your stylesheets
* light and dark color mode support and theming
* a (growing) collection of color-mode aware widgets
* introduction of the Modifier concept, useful for chaining styles
* Implementations for Box, Column, and Row on top of html / css
* Markdown support
* Font Awesome icons
* parsing and handling query parameters (e.g. `/posts?userId=...&postId=...`)
* parsing and handling dynamic routes (e.g. `/users/{userId}/posts/{postId}`"` )
* handling responsive layouts (mobile vs. desktop)
* site exports, for SEO and/or serving pages of your site statically
* an experience built from day 1 around live reloading
* a "building / failed" indicator shown above your page while code is recompiling

I mention these not (just) to humblebrag, but because I myself was surprised by what was needed to create an MVP of
Kobweb. I vastly underestimated the space.

So, sure, I'm biased, but my opinion is that if you're going to use Compose for Web to make a website (as opposed to a
web app), you probably want to at least give Kobweb a try.

### JavaScript/TypeScript

Kotlin/JS may not be for everyone. Most of webdev community is amassed around JavaScript/TypeScript and frameworks like
React.

There are a lot of advantages to sticking with the crowd in this case. And not just because they have a huge headstart
in the space. Compile times tend to be a lot faster, the community is huge, and there's a ton of interesting projects
out there to learn from.

And, honestly, TypeScript has many programmers who vouch for it and say they enjoy working with it. The language has
really done a great job adding seatbelts, helmets, and full body cushions to JavaScript, which itself is still evolving
and getting better over time.

That said, with an ever-growing number of Android *and* backend Kotlin developers out there, I think it becomes more and
more likely that companies may start opting to share code across the full-stack.

## Conclusion

I've been very excited about the Kotlin webdev space ever since Compose for Web was announced, and I hope this post
has hooked some of you in, too.

If Kobweb looks like something you'd want to play with, start by
[installing the Kobweb binary]( https://github.com/varabyte/kobweb#install-the-kobweb-binary).

Once you've done that, you can run:

```bash
$ kobweb create site
# answer a bunch of questions about your project
$ cd site
$ kobweb run
```

If this post made you curious about Compose for Web in general but not yet ready to commit to Kobweb, that's fine -- you
can start with the [official tutorial](https://github.com/JetBrains/compose-jb/tree/master/tutorials/Web/Getting_Started)
but have Kobweb set it up for you in a few seconds:

```bash
$ kobweb create examples/jb/counter
$ cd counter
$ kobweb run
```

I can't see the future, and I have no idea if Kotlin webdev will ever take off, much less Kobweb itself. But I sincerely
want a future where there are more Kotlin developers writing codebases across the full stack. If I can help throw some
code over the wall to help make the experience better, then I'm happy to have tried!

At the very least, Kobweb will always have one user -- this site.
