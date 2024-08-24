package com.example.weather_app

import android.graphics.drawable.Icon
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.util.Log
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import com.example.weather_app.ui.theme.Silver
import com.example.weather_app.ui.theme.Weather_AppTheme
import kotlin.math.log


class MainActivity : ComponentActivity() {

    lateinit var instance: ViewModelClass
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       instance= ViewModelProvider(this).get(ViewModelClass::class.java)
        setContent {

                WeahterPage()


               }

    }

//for input text feild and exchautive handling
    @Composable
    fun WeahterPage() {

        val actualResult by instance.weatherResult.observeAsState()
        var city by rememberSaveable {
            mutableStateOf("")
        }
        if (city.isEmpty()){
            instance.getdefault()
        }
        val result =actualResult

        Column(modifier = Modifier.fillMaxSize()) {

//Search bar
            TextField(
                value = city,
                onValueChange = {

                    city = it

                },
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .height(60.dp)
                    .fillMaxWidth(1f)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(10.dp))
                    .border(
                        width = 2.dp, color = Color.Gray,
                        RoundedCornerShape(10.dp)
                    ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,

                    cursorColor = Color.Green,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,


                    ),

                placeholder = {
                    Text(text = "Search Your Place Here..")

                },
                leadingIcon = {
                    IconButton(onClick = {
                        instance.getcity(city)

                    }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    }
                },
                trailingIcon = {
                    IconButton(onClick = {
                        city = ""

                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                },
                singleLine = true, shape = RoundedCornerShape(10.dp),
            )
            //


            //Exchautive handling for different states
            when (result) {
                is NetworkState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.missing),
                            contentDescription = "",
                            modifier = Modifier.size(100.dp)
                        )
                        Text(text = result.error, fontSize = 17.sp)
                    }
                }

                is NetworkState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        CircularProgressIndicator(color = Color.Green)
                    }

                }

                is NetworkState.Success -> {
   Weather_AppTheme {

                    WeatherUi(result.data)
   }

                }

                is NetworkState.Default -> {
                    Weather_AppTheme {

                    DefaultUi(result.message)
                    }
                }


                null -> {

                }

            }
            //

        }

    }


}

// After Searching  we get the data from api
@Composable
fun WeatherUi(data:WeatherDataModel) {
    //for backgroumd image handling
    val background = when(data.current.condition.text.lowercase()) {
        "clear sky", "clear" -> R.drawable.clear

        "sunny"-> R.drawable.sunny

        "partly cloudy", "cloudy", "overcast", "mist", "foggy", "mostly cloudy" -> R.drawable.cloudy

        "light rain", "drizzle", "moderate rain", "shower", "heavy rain", "rainy", "rain", "patchy rain nearby","light rain shower" -> R.drawable.rainy

        "light snow", "heavy snow", "moderate snow", "blizzard", "snowy" -> R.drawable.snowy

        "thunderstorm", "thunder", "windy", "wind" ,"thundery outbreaks in nearby"-> R.drawable.thunderstorm

        else -> R.drawable.unkwown

    }
    //
        //MAin ui screen design
        Box(modifier = Modifier
            .fillMaxSize()
            ) {
            Image(painter = painterResource(id = background), contentDescription = "", modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop)
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(.1f), verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "", tint = MaterialTheme.colorScheme.primary)
                    Text(text = data.location.name, fontSize = 25.sp,fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = data.location.country, fontSize = 15.sp)

                }
                Text(text = data.current.temp_c + "°C", fontSize = 27.sp,fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(5.dp))
                Text(text = " Feels like  " + data.current.feelslike_c + "°C", fontSize = 15.sp)

                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = "https:${data.current.condition.icon}",
                    contentDescription = "",
                    modifier = Modifier.size(130.dp)
                )

                Text(text = data.current.condition.text, fontSize = 20.sp, fontWeight = FontWeight.Bold )
                Spacer(modifier = Modifier.height(20.dp))
//Container to show weather details
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color.Transparent)
                    .clip(
                        RoundedCornerShape(10.dp)
                    )
                    .border(width = 2.dp, color = Color.White, shape = RoundedCornerShape(10.dp))
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {


                        //First Row for Weather details
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.5f)
                                .padding(10.dp)
                        ) {

                            //humidity
                            Surface(
                                modifier = Modifier
                                    .width(50.dp)
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .border(
                                        width = 2.dp,
                                        color = Color.White,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                color = Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(10.dp)
                            ) {

                                Surface_Component(icon = painterResource(id = R.drawable.humidity), value =data.current.humidity , text ="Humidity" )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            //wind speed
                            Surface(
                                modifier = Modifier
                                    .width(50.dp)
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .border(
                                        width = 2.dp,
                                        color = Color.White,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                color = Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(10.dp)
                            ) {

                                Surface_Component(icon = painterResource(id = R.drawable.wind), value =data.current.wind_kph+" KM/H" , text ="Wind Speed" )

                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            //Visibility
                            Surface(
                                modifier = Modifier
                                    .width(50.dp)
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .border(
                                        width = 2.dp,
                                        color = Color.White,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                color = Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(10.dp)
                            ) {

                                Surface_Component(icon = painterResource(id = R.drawable.eye), value =data.current.vis_km+" KM" , text ="Visibility" )

                            }
                        }//

                        //Second  Row for Weather details
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .fillMaxHeight(0.5f)
                                .padding(10.dp)
                        ) {
                            //UV rays
                            Surface(
                                modifier = Modifier
                                    .width(50.dp)
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .border(
                                        width = 2.dp,
                                        color = Color.White,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                color = Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(10.dp)
                            ) {

                                Surface_Component(icon = painterResource(id = R.drawable.uv2), value =data.current.uv , text ="UV Rays" )


                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            //Local time
                            Surface(
                                modifier = Modifier
                                    .width(50.dp)
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .border(
                                        width = 2.dp,
                                        color = Color.White,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                color = Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Surface_Component(icon = painterResource(id = R.drawable.timer), value =data.location.localtime.split(" ")[1] , text ="Local Time" )


                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            //Local Date
                            Surface(
                                modifier = Modifier
                                    .width(50.dp)
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .border(
                                        width = 2.dp,
                                        color = Color.White,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                color = Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(10.dp)
                            ) {

                                Surface_Component(icon = painterResource(id = R.drawable.calender), value =data.location.localtime.split(" ")[0] , text ="Local Data" )
//
                                }

                            }
                        }
                    }
                }//



            }
        }
    //



// Default UI


@Composable
fun DefaultUi(data:WeatherDataModel) {
    //for background image handling
    val background = when (data.current.condition.text.lowercase()) {
        "clear sky", "clear" -> R.drawable.clear

        "sunny" -> R.drawable.sunny

        "partly cloudy", "cloudy", "overcast", "mist", "foggy", "mostly cloudy" -> R.drawable.cloudy

        "light rain", "drizzle", "moderate rain", "showers", "heavy rain", "rainy", "rain", "patchy rain nearby", "light rain shower" -> R.drawable.rainy

        "light snow", "heavy snow", "moderate snow", "blizzard", "snowy" -> R.drawable.snowy

        "thunderstorm", "thunder", "windy", "wind", "thundery outbreaks in nearby" -> R.drawable.thunderstorm

        else -> R.drawable.unkwown

    }

    //Main ui screen design


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = background),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.1f), verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "")
                Text(text = data.location.name, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = data.location.country, fontSize = 15.sp)

            }
            Text(
                text = data.current.temp_c + "°C",
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = " Feels like  " + data.current.feelslike_c + "°C", fontSize = 15.sp)

            Spacer(modifier = Modifier.height(10.dp))
            AsyncImage(
                model = "https:${data.current.condition.icon}",
                contentDescription = "",
                modifier = Modifier.size(130.dp)
            )

            Text(
                text = data.current.condition.text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))

            //Container to show weather details
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color.Transparent)
                    .clip(
                        RoundedCornerShape(10.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Column(modifier = Modifier.fillMaxSize()) {


                    //First Row for Weather details
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f)
                            .padding(10.dp)
                    ) {

                        //humidity
                        Surface(
                            modifier = Modifier
                                .width(50.dp)
                                .fillMaxHeight()
                                .weight(1f)
                                .border(
                                    width = 2.dp,
                                    color = Color.White,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            color = Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Surface_Component(
                                icon = painterResource(id = R.drawable.humidity),
                                value = data.current.humidity,
                                text = "Humidity"
                            )

//
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        //wind speed
                        Surface(
                            modifier = Modifier
                                .width(50.dp)
                                .fillMaxHeight()
                                .weight(1f)
                                .border(
                                    width = 2.dp,
                                    color = Color.White,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            color = Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Surface_Component(
                                icon = painterResource(id = R.drawable.wind),
                                value = data.current.wind_kph + " KM/H",
                                text = "Wind Speed"
                            )

//

                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        //Visibility
                        Surface(
                            modifier = Modifier
                                .width(50.dp)
                                .fillMaxHeight()
                                .weight(1f)
                                .border(
                                    width = 2.dp,
                                    color = Color.White,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            color = Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Surface_Component(
                                icon = painterResource(id = R.drawable.eye),
                                value = data.current.vis_km + " KM",
                                text = "Visibility"
                            )

//

                        }
                    }//

                    //Second  Row for Weather details
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
//                                .fillMaxHeight(0.5f)
                            .padding(10.dp)
                    ) {
                        //UV rays
                        Surface(
                            modifier = Modifier
                                .width(50.dp)
                                .fillMaxHeight()
                                .weight(1f)
                                .border(
                                    width = 2.dp,
                                    color = Color.White,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            color = Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Surface_Component(
                                icon = painterResource(id = R.drawable.uv2),
                                value = data.current.uv,
                                text = "UV Rays"
                            )

//

                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        //Local time
                        Surface(
                            modifier = Modifier
                                .width(50.dp)
                                .fillMaxHeight()
                                .weight(1f)
                                .border(
                                    width = 2.dp,
                                    color = Color.White,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            color = Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Surface_Component(
                                icon = painterResource(id = R.drawable.timer),
                                value = data.location.localtime.split(" ")[1],
                                text = "Local Time"
                            )

//

                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        //Local Date
                        Surface(
                            modifier = Modifier
                                .width(50.dp)
                                .fillMaxHeight()
                                .weight(1f)
                                .border(
                                    width = 2.dp,
                                    color = Color.White,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            color = Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Surface_Component(
                                icon = painterResource(id = R.drawable.calender),
                                value = data.location.localtime.split(" ")[0],
                                text = "Local Data"
                            )
//

                        }
                    }
                }
            }//

        }
    }
}

    //
//}

//  Surface layout



@Composable
fun Surface_Component(icon:Painter,value:String,text:String ) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = icon,
            contentDescription = "",
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = value,
            fontSize = 17.sp
        )
        Text(text = text, fontSize = 19.sp)
    }
}
//
//
//
