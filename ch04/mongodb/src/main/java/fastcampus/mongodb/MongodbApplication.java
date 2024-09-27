package fastcampus.mongodb;

import com.mongodb.client.model.changestream.OperationType;
import fastcampus.mongodb.entity.ChatDocument;
import fastcampus.mongodb.handler.Chat;
import fastcampus.mongodb.repository.ChatMongoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@SpringBootApplication
@Slf4j
public class MongodbApplication implements CommandLineRunner {

	@Autowired
	private ChatMongoRepository chatMongoRepository;

	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	@Override
	public void run(String... args) throws Exception {
//
//		reactiveMongoTemplate.changeStream(ChatDocument.class)
//				.listen()
//				.doOnNext(
//						item -> {
//							ChatDocument target = item.getBody();
//
//							OperationType operationType = item.getOperationType();
//
//							BsonValue token = item.getResumeToken();
//
//							log.info("target: {}, operationType: {}, token: {}", target, operationType, token);
//						}
//				).subscribe();
//
//		Thread.sleep(1000);
//
//		var newChat = new ChatDocument("a", "b", "HELLO!!");
//
//		chatMongoRepository.insert(newChat)
//				.doOnNext(chat -> log.info("saved document: {}", chat))
//				.subscribe();
	}

	public static void main(String[] args) {
		SpringApplication.run(MongodbApplication.class, args);
	}

}
