package net.teamfruit.emojicord;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;

import com.esotericsoftware.yamlbeans.YamlReader;

public class EmojiEndpoint {
	public static final String EMOJI_GATEWAY = "https://raw.githubusercontent.com/Team-Fruit/Emojicord/api/api.yml";
	public static List<String> EMOJI_STANDARD;
	public static List<String> EMOJI_WEB_ENDPOINT;

	private static class EmojiGateway {
		public List<String> emojis;
		public List<String> api;
	}

	private static YamlReader getYaml(final String url)
			throws IllegalArgumentException, IllegalStateException, IOException {
		final HttpUriRequest req = new HttpGet(url);
		final HttpClientContext context = HttpClientContext.create();
		final HttpResponse response = Downloader.downloader.client.execute(req, context);
		final HttpEntity entity = response.getEntity();

		final int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK)
			throw new HttpResponseException(statusCode, "Invalid status code: " + url);

		return new YamlReader(
				new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8));
	}

	public static void loadGateway() {
		YamlReader reader = null;
		try {
			reader = getYaml(EMOJI_GATEWAY);
			final EmojiGateway gatewayConfig = reader.read(EmojiGateway.class);
			EMOJI_STANDARD = gatewayConfig.emojis;
			EMOJI_WEB_ENDPOINT = gatewayConfig.api;
		} catch (final IllegalArgumentException | IllegalStateException | IOException e) {
			Log.log.warn("Failed to load Emojicord API: ", e);
		} finally {
			if (reader != null)
				IOUtils.closeQuietly(reader::close);
		}
	}
}