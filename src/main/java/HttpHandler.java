import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;


public class HttpHandler {
    CloseableHttpClient _httpClient=null;
    HttpGet _httpGet=null;
    HttpPost _httpPost=null;
    RequestConfig _requestConfig=null;
    String responseText=null;
    CredentialsProvider _credsProvider=null;
    SSLContext _sslContext=null;
    SSLConnectionSocketFactory _sslsf=null;

    public String getResponseText() {
        return responseText;
    }
    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    HashMap<String, String> _requestHeader=new HashMap<String, String>();

    @SuppressWarnings("deprecation")
    public HttpHandler()
    {

        _credsProvider=new BasicCredentialsProvider();
        _sslContext= SSLContexts.createDefault();
        _sslsf=new SSLConnectionSocketFactory(_sslContext,
                new String[] {"TLSv1.2"},
                null,
                new NoopHostnameVerifier());

        _httpClient=HttpClients
                .custom()
                .setDefaultCredentialsProvider(_credsProvider)
                .setSSLSocketFactory(_sslsf)
                .build();
        _requestConfig=RequestConfig.custom().setSocketTimeout(10*1000).setConnectTimeout(10*1000).setConnectionRequestTimeout(10*1000).build();
    }
    //Clean the Request If Reqeust header null then not execute
    public void ClearHeader()
    {
        if(_requestHeader.size()!=0)
            _requestHeader.clear();
    }

    //Using Hashmap Add header, value in hashmap data.
    public void AddRequestHeader(String header, String value)
    {
        if(_requestHeader.get(header)!=null)
            _requestHeader.replace(header, value);
        else
            _requestHeader.put(header,value);
    }

    //Get
    public void Send(String url) throws IOException {

        int timeout=10000;

        BufferedReader bufferReader=null;
        _httpGet=new HttpGet(url);
        _httpGet.setConfig(_requestConfig);

        for(String key : _requestHeader.keySet())
            _httpGet.addHeader(key, _requestHeader.get(key));
        CloseableHttpResponse response=_httpClient.execute(_httpGet);

        System.out.println(":::Get RESPONSE:::");

        Header[] headers=response.getHeaders("Set-Cooke");
        if(response.getStatusLine().getStatusCode()==200)
        {
            bufferReader=new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            String inputLine;
            StringBuffer responseBuffer=new StringBuffer();

            while((inputLine=bufferReader.readLine())!=null)
            {
                responseBuffer.append(inputLine);
            }

            bufferReader.close();

            responseText=responseBuffer.toString();
        }

    }

    //Post
    public void Send(String url, String postData) throws IOException {
        BufferedReader bufferReader=null;
        _httpPost=new HttpPost(url);
        _httpPost.setConfig(_requestConfig);

        StringEntity entity=new StringEntity(postData);
        _httpPost.setEntity(entity);

        for(String key : _requestHeader.keySet())
            _httpPost.setHeader(key, _requestHeader.get(key));

        CloseableHttpResponse response = _httpClient.execute(_httpPost);

        System.out.println(":::Post RESPONSE:::");




        if(response.getStatusLine().getStatusCode()==200)
        {
            bufferReader=new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            String inputLine;
            StringBuffer responseBuffer=new StringBuffer();

            while((inputLine=bufferReader.readLine())!=null)
            {
                responseBuffer.append(inputLine);
            }

            bufferReader.close();

            responseText=responseBuffer.toString();
        }

    }

    public void Dispose() throws IOException
    {
        _httpClient.close();
        if(_httpPost!=null)
            _httpPost=null;
        if(_httpGet!=null)
            _httpGet=null;
    }

}
