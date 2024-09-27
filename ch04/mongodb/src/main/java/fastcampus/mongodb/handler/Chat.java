package fastcampus.mongodb.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Chat{
    private final String message;
    private final String from;
}