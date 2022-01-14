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

    /**
     * Constructor of the service
     */
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

    /**
     * Creates a new conversation with the given name, description and participants and returns it
     * @param name name of the conversation
     * @param description description of the conversation
     * @param participantsIdList list of the id-s of the participants of the conversation
     * @return ConversationDto that contains the name, description and the list of participants (User entities)
     */
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

    /**
     * Sends a message in the conversation with the given id
     * @param conversationId id of the conversation in which the message is sent
     * @param senderId id of the User that sends the message
     * @param text body of the message
     * @param date the date when the message was sent
     */
    public void sendMessageInConversation(Long conversationId, Long senderId, String text, LocalDateTime date){
        Long id = findAvailableIdForMessage();
        Message message = new Message(id, conversationId, senderId, text, date);
        participationValidator.validate(new ConversationParticipation(senderId, conversationId));
        messageValidator.validate(message);
        messageRepository.save(message);
    }

    /**
     * Returns a list with all conversations of the user with the given id
     */
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


    /**
     * Returns a list with all messages received by the user in the given year and month
     */
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

    /**
     * Returns all messages between the users sent in the given year and month
     */
    public List<Message> getMessagesReceivedByUserSentByOtherUserInYearAndMonth(Long receiverId,
                                                                                Long senderId,
                                                                                int year,
                                                                                int month) {
        Predicate<Message> messageWasSentBy = m -> m.getSenderId().equals(senderId);
        return getMessagesReceivedByUserInYearAndMonth(receiverId, year, month).stream()
                .filter(messageWasSentBy)
                .collect(Collectors.toList());
    }
    /**
     * Returns all messages of the given conversation sent in the given year and month
     */
    private List<Message> getConversationMessagesSentInYearAndMonth(Long conversationId, int year ,int month){
        Predicate<Message> messageIsInConversationAndWasSentInYearAndMonth =
                m -> m.getConversationId().equals(conversationId) &&
                        m.getDate().getMonth().getValue() == month &&
                        m.getDate().getYear() == year;
        return messageRepository.getAll().stream()
                .filter(messageIsInConversationAndWasSentInYearAndMonth)
                .collect(Collectors.toList());
    }

    /**
     * Returns a ConversationDto object with the given id
     */
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

    /**
     * Returns all messages of the given conversation
     */
    private List<MessageDto> getConversationMessages(Long conversationId) {
        List<MessageDto> messages = messageRepository.getAll().stream()
                .filter(message -> message.getConversationId().equals(conversationId))
                .map(message -> {
                    User sender = userRepository.findById(message.getSenderId()).get();
                    return new MessageDto(sender, message.getText(), message.getDate());})
                .collect(Collectors.toList());
        return messages;
    }

    /**
     * Returns a list of the Users that participate in the given conversation
     */
    private List<User> getConversationParticipants(Long conversationId) {
        List<User> participants = conversationParticipationRepository.getAll().stream()
                .filter(convo -> convo.getConversationId().equals(conversationId))
                .map(convo -> userRepository.findById(convo.getParticipantId()).get())
                .collect(Collectors.toList());
        return participants;
    }

    /**
     * Returns a long representing an available id for creating a new conversation
     */
    private Long findAvailableIdForConversation() {
        var optional = conversationRepository.getAll().stream()
                        .max(Comparator.comparing(Conversation::getId));
        if(optional.isEmpty())
            return 1L;
        return optional.get().getId() + 1;
    }

    /**
     * Returns a long representing an available id for creating a new message
     */
    private Long findAvailableIdForMessage() {
        var optional = messageRepository.getAll().stream()
                .max(Comparator.comparing(Message::getId));
        if(optional.isEmpty())
            return 1L;
        return optional.get().getId() + 1;
    }
}
