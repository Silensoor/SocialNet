package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import socialnet.api.response.*;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class FriendsService {

    private final JwtUtils jwtUtils;
    private final PersonRepository personRepository;
    private final FriendsShipsRepository friendsShipsRepository;
    private final PersonSettingRepository personSettingRepository;

    public CommonRs<List<PersonRs>> getFriends(String authorization, Integer offset, Integer perPage) {
        String email = jwtUtils.getUserEmail(authorization);
        Person personsEmail = personRepository.findByEmail(email);
        if (personsEmail == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> allFriendships = friendsShipsRepository
                    .findAllFriendships(personsEmail.getId());
            if (allFriendships == null) {
                allFriendships = new ArrayList<>();
            }
            List<Long> friendsId = new ArrayList<>();
            allFriendships.forEach((Friendships friendship) -> {
                if (!friendsId.contains(friendship.getSrcPersonId()) &&
                        !friendship.getSrcPersonId().equals(personsEmail.getId())) {
                    friendsId.add(friendship.getSrcPersonId());
                }
                if (!friendsId.contains(friendship.getDstPersonId()) &&
                        !friendship.getDstPersonId().equals(personsEmail.getId())) {
                    friendsId.add(friendship.getDstPersonId());
                }
            });
            List<Person> personList = new ArrayList<>();
            if (!friendsId.isEmpty()) {
                personList = personRepository.findFriendsAll(friendsId);
            }
            if (personList == null) {
                personList = new ArrayList<>();
            }
            return fillingCommonRsListPersonRs(personList, offset, perPage);
        }
    }

    public CommonRs<List<PersonRs>> fillingCommonRsListPersonRs(List<Person> personList, Integer offset, Integer perPage) {
        CommonRs<List<PersonRs>> commonRsListPersonRsFilling = new CommonRs<>();
        List<Person> personListOffset = new ArrayList<>();
        for (int i = offset; i < offset + perPage; i++) {
            if (i < personList.size()) {
                personListOffset.add(personList.get(i));
            } else {
                break;
            }
        }
        List<PersonRs> personRsList = createPersonRsList(personListOffset);
        commonRsListPersonRsFilling.setData(personRsList);
        Date date = new Date();
        commonRsListPersonRsFilling.setTimestamp(date.getTime());
        commonRsListPersonRsFilling.setTotal((long) personList.size());
        commonRsListPersonRsFilling.setPerPage(personListOffset.size());
        commonRsListPersonRsFilling.setItemPerPage(personListOffset.size());
        return commonRsListPersonRsFilling;
    }

    public List<PersonRs> createPersonRsList(List<Person> personList) {
        List<PersonRs> personRsList = new ArrayList<>();
        for (Person person : personList) {
            PersonRs personRs = PersonMapper.INSTANCE.toDTO(person);
            personRs.setOnline(null);
            personRs.setWeather(new WeatherRs());
            personRs.setIsBlockedByCurrentUser(null);
            personRs.setFriendStatus(FriendshipStatusTypes.FRIEND.name());
            personRs.setCurrency(new CurrencyRs());
            personRsList.add(personRs);
        }
        return personRsList;
    }

    public HttpStatus userBlocks(String authorization, Integer id) {
        String email = jwtUtils.getUserEmail(authorization);
        Person personsEmail = personRepository.findByEmail(email);
        if (personsEmail == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> friend = friendsShipsRepository.findFriend(personsEmail.getId(), Long.valueOf(id));
            if (friend == null) {
                friend = new ArrayList<>();
            }
            if (!friend.isEmpty()) {
                Long idFriend = friend.get(0).getId();
                String statusNew = "";
                if (friend.get(0).getStatusName().toString().equals("BLOCKED")) {
                    statusNew = "FRIEND";
                } else {
                    statusNew = "BLOCKED";
                }
                friendsShipsRepository.insertStatusFriend(idFriend, statusNew);
            }
            return HttpStatus.OK;
        }
    }

    public CommonRs<List<PersonRs>> getOutgoingRequests(String authorization, Integer offset, Integer perPage) {
        String email = jwtUtils.getUserEmail(authorization);
        Person personsEmail = personRepository.findByEmail(email);
        if (personsEmail == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> outgoingRequests = friendsShipsRepository
                    .findAllOutgoingRequests(personsEmail.getId());
            if (outgoingRequests == null) {
                outgoingRequests = new ArrayList<>();
            }
            List<Long> requestsId = new ArrayList<>();
            outgoingRequests.forEach((Friendships friendship) -> {
                requestsId.add(friendship.getDstPersonId());
            });
            List<Person> personList = new ArrayList<>();
            if (!requestsId.isEmpty()) {
                personList = personRepository.findFriendsAll(requestsId);
            }
            if (personList == null) {
                personList = new ArrayList<>();
            }
            return fillingCommonRsListPersonRs(personList, offset, perPage);
        }
    }

    public CommonRs<List<PersonRs>> getRecommendedFriends(String authorization) {
        String email = jwtUtils.getUserEmail(authorization);
        Person personsEmail = personRepository.findByEmail(email);
        if (personsEmail == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> allFriendships = friendsShipsRepository.findAllFriendships(personsEmail.getId());
            if (allFriendships == null) {
                allFriendships = new ArrayList<>();
            }
            List<Long> friendsId = new ArrayList<>();
            Long idNewSrcPersonId = 0L;
            Long idNewDstPersonId = 0L;
            for (Friendships allFriendship : allFriendships) {
                idNewSrcPersonId = allFriendship.getSrcPersonId();
                idNewDstPersonId = allFriendship.getDstPersonId();
                if (!friendsId.contains(idNewSrcPersonId) && !idNewSrcPersonId.equals(personsEmail.getId())) {
                    friendsId.add(idNewSrcPersonId);
                }
                if (!friendsId.contains(idNewDstPersonId) && !idNewDstPersonId.equals(personsEmail.getId())) {
                    friendsId.add(idNewDstPersonId);
                }
            }
            List<Friendships> personList = new ArrayList<>();
            for (Long aLong : friendsId) {
                List<Friendships> allFriendships1 = friendsShipsRepository
                        .findAllFriendships(aLong);
                if (allFriendships1 == null) {
                    allFriendships1 = new ArrayList<>();
                }
                personList.addAll(allFriendships1);
            }
            HashSet<Long> friendsFriendsId = new HashSet<>(cleaningTheListOfRecommendationsFromDuplication(personList,
                    friendsId, personsEmail));
            List<Long> friendsFriendsId2 = new ArrayList<>(friendsFriendsId);
            List<Person> friendFriendsNew = new ArrayList<>();
            if (!friendsFriendsId2.isEmpty()) {
                friendFriendsNew = personRepository.findFriendsAll(friendsFriendsId2);
            }
            if (friendFriendsNew == null) {
                friendFriendsNew = new ArrayList<>();
            }
            List<Person> friendFriendsNewTotal = new ArrayList<>(friendFriendsNew);
            if (friendFriendsNew.size() < 10) {
                friendFriendsNewTotal.addAll(addRecommendedFriendsCityAndAll(friendFriendsNew, personsEmail
                ));
            }
            CommonRs<List<PersonRs>> commonRsListPersonRsFilling = new CommonRs<>();
            List<PersonRs> personRsList = createPersonRsList(friendFriendsNew);
            commonRsListPersonRsFilling.setData(personRsList);
            Date date = new Date();
            commonRsListPersonRsFilling.setTimestamp(date.getTime());
            commonRsListPersonRsFilling.setTotal((long) personRsList.size());
            commonRsListPersonRsFilling.setPerPage(personRsList.size());
            commonRsListPersonRsFilling.setItemPerPage(personRsList.size());
            return commonRsListPersonRsFilling;
        }
    }

    private HashSet<Long> cleaningTheListOfRecommendationsFromDuplication(List<Friendships> personList,
                                                                          List<Long> friendsId, Person personsEmail) {
        HashSet<Long> friendsFriendsId = new HashSet<>();
        AtomicBoolean flagDst = new AtomicBoolean(false);
        AtomicBoolean flagSrc = new AtomicBoolean(false);
        personList.forEach((friendships) -> {
            flagSrc.set(false);
            flagDst.set(false);
            friendsId.forEach((aLong) -> {
                if (Objects.equals(friendships.getSrcPersonId(), aLong)) {
                    flagSrc.set(true);
                }
                if (friendships.getDstPersonId().equals(aLong)) {
                    flagDst.set(true);
                }
                if (friendships.getSrcPersonId().equals(personsEmail.getId())) {
                    flagSrc.set(true);
                }
                if (friendships.getDstPersonId().equals(personsEmail.getId())) {
                    flagDst.set(true);
                }
            });
            if (!flagSrc.get()) {
                friendsFriendsId.add(friendships.getSrcPersonId());
            }
            if (!flagDst.get()) {
                friendsFriendsId.add(friendships.getDstPersonId());
            }
        });
        return friendsFriendsId;
    }

    public List<Person> addRecommendedFriendsCityAndAll(List<Person> friendFriendsNew, Person personsEmail) {
        final String city = personsEmail.getCity();
        List<Person> personsCity = personRepository.findByCity(city);
        if (personsCity == null) {
            personsCity = new ArrayList<>();
        }
        List<Person> friendFriendsNewAddCity = new ArrayList<>(personsCity);
        friendFriendsNew.addAll(friendFriendsNewAddCity);
        List<Long> friendsId = new ArrayList<>();
        friendFriendsNew.forEach((idRecommended) -> {
            friendsId.add(idRecommended.getId());
        });
        if (friendFriendsNew.size() < 10) {
            int limit = 10 - friendFriendsNew.size();
            List<Person> personAll = personRepository.findAll((long) limit);
            if (personAll == null) {
                personAll = new ArrayList<>();
            }
            friendFriendsNew.addAll(personAll);
        }
        return friendFriendsNew;
    }

    public CommonRs<List<PersonRs>> getPotentialFriends(String authorization, Integer offset, Integer perPage) {
        String email = jwtUtils.getUserEmail(authorization);
        Person personsEmail = personRepository.findByEmail(email);
        if (personsEmail == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> outgoingRequests = friendsShipsRepository
                    .findAllPotentialFriends(personsEmail.getId());
            if (outgoingRequests == null) {
                outgoingRequests = new ArrayList<>();
            }
            List<Long> requestsId = new ArrayList<>();
            outgoingRequests.forEach((Friendships friendship) -> {
                requestsId.add(friendship.getSrcPersonId());
            });
            List<Person> personList;
            if (!requestsId.isEmpty()) {
                personList = personRepository.findFriendsAll(requestsId);
                if (personList == null) {
                    personList = new ArrayList<>();
                }
            } else {
                personList = new ArrayList<>();
            }
            return fillingCommonRsListPersonRs(personList, offset, perPage);
        }
    }

    public CommonRs<ComplexRs> addFriend(String authorization, Integer id) {
        String email = jwtUtils.getUserEmail(authorization);
        Person personsEmail = personRepository.findByEmail(email);
        if (personsEmail == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> friend = friendsShipsRepository.findAllFriendships(personsEmail.getId());
            if (friend == null) {
                friend = new ArrayList<>();
            }
            Long friendships = addFriendsSelectUpdateOrInsert(friend, personsEmail, id);
            return fillingCommonRsComplexRs(id);
        }
    }

    private Long addFriendsSelectUpdateOrInsert(List<Friendships> friend, Person personsEmail, Integer id) {
        List<Friendships> friendOutgoingRequests = friendsShipsRepository
                .findAllOutgoingRequests(personsEmail.getId());
        if (friendOutgoingRequests == null) {
            friendOutgoingRequests = new ArrayList<>();
        }
        boolean flagOutgoingRequests = false;
        Long idReQuest = 0L;
        for (Friendships friendships2 : friendOutgoingRequests) {
            if (friendships2.getDstPersonId().equals(Long.valueOf(id))) {
                flagOutgoingRequests = true;
                idReQuest = friendships2.getId();
            }
        }
        if (flagOutgoingRequests) {
            friendsShipsRepository.updateFriend(personsEmail.getId(), Long.valueOf(id), "FRIEND", idReQuest);
        } else {
            List<Friendships> friendPotentialFriends = friendsShipsRepository
                    .findAllPotentialFriends(personsEmail.getId());
            if (friendPotentialFriends == null) {
                friendPotentialFriends = new ArrayList<>();
            }
            boolean flagPotentialFriends = false;
            Long idPotential = 0L;
            for (Friendships friendships3 : friendPotentialFriends) {
                if (friendships3.getSrcPersonId().equals(Long.valueOf(id))) {
                    flagPotentialFriends = true;
                    idPotential = friendships3.getId();
                }
            }
            if (flagPotentialFriends) {
                friendsShipsRepository.updateFriend(personsEmail.getId(), Long.valueOf(id),
                        "FRIEND", idPotential);
            } else {
                boolean flagFriends = false;
                Long idFriend = 0L;
                for (Friendships friendships1 : friend) {
                    if (friendships1.getSrcPersonId().equals(Long.valueOf(id))) {
                        flagFriends = true;
                        idFriend = friendships1.getId();
                    }
                    if (friendships1.getDstPersonId().equals(Long.valueOf(id))) {
                        flagFriends = true;
                        idFriend = friendships1.getId();
                    }
                }
                if (!flagFriends) {
                    friendsShipsRepository.addFriend(personsEmail.getId(), Long.valueOf(id), "FRIEND");
                } else {
                    friendsShipsRepository.updateFriend(personsEmail.getId(), Long.valueOf(id),
                            "FRIEND", idFriend);
                }
            }
        }
        return Long.valueOf(id);
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
        String email = jwtUtils.getUserEmail(authorization);
        Person personsEmail = personRepository.findByEmail(email);
        if (personsEmail == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> addFriend = friendsShipsRepository
                    .findAllPotentialFriends(personsEmail.getId());
            if (addFriend == null) {
                addFriend = new ArrayList<>();
            }
            Long idPotentialFriend = 0L;
            for (Friendships friendships1 : addFriend) {
                if (friendships1.getSrcPersonId().equals(Long.valueOf(id))) {
                    idPotentialFriend = friendships1.getId();
                }
            }
            if (!addFriend.isEmpty()) {
                friendsShipsRepository.deleteSentFriendshipRequest("DECLINED", idPotentialFriend);
            }
            return fillingCommonRsComplexRs(id);
        }
    }


    public CommonRs<ComplexRs> sendFriendsRequest(String authorization, Integer id) {
        String email = jwtUtils.getUserEmail(authorization);
        Person personsEmail = personRepository.findByEmail(email);
        if (personsEmail == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> sendFriends = friendsShipsRepository
                    .sendFriendshipRequest(personsEmail.getId());
            if (sendFriends == null) {
                sendFriends = new ArrayList<>();
            }
            boolean flagFriendshipRequest = false;
            Long idFriendshipRequest = 0L;
            for (Friendships friendships1 : sendFriends) {
                if (friendships1.getDstPersonId().equals(Long.valueOf(id))) {
                    flagFriendshipRequest = true;
                    idFriendshipRequest = friendships1.getDstPersonId();
                }
            }
            if (idFriendshipRequest == 0L) {
                idFriendshipRequest = personsEmail.getId();
            }
            if (sendFriends.isEmpty() || !flagFriendshipRequest) {
                friendsShipsRepository.sendFriendshipRequestUsingPOST(Long.valueOf(id), idFriendshipRequest,
                        "REQUEST");
            } else {
                friendsShipsRepository.updateFriend(Long.valueOf(id), idFriendshipRequest, "REQUEST",
                        idFriendshipRequest);
            }
            PersonSettings personSettings = personSettingRepository.getPersonSettings(id.longValue());
            if (personSettings.getFriendRequest()) {
                Notification notification = NotificationPusher.getNotification(NotificationType.FRIEND_REQUEST,
                        (long) id, personsEmail.getId());
                NotificationPusher.sendPush(notification, personsEmail.getId());
            }
            return fillingCommonRsComplexRs(id);
        }
    }


    public CommonRs<ComplexRs> deleteFriend(String authorization, Integer id) {
        String email = jwtUtils.getUserEmail(authorization);
        Person personsEmail = personRepository.findByEmail(email);
        if (personsEmail == null) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> sendFriends = friendsShipsRepository
                    .findFriend(Long.valueOf(id), personsEmail.getId());
            if (sendFriends == null) {
                sendFriends = new ArrayList<>();
            }
            boolean flagFriends = false;
            Long idFriends = 0L;
            for (Friendships friendships1 : sendFriends) {
                if (friendships1.getDstPersonId().equals(Long.valueOf(id))) {
                    flagFriends = true;
                    idFriends = friendships1.getId();
                }
            }
            if (!sendFriends.isEmpty() && flagFriends) {
                friendsShipsRepository.deleteFriendUsing(idFriends);
            }
            return fillingCommonRsComplexRs(id);
        }
    }
}
