package beat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import config.GlobalVariables;

public class BeatDownloadManager {
    private static final int MAX_THREADS = GlobalVariables.BeatmapDownloadThreads;
    private ExecutorService executor;

    public BeatDownloadManager() {
        this.executor = Executors.newFixedThreadPool(MAX_THREADS);
    }

    public void downloadMap(int ID, String location, Runnable callback) {
        String url = GlobalVariables.BeatmapMirrorLink + ID;
        HttpGet request = new HttpGet(URI.create(url));
        request.setHeader("referer", "https://osu.ppy.sh/beatmapsets/" + ID);

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.disableCookieManagement();

        executor.execute(() -> {
            try (CloseableHttpClient client = clientBuilder.build();
                 CloseableHttpResponse response = client.execute(request)) {

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try (FileOutputStream output = new FileOutputStream(location)) {
                        InputStream input = entity.getContent();
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = input.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                        input.close();
                        output.flush();
                    }
                }
                EntityUtils.consume(entity);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (callback != null) {
                    callback.run();
                }
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}