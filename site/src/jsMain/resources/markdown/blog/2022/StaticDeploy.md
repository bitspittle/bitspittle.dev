---
root: .components.layouts.BlogLayout
title: Static Site Generation and Deployment with Kobweb
description: How to use Kobweb to build a Compose HTML site that can be served by static site hosting providers for cheap (or free)!
author: David Herman
date: 2022-02-11
updated: 2024-09-03
tags:
 - compose html
 - kobweb
 - static site
---

[Kobweb](https://github.com/varabyte/kobweb) is a framework built on 
[Compose HTML](https://github.com/JetBrains/compose-multiplatform#compose-html), which itself is a reactive web UI framework from
JetBrains that allows you to create web apps in Kotlin using a powerful API.

> [!NOTE]
> You can also read more about Kobweb [from this earlier post](KotlinSite.md).

In this post, we'll talk about how to use Kobweb to export your Compose HTML project into a format that can be consumed
by various static website hosting providers. This means you can get fast and cheap (often free!) hosting for your Kotlin
website.

## Background

These background sections are provided for people new to the world of frontend development and/or who are curious about
Kobweb.

However, if you're ready to get your hands dirty, [feel free to skip over them▼](#how-to-deploy-a-static-website).

### Compose HTML / SPAs

Compose HTML is an excellent tool for building a
[single-page application (SPA)](https://en.wikipedia.org/wiki/Single-page_application).

That is, it produces a single, minimal `index.html` file plus some JavaScript that can be used to rearrange your app at
runtime.

Once the page is loaded by the browser and its script starts running, it continuously modifies the page's DOM in place
to give the user the illusion that they're actually navigating around multiple pages as they click around on stuff.

---

Let's discuss a concrete example.

Assume you already navigated onto a Compose HTML site, say `https://mysite.com`.

Next, you click on a link on the home page that takes you to another page on the site, like
`https://mysite.dev/blog/about-me`. The site actually intercepts the navigation request and prevents the browser from
handling it.

At this point, the URL path gets parsed. Based on the result (in this case, the value `"/blog/about-me"`), the site will
dynamically choose to start rendering a new page associated with that path (perhaps `mysite.pages.blog.AboutMePage()`).

In other words, the core of your project is essentially a giant switch statement acting on a string value. You can
imagine something like the following pseudocode:

```kotlin
// Inside your main `renderComposable`
val path = getPath() // Path updated when browser URL changes
when (path) {
    "/" -> mysite.pages.HomePage()
    "/blog/about-me" -> mysite.pages.blog.AboutMePage()
    "/blog/kobweb-tutorial" -> mysite.pages.blog.KobwebTutorialPage()
    // ... etc. ...
}
```

Now, let's say my use-case is I want to visit `https://mysite.dev/blog/about-me` directly. Perhaps my friend sent me
that link on a social media post.

With Compose HTML, what I really want to do is visit `https://mysite.dev` and allow it to intercept the URL value and
re-render the page with new content.

If you want your site to be served by a static website host provider, you should be aware that they are very simple.
They blindly serve static files.

So if a user makes a request to a static website host provider for the path `/blog/about-me`, then a file called
`blog/about-me.html` better exist on it or else that user is getting a 404 error.

> [!NOTE]
> Some static website host providers actually allow configuring rules to allow redirecting to a fallback page, but
> sometimes it's a hack (like creating a fake `404.html` page) while many simply do not. We'll sidestep this nuance for
> the rest of this article, as the approach discussed below should work universally across all static hosting providers.

### Kobweb to the rescue

Unlike Compose HTML, Kobweb can handle this problem because it sits one level above it. It is aware of all the pages on
your site (since it is the one that generates the routing logic for you).

A major part of the Kobweb experience is its CLI binary. The relevant command in this case is:

```bash
$ kobweb export --layout static
```

When you ask Kobweb to export your site, it will spin up a local Kobweb server, visit each page in turn, and
save out its state to an html file. In this way, Kobweb can turn your dynamic Compose HTML pages into static snapshots.

## How to deploy a static website

For this article, we'll discuss two options, for two very popular (and, more importantly, free!) providers: **Netlify**
and **GitHub Pages**.

***Note:** I am not affiliated with or sponsored by Netlify or GitHub in any way. Their inclusion here is because they
are what users in my [Discord server](https://discord.gg/5NZ2GKV5Cs) mentioned they were familiar with.*

We'll start with steps common to both approaches. 

### Common steps

**Requirements**

* `git`
* A GitHub account / familiarity with GitHub
* The `kobweb` binary ([installation instructions](https://github.com/varabyte/kobweb#install-the-kobweb-binary))

#### Create a project

If you already have a project, you can skip this step.

Otherwise, run the following command, so you'll have something concrete to work with for the rest of this article:

```bash
$ kobweb create app
# Kobweb asks a bunch of questions, but defaults should be fine
$ cd app
```

The above steps should have offered to initialize your project with `git`, but if you told it not to or if it didn't
work for some reason, you can manually initialize it yourself:

```bash
$ git init -b main
$ git add . && git commit -m "Initial commit"
```

#### Create a new GitHub repository

[Follow the official instructions to create a new GitHub repository](https://docs.github.com/en/get-started/importing-your-projects-to-github/importing-source-code-to-github/adding-an-existing-project-to-github-using-the-command-line#adding-a-project-to-github-without-github-cli).
You can choose whatever name you want. I used `kobweb-netlify-demo` for Netlify and `kobweb-ghp-demo` for GitHub Pages.

When given an opportunity to populate this repo with a `README` and `.gitignore`, **don't**! Since Kobweb already
creates them for you.

When finished, sync your local project with the GitHub repo:

```bash
# REMOTE_URL looks something like
# https://github.com/<user>/<repo>.git
$ git remote add origin <REMOTE_URL>
$ git push -u origin main
```

### Netlify

***Note:** You should have finished the [common steps▲](#common-steps) first. If you want to use GitHub Pages instead, [skip to that section▼](#github-pages).*

Netlify is becoming a popular solution for developers who want to create static websites that get served *fast*. They
detect changes to your GitHub repository and publish your site in seconds.

#### Sign up for a Netlify account

It's free! [Sign up here](https://app.netlify.com/signup).

#### Integrate Netlify with your repo

* Go to your dashboard on your Netlify page
* Click on the `Add new site` button
* Choose `Import an Existing Project`
* Choose `GitHub` as your git provider
* Follow any authorization steps to tell Netlify about your new Kobweb repo
* Choose your repo from the list

Eventually, you will reach a page that asks you to provide build settings. Leave everything blank except for the
`publish directory` field, which you can set to `site/.kobweb/site`:

![Netlify Build settings](/images/blog/2022/static-deploy/netlify-build-settings.png)

#### Allow `.kobweb/site` in gitignore

By default, Kobweb is set up so that you don't check your exported site into source control.

However, for simplicity with Netlify's workflow, we're going to commit our exported site directly into our repository.

Open up the `.gitignore` file in your project's `site/` folder and add the line `!.kobweb/site` to the bottom:

```text
...

# Kobweb ignores
.kobweb/*
!.kobweb/conf.yaml
!.kobweb/site
```

#### Export your site

```bash
# in kobweb-netlify-demo/site/...
$ kobweb export --layout static
```

This will run for a little while. When finished, run

```bash
$ git status
```

to verify that new files are now ready to be added.

If not, double-check your `.gitignore` changes from the last step and also make sure that files were actually written to
your `.kobweb/site` folder.

#### Push your site

```bash
$ git add . && git commit -m "Exported site"
$ git push
```

#### Netlify: Finished!

If everything went well, you should have a page that is either deployed or well on its way! It only takes a few seconds
once Netlify is aware of the pushed changes.

Go to your Netlify dashboard. You should see an entry like the one I have here:

![Netlify dashboard](/images/blog/2022/static-deploy/netlify-dashboard.png)

Click on it, and you should be taken to a page that has your URL in it:

![Netlify overview](/images/blog/2022/static-deploy/netlify-overview.png)

If you click on the link, you should see a site that looks [like this](https://peaceful-hermann-be6fdf.netlify.app/).

If so, congratulations! You're done. 🎉

If you're still having issues, feel free to compare your project
[with mine](https://github.com/bitspittle/kobweb-netlify-demo).

### GitHub Pages

***Note:** You should have finished the [common steps▲](#common-steps) first. If you want to use Netlify instead,
[go back to that section▲](#netlify).*

There are a few options for configuring GitHub Pages, and discussing them all is out of scope for this post. Instead,
we'll go with the easiest -- using a `docs/` root within your project.

#### GitHub repo settings

* Go to your repo's project on GitHub and click on the `Settings` tab
* In the `Code and automation` section of the sidebar, click `Pages`
* In the `Source` section, set `Branch` to `main` and the folder to `/docs`
* Click `Save`

![GitHub Pages source](/images/blog/2022/static-deploy/ghp-source.png)

#### Configure Kobweb

As you can see, we don't have a lot of control over GitHub Pages. Since we can't change GitHub, we must change ourselves
instead.

An additional wrinkle is that GitHub Pages deploys your site to a subfolder. This will look something like
`https://<user>.github.io/<project>/`. This means that if you try to navigate to the root in your Kobweb site
(i.e. `Link("/")`), or reference resources from the resource root (e.g. `/images/example.png`), the browser will think
you're asking to search against `https://<user>.github.io` instead of the subdirectory!

Because GitHub Pages requires you to put your files under `docs/`, and also because it serves your site under a
subfolder instead of the root, you will need to modify two values in your `.kobweb/conf.yaml`, "routePrefix" and
"siteRoot":

```yaml
site:
  title: "..."
  routePrefix: "<repo-project-name>"
  # i.e. the name you chose for your repo.
  # In my case, the value: "kobweb-ghp-demo"
  # but your name is probably different...

server:
  files:
    dev:
      contentRoot: "..."
      script: "..."
      api: "..."
    prod:
      # Kobweb content is in a subfolder. Need to export to the root, so use ".."
      siteRoot: "../docs"
```

#### Export your site

```bash
# in kobweb-ghp-demo/site/...
$ kobweb export --layout static
```

This will run for a little while. When finished, run

```bash
$ git status
```

to verify that new files are now ready to be added.

#### Push your site

```bash
$ git add . && git commit -m "Exported site"
$ git push
```

#### GitHub Pages: Finished!

If everything went well, you should have a page that is either deployed or well on its way! It takes less than a minute
once GitHub is aware of the pushed changes.

Once it's ready, you can visit your GitHub Pages site, which uses a URL with a format like
`https://<user>.github.io/<project>`.

For example, my site is at https://bitspittle.github.io/kobweb-ghp-demo/.

Are you seeing something similar at your link? If so, congratulations! You're done. 🥳

If you're still having issues, feel free to compare your project
[with mine](https://github.com/bitspittle/kobweb-ghp-demo).

## Conclusion

As you can see, static website hosting is cheap, fast, and easy to set up. There are a lot of options you can use
besides the two listed here, including other popular features such as
[Firebase Hosting](https://firebase.google.com/docs/hosting) and
[AWS](https://aws.amazon.com/getting-started/hands-on/host-static-website/).

Compose HTML is an amazing API. And static website hosting is an amazing service. If you use Kobweb, you won't have
to choose one or the other, but you can revel in the benefits of both!
