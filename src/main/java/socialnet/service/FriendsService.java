package socialnet.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import socialnet.api.friends.*;
import socialnet.dto.ComplexRs;
import socialnet.dto.PersonRs;
import socialnet.model.Friendships;
import socialnet.model.Person;
import socialnet.repository.friends.FriendsShipsRepository;
import socialnet.repository.friends.PersonRepositoryFriends;
import socialnet.security.jwt.JwtUtils;

import java.util.*;

@Service
public class FriendsService {

    private JwtUtils jwtUtils;
    private PersonRepositoryFriends personRepository;

    private FriendsShipsRepository friendsShipsRepository;

    public FriendsService(JwtUtils jwtUtils,
                          PersonRepositoryFriends personRepository,
                          FriendsShipsRepository friendsShipsRepository) {
        this.jwtUtils = jwtUtils;
        this.personRepository = personRepository;
        this.friendsShipsRepository = friendsShipsRepository;
    }

    public ResponseEntity<?> getFriendsUsing(String authorization, Integer offset, Integer perPage) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail.isEmpty()){
            return new ResponseEntity<>(errorRs("EmptyEmailException", "Field 'email' is empty"),
                    HttpStatus.BAD_REQUEST);
        } else {
            List<Friendships> allFriendships = friendsShipsRepository.findAllFriendships(personsEmail.get(0).getId());
            if (allFriendships.isEmpty()){
                return new ResponseEntity<>(new CommonRsListPersonRs(), HttpStatus.OK);
            } else {
                List<Long> friendsId = new ArrayList<>();
                allFriendships.forEach((Friendships friendship) -> {
                    if (!friendsId.contains(friendship.getSrcPersonId()) &&
                            !friendship.getSrcPersonId().equals(personsEmail.get(0).getId())){
                        friendsId.add(friendship.getSrcPersonId());
                    }
                    if (!friendsId.contains(friendship.getDstPersonId())&&
                            !friendship.getDstPersonId().equals(personsEmail.get(0).getId())){
                        friendsId.add(friendship.getDstPersonId());
                    }
                });
                String friendsIdString = friendsIdStringMethod(friendsId);
                List<Person> personList = personRepository.findPersonFriendsAll(friendsIdString);
                return new ResponseEntity<>(fillingCommonRsListPersonRs(personList, offset, perPage), HttpStatus.OK);
            }
        }
    }

    private String friendsIdStringMethod(List<Long> friendsId) {
        String friendsIdString = "";
        for (int i = 0; i < friendsId.size(); i++){
            if (i < friendsId.size() - 1) {
                friendsIdString = friendsIdString + " id=" + friendsId.get(i).toString() + " OR ";
            } else {
                friendsIdString = friendsIdString + " id=" + friendsId.get(i).toString();
            }
        }
        return friendsIdString;
    }

    public CommonRsListPersonRs fillingCommonRsListPersonRs(List<Person> personList, Integer offset, Integer perPage){
        CommonRsListPersonRs commonRsListPersonRsFilling = new CommonRsListPersonRs();
        List<Person> personListOffset = new ArrayList<>();
        for (int i = offset; i < offset + perPage; i++){
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
    public List<PersonRs> createPersonRsList(List<Person> personList){
        List<PersonRs> personRsList= new ArrayList<>();
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
            if (person.getOnlineStatus().equals(true)){
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

    public ResponseEntity<?> userBlocksUserUsingPOST(String authorization, Integer id) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail.isEmpty()){
            return new ResponseEntity<>(errorRs("EmptyEmailException", "Field 'email' is empty"),
                    HttpStatus.BAD_REQUEST);
        } else {
            List<Friendships> friend = friendsShipsRepository.findFriend(personsEmail.get(0).getId(), id);
            if (friend.isEmpty()){
                return new ResponseEntity<>(errorRs("EmptyEmailException", "Field 'friend' is empty"),
                    HttpStatus.BAD_REQUEST);
            } else {
                String status = friend.get(0).getStatusName().toString();
                Long idFriend = friend.get(0).getId();
                String statusNew = "";
                if (friend.get(0).getStatusName().toString().equals("BLOCKED")){
                    statusNew = "FRIEND";
                } else {
                    statusNew = "BLOCKED";
                }
                friendsShipsRepository.insertStatusFriend(idFriend, statusNew);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
    }

    public ErrorRs errorRs(String error, String description){
        ErrorRs errorRs = new ErrorRs();
        errorRs.setError(error);
        errorRs.setError_description(description);
        Date date = new Date();
        errorRs.setTimestamp(date.getTime());
        return errorRs;
    }

    public ResponseEntity<?> getOutgoingRequestsUsingGET(String authorization, Integer offset, Integer perPage) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail.isEmpty()){
            return new ResponseEntity<>(errorRs("EmptyEmailException", "Field 'email' is empty"),
                    HttpStatus.BAD_REQUEST);
        } else {
            List<Friendships> outgoingRequests = friendsShipsRepository
                    .findAllOutgoingRequests(personsEmail.get(0).getId());
            if (outgoingRequests.isEmpty()){
                return new ResponseEntity<>(errorRs("EmptyEmailException",
                        "Field 'OutgoingRequests' is empty"), HttpStatus.BAD_REQUEST);
            } else {
                List<Long> requestsId = new ArrayList<>();
                outgoingRequests.forEach((Friendships friendship) -> {
                    requestsId.add(friendship.getId());
                });
                String friendsIdString = friendsIdStringMethod(requestsId);
                List<Person> personList = personRepository.findPersonFriendsAll(friendsIdString);
                return new ResponseEntity<>(fillingCommonRsListPersonRs(personList, offset, perPage), HttpStatus.OK);
            }
        }
    }

    public ResponseEntity<?> getRecommendedFriendsUsingGET(String authorization) {
        //String email = jwtUtils.getUserEmail(authorization);
        String email = "ipeggs0@amazon.co.uk";
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail.isEmpty()) {
            return new ResponseEntity<>(errorRs("EmptyEmailException", "Field 'email' is empty"),
                    HttpStatus.BAD_REQUEST);
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
            for (Long idFriend : friendsId){
                personList.addAll(friendsShipsRepository.findAllFriendships(idFriend));
            }
            HashSet<Long> friendsFriendsId = new HashSet<>();
            for (Friendships friendships : personList){
                friendsFriendsId.add(friendships.getSrcPersonId());
                friendsFriendsId.add(friendships.getDstPersonId());
            }
            List<Long> friendsFriendsId2 = new ArrayList<>(friendsFriendsId);
            String searchIdFriendFriends = friendsIdStringMethod(friendsFriendsId2);
            List<Person> friendFriendsNew = personRepository.findPersonFriendsAll(searchIdFriendFriends);

            if (friendFriendsNew.size() < 10){
                friendFriendsNew.addAll(addRecommendedFriendsCityAndAll(friendFriendsNew, personsEmail));
            }
            CommonRsListPersonRs commonRsListPersonRsFilling = new CommonRsListPersonRs();
            List<PersonRs> personRsList = createPersonRsList(friendFriendsNew);
            commonRsListPersonRsFilling.setData(personRsList);
            Date date = new Date();
            commonRsListPersonRsFilling.setTimestamp(date.getTime());
            commonRsListPersonRsFilling.setTotal((long) personRsList.size());
            commonRsListPersonRsFilling.setPerPage(personRsList.size());
            commonRsListPersonRsFilling.setItemPerPage(personRsList.size());
            return new ResponseEntity<>(commonRsListPersonRsFilling, HttpStatus.OK);
        }
    }

    public List<Person> addRecommendedFriendsCityAndAll(List<Person> friendFriendsNew, List<Person> personsEmail){
        final String city = personsEmail.get(0).getCity();
        friendFriendsNew.addAll(personRepository.findPersonsCity(city));
//                List<Person> personListAll = new ArrayList<>();
//                if (personList.size() < 10) {
//                    personListAll = personRepository.findPersonAll();
//                }
//                for (int i = 0; i < 10; i++) {
//                    personList.add(personListAll.get(i));
//                    if (personList.size() >= 10) {
//                        break;
//                    }
//                }

        return friendFriendsNew;
    }

    public ResponseEntity<?> getPotentialFriendsUsingGET(String authorization, Integer offset, Integer perPage) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        if (personsEmail.isEmpty()){
            return new ResponseEntity<>(errorRs("EmptyEmailException", "Field 'email' is empty"),
                    HttpStatus.BAD_REQUEST);
        } else {
            List<Friendships> outgoingRequests = friendsShipsRepository
                    .findAllPotentialFriends(personsEmail.get(0).getId());
            if (outgoingRequests.isEmpty()){
                return new ResponseEntity<>(errorRs("EmptyEmailException",
                        "Field 'PotentialFriends' is empty"), HttpStatus.BAD_REQUEST);
            } else {
                List<Long> requestsId = new ArrayList<>();
                outgoingRequests.forEach((Friendships friendship) -> {
                    requestsId.add(friendship.getId());
                });
                String friendsIdString = friendsIdStringMethod(requestsId);
                List<Person> personList = personRepository.findPersonFriendsAll(friendsIdString);
                return new ResponseEntity<>(fillingCommonRsListPersonRs(personList, offset, perPage), HttpStatus.OK);
            }
        }
    }

    public ResponseEntity<?> addFriendUsingPOST(String authorization, Integer id) {
        //String email = jwtUtils.getUserEmail(authorization);
        String email = "ipeggs0@amazon.co.uk";
        //final String password = new BCryptPasswordEncoder().encode("password");
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        Long friendships = 0L;
        if (personsEmail.isEmpty()){
            return new ResponseEntity<>(errorRs("EmptyEmailException", "Field 'email' is empty"),
                    HttpStatus.BAD_REQUEST);
        } else {
            List<Friendships> addFriend = friendsShipsRepository
                    .findAllPotentialFriends(personsEmail.get(0).getId());
            if (addFriend.isEmpty()){
                return new ResponseEntity<>(errorRs("EmptyEmailException",
                        "Field 'addFriend' is empty"), HttpStatus.BAD_REQUEST);
            } else {
                friendships = addFriend.get(0).getId();
                Long idN = personsEmail.get(0).getId();
                friendsShipsRepository.addFriend(idN.longValue(), friendships, "FRIEND");
                return new ResponseEntity<>(fillingCommonRsComplexRs(id, friendships), HttpStatus.OK);
            }
        }
    }

    private Object fillingCommonRsComplexRs(Integer id, Long friendships) {
        CommonRsComplexRs commonRsComplexRs = new CommonRsComplexRs();
        ComplexRs complexRs = new ComplexRs();
        complexRs.setId(id);
        complexRs.setCount(null);
        complexRs.setMessage(null);
        complexRs.setMessage_id(null);
        Date date = new Date();
        commonRsComplexRs.setTimestamp(date.getTime());
        commonRsComplexRs.setTotal(null);
        commonRsComplexRs.setPerPage(null);
        commonRsComplexRs.setItemPerPage(null);
        return commonRsComplexRs;
    }

    public ResponseEntity<?> deleteSentFriendshipRequestUsingDELETE(String authorization, Integer id) {
        //String email = jwtUtils.getUserEmail(authorization);
        String email = "ipeggs0@amazon.co.uk";
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        Long friendships = 0L;
        if (personsEmail.isEmpty()){
            return new ResponseEntity<>(errorRs("EmptyEmailException", "Field 'email' is empty"),
                    HttpStatus.BAD_REQUEST);
        } else {
            List<Friendships> addFriend = friendsShipsRepository
                    .findAllPotentialFriends(personsEmail.get(0).getId());
            if (addFriend.isEmpty()){
                return new ResponseEntity<>(errorRs("EmptyEmailException",
                        "Field 'addFriend' is empty"), HttpStatus.BAD_REQUEST);
            } else {
                friendships = addFriend.get(0).getId();
                Date date = new Date();
                friendsShipsRepository.deleteSentFriendshipRequest(date,"DECLINED", Integer.toUnsignedLong(id));
                return new ResponseEntity<>(fillingCommonRsComplexRs(id, friendships), HttpStatus.OK);
            }
        }
    }


    public ResponseEntity<?> sendFriendshipRequestUsingPOST(String authorization, Integer id) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        Long friendships = 0L;
        if (personsEmail.isEmpty()){
            return new ResponseEntity<>(errorRs("EmptyEmailException", "Field 'email' is empty"),
                    HttpStatus.BAD_REQUEST);
        } else {
            List<Friendships> sendFriends = friendsShipsRepository
                    .sendFriendshipRequest(personsEmail.get(0).getId());
            if (sendFriends.isEmpty()){
                return new ResponseEntity<>(errorRs("EmptyEmailException",
                        "Field 'addFriend' is empty"), HttpStatus.BAD_REQUEST);
            } else {
                friendships = sendFriends.get(0).getId();
                Date date = new Date();
                friendsShipsRepository.sendFriendshipRequestUsingPOST(date,friendships, id, "REQUEST");
                return new ResponseEntity<>(fillingCommonRsComplexRs(id, friendships), HttpStatus.OK);
            }
        }
    }


    public ResponseEntity<?> deleteFriendUsingDELETE(String authorization, Integer id) {
        String email = jwtUtils.getUserEmail(authorization);
        List<Person> personsEmail = personRepository.findPersonsEmail(email);
        Long friendships = 0L;
        if (personsEmail.isEmpty()){
            return new ResponseEntity<>(errorRs("EmptyEmailException", "Field 'email' is empty"),
                    HttpStatus.BAD_REQUEST);
        } else {
            List<Friendships> sendFriends = friendsShipsRepository
                    .sendFriendshipRequest(personsEmail.get(0).getId());
            if (sendFriends.isEmpty()){
                return new ResponseEntity<>(errorRs("EmptyEmailException",
                        "Field 'addFriend' is empty"), HttpStatus.BAD_REQUEST);
            } else {
                friendships = sendFriends.get(0).getId();
                Date date = new Date();
                friendsShipsRepository.deleteFriendUsing(id);
                return new ResponseEntity<>(fillingCommonRsComplexRs(id, friendships), HttpStatus.OK);
            }
        }
    }
}
