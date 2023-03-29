package socialnet.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import socialnet.api.friends.*;
import socialnet.dto.ComplexRs;
import socialnet.dto.PersonRs;
import socialnet.model.Friendships;
import socialnet.model.Person;
import socialnet.model.enums.FriendshipStatusTypes;
import socialnet.repository.friends.FriendsShipsRepository;
import socialnet.repository.friends.PersonRepositoryFriends;
import socialnet.security.jwt.JwtUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static socialnet.model.enums.FriendshipStatusTypes.BLOCKED;
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
        String email = jwtUtils.getUserNameFromJwtToken(authorization);
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
                    if (!friendsId.contains(friendship.getSrcPersonId())){friendsId.add(friendship.getSrcPersonId());}
                    if (!friendsId.contains(friendship.getDstPersonId())){friendsId.add(friendship.getDstPersonId());}
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
        //String email = jwtUtils.getUserNameFromJwtToken(authorization);
        String email = "twharlton27@smh.com.au";
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
                FriendshipStatusTypes status = friend.get(0).getStatusName();
                Long idFriend = friend.get(0).getId();
                String statusNew = "";
                if (friend.get(0).getStatusName().equals(BLOCKED)){
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
        String email = jwtUtils.getUserNameFromJwtToken(authorization);
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
        String email = jwtUtils.getUserNameFromJwtToken(authorization);
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
                    if (!friendsId.contains(friendship.getSrcPersonId())){friendsId.add(friendship.getSrcPersonId());}
                    if (!friendsId.contains(friendship.getDstPersonId())){friendsId.add(friendship.getDstPersonId());}
                });
                String friendsIdString = friendsIdStringMethod(friendsId);
                List<Person> personList = personRepository.findPersonFriendsAll(friendsIdString);
                if (personList.size() < 10){
                    final String city = personsEmail.get(0).getCity();
                    personList.addAll(personRepository.findPersonsCity(city));
                    List<Person> personListAll = new ArrayList<>();
                    if (personList.size() < 10){
                        personListAll = personRepository.findPersonAll();
                    }
                    for (int i = 0; i < 10; i++){
                        personList.add(personListAll.get(i));
                        if (personList.size() >= 10){ break;}
                    }
                }
                CommonRsListPersonRs commonRsListPersonRsFilling = new CommonRsListPersonRs();
                List<PersonRs> personRsList = createPersonRsList(personList);
                commonRsListPersonRsFilling.setData(personRsList);
                Date date = new Date();
                commonRsListPersonRsFilling.setTimestamp(date.getTime());
                commonRsListPersonRsFilling.setTotal((long) personList.size());
                commonRsListPersonRsFilling.setPerPage(personList.size());
                commonRsListPersonRsFilling.setItemPerPage(personList.size());
                return new ResponseEntity<>(commonRsListPersonRsFilling, HttpStatus.OK);
            }
        }
    }


    public ResponseEntity<?> getPotentialFriendsUsingGET(String authorization, Integer offset, Integer perPage) {
        String email = jwtUtils.getUserNameFromJwtToken(authorization);
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
        String email = jwtUtils.getUserNameFromJwtToken(authorization);
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
                friendsShipsRepository.addFriend(date, Integer.toUnsignedLong(id), friendships, "FRIEND");
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
        String email = jwtUtils.getUserNameFromJwtToken(authorization);
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
        String email = jwtUtils.getUserNameFromJwtToken(authorization);
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
        String email = jwtUtils.getUserNameFromJwtToken(authorization);
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
