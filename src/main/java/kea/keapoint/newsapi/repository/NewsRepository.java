package kea.keapoint.newsapi.repository;

import kea.keapoint.newsapi.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NewsRepository extends JpaRepository<News, UUID> {

    @Query(value = "SELECT * FROM news n WHERE DATE(n.created_at) = CURDATE()", nativeQuery = true)
    List<News> findNewsByToday();
}
