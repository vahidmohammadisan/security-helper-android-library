
[![](https://jitpack.io/v/vahidmohammadisan/security-helper.svg)](https://jitpack.io/#vahidmohammadisan/security-helper)

# Security Helper

SecurityHelper is a android library to check the security of your projects.

## Installation

Step 1. Add the JitPack repository to your build file

```bash
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Step 2. Add the dependency

```bash
dependencies {
	        implementation 'com.github.vahidmohammadisan:security-helper:1.0.0'
	}
```

## Usage

```kotlin
       val safe = SafeBuilder(this)
            .checkRoot(true)
            .checkEmulator(true)
            .checkSignature("", true)
            .checkDebuggable(true)
            .checkHookDetected(true)
            .checkVPN(true)
            .check()

        val s = safe.isSafe // boolean
```
