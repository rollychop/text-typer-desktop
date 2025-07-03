# 🖥️ Text Typer - Compose Desktop App

Text Typer is a **Jetpack Compose Desktop** application that simulates automated typing using the `Robot` class. You can input any text, configure a start delay, and watch it "type" the characters into your active editor window after a countdown. Useful for presentations, demos, or just for fun.

---

## ✨ Features

* ✅ Built with **Jetpack Compose for Desktop**
* ⏱️ Configurable typing start delay (1–20 seconds)
* 🔁 Start/Stop controls with a real-time countdown
* 🎵 (Optional) Typing sound support using synthesizer
* 🧠 Thread-safe coroutine handling for background typing
* 🔐 Escape key (`Esc`) stops typing immediately
* ⌨️ Keyboard input simulation via `java.awt.Robot`

---

## 📦 Requirements

* JDK **17 or later**
* Kotlin **1.9+**
* Jetpack Compose for Desktop dependencies
* Works only on **Desktop (Windows, macOS, Linux)**

---

## 🚀 How to Run

1. Clone the project:

```bash
git clone https://github.com/rollychop/text-typer-desktop.git
cd text-typer-desktop
```

2. Build & Run with Gradle:

```bash
./gradlew run
```

> Note: You can also open the project in IntelliJ IDEA (with Kotlin & Compose plugins) and run `main()`.

---

## 🧪 Usage Tips

1. Enter your text into the input box.
2. Set a start delay (in milliseconds) — default is `3000` ms (3 seconds).
3. Click **Start**.
4. Switch to any text editor where you want the text to appear.
5. Typing will begin after the delay.
6. Press `Esc` anytime to cancel mid-way.

---

## ⚠️ Limitations

* Does **not** work on Android or Web — it's a **Compose Desktop-only** app.
* Robot class may behave differently on some OSes (especially Wayland/Linux).
* Typing is sent to the currently active/focused window.

---

## 📁 License

MIT License — feel free to use, modify, and contribute!

---

## 🙏 Credits

* Built with [JetBrains Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
* Kotlin coroutines for async logic
* Java `Robot` for key simulation
