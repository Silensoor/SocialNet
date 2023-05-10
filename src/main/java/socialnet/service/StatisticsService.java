package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import socialnet.api.response.ErrorRs;
import socialnet.api.response.RegionStatisticsRs;
import socialnet.exception.EmptyEmailException;
import socialnet.model.*;
import socialnet.repository.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final CityRepository cityRepository;

    private final CommentRepository commentRepository;

    private final CountryRepository countryRepository;

    private final DialogsRepository dialogsRepository;

    private final LikeRepository likeRepository;

    private final MessageRepository messageRepository;

    private final PostRepository postRepository;

    private final TagRepository tagRepository;

    private final PersonRepository personRepository;

    public Integer getAllCities() {
        return cityRepository.getAllCity();
    }

    public RegionStatisticsRs[] getCitiesUsers() {
        List<RegionStatisticsRs> rs = cityRepository.getCitiesUsers();
        if (rs != null) {
            return rs.toArray(RegionStatisticsRs[]::new);
        }
        return null;
    }

    public ResponseEntity<?> getCommentsByPost(Integer postId) {
        Post post = postRepository.findById(postId);
        if (post == null) {
            return new ResponseEntity<>(new ErrorRs("EntityNotFoundException",
                    "Field 'postId' " + postId + " is empty"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(commentRepository.findByPostIdCount(Long.valueOf(postId)), HttpStatus.OK);
    }

    public Integer getCountry() {
        return countryRepository.findAll().size();
    }

    public RegionStatisticsRs[] getCountryUsers() {
        return countryRepository.findCountryUsers().toArray(RegionStatisticsRs[]::new);
    }

    public Integer getDialog() {
        return dialogsRepository.findDialogCount();
    }

    public ResponseEntity<?> getDialogsUser(Integer userId) {
        Person person = personRepository.findById(Long.valueOf(userId));
        if (person == null) {
            return new ResponseEntity<>(new ErrorRs("EntityNotFoundException",
                    "Field 'userId' " + userId + " is empty"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(dialogsRepository.findDialogsUserCount(userId), HttpStatus.OK);
    }

    public Integer getAllLike() {
        return likeRepository.findAllLike();
    }

    public Integer getLikeEntity(Integer entityId) {
        List<Like> like = likeRepository.getLikesByEntityId(entityId);
        if (like == null || like.isEmpty()) {
            throw new EmptyEmailException("Field 'entityId' " + entityId + " is empty");
        } else {
            return likeRepository.getLikesByEntityId(entityId).size();
        }
    }

    public Integer getAllMessage() {
        return messageRepository.getAllMessage();
    }

    public ResponseEntity<?> getMessage(Integer firstUserId, Integer secondUserId) {
        final List<Message> messageList = messageRepository.getMessage(firstUserId, secondUserId);
        if (messageList == null || messageList.isEmpty()) {
            return new ResponseEntity<>(new ErrorRs("EntityNotFoundException",
                    "Field 'firstUserId' " + firstUserId
                            + " or 'secondUserId' " + secondUserId + " is empty"), HttpStatus.BAD_REQUEST);
        } else {
            Map<String, Integer> messages = new HashMap<>();
            Person firstUser = personRepository.findById(Long.valueOf(firstUserId));
            Person secondUser = personRepository.findById(Long.valueOf(secondUserId));
            int counter = 1;
            for (Message message : messageList) {
                if (message.getAuthorId().equals(firstUser.getId()) && message.getRecipientId()
                        .equals(secondUser.getId())) {
                    messages.put(counter + firstUser.getFirstName() + "_" + firstUser.getLastName() + "->"
                                    + secondUser.getFirstName() + "_" + secondUser.getLastName(),
                            Math.toIntExact(message.getId()));
                } else {
                    messages.put(counter + secondUser.getFirstName() + "_" + secondUser.getLastName() + "->"
                                    + firstUser.getFirstName() + "_" + firstUser.getLastName(),
                            Math.toIntExact(message.getId()));
                }
                counter += 1;
            }
            return new ResponseEntity<>(new TreeMap<>(messages), HttpStatus.OK);
        }
    }

    public ResponseEntity<?> getMessageByDialog(Integer dialogId) {
        Dialog dialog = dialogsRepository.findByDialogId(Long.valueOf(dialogId));
        if (dialog == null) {
            return new ResponseEntity<>(new ErrorRs("EntityNotFoundException",
                    "Field 'dialogId' " + dialogId + " is empty"), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(messageRepository.getMessageByDialog(dialogId), HttpStatus.OK);
        }
    }

    public Integer getAllPost() {
        return postRepository.findAll().size();
    }

    public ResponseEntity<?> getAllPostByUser(Integer userId) {
        Person person = personRepository.findById(Long.valueOf(userId));
        if (person == null) {
            return new ResponseEntity<>(new ErrorRs("EntityNotFoundException",
                    "Field 'userId' " + userId + " is empty"), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(postRepository.getAllPostByUser(userId), HttpStatus.OK);
        }
    }

    public Integer getAllTags() {
        return tagRepository.getAllTags();
    }

    public ResponseEntity<?> getTagsByPost(Integer postId) {
        Post post = postRepository.findById(postId);
        if (post == null) {
            return new ResponseEntity<>(new ErrorRs("EntityNotFoundException",
                    "Field 'postId' " + postId + " is empty"), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(tagRepository.findByPostId(postId.longValue()).size(), HttpStatus.OK);
        }
    }

    public Integer getAllUsers() {
        return personRepository.findAll().size();
    }

    public ResponseEntity<?> getAllUsersByCity(String city) {
        City city1 = cityRepository.getCity(city);
        if (city1 == null) {
            return new ResponseEntity<>(new ErrorRs("EntityNotFoundException",
                    "Field 'city' " + city + " is empty"), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(personRepository.findByCity(city).size(), HttpStatus.OK);
        }
    }

    public ResponseEntity<?> getAllUsersByCountry(String country) {
        Country country1 = countryRepository.getCountry(country);
        if (country1 == null) {
            return new ResponseEntity<>(new ErrorRs("EntityNotFoundException",
                    "Field 'country' " + country + " is empty"), HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(personRepository.getAllUsersByCountry(country), HttpStatus.OK);
        }
    }
}
