package socialnet.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import socialnet.api.friends.*;
import socialnet.dto.CommonRsComplexRs;
import socialnet.dto.ComplexRs;
import socialnet.dto.PersonRs;
import socialnet.exception.EmptyEmailException;
import socialnet.model.Friendships;
import socialnet.model.Person;
import socialnet.repository.friends.FriendsShipsRepository;
import socialnet.repository.friends.PersonRepositoryFriends;
import socialnet.security.jwt.JwtUtils;

import java.sql.Timestamp;
import java.util.*;

@Service
@AllArgsConstructor
public class FriendsService {

    private JwtUtils jwtUtils;
    private PersonRepositoryFriends personRepository;

    private FriendsShipsRepository friendsShipsRepository;

    public CommonRsListPersonRs getFriendsUsing(String authorization, Integer offset, Integer perPage) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> allFriendships = friendsShipsRepository
                    .findAllFriendships(personsEmail.get(0).getId());
            List<Long> friendsId = new ArrayList<>();
            allFriendships.forEach((Friendships friendship) -> {
                if (!friendsId.contains(friendship.getSrcPersonId()) &&
                        !friendship.getSrcPersonId().equals(personsEmail.get(0).getId())) {
                    friendsId.add(friendship.getSrcPersonId());
                }
                if (!friendsId.contains(friendship.getDstPersonId()) &&
                        !friendship.getDstPersonId().equals(personsEmail.get(0).getId())) {
                    friendsId.add(friendship.getDstPersonId());
                }
            });
            String friendsIdString = friendsIdStringMethod(friendsId);
            List<Person> personList = personRepository.findPersonFriendsAll(friendsIdString);
            return fillingCommonRsListPersonRs(personList, offset, perPage);
        }
    }

    private String friendsIdStringMethod(List<Long> friendsId) {
        String friendsIdString = "";
        for (int i = 0; i < friendsId.size(); i++) {
            if (i < friendsId.size() - 1) {
                friendsIdString = friendsIdString + " id=" + friendsId.get(i).toString() + " OR ";
            } else {
                friendsIdString = friendsIdString + " id=" + friendsId.get(i).toString();
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
            PersonRs rs = new PersonRs();
            rs.setAbout(person.getAbout());
            rs.setBirthDate(person.getBirthDate().toString());
            rs.setCity(person.getCity());
            rs.setCountry(person.getCountry());
            rs.setCurrency(null);
            rs.setEmail(person.getEmail());
            rs.setFirstName(person.getFirstName());
            rs.setFriendStatus("FRIEND");
            rs.setId(person.getId());
            rs.setIsBlocked(person.getIsBlocked());
            rs.setIsBlockedByCurrentUser(false);
            rs.setLastOnlineTime(person.getLastOnlineTime().toString());
            rs.setMessagesPermission(null);
            if (person.getOnlineStatus().equals(true)) {
                rs.setOnline(true);
            } else {
                rs.setOnline(false);
            }
            rs.setPhone(person.getPhone());
            rs.setPhoto(person.getPhoto());
            rs.setRegDate(person.getRegDate().toString());
            rs.setToken(null);
            rs.setUserDeleted(person.getIsDeleted());
            rs.setWeather(null);
            personRsList.add(rs);
        }
        return personRsList;
    }

    public HttpStatus userBlocksUserUsingPOST(String authorization, Integer id) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> friend = friendsShipsRepository.findFriend(personsEmail.get(0).getId(), id);
            String status = friend.get(0).getStatusName().toString();
            Long idFriend = friend.get(0).getId();
            String statusNew = "";
            if (friend.get(0).getStatusName().toString().equals("BLOCKED")) {
                statusNew = "FRIEND";
            } else {
                statusNew = "BLOCKED";
            }
            friendsShipsRepository.insertStatusFriend(idFriend, statusNew);
            return HttpStatus.OK;
        }
    }

    public CommonRsListPersonRs getOutgoingRequestsUsingGET(String authorization, Integer offset, Integer perPage) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> outgoingRequests = friendsShipsRepository
                    .findAllOutgoingRequests(personsEmail.get(0).getId());
            List<Long> requestsId = new ArrayList<>();
            outgoingRequests.forEach((Friendships friendship) -> {
                requestsId.add(friendship.getId());
            });
            String friendsIdString = friendsIdStringMethod(requestsId);
            List<Person> personList = personRepository.findPersonFriendsAll(friendsIdString);
            return fillingCommonRsListPersonRs(personList, offset, perPage);
        }
    }

    public CommonRsListPersonRs getRecommendedFriendsUsingGET(String authorization) {
        //String email = jwtUtils.getUserEmail(authorization);
        String email = "nwickey2@ibm.com";
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> allFriendships = friendsShipsRepository.findAllFriendships(personsEmail.get(0).getId());
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
                personList.addAll(friendsShipsRepository.findAllFriendships(friendsId.get(i)));
            }
            HashSet<Long> friendsFriendsId = new HashSet<>(cleaningTheListOfRecommendationsFromDuplication(personList,
                    friendsId, personsEmail));
            List<Long> friendsFriendsId2 = new ArrayList<>(friendsFriendsId);
            String searchIdFriendFriends = friendsIdStringMethod(friendsFriendsId2);
            List<Person> friendFriendsNew = personRepository.findPersonFriendsAll(searchIdFriendFriends);
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
            for (int a = 0; a < friendsId.size(); a++){
                if (personList.get(i).getSrcPersonId() == friendsId.get(a)) {
                    flagSrc = true;
                }
                if (personList.get(i).getDstPersonId().equals(friendsId.get(a))) {
                    flagDst = true;
                }
                if (personList.get(i).getSrcPersonId().equals(personsEmail.get(0).getId())){
                    flagSrc = true;
                }
                if (personList.get(i).getDstPersonId().equals(personsEmail.get(0).getId())){
                    flagDst = true;
                }
            }
            if (!flagSrc){
                friendsFriendsId.add(personList.get(i).getSrcPersonId());
            }
            if (!flagDst){
                friendsFriendsId.add(personList.get(i).getDstPersonId());
            }
        }
        return friendsFriendsId;
    }

    public List<Person> addRecommendedFriendsCityAndAll(List<Person> friendFriendsNew,
                                                        List<Person> personsEmail, List<Friendships> personList) {
        final String city = personsEmail.get(0).getCity();
        List<Person> friendFriendsNewAddCity = new ArrayList<>();
        friendFriendsNewAddCity.addAll(personRepository.findPersonsCity(city));
        boolean flag;
        for (int i = 0; i < friendFriendsNewAddCity.size(); i++) {
            flag = false;
            for (int a = 0; a < friendFriendsNew.size(); a++){
                final long id = friendFriendsNewAddCity.get(i).getId();
                if (friendFriendsNewAddCity.get(i).getId() == friendFriendsNew.get(a)) {
                    flag = true;
                }
                if (personList.get(i).getDstPersonId().equals(friendsId.get(a))) {
                    flagDst = true;
                }
                if (personList.get(i).getSrcPersonId().equals(personsEmail.get(0).getId())){
                    flagSrc = true;
                }
                if (personList.get(i).getDstPersonId().equals(personsEmail.get(0).getId())){
                    flagDst = true;
                }
            }
            if (!flagSrc){
                friendsFriendsId.add(personList.get(i).getSrcPersonId());
            }
            if (!flagDst){
                friendsFriendsId.add(personList.get(i).getDstPersonId());
            }
        }
        friendFriendsNew.addAll(friendFriendsNewAddCity);
        List<Long> friendsId = new ArrayList<>();
        friendFriendsNew.forEach((idRecommended) -> {
            friendsId.add(idRecommended.getId());
        });
        List<Person> personListAll = new ArrayList<>();
        if (friendFriendsNew.size() < 10) {
            int limit = 10 - friendFriendsNew.size();
            friendFriendsNew.addAll(personRepository.findPersonAll(limit));
        }
        return friendFriendsNew;
    }

    public CommonRsListPersonRs getPotentialFriendsUsingGET(String authorization, Integer offset, Integer perPage) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> outgoingRequests = friendsShipsRepository
                    .findAllPotentialFriends(personsEmail.get(0).getId());
            List<Long> requestsId = new ArrayList<>();
            outgoingRequests.forEach((Friendships friendship) -> {
                requestsId.add(friendship.getId());
            });
            String friendsIdString = friendsIdStringMethod(requestsId);
            List<Person> personList = personRepository.findPersonFriendsAll(friendsIdString);
            return fillingCommonRsListPersonRs(personList, offset, perPage);
        }
    }

    public CommonRsComplexRs addFriendUsingPOST(String authorization, Integer id) {
        //String email = jwtUtils.getUserEmail(authorization);
        String email = "ipeggs0@amazon.co.uk";
        //final String password = new BCryptPasswordEncoder().encode("password");
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        Long friendships = 0L;
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> addFriend = friendsShipsRepository
                    .findAllPotentialFriends(personsEmail.get(0).getId());
            friendships = addFriend.get(0).getId();
            Long idN = personsEmail.get(0).getId();
            friendsShipsRepository.addFriend(idN.longValue(), friendships, "FRIEND");
            return fillingCommonRsComplexRs(id, friendships);
        }
    }

    private CommonRsComplexRs fillingCommonRsComplexRs(Integer id, Long friendships) {
        CommonRsComplexRs commonRsComplexRs = new CommonRsComplexRs();
        ComplexRs complexRs = new ComplexRs();
        complexRs.setId(id);
        complexRs.setCount(null);
        complexRs.setMessage(null);
        complexRs.setMessage_id(null);
        Date date = new Date();
        commonRsComplexRs.setTimestamp((int) date.getTime());
        commonRsComplexRs.setTotal(null);
        commonRsComplexRs.setPerPage(null);
        commonRsComplexRs.setItemPerPage(null);
        return commonRsComplexRs;
    }

    public CommonRsComplexRs deleteSentFriendshipRequestUsingDELETE(String authorization, Integer id) {
        //String email = jwtUtils.getUserEmail(authorization);
        String email = "ipeggs0@amazon.co.uk";
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        Long friendships = 0L;
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> addFriend = friendsShipsRepository
                    .findAllPotentialFriends(personsEmail.get(0).getId());
            friendships = addFriend.get(0).getId();
            friendsShipsRepository.deleteSentFriendshipRequest(new Timestamp(System.currentTimeMillis()),
                    "DECLINED", Integer.toUnsignedLong(id));
            return fillingCommonRsComplexRs(id, friendships);
        }
    }


    public CommonRsComplexRs sendFriendshipRequestUsingPOST(String authorization, Integer id) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        Long friendships = 0L;
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> sendFriends = friendsShipsRepository
                    .sendFriendshipRequest(personsEmail.get(0).getId());
            friendships = sendFriends.get(0).getId();
            Date date = new Date();
            friendsShipsRepository.sendFriendshipRequestUsingPOST(date, friendships, id, "REQUEST");
            return fillingCommonRsComplexRs(id, friendships);
        }
    }


    public CommonRsComplexRs deleteFriendUsingDELETE(String authorization, Integer id) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        Long friendships = 0L;
        if (personsEmail.isEmpty()) {
            throw new EmptyEmailException("Field 'email' is empty");
        } else {
            List<Friendships> sendFriends = friendsShipsRepository
                    .sendFriendshipRequest(personsEmail.get(0).getId());
            friendships = sendFriends.get(0).getId();
            Date date = new Date();
            friendsShipsRepository.deleteFriendUsing(id);
            return fillingCommonRsComplexRs(id, friendships);
        }
    }
}
