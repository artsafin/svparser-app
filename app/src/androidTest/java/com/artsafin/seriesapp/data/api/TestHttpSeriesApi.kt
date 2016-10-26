package com.artsafin.seriesapp.data.api

import java.io.IOException

import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody

class TestHttpSeriesApi(respb: Response.Builder, jsonBody: String) : HttpSeriesApi() {
    init {

        this.executor = object : HttpSeriesApi.RequestExecutor {
            @Throws(IOException::class)
            override fun execute(req: Request): Response {
                return respb.request(req).protocol(Protocol.HTTP_1_1).code(200).body(
                        ResponseBody.create(MediaType.parse("application/json"), jsonBody)).build()
            }
        }
    }

}
