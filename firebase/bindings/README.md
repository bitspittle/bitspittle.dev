This project is a collection of bindings for working with various Firebase services in Kotlin/JS.

The goal of these bindings are to provide a clean, Kotlin-idiomatic view of Firebase web services:
* JavaScript methods that return `Promise`s are converted to `suspend fun`s in Kotlin
* Extension methods are added to make types feel more object-oriented.
* Some methods which take JSON-objects in JavaScript are changed to take typed objects in Kotlin
* Constructor (factory) methods are added when possible to make constructing JavaScript interfaces with optional
  parameters easier.

## Background

This project contains bindings I need for using various Firebase services, wrapping API calls from
[the web API](https://firebase.google.com/docs/reference/js) and porting them to Kotlin.

Honestly, I probably should have used an existing solution (for example,
[GitLiveApp/firebase-kotlin-sdk](https://github.com/GitLiveApp/firebase-kotlin-sdk)), but I wanted to see what it's like
to wrap a JS API with a Kotlin one. This is, at the moment, a learning project.

## Usage

This library exposes all functionality through a `Firebase` object.

So for example, this JavaScript code (taken from
[the tutorials](https://firebase.google.com/docs/database/web/read-and-write#basic_write)):

```javascript
import { initializeApp } from "firebase/app";
import { getDatabase, ref, set } from "firebase/database";

const firebaseOptions = { /* ... */ };
const app = initializeApp(firebaseOptions)

function writeUserData(userId, name, email, imageUrl) {
  const db = getDatabase(app);
  set(ref(db, 'users/' + userId), {
    username: name,
    email: email,
    profile_picture : imageUrl
  });
}
```

translates to the following Kotlin code if transcribed directly:

```kotlin
import Firebase.App.initializeApp
import Firebase.Database.getDatabase
import Firebase.Database.ref
import Firebase.Database.set

val app = initializeApp(FirebaseOptions(/*...*/))

fun writeUserData(userId: String, name: String, email: String, imageUrl: String) {
    val db = getDatabase(app)
    set(ref(db, "users/$userId"), json(
        "username" to name,
        "email" to email,
        "profile_picture" to imageUrl
    ))
}
```

However, a bunch of extension methods are provided, to make working with the code feel a bit more natural:

```kotlin
val app = initializeApp(FirebaseOptions(/*...*/))

fun writeUserData(userId: String, name: String, email: String, imageUrl: String) {
    val db = app.getDatabase()
    db.ref("users/$userId").set(json(
        "username" to name,
        "email" to email,
        "profile_picture" to imageUrl
    ))
}
```
