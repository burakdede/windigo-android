# Windigo

![Windigo icon](http://cl.ly/image/1m1A3u3W3M2Z/Tonto-depp.jpg)

> In movie The Lone Ranger, Tonto claims to be the last **Windigo** Hunter of the Comanche and is adamant that Butch Cavendish is a **Windigo***.  

# Overview

###**Windigo**, is easy to use type-safe rest/http client for android and *android developers*.  


* **Windigo** includes ***default http client*** (apache http client) free you from writing same http client creation code. Currently includes most used http operations out of box with default configurations.

* **Windigo** allows you create your remote api's with declerative syntax. Use various annotations and create your remote api with simple java interface file.

* **Windigo** make request for you and return your model objects from remote api, no need to parse your custom objects, this is where type safety comes in.  

# Whats New

* Current operations working on asynchronously thanks to AsyncTask code will be much cleaner.


# How it works

Here is simple example.  
 

### 1. Define your remote api with simple interface
```java
@RestApi
public interface LastfmRestApi {

	@Get("/2.0/")
	Response getAlbumInfo(@QueryParam("method") String method, @QueryParam("api_key") String api_key, ...);
}
```

another one
```java
@RestApi
public interface OpenWeatherApi {

	@Get("/weather")
	ForecastResponse getForecast(@QueryParam("lat") double latitude, @QueryParam("lon") double longtiude);
}
```
### 2. Create http client from factory method with simple one liner
```java
// we need default http client
HttpClient httpClient = HttpClientFactory.getDefaultHttpClient();

```
### 3. Instantiate your rest api interface
```java
// call factory method with url and interface class for rest api
lastfmRestApi = RestApiFactory.createNewService("http://ws.audioscrobbler.com", LastfmRestApi.class, httpClient);
```	
### 4. Start calling your rest methods
```java
// get type safe response directly
Album album = lastfmRestApi.getAlbumInfo("album.getinfo", "49f6b21cab1c48100ee59f216645275e", "Cher", "Believe", "json");
```	
# Download
Download latest jar from [here](https://github.com/burakdd/windigo/raw/master/windigo-release/windigo.jar).  

# Before & After
###With windigo its easy and clean.
```java	
	// we need default http client
	HttpClient httpClient = HttpClientFactory.getDefaultHttpClient();
		
	// call factory method with url and interface class for rest api
	lastfmRestApi = RestApiFactory.createNewService("http://ws.audioscrobbler.com", LastfmRestApi.class, httpClient);
		
	// call factory method with url and interface class for rest api		
	openWeatherApi = RestApiFactory.createNewService("http://api.openweathermap.org/data/2.5", 
				OpenWeatherApi.class, httpClient);
	// this is asynchronous operation
	ForecastpResponse forecast	= openWeatherApi.getForecast(41.163267, 29.094187);
```
###Without windigo library simple request like this gets out of your hand and becomes complicated.
```java
	private class RegularHttpRestTask extends AsyncTask<Void, Integer, ForecastResponse> {

		@Override
		protected ForecastResponse doInBackground(Void... params) {
			
			final HttpParams httpParams = new BasicHttpParams();
			
	        final SchemeRegistry supportedSchemes = new SchemeRegistry();

	        final SocketFactory sf = PlainSocketFactory.getSocketFactory();
	        supportedSchemes.register(new Scheme("http", sf, 80));
	        supportedSchemes.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
			
			HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);
			HttpConnectionParams.setConnectionTimeout(httpParams, 60 * 1000);
			HttpConnectionParams.setSoTimeout(httpParams, 60 * 1000);
			HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
			
			final ClientConnectionManager ccm = new ThreadSafeClientConnManager(httpParams,
	                supportedSchemes);
			
			HttpClient httpClient = new HttpClient(new DefaultHttpClient(ccm, httpParams));
			HttpGet get = new HttpGet("http://api.openweathermap.org/data/2.5/weather?lat=41.163267&lon=29.094187");
			HttpResponse response = null;
			ForecastResponse forecast = new ForecastResponse();
			JSONObject responseJsonObject;
			
			try {
				response =  httpClient.executeHttpRequest(get);
				String responseString = EntityUtils.toString(response.getEntity());
				responseJsonObject = new JSONObject(responseString);
				
				forecast.setName(responseJsonObject.getString("name"));
				
				// get main response
				JSONObject mainJsonObject = responseJsonObject.getJSONObject("main");
				forecast.setMain(new MainResponse(mainJsonObject.getDouble("temp"), 
											mainJsonObject.getDouble("temp_min"), 
											mainJsonObject.getDouble("temp_max"), 
											mainJsonObject.getInt("humidity")));
				
				// get wind respose
				JSONObject windJsonObject = responseJsonObject.getJSONObject("wind");
				forecast.setWind(new WindResponse((float) windJsonObject.getDouble("speed")));
				
				JSONArray weathJsonArray = responseJsonObject.getJSONArray("weather");
				List<WeatherResponse> weatherResponses = new ArrayList<WeatherResponse>();
				for (int i = 0; i < weathJsonArray.length(); i++) {
					JSONObject weatherJsonObject = weathJsonArray.getJSONObject(i);
					weatherResponses.add(new WeatherResponse(weatherJsonObject.getString("description"), weatherJsonObject.getString("icon")));
				}
				forecast.setWeather(weatherResponses);
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
			return forecast;
		}
		
		@Override
		protected void onPostExecute(ForecastResponse result) {
			super.onPostExecute(result);
			responseTextView.setText(result.toString());
		}
		
	}
```

# Windigo Annotations
### RestApi
Use with interface, indicates its a rest api interface
```java
@RestApi
public interface YourApiInterface
```	
### @Get
Use with methods, indicates its a get request
```java
@Get("/weather")
```
### @Post
Used with methods, indicates its a post request
```java
@Post("/user/new")
```	
### @Header
Used as parameter, give headers you want to send with http request.
```java
User getUser(@Header("X-Auth") String token, @Header("Accept-Encoding") String encoding)
```	
### @Placeholder
Used as parameter, replaces placeholder given with endpoint
```java
Get("/artist/{id}")
Artist getArtist(@PlaceHolder("id") int id)
```		
### @QueryParam
Used as parameter, indicates the parameters send with request, does not matter get or post
```java
// /weather?lat=x&lng=y
@Get("/weather")
Forecast getForecast(@QueryParam("lat") double lat, @QueryParam("lng") double lng)
```	
### @QueryParamsObject
Used as parameter, send plain object as query parameter by mapping every field
```java	
@Get("/user/update")
User updateUser(@QueryParamsObject User user)
```	  
	  
# Beware
* <del>Current http/network operations on **Windigo** client are operated synchronously</del> **all requests work asynchronously**
* Library currently in development so check back regularly or star/fork it.  
  

# Dependencies
Only dependency windigo needs is [google gson](https://code.google.com/p/google-gson/) library. Its used for type conversion, no need to reinvent the wheel and writing all conversion code again.  


# Roadmap
* Optional asynchronous requests with callbacks
* Response caching
* Advanced logging and profiling for requests
* Detailed exception and error handling  


# License
 	Copyright (C) Burak Dede.
 
 	Licensed under the Apache License, Version 2.0 (the "License");
 	you may not use this file except in compliance with the License.
 	You may obtain a copy of the License at
 
    	   http://www.apache.org/licenses/LICENSE-2.0
 	
 	Unless required by applicable law or agreed to in writing, software
 	distributed under the License is distributed on an "AS IS" BASIS,
 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 	See the License for the specific language governing permissions and
 	limitations under the License.

  
