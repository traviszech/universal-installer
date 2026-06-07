package app.pwhs.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import app.pwhs.core.data.AppRepository
import app.pwhs.tv.ui.theme.UniversalInstallerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = AppRepository(applicationContext)
        setContent {
            UniversalInstallerTheme {
                ManageScreen(repo = repo)
            }
        }
    }
}
