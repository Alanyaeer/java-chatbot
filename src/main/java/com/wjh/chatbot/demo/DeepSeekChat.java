package com.wjh.chatbot.demo;

import com.google.gson.Gson;
import com.wjh.chatbot.entity.ChatRequest;
import com.wjh.chatbot.entity.Message;
import com.wjh.chatbot.entity.enums.Role;
import com.wjh.chatbot.properties.DeepSeekProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.mongodb.client.*;
import org.bson.Document;

import java.io.FileReader;
import java.io.IOException;

import javax.annotation.Resource;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class DeepSeekChat implements CommandLineRunner {
    private final Gson gson = new Gson();
    @Resource
    private DeepSeekProperties deepSeekProperties;
    public void doChat() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        Message messageSystem = Message.builder().role(Role.SYSTEM.getValue()).content("你是一个香香软软的小蛋糕").build();
        Message messageUser = Message.builder().role(Role.USER.getValue()).content("你是谁呀").build();
        List<Message> messageList = Arrays.asList(messageSystem, messageUser);
        String apiKey = deepSeekProperties.getApiKey();
        log.info("apiKey: {}", apiKey);

        ChatRequest chatRequest = ChatRequest.builder().messages(messageList).model("deepseek-chat").stream(true).build();
        String json = gson.toJson(chatRequest);
        log.info(json);
        RequestBody body = RequestBody.create(mediaType, json);
        String baseUrl = deepSeekProperties.getHost();
        DefaultUriBuilderFactory dsUri = new DefaultUriBuilderFactory(baseUrl);
        URI uri = dsUri.uriString("/chat/{type}")
                .build("completions");
        try {
            Request request = new Request.Builder()
                    .url(uri.toURL())
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .build();
            try(Response response = client.newCall(request).execute()){
                if (response.body() != null) {
                    log.info(response.body().string());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void doFimChat(){
        String apiKey = deepSeekProperties.getApiKey();
        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(5, TimeUnit.MINUTES)
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n  \"model\": \"deepseek-chat\",\n  \"prompt\": \"<FIMPREFIX>Once upon a Time there had a isolate island <FIMEND>\",\n  \"echo\": false,\n  \"frequency_penalty\": 0,\n  \"logprobs\": 0,\n  \"max_tokens\": 128,\n  \"presence_penalty\": 0,\n  \"stop\": null,\n  \"stream\": false,\n  \"stream_options\": null,\n  \"suffix\": null,\n  \"temperature\": 1,\n  \"top_p\": 1\n}");
        Request request = new Request.Builder()
                .url("https://api.deepseek.com/beta/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();
        log.info("apiKey: {}", apiKey);
        try {
            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    log.info(response.body().string());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void run(String... args) throws Exception {
        String[] csvFiles = {
                "studentManagement.classes.csv",
                "studentManagement.students.csv",
                "studentManagement.teachers.csv",
                "studentManagement.scores.csv",
                "studentManagement.courses.csv"
        };

        for (String csvFile : csvFiles) {
            String dbName = "studentManagement";
            String collectionName = csvFile.substring(
                    csvFile.indexOf(".") + 1,
                    csvFile.lastIndexOf(".")
            );

            try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
                 CSVReader csvReader = new CSVReader(new FileReader(csvFile))) {

                MongoDatabase database = mongoClient.getDatabase(dbName);
                MongoCollection<Document> collection = database.getCollection(collectionName);

                // 清空集合，避免重复导入
                collection.deleteMany(new Document());

                String[] headers = csvReader.readNext(); // 第一行作为表头
                String[] line;
                int count = 0;

                while ((line = csvReader.readNext()) != null) {
                    Document doc = new Document();

                    // 根据不同集合进行不同的字段处理
                    switch (collectionName) {
                        case "teachers":
                            processTeachersDocument(doc, headers, line);
                            break;
                        case "classes":
                            processClassesDocument(doc, headers, line);
                            break;
                        case "courses":
                            processCoursesDocument(doc, headers, line);
                            break;
                        case "students":
                            processStudentsDocument(doc, headers, line);
                            break;
                        case "scores":
                            processScoresDocument(doc, headers, line);
                            break;
                        default:
                            // 默认处理，不进行类型转换
                            for (int i = 0; i < headers.length; i++) {
                                doc.append(headers[i], line[i]);
                            }
                    }

                    collection.insertOne(doc);
                    count++;
                }

                System.out.println("成功导入 " + count + " 条数据到 " + collectionName + " 集合！");
            } catch (IOException | CsvValidationException e) {
                e.printStackTrace();
            }
        }
    }

    // 处理教师表数据
    private void processTeachersDocument(Document doc, String[] headers, String[] line) {
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            String value = line[i];

            // 处理联系信息嵌套字段
            if (header.startsWith("contact.")) {
                String subField = header.substring("contact.".length());
                Document contact = doc.get("contact", Document.class);
                if (contact == null) {
                    contact = new Document();
                    doc.append("contact", contact);
                }
                contact.append(subField, value);
            }
            // 处理布尔类型字段
            else if ("isHeadTeacher".equals(header)) {
                doc.append(header, "true".equalsIgnoreCase(value) || "1".equals(value));
            }
            // 其他字段直接添加
            else {
                doc.append(header, value);
            }
        }
    }

    // 处理班级表数据
    private void processClassesDocument(Document doc, String[] headers, String[] line) {
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            String value = line[i];

            // 处理数字类型字段
            if ("grade".equals(header) || "studentCount".equals(header)) {
                try {
                    doc.append(header, Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    doc.append(header, value); // 转换失败则保留原始值
                }
            }
            // 其他字段直接添加
            else {
                doc.append(header, value);
            }
        }
    }

    // 处理课程表数据
    private void processCoursesDocument(Document doc, String[] headers, String[] line) {
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            String value = line[i];

            // 处理数字类型字段
            if ("credit".equals(header)) {
                try {
                    doc.append(header, Double.parseDouble(value));
                } catch (NumberFormatException e) {
                    doc.append(header, value);
                }
            }
            // 其他字段直接添加
            else {
                doc.append(header, value);
            }
        }
    }

    // 处理学生表数据
    private void processStudentsDocument(Document doc, String[] headers, String[] line) {
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            String value = line[i];

            // 处理日期类型字段
            if ("birthDate".equals(header) || "enrollmentDate".equals(header)) {
                try {
                    doc.append(header, new Date(value));
                } catch (IllegalArgumentException e) {
                    // 尝试其他日期格式
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        doc.append(header, sdf.parse(value));
                    } catch (ParseException e2) {
                        doc.append(header, value); // 转换失败则保留原始值
                    }
                }
            }
            // 处理联系信息嵌套字段
            else if (header.startsWith("contact.")) {
                String subField = header.substring("contact.".length());
                Document contact = doc.get("contact", Document.class);
                if (contact == null) {
                    contact = new Document();
                    doc.append("contact", contact);
                }
                contact.append(subField, value);
            }
            // 处理个人资料嵌套字段
            else if (header.startsWith("profile.")) {
                String subField = header.substring("profile.".length());
                Document profile = doc.get("profile", Document.class);
                if (profile == null) {
                    profile = new Document();
                    doc.append("profile", profile);
                }

                // 身高和体重是数字类型
                if ("height".equals(subField) || "weight".equals(subField)) {
                    try {
                        profile.append(subField, Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        profile.append(subField, value);
                    }
                } else {
                    profile.append(subField, value);
                }
            }
            // 其他字段直接添加
            else {
                doc.append(header, value);
            }
        }
    }

    // 处理成绩表数据
    private void processScoresDocument(Document doc, String[] headers, String[] line) {
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            String value = line[i];

            // 处理日期类型字段
            if ("examDate".equals(header)) {
                try {
                    doc.append(header, new Date(value));
                } catch (IllegalArgumentException e) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        doc.append(header, sdf.parse(value));
                    } catch (ParseException e2) {
                        doc.append(header, value);
                    }
                }
            }
            // 处理数字类型字段
            else if ("score".equals(header) || "usualScore".equals(header) || "finalScore".equals(header)) {
                try {
                    doc.append(header, Double.parseDouble(value));
                } catch (NumberFormatException e) {
                    doc.append(header, value);
                }
            }
            // 其他字段直接添加
            else {
                doc.append(header, value);
            }
        }
    }

}
