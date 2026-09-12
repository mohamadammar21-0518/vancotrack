package my.edu.aiu.app.tdminsight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import my.edu.aiu.app.tdminsight.ui.navigation.TdmNavigation
import my.edu.aiu.app.tdminsight.ui.theme.TdmInsightTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TdmInsightTheme {
                Surface(modifier = Modifier.fillMaxSize()) { TdmNavigation() }
            }
        }
    }
}
