import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bohush.surveyapp.R

@Composable
fun StartSurveyScreen(
    onStartSurvey: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp)
    ) {
        Text(
            text = stringResource(id = R.string.welcome),
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(64.dp))
        Button(
            onClick = onStartSurvey
        ) {
            Text(
                stringResource(id = R.string.start_survey),
                modifier = Modifier.padding(horizontal = 42.dp, vertical = 4.dp), fontSize = 16.sp
            )
        }
    }

}