package com.nicolasght.salut

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * Polls GitHub Releases for a newer version and offers install via PackageInstaller intent.
 * Repo: github.com/nicolasght/salut-app
 */
class Updater(private val activity: Activity) {

    companion object {
        private const val OWNER = "nicolasght"
        private const val REPO = "salut-app"
        private const val API = "https://api.github.com/repos/$OWNER/$REPO/releases/latest"
    }

    fun checkForUpdateAsync(onResult: ((String) -> Unit)? = null) {
        thread {
            try {
                val conn = (URL(API).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 8000
                    readTimeout = 8000
                    setRequestProperty("Accept", "application/vnd.github+json")
                }
                if (conn.responseCode != 200) {
                    activity.runOnUiThread { onResult?.invoke("Erreur réseau (${conn.responseCode})") }
                    return@thread
                }
                val body = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(body)
                val tag = json.getString("tag_name").removePrefix("v")
                val current = BuildConfig.VERSION_NAME
                if (isNewer(tag, current)) {
                    val assets = json.getJSONArray("assets")
                    var apkUrl: String? = null
                    for (i in 0 until assets.length()) {
                        val a = assets.getJSONObject(i)
                        if (a.getString("name").endsWith(".apk")) {
                            apkUrl = a.getString("browser_download_url"); break
                        }
                    }
                    if (apkUrl != null) {
                        activity.runOnUiThread {
                            onResult?.invoke("Mise à jour disponible : v$tag")
                            promptInstall(tag, apkUrl)
                        }
                    } else {
                        activity.runOnUiThread { onResult?.invoke("Release v$tag sans APK") }
                    }
                } else {
                    activity.runOnUiThread { onResult?.invoke("À jour (v$current)") }
                }
            } catch (e: Exception) {
                activity.runOnUiThread { onResult?.invoke("Erreur: ${e.message}") }
            }
        }
    }

    private fun isNewer(remote: String, local: String): Boolean {
        val r = remote.split(".").map { it.toIntOrNull() ?: 0 }
        val l = local.split(".").map { it.toIntOrNull() ?: 0 }
        val n = maxOf(r.size, l.size)
        for (i in 0 until n) {
            val rv = r.getOrElse(i) { 0 }; val lv = l.getOrElse(i) { 0 }
            if (rv > lv) return true
            if (rv < lv) return false
        }
        return false
    }

    private fun promptInstall(version: String, apkUrl: String) {
        AlertDialog.Builder(activity)
            .setTitle("Mise à jour v$version")
            .setMessage("Télécharger et installer ?")
            .setPositiveButton("Installer") { _, _ -> downloadAndInstall(apkUrl) }
            .setNegativeButton("Plus tard", null)
            .show()
    }

    private fun downloadAndInstall(apkUrl: String) {
        thread {
            try {
                val dir = File(activity.getExternalFilesDir(null), "updates").apply { mkdirs() }
                val out = File(dir, "update.apk")
                (URL(apkUrl).openConnection() as HttpURLConnection).inputStream.use { input ->
                    out.outputStream().use { input.copyTo(it) }
                }
                val uri = FileProvider.getUriForFile(activity, "com.nicolasght.salut.fileprovider", out)
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/vnd.android.package-archive")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                activity.startActivity(intent)
            } catch (e: Exception) {
                activity.runOnUiThread {
                    AlertDialog.Builder(activity)
                        .setTitle("Échec du téléchargement")
                        .setMessage(e.message ?: "Erreur inconnue")
                        .setPositiveButton("OK", null).show()
                }
            }
        }
    }
}
