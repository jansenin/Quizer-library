package by.dzhen.quizer.task_generators;

import by.dzhen.quizer.tasks.Task;
import by.dzhen.quizer.tasks.TrueFalseTask;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Stack;

public class CSEasyQuestionsTaskGenerator implements Task.Generator {
    private Stack<Task> tasksPool;

    public CSEasyQuestionsTaskGenerator() {
        this.tasksPool = new Stack<>();
    }

    @Override
    public Task generate() {
        if (tasksPool.empty()) {
            JSONObject json = new JSONObject(requestJsonString());
            JSONArray jsonTasks = json.getJSONArray("results");

            for (int i = 0; i < jsonTasks.length(); i++) {
                Task task = JsonObjectToTask(jsonTasks.getJSONObject(i));
                tasksPool.push(task);
            }
        }

        return tasksPool.pop();
    }

    private Task JsonObjectToTask(JSONObject object) {
        String text = object.getString("question");
        boolean answer = object.getBoolean("correct_answer");
        return new TrueFalseTask(text, answer);
    }

    private String requestJsonString() {
        try {
            URI uri = new URI("https://opentdb.com/api.php?amount=10&category=18&difficulty=easy&type=boolean");
            HttpRequest request = HttpRequest.newBuilder(uri).build();
            HttpClient client = HttpClient.newHttpClient();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
