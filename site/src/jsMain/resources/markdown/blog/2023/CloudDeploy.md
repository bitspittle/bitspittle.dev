---
root: .components.layouts.BlogLayout
title: Deploying Kobweb into the Cloud
description: How to use Kobweb to build a Compose HTML site that can be served by a Kobweb server living in the Cloud
author: David Herman
date: 2023-05-07
updated: 2024-11-20
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

You can push out new iterations of your site in about 1-2 minutes (mainly limited by the time it takes to export your
site), as opposed to the 5-10 minutes (or more!) needed to deploy a server in the cloud.

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
  of answers from a user on first login and then run some custom algorithm to generate a personalized experience.
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

#### GET

First, let's start with declaring a simple GET query. Here's an API route that generates a unique ID, which the
client can request and use as desired:

```kotlin
// src/jvmMain/kotlin/api/id.kt
package api

@Api
fun generateId(ctx: ApiContext) {
  if (ctx.req.method != HttpMethod.GET) return
  ctx.res.setBodyText(UUID.randomUUID().toString())
}
```

The `@Api` annotation informs Kobweb that this function is an API route that should be registered. If you tag a function
with this annotation, the following two conditions must be met:

1. The function must exist somewhere under the `api` package.
2. The function must have a single parameter of type `ApiContext`.

> [!TIP]
> API methods can be marked `suspend` if desired.

A complete discussion of the `ApiContext` class is beyond the scope of this post, but as demonstrated above, it includes
two properties: `req` representing the user's request, and `res` representing the response sent back to them.

If your API route receives a request and you don't explicitly set the response's status code and/or body text (e.g.
by returning early), then Kobweb will send an error response to the client.

You could trigger the above API route by using [curl](https://curl.se/docs/) and targeting
`https://(yoursite.com)/api/id`. (Note that the name of the API endpoint comes from the *filename*, not the name of the
method!)

```bash
$ curl https://(yoursite.com)/api/id
# Returns e.g. 96763f81-7307-4c15-b8ca-2475ac16e5c3
```

#### POST

Next, let's look at an example of a POST query:

```kotlin
// src/jvmMain/kotlin/api/user/add.kt
package api

@Api
fun addUser(ctx: ApiContext) {
  if (ctx.req.method != HttpMethod.POST) return

  val userId = ctx.req.params["id"]
  val name = ctx.req.params["name"]
  if (userId == null || name == null) {
    return
  }

  ctx.data.getValue<Database>().addUser(User(userId, name))
  ctx.res.status = 200
}
```

The above method is fairly self-explanatory. The parameters here (`"id"` and `"name"`) come from URL query parameters.

In other words, you could trigger the above API route by using curl with a POST request:

```bash
$ curl -X POST https://(yoursite.com)/api/user/add?id=123&name=Kodee
```

> [!TIP]
> There is a `ctx.req.body` property which, if set, would contain the body of the request. That's another approach for
> encoding values passed from the client to the server. However, for simplicity, we're not using it in this example.

#### @InitApi

In the POST example above, you might have noticed the line `ctx.data.getValue<Database>()` and wondered what it is and
where it came from.

The answer is that Kobweb gives you a way to populate the `data` property with any object you'd like. The framework
includes an `@InitApi` annotation that you can apply to methods which will then be called whenever the server starts up.
These startup methods are where you can initialize `data`.

Let's go ahead and implement our own init method that connects to a database, using a `Database` class of our own
creation:

```kotlin
// src/jvmMain/kotlin/db/Database.kt
class Database {
    init {
        /* sets up a connection to some external database service */
    }

    fun addUser(user: User) { /* ... */ }
}

@InitApi
fun initDatabase(ctx: InitApiContext) {
    ctx.data.add(Database())
}
```

The `ctx.data` property holds a collection of class instances to which you can add any object and later retrieve it by
its type.

> [!NOTE]
> Some astute readers might recognize this as the [Service Locator pattern](https://en.wikipedia.org/wiki/Service_locator_pattern).

With our `Database` instance created on startup, we can now access it using `ctx.data.getValue<Database>()` within any
of our `@Api` methods.

#### Fetching API routes from the client

Once you've defined your API routes, you can trigger them from the client using the extension `window.api` property
provided by Kobweb.

For example, for the GET method from earlier, you could query it from the client like this:

```kotlin
// Will fetch the API endpoint at https://(yoursite.com)/api/id
window.api.tryGet("id")?.then { idBytes ->
    console.log("Got id: ${String(id)}")
}
```

There's more depth to API routes than what we discussed above, but this glimpse should allow you to start understanding
the power afforded by this feature.

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

### A quick introduction to Render

[Render](https://render.com/) is a cloud service offering a variety of useful products and features for hosting web
applications. It's free for small projects, and it gained significant popularity after Heroku started charging for their
previously free tier. We're using Render in this post due to its free offering.

Render provides several different services, including static site hosting. However, for the remainder of this article,
we'll focus on Render's "Web Service" product.

If you're interested, you can learn more about Web Services in [Render's documentation](https://render.com/docs/web-services).

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
* Configure the project for deployment on Render
* Deploy the server

### Create a Kobweb project

If you already have a project, feel free to skip this step. However, if you're just exploring and want a tangible
example, we'll use the demo TODO app from the Kobweb templates for this guide.

In a terminal, navigate to a folder on your computer where you store projects and execute the following commands:

```bash
# e.g. in ~/projects
$ kobweb create examples/todo
# Kobweb asks a few questions, but defaults should be fine
$ cd todo
```

These steps should initialize your project with git. If you opted not to, or if it didn't work as expected, you can
manually initialize it:

```bash
$ git init -b main
$ git add . && git commit -m "Initial commit"
```

This next step is optional, but to get a feel for the TODO app before you deploy it, run it locally:

```bash
# Inside the todo directory
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

There are several ways to create a Render account, but for simplicity and compatibility with later steps, we'll use their GitHub sign-in flow.

> [!NOTE]
> If you already have a Render account connected to GitHub, skip this section. If you have an account that is not
> connected to GitHub, follow [these official instructions](https://render.com/docs/github) instead.

Start by visiting [Render's sign up page](https://dashboard.render.com/register) and click the "GitHub" button.

![Render Sign Up](/images/blog/2023/cloud-deploy/render-sign-up.png)

You'll be redirected to a GitHub page, where you'll be prompted to authorize Render with your GitHub account. Render is a trusted company, so this is a safe action. Click "Authorize Render" to proceed!

![GitHub Authorize Render](/images/blog/2023/cloud-deploy/github-authorize-render.png)

Confirm your email and click the "Complete Sign Up" button.

![Render Verify Email](/images/blog/2023/cloud-deploy/render-email-confirmation.png)

Check your inbox for an email from Render with a link to confirm your email address. Click it to be redirected to the
Render dashboard.

That's it for now. We'll return to Render in a later step.

### Configure CORS

Return to your Kobweb project.

We need to configure our Kobweb server with the domain it will be running on.

We face a chicken-and-egg problem here, because we don't know the domain yet, as you'll have to reserve something on
Render in a later step. It may get rejected if the name is already taken.

Still, we'll do our best to configure it now. If you can't reserve the domain you wanted later, just revisit this step
at that time and update the configuration.

Free domain names provided by Render web service hosting have the format `$(servicename).onrender.com`. For this guide,
I'm planning to reserve `kobweb-todo.onrender.com`.

Open and edit `.kobweb/conf.yaml`, then add a CORS entry to it:

```yaml
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

### Add a Dockerfile

Create a file called `Dockerfile` in the root of your project and populate it with the following contents:

```dockerfile
#-----------------------------------------------------------------------------
# Variables are shared across multiple stages (they need to be explicitly
# opted into each stage by being declaring there too, but their values need
# only be specified once).
ARG KOBWEB_APP_ROOT="site"
# ^ NOTE: Kobweb apps generally live in a root "site" folder in your project,
# but you can change this in case your project has a custom layout.

FROM eclipse-temurin:21 as java

#-----------------------------------------------------------------------------
# Create an intermediate stage which builds and exports our site. In the
# final stage, we'll only extract what we need from this stage, saving a lot
# of space.
FROM java as export

ENV KOBWEB_CLI_VERSION=0.9.18
ARG KOBWEB_APP_ROOT

ENV NODE_MAJOR=20

# Copy the project code to an arbitrary subdir so we can install stuff in the
# Docker container root without worrying about clobbering project files.
COPY . /project

# Update and install required OS packages to continue
# Note: Node install instructions from: https://github.com/nodesource/distributions#installation-instructions
# Note: Playwright is a system for running browsers, and here we use it to
# install Chromium.
RUN apt-get update \
    && apt-get install -y ca-certificates curl gnupg unzip wget \
    && mkdir -p /etc/apt/keyrings \
    && curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg \
    && echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_$NODE_MAJOR.x nodistro main" | tee /etc/apt/sources.list.d/nodesource.list \
    && apt-get update \
    && apt-get install -y nodejs \
    && npm init -y \
    && npx playwright install --with-deps chromium

# Fetch the latest version of the Kobweb CLI
RUN wget https://github.com/varabyte/kobweb-cli/releases/download/v${KOBWEB_CLI_VERSION}/kobweb-${KOBWEB_CLI_VERSION}.zip \
    && unzip kobweb-${KOBWEB_CLI_VERSION}.zip \
    && rm kobweb-${KOBWEB_CLI_VERSION}.zip

ENV PATH="/kobweb-${KOBWEB_CLI_VERSION}/bin:${PATH}"

WORKDIR /project/${KOBWEB_APP_ROOT}

# Decrease Gradle memory usage to avoid OOM situations in tight environments
# (many free Cloud tiers only give you 512M of RAM). The following amount
# should be enough to build and export our site.
RUN mkdir ~/.gradle && \
    echo "org.gradle.jvmargs=-Xmx300m" >> ~/.gradle/gradle.properties

RUN kobweb export --notty

#-----------------------------------------------------------------------------
# Create the final stage, which contains just enough bits to run the Kobweb
# server.
FROM java as run

ARG KOBWEB_APP_ROOT

COPY --from=export /project/${KOBWEB_APP_ROOT}/.kobweb .kobweb

# Because many free tiers only give you 512M of RAM, let's limit the server's
# memory usage to that. You can remove this ENV line if your server isn't so
# restricted. That said, 512M should be plenty for most (all?) sites.
ENV JAVA_TOOL_OPTIONS="-Xmx512m"
ENTRYPOINT .kobweb/server/start.sh
```

> [!NOTE]
> At the time of writing this message, Kobweb CLI v0.9.18 is the latest version, but newer versions may be available
> when you read this (although older versions should still work). See the "kobweb cli" badge at the top of
> the [Kobweb README](https://github.com/varabyte/kobweb) if you want to know the latest version.

> [!TIP]
> Kobweb works with Java 11, but general recommendation is to use newer releases as your runtime if you can, as they
> might contain security fixes and performance improvements. As you can see, we went with JDK 21 here, but you can use
> 11 or even something newer if it is available.
>
> The `eclipse-temurin` image, according to its docs, was designed to be both used for running apps and also generally
> useful as a base foundation, which is perfect for our needs. There are other images out there, and you are welcome to
> investigate further.

This Dockerfile instructs Render to:

1. Create an initial image, building on top of a slim image which has the JDK pre-installed.
2. Fetch a minimal set of applications to run and export our Kobweb project.
3. Create a final image, building on top of a slim with the JRE pre-installed.
4. Copy only the outputs of the Kobweb export process into the final image. This allows Render to discard all the
   intermediate files created in the first image, saving space and potentially improving your server's startup speed.
5. Declare an entrypoint that allows Render to trigger the `start.sh` script anytime it needs to spin up a new server.

### Deploy your site

We've reached the final stretch.

Add and push the CORS and Dockerfile changes to your repo:

```bash
$ git add . && git commit -m "Configuration for deploying to a web service on Render"
$ git push
```

Return to Render and open your dashboard.

From the options available, create a new `Web Service`. This will prompt you to find your relevant GitHub repo and
"Connect" it.

![Render Connect GitHub](/images/blog/2023/cloud-deploy/render-new-web-service-connect-github.png)

Afterwards, you'll be directed to a web service configuration page. You should only need to specify the service name,
as all other defaults should work fine. I used "kobweb-todo" in my case, but you may have to specify a name that's not
already taken.

When ready, press "Create Web Service".

![Render Config Web Service](/images/blog/2023/cloud-deploy/render-new-web-service-config.png)

Now, wait while Render follows the instructions in your Dockerfile. This process can take 10 minutes or more, so please
be patient.

![Render Deploy Screen](/images/blog/2023/cloud-deploy/render-web-service-deploy.png)

Once it's done, you should see the status switch from a grey "In progress" message to a green "Live" indicator:

![Render Live Indicator](/images/blog/2023/cloud-deploy/render-in-progress-to-live.png)

Click on your web service's link to see your site in action!

![Kobweb Site Deployed](/images/blog/2023/cloud-deploy/kobweb-app-deployed.png)

> [!NOTE]
> Your site might feel slow, especially during startup. That's the trade-off with a free service!

At this point, any time you push a new commit to your repo, Render will automatically rebuild and redeploy your site.

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

*If you're having trouble, you can compare your own project [with mine](https://github.com/bitspittle/kobweb-todo-on-render).*

This post covered the essentials for getting a Kobweb server running in the cloud.

For a complete production server experience, there's more to consider, including:

* Selecting data center(s) for deployment (to minimize latency for your users)
* Implementing user authentication, login, and logout flows
* Designing a scalable backend as your site's traffic increases
* Utilizing a decentralized database for data storage (to maintain state across server instances and crashes)
* Storing secrets (such as API keys) securely

In general, as your backend requirements grow, your Kobweb server will benefit from being designed as a middleman that
communicates with other longer-lived, decentralized services. With this approach, your server can be taken down, spun
back up, or even duplicated to handle varying loads.

Web service hosts (like Render, AWS, GCP, Azure, etc.) are designed to handle scaling for you! But you'll need to
consult their documentation for setup guidance.

However, with Kobweb and the general Dockerfile introduced in this post, you can explore different options and, if
necessary, transition your code to another service with minimal effort.

There's nothing like seeing your site live on the web. Thanks to companies like Render that offer a free tier for
hobbyists, it's easier than ever to get started developing rich, powerful web applications.

Happy coding!

## Thanks!

A huge thanks to Stevdza-San ([homepage](https://stevdza-san.com/), [YouTube channel](https://www.youtube.com/c/StevdzaSan))
for his collaboration while experimenting with the work that became this post. He introduced me to Render, and his
patience and feedback while we tested multiple iterations of attempts to get Kobweb running on Render was invaluable.
