package com.robert.phonehealth;

/**
 * All user-visible text in Chinese and English.
 *
 * The build script produces two APKs from this single source tree:
 *  - Chinese version: EN = false (default)
 *  - English version: EN = true (flipped by build.sh for the en variant)
 */
public final class Strings {

    // Flipped to true by build.sh when building the English APK.
    public static final boolean EN = false;

    private Strings() {}

    public static String appName()        { return EN ? "Phone Health" : "手机健康"; }
    public static String storage()        { return EN ? "Storage" : "存储空间"; }
    public static String ram()            { return EN ? "RAM" : "运行内存"; }
    public static String battery()        { return EN ? "Battery" : "电池"; }
    public static String cleanup()        { return EN ? "Surface cleanup" : "表面清理"; }
    public static String junkHint() {
        return EN ? "Tap the button below to scan for leftover installer packages, temp files and other surface junk."
                  : "点下面按钮扫描残留安装包、临时文件等表面垃圾。";
    }
    public static String scan()           { return EN ? "Scan for junk files" : "扫描垃圾文件"; }
    public static String deleteSelected() { return EN ? "Delete selected" : "删除选中"; }
    public static String shortcuts()      { return EN ? "Shortcuts" : "快捷入口"; }
    public static String openStorage()    { return EN ? "Open system storage settings" : "打开系统存储管理"; }
    public static String manageApps()     { return EN ? "Manage apps (uninstall what you don't use)" : "管理应用（卸载不用的）"; }
    public static String note() {
        return EN ? "\nYou'll get a daily cleanup reminder at 21:00.\nNote: Android doesn't allow deep cleaning — this app only does surface cleanup + a health dashboard."
                  : "\n每天 21:00 会提醒你清理一次。\n注意：安卓不开放深层清理，本 App 只做表面清理 + 健康看板。";
    }
    /** Format: used, total. */
    public static String usedOf()         { return EN ? "Used %s / %s total" : "已用 %s / 共 %s"; }
    public static String batteryWord()    { return EN ? "Battery" : "电量"; }
    public static String charging()       { return EN ? " (charging)" : "（充电中）"; }
    public static String tempWord()       { return EN ? "Temp " : "温度 "; }
    public static String healthWord()     { return EN ? "Health " : "健康度 "; }
    public static String tempHigh() {
        return EN ? "\n⚠ Running hot — let it rest a while"
                  : "\n⚠ 温度偏高，建议歇一歇";
    }
    public static String healthGood()     { return EN ? "Good" : "良好"; }
    public static String healthOverheat() { return EN ? "Overheating" : "过热"; }
    public static String healthCold()     { return EN ? "Too cold" : "过冷"; }
    public static String healthOverVolt() { return EN ? "Over-voltage" : "电压过高"; }
    public static String healthDead()     { return EN ? "Damaged" : "损坏"; }
    public static String healthUnknown()  { return EN ? "Unknown" : "未知"; }
    public static String needPermission() {
        return EN ? "Please grant \"All files access\" first"
                  : "需要先授予“所有文件访问权限”";
    }
    public static String cleanNone() {
        return EN ? "All clean — no surface junk found."
                  : "很干净，没扫到表面垃圾。";
    }
    /** Format: count, total size. */
    public static String foundJunk() {
        return EN ? "Found %d items to clean, %s total (tick to delete)"
                  : "找到 %d 个可清理项，共 %s（勾选后删除）";
    }
    /** Format: count, freed size. */
    public static String deleted()        { return EN ? "Deleted %d items, freed %s" : "删除 %d 项，释放 %s"; }
    public static String channelName()    { return EN ? "Cleanup reminder" : "清理提醒"; }
    public static String notifTitle()     { return appName(); }
    public static String notifText() {
        return EN ? "Time to clean up your phone — tap to open"
                  : "该清理一下手机了，点我打开看看";
    }
}
