package kea.keapoint.newsapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kea.keapoint.newsapi.dto.Article;
import kea.keapoint.newsapi.dto.NewsApiResponse;
import kea.keapoint.newsapi.entity.News;
import kea.keapoint.newsapi.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
public class NewsService {

    private final NewsRepository newsRepository;

    private static final String API_KEY = "0aab704ca0444158bbf24e09a74b9918";
    private static final String NEWS_API_URL = "https://newsapi.org/v2/everything?q=bitcoin&apiKey=" + API_KEY;

    @Transactional
    @Scheduled(fixedRate = 300000)
    public Object collectNews() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(NEWS_API_URL)
                .build();

        // API 요청
        try (Response response = client.newCall(request).execute()) {
            log.info(response.toString()); // 요청 결과 로그 찍기

            if (response.isSuccessful() && response.body() != null) { // 성공적으로 응답을 받은 경우
                // 객체 추출
                String responseBody = response.body().string();
                ObjectMapper objectMapper = new ObjectMapper();
                NewsApiResponse newsResponse = objectMapper.readValue(responseBody, NewsApiResponse.class);

                // 수집한 news를 DB에 저장
                for (Article article : newsResponse.getArticles()) {
                    News news = News.builder()
                            .sourceName(article.getSource().getName())
                            .author(article.getAuthor())
                            .title(article.getTitle())
                            .description(article.getDescription())
                            .url(article.getUrl())
                            .urlToImage(article.getUrlToImage())
                            .content(article.getContent())
                            .build();

                    newsRepository.save(news);
                }

            } else { // 올바른 응답을 받는데 실패한 경우
                throw new IOException("Unexpected response code: " + response.code());
            }

        } catch (Exception e) { // 예외가 터진 경우
            log.error(e.getMessage());
        }

        return null;
    }

}
