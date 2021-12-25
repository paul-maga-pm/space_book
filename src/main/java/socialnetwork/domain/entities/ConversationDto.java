package socialnetwork.domain.entities;

import java.util.List;
import java.util.Objects;

public class ConversationDto extends Entity<Long> {
    private List<User> participants;
    private List<MessageDto> messages;
    private String name;
    private String description;


    public ConversationDto(Long id,
                           String name,
                           String description,
                           List<User> participants,
                           List<MessageDto> messages) {
        super(id);
        this.participants = participants;
        this.messages = messages;
        this.name = name;
        this.description = description;
    }

    public ConversationDto(Long id,
                        String name,
                        String description){
        super(id);
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public void setMessages(List<MessageDto> messages) {
        this.messages = messages;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public List<MessageDto> getMessages() {
        return messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConversationDto)) return false;
        ConversationDto that = (ConversationDto) o;
        return Objects.equals(participants, that.participants) &&
                Objects.equals(messages, that.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participants, messages);
    }

    public void addMessage(MessageDto message) {
        this.messages.add(message);
    }
}
