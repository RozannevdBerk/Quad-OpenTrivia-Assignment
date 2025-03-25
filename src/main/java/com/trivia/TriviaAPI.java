package com.trivia;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/")
@SpringBootApplication
public class TriviaAPI {
    private String TriviaJson;

    //Retrieves one trivia question from the OpenTrivia API
    static String GetTriviaJson() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://opentdb.com/api.php?amount=1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request,
        HttpResponse.BodyHandlers.ofString());

        Integer responseCode = response.statusCode();

        //checks for the correct response and retries GET command if it went wrong
        while (responseCode.intValue()!=200) {
            request = HttpRequest.newBuilder()
                    .uri(URI.create("https://opentdb.com/api.php?amount=1"))
                    .GET() // GET is default
                    .build();

            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            responseCode = response.statusCode();
        }

        return response.body();
    }

    //Extracts the question as a string from the json format
    static String GetTriviaQuestion(String rawJson) {
        Pattern questionPattern = Pattern.compile("\"question\":\"(.+?)\"");
        Matcher questionMatcher = questionPattern.matcher(rawJson);
        String question;
        if (questionMatcher.find()) {
            question = questionMatcher.group(1);
        } else {
            question = "Question not Found";
        }
        return question;
    }

    //retrieves correct answer as a string from the json format
    static String GetCorrectAnswer(String rawJson) {
        Pattern answerPattern = Pattern.compile("\"correct_answer\":\"(.+?)\"");
        Matcher answerMatcher = answerPattern.matcher(rawJson);
        String answer;
        if (answerMatcher.find()) {
            answer = answerMatcher.group(1);
        } else {
            answer = "Answer not Found";
        }
        return answer;
    }
	
    //retrieves a list of strings containing all possible answers, including both correct and incorrect ones
    static List<String> GetAllAnswers(String rawJson, String correctAnswer) {
        Pattern p = Pattern.compile("\"incorrect_answers\":\\[\"(.+)\"\\]");
        Matcher m = p.matcher(rawJson);
        String IncAnswers;
        if (m.find()) {
            IncAnswers = m.group(1);
        } else {
            IncAnswers = "Incorrect Answers not Found";
        }
        String[] IncAnswersArr = IncAnswers.split("\",\"");
        List<String> AnswerList = new ArrayList<String>();
        Collections.addAll(AnswerList, IncAnswersArr);
        AnswerList.add(correctAnswer);
        Collections.shuffle(AnswerList);
        return AnswerList;
    }

    //returns a list of strings, the last string in the list is the question, remainder are possible answers
    @GetMapping("/questions")
    public List<String> getTriviaQuestion() throws IOException, InterruptedException {
        TriviaJson = GetTriviaJson();
        String question = GetTriviaQuestion(TriviaJson);
        String correctAnswer = GetCorrectAnswer(TriviaJson);
        List<String> allAnswers = GetAllAnswers(TriviaJson, correctAnswer);
        allAnswers.add(question);
        return allAnswers;
    }

    //Takes an answers as input and compares it to the correct answer
    @PostMapping("/checkanswers")
    public String CheckAnswer(@RequestBody String answer) {
        String correctAnswer = GetCorrectAnswer(TriviaJson);
        if (answer.equals(correctAnswer)) {
            return "Correct";
        } else {
            return "Incorrect";
        }
    }

	public static void main(String[] args) {
		SpringApplication.run(TriviaAPI.class, args);
	}

}