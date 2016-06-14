package br.com.ecodif.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import br.com.ecodif.domain.Device;

@Path("/api/qodisco")
public class QoDiscoResource {

	private String serverUrl = "localhost:8080";



	public String requestGET(String url) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet getRequest = new HttpGet(url);
			getRequest.addHeader("accept", "application/json");

			HttpResponse response = httpClient.execute(getRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));

			StringBuilder sb = new StringBuilder();
			String output;
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}

			httpClient.getConnectionManager().shutdown();
			return sb.toString();

		} catch (ClientProtocolException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	@GET
	@PermitAll
	@Path("/devices")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response syncSearch() {
		String url = "http://localhost:8080/EcodifAPI/api/device.json";
		this.requestGET(url);
		List<Device> list = new ArrayList<Device>();
		try {
			String response = this.requestGET(url);
			JSONArray jsonArr = new JSONArray(response);
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject jsonObj;
				jsonObj = jsonArr.getJSONObject(i);
				ObjectMapper mapper = new ObjectMapper();
				Device device;
				device = mapper.readValue(jsonObj.toString(), Device.class);
				list.add(device);
			}

			GenericEntity<List<Device>> entity = new GenericEntity<List<Device>>(
					list) {
			};

			return Response.ok(entity).build();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(404).build();
	}

	// @GET
	// @PermitAll
	// @Path("/async-search")
	// @Produces({ MediaType.APPLICATION_JSON })
	// public Response syncSearc(@QueryParam("domain") String domain,
	// @QueryParam("query") String query, @QueryParam("type") String type) {
	// String url = "/qodisco/api/asyncsearch?" + domain + "&" + query + "&"
	// + type;
	// try {
	// String response = this.requestGET(url);
	// return Response.ok(response).build();
	// } catch (URISyntaxException | IOException e) {
	// e.printStackTrace();
	// return Response.serverError().build();
	// }
	//
	// }
	//
	// @GET
	// @PermitAll
	// @Path("/sync-search")
	// @Produces({ MediaType.APPLICATION_JSON })
	// public Response syncSearc(@QueryParam("domain") String domain,
	// @QueryParam("query") String query) {
	// String url = "/qodisco/api/sync-search?" + domain + "&" + query;
	// try {
	// String response = this.requestGET(url);
	// return Response.ok(response).build();
	// } catch (URISyntaxException | IOException e) {
	// e.printStackTrace();
	// return Response.serverError().build();
	// }
	//
	// }

}
