# Appium 3.x Compatibility — Migration Notes

This document records the changes made to upgrade the framework from
**Appium Java Client 8.5.1 (Appium 1.x era)** to a stack compatible with the
**Appium 3.x server**.

## Summary

| Area | Before | After |
|------|--------|-------|
| Appium Java Client | `8.5.1` | **`9.4.0`** (the line that targets Appium Server 2.x/3.x) |
| Selenium | `4.11.0` | **`4.33.0`** (pinned inside 9.4.0's required range) |
| Capabilities API | `DesiredCapabilities` | **`UiAutomator2Options` / `XCUITestOptions`** (W3C, auto `appium:` prefix) |
| Server base path | `…:4723/wd/hub` (Android) | **`…:4723/`** for both platforms (`/wd/hub` removed in Appium 2.x/3.x) |
| iOS UDID capability | `undid` (typo — ignored) | **`udid`** |
| Build JDK | Java 21 target | Java 21 target (**build with JDK 21**) |

---

## 1. `pom.xml` — dependency versions

**Appium Java Client → 9.4.0.** The `8.x` line is built for the legacy
Appium 1.x server; `9.x` is the line compatible with Appium Server **2.x and 3.x**.

```xml
<!-- 9.x is the line compatible with Appium Server 2.x/3.x -->
<dependency>
    <groupId>io.appium</groupId>
    <artifactId>java-client</artifactId>
    <version>9.4.0</version>
</dependency>
```

**Selenium → 4.33.0 (pinned).** Java Client `9.4.0` declares Selenium as a
*version range* `[4.26.0, 5.0)`. Leaving that unpinned makes builds
non-reproducible, and a mismatched Selenium is exactly what caused the
`NoClassDefFoundError: .../RemoteWebDriver` recorded in `Issue and Solutions.txt`.
We pin an explicit in-range version so the build is deterministic:

```xml
<!-- Pinned within java-client 9.4.0's required range [4.26.0, 5.0) for a reproducible build -->
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.33.0</version>
</dependency>
```

Resolved dependency tree (no conflict — direct pin wins over the transitive range):

```
+- org.seleniumhq.selenium:selenium-java:jar:4.33.0:compile
|  +- selenium-api:jar:4.33.0
|  +- selenium-remote-driver:jar:4.33.0
|  \- selenium-support:jar:4.33.0
+- io.appium:java-client:jar:9.4.0:compile
```

---

## 2. `AppLauncher.java` — capabilities & server URL

### a) `DesiredCapabilities` → typed `Options` builders

Appium 3.x is strictly W3C: every non-standard capability must carry the
`appium:` prefix. The `UiAutomator2Options` / `XCUITestOptions` builders apply
that prefix automatically and are type-safe.

**Before**
```java
DesiredCapabilities caps = new DesiredCapabilities();
caps.setCapability("platformName", "Android");
caps.setCapability("deviceName", "emulator-5554");
caps.setCapability("automationName", "UiAutomator2");
caps.setCapability("noReset", false);
caps.setCapability("avdLaunchTimeout", 250000);
// ...
driver.set(new AndroidDriver(URI.create("http://127.0.0.1:4723/wd/hub").toURL(), caps));
```

**After**
```java
UiAutomator2Options options = new UiAutomator2Options();
options.setPlatformName("Android");
options.setDeviceName("emulator-5554");
options.setAutomationName("UiAutomator2");
options.setNoReset(false);
options.setAvdLaunchTimeout(Duration.ofMillis(250000));
// ...
driver.set(new AndroidDriver(URI.create(APPIUM_SERVER_URL).toURL(), options));
```

iOS uses `XCUITestOptions` in the same way.

### b) Server base path — `/wd/hub` removed

Appium 2.x/3.x serves at `/` by default; the legacy `/wd/hub` suffix is gone.
Both platforms now share a single constant:

```java
// Appium 2.x/3.x default base path is "/" — the legacy "/wd/hub" suffix was removed
private static final String APPIUM_SERVER_URL = "http://127.0.0.1:4723/";
```

### c) Correctness fixes that came with the typed API

- **`undid` → `udid`** (iOS): the old string-key typo silently dropped the UDID;
  the builder's `setUdid(...)` makes it correct and compiler-checked.
- **iOS `noReset`** was the string `"true"` → now a real `boolean` via `setNoReset(true)`.
- **Timeouts** are now `Duration` types (same values):
  `avdLaunchTimeout` = `Duration.ofMillis(250000)` (250 s),
  `newCommandTimeout` = `Duration.ofSeconds(300)`.

### d) Imports

```diff
- import org.openqa.selenium.remote.DesiredCapabilities;
- import java.net.URL;
+ import io.appium.java_client.android.options.UiAutomator2Options;
+ import io.appium.java_client.ios.options.XCUITestOptions;
+ import java.time.Duration;
```

---

## 3. Nothing to migrate for `TouchAction`

`TouchAction` / `MultiTouchAction` were **removed** in Java Client 9.x (use
`W3C Actions` / `PointerInput` instead). This framework never used them, so no
change was required.

---

## 4. Build & verification

> **Build with JDK 21.** `pom.xml` targets Java 21. Building with an older JDK
> (e.g. 17) fails with *"release 21 not supported"*.

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
mvn clean test-compile
```

Result: **BUILD SUCCESS** — all main and test sources compile against
Java Client 9.4.0 + Selenium 4.33.0, dependency tree conflict-free.

A full `mvn test` additionally requires a running **Appium 3.x server** and an
emulator/simulator:

- **Android:** Appium server reachable at `http://127.0.0.1:4723/`.
- **iOS:** start the server from a terminal with `appium --allow-cors`.

---

## Not yet addressed (pre-existing, optional)

- `Hook.java` still hardcodes `initializeDriver("Android")` despite the README
  documenting dynamic `-Dplatform=` switching.
- Log4j2 is a dependency but unused (no `log4j2.xml`; code uses `System.out`).
