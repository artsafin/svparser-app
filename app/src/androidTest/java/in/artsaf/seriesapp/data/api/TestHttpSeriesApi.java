package in.artsaf.seriesapp.data.api;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TestHttpSeriesApi extends HttpSeriesApi {
    public TestHttpSeriesApi(final Response.Builder respb, final String jsonBody) {
        super();

        this.executor = new RequestExecutor() {
            @Override
            public Response execute(Request req) throws IOException {
                return respb
                        .request(req)
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .body(ResponseBody.create(MediaType.parse("application/json"), jsonBody))
                        .build();
            }
        };
    }

}
