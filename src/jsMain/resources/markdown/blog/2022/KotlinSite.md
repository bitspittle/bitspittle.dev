---
root: .components.layouts.BlogLayout
title: This site is written in Kotlin
description: A bit about Kobweb, and why I'm writing this site in Kotlin
date: 2022-01-08
tags:
 - kotlin/js
 - web compose
 - webdev
---

(... well, and markdown, if you want to get technical.)

To make this site, I could have used TypeScript, or GitHub Pages, or some low-code or no-code site builder. But here I
am, using Kotlin.

It wasn't a trivial choice, either -- this is the culmination of a framework that has (so far) taken six months of solid
effort to write.

So how did I end up here?! I'll move quickly, but it all started six months ago. I quit my last job to take my first
break in over two decades, and I found myself wanting to learn more about frontend development...

## Kotlin/JS

I've been using Kotlin for several years now. For me, it hits a sweet spot of expressiveness and conciseness.

But when I say that I've written Kotlin, I really mean Android, desktop, and server code. In contrast, I've honestly
always viewed Kotlin/JS with a bit of suspicion.

There's a lot of friction at play just to start a Kotlin/JS project: you still need to learn html / css concepts, you
end up mixing Gradle *and* npm, you lose access to the broader JVM ecosystem, and you start working with APIs that take
`dynamic` arguments.

Not to mention, I'm not aware of there being a Kotlin webdev community of significant size at this point. I'm *still*
not sure if we will ever get there. I'm trying to imagine a world where startups are scrambling to hire
"Kotlin frontend devs" and, despite my love for the language, I'm not seeing it.

## TypeScript

So, I began teaching myself TypeScript. I went in with high expectations but in the end felt that it was just... OK.

I was surprised how awkward it was to enforce types when defining React functions in my code. In practice, I notice most
users just omit types and escape hatch to JavaScript's laissez-faire attitude in several cases.

That's crazy if you think about! React is the hot framework in the frontend world, and TypeScript the hot language in
said same world, but the two dance very awkwardly together.

Part of the pain was losing my most comfortable language *and* my most comfortable IDE at the same time.

WebStorm is not free. They do have a license for open source projects, but there's a chci


## Next.js and Chakra UI

It wasn't long before I stumbled upon Next.js as a recommended framework for setting up a TypeScript / JavaScript
project. And the experience was so good. You can go from absolutely nothing to a live-reloading project with a super
easy mental model.

In fact the experience feels so simple that it caused me to under-estimate the space quite a bit. 

## Web Compose


## Advantages

* IntelliJ IDEA
* Receiver methods
* No ===
* No inconsistent lambda / scoping syntax
* Kotlin
* Sharing code between frontend and backend
* Interactive components

## Disadvantages

* Compile times
* Debugging story
* Java packages
* Web APIs
* Community

## Conclusion

So yeah, I chose Kotlin. And hey, even if it doesn't take off, if you're reading this, it means it's at least working
fine in this one

I realized I wanted a future to exist where maybe people *could* write frontend apps in Kotlin, 

Happy with Kobweb, but I'm not sure I could recommend it. It may be a long time and maybe never where we'd expect a
startup to ask for I think Kotlin/JS will remain a niche, but big enough t