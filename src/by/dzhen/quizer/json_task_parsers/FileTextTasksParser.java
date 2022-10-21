package by.dzhen.quizer.json_task_parsers;

import by.dzhen.quizer.tasks.Task;
import by.dzhen.quizer.tasks.TextTask;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileTextTasksParser implements TaskParser {
    private String filename;

    public FileTextTasksParser(String filename) {
        this.filename = filename;
    }

    @Override
    public List<Task> parse() {
        String text = readAllFromFile();
        JSONArray tasksJsonArray = new JSONArray(text);

        List<Task> result = new ArrayList<>();
        for (int i = 0; i < tasksJsonArray.length(); i++) {
            JSONObject jsonTask = tasksJsonArray.getJSONObject(i);
            result.add(parseTask(jsonTask));
        }

        return result;
    }

    private TextTask parseTask(JSONObject jsonTask) {
        String text = jsonTask.getString("text");
        String answer = jsonTask.getString("answer");
        TextTask result = new TextTask(text, answer);

        if (jsonTask.has("ignore_case")) {
            result.setIgnoreCase(jsonTask.getBoolean("ignore_case"));
        }

        return result;
    }

    private String readAllFromFile() {
        try {
            return Files.readString(Paths.get(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
