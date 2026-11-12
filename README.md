# Phone Health / 手机健康

A small, honest Android app: a phone health dashboard + surface cleanup. No root needed. No ads. No account.

一款小而实在的安卓应用：手机健康看板 + 表面清理。无需 root，无广告，无需注册账号。

## What it does / 功能

- **Storage / RAM / battery dashboard** — used vs total, battery level, temperature and health. Daily reminder at 21:00 to clean up.
  **存储 / 内存 / 电池看板**——已用与总量、电量、温度与健康度；每天 21:00 提醒清理一次。
- **Surface junk scan** — finds leftover installer packages (.apk), temp / log / backup / dump files, empty folders, and files over 100 MB outside Android-private paths. You tick what to delete; nothing is deleted automatically.
  **表面垃圾扫描**——找出残留安装包（.apk）、临时 / 日志 / 备份 / 转储文件、空文件夹，以及 Android 私有目录之外的 100 MB 以上大文件。由你勾选删除，绝不自动删除。
- **Shortcuts** — jump to the system storage manager and app manager.
  **快捷入口**——直达系统存储管理和应用管理。

**Honest scope / 诚实说明：** Android does not allow deep system cleaning without root. This app only does surface cleanup + a health dashboard — it cannot do a WPT-style deep optimization on a phone.
安卓不开放深层清理（无 root 做不到），本应用只做表面清理 + 健康看板，无法像电脑端那样做深度优化。

## Downloads / 下载

| Version / 版本 | File / 文件 |
|---|---|
| Chinese / 中文版 | `apk/phone-health-zh-v1.0.apk` |
| English / 英文版 | `apk/phone-health-en-v1.0.apk` |

Install on your phone: allow “Install unknown apps” for your browser / file manager when asked, then open the APK. Grant the notification permission (for the 21:00 reminder) and “All files access” (for junk scanning) when the app asks.
安装方法：打开 APK 时，按提示允许浏览器 / 文件管理器“安装未知应用”；应用内按提示授予通知权限（21:00 提醒用）和“所有文件访问权限”（扫描垃圾用）。

## Build from source / 自行编译

```bash
bash build.sh
```

Needs JDK 17 and Android SDK (build-tools 34, platform android-34). The script compiles with `javac`, dexes with `d8`, links the manifest with `aapt2`, aligns with `zipalign` and signs with `apksigner` — no Gradle daemon needed. Both APKs are built from one source tree; `Strings.java` holds the Chinese and English text, and the build flips one flag for the English version.
需要 JDK 17 与 Android SDK（build-tools 34、android-34 平台）。脚本用 `javac` 编译、`d8` 转 dex、`aapt2` 打包、`zipalign` 对齐、`apksigner` 签名，无需 Gradle。两个版本共用同一份源码，中英文文本都在 `Strings.java` 里，编译英文版时脚本只切换一个开关。

## Free to use — no liability / 免费使用，作者不承担责任

This software is **free for anyone to use**, under the MIT License (see `LICENSE`). It is provided **“as is”, without warranty of any kind**. The author is **not liable for any claim, damages, or other liability** arising from using it — including any files you choose to delete with it. Review what you tick before you tap delete.
本软件**免费供任何人使用**，采用 MIT 许可证（见 `LICENSE` 文件）。软件按“原样”提供，**不作任何保证**；作者对使用本软件产生的**任何索赔、损失或其他责任概不负责**，包括你用它删除的文件。点删除前请先检查勾选内容。
