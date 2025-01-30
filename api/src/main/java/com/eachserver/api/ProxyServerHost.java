package com.eachserver.api;

import java.net.URI;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProxyServerHost {

    public static final String PATH = "/proxyserver/host";

    @Getter
    @Setter
    public static class ResponseBody {

        private Host host;

        @Getter
        @Setter
        public static class Host {

            private URI uri;
        }
    }
}
