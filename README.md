
# Windigo

![Windigo icon](http://cl.ly/image/1m1A3u3W3M2Z/Tonto-depp.jpg)

> In movie The Lone Ranger, Tonto claims to be the last **Windigo** Hunter of the Comanche and is adamant that Butch Cavendish is a **Windigo***.  

# Overview

###**Windigo**, is easy to use type-safe rest/http client for android and *android developers*.  


* **Windigo** includes ***default http client*** (apache http client) free you from writing same http client creation code. Currently includes most used http operations out of box with default configurations.

* **Windigo** allows you create your remote api's with declerative syntax. Use various annotations and create your remote api with simple java interface file.

* **Windigo** make request for you and return your model objects from remote api, no need to parse your custom objects, this is where type safety comes in.  


# How it works

Here is simple example.  
 

### 1. Define your remote api with simple interface
	@RestApi
	public interface LastfmRestApi {

		@Get("/2.0/")
		Response getAlbumInfo(@QueryParam("method") String method, @QueryParam("api_key") String api_key, ...);
	}


another one

	@RestApi
	public interface OpenWeatherApi {

		@Get("/weather")
		ForecastResponse getForecast(@QueryParam("lat") double latitude, @QueryParam("lon") double longtiude);
	}

### 2. Create http client from factory method with simple one liner

	// we need default http client
	HttpClient httpClient = HttpClientFactory.getDefaultHttpClient();


### 3. Instantiate your rest api interface

	// call factory method with url and interface class for rest api
	lastfmRestApi = RestApiFactory.createNewService("http://ws.audioscrobbler.com", LastfmRestApi.class, httpClient);
	
### 4. Start calling your rest methods

	// get type safe response directly
	Album album = lastfmRestApi.getAlbumInfo("album.getinfo", "49f6b21cab1c48100ee59f216645275e", "Cher", "Believe", "json");
	  
	
# Download
Download latest jar from [here](https://github.com/burakdd/windigo/raw/master/windigo-release/windigo.jar).  


# Windigo Annotations
### RestApi
Use with interface, indicates its a rest api interface

	@RestApi
	public interface YourApiInterface
	
### Get
Use with methods, indicates its a get request

	@Get("/weather")

### Post
Used with methods, indicates its a post request

	@Post("/user/new")
	
### Header
Used as parameter, give headers you want to send with http request.

	User getUser(@Header("X-Auth") String token, @Header("Accept-Encoding") String encoding)
	
### Placeholder
Used as parameter, replaces placeholder given with endpoint

	Get("/artist/{id}")
	Artist getArtist(@PlaceHolder("id") int id)
		
### QueryParam
Used as parameter, indicates the parameters send with request, does not matter get or post

	// /weather?lat=x&lng=y
	@Get("/weather")
	Forecast getForecast(@QueryParam("lat") double lat, @QueryParam("lng") double lng)
	
### QueryParamsObject
Used as parameter, send plain object as query parameter by mapping every field
	
	@Get("/user/update")
	User updateUser(@QueryParamsObject User user)
	  
	  
# Beware
* Current http/network operations on **Windigo** client are operated synchronously
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

  