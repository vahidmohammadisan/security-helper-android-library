# Security Helper

SecurityHelper is a android library to check the security of your projects.

## Installation

Use the package manager [pip](https://pip.pypa.io/en/stable/) to install foobar.

```bash
pip install foobar
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

        val s = safe.isSafe
```
