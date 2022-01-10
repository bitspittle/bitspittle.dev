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

It wasn't trivial, either -- this is the culmination of a framework that has (so far) taken five months of solid effort
to write.

So why the heck did I do choose Kotlin for BitSpittle.dev? And what are the pros and cons?

To answer these questions, I need to start with some background for context.

## Kotlin/JS

I've been using Kotlin for several years now. For me, it hits a sweet spot of expressiveness and conciseness.

But when I say that I've written Kotlin, I really mean Android, desktop, and backend code. In contrast, I've always
viewed Kotlin/JS with a bit of suspicion.

I mean, I'm glad it exists -- it sure seems like an audacious bet -- but I worried it wasn't the right tool for the job.
There's a lot of friction at play just to start a Kotlin/JS project: you still need to learn html / css concepts, you
end up mixing Gradle *and* npm, you lose access to the broader JVM ecosystem, and a lot of the web APIs are dynamic
(and poorly documented on the JetBrains side in my opinion).

There isn't really a Kotlin webdev community of significant size at this point, and I'm not sure what it would take to
get there. I'm just saying, I think it will be awhile before you can throw a rock and hit a bunch of job openings
looking for Kotlin frontend devs.

## TypeScript and next.js

So, six months ago, when I found myself wanting to learn more about frontend development, I began teaching myself
TypeScript.

It was... OK. I'm super impressed with the choices Microsoft made when designing the language, but I also found myself
feeling that the more I learned, the more I would dread giving TypeScript code reviews. First of all, the language is
built on top of JavaScript, and while things have come a long way with ES6, there are still some fundamental flaws that
can never be paved over -- `===`, a `this` scope that's hard to reason about, and `override` logic 



## Web Compose

But when I say using, I mean I've used it for 

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