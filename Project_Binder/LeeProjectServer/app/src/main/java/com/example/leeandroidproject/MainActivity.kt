package com.example.leeandroidproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.leeandroidproject.ui.theme.LeeAndroidProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 打印包名信息
        Log.d("PackageInfo", "包名: $packageName")
        Toast.makeText(this, "包名: $packageName", Toast.LENGTH_LONG).show()
        enableEdgeToEdge()
        setContent {
            LeeAndroidProjectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "欢迎使用 Android 应用",
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = {
                // 跳转到绑定服务Activity
                val intent = Intent(context, BoundServiceActivity::class.java)
                context.startActivity(intent)
            }
        ) {
            Text(text = "跳转到绑定服务")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // 启动服务
                val serviceIntent = Intent(context, MyBoundService::class.java)
                context.startService(serviceIntent)
            }
        ) {
            Text(text = "启动服务")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LeeAndroidProjectTheme {
        Greeting("Android")
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    LeeAndroidProjectTheme {
        MainScreen()
    }
}