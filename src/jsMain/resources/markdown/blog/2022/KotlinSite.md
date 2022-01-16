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

It wasn't trivial, either -- this is the culmination of a framework that has (so far) taken six months of solid effort
to write.

So why the heck did I do choose Kotlin for BitSpittle.dev? And what did I find to be the pros and cons?

## Kotlin/JS

I've been using Kotlin for several years now. For me, it hits a sweet spot of expressiveness and conciseness.

But when I say that I've written Kotlin, I really mean Android, desktop, and server code. In contrast, I've honestly
always viewed Kotlin/JS with a bit of suspicion.

I mean, I'm glad it exists -- it sure seems like an audacious bet -- but I tend to feel it usually isn't the right tool
for the job.

There's a lot of friction at play just to start a Kotlin/JS project: you still need to learn html / css concepts, you
end up mixing Gradle *and* npm, you lose access to the broader JVM ecosystem, and you start working with APIs that take
`dynamic` arguments.

Not to mention, there isn't really a Kotlin webdev community of significant size at this point. I'm *still* not sure
if it will ever get there. I'm trying to imagine a world where startups are scrambling to hire "Kotlin frontend devs"
and, despite my love for the language, I'm not seeing it.

## TypeScript

So, six months ago, when I found myself wanting to learn more about frontend development, I began teaching myself
TypeScript.

It was... OK. I think TypeScript in a bubble would not be as loved as much as it is. Instead, I think Microsoft has done
a terrific job seatbelting JavaScript, and people may appreciate that someone has muzzled a wailing baby, but that
doesn't make a language great when judged on its own merits.

I actually couldn't believe how awkward it was to enforce types when defining React functions in my code. In practice,
I notice most users just omit them and escape hatch to JavaScript's laissez-faire attitude in those cases.

That's crazy if you think about! React is the hot framework in the
frontend world, and TypeScript the hot language, but the two dance very awkwardly together.

## Next.js and Chakra UI

Despite that awkwardness

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