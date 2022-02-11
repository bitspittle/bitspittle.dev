---
root: .components.layouts.BlogLayout
title: Static Site Deployment with Kobweb
description: How to use Kobweb to build a Compose for Web site that can be served by static site hosting providers for cheap (or free)!
author: David Herman
date: 2022-02-10
tags:
 - compose for web
 - kobweb
---

[Kobweb](https://github.com/varabyte/kobweb) is a framework built on 
[Compose for Web](https://compose-web.ui.pages.jetbrains.team/), which itself is a reactive web framework from JetBrains
that allows you to create web apps in Kotlin using a powerful API.

In this post, we'll talk about how to use Kobweb to export your Compose for Web project into a format that can be
consumed by various static site hosting solutions, including [Netlify](https://www.netlify.com/),
[GitHub Pages](https://pages.github.com/),
[Google Cloud Storage](https://cloud.google.com/storage/docs/hosting-static-website),
[AWS](https://aws.amazon.com/getting-started/hands-on/host-static-website/), and many others.


## Background

Background is provided for people new to the world of frontend development and/or are curious about Kobweb.
**However, [feel free to skip ahead▼](#exporting-steps)** if you just want to get your hands dirty.

### Compose for Web / SPAs

Compose for Web is well-designed for building a
[single-page application (SPA)](https://en.wikipedia.org/wiki/Single-page_application).

That is, it produces a single, minimal `index.html` file plus some javascript and other resources that can be used to
build your app up at runtime. Once the page is loaded and the script starts running, it continuously modifies the
page's DOM in place to give the user the illusion that they're actually navigating around multiple pages as they click
around on stuff.

Let's use this blog as a concrete example.

For a Compose for Web project, if a user entered a URL like `https://bitspittle.dev/blog/static`, normally
what would happen is the request would be intercepted and the URL path parsed. Based on the result (here, the value
`/blog/static`), your project would dynamically choose to start rendering a new screen starting at the root element,
essentially a giant switch statement.

The above is fine as long as your server understands that this is happening! In other words, if I ping a server with a
request for `/blog/static`, it should just send me the default `index.html` page.

But static server hosts, in contrast, serve static files. Shocker, I know.

So if a user makes a request to `/blog/static`, that means that a file called `blog/static.html` better exist on the
server or that user is getting a 404 error.

### Kobweb to the rescue

Unlike Compose for Web, Kobweb can handle this problem because it sits one level above it. It is aware of all the
pages on your site (since it is the one that generates the routing logic for you), and it comes with a binary that can
run a bunch of useful, project-managing commands.

The important command in this case: `$ kobweb export`

When you ask Kobweb to export your site, it will spin up a local Kobweb server, visit each page in turn, and
snapshot its html. It will then save those files to a target folder.

By default, Kobweb is designed to support rich, full stack web application behavior, including client and server API
connections and dynamic URL routing. And by default, the export process assumes that setup, for example
packaging a library jar that would get consumed by a Kobweb server.

However - a majority of sites don't need all that power. If all you want to do is create a simple blog site,
portfolio, or similar project -- basically, a bunch of local content that doesn't need to continually talk to a
server -- you can tell Kobweb to export to a static layout instead, using the `--layout static` argument.

This will be demonstrated explicitly in the next section.

## Exporting steps

We'll include two options here, for two very popular (and free!) options: [Netlify▼](#netlify) and
[GitHub Pages▼](#github-pages)

***Note:** I am not affiliated with or sponsored by Netlify or GitHub in any way. Their inclusion here is because it's
what users in my [Discord server](https://discord.gg/5NZ2GKV5Cs) mentioned they were familiar with.*

We'll start with steps common to both approaches. 

**Requirements**

* `git`
* A GitHub account / familiarity with GitHub
* The `kobweb` binary ([installation instructions](https://github.com/varabyte/kobweb#install-the-kobweb-binary))

### Common steps

#### Create a project

If you already have a project, you can skip this step.

Otherwise, run the following command, so you'll have something concrete to work with:

```shell
$ kobweb create site
# asks a bunch of questions, but defaults should be fine
$ cd site
```

#### Upload to a new GitHub repository

```shell
$ git init -b main
$ git add . && git commit -m "Initial commit"
```

[Create a new GitHub repo](https://docs.github.com/en/get-started/importing-your-projects-to-github/importing-source-code-to-github/adding-an-existing-project-to-github-using-the-command-line#adding-a-project-to-github-without-github-cli).
When given an opportunity to populate this repo with a `README` and `.gitignore`, **don't**! Since Kobweb already
creates them for you.

```shell
# REMOTE_URL looks something like
# https://github.com/<user>/<repo>.git
$ git remote add origin <REMOTE_URL>
$ git pull origin main
$ git push --set-upstream origin main
```

### Netlify

#### Sign up for a Netlify account

It's free. [Sign up here](https://app.netlify.com/signup)!

#### Integrate with your repo

* Go to your dashboard on your Netlify page
* Click on the "Add new site" button
* Choose "Import an Existing Project"
* Choose "GitHub" as your Git provider
* Follow any authorization steps to tell Netlify about your new Kobweb repo
* Choose your repo from the list

Eventually, you will reach a page that asks you to provide build settings. Leave everything blank except for the
publish directory, which you can set to `.kobweb/site`:

![Netlify Build settings](/images/blog/2022/staticdeploy/netlify-build-settings.png)

#### Allow `.kobweb/site` in gitignore

By default, Kobweb is set up so that you don't check your exported site into source control.

It's common for many projects, especially larger ones with multiple contributors, to allow servers to clone a target git
repository and build the site itself. This avoids permissions issue, i.e. allowing anyone to modify the site, and
eliminates a ton of git noise while you're working.

However, for simplicity with Netlify's workflow, we're just going to ignore those concerns and commit our exported site
directly into our repository.

Open up the `.gitignore` file in your project's root and add the line `!.kobweb/site` to the bottom:

```text
...

# Kobweb ignores
.kobweb/*
!.kobweb/conf.yaml
!.kobweb/template.yaml
!.kobweb/site
```

#### Export your site

```shell
$ kobweb export --layout static
```

This will run for a little while. When finished, run `$ git status` and verify that new files are now ready to be added.

*If not, double-check your `.gitignore` changes from the last step and also make sure that files were actually written to
your `.kobweb/site` folder.*

#### Push your site!

```shell
$ git add . && git commit -m "Exported site"
$ git push
```

#### Finished with Netlify

If everything went well, you should have a page either deployed or well on its way! It only takes a
few seconds once Netlify is aware of the pushed changes.

Go to your Netlify dashboard. You should see an entry like I have here:

![Netlify dashboard](/images/blog/2022/staticdeploy/netlify-dashboard.png)

Click on your site, and you should be taken to a page that has your URL in it.

When finished, you should have a site that looks [like this](https://peaceful-hermann-be6fdf.netlify.app/).

**If so, congratulations! You're done.**

At this point, you can work on your site as much as you want, and whenever you're
ready to update your public facing site, just run `kobweb export --layout static` and push the changes! 

### GitHub Pages

#### Sign up for a Netlify account

It's free. [Sign up here](https://app.netlify.com/signup)!

#### Integrate with your repo

* Go to your dashboard on your Netlify page
* Click on the "Add new site" button
* Choose "Import an Existing Project"
* Choose "GitHub" as your Git provider
* Follow any authorization steps to tell Netlify about your new Kobweb repo
* Choose your repo from the list

Eventually, you will reach a page that asks you to provide build settings. Leave everything blank except for the
publish directory, which you can set to `.kobweb/site`:

![Netlify Build settings](/images/blog/2022/staticdeploy/netlify-build-settings.png)

#### Allow `.kobweb/site` in gitignore

By default, Kobweb is set up so that you don't check your exported site into source control.

It's common for many projects, especially larger ones with multiple contributors, to allow servers to clone a target git
repository and build the site itself. This avoids permissions issue, i.e. allowing anyone to modify the site, and
eliminates a ton of git noise while you're working.

However, for simplicity with Netlify's workflow, we're just going to ignore those concerns and commit our exported site
directly into our repository.

Open up the `.gitignore` file in your project's root and add the line `!.kobweb/site` to the bottom:

```text
...

# Kobweb ignores
.kobweb/*
!.kobweb/conf.yaml
!.kobweb/template.yaml
!.kobweb/site
```

#### Export your site for Netlify

```shell
$ kobweb export --layout static
```

This will run for a little while. When finished, run `$ git status` and verify that new files are now ready to be added.

*If not, double-check your `.gitignore` changes from the last step and also make sure that files were actually written
to your `.kobweb/site` folder.*

All that's left is pushing the changes.

```shell
$ git add . && git commit -m "Exported site"
$ git push
```

#### Finished with Netlify

If everything went well, you should have a page either deployed or well on its way! It only takes a
few seconds once Netlify is aware of pushed changes.

Go to your Netlify dashboard. You should see an entry like I have here:

![Netlify dashboard](/images/blog/2022/staticdeploy/netlify-dashboard.png)

Click on your site, and you should be taken to a page that has your URL in it.

When finished, you should have a site that looks [like this](https://peaceful-hermann-be6fdf.netlify.app/).

**If so, congratulations! You're done.**

At this point, you can work on your site as much as you want, and whenever you're
ready to update your public facing site, just run `kobweb export --layout static` and push the changes!

### GitHub Pages

There are a few ways to configure GitHub Pages, and the different options are a bit out of scope for this post.
Instead, we'll go with the easiest-- using a `docs/` source root within your project.

#### Setup GitHub Pages

* Go to your repo's project on GitHub and click on the `Settings` tab
* In the `Code and automation` section of the sidebar, click `Pages`
* In the `Source` section, set `Branch` to `main` and the folder to `/docs`
* Click `Save`

#### Configure Kobweb's export location

Edit your `.kobweb/conf.yaml` file, changing the `siteRoot` to from `.kobweb/site` to `docs`:

```yaml
server:
  files:
    dev:
      contentRoot: "..."
      script: "..."
      api: "..."
    prod:
      siteRoot: "docs"
```

#### Export your site for GitHub Pages

```shell
$ kobweb export --layout static
```

This will run for a little while. When finished, run `$ git status` and verify that new files are now ready to be added.

*If not, double-check your `.gitignore` changes from the last step and also make sure that files were actually written
to your `docs/` folder.*

All that's left is pushing the changes.

```shell
$ git add . && git commit -m "Exported site"
$ git push
```

#### Finished with GitHub Pages

If everything went well, you should have a page either deployed or well on its way! It only takes about a minute once
GitHub is aware of the pushed changes.

Once it's ready, you can visit your GitHub Pages site, which should have the url `https://<user>.github.io/<project>`.
For example, my site is at https://bitspittle.github.io/kobweb-ghp-demo/.

Do you see a result at your link? **If so, congratulations! You're done.**

At this point, you can work on your project as much as you want, and whenever you're ready to update your public facing
site, just run `kobweb export --layout static` and push the changes!

## Conclusion

As you can see, hosting static sites is cheap / free, and there are a lot of options you can use besides the two listed
here.

Compose for Web is an amazing API, and using static hosting sites is an easy way to get yourself a web presence. Thanks
to Kobweb, you don't have to choose one or the other, but you can enjoy the benefits of both.