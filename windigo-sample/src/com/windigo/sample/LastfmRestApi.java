package com.windigo.sample;

import com.windigo.annotations.Get;
import com.windigo.annotations.QueryParam;
import com.windigo.annotations.RestApi;
import com.windigo.sample.lastfm.Response;

@RestApi
public interface LastfmRestApi {

	@Get("/2.0/")
	Response getAlbumInfo(@QueryParam("method") String method, @QueryParam("api_key") String api_key, @QueryParam("artist") String artist, @QueryParam("album") String album, @QueryParam("format") String format);
}
