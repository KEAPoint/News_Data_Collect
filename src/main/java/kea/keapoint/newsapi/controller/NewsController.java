package kea.keapoint.newsapi.controller;

import com.google.gson.JsonArray;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kea.keapoint.newsapi.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/word-count")
    public String getTodayNewsWordCount(Model model) {
        JsonArray wordCounts = newsService.getTodayNewsWordCount();
        model.addAttribute("wordCounts", wordCounts.toString());
        return "wordcloud";
    }

    @GetMapping(value = "/getData")
    public void sendData(HttpServletResponse res, HttpServletRequest req) throws IOException {
        JsonArray wordCounts = newsService.getTodayNewsWordCount();

        // 전달하는 값의 타입을 application/json;charset=utf-8 로 하고(한글을 정상적으로 출력하기 위함)
        // printwriter 과 prrint 를 사용하여 값을 response 로 값을 전달함
        // 이때 toJSONString 로 전당하는데 이는 추후 Jsonparsing 을 원활하게 하기 위해서
        // pw 로 값을 전달하면 값이 response body 에 들어가서 보내짐
        res.setContentType("application/json;charset=utf-8");
        PrintWriter pw = res.getWriter();
        pw.print(wordCounts.toString());
    }
}
