package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ComplexRs;
import socialnet.api.response.NotificationType;
import socialnet.api.response.PersonRs;
import socialnet.exception.EmptyEmailException;
import socialnet.mappers.PersonMapper;
import socialnet.model.Friendships;
import socialnet.model.Notification;
import socialnet.model.Person;
import socialnet.model.PersonSettings;
import socialnet.model.enums.FriendshipStatusTypes;
import socialnet.repository.FriendsShipsRepository;
import socialnet.repository.PersonRepository;
import socialnet.repository.PersonSettingRepository;
import socialnet.security.jwt.JwtUtils;
import socialnet.utils.NotificationPusher;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendsService {

    private final JwtUtils jwtUtils;
    private final PersonRepository personRepository;
    private final FriendsShipsRepository friendsShipsRepository;
    private final PersonSettingRepository personSettingRepository;
    private final PersonMapper personMapper;

    private final WeatherService weatherService;
    private final CurrencyService currencyService;

    public CommonRs<List<PersonRs>> getFriends(String authorization, Integer offset, Integer perPage) {
        Person personsEmail = tokenToMail(authorization);
        List<Person> friendsList;
        long friendsListAll;
        friendsList = personRepository.findFriendsAll(personsEmail.getId(), offset, perPage);
        friendsListAll = Integer.toUnsignedLong(personRepository.findFriendsAllCount(personsEmail.getId()));
        return personToPersonRs(friendsList, offset, perPage, friendsListAll, personsEmail.getId());
    }

    public Person tokenToMail(String jwtToken) {
        String email = jwtUtils.getUserEmail(jwtToken);
        Person personsEmail = personRepository.findByEmail(email);
        if (personsEmail == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            return personsEmail;
        }
    }

    public synchronized CommonRs<List<PersonRs>> personToPersonRs(List<Person> personList, Integer offset, Integer perPage,
                                                                  long friendsListAll, long id) {
        List<PersonRs> personRsList = new ArrayList<>();
        personList.forEach(person -> {
            PersonRs friendRs = personMapper.toDTO(person);
            friendRs.setWeather(weatherService.getWeatherByCity(friendRs.getCity()));
            friendRs.setCurrency(currencyService.getCurrency(LocalDate.now()));
            if (friendsShipsRepository.getFriendStatus(id, friendRs.getId()) != null) {
                friendRs.setFriendStatus(friendsShipsRepository.getFriendStatus(id, friendRs.getId())
                        .getStatusName().toString());
                if (friendsShipsRepository.getFriendStatus(id, friendRs.getId()).toString().equals("BLOCKED")) {
                    friendRs.setIsBlockedByCurrentUser(true);
                }
            } else {
                friendRs.setFriendStatus("UNKNOWN");
                friendRs.setIsBlockedByCurrentUser(null);
            }

            friendRs.setWeather(weatherService.getWeatherByCity(friendRs.getCity()));
            friendRs.setCurrency(currencyService.getCurrency(LocalDate.now()));
            if (friendsShipsRepository.getFriendStatus(id, friendRs.getId()) != null) {
                friendRs.setFriendStatus(friendsShipsRepository.getFriendStatus(id, friendRs.getId())
                        .getStatusName().toString());
                if (friendsShipsRepository.getFriendStatus(id, friendRs.getId()).toString().equals("BLOCKED")) {
                    friendRs.setIsBlockedByCurrentUser(true);
                }
            } else {
                friendRs.setFriendStatus("UNKNOWN");
                friendRs.setIsBlockedByCurrentUser(null);
            }

            personRsList.add(friendRs);
        });
        return new CommonRs<>(personRsList, personRsList.size(), offset, perPage, System.currentTimeMillis(),
                friendsListAll);
    }

    public HttpStatus userBlocks(String authorization, Integer id) {
        Person personsEmail = tokenToMail(authorization);
        Friendships friend = friendsShipsRepository.findFriend(personsEmail.getId(), Long.valueOf(id));
        if (friend.getStatusName().equals(FriendshipStatusTypes.BLOCKED)) {
            friendsShipsRepository.insertStatusFriend(friend.getId(), FriendshipStatusTypes.FRIEND);
        } else {
            friendsShipsRepository.insertStatusFriend(friend.getId(), FriendshipStatusTypes.BLOCKED);
        }
        return HttpStatus.OK;
    }

    public CommonRs<List<PersonRs>> getOutgoingRequests(String authorization, Integer offset, Integer perPage) {
        Person personsEmail = tokenToMail(authorization);
        List<Person> outgoingRequests = personRepository
                .findAllOutgoingRequests(personsEmail.getId(), offset, perPage);
        long outgoingRequestsAll = personRepository.findAllOutgoingRequestsAll(personsEmail.getId());
        return personToPersonRs(outgoingRequests, offset, perPage, outgoingRequestsAll, personsEmail.getId());
    }

    public CommonRs<List<PersonRs>> getRecommendedFriends(String authorization) {
        Person personsEmail = tokenToMail(authorization);
        List<Person> recommendationFriends = new ArrayList<>();
        List<Person> friends = personRepository.findFriendsAll(personsEmail.getId(), 0, 20);
        if (friends != null) {
            recommendationFriends = personRepository.findRecommendedFriends(personsEmail.getId(),
                    friends, 0, 20);
        }
        if (recommendationFriends.size() < 10) {
            recommendationFriends.addAll(getRecommendedFriendsCity(personsEmail, recommendationFriends));
        }
        if (recommendationFriends.size() < 10) {
            assert friends != null;
            recommendationFriends.addAll(getRecommendedFriendsAll(personsEmail, recommendationFriends, friends));
        }
        return personToPersonRs(recommendationFriends, 0, 20, recommendationFriends.size(),
                personsEmail.getId());
    }

    public List<Person> getRecommendedFriendsCity(Person personsEmail, List<Person> recommendationFriends){
        List<Person> recommendationFriendsCity = new ArrayList<>();
        StringBuilder str1 = new StringBuilder();
        recommendationFriends.forEach(friend -> str1.append(friend.getId()).append(", "));
        List<Person> cityFriends;
        if (!str1.toString().equals("")) {
            cityFriends = personRepository.findByCityForFriends(personsEmail.getId(),
                    personsEmail.getCity(), str1.substring(0, str1.length() - 2), 0, 20);
        } else {
            cityFriends = personRepository.findByCityForFriends(personsEmail.getId(),
                    personsEmail.getCity(), "", 0, 20);
        }
        if (cityFriends != null && !cityFriends.isEmpty()) {
                int i = 0;
                while (i < 10 - recommendationFriends.size()) {
                    recommendationFriendsCity.add(cityFriends.get(i));
                    i++;
                }
        }
        return recommendationFriendsCity;
    }

    public List<Person> getRecommendedFriendsAll(Person personsEmail, List<Person> recommendationFriends,
                                                 List<Person> friends){
        StringBuilder str2 = new StringBuilder();
        recommendationFriends.forEach(friend -> str2.append(friend.getId()).append(", "));
        assert friends != null;
        friends.forEach(friend -> str2.append(friend.getId()).append(", "));
        List<Person> recommendationFriendsAll = new ArrayList<>(personRepository.
                findAllForFriends(personsEmail.getId(), str2.substring(0, str2.length() - 2),
                        10 - recommendationFriends.size()));
        return recommendationFriendsAll;
    }

    public CommonRs<List<PersonRs>> getPotentialFriends(String authorization, Integer offset, Integer perPage) {
        Person personsEmail = tokenToMail(authorization);
        List<Person> potentialFriends = personRepository
                .findAllPotentialFriends(personsEmail.getId(), offset, perPage);
        return personToPersonRs(potentialFriends, offset, perPage, potentialFriends.size(), personsEmail.getId());
    }

    public CommonRs<ComplexRs> addFriend(String authorization, Integer id) {
        Person personsEmail = tokenToMail(authorization);
        final Friendships friendships = friendsShipsRepository.findFriend(personsEmail.getId(), Long.valueOf(id));
        if (friendships != null) {
            if (!friendships.getStatusName().equals(FriendshipStatusTypes.FRIEND)) {
                friendsShipsRepository.updateFriend(friendships.getDstPersonId(), friendships.getSrcPersonId(),
                        FriendshipStatusTypes.FRIEND, friendships.getId());
            }
        } else {
            friendsShipsRepository.addFriend(personsEmail.getId(), Long.valueOf(id), FriendshipStatusTypes.FRIEND);
        }
        return fillingCommonRsComplexRs(id);
    }

    private CommonRs<ComplexRs> fillingCommonRsComplexRs(Integer id) {
        CommonRs<ComplexRs> commonRsComplexRs = new CommonRs<>();
        ComplexRs complexRs = new ComplexRs();
        complexRs.setId(id);
        Date date = new Date();
        commonRsComplexRs.setTimestamp(date.getTime());
        return commonRsComplexRs;
    }

    public CommonRs<ComplexRs> deleteFriendsRequest(String authorization, Integer id) {
        Person personsEmail = tokenToMail(authorization);
        final Friendships friendships = friendsShipsRepository.getFriendStatus(personsEmail.getId(), Long.valueOf(id));
        if (friendships != null) {
            if (friendships.getStatusName().equals(FriendshipStatusTypes.REQUEST)) {
                friendsShipsRepository.updateFriend(friendships.getDstPersonId(), friendships.getSrcPersonId(),
                        FriendshipStatusTypes.DECLINED, friendships.getId());
            }
        }
        return fillingCommonRsComplexRs(id);
    }

    public CommonRs<ComplexRs> sendFriendsRequest(String authorization, Integer id) {
        Person personsEmail = tokenToMail(authorization);
        final Friendships friendships = friendsShipsRepository.findRequest(personsEmail.getId(), Long.valueOf(id));
        if (friendships != null) {
            if (!friendships.getStatusName().equals(FriendshipStatusTypes.REQUEST)) {
                friendsShipsRepository.updateFriend(friendships.getDstPersonId(), friendships.getSrcPersonId(),
                        FriendshipStatusTypes.REQUEST, friendships.getId());
            }
        } else {
            friendsShipsRepository.addFriend(personsEmail.getId(), Long.valueOf(id), FriendshipStatusTypes.REQUEST);
        }
        PersonSettings personSettings = personSettingRepository.getPersonSettings(id.longValue());
        if (personSettings.getFriendRequest()) {
            Notification notification = NotificationPusher.getNotification(NotificationType.FRIEND_REQUEST,
                    (long) id, personsEmail.getId());
            NotificationPusher.sendPush(notification, personsEmail.getId());
        }
        return fillingCommonRsComplexRs(id);
    }

    public CommonRs<ComplexRs> deleteFriend(String authorization, Integer id) {
        Person personsEmail = tokenToMail(authorization);
        final Friendships friendships = friendsShipsRepository.findFriend(personsEmail.getId(), Long.valueOf(id));
        if (friendships != null) {
            friendsShipsRepository.deleteFriendUsing(friendships.getId());
        }
        return fillingCommonRsComplexRs(id);
    }
}
