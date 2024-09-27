package fastcampus.mongodb.service;

import com.mongodb.client.model.changestream.OperationType;
import fastcampus.mongodb.entity.ChatDocument;
import fastcampus.mongodb.handler.Chat;
import fastcampus.mongodb.repository.ChatMongoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonValue;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ChatMongoRepository chatMongoRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    private static Map<String, Sinks.Many<Chat>> chatSinkMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {

		reactiveMongoTemplate.changeStream(ChatDocument.class)
				.listen()
				.doOnNext(
						item -> {
							ChatDocument target = item.getBody();

							OperationType operationType = item.getOperationType();

                            if(target != null && operationType == OperationType.INSERT) {
                                String from = target.getFrom();
                                String to = target.getTo();
                                String message = target.getMessage();

                                doSend(from, to, message);
                            }
						}
				).subscribe();
    }

    public Flux<Chat> register(String iam) {
        Sinks.Many<Chat> sink = Sinks.many().unicast().onBackpressureBuffer();

        chatSinkMap.put(iam, sink);

        chatMongoRepository.findAllByTo(iam)
                .doOnNext(chat -> {
                    sink.tryEmitNext(new Chat(chat.getMessage(), chat.getTo()));
                })
                .subscribe();


        return sink.asFlux();
    }

    public void sendChat(String from, String to, String message) {
        log.info("from: {}, to: {}, message: {}", from, to, message);
        ChatDocument chat = new ChatDocument(from, to, message);
        chatMongoRepository.save(chat)
                .subscribe();
    }

    public void doSend(String from, String to, String message) {
        Sinks.Many<Chat> sink = chatSinkMap.get(to);
        if (sink == null) {
            Sinks.Many<Chat> my = chatSinkMap.get(from);
            my.tryEmitNext(new Chat("대화 상대가 없습니다.", "system"));
            return;
        }
        sink.tryEmitNext(new Chat(message, from));
    }
}
