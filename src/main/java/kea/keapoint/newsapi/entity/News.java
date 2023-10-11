package kea.keapoint.newsapi.entity;

import javax.persistence.*;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "news")
@NoArgsConstructor
public class News {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "news_idx", nullable = false)
    private UUID newsIdx;

    @Column(name = "source_name")
    private String sourceName; // 출처

    private String author; // 작성자

    private String title; // 제목

    @Column(columnDefinition = "TEXT")
    private String description; // 설명

    private String url; // url

    @Column(name = "url_to_image")
    private String urlToImage; // 이미지url

    private String content; // 내용

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;// 수집시간

    @Builder
    public News(String sourceName, String author, String title, String description, String url, String urlToImage, String content) {
        this.sourceName = sourceName;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.content = content;
    }

}
