---
root: .components.layouts.BlogLayout
title: Static Site Generation and Deployment with Kobweb
description: How to use Kobweb to build a Compose for Web site that can be served by static site hosting providers for cheap (or free)!
author: David Herman
date: 2022-02-11
updated: 2022-02-14
tags:
 - compose for web
 - kobweb
---

[Kobweb](https://github.com/varabyte/kobweb) is a framework built on 
[Compose for Web](https://compose-web.ui.pages.jetbrains.team/), which itself is a reactive web UI framework from
JetBrains that allows you to create web apps in Kotlin using a powerful API.

You can also read more about Kobweb [in a previous post](kotlinsite).

In this post, we'll talk about how to use Kobweb to export your Compose for Web project into a format that can be
consumed by various static website hosting providers. This means you can get fast and cheap (often free!) hosting
for your Kotlin website.

## Background

These background sections are provided for people new to the world of frontend development and/or who are curious about
Kobweb.

However, if you're ready to get your hands dirty, [feel free to skip over them▼](#how-to-deploy-a-static-website).

### Compose for Web / SPAs

Compose for Web is an excellent tool for building a
[single-page application (SPA)](https://en.wikipedia.org/wiki/Single-page_application).

That is, it produces a single, minimal `index.html` file plus some JavaScript and other resources that can be used to
build your app up at runtime. Once the page is loaded by the browser and the script starts running, it continuously
modifies the page's DOM in place to give the user the illusion that they're actually navigating around multiple pages as
they click around on stuff.

---

Let's use this blog as a concrete example.

For a Compose for Web project, if a user entered a URL like `https://bitspittle.dev/blog/static`, the request would be
intercepted before the browser could handle it, and its URL path would get parsed.

Based on the result (in this case, the value `/blog/static`), your project would dynamically choose to start rendering a
new screen associated with that name (so, maybe `mysite.pages.blog.StaticPage()`). The core of your project is
essentially a giant switch statement acting on a string value.

The above is fine as long as your server understands that this is happening! In other words, if I make a request to a
server asking for resources associated with `/blog/static`, it should just send me the default `index.html` page and
its JavaScript.

But static website host providers are simple. They blindly serve static files.

So if a user makes a request to a static website host provider for the path `/blog/static`, then a file called
`blog/static.html` better exist on it or else that user is getting a 404 error.

### Kobweb to the rescue

Unlike Compose for Web, Kobweb can handle this problem because it sits one level above it. It is aware of all the
pages on your site (since it is the one that generates the routing logic for you), and it comes with a binary that can
run a bunch of useful, project-managing commands.

The important command in this case:

```bash
$ kobweb export --layout static
```

When you ask Kobweb to export your site, it will spin up a local Kobweb server, visit each page in turn, and
save out its state to an html file. In this way, Kobweb can turn your dynamic Compose for Web pages into static
snapshots.

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
$ kobweb create site
# Kobweb asks a bunch of questions, but defaults should be fine
$ cd site
```

and initialize it with `git`:

```bash
$ git init -b main
$ git add . && git commit -m "Initial commit"
```

#### Create a new GitHub repository

[Follow the official instructions to create a new GitHub repository](https://docs.github.com/en/get-started/importing-your-projects-to-github/importing-source-code-to-github/adding-an-existing-project-to-github-using-the-command-line#adding-a-project-to-github-without-github-cli).
You can choose whatever name you want. I used `kobweb-ghp-demo`.

When given an opportunity to populate this repo with a `README` and `.gitignore`, **don't**! Since Kobweb already
creates them for you.

When finished, sync your local repo with the GitHub repo:

```bash
# REMOTE_URL looks something like
# https://github.com/<user>/<repo>.git
$ git remote add origin <REMOTE_URL>
$ git pull origin main
$ git push --set-upstream origin main
```

### Netlify

***Note:** If you want to use GitHub Pages instead, [skip to that section▼](#github-pages).*

Netlify is becoming a popular solution for developers who want to create static websites that get served *fast*. They
detect changes to your GitHub repository and publish your site in seconds.

#### Sign up for a Netlify account

It's free. [Sign up here](https://app.netlify.com/signup)!

#### Integrate Netlify with your repo

* Go to your dashboard on your Netlify page
* Click on the `Add new site` button
* Choose `Import an Existing Project`
* Choose `GitHub` as your git provider
* Follow any authorization steps to tell Netlify about your new Kobweb repo
* Choose your repo from the list

Eventually, you will reach a page that asks you to provide build settings. Leave everything blank except for the
`publish directory` field, which you can set to `.kobweb/site`:

![Netlify Build settings](/images/blog/2022/staticdeploy/netlify-build-settings.png)

#### Allow `.kobweb/site` in gitignore

By default, Kobweb is set up so that you don't check your exported site into source control.

However, for simplicity with Netlify's workflow, we're going to commit our exported site directly into our repository.

Open up the `.gitignore` file in your project's root and add the line `!.kobweb/site` to the bottom:

```text
...

# Kobweb ignores
.kobweb/*
!.kobweb/conf.yaml
!.kobweb/conf.yaml.ftl
!.kobweb/template.yaml
!.kobweb/site
```

#### Export your site

```bash
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

![Netlify dashboard](/images/blog/2022/staticdeploy/netlify-dashboard.png)

Click on it, and you should be taken to a page that has your URL in it:

![Netlify overview](/images/blog/2022/staticdeploy/netlify-overview.png)

If you click on the link, you should see a site that looks [like this](https://peaceful-hermann-be6fdf.netlify.app/).

If so, congratulations! You're done. 🎉

If you're still having issues, feel free to compare your project
[with mine](https://github.com/bitspittle/kobweb-netlify-demo).

### GitHub Pages

***Note:** If you want to use Netlify instead, [go back to that section▲](#netlify).*

There are a few options for configuring GitHub Pages, and discussing them all is out of scope for this post. Instead,
we'll go with the easiest -- using a `docs/` root within your project.

#### GitHub repo settings

* Go to your repo's project on GitHub and click on the `Settings` tab
* In the `Code and automation` section of the sidebar, click `Pages`
* In the `Source` section, set `Branch` to `main` and the folder to `/docs`
* Click `Save`

![GitHub Pages source](/images/blog/2022/staticdeploy/ghp-source.png)

#### Configure Kobweb

As you can see, we don't have a lot of control over GitHub Pages. Since we can't change GitHub, we must change ourselves
instead.

An additional wrinkle is that GitHub Pages deploys your site to a subfolder. This will look something like
`https://<user>.github.io/<project>/`. This means that if you try to navigate to the root in your Kobweb site
(i.e. `Link("/")`), or reference resources from the resource root (e.g. `/images/example.png`), the browser will think
you're asking to search against `https://<user>.github.io` instead of the subdirectory!

Because GitHub Pages requires you to put your files under `docs/`, and also because it serves your site under a
subfolder instead of the root, you will need to modify two values in your `.kobweb/conf.yaml`:

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
      siteRoot: "docs"
```

#### Export your site

```bash
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
besides the two listed here, including powerhouses such as
[Google Cloud Storage](https://cloud.google.com/storage/docs/hosting-static-website) and
[AWS](https://aws.amazon.com/getting-started/hands-on/host-static-website/).

Compose for Web is an amazing API. And static website hosting is an amazing service. If you use Kobweb, you won't have
to choose one or the other, but you can revel in the benefits of both!