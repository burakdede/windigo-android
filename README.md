# Windigo 

### **Windigo**, is easy to use type-safe rest/http client for android and *android developers*. 


# Documentation

Refer to **[documentation site](http://burakdd.github.io/windigo/)** for more info.

* **Windigo** free you from writing same http client creation code. Currently includes most used http clients out of box with default configurations.

* **Windigo** allows you create your remote api's with declerative syntax. Use various annotations and create your remote api with simple java interface file.

* **Windigo** make request for you and return your model objects from remote api, no need to parse your custom objects, this is where type safety comes in.  

# Whats New

* Current operations working on asynchronously.
* Choose your http client **Windigo** works with most of them. Currently support Apache HttpClient, HttpUrlConnectionClient and Square's OkHttpClient 
* Removed annotation support for **QueryObjectParam** which let you map POJO to request parameters.
* Cookie support for http clients
* Faster response handling with streams, removed raw response memory footprint.

# Download

Download latest jar from [here](https://github.com/burakdd/windigo/raw/master/windigo-release/windigo.jar).


# Roadmap
* <del>Replace OkHttp with HttpClient</del> Let developers choose which http client library to use (okhttp, httpclient etc.)
* <del>Optional asynchronous requests with callbacks</del>
* Let developer choose whether to get response stream or actual raw respose string
* HttpUrlConnectionClient http-https follow problem
* <del>Cookie support for clients</del>
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

  
