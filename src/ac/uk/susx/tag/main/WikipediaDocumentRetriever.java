package ac.uk.susx.tag.main;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class WikipediaDocumentRetriever {
	private CloseableHttpClient httpclient;
	
	public WikipediaDocumentRetriever(){
		httpclient = HttpClients.createDefault();
	}
	
	public String getDoc(String uri) throws Exception{
        try {
            HttpGet httpget = new HttpGet(uri);

            System.out.println("executing request " + httpget.getURI());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");
            if(responseBody.contains("#REDIRECT")){
            	System.err.println(responseBody);
            	final String regPatt = "\\#REDIRECT \\[\\[[\\w\\s]*\\]\\]";
            	Pattern pat = Pattern.compile(regPatt);
            	System.err.println(pat.toString());
            	Matcher mat = pat.matcher(responseBody);
            	mat.find();
            	String page = responseBody.substring((mat.start()+12), (mat.end()-2)).replace(" ", "_");
            	return getDoc(URLBuilder(page));
            }
            System.out.println(responseBody);
            System.out.println("----------------------------------------");
            return responseBody;

        } finally {
            //httpclient.close();
        }
	}
	
	public void closeHTTP() throws IOException{
		httpclient.close();
	}
	
	public String URLBuilder(String page){
		return "http://en.wikipedia.org/w/api.php?format=json&action=query&prop=revisions&rvprop=content&titles=" + page;
	}

}
