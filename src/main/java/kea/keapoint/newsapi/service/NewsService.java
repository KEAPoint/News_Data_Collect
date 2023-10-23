package kea.keapoint.newsapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.transaction.Transactional;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
public class NewsService {

    private final NewsRepository newsRepository;

    private static final String API_KEY = "0aab704ca0444158bbf24e09a74b9918";
    private static final String NEWS_API_URL = "https://newsapi.org/v2/everything?domains=forbes.com,bbc.com,cnn.com&language=en&apiKey=" + API_KEY;

    @Transactional
    @Scheduled(fixedRate = 1500000)
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

    public JsonArray getTodayNewsWordCount() {
        List<String> titleList = newsRepository.findNewsByToday()
                .stream()
                .map(News::getTitle)
                .toList();

        log.info(titleList.toString());

        // word press에 포함하지 않을 단어
        Set<String> stopWords = new HashSet<>(Arrays.asList(".", ",", "!", "their", "a", "other", "so", "that", "being", "between", "ours", "yourselves", "what", "if", "each", "haven", "you", "they", "not", "because", "against", "mustn't", "them", "over", "you're", "are", "won't", "its", "off", "am", "with", "there", "needn't", "hadn't", "about", "nor", "just", "by", "should", "needn", "shan", "until", "that'll", "more", "you'll", "you've", "will", "mightn", "you'd", "up", "yours", "through", "hasn't", "ma", "my", "such", "itself", "under", "too", "which", "during", "was", "than", "an", "been", "down", "these", "should've", "how", "herself", "the", "here", "to", "having", "don", "t", "theirs", "does", "on", "both", "wouldn't", "don't", "only", "who", "when", "were", "into", "any", "shouldn", "and", "why", "couldn't", "haven't", "or", "where", "she", "me", "him", "at", "below", "his", "her", "then", "did", "this", "he", "it's", "few", "most", "can", "mustn", "very", "s", "himself", "again", "ll", "yourself", "further", "doing", "some", "but", "i", "couldn", "aren't", "it", "now", "ourselves", "re", "have", "after", "our", "your", "out", "wasn't", "mightn't", "themselves", "whom", "once", "is", "hadn", "y", "doesn't", "weren", "we", "before", "as", "o", "own", "above", "wouldn", "of", "all", "shan't", "be", "weren't", "while", "d", "from", "aren", "isn", "won", "didn", "no", "hasn", "those", "didn't", "isn't", "she's", "ain", "same", "shouldn't", "do", "m", "for", "hers", "had", "doesn", "myself", "in", "wasn", "has", "ve"));

        // news title에서 단어 단위로 쪼개 어떤 단어가 몇번 사용되었는지 추출
        Map<String, Long> wordFrequency = titleList.stream()
                .flatMap(title -> Arrays.stream(title.toLowerCase().split("\\W+")))
                .filter(word -> !stopWords.contains(word))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        log.info(wordFrequency.toString());

        // AntChart's WordCloud에 맞는 json으로 변환
        JsonArray jsonArray = new JsonArray();
        wordFrequency.forEach((key, value) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("x", key);
            jsonObject.addProperty("value", value);
            jsonArray.add(jsonObject);
        });
        log.info(jsonArray.toString());

        return jsonArray;
    }

}
