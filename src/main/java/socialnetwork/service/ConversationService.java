package socialnetwork.service;

import socialnetwork.domain.entities.*;
import socialnetwork.domain.validators.EntityValidator;
import socialnetwork.repository.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConversationService {
    private final EntityValidator<ConversationParticipationId, ConversationParticipation> participationValidator;
    private Repository<Long, User> userRepository;
    private Repository<Long, Conversation> conversationRepository;
    private Repository<ConversationParticipationId, ConversationParticipation> conversationParticipationRepository;
    private Repository<Long, Message> messageRepository;
    private EntityValidator<Long, Message> messageValidator;
    private EntityValidator<Long, Conversation> conversationValidator;

    public ConversationService(Repository<Long, User> userRepository,
                               Repository<Long, Conversation> conversationRepository,
                               Repository<ConversationParticipationId, ConversationParticipation>
                                       conversationParticipationRepository,
                               Repository<Long, Message> messageRepository,
                               EntityValidator<Long, Message> messageValidator,
                               EntityValidator<Long, Conversation> conversationValidator,
                               EntityValidator<ConversationParticipationId, ConversationParticipation> participationValidator) {
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
        this.conversationParticipationRepository = conversationParticipationRepository;
        this.messageRepository = messageRepository;
        this.messageValidator = messageValidator;
        this.conversationValidator = conversationValidator;
        this.participationValidator = participationValidator;
    }

    public ConversationDto createConversation(String name, String description, List<Long> participantsIdList){
        Long conversationId = findAvailableIdForConversation();
        Conversation conversation = new Conversation(conversationId, name, description);

        conversationValidator.validate(conversation);
        conversationRepository.save(conversation);

        for(var participantId : participantsIdList){
            ConversationParticipation participation = new ConversationParticipation(participantId, conversationId);
            participationValidator.validate(participation);
            conversationParticipationRepository.save(participation);
        }

        List<User> conversationParticipants = getConversationParticipants(conversationId);

        return new ConversationDto(conversationId,
                                name,
                                description,
                                conversationParticipants,
                                new ArrayList<>());
    }

    public void sendMessageInConversation(Long conversationId, Long senderId, String text, LocalDateTime date){
        Long id = findAvailableIdForMessage();
        Message message = new Message(id, conversationId, senderId, text, date);
        participationValidator.validate(new ConversationParticipation(senderId, conversationId));
        messageValidator.validate(message);
        messageRepository.save(message);
    }

    public List<ConversationDto> getConversationsOfUser(Long userId){
        List<Long> userConversationsIdList = conversationParticipationRepository.getAll().stream()
                .filter(convo -> convo.getParticipantId().equals(userId))
                .map(convo -> convo.getConversationId())
                .collect(Collectors.toList());

        List<ConversationDto> conversationDtoList = new ArrayList<>();
        for(var conversationId : userConversationsIdList){
            ConversationDto conversationDto = getConversationDtoWithId(conversationId);
            conversationDtoList.add(conversationDto);
        }
        return conversationDtoList;
    }

    public List<Message> getMessagesReceivedByUserSentInMonth(Long receiverId, int month){
        List<Long> userConversationsIdList = conversationParticipationRepository.getAll().stream()
                .filter(convo -> convo.getParticipantId().equals(receiverId))
                .map(convo -> convo.getConversationId())
                .collect(Collectors.toList());

        List<Message> userMessages = new ArrayList<>();
        for(var conversationId : userConversationsIdList)
            userMessages.addAll(getConversationMessagesSentInMonth(conversationId, month));

        return userMessages;
    }

    public List<Message> getMessagesReceivedByUserInYearAndMonth(Long receiverId, int year , int month){
        List<Long> userConversationsIdList = conversationParticipationRepository.getAll().stream()
                .filter(convo -> convo.getParticipantId().equals(receiverId))
                .map(convo -> convo.getConversationId())
                .collect(Collectors.toList());

        List<Message> userMessages = new ArrayList<>();
        for(var conversationId : userConversationsIdList) {
            var messages = getConversationMessagesSentInYearAndMonth(conversationId, year, month).stream()
                            .filter(m -> m.getSenderId() != receiverId)
                            .collect(Collectors.toList());
            userMessages.addAll(messages);
        }
        return userMessages;
    }

    public List<Message> getMessagesReceivedByUserSentByOtherUserInYearAndMonth(Long receiverId,
                                                                                Long senderId,
                                                                                int year,
                                                                                int month) {
        Predicate<Message> messageWasSentBy = m -> m.getSenderId().equals(senderId);
        return getMessagesReceivedByUserInYearAndMonth(receiverId, year, month).stream()
                .filter(messageWasSentBy)
                .collect(Collectors.toList());
    }

    public List<Message> getMessagesReceivedByUserSentByOtherUserInMonth(Long receiverId, Long senderId, int month){
        Predicate<Message> messageWasSentBy = m -> m.getSenderId().equals(senderId);
        return getMessagesReceivedByUserSentInMonth(receiverId, month).stream()
                .filter(messageWasSentBy)
                .collect(Collectors.toList());
    }


    private List<Message> getConversationMessagesSentInMonth(Long conversationId, int month){
        Predicate<Message> messageIsInConversationAndWasSentInMonth =
                m -> m.getConversationId().equals(conversationId) &&
                        m.getDate().getMonth().getValue() == month &&
                        m.getDate().getYear() == LocalDateTime.now().getYear();
        return messageRepository.getAll().stream()
                .filter(messageIsInConversationAndWasSentInMonth)
                .collect(Collectors.toList());
    }

    private List<Message> getConversationMessagesSentInYearAndMonth(Long conversationId, int year ,int month){
        Predicate<Message> messageIsInConversationAndWasSentInYearAndMonth =
                m -> m.getConversationId().equals(conversationId) &&
                        m.getDate().getMonth().getValue() == month &&
                        m.getDate().getYear() == year;
        return messageRepository.getAll().stream()
                .filter(messageIsInConversationAndWasSentInYearAndMonth)
                .collect(Collectors.toList());
    }

    private ConversationDto getConversationDtoWithId(Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId).get();
        String name = conversation.getName();
        String description = conversation.getDescription();

        List<User> participants = getConversationParticipants(conversationId);

        List<MessageDto> messages = getConversationMessages(conversationId);

        return new ConversationDto(conversationId,
                name,
                description,
                participants,
                messages);
    }

    private List<MessageDto> getConversationMessages(Long conversationId) {
        List<MessageDto> messages = messageRepository.getAll().stream()
                .filter(message -> message.getConversationId().equals(conversationId))
                .map(message -> {
                    User sender = userRepository.findById(message.getSenderId()).get();
                    return new MessageDto(sender, message.getText(), message.getDate());})
                .collect(Collectors.toList());
        return messages;
    }

    private List<User> getConversationParticipants(Long conversationId) {
        List<User> participants = conversationParticipationRepository.getAll().stream()
                .filter(convo -> convo.getConversationId().equals(conversationId))
                .map(convo -> userRepository.findById(convo.getParticipantId()).get())
                .collect(Collectors.toList());
        return participants;
    }

    private Long findAvailableIdForConversation() {
        var optional = conversationRepository.getAll().stream()
                        .max(Comparator.comparing(Conversation::getId));
        if(optional.isEmpty())
            return 1L;
        return optional.get().getId() + 1;
    }

    private Long findAvailableIdForMessage() {
        var optional = messageRepository.getAll().stream()
                .max(Comparator.comparing(Message::getId));
        if(optional.isEmpty())
            return 1L;
        return optional.get().getId() + 1;
    }
}
