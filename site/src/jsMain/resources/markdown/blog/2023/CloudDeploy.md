---
title: Deploying Kobweb into the Cloud
description: How to use Kobweb to build a Compose HTML site that can be served by a Kobweb server living in the Cloud
author: David Herman
date: 2023-05-07
updated: 2026-08-29
tags:
 - compose html
 - kobweb
 - server
---

[Kobweb](https://github.com/varabyte/kobweb) is a framework built on
[Compose HTML](https://github.com/JetBrains/compose-multiplatform#compose-html), a reactive web UI
framework from JetBrains. It allows you to create web apps in Kotlin using a powerful API.

> [!NOTE]
> You can also read more about Kobweb in [this earlier post](../2022/KotlinSite.md).

Kobweb provides a feature called API routes. Essentially, these are functions that get called when you fetch a certain
URL on your site ([discussed in more detail below▼](#server-api-routes)). They can be incredibly useful, but to use
them, you need to have a Kobweb server running somewhere on the internet.

In this post, we'll talk about how to deploy your Kobweb project into the cloud using Render, a popular hosting service
that can host and manage your web services *for free*.

## Background

Before we dive into creating and deploying our site, let's cover some useful background information. However, if you're
already familiar with these concepts, feel free to [skip straight to the action▼](#deploying-a-kobweb-server).

### Do you really need to run your own web server?

Compared to developing a full-stack app, creating a client-only site served by a static-hosting service is often faster
to develop and deploy.

Static sites are always up and running non-stop (aside from occasional server outages), while cloud servers sometimes
need to be instantiated or woken up. On a free hosting tier like that provided by Render, this process can take up to 10
seconds (and possibly much longer).

Additionally, static site hosting is generally more cost-effective than general cloud hosting, as static hosting servers
can optimize for simple file delivery.

Surprisingly, you can create a site with a significant amount of dynamic behavior without ever writing a server. For
example, services like Firebase can manage database, storage, and authentication features for you. In many cases, by
writing client-side code that communicates with their APIs, you can provide an identical experience to another site that
spent more time and money implementing a full-stack solution.

#### So when *should* you write a server?

Despite the above warnings, there are a few reasons you might want to write a server:

* You want to run some custom code that is only relevant for your site. For example, you might want to collect a bunch
  of answers from a user when they first log in and then run some custom, secret algorithm to generate a personalized
  experience. (Think of social media tuning their algorithms based on your preferred topics.)
* You want to write code that talks to private backend services (like a company server that stores private user data)
  without a client-facing API.
* You want to do some work on behalf of the user with an external service that requires a private API key for
  authentication (for example, the ChatGPT API). Exposing these credentials publicly is a major security issue.
* You expect your backend to act as a hub connecting multiple users (for example, a chat server).

At this point, if you're still unsure, a client-only site is likely the better choice. I discuss this approach in more
detail in [this post](../2022/StaticDeploy.md).

You can always start with a static site and migrate to a web server in the cloud later if the situation demands it.

If you're still here and undeterred, let's continue!

### Server API routes

Server API routes are essentially functions that are triggered when a user fetches a URL associated with them. Below,
we'll demonstrate a few concrete examples to help you gain a deeper understanding of this feature.

API routes generally come in two flavors -- read-only queries, and mutations.

For queries, GET operations are common, while for mutations, POST is useful for adding data, PUT for replacing it, and
DELETE for removing it. There are [several other HTTP methods](https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods)
you can explore, but in practice, you can achieve a lot with just GET and POST operations.

Below, we'll explore endpoints that could be used in a simple TODO app.

#### GET

First, let's start with declaring a simple GET query. Here's an API route that generates a unique ID, which the
client can request and then use to uniquely identify themselves as a specific user moving forward:

```kotlin "src/jvmMain/kotlin/api/Id.kt"
package api

@Api
fun generateId(ctx: ApiContext) {
  if (ctx.req.method != HttpMethod.GET) return
  ctx.res.body = bodyOf(UUID.randomUUID().toString())
}
```

The `@Api` annotation informs Kobweb that this function is an API route that should be registered on the backend.

If you tag a function with this annotation, the following two conditions must be met:

1. The function must exist somewhere under the `api` package.
2. The function must have a single parameter of type `ApiContext`.

> [!TIP]
> API methods can be marked `suspend` if desired. In fact, we'll be doing that on a different API route later. But here,
> we don't technically need it, so we choose to leave it off.

A complete discussion of the `ApiContext` class is beyond the scope of this post, but as demonstrated above, it includes
two properties: `req` representing the user's request, and `res` representing the response to send back to them.

You could trigger the above API route by using [curl](https://curl.se/docs/) and targeting
`https://(yoursite.com)/api/id`. (Note that the name of the API route comes from the *filename* – here, "Id.kt" – not
the name of the method!)

```bash
$ curl https://(yoursite.com)/api/id
# Returns e.g. 96763f81-7307-4c15-b8ca-2475ac16e5c3
```

#### POST

Next, let's look at an example of a POST query:

```kotlin "src/jvmMain/kotlin/api/user/Add.kt"
package api

@Api
suspend fun addTodo(ctx: ApiContext) {
  if (ctx.req.method != HttpMethod.POST) return
    
  val ownerId = ctx.req.params["owner"]
  val todo = ctx.req.params["todo"]
  if (userId == null || name == null) {
    return
  }

  ctx.data.getValue<TodoDataStore>().add(ownerId, todo)
  ctx.res.status = 200
}
```

The parameters above (`"owner"` and `"todo"`) will come from URL query parameters. In other words, you could trigger the
above API route with a POST request like this:

```bash
$ curl -X POST https://(yoursite.com)/api/user/add?owner=7ce...379&todo=Add%20my%20first%20TODO%20item
```

> [!TIP]
> There is a `ctx.req.body` property which, if set on the client, would contain the body of the request. That's another
> approach for encoding values passed from the client to the server. However, for simplicity, we're not using it in this
> example.

#### @InitApi

In the POST example above, you might have noticed the line `ctx.data.getValue<TodoDataStore>()` and wondered what it is
and where it came from.

The answer is that Kobweb provides a generic `data` object that you can populate with any collection of objects that
you'd like.

Additionally, the framework includes an `@InitApi` annotation that you can apply to methods which will then be called
whenever the server starts up. Such methods must take a single `InitApiContext` parameter, which, among other values,
provides access to a mutable instance of `data`.

Let's go ahead and implement our own init method that creates a datastore class (in production, this would be backed by
a database, for example). Then, we just need to register an instance of it with the `data` object:

```kotlin 1,12 "src/jvmMain/todo/model/datastore/TodoDataStore.kt"
class TodoDataStore {
    suspend fun add(ownerId: String, todo: String) {
      /* ... */
    }
    suspend fun remove(ownerId: String, id: String) {
      /* ... */
    }
}

@InitApi
fun initDataStore(ctx: InitApiContext) {
    ctx.data.add(TodoDataStore())
}
```

> [!NOTE]
> Some astute readers might recognize `data` as the [Service Locator pattern](https://en.wikipedia.org/wiki/Service_locator_pattern).

With our `TodoDataStore` instance created on startup, we can now access it using `ctx.data.getValue<TodoDataStore>()`
within any of our `@Api` methods.

#### Connecting to API routes from the client

Once you've defined your API routes, you can talke to them from the client using the extension `window.api` property
provided by Kobweb.

For example, for the GET method from earlier, you could access it from the client like so:

```kotlin
// Will fetch the API endpoint at https://(yoursite.com)/api/id
val id = window.api.get("id").bodyAsBytes().decodeToString()
```

> [!NOTE]
> Earlier we mentioned that the route for the "id" endpoint was `https://(yoursite.com)/api/id`, but here we don't need
> to explicitly include the `"api/"` prefix. The `window.api` property handles that for you.

#### Learning more

Ultimately, there's more depth to API routes than what we discussed above, but this glimpse should allow you to start
understanding the power afforded by this feature.

That said, you can read more about API routes [in the official documentation](https://kobweb.varabyte.com/docs/concepts/server/fullstack#define-api-routes).

### A quick introduction to Render

[Render](https://render.com/) is a cloud service offering a variety of useful products and features for hosting web
applications. It's free for small projects, and it gained significant popularity after Heroku started charging for their
previously free tier. We're using Render in this post due to its free offering.

Render provides several different services, including static site hosting. However, for the remainder of this article,
we'll focus on Render's "Web Service" product.

If you're interested, you can learn more about Web Services in [Render's documentation](https://render.com/docs/web-services).

### A brief overview of GitHub workflows

We discussed GitHub workflows in a [previous blog post](../2022/static-deploy#github-actions-workflow), so for now,
we'll just repeat this first part:

> GitHub Actions is GitHub's approach to automating work, which is commonly used for continuous integration. A workflow
> is a script which defines one or more related jobs that run together in response to some event.

We'll use a workflow below to handle exporting our site and, when done, will send out a message that pings our web
hosting service Render when those files are ready to download.

### A minimal discussion of Docker containers

Docker containers are way too nuanced and complex a topic to cover in-depth here. Instead, we'll cover the bare minimum
needed for you to understand a later step in this post.

1. A Docker container is a lightweight, executable package of software that contains everything needed to run an
   application in a portable way.
2. When building a new Docker container, you start with a base image, which is a pre-built Docker container that you
   can use as a starting point. This is often very lean, such as a barebones Linux distribution.
3. You can build a Docker image up in layers, where later layers can selectively copy parts from previous layers. In
   this manner, you can download a bunch of tools in an initial layer, which do work to generate a bunch of outputs,
   then selectively copy only the outputs you need into the final image. After that point, you can share the final image
   alone, discarding any previous layers, which saves space.
4. A `Dockerfile` is a text file that contains instructions for how to build a Docker image. It is common for projects
   to include a `Dockerfile` in the root directory of the project so that some service can find it after syncing your
   project and then build the image automatically.

You may wish to read the [official documentation](https://www.docker.com/resources/what-container/) if you'd like to
understand the feature in more depth.

### CORS

If you're already familiar with CORS, then we empathize with the indigestion its memory is undoubtedly causing you right
now. ❤️‍🔥

CORS, or *Cross-Origin Resource Sharing*, is a security feature built on the idea that a web page should not be able to
make requests for resources from a server that is not the same as the one that served the page.

The underlying security mechanism that enforces this restriction is called the *Same-Origin Policy* (SOP). SOP prevents
malicious sites from requesting sensitive data from other sites. For example, if you visit a malicious site, it should
not be able to make a request to your bank's website and then read the response to see your account balance.

SOP prevents cross-domain server requests by default. CORS offers a way to relax this policy in a controlled manner by
allowing trusted exceptions.

It's important to note that not all operations are blocked by SOP. As a result, you might create a site that functions
well without configuring CORS, only to encounter issues when you introduce a new feature later that requires it.

This brief introduction should give you a basic understanding of CORS and its importance. For a deeper dive, consider
exploring Mozilla's documentation on [CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)
and [SOP](https://developer.mozilla.org/en-US/docs/Web/Security/Same-origin_policy).

## Deploying a Kobweb server

Now that we've covered the necessary background information, it's time to deploy a Kobweb server to the cloud! We'll
follow these steps:

* Create a Kobweb project
* Set up a GitHub repository for our code
* Create an account on Render
* Configure Render and GitHub with secrets from each other
* Create a GitHub workflow that handles exporting our site and then pings Render
* Create a Dockerfile that will direct Render to download server artifacts from GitHub and deploy them

Admittedly, this is quite a bit of initial legwork, but once everything is in place, you'll have a project where:

* you submit a change to the `main` branch
* this triggers a GitHub runner that will export your Kobweb site and ping Render
* Render will fetch the artifacts from GitHub and deploy your site!

And the whole process should take about 5 minutes before your new site is up and running.

### Create a Kobweb project

If you already have a project, feel free to skip this step. However, if you don't and want a concrete example to use
while following along, we suggest getting the demo _todo_ app for this guide.

In a terminal, navigate to a folder on your computer where you store projects and execute the following commands:

```bash "e.g. in ~/projects"
$ kobweb create examples/todo
# Kobweb asks a few questions, but defaults should be fine
$ cd todo
```

These steps should initialize your project with git. If you originally opted not to, you can manually initialize it:

```bash
$ git init -b main
$ git add . && git commit -m "Initial commit"
```

This next step is optional, but to get a feel for the app before you deploy it, run it locally!

```bash "e.g. in ~/projects/todo"
$ cd site
$ kobweb run
```

### Create a new GitHub repository

[Follow the official instructions to create a new GitHub repository](https://docs.github.com/en/get-started/importing-your-projects-to-github/importing-source-code-to-github/adding-an-existing-project-to-github-using-the-command-line#adding-a-project-to-github-without-github-cli).
Choose a name that suits your project. For this guide, I used `kobweb-todo-on-render`, but feel free to select something
more concise and appropriate for the specific project you're working on.

When given an opportunity to populate this repo with a `README` and `.gitignore`, **don't**! Kobweb has already created
these for you.

After completing the process, sync your local project with the GitHub repo:

```bash
# REMOTE_URL looks something like
# https://github.com/<user>/<repo>.git
$ git remote add origin <REMOTE_URL>
$ git push -u origin main
```

### Create a Render account

There are several ways to create a Render account, but for simplicity and compatibility with later steps, we'll use
their GitHub sign-in flow.

> [!NOTE]
> If you already have a Render account connected to GitHub, skip this section. If you have an account not connected to
> GitHub, follow [these official instructions](https://render.com/docs/github) instead.

Start by visiting [Render's sign up page](https://dashboard.render.com/register) and clicking the
<b><span style="white-space:nowrap">${FaGithub} GitHub</span></b> button:

![Render Sign Up](/images/blog/2023/cloud-deploy/render-sign-up.png)

You'll be redirected to a GitHub page, where you'll be prompted to authorize Render with your GitHub account. Render is
a trusted company, so this is a safe action. Click **Authorize Render** to proceed!

![GitHub Authorize Render](/images/blog/2023/cloud-deploy/github-authorize-render.png)

Confirm your email and click the **Complete Sign Up** button.

![Render Verify Email](/images/blog/2023/cloud-deploy/render-email-confirmation.png)

Check your inbox for an email from Render with a link to confirm your email address. Click it to be redirected to the
Render dashboard.

### Connect Render to GitHub

At this point, go to Render and open your dashboard.

From the options available, create a new **Web Service**. This will prompt you to find your relevant GitHub repo and
**Connect** it.

![Render Connect GitHub](/images/blog/2023/cloud-deploy/render-new-web-service-connect-github.png)

Afterward, you'll be directed to a web service configuration page. You should only need to specify the service name,
as all other defaults should work fine. I used `"kobweb-todo"` in my case, but you will have to specify a name that's
not already taken.

When ready, press **Create Web Service**.

![Render Config Web Service](/images/blog/2023/cloud-deploy/render-new-web-service-config.png)

### Sharing secrets

We will need to generate two secrets, one from GitHub and the other from Render, which will let them talk to each other.

#### Secret #1: GitHub Token for read-only Actions permissions

Later in this article, we'll create a workflow that will tell your GitHub runner how to export your site and upload
those files as artifacts.

To download those artifacts from Render, you will need to use a private token to authenticate the request. We will
generate that now.

Although we will ultimately be creating a token that is only for use with our current project, the flow for creating it
starts from your top-level _user_ settings.

So, to begin, go to your user icon in the top right, click on it, and then select
<b><span style="white-space:nowrap">${FaGear} Settings</span></b>:

![GitHub User Settings](/images/blog/2023/cloud-deploy/github-user-settings.png)

In the left-hand menu, look for **Developer Settings** at the very bottom and click on it:

![GitHub Developer Settings](/images/blog/2023/cloud-deploy/github-settings-menu-dev-settings.png)

This will take you to a new page where you should click on
<b><span style="white-space:nowrap">${FaKey} Personal access tokens > Fine-grained tokens</span></b>.

Then click on the **Generate new token** button:

![GitHub Personal Access Token List](/images/blog/2023/cloud-deploy/github-pat-list.png)

Give the token a name (anything unique) and, optionally, a description:

![GitHub Personal Access Token creation - part 1](/images/blog/2023/cloud-deploy/github-pat1a.png)

For this case, I recommend setting the **Expiration** value to **No expiration**. However, GitHub highly discourages
this, as it is a potential security risk if your secret leaks later.

That said, in this case, our key will have restricted, minimal permissions, so personally I'm not too worried about it
even if mine got stolen.

However, this is not a best practice, so you may choose to give your token a lifetime (at which point you'd need to
create a new one later and update Render when you do).

I also make sure the key _only_ applies to my one repository by choosing **Only select repositories** and finding my
specific repository in the **Select repositories** pull-down list.

![GitHub Personal Access Token creation - part 2](/images/blog/2023/cloud-deploy/github-pat1b.png)

Finally, press
<b><span style="white-space:nowrap">${FaPlus} Add permissions</span></b> and chose **Actions (read-only)**. The
**Metadata** permission gets added automatically by GitHub.

When all information is ready, press **Generate token**:

![GitHub Personal Access Token creation - part 3](/images/blog/2023/cloud-deploy/github-pat1c.png)

In the UI popup that appears, copy the value using the ${FaCopy} button! We'll bring it over to Render momentarily.

> [!CAUTION]
> If you fail to save this value in your clipboard and close the popup, you'll need to go back through the token flow
> one more time to create a new one.

![GitHub Personal Access Token creation - the generated secret](/images/blog/2023/cloud-deploy/github-pat2.png)

Go back to Render, and visit your new service's project page. On the left side, you should see an **Environment** menu
item. Click on it and look for the **Secret Files** section. 

Our goal here is to create a file that our Dockerfile will later be able to read. This is a very secure way to handle
secrets in Render.

Finally, press the <b><span style="white-space:nowrap">${FaPlus} Add file</span></b> to continue.

![Render Secret Files](/images/blog/2023/cloud-deploy/render-project-env-secret-files1.png)

We need to give our file a name. We used `"GH_TOKEN"` but you can be more descriptive if you'd like (such as
`"GH_ARTIFACT_DOWNLOAD_TOKEN"`).

Finally, click on the ${FaEye} button, which will open up a UI popup for entering in our secret.

![Render Secret Files - adding the filename](/images/blog/2023/cloud-deploy/render-project-env-secret-files2.png)

Enter the secret we just copied over from our GitHub token flow!

![Render Secret Files - adding the file value](/images/blog/2023/cloud-deploy/render-project-env-secret-files3.png)

That's it for now! Later, you'll see how we will read this value from our Dockerfile.

#### Secret #2: Render deploy hook URL

Render supplies a secret URL for your project which, when pinged with a POST request, will kick off a deploy. We will
use this to allow GitHub to notify Render after the artifacts have been updated.

In your project's settings section, if you scroll down a little bit, you will find a **Deploy** area.

Make sure **Auto-Deploy** is set to **Off** (since we'll be kicking off deployments via a trigger instead). And then,
press the ${FaCopy} button on the **Deploy Hook** line.

![Render Deploy settings](/images/blog/2023/cloud-deploy/render-settings-deploy.png)

Let's go back to GitHub. This time, find your _repository's_ settings (to the right of the top bar on your project
page):

![GitHub project settings](/images/blog/2023/cloud-deploy/github-project-settings.png)

In the menu on the left hand side, look for
<b><span style="white-space:nowrap">${FaAsterisk} Secrets and variables > Actions</span></b>.
Once there, click on **New repository secret** (found under the **Secrets** tab):

![GitHub Repository secrets](/images/blog/2023/cloud-deploy/github-repository-secret.png)

In the UI that pops up, give the secret a name (whatever you want, but we went with `RENDER_DEPLOY_HOOK_URL`), and in
the **Secret** text area, paste the value we got from Render:

![GitHub Repository secrets - deploy hook URL](/images/blog/2023/cloud-deploy/github-render-deploy-hook-url.png)

Hit **Add secret** and you're done!

### Add a workflow

Copy the following workflow as-is into your project at `.github/workflows/export-and-deploy-site.yml`:

```yaml ".github/workflows/export-and-deploy-site.yml"
name: Export and deploy site

on:
  workflow_dispatch:
  push:
    branches:
      - main

jobs:
  export_and_upload:
    runs-on: ubuntu-latest
    defaults:
      run:
        shell: bash

    steps:
      # Will fetch latest CLI version and store it in KOBWEB_CLI_VERSION env var
      - name: Fetch latest Kobweb CLI version
        run: |
          VERSION=$(curl -sSL https://raw.githubusercontent.com/varabyte/data/refs/heads/main/kobweb/cli-version.txt | xargs)
          echo "KOBWEB_CLI_VERSION=$VERSION" >> $GITHUB_ENV

      - uses: actions/checkout@v7
      - uses: actions/setup-java@v6
        with:
          distribution: temurin
          java-version: 17

      # When projects are created on Windows, the executable bit is sometimes lost. So set it back just in case.
      - name: Ensure Gradle is executable
        run: chmod +x gradlew

      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v6

      - name: Query Browser Cache ID
        id: browser-cache-id
        run: echo "value=$(./gradlew -q :site:kobwebBrowserCacheId)" >> $GITHUB_OUTPUT

      - name: Cache Browser Dependencies
        uses: actions/cache@v6
        id: playwright-cache
        with:
          path: ~/.cache/ms-playwright
          key: ${{ runner.os }}-playwright-${{ steps.browser-cache-id.outputs.value }}

      - name: Fetch kobweb
        uses: robinraju/release-downloader@v1.9
        with:
          repository: "varabyte/kobweb-cli"
          tag: "v${{ env.KOBWEB_CLI_VERSION }}"
          fileName: "kobweb-${{ env.KOBWEB_CLI_VERSION }}.zip"
          tarBall: false
          zipBall: false

      - name: Unzip kobweb
        run: unzip kobweb-${{ env.KOBWEB_CLI_VERSION }}.zip

      - name: Run export (fullstack)
        run: |
          cd site
          ../kobweb-${{ env.KOBWEB_CLI_VERSION }}/bin/kobweb export --notty --layout fullstack

      - name: Upload site
        uses: actions/upload-artifact@v7
        with:
          name: kobweb-folder
          path: site/.kobweb
          include-hidden-files: true
          if-no-files-found: error
          retention-days: 1

      - name: Trigger Render Deployment
        run: curl -X POST ${{ secrets.RENDER_DEPLOY_HOOK_URL }}
```

Essentially, it directs GitHub to fetch, build, and export your site in fullstack mode. This is all kicked off
automatically whenever any commit is checked into the `main` branch. We also handle the `workflow_dispatch` event, which
means a user can manually trigger this workflow to run from any branch.

Once the export is finished, the workflow grabs all the contents of the `.kobweb` folder (which has everything you need
to run your server) and uploads it as a zipped artifact using the `upload-artifact` action.

After that is done, we ping Render using `secrets.RENDER_DEPLOY_HOOK_URL`. If you used a different name above when
creating your repository secret earlier, then you must change it here as well!

### Add a Dockerfile

Create a file called `Dockerfile` in the root of your project and populate it with the following contents:

> [!IMPORTANT]
> You must update the `REPO_OWNER` and `REPO_NAME` arguments to valid values or your deployment will fail! Furthermore,
> if you named your **Secret File** something besides `GH_TOKEN`, be sure to update the name below as well.

```dockerfile "Dockerfile"
# Variables declared before stages can be re-used; they will need to be
# redeclared explicitly, but the value only needs to be specified once.
ARG KOBWEB_APP_ROOT="site"

#-----------------------------------------------------------------------
# Stage 1: Download the files needed to drive our site (from GitHub, where they were built)
FROM alpine:latest AS download

# Minimum deps needed to fetch and process responses / files
RUN apk add --no-cache curl jq unzip

# ⚠️ YOU MUST CHANGE THE FOLLOWING TO YOUR OWN VALUES!!! ⚠️
# You can extract them from the URL of your GitHub project,
# e.g. `https://github.com/(REPO_OWNER)/(REPO_NAME)
ARG REPO_OWNER="***"
ARG REPO_NAME="***"

# The following is the name used in the GitHub workflow
ARG ARTIFACT_NAME="kobweb-folder"
ARG KOBWEB_APP_ROOT

# Render automatically injects this value during Docker builds. We will use it to ensure we download the right artifact.
ARG RENDER_GIT_COMMIT

# We will search for the artifact associated with our specific git commit. If for some reason the API says it can't find
# it, we'll try a few more times with exponential backoff, as maybe things are still propagating through GitHub's
# system. In practice, we expect to find the artifact on the first search.
RUN --mount=type=secret,id=GH_TOKEN,target=/etc/secrets/GH_TOKEN \
    set -e; \
    echo "==> [1/4] Starting artifact download process..."; \
    echo "    Target Commit: ${RENDER_GIT_COMMIT}"; \
    if [ -f /etc/secrets/GH_TOKEN ]; then \
      GH_TOKEN=$(cat /etc/secrets/GH_TOKEN); \
    fi; \
    if [ -z "$GH_TOKEN" ]; then \
      echo "==> Missing GH_TOKEN secret file." && exit 1; \
    fi; \
    \
    ARTIFACTS_URL="https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/actions/artifacts?name=${ARTIFACT_NAME}"; \
    ARTIFACT_ID=""; \
    MAX_ATTEMPTS=5; \
    DELAY=2; \
    \
    echo "==> [2/4] Searching GitHub API for matching artifact (with retry/backoff)..."; \
    for attempt in $(seq 1 $MAX_ATTEMPTS); do \
      echo "    Attempt ${attempt}/${MAX_ATTEMPTS}..."; \
      RESPONSE=$(curl -sS -f -H "Authorization: Bearer $GH_TOKEN" "$ARTIFACTS_URL" || true); \
      \
      if [ -n "$RESPONSE" ]; then \
        ARTIFACT_ID=$(echo "$RESPONSE" | jq -r --arg SHA "$RENDER_GIT_COMMIT" \
          '(.artifacts // []) | map(select(.workflow_run.head_sha == $SHA)) | sort_by(.created_at) | last | .id // empty'); \
      fi; \
      \
      if [ -n "$ARTIFACT_ID" ]; then \
        echo "==> Match found! Artifact ID: ${ARTIFACT_ID}"; \
        break; \
      fi; \
      \
      if [ $attempt -lt $MAX_ATTEMPTS ]; then \
        echo "    Artifact for commit ${RENDER_GIT_COMMIT} not found yet. Retrying in ${DELAY}s..."; \
        sleep $DELAY; \
        DELAY=$((DELAY * 2)); \
      fi; \
    done; \
    \
    if [ -z "$ARTIFACT_ID" ]; then \
      echo "==> Failed to find artifact '${ARTIFACT_NAME}' for commit ${RENDER_GIT_COMMIT} after ${MAX_ATTEMPTS} attempts." && exit 1; \
    fi; \
    \
    ARTIFACT_URL="https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/actions/artifacts/${ARTIFACT_ID}/zip"; \
    echo "==> [3/4] Downloading zip file from ${ARTIFACT_URL} ..."; \
    curl -sS -f -L \
      -H "Authorization: Bearer $GH_TOKEN" \
      -H "Accept: application/vnd.github+json" \
      -o ${ARTIFACT_NAME}.zip \
      "$ARTIFACT_URL"; \
    \
    echo "==> [4/4] Extracting artifact to target directory..."; \
    mkdir -p /project/${KOBWEB_APP_ROOT}/.kobweb; \
    unzip -q ${ARTIFACT_NAME}.zip -d /project/${KOBWEB_APP_ROOT}/.kobweb; \
    echo "==> Download step completed successfully!"

#-----------------------------------------------------------------------
# Stage 2: Copy over the minimum amout of stuff needed to run the Kobweb server.
# We use the latest JRE image available to us at this time.
FROM eclipse-temurin:25-jre-alpine AS run

ARG KOBWEB_APP_ROOT

WORKDIR /project/${KOBWEB_APP_ROOT}

COPY --from=download /project/${KOBWEB_APP_ROOT}/.kobweb .kobweb

# Because many free tiers only give you 512M of RAM, let's limit the server's
# memory usage to that. You can remove this ENV line if your server isn't so
# restricted. That said, 512M should be plenty for most sites.
ENV JAVA_TOOL_OPTIONS="-Xmx512m"

ENTRYPOINT ["/bin/sh", ".kobweb/server/start.sh"]
```

Render will be able to find this file and execute it when a deployment is requested.

The above script looks for an artifact associated with the most recent git commit and downloads it. There is some extra
complexity to support searching multiple times with exponential backoff in case the artifact is not found yet.

If you review your Render logs, you should see information that looks like the following (with real values instead of
asterisks):

``` "Render logs"
#11 0.063 ==> [1/4] Starting artifact download process...
#11 0.063     Target Commit: ****************************************
#11 0.064 ==> [2/4] Searching GitHub API for matching artifact (with retry/backoff)...
#11 0.064     Attempt 1/5...
#11 0.344 ==> Match found! Artifact ID: **********
#11 0.344 ==> [3/4] Downloading zip file from https://api.github.com/repos/*******/*************/actions/artifacts/**********/zip ...
#11 3.699 ==> [4/4] Extracting artifact to target directory...
#11 3.932 ==> Download step completed successfully!
#11 DONE 4.0s
```

> [!TIP]
> Kobweb works with Java 11, but the general recommendation is to use newer releases as your runtime if you can, as they
> might contain security fixes and performance improvements.
>
> The `eclipse-temurin` image, according to its docs, was designed to be both used for running apps and also be
> useful as a general base foundation, which is perfect for our needs. The `alpine` variant is supposed to be extra
> slim.
>
> There are other images out there, and you are welcome to investigate further.

### Configure CORS

Return to your Kobweb project.

We need to configure our Kobweb server with the domain it will be running on.

Earlier, when you created the web service with Render, you had to choose a unique name.

Free domain names provided by Render web service hosting have the format `$(servicename).onrender.com`. For this guide,
the name I chose reserved `kobweb-todo.onrender.com`.

Open and edit `.kobweb/conf.yaml`, then add a CORS entry to it, replacing the host name below with what _your_ site will
be:

```yaml ".kobweb/conf.yaml"
site:
  title: "Todo"

server:
  files:
     # ...

  # ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
  cors:
    hosts:
      - name: "kobweb-todo.onrender.com"
        schemes:
          - "https"
  # ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

  # ...
```

> [!NOTE]
> Specifying the schemes is optional. If you don't specify them, Kobweb defaults to "http" and "https".

To test that you did this correctly, run your app (`cd site && kobweb run`) and open up the log file at
`.kobweb/server/logs/kobweb-server.log`. Look for the line near the top that should say your host is registered:

``` 3 ".kobweb/server/logs/kobweb-server.log"
INFO  kobweb.system - Initializing server engine for Kobweb project "Todo"
INFO  kobweb.system - No API jar file specified in conf.yaml. Server API routes will not be available.
INFO  kobweb.system - CORS: Registered host(s): kobweb-todo.onrender.com
INFO  io.ktor.server.Application - Application started in 0.167 seconds.
INFO  io.ktor.server.Application - Responding at http://0.0.0.0:8080
```

If the conf file was set up incorrectly (perhaps the indentation is off), you'll instead see:

``` 3 ".kobweb/server/logs/kobweb-server.log"
INFO  kobweb.system - Initializing server engine for Kobweb project "Todo"
INFO  kobweb.system - No API jar file specified in conf.yaml. Server API routes will not be available.
INFO  kobweb.system - CORS: No hosts registered.
INFO  io.ktor.server.Application - Application started in 0.167 seconds.
INFO  io.ktor.server.Application - Responding at http://0.0.0.0:8080
```

### Deploy your site

We've reached the final stretch.

Add and push the CORS, Dockerfile, and workflow changes to your repo:

```bash
$ git status
On branch main
  modified: .github/workflows/export-and-deploy.yaml
  modified: Dockerfile
  modified: site/.kobweb/conf.yaml
$ git add . && git commit -m "Configuration for deploying to a web service on Render"
$ git push
```

If you want, you can go to your GitHub project, open up the actions window, and watch the export happen live. When it
finishes, it will fire a ping to Render.

Then, wait while Render follows the instructions in your Dockerfile. This process should go fairly quickly.

![Render Deploy Screen](/images/blog/2023/cloud-deploy/render-web-service-deploy.png)

Once it's done, you should see the status switch from a grey "In progress" message to a green "Live" indicator:

![Render Live Indicator](/images/blog/2023/cloud-deploy/render-in-progress-to-live.png)

Click on your web service's link to see your site in action!

![Kobweb Site Deployed](/images/blog/2023/cloud-deploy/kobweb-app-deployed.png)

> [!NOTE]
> Your site might feel slow, especially during startup. That's the trade-off with a free service!

At this point, any time you push a new commit to your repo, GitHub and Render will coordinate automatically to rebuild
and redeploy your site.

> [!WARNING]
> The TODO demo is not production ready!

Keep in mind that the TODO example is designed as a demo and is not intended for production use. In its current design:

* **There is no authentication.** The app generates a unique ID for you, saved locally by your browser. However, this
  ID won't carry over to other browsers, or to the same browser on other machines.
* **There is no abuse protection.** There are no checks to limit the number of TODO items or even user accounts that a
  user can create.
* **There is no error handling.** If something goes wrong when adding or removing a TODO item, the app will
  spin indefinitely.
* **There is no database.** The app stores all TODO items in memory, which means that if the server crashes, or is spun
  down, or a new instance is spun up and you get connected with that one, all previous TODO items will
  be lost or inaccessible.
* **There is no pagination.** When a user visits the site, all their TODO items are fetched. The API should be
  updated to only fetch a subset of items at a time.

You should only consider the TODO demo as a starting point for your projects. Creating a production-ready full-stack app
requires considerable effort, and the concerns mentioned above are additional reasons you might prefer to create a
client-only static site instead of a full stack product.

## Conclusion

Congratulations! Your Kobweb server should now be online!

> [!TIP]
> If you're having trouble, you can compare your own project [with mine](https://github.com/bitspittle/kobweb-todo-on-render).

This post covered the essentials for getting a Kobweb server running in the cloud.

For a complete production server experience, there's more to consider, including:

* Selecting data center(s) for deployment (to minimize latency for your users)
* Implementing user authentication, login, and logout flows
* Designing a scalable backend as your site's traffic increases
* Utilizing a decentralized database for data storage (to maintain state across server instances and crashes)
* Storing secrets (such as API keys) securely

Web service hosts (like Render, AWS, GCP, Azure, etc.) are designed to handle scaling for you! But you'll need to
consult their documentation for setup guidance.

You are welcome to explore different options besides Render! However, it is left as an exercise to the reader on how to
replace:

* the GitHub workflow line that pings the Render via its deploy hook URL
* the Dockerfile's use of RENDER_GIT_COMMIT, which won't be set by other services
* the Dockerfile's way to access secrets, when you are not using Render's secret file system

There's nothing like seeing your site live on the web. Thanks to companies like Render that offer a free tier for
hobbyists, it's easier than ever to get started developing rich, powerful web applications.

Happy coding!

## Thanks!

A huge thanks to Stevdza-San ([homepage](https://stevdza-san.com/), [YouTube channel](https://www.youtube.com/c/StevdzaSan))
for his collaboration while experimenting with the work that became this post. He introduced me to Render, and his
patience and feedback while we tested multiple iterations of attempts to get Kobweb running on Render was invaluable.
