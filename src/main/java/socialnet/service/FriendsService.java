package socialnet.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import socialnet.api.response.*;
import socialnet.exception.EmptyEmailException;
import socialnet.mappers.PersonMapper;
import socialnet.model.Friendships;
import socialnet.model.Person;
import socialnet.model.enums.FriendshipStatusTypes;
import socialnet.repository.FriendsShipsRepository;
import socialnet.repository.PersonRepository;
import socialnet.security.jwt.JwtUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
@AllArgsConstructor
public class FriendsService {

    private JwtUtils jwtUtils;
    private PersonRepository personRepository;

    private FriendsShipsRepository friendsShipsRepository;

    public CommonRsListPersonRs getFriendsUsing(String authorization, Integer offset, Integer perPage) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail == null) {
            personsEmail = new ArrayList<>();
        }
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> allFriendships = friendsShipsRepository
                    .findAllFriendships(personsEmail.get(0).getId());
            if (allFriendships == null) {
                allFriendships = new ArrayList<>();
            }
            List<Long> friendsId = new ArrayList<>();
            List<Person> finalPersonsEmail = personsEmail;
            allFriendships.forEach((Friendships friendship) -> {
                if (!friendsId.contains(friendship.getSrcPersonId()) &&
                    !friendship.getSrcPersonId().equals(finalPersonsEmail.get(0).getId())) {
                    friendsId.add(friendship.getSrcPersonId());
                }
                if (!friendsId.contains(friendship.getDstPersonId()) &&
                    !friendship.getDstPersonId().equals(finalPersonsEmail.get(0).getId())) {
                    friendsId.add(friendship.getDstPersonId());
                }
            });
            List<Person> personList = new ArrayList<>();
            if (!friendsId.isEmpty()) {
                String sql = "SELECT * FROM persons WHERE";
                String friendsIdString = friendsIdStringMethod(friendsId, sql);
                personList = personRepository.findFriendsAll(friendsIdString);
            }
            if (personList == null) {
                personList = new ArrayList<>();
            }
            return fillingCommonRsListPersonRs(personList, offset, perPage);
        }
    }

    private String friendsIdStringMethod(List<Long> friendsId, String sql) {
        String friendsIdString = sql;
        for (int i = 0; i < friendsId.size(); i++) {
            if (i < friendsId.size() - 1) {
                friendsIdString = friendsIdString + " id =" + friendsId.get(i) + " OR";
            } else {
                friendsIdString = friendsIdString + " id =" + friendsId.get(i);
            }
        }
        return friendsIdString;
    }

    public CommonRsListPersonRs fillingCommonRsListPersonRs(List<Person> personList, Integer offset, Integer perPage) {
        CommonRsListPersonRs commonRsListPersonRsFilling = new CommonRsListPersonRs();
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

    public HttpStatus userBlocksUserUsingPOST(String authorization, Integer id) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail == null) {
            personsEmail = new ArrayList<>();
        }
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> friend = friendsShipsRepository.findFriend(personsEmail.get(0).getId(), Long.valueOf(id));
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

    public CommonRsListPersonRs getOutgoingRequestsUsingGET(String authorization, Integer offset, Integer perPage) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail == null) {
            personsEmail = new ArrayList<>();
        }
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> outgoingRequests = friendsShipsRepository
                    .findAllOutgoingRequests(personsEmail.get(0).getId());
            if (outgoingRequests == null) {
                outgoingRequests = new ArrayList<>();
            }
            List<Long> requestsId = new ArrayList<>();
            outgoingRequests.forEach((Friendships friendship) -> {
                requestsId.add(friendship.getDstPersonId());
            });
            List<Person> personList = new ArrayList<>();
            if (!requestsId.isEmpty()) {
                String sql = "SELECT * FROM persons WHERE";
                String friendsIdString = friendsIdStringMethod(requestsId, sql);
                personList = personRepository.findFriendsAll(friendsIdString);
            }
            if (personList == null) {
                personList = new ArrayList<>();
            }
            return fillingCommonRsListPersonRs(personList, offset, perPage);
        }
    }

    public CommonRsListPersonRs getRecommendedFriendsUsingGET(String authorization) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail == null) {
            personsEmail = new ArrayList<>();
        }
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> allFriendships = friendsShipsRepository.findAllFriendships(personsEmail.get(0).getId());
            if (allFriendships == null) {
                allFriendships = new ArrayList<>();
            }
            List<Long> friendsId = new ArrayList<>();
            Long idNewSrcPersonId = 0L;
            Long idNewDstPersonId = 0L;
            for (int i = 0; i < allFriendships.size(); i++) {
                idNewSrcPersonId = allFriendships.get(i).getSrcPersonId();
                idNewDstPersonId = allFriendships.get(i).getDstPersonId();
                if (!friendsId.contains(idNewSrcPersonId) && !idNewSrcPersonId.equals(personsEmail.get(0).getId())) {
                    friendsId.add(idNewSrcPersonId);
                }
                if (!friendsId.contains(idNewDstPersonId) && !idNewDstPersonId.equals(personsEmail.get(0).getId())) {
                    friendsId.add(idNewDstPersonId);
                }
            }
            List<Friendships> personList = new ArrayList<>();
            for (int i = 0; i < friendsId.size(); i++) {
                List<Friendships> allFriendships1 = friendsShipsRepository
                        .findAllFriendships(friendsId.get(i));
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
                String sql = "SELECT * FROM persons WHERE";
                String searchIdFriendFriends = friendsIdStringMethod(friendsFriendsId2, sql);
                friendFriendsNew = personRepository.findFriendsAll(searchIdFriendFriends);
            }
            if (friendFriendsNew == null) {
                friendFriendsNew = new ArrayList<>();
            }
            List<Person> friendFriendsNewTotal = new ArrayList<>(friendFriendsNew);
            if (friendFriendsNew.size() < 10) {
                friendFriendsNewTotal.addAll(addRecommendedFriendsCityAndAll(friendFriendsNew, personsEmail,
                        personList));
            }
            CommonRsListPersonRs commonRsListPersonRsFilling = new CommonRsListPersonRs();
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
                                                                          List<Long> friendsId, List<Person> personsEmail) {
        HashSet<Long> friendsFriendsId = new HashSet<>();
        boolean flagDst;
        boolean flagSrc;
        for (int i = 0; i < personList.size(); i++) {
            flagSrc = false;
            flagDst = false;
            for (int a = 0; a < friendsId.size(); a++) {
                if (personList.get(i).getSrcPersonId() == friendsId.get(a)) {
                    flagSrc = true;
                }
                if (personList.get(i).getDstPersonId().equals(friendsId.get(a))) {
                    flagDst = true;
                }
                if (personList.get(i).getSrcPersonId().equals(personsEmail.get(0).getId())) {
                    flagSrc = true;
                }
                if (personList.get(i).getDstPersonId().equals(personsEmail.get(0).getId())) {
                    flagDst = true;
                }
            }
            if (!flagSrc) {
                friendsFriendsId.add(personList.get(i).getSrcPersonId());
            }
            if (!flagDst) {
                friendsFriendsId.add(personList.get(i).getDstPersonId());
            }
        }
        return friendsFriendsId;
    }

    public List<Person> addRecommendedFriendsCityAndAll(List<Person> friendFriendsNew,
                                                        List<Person> personsEmail, List<Friendships> personList) {
        final String city = personsEmail.get(0).getCity();
        List<Person> friendFriendsNewAddCity = new ArrayList<>();
        List<Person> personsCity = personRepository.findByCity(city);
        if (personsCity == null) {
            personsCity = new ArrayList<>();
        }
        friendFriendsNewAddCity.addAll(personsCity);
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

    public CommonRsListPersonRs getPotentialFriendsUsingGET(String authorization, Integer offset, Integer perPage) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail == null) {
            personsEmail = new ArrayList<>();
        }
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> outgoingRequests = friendsShipsRepository
                    .findAllPotentialFriends(personsEmail.get(0).getId());
            if (outgoingRequests == null) {
                outgoingRequests = new ArrayList<>();
            }
            List<Long> requestsId = new ArrayList<>();
            outgoingRequests.forEach((Friendships friendship) -> {
                requestsId.add(friendship.getSrcPersonId());
            });
            List<Person> personList;
            if (!requestsId.isEmpty()) {
                String sql = "SELECT * FROM persons WHERE";
                String friendsIdString = friendsIdStringMethod(requestsId, sql);
                personList = personRepository.findFriendsAll(friendsIdString);
                if (personList == null) {
                    personList = new ArrayList<>();
                }
            } else {
                personList = new ArrayList<>();
            }
            return fillingCommonRsListPersonRs(personList, offset, perPage);
        }
    }

    public CommonRs<ComplexRs> addFriendUsingPOST(String authorization, Integer id) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail == null) {
            personsEmail = new ArrayList<>();
        }
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> friend = friendsShipsRepository.findAllFriendships(personsEmail.get(0).getId());
            if (friend == null) {
                friend = new ArrayList<>();
            }
            Long friendships = addFriendsSelectUpdateOrInsert(friend, personsEmail, id);
            return fillingCommonRsComplexRs(id, friendships);
        }
    }

    private Long addFriendsSelectUpdateOrInsert(List<Friendships> friend, List<Person> personsEmail, Integer id) {
        List<Friendships> friendOutgoingRequests = friendsShipsRepository
                .findAllOutgoingRequests(personsEmail.get(0).getId());
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
            friendsShipsRepository.updateFriend(personsEmail.get(0).getId(), Long.valueOf(id), "FRIEND", idReQuest);
        } else {
            List<Friendships> friendPotentialFriends = friendsShipsRepository
                    .findAllPotentialFriends(personsEmail.get(0).getId());
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
                friendsShipsRepository.updateFriend(personsEmail.get(0).getId(), Long.valueOf(id),
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
                    friendsShipsRepository.addFriend(personsEmail.get(0).getId(), Long.valueOf(id), "FRIEND");
                } else {
                    friendsShipsRepository.updateFriend(personsEmail.get(0).getId(), Long.valueOf(id),
                            "FRIEND", idFriend);
                }
            }
        }
        return Long.valueOf(id);
    }

    private CommonRs<ComplexRs> fillingCommonRsComplexRs(Integer id, Long friendships) {
        CommonRs<ComplexRs> commonRs = new CommonRs<>();
        ComplexRs complexRs = new ComplexRs();
        complexRs.setId(id);
        complexRs.setCount(null);
        complexRs.setMessage(null);
        complexRs.setMessageId(null);
        Date date = new Date();
        commonRs.setTimestamp(date.getTime());
        commonRs.setTotal(null);
        commonRs.setPerPage(null);
        commonRs.setItemPerPage(null);
        return commonRs;
    }

    public CommonRs<ComplexRs> deleteSentFriendshipRequestUsingDELETE(String authorization, Integer id) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail == null) {
            personsEmail = new ArrayList<>();
        }
        Long friendships;
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> addFriend = friendsShipsRepository
                    .findAllPotentialFriends(personsEmail.get(0).getId());
            if (addFriend == null) {
                addFriend = new ArrayList<>();
            }
            Long idPotentialFriend = 0L;
            for (Friendships friendships1 : addFriend) {
                if (friendships1.getSrcPersonId().equals(Long.valueOf(id))) {
                    idPotentialFriend = friendships1.getId();
                }
            }
            friendships = addFriend.get(0).getId();
            if (!addFriend.isEmpty()) {
                friendsShipsRepository.deleteSentFriendshipRequest("DECLINED", idPotentialFriend);
            }
            return fillingCommonRsComplexRs(id, friendships);
        }
    }


    public CommonRs<ComplexRs> sendFriendshipRequestUsingPOST(String authorization, Integer id) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail == null) {
            personsEmail = new ArrayList<>();
        }
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> sendFriends = friendsShipsRepository
                    .sendFriendshipRequest(personsEmail.get(0).getId());
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
                idFriendshipRequest = personsEmail.get(0).getId();
            }
            if (sendFriends.isEmpty() || !flagFriendshipRequest) {
                friendsShipsRepository.sendFriendshipRequestUsingPOST(Long.valueOf(id), idFriendshipRequest,
                        "REQUEST");
            } else {
                friendsShipsRepository.updateFriend(Long.valueOf(id), idFriendshipRequest, "REQUEST",
                        idFriendshipRequest);
            }
            return fillingCommonRsComplexRs(id, idFriendshipRequest);
        }
    }


    public CommonRs<ComplexRs> deleteFriendUsingDELETE(String authorization, Integer id) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail == null) {
            personsEmail = new ArrayList<>();
        }
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> sendFriends = friendsShipsRepository
                    .findFriend(Long.valueOf(id), personsEmail.get(0).getId());
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
                friendsShipsRepository.deleteFriendUsing(Long.valueOf(idFriends));
            }
            return fillingCommonRsComplexRs(id, idFriends);
        }
    }
}
