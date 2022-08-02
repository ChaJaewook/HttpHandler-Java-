import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


public class HttpHandler {
    CloseableHttpClient _httpClient=null;
    HttpGet _httpGet=null;
    HttpPost _httpPost=null;
    RequestConfig _requestConfig=null;

    HashMap<String, String> _requestHeader=new HashMap<String, String>();

    public void HttpHandler()
    {
        _httpClient=HttpClients.createDefault();
        _requestConfig=RequestConfig.custom().setSocketTimeout(10*1000).setConnectTimeout(10*1000).setConnectionRequestTimeout(10*1000).build();
    }
    //Clean the Request If Reqeust header null then not execute
    public void ClearHeader()
    {
        if(_requestHeader.size()!=0)
            _requestHeader.clear();
    }

    public void AddRequestHeader(String header, String value)
    {
        if(_requestHeader.get(header)!=null)
            _requestHeader.replace(header, value);
        else
            _requestHeader.put(header,value);
    }

    //Get
    public void Send(String url)
    {
        _httpGet=new HttpGet(url);

        for(String key : _requestHeader.keySet())
            _httpGet.addHeader(key, _requestHeader.get(key));


    }

    //Post
    public void Send(String url, String postData)
    {

    }

    public void Dispose() throws IOException
    {
        _httpClient.close();
    }

}
